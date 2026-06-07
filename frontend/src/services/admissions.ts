import type { Admission } from '../shared';
import { api } from './api';

export interface AdmissionPayload {
  vendorId: string;
  projectId: string;
  applyDate: string;
  plannedEntryDate: string;
  scopeOfWork: string;
}

export interface AdmissionReviewPayload {
  status: 'approved' | 'rejected';
  reviewComment?: string;
}

export function fetchAdmissions() {
  return api.get<Admission[]>('/api/admissions');
}

export function fetchAdmission(id: string) {
  return api.get<Admission>(`/api/admissions/${id}`);
}

export function createAdmission(payload: AdmissionPayload) {
  return api.post<Admission>('/api/admissions', payload);
}

export function reviewAdmission(id: string, payload: AdmissionReviewPayload) {
  return api.patch<Admission>(`/api/admissions/${id}/review`, payload);
}
