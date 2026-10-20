package br.com.imovix.properties.infrastructure.persistence;

import br.com.imovix.properties.domain.PropertyStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositório Spring Data JPA para {@link PropertyJpaEntity}. */
@Repository
public interface SpringDataPropertyRepository extends JpaRepository<PropertyJpaEntity, UUID> {

  Optional<PropertyJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);

  boolean existsByTenantIdAndCode(UUID tenantId, String code);

  Page<PropertyJpaEntity> findByTenantId(UUID tenantId, Pageable pageable);

  Page<PropertyJpaEntity> findByTenantIdAndStatus(
      UUID tenantId, PropertyStatus status, Pageable pageable);

  long countByTenantId(UUID tenantId);

  long countByTenantIdAndStatus(UUID tenantId, PropertyStatus status);
}
