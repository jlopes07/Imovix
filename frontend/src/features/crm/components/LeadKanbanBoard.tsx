import type { Lead, LeadStatus } from '../../../types/crm';
import { CONTACT_TYPE_LABELS, LEAD_SOURCE_LABELS, LEAD_STATUS_LABELS } from '../../../types/crm';

interface LeadKanbanBoardProps {
  leads: Lead[];
  onMoveStatus: (leadId: string, newStatus: LeadStatus) => Promise<void>;
  onEditLead: (lead: Lead) => void;
  onDeleteLead: (leadId: string) => Promise<void>;
  onOpenSchedule: (lead: Lead) => void;
  onOpenHistory: (lead: Lead) => void;
}

const STAGES: LeadStatus[] = [
  'NEW',
  'CONTACTED',
  'QUALIFIED',
  'VISITING',
  'PROPOSAL',
  'WON',
  'LOST',
];

export function LeadKanbanBoard({
  leads,
  onMoveStatus,
  onEditLead,
  onDeleteLead,
  onOpenSchedule,
  onOpenHistory,
}: LeadKanbanBoardProps) {
  const getLeadsForStatus = (status: LeadStatus) => {
    return leads.filter((lead) => lead.status === status);
  };

  const formatCurrency = (val: number | null) => {
    if (val === null || val === undefined) return null;
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      maximumFractionDigits: 0,
    }).format(val);
  };

  const getNextStatus = (current: LeadStatus): LeadStatus | null => {
    const idx = STAGES.indexOf(current);
    if (idx >= 0 && idx < STAGES.length - 2) {
      return STAGES[idx + 1];
    }
    return null;
  };

  const getPrevStatus = (current: LeadStatus): LeadStatus | null => {
    const idx = STAGES.indexOf(current);
    if (idx > 0 && idx <= STAGES.length - 2) {
      return STAGES[idx - 1];
    }
    return null;
  };

  return (
    <div className="crm-kanban-container">
      <div className="crm-kanban-board">
        {STAGES.map((status) => {
          const stageLeads = getLeadsForStatus(status);
          const isClosed = status === 'WON' || status === 'LOST';

          return (
            <div
              key={status}
              className={`kanban-column ${status.toLowerCase()} ${isClosed ? 'closed-column' : ''}`}
            >
              <div className="kanban-column-header">
                <div className="column-title-group">
                  <span className="column-title">{LEAD_STATUS_LABELS[status]}</span>
                  <span className="column-count">{stageLeads.length}</span>
                </div>
              </div>

              <div className="kanban-column-body">
                {stageLeads.length === 0 ? (
                  <div className="empty-column-placeholder">
                    <span>Sem leads nesta etapa</span>
                  </div>
                ) : (
                  stageLeads.map((lead) => {
                    const nextStatus = getNextStatus(lead.status);
                    const prevStatus = getPrevStatus(lead.status);

                    return (
                      <div key={lead.id} className="lead-card">
                        <div className="lead-card-header">
                          <span className="lead-type-tag">
                            {CONTACT_TYPE_LABELS[lead.type] || lead.type}
                          </span>
                          <span className="lead-source-tag">
                            {LEAD_SOURCE_LABELS[lead.source] || lead.source}
                          </span>
                        </div>

                        <h4 className="lead-name">{lead.name}</h4>

                        <div className="lead-contact-info">
                          {lead.phone && <div className="contact-row">Tel: {lead.phone}</div>}
                          {lead.email && <div className="contact-row">Email: {lead.email}</div>}
                        </div>

                        {(lead.budgetMin || lead.budgetMax) && (
                          <div className="lead-budget-info">
                            Orcamento:{' '}
                            {lead.budgetMax
                              ? `Ate ${formatCurrency(lead.budgetMax)}`
                              : `A partir de ${formatCurrency(lead.budgetMin)}`}
                          </div>
                        )}

                        {lead.notes && (
                          <p className="lead-notes-preview" title={lead.notes}>
                            {lead.notes}
                          </p>
                        )}

                        {/* Acoes do Lead */}
                        <div className="lead-card-actions">
                          <div className="action-buttons-group">
                            <button
                              type="button"
                              className="btn btn-xs btn-primary-outline"
                              onClick={() => onOpenSchedule(lead)}
                              title="Agendar visita ou tarefa"
                            >
                              + Visita/Tarefa
                            </button>
                            <button
                              type="button"
                              className="btn btn-xs btn-secondary"
                              onClick={() => onOpenHistory(lead)}
                              title="Ver historico de visitas e notas"
                            >
                              Historico
                            </button>
                            <button
                              type="button"
                              className="btn btn-xs btn-outline"
                              onClick={() => onEditLead(lead)}
                              title="Editar informacoes do contato"
                            >
                              Editar
                            </button>
                            <button
                              type="button"
                              className="btn btn-xs btn-danger-outline"
                              onClick={() => onDeleteLead(lead.id)}
                              title="Excluir contato"
                            >
                              Excluir
                            </button>
                          </div>

                          {/* Movimentacao rapida no funil */}
                          <div className="stage-navigation-group">
                            {prevStatus && (
                              <button
                                type="button"
                                className="btn btn-xs btn-nav-stage"
                                onClick={() => onMoveStatus(lead.id, prevStatus)}
                                title={`Voltar para ${LEAD_STATUS_LABELS[prevStatus]}`}
                              >
                                &lt; Voltar
                              </button>
                            )}

                            {lead.status !== 'WON' && lead.status !== 'LOST' && (
                              <button
                                type="button"
                                className="btn btn-xs btn-won-stage"
                                onClick={() => onMoveStatus(lead.id, 'WON')}
                                title="Fechar Negocio"
                              >
                                Fechar
                              </button>
                            )}

                            {nextStatus && (
                              <button
                                type="button"
                                className="btn btn-xs btn-nav-stage next"
                                onClick={() => onMoveStatus(lead.id, nextStatus)}
                                title={`Avancar para ${LEAD_STATUS_LABELS[nextStatus]}`}
                              >
                                Avancar &gt;
                              </button>
                            )}
                          </div>
                        </div>
                      </div>
                    );
                  })
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
