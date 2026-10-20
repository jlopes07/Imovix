package br.com.imovix.crm.application;

import br.com.imovix.crm.domain.LeadNotFoundException;
import br.com.imovix.crm.domain.LeadRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para obter os detalhes de um lead por identificador e tenant. */
@Service
public class GetLeadUseCase {

  private final LeadRepository leadRepository;

  public GetLeadUseCase(LeadRepository leadRepository) {
    this.leadRepository = leadRepository;
  }

  @Transactional(readOnly = true)
  public LeadResponse execute(UUID tenantId, UUID leadId) {
    return leadRepository
        .findByTenantIdAndId(tenantId, leadId)
        .map(LeadResponse::fromDomain)
        .orElseThrow(() -> new LeadNotFoundException(leadId));
  }
}
