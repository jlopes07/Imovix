import React, { useEffect, useState } from 'react';
import { SubscriptionService } from '../../services/subscriptionService';
import { AuthService } from '../../services/authService';
import type { Plan, Subscription } from '../../types/subscription';
import './subscriptions.css';

export const SubscriptionPage: React.FC = () => {
  const [subscription, setSubscription] = useState<Subscription | null>(null);
  const [plans, setPlans] = useState<Plan[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [changingPlanId, setChangingPlanId] = useState<string | null>(null);
  const [message, setMessage] = useState<{ text: string; type: 'success' | 'error' } | null>(null);

  const loadData = async () => {
    try {
      setLoading(true);
      await AuthService.bootstrapDemoSession();
      const [subData, plansData] = await Promise.all([
        SubscriptionService.getCurrentSubscription(),
        SubscriptionService.listPlans(),
      ]);
      setSubscription(subData);
      setPlans(plansData);
    } catch (err: unknown) {
      const errorMsg = err instanceof Error ? err.message : 'Falha ao carregar informacoes da assinatura.';
      setMessage({ text: errorMsg, type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleChangePlan = async (plan: Plan) => {
    if (!subscription || subscription.plan.id === plan.id) {
      return;
    }

    const confirmed = window.confirm(
      `Deseja realmente alterar a assinatura para o ${plan.name} (${formatCurrency(plan.price)}/mes)?`
    );
    if (!confirmed) {
      return;
    }

    try {
      setChangingPlanId(plan.id);
      setMessage(null);
      const updated = await SubscriptionService.changePlan(plan.id);
      setSubscription(updated);
      setMessage({
        text: `Assinatura atualizada com sucesso para o ${plan.name}! Novos limites já estao em vigor.`,
        type: 'success',
      });
    } catch (err: unknown) {
      const errorMsg = err instanceof Error ? err.message : 'Nao foi possivel alterar o plano contratado.';
      setMessage({ text: errorMsg, type: 'error' });
    } finally {
      setChangingPlanId(null);
    }
  };

  const formatCurrency = (val: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    }).format(val);
  };

  const getMeterColorClass = (percent: number) => {
    if (percent >= 100) return 'fill-danger';
    if (percent >= 80) return 'fill-warning';
    return 'fill-safe';
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return { label: 'Ativa', className: 'status-active' };
      case 'TRIALING':
        return { label: 'Periodo de Testes', className: 'status-trial' };
      case 'PAST_DUE':
        return { label: 'Pagamento Pendente', className: 'status-warning' };
      case 'CANCELED':
        return { label: 'Cancelada', className: 'status-canceled' };
      default:
        return { label: status, className: 'status-active' };
    }
  };

  if (loading) {
    return (
      <div className="subscriptions-container">
        <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text-secondary)' }}>
          Carregando dados da assinatura e planos disponiveis...
        </div>
      </div>
    );
  }

  const statusBadge = subscription ? getStatusLabel(subscription.status) : { label: 'N/A', className: '' };

  const propertiesUsagePercent =
    subscription && subscription.maxProperties > 0
      ? (subscription.currentPropertiesCount / subscription.maxProperties) * 100
      : 0;

  const usersUsagePercent =
    subscription && subscription.maxUsers > 0
      ? (subscription.currentUsersCount / subscription.maxUsers) * 100
      : 0;

  const hasCustomDomain =
    subscription?.plan.code === 'PRO' || subscription?.plan.code === 'ENTERPRISE';
  const hasPortalIntegration =
    subscription?.plan.code === 'PRO' || subscription?.plan.code === 'ENTERPRISE';

  return (
    <div className="subscriptions-container">
      {/* Cabecalho do Modulo */}
      <div className="sub-header-row">
        <div>
          <h1 className="sub-main-title">Planos, Assinaturas e Limites SaaS</h1>
          <p className="sub-subtitle">
            Gerenciamento transparente de cotas de recursos, capacidade de imoveis e faturamento da imobiliaria.
          </p>
        </div>
      </div>

      {/* Alerta de Feedback */}
      {message && (
        <div className={`sub-alert-box ${message.type === 'success' ? 'sub-alert-success' : 'sub-alert-error'}`}>
          <span>{message.text}</span>
          <button className="sub-alert-close" onClick={() => setMessage(null)}>
            x
          </button>
        </div>
      )}

      {/* Card da Assinatura Atual */}
      {subscription && (
        <div className="sub-current-card">
          <div className="sub-current-header">
            <div>
              <div className="sub-plan-badge-group">
                <h2>{subscription.plan.name}</h2>
                <span className={`sub-badge ${statusBadge.className}`}>
                  {statusBadge.label}
                </span>
              </div>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.85rem', margin: '6px 0 0 0' }}>
                ID do Tenant: <code style={{ color: 'var(--accent-primary)' }}>{subscription.tenantId}</code>
              </p>
            </div>

            <div className="sub-billing-details">
              <div className="sub-price-current">
                {formatCurrency(subscription.plan.price)} <span>/ mes</span>
              </div>
              <div className="sub-renewal-date">
                Renovacao automatica: {!subscription.cancelAtPeriodEnd ? 'Ativada' : 'Desativada'}
              </div>
            </div>
          </div>

          {/* Medidores de Cota e Consumo */}
          <div className="sub-meters-grid">
            {/* Medidor de Imoveis */}
            <div className="sub-meter-box">
              <div className="sub-meter-top">
                <span className="sub-meter-label">Capacidade de Imóveis</span>
                <span className="sub-meter-value">
                  {subscription.currentPropertiesCount} /{' '}
                  {subscription.maxProperties === 2147483647 ? 'Ilimitado' : subscription.maxProperties}
                </span>
              </div>
              <div className="sub-progress-bar-bg">
                <div
                  className={`sub-progress-bar-fill ${getMeterColorClass(propertiesUsagePercent)}`}
                  style={{ width: `${Math.min(propertiesUsagePercent, 100)}%` }}
                />
              </div>
              <div className="sub-meter-footer">
                <span>{propertiesUsagePercent.toFixed(1)}% utilizado</span>
                <span>
                  {subscription.maxProperties === 2147483647
                    ? 'Sem limite de cadastro'
                    : `${subscription.maxProperties - subscription.currentPropertiesCount} vagas restantes`}
                </span>
              </div>
            </div>

            {/* Medidor de Usuarios da Equipe */}
            <div className="sub-meter-box">
              <div className="sub-meter-top">
                <span className="sub-meter-label">Usuários da Equipe</span>
                <span className="sub-meter-value">
                  {subscription.currentUsersCount} /{' '}
                  {subscription.maxUsers === 2147483647 ? 'Ilimitado' : subscription.maxUsers}
                </span>
              </div>
              <div className="sub-progress-bar-bg">
                <div
                  className={`sub-progress-bar-fill ${getMeterColorClass(usersUsagePercent)}`}
                  style={{ width: `${Math.min(usersUsagePercent, 100)}%` }}
                />
              </div>
              <div className="sub-meter-footer">
                <span>{usersUsagePercent.toFixed(1)}% utilizado</span>
                <span>
                  {subscription.maxUsers === 2147483647
                    ? 'Equipe sem restricoes'
                    : `${subscription.maxUsers - subscription.currentUsersCount} convites disponiveis`}
                </span>
              </div>
            </div>
          </div>

          {/* Recursos Ativos na Assinatura */}
          <div className="sub-features-row">
            <span className={`sub-feature-pill ${hasCustomDomain ? 'active' : ''}`}>
              [+] Dominio Proprio: {hasCustomDomain ? 'Habilitado' : 'Indisponivel no plano'}
            </span>
            <span className={`sub-feature-pill ${hasPortalIntegration ? 'active' : ''}`}>
              [+] Portais Imobiliarios (Zap/VivaReal): {hasPortalIntegration ? 'Integracao Ativa' : 'Indisponivel no plano'}
            </span>
          </div>
        </div>
      )}

      {/* Grade de Selecao de Planos (Upgrade / Downgrade) */}
      <div className="sub-plans-title-area">
        <h2 className="sub-plans-title">Planos Disponíveis para o seu Negócio</h2>
        <p className="sub-subtitle">
          Escale a capacidade da sua imobiliária com flexibilidade imediata e sem burocracia.
        </p>
      </div>

      <div className="sub-plans-grid">
        {plans.map((plan) => {
          const isCurrent = subscription?.plan.id === plan.id;
          const isPopular = plan.code === 'PRO';
          const isUpgrading = changingPlanId === plan.id;
          const planHasDomain = plan.code === 'PRO' || plan.code === 'ENTERPRISE';
          const planHasPortals = plan.code === 'PRO' || plan.code === 'ENTERPRISE';

          return (
            <div
              key={plan.id}
              className={`sub-plan-card ${isCurrent ? 'current' : ''} ${isPopular ? 'popular' : ''}`}
            >
              {isPopular && <span className="sub-popular-ribbon">Mais Escolhido</span>}

              <div>
                <div className="sub-plan-head">
                  <h3>{plan.name}</h3>
                  <p className="sub-plan-desc">{plan.description}</p>
                </div>

                <div className="sub-plan-price-box">
                  <span className="sub-plan-amount">{formatCurrency(plan.price)}</span>
                  <span className="sub-plan-period">/ mes</span>
                </div>

                <ul className="sub-plan-features-list">
                  <li className="sub-plan-feature-item">
                    <span className="sub-bullet-check">v</span>
                    <span>
                      Ate <strong>{plan.maxProperties === 2147483647 ? 'Imóveis Ilimitados' : `${plan.maxProperties} imóveis`}</strong>
                    </span>
                  </li>
                  <li className="sub-plan-feature-item">
                    <span className="sub-bullet-check">v</span>
                    <span>
                      Ate <strong>{plan.maxUsers === 2147483647 ? 'Usuários Ilimitados' : `${plan.maxUsers} usuários`}</strong> na equipe
                    </span>
                  </li>
                  <li className="sub-plan-feature-item">
                    {planHasDomain ? (
                      <span className="sub-bullet-check">v</span>
                    ) : (
                      <span className="sub-bullet-cross">-</span>
                    )}
                    <span>Dominio personalizado (portal proprio)</span>
                  </li>
                  <li className="sub-plan-feature-item">
                    {planHasPortals ? (
                      <span className="sub-bullet-check">v</span>
                    ) : (
                      <span className="sub-bullet-cross">-</span>
                    )}
                    <span>Integracao automatica com portais (Zap/VivaReal)</span>
                  </li>
                </ul>
              </div>

              <div>
                {isCurrent ? (
                  <button className="sub-plan-btn sub-plan-btn-current" disabled>
                    Plano Atual
                  </button>
                ) : (
                  <button
                    className={`sub-plan-btn ${isPopular ? 'sub-plan-btn-upgrade' : 'sub-plan-btn-secondary'}`}
                    disabled={changingPlanId !== null}
                    onClick={() => handleChangePlan(plan)}
                  >
                    {isUpgrading ? 'Alterando plano...' : 'Alterar para este Plano'}
                  </button>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
