package br.com.imovix.crm.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ActivityTest {

  @Test
  @DisplayName("Deve agendar atividade com sucesso")
  void shouldScheduleActivitySuccessfully() {
    UUID tenantId = UUID.randomUUID();
    UUID leadId = UUID.randomUUID();
    Instant scheduledAt = Instant.now().plusSeconds(86400);

    Activity activity =
        new Activity(
            null,
            tenantId,
            leadId,
            null,
            null,
            ActivityType.VISIT,
            ActivityStatus.SCHEDULED,
            "Visita no Edificio Horizonte",
            "Apresentar areas comuns e apto 402",
            scheduledAt,
            null,
            null,
            null,
            null);

    assertNotNull(activity.getId());
    assertEquals(ActivityStatus.SCHEDULED, activity.getStatus());
    assertEquals("Visita no Edificio Horizonte", activity.getTitle());
  }

  @Test
  @DisplayName("Deve concluir visita registrando feedback")
  void shouldCompleteActivityWithFeedback() {
    UUID tenantId = UUID.randomUUID();
    UUID leadId = UUID.randomUUID();
    Instant scheduledAt = Instant.now();

    Activity activity =
        new Activity(
            null,
            tenantId,
            leadId,
            null,
            null,
            ActivityType.VISIT,
            ActivityStatus.SCHEDULED,
            "Visita no Imovel",
            null,
            scheduledAt,
            null,
            null,
            null,
            null);

    activity.complete("Cliente adorou a sacada gourmet. Vai apresentar proposta.", Instant.now());

    assertEquals(ActivityStatus.COMPLETED, activity.getStatus());
    assertEquals(
        "Cliente adorou a sacada gourmet. Vai apresentar proposta.", activity.getFeedback());
    assertNotNull(activity.getCompletedAt());
  }

  @Test
  @DisplayName("Deve lancar excecao quando titulo for vazio")
  void shouldThrowExceptionWhenTitleIsBlank() {
    UUID tenantId = UUID.randomUUID();
    UUID leadId = UUID.randomUUID();
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Activity(
                null,
                tenantId,
                leadId,
                null,
                null,
                ActivityType.TASK,
                ActivityStatus.SCHEDULED,
                "  ",
                null,
                Instant.now(),
                null,
                null,
                null,
                null));
  }
}
