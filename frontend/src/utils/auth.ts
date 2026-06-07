const TOKEN_KEY = 'contractor_control_token';
const USER_KEY = 'contractor_control_user';

export interface StoredUser {
  id: string;
  username: string;
  name: string;
  role: string;
  status: string;
  createdAt?: string;
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setAuth(token: string, user: StoredUser) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}

export function getStoredUser(): StoredUser | null {
  const raw = localStorage.getItem(USER_KEY);

  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw) as StoredUser;
  } catch {
    clearAuth();
    return null;
  }
}
