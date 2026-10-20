package br.com.imovix.crm.api;

import br.com.imovix.crm.application.CreateLeadUseCase.CreateLeadCommand;
import br.com.imovix.crm.domain.ContactType;
import br.com.imovix.crm.domain.LeadSource;
import br.com.imovix.crm.domain.LeadStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

/** DTO de entrada para criacao de um novo lead. */
public record CreateLeadRequest(
    @NotBlank(message = "O nome do contato e obrigatorio.")
        @Size(max = 255, message = "O nome deve ter no maximo 255 caracteres.")
        String name,
    @Size(max = 255, message = "O email deve ter no maximo 255 caracteres.") String email,
    @Size(max = 50, message = "O telefone deve ter no maximo 50 caracteres.") String phone,
    ContactType type,
    LeadStatus status,
    LeadSource source,
    UUID assignedUserId,
    UUID interestedPropertyId,
    BigDecimal budgetMin,
    BigDecimal budgetMax,
    String notes) {

  public CreateLeadCommand toCommand(UUID tenantId, UUID creatorUserId) {
    return new CreateLeadCommand(
        tenantId,
        this.name,
        this.email,
        this.phone,
        this.type,
        this.status,
        this.source,
        this.assignedUserId,
        this.interestedPropertyId,
        this.budgetMin,
        this.budgetMax,
        this.notes,
        creatorUserId);
  }
}
