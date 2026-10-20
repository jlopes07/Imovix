import { useState, useEffect, useCallback } from 'react';
import './crm.css';
import type { Activity, CreateLeadPayload, Lead, LeadStatus } from '../../types/crm';
import { CrmService } from '../../services/crmService';
import { LeadKanbanBoard } from './components/LeadKanbanBoard';
import { LeadModal } from './components/LeadModal';
import { ActivityModal } from './components/ActivityModal';
import { ActivityHistoryModal } from './components/ActivityHistoryModal';

export function CrmView() {
  const [leads, setLeads] = useState<Lead[]>([]);
  const [upcomingActivities, setUpcomingActivities] = useState<Activity[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Modais
  const [isLeadModalOpen, setIsLeadModalOpen] = useState(false);
  const [editingLead, setEditingLead] = useState<Lead | null>(null);

  const [schedulingLead, setSchedulingLead] = useState<Lead | null>(null);
  const [historyLead, setHistoryLead] = useState<Lead | null>(null);

  const loadData = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      const [leadsRes, activitiesRes] = await Promise.all([
        CrmService.listLeads(undefined, 0, 100),
        CrmService.listUpcomingActivities(14),
      ]);
      setLeads(leadsRes.content);
      setUpcomingActivities(activitiesRes);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Erro ao carregar dados do CRM.');
      }
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  // Handlers
  const handleSaveLead = async (payload: CreateLeadPayload) => {
    if (editingLead) {
      await CrmService.updateLead(editingLead.id, payload);
    } else {
      await CrmService.createLead(payload);
    }
    await loadData();
  };

  const handleMoveStatus = async (leadId: string, newStatus: LeadStatus) => {
    try {
      await CrmService.updateLeadStatus(leadId, newStatus);
      await loadData();
    } catch (err: unknown) {
      if (err instanceof Error) {
        alert(err.message);
      }
    }
  };

  const handleDeleteLead = async (leadId: string) => {
    if (!window.confirm('Tem certeza que deseja remover este lead/contato?')) return;
    try {
      await CrmService.deleteLead(leadId);
      await loadData();
    } catch (err: unknown) {
      if (err instanceof Error) {
        alert(err.message);
      }
    }
  };

  // Filtragem local por termo de busca
  const filteredLeads = leads.filter((lead) => {
    if (!searchTerm.trim()) return true;
    const term = searchTerm.toLowerCase();
    return (
      lead.name.toLowerCase().includes(term) ||
      (lead.email && lead.email.toLowerCase().includes(term)) ||
      (lead.phone && lead.phone.includes(term)) ||
      (lead.notes && lead.notes.toLowerCase().includes(term))
    );
  });

  // Métricas
  const totalLeads = leads.length;
  const inVisiting = leads.filter((l) => l.status === 'VISITING').length;
  const inProposal = leads.filter((l) => l.status === 'PROPOSAL').length;
  const wonCount = leads.filter((l) => l.status === 'WON').length;

  return (
    <div className="crm-view-container">
      {/* Top Header */}
      <div className="crm-header-row">
        <div>
          <h2 className="crm-main-title">CRM &amp; Funil de Vendas Imobiliario</h2>
          <p className="crm-subtitle">
            Gestao de contatos, acompanhamento do ciclo de atendimento e agendamento de visitas
          </p>
        </div>

        <button
          type="button"
          className="btn btn-primary"
          onClick={() => {
            setEditingLead(null);
            setIsLeadModalOpen(true);
          }}
        >
          + Novo Lead / Contato
        </button>
      </div>

      {/* Cards de Métricas */}
      <div className="crm-metrics-grid">
        <div className="crm-metric-card">
          <span className="metric-label">Total de Leads</span>
          <span className="metric-number">{totalLeads}</span>
        </div>
        <div className="crm-metric-card accent-blue">
          <span className="metric-label">Em Visitas</span>
          <span className="metric-number">{inVisiting}</span>
        </div>
        <div className="crm-metric-card accent-purple">
          <span className="metric-label">Em Proposta</span>
          <span className="metric-number">{inProposal}</span>
        </div>
        <div className="crm-metric-card accent-green">
          <span className="metric-label">Negocios Fechados</span>
          <span className="metric-number">{wonCount}</span>
        </div>
      </div>

      {/* Próximas Visitas / Atividades dos Próximos 14 dias */}
      {upcomingActivities.length > 0 && (
        <div className="upcoming-activities-panel">
          <div className="panel-header">
            <h3>Proximas Visitas e Tarefas Agendadas (Proximos 14 dias)</h3>
            <span className="badge-count">{upcomingActivities.length} agendadas</span>
          </div>
          <div className="upcoming-activities-grid">
            {upcomingActivities.slice(0, 4).map((act) => (
              <div key={act.id} className="upcoming-item-card">
                <div className="upcoming-type">{act.type}</div>
                <div className="upcoming-title">{act.title}</div>
                <div className="upcoming-date">
                  {new Date(act.scheduledAt).toLocaleString('pt-BR', {
                    day: '2-digit',
                    month: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit',
                  })}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Barra de Busca e Filtros */}
      <div className="crm-toolbar">
        <div className="search-box">
          <input
            type="text"
            placeholder="Buscar por nome, telefone, email ou notas..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {error && <div className="alert-box error-alert">{error}</div>}

      {/* Quadro Kanban do Funil */}
      {isLoading ? (
        <div className="loading-state">Carregando funil comercial...</div>
      ) : (
        <LeadKanbanBoard
          leads={filteredLeads}
          onMoveStatus={handleMoveStatus}
          onEditLead={(lead) => {
            setEditingLead(lead);
            setIsLeadModalOpen(true);
          }}
          onDeleteLead={handleDeleteLead}
          onOpenSchedule={(lead) => setSchedulingLead(lead)}
          onOpenHistory={(lead) => setHistoryLead(lead)}
        />
      )}

      {/* Modais */}
      {isLeadModalOpen && (
        <LeadModal
          isOpen={isLeadModalOpen}
          onClose={() => {
            setIsLeadModalOpen(false);
            setEditingLead(null);
          }}
          onSave={handleSaveLead}
          leadToEdit={editingLead}
        />
      )}

      {schedulingLead && (
        <ActivityModal
          isOpen={Boolean(schedulingLead)}
          onClose={() => setSchedulingLead(null)}
          leadId={schedulingLead.id}
          leadName={schedulingLead.name}
          onSchedule={async (payload) => {
            await CrmService.scheduleActivity(payload);
            await loadData();
          }}
        />
      )}

      {historyLead && (
        <ActivityHistoryModal
          isOpen={Boolean(historyLead)}
          onClose={() => setHistoryLead(null)}
          leadId={historyLead.id}
          leadName={historyLead.name}
        />
      )}
    </div>
  );
}
