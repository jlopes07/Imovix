-- Migração V4: Criação das tabelas do CRM (Contatos, Leads, Funil, Atividades e Visitas)

CREATE TABLE IF NOT EXISTS imovix.contacts_leads (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES imovix.companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    source VARCHAR(50) NOT NULL,
    assigned_user_id UUID REFERENCES imovix.users(id) ON DELETE SET NULL,
    interested_property_id UUID REFERENCES imovix.properties(id) ON DELETE SET NULL,
    budget_min NUMERIC(15, 2),
    budget_max NUMERIC(15, 2),
    notes TEXT,
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_leads_tenant ON imovix.contacts_leads(tenant_id);
CREATE INDEX IF NOT EXISTS idx_leads_tenant_status ON imovix.contacts_leads(tenant_id, status);
CREATE INDEX IF NOT EXISTS idx_leads_tenant_assigned ON imovix.contacts_leads(tenant_id, assigned_user_id);
CREATE INDEX IF NOT EXISTS idx_leads_tenant_property ON imovix.contacts_leads(tenant_id, interested_property_id);

CREATE TABLE IF NOT EXISTS imovix.crm_activities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL REFERENCES imovix.companies(id) ON DELETE CASCADE,
    lead_id UUID NOT NULL REFERENCES imovix.contacts_leads(id) ON DELETE CASCADE,
    property_id UUID REFERENCES imovix.properties(id) ON DELETE SET NULL,
    user_id UUID REFERENCES imovix.users(id) ON DELETE SET NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    scheduled_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ,
    feedback TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_activities_tenant ON imovix.crm_activities(tenant_id);
CREATE INDEX IF NOT EXISTS idx_activities_tenant_lead ON imovix.crm_activities(tenant_id, lead_id);
CREATE INDEX IF NOT EXISTS idx_activities_tenant_scheduled ON imovix.crm_activities(tenant_id, scheduled_at);
