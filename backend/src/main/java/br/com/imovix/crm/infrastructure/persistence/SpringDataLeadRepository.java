package br.com.imovix.crm.infrastructure.persistence;

import br.com.imovix.crm.domain.LeadStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio Spring Data JPA para {@link LeadJpaEntity}. */
public interface SpringDataLeadRepository extends JpaRepository<LeadJpaEntity, UUID> {

  Optional<LeadJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);

  Page<LeadJpaEntity> findByTenantId(UUID tenantId, Pageable pageable);

  Page<LeadJpaEntity> findByTenantIdAndStatus(UUID tenantId, LeadStatus status, Pageable pageable);

  long countByTenantId(UUID tenantId);

  long countByTenantIdAndStatus(UUID tenantId, LeadStatus status);
}
