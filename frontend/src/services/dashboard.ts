import type { DashboardSummary } from '../shared';
import { api } from './api';

export function fetchDashboardSummary() {
  return api.get<DashboardSummary>('/api/dashboard/summary');
}
