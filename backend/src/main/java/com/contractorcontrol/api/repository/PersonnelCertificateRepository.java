package com.contractorcontrol.api.repository;

import com.contractorcontrol.api.entity.PersonnelCertificateEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonnelCertificateRepository extends JpaRepository<PersonnelCertificateEntity, String>, JpaSpecificationExecutor<PersonnelCertificateEntity> {

  List<PersonnelCertificateEntity> findByExpiryDateBetween(Instant start, Instant end, Sort sort);

  long countByExpiryDateBetween(Instant start, Instant end);
}
