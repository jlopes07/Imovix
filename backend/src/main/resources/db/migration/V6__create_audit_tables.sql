-- Migration V6: Criacao da tabela de trilha de auditoria (audit_events)
-- Em conformidade com os requisitos de rastreabilidade e LGPD.

CREATE TABLE IF NOT EXISTS imovix.audit_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    user_id UUID,
    user_email VARCHAR(180),
    action VARCHAR(80) NOT NULL,
    resource_type VARCHAR(60) NOT NULL,
    resource_id VARCHAR(100),
    details JSONB,
    ip_address VARCHAR(45),
    correlation_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_audit_tenant_created 
    ON imovix.audit_events (tenant_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_audit_resource 
    ON imovix.audit_events (tenant_id, resource_type, created_at DESC);

-- Registro de auditoria inicial do tenant padrao
INSERT INTO imovix.audit_events (
    id,
    tenant_id,
    user_id,
    user_email,
    action,
    resource_type,
    resource_id,
    details,
    ip_address,
    correlation_id,
    created_at
) VALUES (
    '50000000-0000-0000-0000-000000000001',
    '11111111-1111-1111-1111-111111111111',
    'a0000000-0000-0000-0000-000000000001',
    'admin@imovix.com.br',
    'TENANT_INITIALIZED',
    'COMPANY',
    '11111111-1111-1111-1111-111111111111',
    '{"message": "Tenant padrao inicializado com Plano Profissional"}'::jsonb,
    '127.0.0.1',
    'system-bootstrap-init',
    NOW() - INTERVAL '1 day'
) ON CONFLICT (id) DO NOTHING;
