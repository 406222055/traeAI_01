package com.contractorcontrol.api.util;

import com.contractorcontrol.api.entity.AdmissionEntity;
import com.contractorcontrol.api.entity.ComplianceItemEntity;
import com.contractorcontrol.api.entity.PersonnelCertificateEntity;
import com.contractorcontrol.api.entity.ProjectEntity;
import com.contractorcontrol.api.entity.UserEntity;
import com.contractorcontrol.api.entity.VendorEntity;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ApiSerializers {

  public static final String WORKSPACE_NAME = "contractor-control-platform";
  private static final DateTimeFormatter ISO_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

  private ApiSerializers() {
  }

  public static String formatInstant(Instant instant) {
    return instant == null ? null : ISO_FORMATTER.format(instant);
  }

  public static final int EXPIRING_SOON_DAYS = 30;

  public static String computeComplianceItemStatus(Instant expiryDate) {
    if (expiryDate == null) {
      return "expired";
    }
    Instant now = Instant.now();
    if (expiryDate.isBefore(now)) {
      return "expired";
    }
    Instant threshold = now.plus(EXPIRING_SOON_DAYS, ChronoUnit.DAYS);
    if (!expiryDate.isAfter(threshold)) {
      return "expiring_soon";
    }
    return "active";
  }

  public static String computePersonnelCertificateStatus(Instant expiryDate) {
    if (expiryDate == null) {
      return "expired";
    }
    Instant now = Instant.now();
    if (expiryDate.isBefore(now)) {
      return "expired";
    }
    Instant threshold = now.plus(EXPIRING_SOON_DAYS, ChronoUnit.DAYS);
    if (!expiryDate.isAfter(threshold)) {
      return "expiring_soon";
    }
    return "active";
  }

  public static Map<String, Object> serializeUser(UserEntity user) {
    LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
    data.put("id", user.getId());
    data.put("username", user.getUsername());
    data.put("name", user.getName());
    data.put("role", user.getRole());
    data.put("status", user.getStatus());
    data.put("createdAt", formatInstant(user.getCreatedAt()));
    return data;
  }

  public static Map<String, Object> serializeVendor(VendorEntity vendor) {
    LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
    data.put("id", vendor.getId());
    data.put("name", vendor.getName());
    data.put("creditCode", vendor.getCreditCode());
    data.put("serviceType", vendor.getServiceType());
    data.put("contactName", vendor.getContactName());
    data.put("contactPhone", vendor.getContactPhone());
    data.put("status", vendor.getStatus());
    data.put("remark", vendor.getRemark());
    data.put("createdAt", formatInstant(vendor.getCreatedAt()));
    return data;
  }

  public static Map<String, Object> serializeProject(ProjectEntity project) {
    LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
    data.put("id", project.getId());
    data.put("code", project.getCode());
    data.put("name", project.getName());
    data.put("region", project.getRegion());
    data.put("managerName", project.getManagerName());
    data.put("status", project.getStatus());
    data.put("createdAt", formatInstant(project.getCreatedAt()));
    return data;
  }

  public static Map<String, Object> serializeAdmission(AdmissionEntity admission) {
    LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
    data.put("id", admission.getId());
    data.put("vendorId", admission.getVendor().getId());
    data.put("projectId", admission.getProject().getId());
    data.put("applyDate", formatInstant(admission.getApplyDate()));
    data.put("plannedEntryDate", formatInstant(admission.getPlannedEntryDate()));
    data.put("scopeOfWork", admission.getScopeOfWork());
    data.put("status", admission.getStatus());
    data.put("reviewComment", admission.getReviewComment());
    data.put("reviewedBy", admission.getReviewedBy());
    data.put("reviewedAt", formatInstant(admission.getReviewedAt()));
    data.put("createdAt", formatInstant(admission.getCreatedAt()));
    data.put("vendor", serializeVendor(admission.getVendor()));
    data.put("project", serializeProject(admission.getProject()));
    return data;
  }

  public static Map<String, Object> serializeComplianceItem(ComplianceItemEntity item) {
    LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
    data.put("id", item.getId());
    data.put("vendorId", item.getVendor().getId());
    data.put("projectId", item.getProject() == null ? null : item.getProject().getId());
    data.put("type", item.getType());
    data.put("name", item.getName());
    data.put("issueDate", formatInstant(item.getIssueDate()));
    data.put("expiryDate", formatInstant(item.getExpiryDate()));
    data.put("status", computeComplianceItemStatus(item.getExpiryDate()));
    data.put("remark", item.getRemark());
    data.put("vendor", serializeVendor(item.getVendor()));
    data.put("project", item.getProject() == null ? null : serializeProject(item.getProject()));
    return data;
  }

  public static Map<String, Object> serializePersonnelCertificate(PersonnelCertificateEntity cert) {
    LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
    data.put("id", cert.getId());
    data.put("vendorId", cert.getVendor().getId());
    data.put("projectId", cert.getProject() == null ? null : cert.getProject().getId());
    data.put("personnelName", cert.getPersonnelName());
    data.put("idCardNo", cert.getIdCardNo());
    data.put("certificateType", cert.getCertificateType());
    data.put("certificateNo", cert.getCertificateNo());
    data.put("issueDate", formatInstant(cert.getIssueDate()));
    data.put("expiryDate", formatInstant(cert.getExpiryDate()));
    data.put("status", computePersonnelCertificateStatus(cert.getExpiryDate()));
    data.put("remark", cert.getRemark());
    data.put("createdAt", formatInstant(cert.getCreatedAt()));
    data.put("vendor", serializeVendor(cert.getVendor()));
    data.put("project", cert.getProject() == null ? null : serializeProject(cert.getProject()));
    return data;
  }
}
