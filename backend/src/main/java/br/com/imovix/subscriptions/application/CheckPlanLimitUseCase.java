package br.com.imovix.subscriptions.application;

import br.com.imovix.properties.domain.PropertyRepository;
import br.com.imovix.subscriptions.domain.Plan;
import br.com.imovix.subscriptions.domain.PlanLimitExceededException;
import br.com.imovix.subscriptions.domain.PlanRepository;
import br.com.imovix.subscriptions.domain.SubscriptionRepository;
import br.com.imovix.subscriptions.domain.TenantSubscription;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para validar se uma imobiliaria pode cadastrar novos recursos de acordo com a cota do
 * seu plano.
 */
@Service
public class CheckPlanLimitUseCase {

  private final SubscriptionRepository subscriptionRepository;
  private final PlanRepository planRepository;
  private final PropertyRepository propertyRepository;

  public CheckPlanLimitUseCase(
      SubscriptionRepository subscriptionRepository,
      PlanRepository planRepository,
      PropertyRepository propertyRepository) {
    this.subscriptionRepository = subscriptionRepository;
    this.planRepository = planRepository;
    this.propertyRepository = propertyRepository;
  }

  @Transactional(readOnly = true)
  public void ensureCanCreateProperty(UUID tenantId) {
    TenantSubscription subscription = subscriptionRepository.findByTenantId(tenantId).orElse(null);
    if (subscription == null) {
      return; // Se nao ha assinatura explicita ainda, nao bloqueia no modo local/dev
    }

    Plan plan = planRepository.findById(subscription.getPlanId()).orElse(null);
    if (plan == null) {
      return;
    }

    long currentProperties = propertyRepository.countByTenantId(tenantId);
    if (currentProperties >= plan.getMaxProperties()) {
      throw new PlanLimitExceededException(
          "imoveis", (int) currentProperties, plan.getMaxProperties());
    }
  }
}
