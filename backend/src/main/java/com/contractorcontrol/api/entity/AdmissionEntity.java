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
@Table(name = "admissions")
public class AdmissionEntity {

  @Id
  private String id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "vendor_id", nullable = false)
  private VendorEntity vendor;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "project_id", nullable = false)
  private ProjectEntity project;

  @Convert(converter = InstantLongConverter.class)
  @Column(name = "apply_date", nullable = false)
  private Instant applyDate;

  @Convert(converter = InstantLongConverter.class)
  @Column(name = "planned_entry_date", nullable = false)
  private Instant plannedEntryDate;

  @Column(name = "scope_of_work", nullable = false)
  private String scopeOfWork;

  @Column(nullable = false)
  private String status;

  @Column(name = "review_comment")
  private String reviewComment;

  @Column(name = "reviewed_by")
  private String reviewedBy;

  @Convert(converter = InstantLongConverter.class)
  @Column(name = "reviewed_at")
  private Instant reviewedAt;

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

  public Instant getApplyDate() {
    return applyDate;
  }

  public void setApplyDate(Instant applyDate) {
    this.applyDate = applyDate;
  }

  public Instant getPlannedEntryDate() {
    return plannedEntryDate;
  }

  public void setPlannedEntryDate(Instant plannedEntryDate) {
    this.plannedEntryDate = plannedEntryDate;
  }

  public String getScopeOfWork() {
    return scopeOfWork;
  }

  public void setScopeOfWork(String scopeOfWork) {
    this.scopeOfWork = scopeOfWork;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getReviewComment() {
    return reviewComment;
  }

  public void setReviewComment(String reviewComment) {
    this.reviewComment = reviewComment;
  }

  public String getReviewedBy() {
    return reviewedBy;
  }

  public void setReviewedBy(String reviewedBy) {
    this.reviewedBy = reviewedBy;
  }

  public Instant getReviewedAt() {
    return reviewedAt;
  }

  public void setReviewedAt(Instant reviewedAt) {
    this.reviewedAt = reviewedAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
