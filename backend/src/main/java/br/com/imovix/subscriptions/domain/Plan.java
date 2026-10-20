package br.com.imovix.subscriptions.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Entidade representando um plano de assinatura comercial disponivel no SaaS. */
public class Plan {

  private final UUID id;
  private final String code;
  private String name;
  private String description;
  private BigDecimal price;
  private String billingCycle;
  private int maxProperties;
  private int maxUsers;
  private boolean active;
  private final Instant createdAt;
  private Instant updatedAt;

  public Plan(
      UUID id,
      String code,
      String name,
      String description,
      BigDecimal price,
      String billingCycle,
      int maxProperties,
      int maxUsers,
      boolean active,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id != null ? id : UUID.randomUUID();
    this.code =
        Objects.requireNonNull(code, "O codigo do plano e obrigatorio.").trim().toUpperCase();
    this.name = Objects.requireNonNull(name, "O nome do plano e obrigatorio.").trim();
    this.description = description != null ? description.trim() : null;
    this.price = price != null ? price : BigDecimal.ZERO;
    this.billingCycle = billingCycle != null ? billingCycle : "MONTHLY";
    this.maxProperties = Math.max(1, maxProperties);
    this.maxUsers = Math.max(1, maxUsers);
    this.active = active;
    Instant now = Instant.now();
    this.createdAt = createdAt != null ? createdAt : now;
    this.updatedAt = updatedAt != null ? updatedAt : now;
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
