-- ==============================================================================
-- Imovix Database Migration: V3__create_properties_tables.sql
-- Módulo de Cadastro e Gestão de Imóveis (Properties) com Isolamento Multi-Tenant
-- ==============================================================================

CREATE TABLE IF NOT EXISTS imovix.properties (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    code VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    purpose VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',

    -- Valores monetários em NUMERIC(14,2)
    sale_price NUMERIC(14,2),
    rental_price NUMERIC(14,2),
    condo_fee NUMERIC(14,2),
    iptu NUMERIC(14,2),

    -- Características físicas e metragens
    bedrooms INT,
    bathrooms INT,
    suites INT,
    parking_spaces INT,
    usable_area NUMERIC(10,2),
    total_area NUMERIC(10,2),

    -- Endereço
    zip_code VARCHAR(10),
    street VARCHAR(255),
    number VARCHAR(30),
    complement VARCHAR(100),
    neighborhood VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(2),

    -- Auditoria e rastreabilidade
    created_by UUID,
    updated_by UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_properties_tenant FOREIGN KEY (tenant_id)
        REFERENCES imovix.companies (id) ON DELETE RESTRICT,
    CONSTRAINT uq_properties_tenant_code UNIQUE (tenant_id, code)
);

-- Índices otimizados para busca com escopo obrigatório de tenant
CREATE INDEX IF NOT EXISTS idx_properties_tenant_id ON imovix.properties (tenant_id);
CREATE INDEX IF NOT EXISTS idx_properties_tenant_status ON imovix.properties (tenant_id, status);
CREATE INDEX IF NOT EXISTS idx_properties_tenant_type ON imovix.properties (tenant_id, type);
CREATE INDEX IF NOT EXISTS idx_properties_tenant_purpose ON imovix.properties (tenant_id, purpose);
CREATE INDEX IF NOT EXISTS idx_properties_tenant_city ON imovix.properties (tenant_id, city);
