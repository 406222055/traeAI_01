import type { AuthUser, LoginPayload } from '../shared';
import { api } from './api';

export function login(payload: LoginPayload) {
  return api.post<{ token: string; user: AuthUser }>('/api/auth/login', payload);
}

export function fetchMe() {
  return api.get<{ user: AuthUser }>('/api/auth/me');
}
