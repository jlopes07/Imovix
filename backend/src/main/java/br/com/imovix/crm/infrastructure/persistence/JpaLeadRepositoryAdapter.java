package br.com.imovix.crm.infrastructure.persistence;

import br.com.imovix.crm.domain.Lead;
import br.com.imovix.crm.domain.LeadRepository;
import br.com.imovix.crm.domain.LeadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/** Adaptador de persistencia que implementa {@link LeadRepository} usando Spring Data JPA. */
@Component
public class JpaLeadRepositoryAdapter implements LeadRepository {

  private final SpringDataLeadRepository repository;

  public JpaLeadRepositoryAdapter(SpringDataLeadRepository repository) {
    this.repository = repository;
  }

  @Override
  public Lead save(Lead lead) {
    LeadJpaEntity entity = LeadJpaEntity.fromDomain(lead);
    LeadJpaEntity saved = repository.save(entity);
    return saved.toDomain();
  }

  @Override
  public Optional<Lead> findByTenantIdAndId(UUID tenantId, UUID id) {
    return repository.findByTenantIdAndId(tenantId, id).map(LeadJpaEntity::toDomain);
  }

  @Override
  public List<Lead> findByTenantId(UUID tenantId, LeadStatus status, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    if (status != null) {
      return repository
          .findByTenantIdAndStatus(tenantId, status, pageRequest)
          .map(LeadJpaEntity::toDomain)
          .getContent();
    }
    return repository
        .findByTenantId(tenantId, pageRequest)
        .map(LeadJpaEntity::toDomain)
        .getContent();
  }

  @Override
  public long countByTenantId(UUID tenantId, LeadStatus status) {
    if (status != null) {
      return repository.countByTenantIdAndStatus(tenantId, status);
    }
    return repository.countByTenantId(tenantId);
  }

  @Override
  public void delete(Lead lead) {
    repository.deleteById(lead.getId());
  }
}
