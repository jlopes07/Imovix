package br.com.imovix.audit.infrastructure.persistence;

import br.com.imovix.audit.domain.AuditEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Entidade JPA mapeada para a tabela imovix.audit_events. */
@Entity
@Table(name = "audit_events", schema = "imovix")
public class AuditEventJpaEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "user_email", length = 180)
  private String userEmail;

  @Column(name = "action", nullable = false, length = 80)
  private String action;

  @Column(name = "resource_type", nullable = false, length = 60)
  private String resourceType;

  @Column(name = "resource_id", length = 100)
  private String resourceId;

  @Column(name = "details", columnDefinition = "jsonb")
  private String details;

  @Column(name = "ip_address", length = 45)
  private String ipAddress;

  @Column(name = "correlation_id", length = 100)
  private String correlationId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  protected AuditEventJpaEntity() {}

  public static AuditEventJpaEntity fromDomain(AuditEvent event) {
    AuditEventJpaEntity entity = new AuditEventJpaEntity();
    entity.id = event.getId() != null ? event.getId() : UUID.randomUUID();
    entity.tenantId = event.getTenantId();
    entity.userId = event.getUserId();
    entity.userEmail = event.getUserEmail();
    entity.action = event.getAction();
    entity.resourceType = event.getResourceType();
    entity.resourceId = event.getResourceId();
    entity.details = event.getDetails();
    entity.ipAddress = event.getIpAddress();
    entity.correlationId = event.getCorrelationId();
    entity.createdAt = event.getCreatedAt() != null ? event.getCreatedAt() : Instant.now();
    return entity;
  }

  public AuditEvent toDomain() {
    return new AuditEvent(
        id,
        tenantId,
        userId,
        userEmail,
        action,
        resourceType,
        resourceId,
        details,
        ipAddress,
        correlationId,
        createdAt);
  }

  public UUID getId() {
    return id;
  }

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
