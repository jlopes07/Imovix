package br.com.imovix.subscriptions.application;

import br.com.imovix.companies.domain.CompanyMembershipRepository;
import br.com.imovix.properties.domain.PropertyRepository;
import br.com.imovix.subscriptions.domain.Plan;
import br.com.imovix.subscriptions.domain.PlanRepository;
import br.com.imovix.subscriptions.domain.SubscriptionNotFoundException;
import br.com.imovix.subscriptions.domain.SubscriptionRepository;
import br.com.imovix.subscriptions.domain.TenantSubscription;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para alterar ou realizar upgrade do plano contratado da imobiliaria. */
@Service
public class ChangePlanUseCase {

  private final SubscriptionRepository subscriptionRepository;
  private final PlanRepository planRepository;
  private final PropertyRepository propertyRepository;
  private final CompanyMembershipRepository membershipRepository;

  public ChangePlanUseCase(
      SubscriptionRepository subscriptionRepository,
      PlanRepository planRepository,
      PropertyRepository propertyRepository,
      CompanyMembershipRepository membershipRepository) {
    this.subscriptionRepository = subscriptionRepository;
    this.planRepository = planRepository;
    this.propertyRepository = propertyRepository;
    this.membershipRepository = membershipRepository;
  }

  @Transactional
  public SubscriptionResponse execute(UUID tenantId, UUID newPlanId) {
    Plan newPlan =
        planRepository
            .findById(newPlanId)
            .orElseThrow(() -> new IllegalArgumentException("Plano selecionado nao existe."));

    TenantSubscription subscription =
        subscriptionRepository
            .findByTenantId(tenantId)
            .orElseThrow(() -> new SubscriptionNotFoundException(tenantId));

    subscription.changePlan(newPlan.getId());
    TenantSubscription saved = subscriptionRepository.save(subscription);

    long propertiesCount = propertyRepository.countByTenantId(tenantId);
    long usersCount = membershipRepository.findByCompanyId(tenantId).size();

    return SubscriptionResponse.of(saved, newPlan, propertiesCount, usersCount);
  }
}
