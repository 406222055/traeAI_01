package com.contractorcontrol.api.controller;

import com.contractorcontrol.api.entity.PersonnelCertificateEntity;
import com.contractorcontrol.api.entity.ProjectEntity;
import com.contractorcontrol.api.entity.VendorEntity;
import com.contractorcontrol.api.repository.PersonnelCertificateRepository;
import com.contractorcontrol.api.repository.ProjectRepository;
import com.contractorcontrol.api.repository.VendorRepository;
import com.contractorcontrol.api.util.ApiConstants;
import com.contractorcontrol.api.util.ApiSerializers;
import com.contractorcontrol.api.util.ValidationUtils;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/personnel-certificates")
public class PersonnelCertificateController {

  private final PersonnelCertificateRepository certificateRepository;
  private final VendorRepository vendorRepository;
  private final ProjectRepository projectRepository;

  public PersonnelCertificateController(
      PersonnelCertificateRepository certificateRepository,
      VendorRepository vendorRepository,
      ProjectRepository projectRepository) {
    this.certificateRepository = certificateRepository;
    this.vendorRepository = vendorRepository;
    this.projectRepository = projectRepository;
  }

  @GetMapping
  public List<Map<String, Object>> list(
      @RequestParam(required = false) String vendorId,
      @RequestParam(required = false) String projectId,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String certificateType,
      @RequestParam(required = false) String keyword) {
    Specification<PersonnelCertificateEntity> specification = Specification.where(null);
    if (vendorId != null && !vendorId.isEmpty()) {
      specification = specification.and((root, query, cb) -> cb.equal(root.get("vendor").get("id"), vendorId));
    }
    if (projectId != null && !projectId.isEmpty()) {
      specification = specification.and((root, query, cb) -> cb.equal(root.get("project").get("id"), projectId));
    }
    if (certificateType != null && !certificateType.isEmpty()) {
      specification = specification.and((root, query, cb) -> cb.equal(root.get("certificateType"), certificateType));
    }
    if (keyword != null && !keyword.trim().isEmpty()) {
      String like = "%" + keyword.trim() + "%";
      specification = specification.and((root, query, cb) -> cb.or(
          cb.like(root.get("personnelName"), like),
          cb.like(root.get("certificateNo"), like),
          cb.like(root.get("remark"), like)));
    }
    Stream<Map<String, Object>> stream = certificateRepository.findAll(specification, Sort.by(Sort.Direction.ASC, "expiryDate"))
        .stream()
        .map(ApiSerializers::serializePersonnelCertificate);
    if (status != null && !status.isEmpty() && ApiConstants.PERSONNEL_CERTIFICATE_STATUSES.contains(status)) {
      stream = stream.filter(item -> status.equals(item.get("status")));
    }
    return stream.collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public Map<String, Object> detail(@PathVariable String id) {
    PersonnelCertificateEntity cert = certificateRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("人员证照不存在"));
    return ApiSerializers.serializePersonnelCertificate(cert);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Map<String, Object> create(@RequestBody(required = false) Map<String, Object> body) {
    return ApiSerializers.serializePersonnelCertificate(saveCert(new PersonnelCertificateEntity(), body, true));
  }

  @PutMapping("/{id}")
  public Map<String, Object> update(@PathVariable String id, @RequestBody(required = false) Map<String, Object> body) {
    PersonnelCertificateEntity cert = certificateRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("人员证照不存在"));
    return ApiSerializers.serializePersonnelCertificate(saveCert(cert, body, false));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable String id) {
    if (!certificateRepository.existsById(id)) {
      throw new NoSuchElementException("人员证照不存在");
    }
    certificateRepository.deleteById(id);
  }

  private PersonnelCertificateEntity saveCert(PersonnelCertificateEntity cert, Map<String, Object> body, boolean create) {
    Map<String, Object> payload = body == null ? java.util.Collections.<String, Object>emptyMap() : body;
    VendorEntity vendor = vendorRepository.findById(ValidationUtils.assertString(payload.get("vendorId"), "vendorId"))
        .orElseThrow(() -> new IllegalArgumentException("vendorId is required"));

    Object projectIdValue = payload.get("projectId");
    ProjectEntity project = null;
    if (projectIdValue instanceof String && !((String) projectIdValue).trim().isEmpty()) {
      project = projectRepository.findById(((String) projectIdValue).trim())
          .orElseThrow(() -> new IllegalArgumentException("Invalid projectId"));
    }

    Instant issueDate = ValidationUtils.assertDate(payload.get("issueDate"), "issueDate");
    Instant expiryDate = ValidationUtils.assertDate(payload.get("expiryDate"), "expiryDate");
    if (!expiryDate.isAfter(issueDate)) {
      throw new IllegalArgumentException("到期日期必须晚于签发日期");
    }

    if (create) {
      cert.setId(UUID.randomUUID().toString().replace("-", ""));
      cert.setCreatedAt(Instant.now());
    }
    cert.setVendor(vendor);
    cert.setProject(project);
    cert.setPersonnelName(ValidationUtils.assertString(payload.get("personnelName"), "personnelName"));
    cert.setIdCardNo(ValidationUtils.assertOptionalString(payload.get("idCardNo")));
    cert.setCertificateType(ValidationUtils.assertEnum(payload.get("certificateType"), ApiConstants.PERSONNEL_CERTIFICATE_TYPES, "certificateType"));
    cert.setCertificateNo(ValidationUtils.assertString(payload.get("certificateNo"), "certificateNo"));
    cert.setIssueDate(issueDate);
    cert.setExpiryDate(expiryDate);
    cert.setStatus(ApiSerializers.computePersonnelCertificateStatus(expiryDate));
    cert.setRemark(ValidationUtils.assertOptionalString(payload.get("remark")));
    return certificateRepository.save(cert);
  }
}
