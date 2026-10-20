package br.com.imovix.subscriptions.infrastructure.persistence;

import br.com.imovix.subscriptions.domain.Plan;
import br.com.imovix.subscriptions.domain.PlanRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Adaptador de persistencia que implementa {@link PlanRepository} usando Spring Data JPA. */
@Component
public class JpaPlanRepositoryAdapter implements PlanRepository {

  private final SpringDataPlanRepository repository;

  public JpaPlanRepositoryAdapter(SpringDataPlanRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<Plan> findById(UUID id) {
    return repository.findById(id).map(PlanJpaEntity::toDomain);
  }

  @Override
  public Optional<Plan> findByCode(String code) {
    return repository.findByCode(code).map(PlanJpaEntity::toDomain);
  }

  @Override
  public List<Plan> findAllActive() {
    return repository.findByActiveTrueOrderByPriceAsc().stream()
        .map(PlanJpaEntity::toDomain)
        .toList();
  }

  @Override
  public Plan save(Plan plan) {
    PlanJpaEntity entity = PlanJpaEntity.fromDomain(plan);
    PlanJpaEntity saved = repository.save(entity);
    return saved.toDomain();
  }
}
