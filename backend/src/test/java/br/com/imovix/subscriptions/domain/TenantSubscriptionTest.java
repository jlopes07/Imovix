package br.com.imovix.subscriptions.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TenantSubscriptionTest {

  @Test
  @DisplayName("Deve inicializar assinatura ativa e permitir troca de plano")
  void shouldInitializeActiveAndAllowPlanChange() {
    UUID tenantId = UUID.randomUUID();
    UUID planA = UUID.randomUUID();
    UUID planB = UUID.randomUUID();

    TenantSubscription sub =
        new TenantSubscription(
            null,
            tenantId,
            planA,
            SubscriptionStatus.ACTIVE,
            Instant.now(),
            Instant.now().plusSeconds(86400 * 30),
            false,
            null,
            null);

    assertNotNull(sub.getId());
    assertEquals(planA, sub.getPlanId());
    assertTrue(sub.isActive());
    assertFalse(sub.isCancelAtPeriodEnd());

    sub.changePlan(planB);
    assertEquals(planB, sub.getPlanId());
  }
}
