package br.com.imovix.subscriptions.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlanTest {

  @Test
  @DisplayName("Deve instanciar plano comercial com limites corretos")
  void shouldCreatePlanWithLimits() {
    Plan plan =
        new Plan(
            null,
            "PRO",
            "Plano Profissional",
            "Descricao",
            new BigDecimal("349.00"),
            "MONTHLY",
            100,
            10,
            true,
            null,
            null);

    assertNotNull(plan.getId());
    assertEquals("PRO", plan.getCode());
    assertEquals(100, plan.getMaxProperties());
    assertEquals(10, plan.getMaxUsers());
    assertTrue(plan.isActive());
  }
}
