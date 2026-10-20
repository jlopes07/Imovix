package br.com.imovix.crm.application;

import br.com.imovix.crm.domain.Activity;
import br.com.imovix.crm.domain.ActivityNotFoundException;
import br.com.imovix.crm.domain.ActivityRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para concluir uma atividade ou visita, registrando feedback e impressao do cliente.
 */
@Service
public class CompleteActivityUseCase {

  private final ActivityRepository activityRepository;

  public CompleteActivityUseCase(ActivityRepository activityRepository) {
    this.activityRepository = activityRepository;
  }

  @Transactional
  public ActivityResponse execute(
      UUID tenantId, UUID activityId, String feedback, Instant completedAt) {
    Activity activity =
        activityRepository
            .findByTenantIdAndId(tenantId, activityId)
            .orElseThrow(() -> new ActivityNotFoundException(activityId));

    activity.complete(feedback, completedAt);
    Activity saved = activityRepository.save(activity);
    return ActivityResponse.fromDomain(saved);
  }
}
