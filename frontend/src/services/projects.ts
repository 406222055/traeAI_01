import type { Project } from '../shared';
import { api } from './api';

export interface ProjectPayload {
  code: string;
  name: string;
  region: string;
  managerName: string;
  status: string;
}

export function fetchProjects() {
  return api.get<Project[]>('/api/projects');
}

export function fetchProject(id: string) {
  return api.get<Project>(`/api/projects/${id}`);
}

export function createProject(payload: ProjectPayload) {
  return api.post<Project>('/api/projects', payload);
}

export function updateProject(id: string, payload: ProjectPayload) {
  return api.put<Project>(`/api/projects/${id}`, payload);
}
