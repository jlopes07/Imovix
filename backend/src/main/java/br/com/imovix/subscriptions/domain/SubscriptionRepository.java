package br.com.imovix.subscriptions.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Porta de persistencia para entidades {@link TenantSubscription} com escopo obrigatorio de tenant.
 */
public interface SubscriptionRepository {

  Optional<TenantSubscription> findByTenantId(UUID tenantId);

  TenantSubscription save(TenantSubscription subscription);
}
