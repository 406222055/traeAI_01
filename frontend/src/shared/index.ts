export const WORKSPACE_NAME = 'contractor-control-platform';

export const USER_ROLES = ['platform_admin', 'project_admin'] as const;
export type UserRole = (typeof USER_ROLES)[number];

export const USER_STATUSES = ['active', 'disabled'] as const;
export type UserStatus = (typeof USER_STATUSES)[number];

export const VENDOR_STATUSES = ['active', 'inactive'] as const;
export type VendorStatus = (typeof VENDOR_STATUSES)[number];

export const PROJECT_STATUSES = ['active', 'inactive', 'completed'] as const;
export type ProjectStatus = (typeof PROJECT_STATUSES)[number];

export const ADMISSION_STATUSES = ['pending', 'approved', 'rejected'] as const;
export type AdmissionStatus = (typeof ADMISSION_STATUSES)[number];

export const COMPLIANCE_ITEM_TYPES = ['qualification', 'contract', 'insurance', 'safety', 'other'] as const;
export type ComplianceItemType = (typeof COMPLIANCE_ITEM_TYPES)[number];

export const COMPLIANCE_ITEM_STATUSES = ['active', 'expired'] as const;
export type ComplianceItemStatus = (typeof COMPLIANCE_ITEM_STATUSES)[number];

export const PERSONNEL_CERTIFICATE_TYPES = [
  'safety_operation',
  'special_operation',
  'electrician',
  'welder',
  'elevator',
  'crane',
  'scaffold',
  'high_altitude',
  'other',
] as const;
export type PersonnelCertificateType = (typeof PERSONNEL_CERTIFICATE_TYPES)[number];

export const PERSONNEL_CERTIFICATE_STATUSES = ['active', 'expiring_soon', 'expired'] as const;
export type PersonnelCertificateStatus = (typeof PERSONNEL_CERTIFICATE_STATUSES)[number];

export interface LoginPayload {
  username: string;
  password: string;
}

export interface AuthUser {
  id: string;
  username: string;
  name: string;
  role: UserRole;
  status: UserStatus;
}

export interface Vendor {
  id: string;
  name: string;
  creditCode: string;
  serviceType: string;
  contactName: string;
  contactPhone: string;
  status: VendorStatus;
  remark: string | null;
  createdAt: string;
}

export interface Project {
  id: string;
  code: string;
  name: string;
  region: string;
  managerName: string;
  status: ProjectStatus;
  createdAt: string;
}

export interface Admission {
  id: string;
  vendorId: string;
  projectId: string;
  applyDate: string;
  plannedEntryDate: string;
  scopeOfWork: string;
  status: AdmissionStatus;
  reviewComment: string | null;
  reviewedBy: string | null;
  reviewedAt: string | null;
  createdAt: string;
  vendor?: Vendor;
  project?: Project;
}

export interface ComplianceItem {
  id: string;
  vendorId: string;
  projectId: string | null;
  type: ComplianceItemType;
  name: string;
  issueDate: string;
  expiryDate: string;
  status: ComplianceItemStatus;
  remark: string | null;
  vendor?: Vendor;
  project?: Project | null;
}

export interface PersonnelCertificate {
  id: string;
  vendorId: string;
  projectId: string | null;
  personnelName: string;
  idCardNo: string | null;
  certificateType: PersonnelCertificateType;
  certificateNo: string;
  issueDate: string;
  expiryDate: string;
  status: PersonnelCertificateStatus;
  remark: string | null;
  createdAt: string;
  vendor?: Vendor;
  project?: Project | null;
}

export interface DashboardSummary {
  vendorCount: number;
  projectCount: number;
  pendingAdmissionCount: number;
  expiringIn7DaysCount: number;
  expiringIn30DaysCount: number;
  personnelCertExpiringIn7DaysCount: number;
  personnelCertExpiringIn30DaysCount: number;
}

export interface ExpiringAlertsResponse {
  within7Days: ComplianceItem[];
  within30Days: ComplianceItem[];
  personnelWithin7Days: PersonnelCertificate[];
  personnelWithin30Days: PersonnelCertificate[];
}
