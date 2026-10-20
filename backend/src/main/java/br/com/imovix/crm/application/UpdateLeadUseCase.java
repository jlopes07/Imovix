package br.com.imovix.crm.application;

import br.com.imovix.crm.domain.ContactType;
import br.com.imovix.crm.domain.Lead;
import br.com.imovix.crm.domain.LeadNotFoundException;
import br.com.imovix.crm.domain.LeadRepository;
import br.com.imovix.crm.domain.LeadSource;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para atualizar os dados cadastrais de um lead do tenant. */
@Service
public class UpdateLeadUseCase {

  private final LeadRepository leadRepository;

  public UpdateLeadUseCase(LeadRepository leadRepository) {
    this.leadRepository = leadRepository;
  }

  @Transactional
  public LeadResponse execute(UpdateLeadCommand command) {
    Lead lead =
        leadRepository
            .findByTenantIdAndId(command.tenantId(), command.leadId())
            .orElseThrow(() -> new LeadNotFoundException(command.leadId()));

    lead.update(
        command.name(),
        command.email(),
        command.phone(),
        command.type(),
        command.source(),
        command.assignedUserId(),
        command.interestedPropertyId(),
        command.budgetMin(),
        command.budgetMax(),
        command.notes(),
        command.updaterUserId());

    Lead saved = leadRepository.save(lead);
    return LeadResponse.fromDomain(saved);
  }

  public record UpdateLeadCommand(
      UUID tenantId,
      UUID leadId,
      String name,
      String email,
      String phone,
      ContactType type,
      LeadSource source,
      UUID assignedUserId,
      UUID interestedPropertyId,
      BigDecimal budgetMin,
      BigDecimal budgetMax,
      String notes,
      UUID updaterUserId) {}
}
