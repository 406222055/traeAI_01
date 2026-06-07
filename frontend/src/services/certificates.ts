import type { PersonnelCertificate } from '../shared';
import { api } from './api';

export interface PersonnelCertificatePayload {
  vendorId: string;
  projectId?: string;
  personnelName: string;
  idCardNo?: string;
  certificateType: string;
  certificateNo: string;
  issueDate: string;
  expiryDate: string;
  status: string;
  remark?: string;
}

export function fetchPersonnelCertificates(params?: {
  vendorId?: string;
  projectId?: string;
  status?: string;
  certificateType?: string;
  keyword?: string;
}) {
  return api.get<PersonnelCertificate[]>('/api/personnel-certificates', { params });
}

export function createPersonnelCertificate(payload: PersonnelCertificatePayload) {
  return api.post<PersonnelCertificate>('/api/personnel-certificates', payload);
}

export function updatePersonnelCertificate(id: string, payload: PersonnelCertificatePayload) {
  return api.put<PersonnelCertificate>(`/api/personnel-certificates/${id}`, payload);
}

export function deletePersonnelCertificate(id: string) {
  return api.delete<void>(`/api/personnel-certificates/${id}`);
}
