import { useAuthStore } from '@/store/authStore';

const base = (import.meta.env.VITE_API_URL || '').replace(/\/$/, '');
export type Row = Record<string, any>;
export async function request(path: string, method = 'GET', data?: unknown): Promise<any> {
  const token = localStorage.getItem('auth_token');
  const response = await fetch(base + path, {
    method,
    headers: { ...(data instanceof FormData ? {} : { 'Content-Type': 'application/json' }), ...(token ? { Authorization: `Bearer ${token}` } : {}) },
    body: data === undefined ? undefined : data instanceof FormData ? data : JSON.stringify(data),
  });
  if (!response.ok) {
    const body = await response.json().catch(() => ({}));
    if (response.status === 401 && !path.startsWith('/api/auth/')) useAuthStore.getState().logout();
    throw new Error(body.message || (response.status === 403 ? 'Your role does not permit this action.' : `Request failed (${response.status}).`));
  }
  if (response.status === 204) return null;
  if (!response.headers.get('content-type')?.includes('application/json')) {
    throw new Error('The laboratory service is currently unavailable. Please try again later.');
  }
  return response.json();
}
export async function download(path: string, name: string) {
  const response = await fetch(base + path, { headers: { Authorization: `Bearer ${localStorage.getItem('auth_token') || ''}` } });
  if (!response.ok) throw new Error(`Download failed (${response.status})`);
  const url = URL.createObjectURL(await response.blob());
  const link = document.createElement('a'); link.href = url; link.download = name; link.click();
  setTimeout(() => URL.revokeObjectURL(url), 1000);
}
