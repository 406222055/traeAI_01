package com.contractorcontrol.api.controller;

import com.contractorcontrol.api.entity.AdmissionEntity;
import com.contractorcontrol.api.entity.ProjectEntity;
import com.contractorcontrol.api.entity.VendorEntity;
import com.contractorcontrol.api.repository.AdmissionRepository;
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

  public AdmissionController(
      AdmissionRepository admissionRepository,
      VendorRepository vendorRepository,
      ProjectRepository projectRepository) {
    this.admissionRepository = admissionRepository;
    this.vendorRepository = vendorRepository;
    this.projectRepository = projectRepository;
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
    CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
    admission.setStatus(status);
    admission.setReviewComment(ValidationUtils.assertOptionalString(payload.get("reviewComment")));
    admission.setReviewedBy(currentUser.getUser().getName());
    admission.setReviewedAt(Instant.now());
    return ApiSerializers.serializeAdmission(admissionRepository.save(admission));
  }
}
