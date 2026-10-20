package br.com.imovix.crm.application;

import br.com.imovix.crm.domain.ContactType;
import br.com.imovix.crm.domain.Lead;
import br.com.imovix.crm.domain.LeadSource;
import br.com.imovix.crm.domain.LeadStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** DTO de saida com os detalhes de um lead. */
public record LeadResponse(
    UUID id,
    UUID tenantId,
    String name,
    String email,
    String phone,
    ContactType type,
    LeadStatus status,
    LeadSource source,
    UUID assignedUserId,
    UUID interestedPropertyId,
    BigDecimal budgetMin,
    BigDecimal budgetMax,
    String notes,
    UUID createdBy,
    UUID updatedBy,
    Instant createdAt,
    Instant updatedAt) {

  public static LeadResponse fromDomain(Lead lead) {
    return new LeadResponse(
        lead.getId(),
        lead.getTenantId(),
        lead.getName(),
        lead.getEmail(),
        lead.getPhone(),
        lead.getType(),
        lead.getStatus(),
        lead.getSource(),
        lead.getAssignedUserId(),
        lead.getInterestedPropertyId(),
        lead.getBudgetMin(),
        lead.getBudgetMax(),
        lead.getNotes(),
        lead.getCreatedBy(),
        lead.getUpdatedBy(),
        lead.getCreatedAt(),
        lead.getUpdatedAt());
  }
}
