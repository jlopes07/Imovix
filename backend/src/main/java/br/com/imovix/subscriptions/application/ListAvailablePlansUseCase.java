package br.com.imovix.subscriptions.application;

import br.com.imovix.subscriptions.domain.PlanRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para listar todos os planos comerciais ativos disponiveis para contratacao. */
@Service
public class ListAvailablePlansUseCase {

  private final PlanRepository planRepository;

  public ListAvailablePlansUseCase(PlanRepository planRepository) {
    this.planRepository = planRepository;
  }

  @Transactional(readOnly = true)
  public List<PlanResponse> execute() {
    return planRepository.findAllActive().stream().map(PlanResponse::fromDomain).toList();
  }
}
