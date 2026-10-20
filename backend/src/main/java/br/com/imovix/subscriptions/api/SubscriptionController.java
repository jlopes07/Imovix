package br.com.imovix.subscriptions.api;

import br.com.imovix.subscriptions.application.ChangePlanUseCase;
import br.com.imovix.subscriptions.application.GetCurrentSubscriptionUseCase;
import br.com.imovix.subscriptions.application.ListAvailablePlansUseCase;
import br.com.imovix.subscriptions.application.PlanResponse;
import br.com.imovix.subscriptions.application.SubscriptionResponse;
import br.com.imovix.tenancy.application.TenantContext;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint REST para gerenciamento de planos, assinaturas e metricas de consumo. */
@RestController
@RequestMapping("/api/v1")
public class SubscriptionController {

  private final GetCurrentSubscriptionUseCase getCurrentSubscriptionUseCase;
  private final ChangePlanUseCase changePlanUseCase;
  private final ListAvailablePlansUseCase listAvailablePlansUseCase;

  public SubscriptionController(
      GetCurrentSubscriptionUseCase getCurrentSubscriptionUseCase,
      ChangePlanUseCase changePlanUseCase,
      ListAvailablePlansUseCase listAvailablePlansUseCase) {
    this.getCurrentSubscriptionUseCase = getCurrentSubscriptionUseCase;
    this.changePlanUseCase = changePlanUseCase;
    this.listAvailablePlansUseCase = listAvailablePlansUseCase;
  }

  @GetMapping("/subscriptions/current")
  public ResponseEntity<SubscriptionResponse> getCurrentSubscription() {
    UUID tenantId = TenantContext.requireId();
    return ResponseEntity.ok(getCurrentSubscriptionUseCase.execute(tenantId));
  }

  @PostMapping("/subscriptions/change-plan")
  public ResponseEntity<SubscriptionResponse> changePlan(
      @Valid @RequestBody ChangePlanRequest request) {
    UUID tenantId = TenantContext.requireId();
    return ResponseEntity.ok(changePlanUseCase.execute(tenantId, request.planId()));
  }

  @GetMapping("/plans")
  public ResponseEntity<List<PlanResponse>> listPlans() {
    return ResponseEntity.ok(listAvailablePlansUseCase.execute());
  }
}
