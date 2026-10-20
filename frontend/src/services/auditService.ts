import { apiFetch } from './api';
import type { PagedAuditLogsResponse } from '../types/audit';

export const AuditService = {
  async listEvents(page = 0, size = 20): Promise<PagedAuditLogsResponse> {
    const query = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    return await apiFetch<PagedAuditLogsResponse>(`/api/v1/audit/events?${query.toString()}`);
  },
};
