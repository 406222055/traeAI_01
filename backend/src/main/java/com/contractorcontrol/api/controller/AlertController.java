package com.contractorcontrol.api.controller;

import com.contractorcontrol.api.repository.ComplianceItemRepository;
import com.contractorcontrol.api.repository.PersonnelCertificateRepository;
import com.contractorcontrol.api.util.ApiSerializers;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

  private final ComplianceItemRepository complianceItemRepository;
  private final PersonnelCertificateRepository personnelCertificateRepository;

  public AlertController(
      ComplianceItemRepository complianceItemRepository,
      PersonnelCertificateRepository personnelCertificateRepository) {
    this.complianceItemRepository = complianceItemRepository;
    this.personnelCertificateRepository = personnelCertificateRepository;
  }

  @GetMapping("/expiring")
  public Map<String, Object> expiring() {
    Instant now = Instant.now();
    Instant within30 = now.plus(ApiSerializers.EXPIRING_SOON_DAYS, ChronoUnit.DAYS);
    Instant farPast = Instant.ofEpochMilli(0);
    Sort sort = Sort.by(Sort.Direction.ASC, "expiryDate");
    Map<String, Object> data = new LinkedHashMap<String, Object>();
    data.put("expiredItems", complianceItemRepository.findByExpiryDateBetween(farPast, now.minusMillis(1), sort).stream().map(ApiSerializers::serializeComplianceItem).collect(Collectors.toList()));
    data.put("expiringSoonItems", complianceItemRepository.findByExpiryDateBetween(now, within30, sort).stream().map(ApiSerializers::serializeComplianceItem).collect(Collectors.toList()));
    data.put("expiredPersonnelCerts", personnelCertificateRepository.findByExpiryDateBetween(farPast, now.minusMillis(1), sort).stream().map(ApiSerializers::serializePersonnelCertificate).collect(Collectors.toList()));
    data.put("expiringSoonPersonnelCerts", personnelCertificateRepository.findByExpiryDateBetween(now, within30, sort).stream().map(ApiSerializers::serializePersonnelCertificate).collect(Collectors.toList()));
    return data;
  }
}
