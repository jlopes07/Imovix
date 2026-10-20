package br.com.imovix.crm.application;

import br.com.imovix.crm.domain.Lead;
import br.com.imovix.crm.domain.LeadRepository;
import br.com.imovix.crm.domain.LeadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para listar leads do tenant ativo com suporte a filtro por status e paginacao. */
@Service
public class ListLeadsUseCase {

  private final LeadRepository leadRepository;

  public ListLeadsUseCase(LeadRepository leadRepository) {
    this.leadRepository = leadRepository;
  }

  @Transactional(readOnly = true)
  public PagedLeadsResponse execute(UUID tenantId, LeadStatus status, int page, int size) {
    int resolvedPage = Math.max(0, page);
    int resolvedSize = size > 0 && size <= 100 ? size : 20;

    List<Lead> leads = leadRepository.findByTenantId(tenantId, status, resolvedPage, resolvedSize);
    long totalElements = leadRepository.countByTenantId(tenantId, status);
    int totalPages = (int) Math.ceil((double) totalElements / resolvedSize);

    List<LeadResponse> content = leads.stream().map(LeadResponse::fromDomain).toList();

    return new PagedLeadsResponse(content, resolvedPage, resolvedSize, totalElements, totalPages);
  }
}
