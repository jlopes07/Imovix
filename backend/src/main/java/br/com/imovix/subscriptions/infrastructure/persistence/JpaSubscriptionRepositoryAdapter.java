package br.com.imovix.subscriptions.infrastructure.persistence;

import br.com.imovix.subscriptions.domain.SubscriptionRepository;
import br.com.imovix.subscriptions.domain.TenantSubscription;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Adaptador de persistencia que implementa {@link SubscriptionRepository} usando Spring Data JPA.
 */
@Component
public class JpaSubscriptionRepositoryAdapter implements SubscriptionRepository {

  private final SpringDataSubscriptionRepository repository;

  public JpaSubscriptionRepositoryAdapter(SpringDataSubscriptionRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<TenantSubscription> findByTenantId(UUID tenantId) {
    return repository.findByTenantId(tenantId).map(SubscriptionJpaEntity::toDomain);
  }

  @Override
  public TenantSubscription save(TenantSubscription subscription) {
    SubscriptionJpaEntity entity = SubscriptionJpaEntity.fromDomain(subscription);
    SubscriptionJpaEntity saved = repository.save(entity);
    return saved.toDomain();
  }
}
