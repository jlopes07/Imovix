package br.com.imovix.audit.application;

import br.com.imovix.audit.domain.AuditEvent;
import java.time.Instant;
import java.util.UUID;

/** DTO de resposta para eventos de auditoria. */
public record AuditLogResponse(
    UUID id,
    UUID tenantId,
    UUID userId,
    String userEmail,
    String action,
    String resourceType,
    String resourceId,
    String details,
    String ipAddress,
    String correlationId,
    Instant createdAt) {

  public static AuditLogResponse fromDomain(AuditEvent event) {
    return new AuditLogResponse(
        event.getId(),
        event.getTenantId(),
        event.getUserId(),
        event.getUserEmail(),
        event.getAction(),
        event.getResourceType(),
        event.getResourceId(),
        event.getDetails(),
        event.getIpAddress(),
        event.getCorrelationId(),
        event.getCreatedAt());
  }
}
