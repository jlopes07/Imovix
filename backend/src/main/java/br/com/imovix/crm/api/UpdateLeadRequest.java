package br.com.imovix.crm.api;

import br.com.imovix.crm.application.UpdateLeadUseCase.UpdateLeadCommand;
import br.com.imovix.crm.domain.ContactType;
import br.com.imovix.crm.domain.LeadSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

/** DTO de entrada para atualizacao cadastral de um lead. */
public record UpdateLeadRequest(
    @NotBlank(message = "O nome do contato e obrigatorio.")
        @Size(max = 255, message = "O nome deve ter no maximo 255 caracteres.")
        String name,
    @Size(max = 255, message = "O email deve ter no maximo 255 caracteres.") String email,
    @Size(max = 50, message = "O telefone deve ter no maximo 50 caracteres.") String phone,
    ContactType type,
    LeadSource source,
    UUID assignedUserId,
    UUID interestedPropertyId,
    BigDecimal budgetMin,
    BigDecimal budgetMax,
    String notes) {

  public UpdateLeadCommand toCommand(UUID tenantId, UUID leadId, UUID updaterUserId) {
    return new UpdateLeadCommand(
        tenantId,
        leadId,
        this.name,
        this.email,
        this.phone,
        this.type,
        this.source,
        this.assignedUserId,
        this.interestedPropertyId,
        this.budgetMin,
        this.budgetMax,
        this.notes,
        updaterUserId);
  }
}
