package br.com.imovix.subscriptions.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import br.com.imovix.properties.domain.PropertyRepository;
import br.com.imovix.subscriptions.domain.Plan;
import br.com.imovix.subscriptions.domain.PlanLimitExceededException;
import br.com.imovix.subscriptions.domain.PlanRepository;
import br.com.imovix.subscriptions.domain.SubscriptionRepository;
import br.com.imovix.subscriptions.domain.SubscriptionStatus;
import br.com.imovix.subscriptions.domain.TenantSubscription;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckPlanLimitUseCaseTest {

  @Mock private SubscriptionRepository subscriptionRepository;
  @Mock private PlanRepository planRepository;
  @Mock private PropertyRepository propertyRepository;

  private CheckPlanLimitUseCase useCase;

  @BeforeEach
  void setUp() {
    useCase = new CheckPlanLimitUseCase(subscriptionRepository, planRepository, propertyRepository);
  }

  @Test
  @DisplayName("Deve permitir criacao de imovel quando cota nao foi atingida")
  void shouldAllowCreationWhenWithinLimit() {
    UUID tenantId = UUID.randomUUID();
    UUID planId = UUID.randomUUID();

    TenantSubscription sub =
        new TenantSubscription(
            UUID.randomUUID(),
            tenantId,
            planId,
            SubscriptionStatus.ACTIVE,
            Instant.now(),
            Instant.now().plusSeconds(86400 * 30),
            false,
            Instant.now(),
            Instant.now());

    Plan plan =
        new Plan(
            planId,
            "BASIC",
            "Plano Essencial",
            null,
            new BigDecimal("149.00"),
            "MONTHLY",
            15,
            2,
            true,
            Instant.now(),
            Instant.now());

    when(subscriptionRepository.findByTenantId(tenantId)).thenReturn(Optional.of(sub));
    when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
    when(propertyRepository.countByTenantId(tenantId)).thenReturn(10L);

    assertDoesNotThrow(() -> useCase.ensureCanCreateProperty(tenantId));
  }

  @Test
  @DisplayName("Deve lancar PlanLimitExceededException quando cota de imoveis for atingida")
  void shouldThrowWhenLimitExceeded() {
    UUID tenantId = UUID.randomUUID();
    UUID planId = UUID.randomUUID();

    TenantSubscription sub =
        new TenantSubscription(
            UUID.randomUUID(),
            tenantId,
            planId,
            SubscriptionStatus.ACTIVE,
            Instant.now(),
            Instant.now().plusSeconds(86400 * 30),
            false,
            Instant.now(),
            Instant.now());

    Plan plan =
        new Plan(
            planId,
            "BASIC",
            "Plano Essencial",
            null,
            new BigDecimal("149.00"),
            "MONTHLY",
            15,
            2,
            true,
            Instant.now(),
            Instant.now());

    when(subscriptionRepository.findByTenantId(tenantId)).thenReturn(Optional.of(sub));
    when(planRepository.findById(planId)).thenReturn(Optional.of(plan));
    when(propertyRepository.countByTenantId(tenantId)).thenReturn(15L);

    assertThrows(PlanLimitExceededException.class, () -> useCase.ensureCanCreateProperty(tenantId));
  }
}
