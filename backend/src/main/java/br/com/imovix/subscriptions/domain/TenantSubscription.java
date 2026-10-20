package br.com.imovix.subscriptions.domain;

import br.com.imovix.tenancy.domain.TenantScoped;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade representando o vinculo de assinatura de uma imobiliaria cliente com um plano do SaaS.
 */
public class TenantSubscription implements TenantScoped {

  private final UUID id;
  private final UUID tenantId;
  private UUID planId;
  private SubscriptionStatus status;
  private Instant currentPeriodStart;
  private Instant currentPeriodEnd;
  private boolean cancelAtPeriodEnd;
  private final Instant createdAt;
  private Instant updatedAt;

  public TenantSubscription(
      UUID id,
      UUID tenantId,
      UUID planId,
      SubscriptionStatus status,
      Instant currentPeriodStart,
      Instant currentPeriodEnd,
      boolean cancelAtPeriodEnd,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id != null ? id : UUID.randomUUID();
    this.tenantId = Objects.requireNonNull(tenantId, "O identificador do tenant e obrigatorio.");
    this.planId = Objects.requireNonNull(planId, "O identificador do plano e obrigatorio.");
    this.status = status != null ? status : SubscriptionStatus.ACTIVE;
    Instant now = Instant.now();
    this.currentPeriodStart = currentPeriodStart != null ? currentPeriodStart : now;
    this.currentPeriodEnd =
        currentPeriodEnd != null ? currentPeriodEnd : now.plusSeconds(86400 * 30);
    this.cancelAtPeriodEnd = cancelAtPeriodEnd;
    this.createdAt = createdAt != null ? createdAt : now;
    this.updatedAt = updatedAt != null ? updatedAt : now;
  }

  public void changePlan(UUID newPlanId) {
    this.planId = Objects.requireNonNull(newPlanId, "O identificador do novo plano e obrigatorio.");
    this.status = SubscriptionStatus.ACTIVE;
    this.cancelAtPeriodEnd = false;
    this.updatedAt = Instant.now();
  }

  public void cancel() {
    this.cancelAtPeriodEnd = true;
    this.updatedAt = Instant.now();
  }

  public void renew(Instant nextPeriodEnd) {
    this.currentPeriodStart = Instant.now();
    this.currentPeriodEnd =
        Objects.requireNonNull(nextPeriodEnd, "A data de fim do proximo periodo e obrigatoria.");
    this.status = SubscriptionStatus.ACTIVE;
    this.updatedAt = Instant.now();
  }

  public boolean isActive() {
    return (status == SubscriptionStatus.ACTIVE || status == SubscriptionStatus.TRIALING)
        && currentPeriodEnd.isAfter(Instant.now());
  }

  @Override
  public UUID getTenantId() {
    return tenantId;
  }

  public UUID getId() {
    return id;
  }

  public UUID getPlanId() {
    return planId;
  }

  public SubscriptionStatus getStatus() {
    return status;
  }

  public Instant getCurrentPeriodStart() {
    return currentPeriodStart;
  }

  public Instant getCurrentPeriodEnd() {
    return currentPeriodEnd;
  }

  public boolean isCancelAtPeriodEnd() {
    return cancelAtPeriodEnd;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
