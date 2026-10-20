package br.com.imovix.audit.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Interface Spring Data JPA para {@link AuditEventJpaEntity}. */
@Repository
public interface SpringDataAuditEventRepository extends JpaRepository<AuditEventJpaEntity, UUID> {

  List<AuditEventJpaEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId, Pageable pageable);

  long countByTenantId(UUID tenantId);
}
