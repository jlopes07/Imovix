-- Migração V5: Tabelas de Planos, Limites e Assinaturas Multi-tenant

CREATE TABLE IF NOT EXISTS imovix.plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    billing_cycle VARCHAR(50) NOT NULL DEFAULT 'MONTHLY',
    max_properties INT NOT NULL,
    max_users INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS imovix.tenant_subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES imovix.companies(id) ON DELETE CASCADE,
    plan_id UUID NOT NULL REFERENCES imovix.plans(id),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    current_period_start TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    current_period_end TIMESTAMPTZ NOT NULL,
    cancel_at_period_end BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_tenant_subscription UNIQUE (tenant_id)
);

CREATE INDEX IF NOT EXISTS idx_subscriptions_tenant ON imovix.tenant_subscriptions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_plan ON imovix.tenant_subscriptions(plan_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_status ON imovix.tenant_subscriptions(status);

-- Inserção dos planos comerciais padrão do SaaS
INSERT INTO imovix.plans (id, code, name, description, price, billing_cycle, max_properties, max_users, active)
VALUES 
    ('10000000-0000-0000-0000-000000000001', 'BASIC', 'Plano Essencial', 'Ideal para corretores autonomos e imobiliarias iniciantes.', 149.00, 'MONTHLY', 15, 2, true),
    ('10000000-0000-0000-0000-000000000002', 'PRO', 'Plano Profissional', 'Para imobiliarias em crescimento com equipe ativa e alto volume de negocios.', 349.00, 'MONTHLY', 100, 10, true),
    ('10000000-0000-0000-0000-000000000003', 'ENTERPRISE', 'Plano Enterprise', 'Para grandes imobiliarias e redes com multiplos corretores e carteira ilimitada.', 799.00, 'MONTHLY', 1000, 50, true)
ON CONFLICT (code) DO NOTHING;

-- Assinatura ativa inicial para a empresa padrao
INSERT INTO imovix.tenant_subscriptions (id, tenant_id, plan_id, status, current_period_start, current_period_end, cancel_at_period_end)
SELECT 
    '20000000-0000-0000-0000-000000000001',
    c.id,
    '10000000-0000-0000-0000-000000000002',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP + INTERVAL '30 days',
    false
FROM imovix.companies c
WHERE c.id = '00000000-0000-0000-0000-000000000001'
ON CONFLICT (tenant_id) DO NOTHING;
