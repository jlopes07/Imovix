package br.com.imovix.crm.application;

import br.com.imovix.crm.domain.Lead;
import br.com.imovix.crm.domain.LeadNotFoundException;
import br.com.imovix.crm.domain.LeadRepository;
import br.com.imovix.crm.domain.LeadStatus;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para alterar o status/estagio do funil comercial de um lead. */
@Service
public class UpdateLeadStatusUseCase {

  private final LeadRepository leadRepository;

  public UpdateLeadStatusUseCase(LeadRepository leadRepository) {
    this.leadRepository = leadRepository;
  }

  @Transactional
  public LeadResponse execute(
      UUID tenantId, UUID leadId, LeadStatus newStatus, UUID updaterUserId) {
    Lead lead =
        leadRepository
            .findByTenantIdAndId(tenantId, leadId)
            .orElseThrow(() -> new LeadNotFoundException(leadId));

    lead.changeStatus(newStatus, updaterUserId);
    Lead saved = leadRepository.save(lead);
    return LeadResponse.fromDomain(saved);
  }
}
