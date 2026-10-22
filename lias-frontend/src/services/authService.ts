import { apiClient } from './apiClient';
import { AuthResponse, LoginRequest, RegisterRequest } from '@/types';

export const authService = {
  async login(credentials: LoginRequest): Promise<AuthResponse> {
    const response = await apiClient.post('/api/auth/login', credentials);
    return response.data;
  },

  async register(data: RegisterRequest): Promise<AuthResponse> {
    const response = await apiClient.post('/api/auth/register', data);
    return response.data;
  },

  async logout() {
    apiClient.clearToken();
  },

  getToken() {
    return localStorage.getItem('auth_token');
  },

  setToken(token: string) {
    apiClient.setToken(token);
  },

  isAuthenticated() {
    return !!this.getToken();
  },
};
