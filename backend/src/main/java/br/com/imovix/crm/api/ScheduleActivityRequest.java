package br.com.imovix.crm.api;

import br.com.imovix.crm.application.ScheduleActivityUseCase.ScheduleActivityCommand;
import br.com.imovix.crm.domain.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

/** DTO de entrada para agendamento de atividade ou visita. */
public record ScheduleActivityRequest(
    @NotNull(message = "O identificador do lead e obrigatorio.") UUID leadId,
    UUID propertyId,
    UUID userId,
    @NotNull(message = "O tipo da atividade e obrigatorio.") ActivityType type,
    @NotBlank(message = "O titulo da atividade e obrigatorio.")
        @Size(max = 255, message = "O titulo deve ter no maximo 255 caracteres.")
        String title,
    String description,
    @NotNull(message = "A data e hora do agendamento sao obrigatorias.") Instant scheduledAt) {

  public ScheduleActivityCommand toCommand(UUID tenantId) {
    return new ScheduleActivityCommand(
        tenantId,
        this.leadId,
        this.propertyId,
        this.userId,
        this.type,
        this.title,
        this.description,
        this.scheduledAt);
  }
}
