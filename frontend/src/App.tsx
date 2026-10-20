import React, { useState } from 'react';
import './App.css';
import { PropertiesPage } from './features/properties/PropertiesPage';
import { CrmView } from './features/crm/CrmView';

interface ModuleCard {
  id: string;
  name: string;
  category: 'core' | 'business' | 'ops';
  description: string;
  status: 'scaffolded' | 'ready' | 'planned';
  apiPath: string;
  icon: string;
}

const MODULES: ModuleCard[] = [
  {
    id: 'identity',
    name: 'Identity & Contas',
    category: 'core',
    description: 'Gestão de usuários, convites para corretores e autenticação segura.',
    status: 'ready',
    apiPath: '/api/v1/users',
    icon: 'ID',
  },
  {
    id: 'tenancy',
    name: 'Multi-tenancy & Empresas',
    category: 'core',
    description: 'Isolamento de dados por imobiliária em nível de aplicação e banco.',
    status: 'ready',
    apiPath: '/api/v1/companies/current',
    icon: 'TEN',
  },
  {
    id: 'properties',
    name: 'Gestão de Imóveis',
    category: 'business',
    description: 'Cadastro detalhado, precificação, características e galeria de fotos.',
    status: 'ready',
    apiPath: '/api/v1/properties',
    icon: 'IMO',
  },
  {
    id: 'crm',
    name: 'CRM & Funil Imobiliário',
    category: 'business',
    description: 'Contatos, leads, funil comercial, atividades e visitas.',
    status: 'ready',
    apiPath: '/api/v1/leads',
    icon: 'CRM',
  },
  {
    id: 'listings',
    name: 'Divulgação Pública',
    category: 'business',
    description: 'Catálogo aberto para clientes, SEO otimizado e captura de leads.',
    status: 'scaffolded',
    apiPath: '/api/v1/public/listings',
    icon: 'PUB',
  },
  {
    id: 'subscriptions',
    name: 'Planos & Assinaturas',
    category: 'ops',
    description: 'Cobrança recorrente, controle de limites e webhooks de pagamento.',
    status: 'scaffolded',
    apiPath: '/api/v1/subscription',
    icon: 'SUB',
  },
  {
    id: 'audit',
    name: 'Trilha de Auditoria',
    category: 'ops',
    description: 'Log imutável de eventos sensíveis para conformidade e segurança.',
    status: 'scaffolded',
    apiPath: '/api/v1/audit/events',
    icon: 'AUD',
  },
  {
    id: 'platformadmin',
    name: 'Administração da Plataforma',
    category: 'ops',
    description: 'Painel do operador do SaaS para suporte e métricas globais.',
    status: 'scaffolded',
    apiPath: '/api/v1/platform/tenants',
    icon: 'ADM',
  },
];

