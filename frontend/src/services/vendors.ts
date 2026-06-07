import type { Vendor } from '../shared';
import { api } from './api';

export interface VendorPayload {
  name: string;
  creditCode: string;
  serviceType: string;
  contactName: string;
  contactPhone: string;
  status: string;
  remark?: string;
}

export function fetchVendors() {
  return api.get<Vendor[]>('/api/vendors');
}

export function fetchVendor(id: string) {
  return api.get<Vendor>(`/api/vendors/${id}`);
}

export function createVendor(payload: VendorPayload) {
  return api.post<Vendor>('/api/vendors', payload);
}

export function updateVendor(id: string, payload: VendorPayload) {
  return api.put<Vendor>(`/api/vendors/${id}`, payload);
}
