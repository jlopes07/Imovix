package br.com.imovix.crm.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LeadTest {

  @Test
  @DisplayName("Deve instanciar lead com valores padrao corretos")
  void shouldCreateLeadWithDefaults() {
    UUID tenantId = UUID.randomUUID();
    Lead lead =
        new Lead(
            null,
            tenantId,
            "Carlos Silva",
            "carlos@exemplo.com",
            "(11) 98765-4321",
            ContactType.BUYER,
            null,
            null,
            null,
            null,
            new BigDecimal("500000"),
            new BigDecimal("750000"),
            "Interesse em apartamento proximo ao metro",
            null,
            null,
            null,
            null);

    assertNotNull(lead.getId());
    assertEquals(tenantId, lead.getTenantId());
    assertEquals("Carlos Silva", lead.getName());
    assertEquals(LeadStatus.NEW, lead.getStatus());
    assertEquals(LeadSource.WEBSITE, lead.getSource());
    assertNotNull(lead.getCreatedAt());
  }

  @Test
  @DisplayName("Deve lancar excecao ao tentar criar lead com nome em branco")
  void shouldThrowExceptionWhenNameIsBlank() {
    UUID tenantId = UUID.randomUUID();
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Lead(
                null,
                tenantId,
                "   ",
                null,
                null,
                ContactType.BUYER,
                LeadStatus.NEW,
                LeadSource.WEBSITE,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null));
  }

  @Test
  @DisplayName("Deve avancar status no funil comercial")
  void shouldChangeLeadStatus() {
    UUID tenantId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    Lead lead =
        new Lead(
            null,
            tenantId,
            "Mariana Souza",
            "mariana@exemplo.com",
            null,
            ContactType.BUYER,
            LeadStatus.NEW,
            LeadSource.WHATSAPP,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    lead.changeStatus(LeadStatus.QUALIFIED, userId);
    assertEquals(LeadStatus.QUALIFIED, lead.getStatus());
    assertEquals(userId, lead.getUpdatedBy());
  }
}
