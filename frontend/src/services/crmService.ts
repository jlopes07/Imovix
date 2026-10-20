import { apiFetch } from './api';
import type {
  Activity,
  CompleteActivityPayload,
  CreateLeadPayload,
  Lead,
  LeadStatus,
  ScheduleActivityPayload,
  UpdateLeadPayload,
} from '../types/crm';

export interface PagedLeadsResponse {
  content: Lead[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export const CrmService = {
  async listLeads(status?: LeadStatus, page = 0, size = 50): Promise<PagedLeadsResponse> {
    const query = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    if (status) {
      query.append('status', status);
    }
    return await apiFetch<PagedLeadsResponse>(`/api/v1/leads?${query.toString()}`);
  },

  async getLeadById(id: string): Promise<Lead> {
    return await apiFetch<Lead>(`/api/v1/leads/${id}`);
  },

  async createLead(payload: CreateLeadPayload): Promise<Lead> {
    return await apiFetch<Lead>('/api/v1/leads', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  async updateLead(id: string, payload: UpdateLeadPayload): Promise<Lead> {
    return await apiFetch<Lead>(`/api/v1/leads/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    });
  },

  async updateLeadStatus(id: string, status: LeadStatus): Promise<Lead> {
    return await apiFetch<Lead>(`/api/v1/leads/${id}/status`, {
      method: 'PATCH',
      body: JSON.stringify({ status }),
    });
  },

  async deleteLead(id: string): Promise<void> {
    await apiFetch<void>(`/api/v1/leads/${id}`, {
      method: 'DELETE',
    });
  },

  async listActivitiesForLead(leadId: string): Promise<Activity[]> {
    return await apiFetch<Activity[]>(`/api/v1/leads/${leadId}/activities`);
  },

  async listUpcomingActivities(daysAhead = 30): Promise<Activity[]> {
    return await apiFetch<Activity[]>(`/api/v1/activities?daysAhead=${daysAhead}`);
  },

  async scheduleActivity(payload: ScheduleActivityPayload): Promise<Activity> {
    return await apiFetch<Activity>('/api/v1/activities', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  async completeActivity(id: string, payload: CompleteActivityPayload): Promise<Activity> {
    return await apiFetch<Activity>(`/api/v1/activities/${id}/complete`, {
      method: 'PATCH',
      body: JSON.stringify(payload),
    });
  },

  async deleteActivity(id: string): Promise<void> {
    await apiFetch<void>(`/api/v1/activities/${id}`, {
      method: 'DELETE',
    });
  },
};
