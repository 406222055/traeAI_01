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
@Table(name = "compliance_items")
public class ComplianceItemEntity {

  @Id
  private String id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "vendor_id", nullable = false)
  private VendorEntity vendor;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "project_id")
  private ProjectEntity project;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private String name;

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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
}
