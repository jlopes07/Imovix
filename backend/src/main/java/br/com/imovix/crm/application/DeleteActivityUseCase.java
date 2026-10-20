package br.com.imovix.crm.application;

import br.com.imovix.crm.domain.Activity;
import br.com.imovix.crm.domain.ActivityNotFoundException;
import br.com.imovix.crm.domain.ActivityRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para excluir ou remover uma atividade do tenant. */
@Service
public class DeleteActivityUseCase {

  private final ActivityRepository activityRepository;

  public DeleteActivityUseCase(ActivityRepository activityRepository) {
    this.activityRepository = activityRepository;
  }

  @Transactional
  public void execute(UUID tenantId, UUID activityId) {
    Activity activity =
        activityRepository
            .findByTenantIdAndId(tenantId, activityId)
            .orElseThrow(() -> new ActivityNotFoundException(activityId));

    activityRepository.delete(activity);
  }
}