export const App: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'properties' | 'crm' | 'architecture'>('properties');
  const [filter, setFilter] = useState<'all' | 'core' | 'business' | 'ops'>('all');

  const filteredModules = MODULES.filter(
    (m) => filter === 'all' || m.category === filter
  );

  return (
    <div className="app-layout">
      {/* Top Navigation */}
      <header className="app-header">
        <div className="container header-container">
          <div className="brand" onClick={() => setActiveTab('properties')} style={{ cursor: 'pointer' }}>
            <div className="brand-logo">IX</div>
            <div className="brand-info">
              <span className="brand-name">Imovix</span>
              <span className="brand-badge">SaaS Imobiliário</span>
            </div>
          </div>

          <nav className="header-nav-tabs" style={{ display: 'flex', gap: '0.5rem' }}>
            <button
              className={`btn ${activeTab === 'properties' ? 'btn-primary' : 'btn-secondary'}`}
              style={{ padding: '0.5rem 1rem', fontSize: '0.85rem' }}
              onClick={() => setActiveTab('properties')}
            >
              Gestão de Imóveis (CRUD)
            </button>
            <button
              className={`btn ${activeTab === 'crm' ? 'btn-primary' : 'btn-secondary'}`}
              style={{ padding: '0.5rem 1rem', fontSize: '0.85rem' }}
              onClick={() => setActiveTab('crm')}
            >
              CRM &amp; Funil de Vendas
            </button>
            <button
              className={`btn ${activeTab === 'architecture' ? 'btn-primary' : 'btn-secondary'}`}
              style={{ padding: '0.5rem 1rem', fontSize: '0.85rem' }}
              onClick={() => setActiveTab('architecture')}
            >
              Módulos da Arquitetura
            </button>
          </nav>

          <div className="header-status">
            <span className="status-indicator"></span>
            <span className="status-text">Backend: Java 25 LTS • Spring Boot 4.1.1</span>
          </div>

          <div className="header-actions">
            <a
              href="https://github.com/jlopes07/Imovix"
              target="_blank"
              rel="noreferrer"
              className="btn btn-secondary"
            >
              Repositório
            </a>
          </div>
        </div>
      </header>

      {/* Main Content Area */}
      {activeTab === 'properties' ? (
        <main className="container main-content" style={{ paddingTop: '1.5rem' }}>
          <div style={{ marginBottom: '1.5rem' }}>
            <h1 className="section-title" style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>
              Catálogo e Gestão de Imóveis
            </h1>
            <p className="section-description">
              CRUD completo conectado à API REST com isolamento de dados multi-tenant, validação e persistência PostgreSQL.
            </p>
          </div>
          <PropertiesPage />
        </main>
      ) : activeTab === 'crm' ? (
        <main className="container main-content" style={{ paddingTop: '1.5rem' }}>
          <CrmView />
        </main>
      ) : (
        <>
          {/* Hero Section */}
          <section className="hero-section">
            <div className="container">
              <div className="hero-badge-wrapper">
                <span className="badge badge-emerald">Fase 1: Fundação Técnica & Arquitetura</span>
              </div>
              <h1 className="hero-title">
                Plataforma de Gestão Imobiliária <br />
                <span className="gradient-text">Moderna, Modular & Multi-tenant</span>
              </h1>
              <p className="hero-subtitle">
                Estrutura desacoplada monólito modular no backend (Java 25, Spring Boot, PostgreSQL 18, Flyway, Docker)
                e frontend reativo e tipado em React com TypeScript.
              </p>

              <div className="tech-stack-row">
                <div className="tech-pill">Java 25 (LTS)</div>
                <div className="tech-pill">Spring Boot 4.1.1</div>
                <div className="tech-pill">PostgreSQL 18</div>
                <div className="tech-pill">Docker Compose</div>
                <div className="tech-pill">React 19 + TypeScript</div>
                <div className="tech-pill">Vite</div>
              </div>
            </div>
          </section>

          {/* Modules Overview */}
          <main className="container main-content">
            <div className="section-header">
              <div>
                <h2 className="section-title">Módulos da Arquitetura</h2>
                <p className="section-description">
                  Domínios isolados com fronteiras estritas validadas por testes arquiteturais (ArchUnit).
                </p>
              </div>

              <div className="filter-tabs">
                <button
                  className={`filter-btn ${filter === 'all' ? 'active' : ''}`}
                  onClick={() => setFilter('all')}
                >
                  Todos ({MODULES.length})
                </button>
                <button
                  className={`filter-btn ${filter === 'core' ? 'active' : ''}`}
                  onClick={() => setFilter('core')}
                >
                  Núcleo & Tenant
                </button>
                <button
                  className={`filter-btn ${filter === 'business' ? 'active' : ''}`}
                  onClick={() => setFilter('business')}
                >
                  Negócio
                </button>
                <button
                  className={`filter-btn ${filter === 'ops' ? 'active' : ''}`}
                  onClick={() => setFilter('ops')}
                >
                  Operações & SaaS
                </button>
              </div>
            </div>

            <div className="modules-grid">
              {filteredModules.map((m) => (
                <div key={m.id} className="glass-card module-card">
                  <div className="module-top">
                    <span className="module-icon" style={{ fontSize: '0.85rem', fontWeight: 700, color: '#38bdf8' }}>
                      [{m.icon}]
                    </span>
                    <span className={`badge ${m.status === 'ready' ? 'badge-emerald' : m.status === 'scaffolded' ? 'badge-blue' : 'badge-amber'}`}>
                      {m.status === 'ready' ? 'Ativo' : m.status === 'scaffolded' ? 'Estruturado' : 'Planejado'}
                    </span>
                  </div>
                  <h3 className="module-title">{m.name}</h3>
                  <p className="module-desc">{m.description}</p>
                  <div className="module-footer">
                    <code className="module-endpoint">{m.apiPath}</code>
                  </div>
                </div>
              ))}
            </div>

            {/* Security & Isolation Summary Banner */}
            <div className="glass-card security-banner">
              <div className="security-icon" style={{ fontSize: '0.85rem', fontWeight: 700, color: '#10b981' }}>
                SEC
              </div>
              <div className="security-content">
                <h3 className="security-title">Segurança & Multi-tenancy por Desenho</h3>
                <p className="security-text">
                  Cada requisição resolve o tenant a partir do usuário autenticado. Nenhuma query de leitura ou alteração
                  confia cegamente em <code>tenant_id</code> vindo do cliente. Regras de isolamento são validadas
                  automaticamente em testes unitários e de integração com Testcontainers.
                </p>
              </div>
            </div>
          </main>
        </>
      )}

      {/* Footer */}
      <footer className="app-footer">
        <div className="container footer-content">
          <p>© 2026 Imovix — Arquitetura SaaS Imobiliário em Monólito Modular.</p>
          <p className="footer-meta">LGPD Compliant • OWASP ASVS Aligned</p>
        </div>
      </footer>
    </div>
  );
};

export default App;
