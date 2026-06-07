package com.contractorcontrol.api.repository;

import com.contractorcontrol.api.entity.VendorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VendorRepository extends JpaRepository<VendorEntity, String>, JpaSpecificationExecutor<VendorEntity> {
}
