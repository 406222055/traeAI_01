package com.contractorcontrol.api.config;

import com.contractorcontrol.api.entity.AdmissionEntity;
import com.contractorcontrol.api.entity.ComplianceItemEntity;
import com.contractorcontrol.api.entity.ProjectEntity;
import com.contractorcontrol.api.entity.UserEntity;
import com.contractorcontrol.api.entity.VendorEntity;
import com.contractorcontrol.api.repository.AdmissionRepository;
import com.contractorcontrol.api.repository.ComplianceItemRepository;
import com.contractorcontrol.api.repository.ProjectRepository;
import com.contractorcontrol.api.repository.UserRepository;
import com.contractorcontrol.api.repository.VendorRepository;
import com.contractorcontrol.api.security.SecurityUtils;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

  @Bean
  public CommandLineRunner seedData(
      UserRepository userRepository,
      VendorRepository vendorRepository,
      ProjectRepository projectRepository,
      AdmissionRepository admissionRepository,
      ComplianceItemRepository complianceItemRepository,
      SecurityUtils securityUtils) {
    return args -> {
      if (userRepository.count() > 0) {
        return;
      }

      UserEntity admin = new UserEntity();
      admin.setId(id());
      admin.setUsername("platform_admin");
      admin.setPasswordHash(securityUtils.hashPassword("Admin123456"));
      admin.setName("平台管理员");
      admin.setRole("platform_admin");
      admin.setStatus("active");
      admin.setCreatedAt(Instant.now());
      userRepository.save(admin);

      UserEntity projectAdmin = new UserEntity();
      projectAdmin.setId(id());
      projectAdmin.setUsername("project_admin");
      projectAdmin.setPasswordHash(securityUtils.hashPassword("Project123456"));
      projectAdmin.setName("项目管理员");
      projectAdmin.setRole("project_admin");
      projectAdmin.setStatus("active");
      projectAdmin.setCreatedAt(Instant.now());
      userRepository.save(projectAdmin);

      VendorEntity vendorA = new VendorEntity();
      vendorA.setId(id());
      vendorA.setName("华建劳务服务有限公司");
      vendorA.setCreditCode("91310000123456789A");
      vendorA.setServiceType("劳务分包");
      vendorA.setContactName("张伟");
      vendorA.setContactPhone("13800000001");
      vendorA.setStatus("active");
      vendorA.setRemark("长期合作服务商");
      vendorA.setCreatedAt(Instant.now());
      vendorRepository.save(vendorA);

      VendorEntity vendorB = new VendorEntity();
      vendorB.setId(id());
      vendorB.setName("安泰设备安装有限公司");
      vendorB.setCreditCode("91310000123456789B");
      vendorB.setServiceType("设备安装");
      vendorB.setContactName("李娜");
      vendorB.setContactPhone("13800000002");
      vendorB.setStatus("active");
      vendorB.setRemark("具备大型设备吊装资质");
      vendorB.setCreatedAt(Instant.now());
      vendorRepository.save(vendorB);

      ProjectEntity projectA = new ProjectEntity();
      projectA.setId(id());
      projectA.setCode("XM-2026-001");
      projectA.setName("智慧园区一期工程");
      projectA.setRegion("华东");
      projectA.setManagerName("王强");
      projectA.setStatus("active");
      projectA.setCreatedAt(Instant.now());
      projectRepository.save(projectA);

      ProjectEntity projectB = new ProjectEntity();
      projectB.setId(id());
      projectB.setCode("XM-2026-002");
      projectB.setName("城市更新示范项目");
      projectB.setRegion("华南");
      projectB.setManagerName("赵敏");
      projectB.setStatus("active");
      projectB.setCreatedAt(Instant.now());
      projectRepository.save(projectB);

      AdmissionEntity admissionA = new AdmissionEntity();
      admissionA.setId(id());
      admissionA.setVendor(vendorA);
      admissionA.setProject(projectA);
      admissionA.setApplyDate(date("2026-06-01"));
      admissionA.setPlannedEntryDate(date("2026-06-10"));
      admissionA.setScopeOfWork("土建劳务施工");
      admissionA.setStatus("pending");
      admissionA.setCreatedAt(Instant.now());
      admissionRepository.save(admissionA);

      AdmissionEntity admissionB = new AdmissionEntity();
      admissionB.setId(id());
      admissionB.setVendor(vendorB);
      admissionB.setProject(projectB);
      admissionB.setApplyDate(date("2026-05-20"));
      admissionB.setPlannedEntryDate(date("2026-05-28"));
      admissionB.setScopeOfWork("机电设备安装");
      admissionB.setStatus("approved");
      admissionB.setReviewComment("资料齐全，准予入场");
      admissionB.setReviewedBy("平台管理员");
      admissionB.setReviewedAt(Instant.parse("2026-05-22T09:00:00Z"));
      admissionB.setCreatedAt(Instant.now());
      admissionRepository.save(admissionB);

      ComplianceItemEntity itemA = new ComplianceItemEntity();
      itemA.setId(id());
      itemA.setVendor(vendorA);
      itemA.setProject(projectA);
      itemA.setType("qualification");
      itemA.setName("安全生产许可证");
      itemA.setIssueDate(date("2025-06-01"));
      itemA.setExpiryDate(date("2026-06-10"));
      itemA.setStatus("active");
      itemA.setRemark("7 天内到期示例");
      complianceItemRepository.save(itemA);

      ComplianceItemEntity itemB = new ComplianceItemEntity();
      itemB.setId(id());
      itemB.setVendor(vendorA);
      itemB.setProject(projectA);
      itemB.setType("insurance");
      itemB.setName("雇主责任险");
      itemB.setIssueDate(date("2025-07-01"));
      itemB.setExpiryDate(date("2026-06-28"));
      itemB.setStatus("active");
      itemB.setRemark("30 天内到期示例");
      complianceItemRepository.save(itemB);

      ComplianceItemEntity itemC = new ComplianceItemEntity();
      itemC.setId(id());
      itemC.setVendor(vendorB);
      itemC.setProject(projectB);
      itemC.setType("contract");
      itemC.setName("设备安装合同");
      itemC.setIssueDate(date("2026-01-15"));
      itemC.setExpiryDate(date("2026-08-20"));
      itemC.setStatus("active");
      itemC.setRemark("正常有效");
      complianceItemRepository.save(itemC);
    };
  }

  private String id() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  private Instant date(String value) {
    return LocalDate.parse(value).atStartOfDay().toInstant(ZoneOffset.UTC);
  }
}
