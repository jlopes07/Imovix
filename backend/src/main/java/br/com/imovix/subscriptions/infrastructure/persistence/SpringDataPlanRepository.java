package br.com.imovix.subscriptions.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositorio Spring Data JPA para {@link PlanJpaEntity}. */
@Repository
public interface SpringDataPlanRepository extends JpaRepository<PlanJpaEntity, UUID> {

  Optional<PlanJpaEntity> findByCode(String code);

  List<PlanJpaEntity> findByActiveTrueOrderByPriceAsc();
}
