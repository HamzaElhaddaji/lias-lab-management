import { apiClient } from './apiClient';
import { Member, MemberProfile, Affiliation } from '@/types';

export const memberService = {
  async getProfile(): Promise<MemberProfile> {
    const response = await apiClient.get('/api/members/me');
    return response.data;
  },

  async getMember(id: number): Promise<Member> {
    const response = await apiClient.get(`/api/members/${id}`);
    return response.data;
  },

  async updateProfile(data: Partial<Member>) {
    const response = await apiClient.put('/api/members/me', data);
    return response.data;
  },

  async uploadPhoto(file: File) {
    const formData = new FormData();
    formData.append('photo', file);
    const response = await apiClient.post('/api/members/me/photo', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },

  async getAffiliations(): Promise<Affiliation[]> {
    const response = await apiClient.get('/api/affiliations');
    return response.data;
  },

  async addAffiliation(data: Omit<Affiliation, 'id'>) {
    const response = await apiClient.post('/api/affiliations', data);
    return response.data;
  },

  async updateAffiliation(id: number, data: Partial<Affiliation>) {
    const response = await apiClient.patch(`/api/affiliations/${id}`, data);
    return response.data;
  },
};
