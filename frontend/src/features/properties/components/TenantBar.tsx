import React from 'react';
import type { User, Company } from '../../../types/tenant';

interface TenantBarProps {
  user: User | null;
  company: Company | null;
  onRefreshSession: () => void;
}

export const TenantBar: React.FC<TenantBarProps> = ({ user, company, onRefreshSession }) => {
  return (
    <aside className="tenant-bar" aria-label="Informações da empresa e sessão ativa">
      <div className="tenant-info-group">
        <div className="tenant-pill">
          <span className="indicator-dot" title="Sessão ativa e isolamento multi-tenant garantido"></span>
          <span>Imobiliária:</span>
          <strong>{company ? company.name : 'Carregando imobiliária...'}</strong>
          {company?.creci && <span className="card-code-tag">CRECI {company.creci}</span>}
        </div>

        <div className="tenant-pill">
          <span>Usuário:</span>
          <strong>{user ? user.name : 'Autenticando...'}</strong>
          {user?.email && <small>({user.email})</small>}
        </div>

        <div className="tenant-pill">
          <span>Tenant ID:</span>
          <code style={{ fontSize: '0.75rem', color: '#38bdf8' }}>
            {company ? company.id.substring(0, 8) + '...' : '—'}
          </code>
        </div>
      </div>

      <div className="tenant-actions">
        <button
          className="btn btn-secondary"
          style={{ padding: '0.4rem 0.85rem', fontSize: '0.8rem' }}
          onClick={onRefreshSession}
          title="Alternar ou redefinir a empresa e usuário ativos"
        >
          🔄 Alternar Empresa / Sessão
        </button>
      </div>
    </aside>
  );
};
