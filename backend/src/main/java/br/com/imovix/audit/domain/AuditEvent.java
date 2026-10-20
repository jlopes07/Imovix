package br.com.imovix.audit.domain;

import br.com.imovix.tenancy.domain.TenantScoped;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Entidade de auditoria para rastreabilidade de operacoes sensiveis no SaaS. */
public class AuditEvent implements TenantScoped {

  private final UUID id;
  private final UUID tenantId;
  private final UUID userId;
  private final String userEmail;
  private final String action;
  private final String resourceType;
  private final String resourceId;
  private final String details;
  private final String ipAddress;
  private final String correlationId;
  private final Instant createdAt;

  public AuditEvent(
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
    this.id = id;
    this.tenantId = Objects.requireNonNull(tenantId, "TenantId e obrigatorio.");
    this.userId = userId;
    this.userEmail = userEmail;
    this.action = Objects.requireNonNull(action, "Acao e obrigatoria.");
    this.resourceType = Objects.requireNonNull(resourceType, "Tipo de recurso e obrigatorio.");
    this.resourceId = resourceId;
    this.details = details;
    this.ipAddress = ipAddress;
    this.correlationId = correlationId;
    this.createdAt = createdAt != null ? createdAt : Instant.now();
  }

  public static AuditEvent create(
      UUID tenantId,
      UUID userId,
      String userEmail,
      String action,
      String resourceType,
      String resourceId,
      String details,
      String ipAddress,
      String correlationId) {
    return new AuditEvent(
        null,
        tenantId,
        userId,
        userEmail,
        action,
        resourceType,
        resourceId,
        details,
        ipAddress,
        correlationId,
        Instant.now());
  }

  public UUID getId() {
    return id;
  }

  @Override
  public UUID getTenantId() {
    return tenantId;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public String getAction() {
    return action;
  }

  public String getResourceType() {
    return resourceType;
  }

  public String getResourceId() {
    return resourceId;
  }

  public String getDetails() {
    return details;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
