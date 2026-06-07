package com.contractorcontrol.api.controller;

import com.contractorcontrol.api.entity.VendorEntity;
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
@RequestMapping("/api/vendors")
public class VendorController {

  private final VendorRepository vendorRepository;

  public VendorController(VendorRepository vendorRepository) {
    this.vendorRepository = vendorRepository;
  }

  @GetMapping
  public List<Map<String, Object>> list(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String status) {
    Specification<VendorEntity> specification = Specification.where(null);
    if (status != null && !status.isEmpty()) {
      specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), status));
    }
    if (keyword != null && !keyword.trim().isEmpty()) {
      String like = "%" + keyword.trim() + "%";
      specification = specification.and((root, query, cb) -> cb.or(
          cb.like(root.get("name"), like),
          cb.like(root.get("creditCode"), like),
          cb.like(root.get("contactName"), like)));
    }
    return vendorRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "createdAt"))
        .stream()
        .map(ApiSerializers::serializeVendor)
        .collect(Collectors.toList());
  }

  @GetMapping("/{id}")
  public Map<String, Object> detail(@PathVariable String id) {
    VendorEntity vendor = vendorRepository.findById(id).orElseThrow(() -> new NoSuchElementException("服务商不存在"));
    return ApiSerializers.serializeVendor(vendor);
  }

  @PostMapping
  @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
  public Map<String, Object> create(@RequestBody(required = false) Map<String, Object> body) {
    return ApiSerializers.serializeVendor(saveVendor(new VendorEntity(), body, true));
  }

  @PutMapping("/{id}")
  public Map<String, Object> update(@PathVariable String id, @RequestBody(required = false) Map<String, Object> body) {
    VendorEntity vendor = vendorRepository.findById(id).orElseThrow(() -> new NoSuchElementException("服务商不存在"));
    return ApiSerializers.serializeVendor(saveVendor(vendor, body, false));
  }

  private VendorEntity saveVendor(VendorEntity vendor, Map<String, Object> body, boolean create) {
    Map<String, Object> payload = body == null ? java.util.Collections.<String, Object>emptyMap() : body;
    if (create) {
      vendor.setId(UUID.randomUUID().toString().replace("-", ""));
      vendor.setCreatedAt(Instant.now());
    }
    vendor.setName(ValidationUtils.assertString(payload.get("name"), "name"));
    vendor.setCreditCode(ValidationUtils.assertString(payload.get("creditCode"), "creditCode"));
    vendor.setServiceType(ValidationUtils.assertString(payload.get("serviceType"), "serviceType"));
    vendor.setContactName(ValidationUtils.assertString(payload.get("contactName"), "contactName"));
    vendor.setContactPhone(ValidationUtils.assertString(payload.get("contactPhone"), "contactPhone"));
    vendor.setStatus(ValidationUtils.assertEnum(payload.get("status"), ApiConstants.VENDOR_STATUSES, "status"));
    vendor.setRemark(ValidationUtils.assertOptionalString(payload.get("remark")));
    return vendorRepository.save(vendor);
  }
}
