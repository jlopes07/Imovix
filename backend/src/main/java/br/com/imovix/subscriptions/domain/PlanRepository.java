package br.com.imovix.subscriptions.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Porta de persistencia para entidades {@link Plan}. */
public interface PlanRepository {

  Optional<Plan> findById(UUID id);

  Optional<Plan> findByCode(String code);

  List<Plan> findAllActive();

  Plan save(Plan plan);
}
