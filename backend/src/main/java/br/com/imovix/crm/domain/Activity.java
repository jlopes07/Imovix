package br.com.imovix.crm.domain;

import br.com.imovix.tenancy.domain.TenantScoped;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Entidade de dominio representando uma Atividade, Tarefa ou Visita imobiliaria. */
public class Activity implements TenantScoped {

  private final UUID id;
  private final UUID tenantId;
  private final UUID leadId;
  private UUID propertyId;
  private UUID userId;
  private ActivityType type;
  private ActivityStatus status;
  private String title;
  private String description;
  private Instant scheduledAt;
  private Instant completedAt;
  private String feedback;
  private final Instant createdAt;
  private Instant updatedAt;

  public Activity(
      UUID id,
      UUID tenantId,
      UUID leadId,
      UUID propertyId,
      UUID userId,
      ActivityType type,
      ActivityStatus status,
      String title,
      String description,
      Instant scheduledAt,
      Instant completedAt,
      String feedback,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id != null ? id : UUID.randomUUID();
    this.tenantId = Objects.requireNonNull(tenantId, "O identificador do tenant e obrigatorio.");
    this.leadId = Objects.requireNonNull(leadId, "O identificador do lead e obrigatorio.");
    this.propertyId = propertyId;
    this.userId = userId;
    this.type = Objects.requireNonNull(type, "O tipo da atividade e obrigatorio.");
    this.status = status != null ? status : ActivityStatus.SCHEDULED;
    this.title = validateTitle(title);
    this.description = description != null ? description.trim() : null;
    this.scheduledAt = Objects.requireNonNull(scheduledAt, "A data e hora agendada e obrigatoria.");
    this.completedAt = completedAt;
    this.feedback = feedback != null ? feedback.trim() : null;
    Instant now = Instant.now();
    this.createdAt = createdAt != null ? createdAt : now;
    this.updatedAt = updatedAt != null ? updatedAt : now;
  }

  private String validateTitle(String title) {
    if (title == null || title.trim().isBlank()) {
      throw new IllegalArgumentException("O titulo da atividade e obrigatorio.");
    }
    return title.trim();
  }

  public void complete(String feedback, Instant completedAt) {
    this.status = ActivityStatus.COMPLETED;
    this.feedback = feedback != null ? feedback.trim() : null;
    this.completedAt = completedAt != null ? completedAt : Instant.now();
    this.updatedAt = Instant.now();
  }

  public void cancel(String reason) {
    this.status = ActivityStatus.CANCELLED;
    if (reason != null && !reason.trim().isBlank()) {
      this.feedback = "Cancelada: " + reason.trim();
    }
    this.updatedAt = Instant.now();
  }

  public void reschedule(Instant newScheduledAt) {
    this.scheduledAt =
        Objects.requireNonNull(newScheduledAt, "A nova data agendada e obrigatoria.");
    this.status = ActivityStatus.SCHEDULED;
    this.updatedAt = Instant.now();
  }

  @Override
  public UUID getTenantId() {
    return tenantId;
  }

  public UUID getId() {
    return id;
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
