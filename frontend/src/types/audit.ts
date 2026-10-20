export interface AuditLog {
  id: string;
  tenantId: string;
  userId: string | null;
  userEmail: string | null;
  action: string;
  resourceType: string;
  resourceId: string | null;
  details: string | null;
  ipAddress: string | null;
  correlationId: string | null;
  createdAt: string;
}

export interface PagedAuditLogsResponse {
  content: AuditLog[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
