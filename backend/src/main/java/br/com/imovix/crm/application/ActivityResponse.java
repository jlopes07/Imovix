package br.com.imovix.crm.application;

import br.com.imovix.crm.domain.Activity;
import br.com.imovix.crm.domain.ActivityStatus;
import br.com.imovix.crm.domain.ActivityType;
import java.time.Instant;
import java.util.UUID;

/** DTO de saida com os detalhes de uma atividade ou visita. */
public record ActivityResponse(
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

  public static ActivityResponse fromDomain(Activity activity) {
    return new ActivityResponse(
        activity.getId(),
        activity.getTenantId(),
        activity.getLeadId(),
        activity.getPropertyId(),
        activity.getUserId(),
        activity.getType(),
        activity.getStatus(),
        activity.getTitle(),
        activity.getDescription(),
        activity.getScheduledAt(),
        activity.getCompletedAt(),
        activity.getFeedback(),
        activity.getCreatedAt(),
        activity.getUpdatedAt());
  }
}
