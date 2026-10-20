package br.com.imovix.crm.application;

import br.com.imovix.crm.domain.Lead;
import br.com.imovix.crm.domain.LeadNotFoundException;
import br.com.imovix.crm.domain.LeadRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para excluir um lead do tenant. */
@Service
public class DeleteLeadUseCase {

  private final LeadRepository leadRepository;

  public DeleteLeadUseCase(LeadRepository leadRepository) {
    this.leadRepository = leadRepository;
  }

  @Transactional
  public void execute(UUID tenantId, UUID leadId) {
    Lead lead =
        leadRepository
            .findByTenantIdAndId(tenantId, leadId)
            .orElseThrow(() -> new LeadNotFoundException(leadId));

    leadRepository.delete(lead);
  }
}
