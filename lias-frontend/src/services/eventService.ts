import { apiClient } from './apiClient';
import { Event } from '@/types';

export const eventService = {
  async getAllEvents(params?: Record<string, unknown>) {
    const response = await apiClient.get('/api/events', { params });
    return response.data;
  },

  async getEvent(id: number): Promise<Event> {
    const response = await apiClient.get(`/api/events/${id}`);
    return response.data;
  },

  async getMyEvents(): Promise<Event[]> {
    const response = await apiClient.get('/api/events/me');
    return response.data;
  },

  async getByType(type: string): Promise<Event[]> {
    const response = await apiClient.get(`/api/events/type/${type}`);
    return response.data;
  },

  async getByStatus(status: string): Promise<Event[]> {
    const response = await apiClient.get(`/api/events/status/${status}`);
    return response.data;
  },

  async createEvent(data: Omit<Event, 'id' | 'createdAt' | 'createdBy'>) {
    const response = await apiClient.post('/api/events', data);
    return response.data;
  },

  async updateEvent(id: number, data: Partial<Event>) {
    const response = await apiClient.put(`/api/events/${id}`, data);
    return response.data;
  },

  async updateStatus(id: number, status: string) {
    const response = await apiClient.patch(`/api/events/${id}/status`, { status });
    return response.data;
  },

  async deleteEvent(id: number) {
    await apiClient.delete(`/api/events/${id}`);
  },
};
