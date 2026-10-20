package br.com.imovix.crm.application;

import br.com.imovix.crm.domain.Activity;
import br.com.imovix.crm.domain.ActivityRepository;
import br.com.imovix.crm.domain.ActivityStatus;
import br.com.imovix.crm.domain.ActivityType;
import br.com.imovix.crm.domain.LeadNotFoundException;
import br.com.imovix.crm.domain.LeadRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para agendar uma nova atividade ou visita imobiliaria vinculada a um lead. */
@Service
public class ScheduleActivityUseCase {

  private final ActivityRepository activityRepository;
  private final LeadRepository leadRepository;

  public ScheduleActivityUseCase(
      ActivityRepository activityRepository, LeadRepository leadRepository) {
    this.activityRepository = activityRepository;
    this.leadRepository = leadRepository;
  }

  @Transactional
  public ActivityResponse execute(ScheduleActivityCommand command) {
    // Valida que o lead pertence ao mesmo tenant
    leadRepository
        .findByTenantIdAndId(command.tenantId(), command.leadId())
        .orElseThrow(() -> new LeadNotFoundException(command.leadId()));

    Activity activity =
        new Activity(
            null,
            command.tenantId(),
            command.leadId(),
            command.propertyId(),
            command.userId(),
            command.type(),
            ActivityStatus.SCHEDULED,
            command.title(),
            command.description(),
            command.scheduledAt(),
            null,
            null,
            null,
            null);

    Activity saved = activityRepository.save(activity);
    return ActivityResponse.fromDomain(saved);
  }

  public record ScheduleActivityCommand(
      UUID tenantId,
      UUID leadId,
      UUID propertyId,
      UUID userId,
      ActivityType type,
      String title,
      String description,
      Instant scheduledAt) {}
}
