package br.com.imovix.crm.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Porta de persistencia para a entidade {@link Lead} com escopo obrigatorio de tenant. */
public interface LeadRepository {

  Lead save(Lead lead);

  Optional<Lead> findByTenantIdAndId(UUID tenantId, UUID id);

  List<Lead> findByTenantId(UUID tenantId, LeadStatus status, int page, int size);

  long countByTenantId(UUID tenantId, LeadStatus status);

  void delete(Lead lead);
}
