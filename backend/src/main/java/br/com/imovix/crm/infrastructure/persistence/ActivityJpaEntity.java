package br.com.imovix.crm.infrastructure.persistence;

import br.com.imovix.crm.domain.Activity;
import br.com.imovix.crm.domain.ActivityStatus;
import br.com.imovix.crm.domain.ActivityType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Entidade JPA mapeada para a tabela imovix.crm_activities. */
@Entity
@Table(name = "crm_activities", schema = "imovix")
public class ActivityJpaEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "lead_id", nullable = false)
  private UUID leadId;

  @Column(name = "property_id")
  private UUID propertyId;

  @Column(name = "user_id")
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private ActivityType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ActivityStatus status;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "scheduled_at", nullable = false)
  private Instant scheduledAt;

  @Column(name = "completed_at")
  private Instant completedAt;

  @Column(name = "feedback")
  private String feedback;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public ActivityJpaEntity() {}

  public static ActivityJpaEntity fromDomain(Activity activity) {
    ActivityJpaEntity entity = new ActivityJpaEntity();
    entity.id = activity.getId();
    entity.tenantId = activity.getTenantId();
    entity.leadId = activity.getLeadId();
    entity.propertyId = activity.getPropertyId();
    entity.userId = activity.getUserId();
    entity.type = activity.getType();
    entity.status = activity.getStatus();
    entity.title = activity.getTitle();
    entity.description = activity.getDescription();
    entity.scheduledAt = activity.getScheduledAt();
    entity.completedAt = activity.getCompletedAt();
    entity.feedback = activity.getFeedback();
    entity.createdAt = activity.getCreatedAt();
    entity.updatedAt = activity.getUpdatedAt();
    return entity;
  }

  public Activity toDomain() {
    return new Activity(
        this.id,
        this.tenantId,
        this.leadId,
        this.propertyId,
        this.userId,
        this.type,
        this.status,
        this.title,
        this.description,
        this.scheduledAt,
        this.completedAt,
        this.feedback,
        this.createdAt,
        this.updatedAt);
  }

  public UUID getId() {
    return id;
  }

  public UUID getTenantId() {
    return tenantId;
  }

  public UUID getLeadId() {
    return leadId;
  }

  public UUID getPropertyId() {
    return propertyId;
  }

  public UUID getUserId() {
    return userId;
  }

  public ActivityType getType() {
    return type;
  }

  public ActivityStatus getStatus() {
    return status;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public Instant getScheduledAt() {
    return scheduledAt;
  }

  public Instant getCompletedAt() {
    return completedAt;
  }

  public String getFeedback() {
    return feedback;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
