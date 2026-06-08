package com.contractorcontrol.api.controller;

import com.contractorcontrol.api.repository.AdmissionRepository;
import com.contractorcontrol.api.repository.ComplianceItemRepository;
import com.contractorcontrol.api.repository.PersonnelCertificateRepository;
import com.contractorcontrol.api.repository.ProjectRepository;
import com.contractorcontrol.api.repository.VendorRepository;
import com.contractorcontrol.api.util.ApiSerializers;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final VendorRepository vendorRepository;
  private final ProjectRepository projectRepository;
  private final AdmissionRepository admissionRepository;
  private final ComplianceItemRepository complianceItemRepository;
  private final PersonnelCertificateRepository personnelCertificateRepository;

  public DashboardController(
      VendorRepository vendorRepository,
      ProjectRepository projectRepository,
      AdmissionRepository admissionRepository,
      ComplianceItemRepository complianceItemRepository,
      PersonnelCertificateRepository personnelCertificateRepository) {
    this.vendorRepository = vendorRepository;
    this.projectRepository = projectRepository;
    this.admissionRepository = admissionRepository;
    this.complianceItemRepository = complianceItemRepository;
    this.personnelCertificateRepository = personnelCertificateRepository;
  }

  @GetMapping("/summary")
  public Map<String, Object> summary() {
    Instant now = Instant.now();
    Instant within30 = now.plus(ApiSerializers.EXPIRING_SOON_DAYS, ChronoUnit.DAYS);
    Instant farPast = Instant.ofEpochMilli(0);
    Map<String, Object> data = new LinkedHashMap<String, Object>();
    data.put("vendorCount", vendorRepository.count());
    data.put("projectCount", projectRepository.count());
    data.put("pendingAdmissionCount", admissionRepository.countByStatus("pending"));
    data.put("complianceExpiredCount", complianceItemRepository.countByExpiryDateBetween(farPast, now.minusMillis(1)));
    data.put("complianceExpiringSoonCount", complianceItemRepository.countByExpiryDateBetween(now, within30));
    data.put("personnelCertExpiredCount", personnelCertificateRepository.countByExpiryDateBetween(farPast, now.minusMillis(1)));
    data.put("personnelCertExpiringSoonCount", personnelCertificateRepository.countByExpiryDateBetween(now, within30));
    return data;
  }
}
