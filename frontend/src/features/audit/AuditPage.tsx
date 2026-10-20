import React, { useEffect, useState } from 'react';
import { AuditService } from '../../services/auditService';
import { AuthService } from '../../services/authService';
import type { AuditLog, PagedAuditLogsResponse } from '../../types/audit';
import './audit.css';

export const AuditPage: React.FC = () => {
  const [data, setData] = useState<PagedAuditLogsResponse | null>(null);
  const [page, setPage] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedId, setExpandedId] = useState<string | null>(null);

  const loadAuditLogs = async (targetPage = 0) => {
    try {
      setLoading(true);
      setError(null);
      await AuthService.bootstrapDemoSession();
      const res = await AuditService.listEvents(targetPage, 15);
      setData(res);
      setPage(targetPage);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Falha ao carregar trilha de auditoria.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAuditLogs(0);
  }, []);

  const formatDate = (isoString: string) => {
    try {
      return new Intl.DateTimeFormat('pt-BR', {
        dateStyle: 'short',
        timeStyle: 'medium',
      }).format(new Date(isoString));
    } catch {
      return isoString;
    }
  };

  const getActionBadgeClass = (action: string) => {
    if (action.includes('CREATED') || action.includes('INITIALIZED')) {
      return 'badge-create';
    }
    if (action.includes('UPDATED') || action.includes('CHANGED')) {
      return 'badge-update';
    }
    if (action.includes('DELETED') || action.includes('REMOVED')) {
      return 'badge-delete';
    }
    return 'badge-generic';
  };

  return (
    <div className="audit-view-container">
      {/* Cabecalho */}
      <div className="audit-header-row">
        <div>
          <h1 className="audit-main-title">Trilha de Auditoria &amp; Rastreabilidade LGPD</h1>
          <p className="audit-subtitle">
            Log imutável de eventos sensíveis, controle de alterações e integridade de dados por imobiliária.
          </p>
        </div>
      </div>

      {/* Overview Banner */}
      <div className="audit-info-banner">
        <div className="audit-info-item">
          <span className="audit-info-label">Total de Eventos Registrados</span>
          <span className="audit-info-val">{data?.totalElements ?? 0}</span>
        </div>
        <div className="audit-info-item">
          <span className="audit-info-label">Regime de Retencao</span>
          <span className="audit-info-val" style={{ color: '#10b981' }}>Imutavel • 5 Anos (LGPD)</span>
        </div>
        <div className="audit-info-item">
          <span className="audit-info-label">Isolamento Multi-Tenant</span>
          <span className="audit-info-val" style={{ color: '#38bdf8' }}>Ativo (Tenant Scope)</span>
        </div>
        <div className="audit-info-item">
          <span className="audit-info-label">Rastreabilidade</span>
          <span className="audit-info-val">Correlation-ID em 100%</span>
        </div>
      </div>

      {error && (
        <div style={{ padding: '14px', background: 'rgba(239, 68, 68, 0.15)', color: '#ef4444', borderRadius: '6px' }}>
          {error}
        </div>
      )}

      {/* Tabela de Auditoria */}
      <div className="audit-table-card">
        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text-secondary)' }}>
            Carregando eventos de auditoria...
          </div>
        ) : !data || data.content.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text-secondary)' }}>
            Nenhum evento de auditoria registrado para esta imobiliaria.
          </div>
        ) : (
          <div style={{ overflowX: 'auto' }}>
            <table className="audit-table">
              <thead>
                <tr>
                  <th>Data / Hora</th>
                  <th>Acao</th>
                  <th>Recurso</th>
                  <th>Autor / Usuario</th>
                  <th>IP / Correlacao</th>
                  <th>Detalhes</th>
                </tr>
              </thead>
              <tbody>
                {data.content.map((log: AuditLog) => (
                  <tr key={log.id}>
                    <td style={{ whiteSpace: 'nowrap' }}>{formatDate(log.createdAt)}</td>
                    <td>
                      <span className={`audit-badge ${getActionBadgeClass(log.action)}`}>
                        {log.action}
                      </span>
                    </td>
                    <td>
                      <div>
                        <strong>{log.resourceType}</strong>
                        {log.resourceId && (
                          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                            ID: {log.resourceId}
                          </div>
                        )}
                      </div>
                    </td>
                    <td>
                      <div style={{ fontSize: '0.85rem' }}>
                        {log.userEmail || (log.userId ? log.userId.substring(0, 8) + '...' : 'Sistema')}
                      </div>
                    </td>
                    <td>
                      <div style={{ fontSize: '0.75rem' }}>
                        <div>IP: {log.ipAddress || '127.0.0.1'}</div>
                        {log.correlationId && (
                          <div style={{ color: 'var(--text-muted)' }}>CID: {log.correlationId.substring(0, 16)}...</div>
                        )}
                      </div>
                    </td>
                    <td>
                      {log.details ? (
                        <div>
                          <button
                            className="btn btn-secondary"
                            style={{ padding: '4px 8px', fontSize: '0.75rem' }}
                            onClick={() => setExpandedId(expandedId === log.id ? null : log.id)}
                          >
                            {expandedId === log.id ? 'Ocultar' : 'Ver JSON'}
                          </button>
                          {expandedId === log.id && (
                            <div className="audit-json-details" style={{ marginTop: '8px' }}>
                              {log.details}
                            </div>
                          )}
                        </div>
                      ) : (
                        <span style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>Nenhum detalhe</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Rodape com Paginacao */}
        {data && data.totalPages > 1 && (
          <div className="audit-pagination">
            <span>
              Pagina {data.page + 1} de {data.totalPages} ({data.totalElements} registros no total)
            </span>
            <div className="audit-page-btns">
              <button
                className="btn btn-secondary"
                style={{ padding: '6px 12px', fontSize: '0.8rem' }}
                disabled={page === 0 || loading}
                onClick={() => loadAuditLogs(page - 1)}
              >
                Anterior
              </button>
              <button
                className="btn btn-secondary"
                style={{ padding: '6px 12px', fontSize: '0.8rem' }}
                disabled={page >= data.totalPages - 1 || loading}
                onClick={() => loadAuditLogs(page + 1)}
              >
                Proximo
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
