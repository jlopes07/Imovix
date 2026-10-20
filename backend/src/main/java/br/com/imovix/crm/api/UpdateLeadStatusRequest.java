package br.com.imovix.crm.api;

import br.com.imovix.crm.domain.LeadStatus;
import jakarta.validation.constraints.NotNull;

/** DTO de entrada para atualizacao rapida do estagio do lead no funil. */
public record UpdateLeadStatusRequest(
    @NotNull(message = "O novo status e obrigatorio.") LeadStatus status) {}
