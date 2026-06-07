package com.contractorcontrol.api.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class ApiConstants {

  public static final Set<String> USER_ROLES = new HashSet<String>(Arrays.asList("platform_admin", "project_admin"));
  public static final Set<String> USER_STATUSES = new HashSet<String>(Arrays.asList("active", "disabled"));
  public static final Set<String> VENDOR_STATUSES = new HashSet<String>(Arrays.asList("active", "inactive"));
  public static final Set<String> PROJECT_STATUSES = new HashSet<String>(Arrays.asList("active", "inactive", "completed"));
  public static final Set<String> ADMISSION_STATUSES = new HashSet<String>(Arrays.asList("pending", "approved", "rejected"));
  public static final Set<String> COMPLIANCE_ITEM_TYPES = new HashSet<String>(Arrays.asList("qualification", "contract", "insurance", "safety", "other"));
  public static final Set<String> COMPLIANCE_ITEM_STATUSES = new HashSet<String>(Arrays.asList("active", "expired"));
  public static final Set<String> PERSONNEL_CERTIFICATE_TYPES = new HashSet<String>(Arrays.asList("safety_operation", "special_operation", "electrician", "welder", "elevator", "crane", "scaffold", "high_altitude", "other"));
  public static final Set<String> PERSONNEL_CERTIFICATE_STATUSES = new HashSet<String>(Arrays.asList("active", "expiring_soon", "expired"));

  private ApiConstants() {
  }
}
