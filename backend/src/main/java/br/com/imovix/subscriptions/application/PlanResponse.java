package br.com.imovix.subscriptions.application;

import br.com.imovix.subscriptions.domain.Plan;
import java.math.BigDecimal;
import java.util.UUID;

/** DTO representando os dados de um plano comercial do SaaS. */
public record PlanResponse(
    UUID id,
    String code,
    String name,
    String description,
    BigDecimal price,
    String billingCycle,
    int maxProperties,
    int maxUsers,
    boolean active) {

  public static PlanResponse fromDomain(Plan plan) {
    return new PlanResponse(
        plan.getId(),
        plan.getCode(),
        plan.getName(),
        plan.getDescription(),
        plan.getPrice(),
        plan.getBillingCycle(),
        plan.getMaxProperties(),
        plan.getMaxUsers(),
        plan.isActive());
  }
}
