package br.com.imovix.subscriptions.application;

import br.com.imovix.subscriptions.domain.Plan;
import br.com.imovix.subscriptions.domain.SubscriptionStatus;
import br.com.imovix.subscriptions.domain.TenantSubscription;
import java.time.Instant;
import java.util.UUID;

/** DTO com os detalhes da assinatura do tenant e metricas de consumo de cotas. */
public record SubscriptionResponse(
    UUID id,
    UUID tenantId,
    PlanResponse plan,
    SubscriptionStatus status,
    Instant currentPeriodStart,
    Instant currentPeriodEnd,
    boolean cancelAtPeriodEnd,
    boolean isActive,
    long currentPropertiesCount,
    int maxProperties,
    boolean isPropertiesLimitReached,
    long currentUsersCount,
    int maxUsers,
    boolean isUsersLimitReached) {

  public static SubscriptionResponse of(
      TenantSubscription subscription, Plan plan, long propertiesCount, long usersCount) {
    boolean propertiesReached = propertiesCount >= plan.getMaxProperties();
    boolean usersReached = usersCount >= plan.getMaxUsers();

    return new SubscriptionResponse(
        subscription.getId(),
        subscription.getTenantId(),
        PlanResponse.fromDomain(plan),
        subscription.getStatus(),
        subscription.getCurrentPeriodStart(),
        subscription.getCurrentPeriodEnd(),
        subscription.isCancelAtPeriodEnd(),
        subscription.isActive(),
        propertiesCount,
        plan.getMaxProperties(),
        propertiesReached,
        usersCount,
        plan.getMaxUsers(),
        usersReached);
  }
}
