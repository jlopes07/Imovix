package br.com.imovix.audit.infrastructure.persistence;

import br.com.imovix.audit.domain.AuditEvent;
import br.com.imovix.audit.domain.AuditEventRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

/** Adaptador de persistencia que implementa {@link AuditEventRepository}. */
@Component
public class JpaAuditEventRepositoryAdapter implements AuditEventRepository {

  private final SpringDataAuditEventRepository repository;

  public JpaAuditEventRepositoryAdapter(SpringDataAuditEventRepository repository) {
    this.repository = repository;
  }

  @Override
  public AuditEvent save(AuditEvent event) {
    AuditEventJpaEntity entity = AuditEventJpaEntity.fromDomain(event);
    return repository.save(entity).toDomain();
  }

  @Override
  public List<AuditEvent> findByTenantId(UUID tenantId, int page, int size) {
    return repository
        .findByTenantIdOrderByCreatedAtDesc(tenantId, PageRequest.of(page, size))
        .stream()
        .map(AuditEventJpaEntity::toDomain)
        .toList();
  }

  @Override
  public long countByTenantId(UUID tenantId) {
    return repository.countByTenantId(tenantId);
  }
}
