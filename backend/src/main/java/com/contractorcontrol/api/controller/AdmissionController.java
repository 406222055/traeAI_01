package com.contractorcontrol.api.controller;

import com.contractorcontrol.api.entity.AdmissionEntity;
import com.contractorcontrol.api.entity.PersonnelCertificateEntity;
import com.contractorcontrol.api.entity.ProjectEntity;
import com.contractorcontrol.api.entity.VendorEntity;
import com.contractorcontrol.api.repository.AdmissionRepository;
import com.contractorcontrol.api.repository.PersonnelCertificateRepository;
import com.contractorcontrol.api.repository.ProjectRepository;
import com.contractorcontrol.api.repository.VendorRepository;
import com.contractorcontrol.api.security.CurrentUser;
import com.contractorcontrol.api.util.ApiConstants;
import com.contractorcontrol.api.util.ApiSerializers;
import com.contractorcontrol.api.util.ValidationUtils;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admissions")
public class AdmissionController {

  private final AdmissionRepository admissionRepository;
  private final VendorRepository vendorRepository;
  private final ProjectRepository projectRepository;
  private final PersonnelCertificateRepository personnelCertificateRepository;

  public AdmissionController(
      AdmissionRepository admissionRepository,
      VendorRepository vendorRepository,
      ProjectRepository projectRepository,
      PersonnelCertificateRepository personnelCertificateRepository) {
    this.admissionRepository = admissionRepository;
    this.vendorRepository = vendorRepository;
    this.projectRepository = projectRepository;
    this.personnelCertificateRepository = personnelCertificateRepository;
  }

  @GetMapping
  public List<Map<String, Object>> list(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String vendorId,
      @RequestParam(required = false) String projectId) {
    Specification<AdmissionEntity> specification = Specification.where(null);
    if (status != null && !status.isEmpty()) {
      specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), status));
    }
    if (vendorId != null && !vendorId.isEmpty()) {
      specification = specification.and((root, query, cb) -> cb.equal(root.get("vendor").get("id"), vendorId));
    }
    if (projectId != null && !projectId.isEmpty()) {
      specification = specification.and((root, query, cb) -> cb.equal(root.get("project").get("id"), projectId));
    }
    return admissionRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt"))
        .stream()
        .map(ApiSerializers::serializeAdmission)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public Map<String, Object> detail(@PathVariable String id) {
    AdmissionEntity admission = admissionRepository.findById(id).orElseThrow(() -> new NoSuchElementException("准入申请不存在"));
    return ApiSerializers.serializeAdmission(admission);
  }

  @PostMapping
  @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
  public Map<String, Object> create(@RequestBody(required = false) Map<String, Object> body) {
    Map<String, Object> payload = body == null ? java.util.Collections.<String, Object>emptyMap() : body;
    VendorEntity vendor = vendorRepository.findById(ValidationUtils.assertString(payload.get("vendorId"), "vendorId"))
        .orElseThrow(() -> new IllegalArgumentException("vendorId is required"));
    ProjectEntity project = projectRepository.findById(ValidationUtils.assertString(payload.get("projectId"), "projectId"))
        .orElseThrow(() -> new IllegalArgumentException("projectId is required"));

    AdmissionEntity admission = new AdmissionEntity();
    admission.setId(UUID.randomUUID().toString().replace("-", ""));
    admission.setVendor(vendor);
    admission.setProject(project);
    admission.setApplyDate(ValidationUtils.assertDate(payload.get("applyDate"), "applyDate"));
    admission.setPlannedEntryDate(ValidationUtils.assertDate(payload.get("plannedEntryDate"), "plannedEntryDate"));
    admission.setScopeOfWork(ValidationUtils.assertString(payload.get("scopeOfWork"), "scopeOfWork"));
    admission.setStatus("pending");
    admission.setCreatedAt(Instant.now());
    return ApiSerializers.serializeAdmission(admissionRepository.save(admission));
  }

  @PatchMapping("/{id}/review")
  public Map<String, Object> review(@PathVariable String id, @RequestBody(required = false) Map<String, Object> body, Authentication authentication) {
    Map<String, Object> payload = body == null ? java.util.Collections.<String, Object>emptyMap() : body;
    String status = ValidationUtils.assertEnum(payload.get("status"), ApiConstants.ADMISSION_STATUSES, "status");
    if ("pending".equals(status)) {
      throw new IllegalArgumentException("审核结果不能为 pending");
    }
    AdmissionEntity admission = admissionRepository.findById(id).orElseThrow(() -> new NoSuchElementException("准入申请不存在"));

    if ("approved".equals(status)) {
      validateNoExpiredCertificates(admission.getVendor().getId(), admission.getProject().getId());
    }

    CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    admission.setStatus(status);
    admission.setReviewComment(ValidationUtils.assertOptionalString(payload.get("reviewComment")));
    admission.setReviewedBy(currentUser.getUser().getName());
    admission.setReviewedAt(Instant.now());
    return ApiSerializers.serializeAdmission(admissionRepository.save(admission));
  }

  private void validateNoExpiredCertificates(String vendorId, String projectId) {
    Specification<PersonnelCertificateEntity> spec = Specification.where(null);
    spec = spec.and((root, query, cb) -> cb.equal(root.get("vendor").get("id"), vendorId));
    spec = spec.and((root, query, cb) -> cb.or(
        cb.isNull(root.get("project")),
        cb.equal(root.get("project").get("id"), projectId)));

    List<PersonnelCertificateEntity> certs = personnelCertificateRepository.findAll(spec);
    List<String> expiredDetails = certs.stream()
        .filter(cert -> "expired".equals(ApiSerializers.computePersonnelCertificateStatus(cert.getExpiryDate())))
        .map(cert -> String.format("人员[%s] 证照编号[%s]", cert.getPersonnelName(), cert.getCertificateNo()))
        .collect(Collectors.toList());

    if (!expiredDetails.isEmpty()) {
      throw new IllegalArgumentException(
          "准入批准被拦截：该服务商存在 " + expiredDetails.size() + " 个过期人员证照。"
              + String.join("；", expiredDetails));
    }
  }
}
