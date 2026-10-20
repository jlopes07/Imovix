package br.com.imovix.subscriptions.infrastructure.persistence;

import br.com.imovix.subscriptions.domain.Plan;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Entidade JPA mapeada para a tabela imovix.plans. */
@Entity
@Table(name = "plans", schema = "imovix")
public class PlanJpaEntity {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "code", nullable = false, unique = true)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "price", nullable = false)
  private BigDecimal price;

  @Column(name = "billing_cycle", nullable = false)
  private String billingCycle;

  @Column(name = "max_properties", nullable = false)
  private int maxProperties;

  @Column(name = "max_users", nullable = false)
  private int maxUsers;

  @Column(name = "active", nullable = false)
  private boolean active;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public PlanJpaEntity() {}

  public static PlanJpaEntity fromDomain(Plan plan) {
    PlanJpaEntity entity = new PlanJpaEntity();
    entity.id = plan.getId();
    entity.code = plan.getCode();
    entity.name = plan.getName();
    entity.description = plan.getDescription();
    entity.price = plan.getPrice();
    entity.billingCycle = plan.getBillingCycle();
    entity.maxProperties = plan.getMaxProperties();
    entity.maxUsers = plan.getMaxUsers();
    entity.active = plan.isActive();
    entity.createdAt = plan.getCreatedAt();
    entity.updatedAt = plan.getUpdatedAt();
    return entity;
  }

  public Plan toDomain() {
    return new Plan(
        this.id,
        this.code,
        this.name,
        this.description,
        this.price,
        this.billingCycle,
        this.maxProperties,
        this.maxUsers,
        this.active,
        this.createdAt,
        this.updatedAt);
  }

  public UUID getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public String getBillingCycle() {
    return billingCycle;
  }

  public int getMaxProperties() {
    return maxProperties;
  }

  public int getMaxUsers() {
    return maxUsers;
  }

  public boolean isActive() {
    return active;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
