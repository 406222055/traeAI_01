package com.contractorcontrol.api.controller;

import com.contractorcontrol.api.entity.ComplianceItemEntity;
import com.contractorcontrol.api.entity.ProjectEntity;
import com.contractorcontrol.api.entity.VendorEntity;
import com.contractorcontrol.api.repository.ComplianceItemRepository;
import com.contractorcontrol.api.repository.ProjectRepository;
import com.contractorcontrol.api.repository.VendorRepository;
import com.contractorcontrol.api.util.ApiConstants;
import com.contractorcontrol.api.util.ApiSerializers;
import com.contractorcontrol.api.util.ValidationUtils;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compliance-items")
public class ComplianceItemController {

  private final ComplianceItemRepository complianceItemRepository;
  private final VendorRepository vendorRepository;
  private final ProjectRepository projectRepository;

  public ComplianceItemController(
      ComplianceItemRepository complianceItemRepository,
      VendorRepository vendorRepository,
      ProjectRepository projectRepository) {
    this.complianceItemRepository = complianceItemRepository;
    this.vendorRepository = vendorRepository;
    this.projectRepository = projectRepository;
  }

  @GetMapping
  public List<Map<String, Object>> list(
      @RequestParam(required = false) String vendorId,
      @RequestParam(required = false) String projectId,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String keyword) {
    Specification<ComplianceItemEntity> specification = Specification.where(null);
    if (vendorId != null && !vendorId.isEmpty()) {
      specification = specification.and((root, query, cb) -> cb.equal(root.get("vendor").get("id"), vendorId));
    }
    if (projectId != null && !projectId.isEmpty()) {
      specification = specification.and((root, query, cb) -> cb.equal(root.get("project").get("id"), projectId));
    }
    if (status != null && !status.isEmpty()) {
      specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), status));
    }
    if (type != null && !type.isEmpty()) {
      specification = specification.and((root, query, cb) -> cb.equal(root.get("type"), type));
    }
    if (keyword != null && !keyword.trim().isEmpty()) {
      String like = "%" + keyword.trim() + "%";
      specification = specification.and((root, query, cb) -> cb.or(
          cb.like(root.get("name"), like),
          cb.like(root.get("remark"), like)));
    }
    return complianceItemRepository.findAll(specification, Sort.by(Sort.Direction.ASC, "expiryDate"))
        .stream()
        .map(ApiSerializers::serializeComplianceItem)
        .collect(Collectors.toList());
  }

  @PostMapping
  @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
  public Map<String, Object> create(@RequestBody(required = false) Map<String, Object> body) {
    return ApiSerializers.serializeComplianceItem(saveItem(new ComplianceItemEntity(), body, true));
  }

  @PutMapping("/{id}")
  public Map<String, Object> update(@PathVariable String id, @RequestBody(required = false) Map<String, Object> body) {
    ComplianceItemEntity item = complianceItemRepository.findById(id).orElseThrow(() -> new NoSuchElementException("到期项不存在"));
    return ApiSerializers.serializeComplianceItem(saveItem(item, body, false));
  }

  private ComplianceItemEntity saveItem(ComplianceItemEntity item, Map<String, Object> body, boolean create) {
    Map<String, Object> payload = body == null ? java.util.Collections.<String, Object>emptyMap() : body;
    VendorEntity vendor = vendorRepository.findById(ValidationUtils.assertString(payload.get("vendorId"), "vendorId"))
        .orElseThrow(() -> new IllegalArgumentException("vendorId is required"));

    Object projectIdValue = payload.get("projectId");
    ProjectEntity project = null;
    if (projectIdValue instanceof String && !((String) projectIdValue).trim().isEmpty()) {
      project = projectRepository.findById(((String) projectIdValue).trim())
          .orElseThrow(() -> new IllegalArgumentException("Invalid projectId"));
    }

    if (create) {
      item.setId(UUID.randomUUID().toString().replace("-", ""));
    }
    item.setVendor(vendor);
    item.setProject(project);
    item.setType(ValidationUtils.assertEnum(payload.get("type"), ApiConstants.COMPLIANCE_ITEM_TYPES, "type"));
    item.setName(ValidationUtils.assertString(payload.get("name"), "name"));
    item.setIssueDate(ValidationUtils.assertDate(payload.get("issueDate"), "issueDate"));
    item.setExpiryDate(ValidationUtils.assertDate(payload.get("expiryDate"), "expiryDate"));
    item.setStatus(ValidationUtils.assertEnum(payload.get("status"), ApiConstants.COMPLIANCE_ITEM_STATUSES, "status"));
    item.setRemark(ValidationUtils.assertOptionalString(payload.get("remark")));
    return complianceItemRepository.save(item);
  }
}
