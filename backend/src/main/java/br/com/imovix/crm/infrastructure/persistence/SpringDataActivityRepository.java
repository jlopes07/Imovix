package br.com.imovix.crm.infrastructure.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Repositorio Spring Data JPA para {@link ActivityJpaEntity}. */
public interface SpringDataActivityRepository extends JpaRepository<ActivityJpaEntity, UUID> {

  Optional<ActivityJpaEntity> findByTenantIdAndId(UUID tenantId, UUID id);

  List<ActivityJpaEntity> findByTenantIdAndLeadIdOrderByScheduledAtDesc(UUID tenantId, UUID leadId);

  @Query(
      "SELECT a FROM ActivityJpaEntity a WHERE a.tenantId = :tenantId AND a.scheduledAt BETWEEN"
          + " :from AND :to ORDER BY a.scheduledAt ASC")
  List<ActivityJpaEntity> findUpcomingByTenantId(
      @Param("tenantId") UUID tenantId, @Param("from") Instant from, @Param("to") Instant to);
}
