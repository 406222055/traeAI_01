package com.contractorcontrol.api.entity;

import com.contractorcontrol.api.util.InstantLongConverter;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "personnel_certificates")
public class PersonnelCertificateEntity {

  @Id
  private String id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "vendor_id", nullable = false)
  private VendorEntity vendor;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "project_id")
  private ProjectEntity project;

  @Column(name = "personnel_name", nullable = false)
  private String personnelName;

  @Column(name = "id_card_no")
  private String idCardNo;

  @Column(name = "certificate_type", nullable = false)
  private String certificateType;

  @Column(name = "certificate_no", nullable = false)
  private String certificateNo;

  @Convert(converter = InstantLongConverter.class)
  @Column(name = "issue_date", nullable = false)
  private Instant issueDate;

  @Convert(converter = InstantLongConverter.class)
  @Column(name = "expiry_date", nullable = false)
  private Instant expiryDate;

  @Column(nullable = false)
  private String status;

  @Column
  private String remark;

  @Convert(converter = InstantLongConverter.class)
  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public VendorEntity getVendor() {
    return vendor;
  }

  public void setVendor(VendorEntity vendor) {
    this.vendor = vendor;
  }

  public ProjectEntity getProject() {
    return project;
  }

  public void setProject(ProjectEntity project) {
    this.project = project;
  }

  public String getPersonnelName() {
    return personnelName;
  }

  public void setPersonnelName(String personnelName) {
    this.personnelName = personnelName;
  }

  public String getIdCardNo() {
    return idCardNo;
  }

  public void setIdCardNo(String idCardNo) {
    this.idCardNo = idCardNo;
  }

  public String getCertificateType() {
    return certificateType;
  }

  public void setCertificateType(String certificateType) {
    this.certificateType = certificateType;
  }

  public String getCertificateNo() {
    return certificateNo;
  }

  public void setCertificateNo(String certificateNo) {
    this.certificateNo = certificateNo;
  }

  public Instant getIssueDate() {
    return issueDate;
  }

  public void setIssueDate(Instant issueDate) {
    this.issueDate = issueDate;
  }

  public Instant getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(Instant expiryDate) {
    this.expiryDate = expiryDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
