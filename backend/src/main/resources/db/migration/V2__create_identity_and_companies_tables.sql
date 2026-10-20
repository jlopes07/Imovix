-- ==============================================================================
-- Imovix Database Migration: V2__create_identity_and_companies_tables.sql
-- Módulos de Identidade, Empresas e Associação de Membros
-- ==============================================================================

-- 1. Tabela de Usuários (Identidade)
CREATE TABLE IF NOT EXISTS imovix.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX IF NOT EXISTS idx_users_email ON imovix.users (LOWER(email));
CREATE INDEX IF NOT EXISTS idx_users_status ON imovix.users (status);

-- 2. Tabela de Empresas / Imobiliárias
CREATE TABLE IF NOT EXISTS imovix.companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    trade_name VARCHAR(255),
    document_number VARCHAR(32),
    creci VARCHAR(50),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_companies_document_number ON imovix.companies (document_number);
CREATE INDEX IF NOT EXISTS idx_companies_status ON imovix.companies (status);

-- 3. Tabela de Associação de Membros (Company Memberships)
CREATE TABLE IF NOT EXISTS imovix.company_memberships (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_memberships_company FOREIGN KEY (company_id)
        REFERENCES imovix.companies (id) ON DELETE RESTRICT,
    CONSTRAINT fk_memberships_user FOREIGN KEY (user_id)
        REFERENCES imovix.users (id) ON DELETE RESTRICT,
    CONSTRAINT uq_company_membership_company_user UNIQUE (company_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_memberships_company_id ON imovix.company_memberships (company_id);
CREATE INDEX IF NOT EXISTS idx_memberships_user_id ON imovix.company_memberships (user_id);
CREATE INDEX IF NOT EXISTS idx_memberships_role ON imovix.company_memberships (role);
