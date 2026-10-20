package br.com.imovix.crm.application;

import br.com.imovix.crm.domain.Activity;
import br.com.imovix.crm.domain.ActivityRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para listar atividades de um lead ou proximas atividades da imobiliaria. */
@Service
public class ListActivitiesUseCase {

  private final ActivityRepository activityRepository;

  public ListActivitiesUseCase(ActivityRepository activityRepository) {
    this.activityRepository = activityRepository;
  }

  @Transactional(readOnly = true)
  public List<ActivityResponse> listByLead(UUID tenantId, UUID leadId) {
    List<Activity> activities = activityRepository.findByTenantIdAndLeadId(tenantId, leadId);
    return activities.stream().map(ActivityResponse::fromDomain).toList();
  }

  @Transactional(readOnly = true)
  public List<ActivityResponse> listUpcoming(UUID tenantId, int daysAhead) {
    Instant from = Instant.now().minus(1, ChronoUnit.DAYS);
    Instant to = Instant.now().plus(Math.max(1, daysAhead), ChronoUnit.DAYS);
    List<Activity> activities = activityRepository.findUpcomingByTenantId(tenantId, from, to);
    return activities.stream().map(ActivityResponse::fromDomain).toList();
  }
}
