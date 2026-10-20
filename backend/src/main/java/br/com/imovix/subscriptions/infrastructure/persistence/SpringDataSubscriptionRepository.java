package br.com.imovix.subscriptions.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositorio Spring Data JPA para {@link SubscriptionJpaEntity}. */
@Repository
public interface SpringDataSubscriptionRepository
    extends JpaRepository<SubscriptionJpaEntity, UUID> {

  Optional<SubscriptionJpaEntity> findByTenantId(UUID tenantId);
}
