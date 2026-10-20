package br.com.imovix.subscriptions.application;

import br.com.imovix.companies.domain.CompanyMembershipRepository;
import br.com.imovix.properties.domain.PropertyRepository;
import br.com.imovix.subscriptions.domain.Plan;
import br.com.imovix.subscriptions.domain.PlanRepository;
import br.com.imovix.subscriptions.domain.SubscriptionNotFoundException;
import br.com.imovix.subscriptions.domain.SubscriptionRepository;
import br.com.imovix.subscriptions.domain.SubscriptionStatus;
import br.com.imovix.subscriptions.domain.TenantSubscription;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para obter a assinatura ativa e as metricas de uso do tenant informado. */
@Service
public class GetCurrentSubscriptionUseCase {

  private final SubscriptionRepository subscriptionRepository;
  private final PlanRepository planRepository;
  private final PropertyRepository propertyRepository;
  private final CompanyMembershipRepository membershipRepository;

  public GetCurrentSubscriptionUseCase(
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
  public SubscriptionResponse execute(UUID tenantId) {
    TenantSubscription subscription =
        subscriptionRepository
            .findByTenantId(tenantId)
            .orElseGet(() -> createDefaultSubscription(tenantId));

    Plan plan =
        planRepository
            .findById(subscription.getPlanId())
            .orElseThrow(() -> new SubscriptionNotFoundException(tenantId));

    long propertiesCount = propertyRepository.countByTenantId(tenantId);
    long usersCount = membershipRepository.findByCompanyId(tenantId).size();

    return SubscriptionResponse.of(subscription, plan, propertiesCount, usersCount);
  }

  private TenantSubscription createDefaultSubscription(UUID tenantId) {
    Plan defaultPlan =
        planRepository
            .findByCode("PRO")
            .or(() -> planRepository.findAllActive().stream().findFirst())
            .orElseThrow(() -> new IllegalStateException("Nenhum plano configurado no sistema."));

    TenantSubscription newSub =
        new TenantSubscription(
            null,
            tenantId,
            defaultPlan.getId(),
            SubscriptionStatus.ACTIVE,
            Instant.now(),
            Instant.now().plusSeconds(86400 * 30),
            false,
            null,
            null);

    return subscriptionRepository.save(newSub);
  }
}
