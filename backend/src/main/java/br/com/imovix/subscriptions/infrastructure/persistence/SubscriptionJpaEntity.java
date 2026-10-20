package br.com.imovix.subscriptions.infrastructure.persistence;

import br.com.imovix.subscriptions.domain.SubscriptionStatus;
import br.com.imovix.subscriptions.domain.TenantSubscription;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Entidade JPA mapeada para a tabela imovix.tenant_subscriptions. */
@Entity
@Table(name = "tenant_subscriptions", schema = "imovix")
public class SubscriptionJpaEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "tenant_id", nullable = false, unique = true)
  private UUID tenantId;

  @Column(name = "plan_id", nullable = false)
  private UUID planId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private SubscriptionStatus status;

  @Column(name = "current_period_start", nullable = false)
  private Instant currentPeriodStart;

  @Column(name = "current_period_end", nullable = false)
  private Instant currentPeriodEnd;

  @Column(name = "cancel_at_period_end", nullable = false)
  private boolean cancelAtPeriodEnd;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public SubscriptionJpaEntity() {}

  public static SubscriptionJpaEntity fromDomain(TenantSubscription subscription) {
    SubscriptionJpaEntity entity = new SubscriptionJpaEntity();
    entity.id = subscription.getId();
    entity.tenantId = subscription.getTenantId();
    entity.planId = subscription.getPlanId();
    entity.status = subscription.getStatus();
    entity.currentPeriodStart = subscription.getCurrentPeriodStart();
    entity.currentPeriodEnd = subscription.getCurrentPeriodEnd();
    entity.cancelAtPeriodEnd = subscription.isCancelAtPeriodEnd();
    entity.createdAt = subscription.getCreatedAt();
    entity.updatedAt = subscription.getUpdatedAt();
    return entity;
  }

  public TenantSubscription toDomain() {
    return new TenantSubscription(
        this.id,
        this.tenantId,
        this.planId,
        this.status,
        this.currentPeriodStart,
        this.currentPeriodEnd,
        this.cancelAtPeriodEnd,
        this.createdAt,
        this.updatedAt);
  }

  public UUID getId() {
    return id;
  }

  public UUID getTenantId() {
    return tenantId;
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
