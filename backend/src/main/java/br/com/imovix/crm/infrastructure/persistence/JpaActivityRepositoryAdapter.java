package br.com.imovix.crm.infrastructure.persistence;

import br.com.imovix.crm.domain.Activity;
import br.com.imovix.crm.domain.ActivityRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Adaptador de persistencia que implementa {@link ActivityRepository} usando Spring Data JPA. */
@Component
public class JpaActivityRepositoryAdapter implements ActivityRepository {

  private final SpringDataActivityRepository repository;

  public JpaActivityRepositoryAdapter(SpringDataActivityRepository repository) {
    this.repository = repository;
  }

  @Override
  public Activity save(Activity activity) {
    ActivityJpaEntity entity = ActivityJpaEntity.fromDomain(activity);
    ActivityJpaEntity saved = repository.save(entity);
    return saved.toDomain();
  }

  @Override
  public Optional<Activity> findByTenantIdAndId(UUID tenantId, UUID id) {
    return repository.findByTenantIdAndId(tenantId, id).map(ActivityJpaEntity::toDomain);
  }

  @Override
  public List<Activity> findByTenantIdAndLeadId(UUID tenantId, UUID leadId) {
    return repository.findByTenantIdAndLeadIdOrderByScheduledAtDesc(tenantId, leadId).stream()
        .map(ActivityJpaEntity::toDomain)
        .toList();
  }

  @Override
  public List<Activity> findUpcomingByTenantId(UUID tenantId, Instant from, Instant to) {
    return repository.findUpcomingByTenantId(tenantId, from, to).stream()
        .map(ActivityJpaEntity::toDomain)
        .toList();
  }

  @Override
  public void delete(Activity activity) {
    repository.deleteById(activity.getId());
  }
}
