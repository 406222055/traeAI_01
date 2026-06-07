import type { ComplianceItem, ExpiringAlertsResponse } from '../shared';
import { api } from './api';

export interface ComplianceItemPayload {
  vendorId: string;
  projectId?: string;
  type: string;
  name: string;
  issueDate: string;
  expiryDate: string;
  status: string;
  remark?: string;
}

export function fetchComplianceItems() {
  return api.get<ComplianceItem[]>('/api/compliance-items');
}

export function createComplianceItem(payload: ComplianceItemPayload) {
  return api.post<ComplianceItem>('/api/compliance-items', payload);
}

export function updateComplianceItem(id: string, payload: ComplianceItemPayload) {
  return api.put<ComplianceItem>(`/api/compliance-items/${id}`, payload);
}

export function fetchExpiringAlerts() {
  return api.get<ExpiringAlertsResponse>('/api/alerts/expiring');
}
