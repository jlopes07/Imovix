import { useState, useEffect, useCallback } from 'react';
import type { Activity } from '../../../types/crm';
import { ACTIVITY_TYPE_LABELS } from '../../../types/crm';
import { CrmService } from '../../../services/crmService';

interface ActivityHistoryModalProps {
  isOpen: boolean;
  onClose: () => void;
  leadId: string;
  leadName: string;
}

export function ActivityHistoryModal({
  isOpen,
  onClose,
  leadId,
  leadName,
}: ActivityHistoryModalProps) {
  const [activities, setActivities] = useState<Activity[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Estado para concluir atividade com feedback
  const [completingActivityId, setCompletingActivityId] = useState<string | null>(null);
  const [feedbackText, setFeedbackText] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const loadActivities = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await CrmService.listActivitiesForLead(leadId);
      setActivities(data);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Erro ao carregar historico de atividades.');
      }
    } finally {
      setIsLoading(false);
    }
  }, [leadId]);

  useEffect(() => {
    if (isOpen) {
      loadActivities();
    }
  }, [isOpen, loadActivities]);

  if (!isOpen) return null;

  const handleComplete = async (activityId: string) => {
    try {
      setIsSubmitting(true);
      await CrmService.completeActivity(activityId, {
        feedback: feedbackText.trim() || undefined,
      });
      setCompletingActivityId(null);
      setFeedbackText('');
      await loadActivities();
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (activityId: string) => {
    if (!window.confirm('Deseja realmente remover este agendamento?')) return;
    try {
      await CrmService.deleteActivity(activityId);
      await loadActivities();
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      }
    }
  };

  const formatDate = (isoString: string) => {
    try {
      const date = new Date(isoString);
      return date.toLocaleString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return isoString;
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-container crm-modal history-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Historico de Interacoes e Visitas</h2>
          <button type="button" className="close-btn" onClick={onClose} aria-label="Fechar">
            X
          </button>
        </div>

        <div className="lead-summary-pill">
          <span className="pill-label">Contato:</span>
          <span className="pill-name">{leadName}</span>
        </div>

        {error && <div className="alert-box error-alert">{error}</div>}

        {isLoading ? (
          <div className="loading-state">Carregando atividades...</div>
        ) : activities.length === 0 ? (
          <div className="empty-activities">
            <p>Nenhuma atividade ou visita registrada para este lead.</p>
          </div>
        ) : (
          <div className="activities-list">
            {activities.map((act) => (
              <div key={act.id} className={`activity-card ${act.status.toLowerCase()}`}>
                <div className="activity-header">
                  <div className="activity-type-tag">
                    {ACTIVITY_TYPE_LABELS[act.type] || act.type}
                  </div>
                  <div className={`activity-status-badge ${act.status.toLowerCase()}`}>
                    {act.status === 'COMPLETED'
                      ? 'Concluida'
                      : act.status === 'CANCELLED'
                      ? 'Cancelada'
                      : 'Agendada'}
                  </div>
                </div>

                <h4 className="activity-title">{act.title}</h4>

                <div className="activity-meta">
                  <span>Data/Hora: {formatDate(act.scheduledAt)}</span>
                  {act.completedAt && (
                    <span>Realizada em: {formatDate(act.completedAt)}</span>
                  )}
                </div>

                {act.description && (
                  <p className="activity-desc">{act.description}</p>
                )}

                {act.feedback && (
                  <div className="activity-feedback-box">
                    <strong>Feedback do Atendimento / Visita:</strong>
                    <p>{act.feedback}</p>
                  </div>
                )}

                {completingActivityId === act.id ? (
                  <div className="feedback-form">
                    <label htmlFor={`feedback-${act.id}`}>
                      Feedback da Visita / Atendimento:
                    </label>
                    <textarea
                      id={`feedback-${act.id}`}
                      rows={2}
                      placeholder="Ex: O cliente gostou do imovel, achou o preco justo e ficou de enviar documentos para analise."
                      value={feedbackText}
                      onChange={(e) => setFeedbackText(e.target.value)}
                    />
                    <div className="feedback-form-actions">
                      <button
                        type="button"
                        className="btn btn-sm btn-secondary"
                        onClick={() => setCompletingActivityId(null)}
                        disabled={isSubmitting}
                      >
                        Cancelar
                      </button>
                      <button
                        type="button"
                        className="btn btn-sm btn-primary"
                        onClick={() => handleComplete(act.id)}
                        disabled={isSubmitting}
                      >
                        {isSubmitting ? 'Salvando...' : 'Salvar e Concluir'}
                      </button>
                    </div>
                  </div>
                ) : (
                  <div className="activity-actions">
                    {act.status === 'SCHEDULED' && (
                      <button
                        type="button"
                        className="btn btn-xs btn-outline"
                        onClick={() => {
                          setCompletingActivityId(act.id);
                          setFeedbackText('');
                        }}
                      >
                        Concluir com Feedback
                      </button>
                    )}
                    <button
                      type="button"
                      className="btn btn-xs btn-danger-outline"
                      onClick={() => handleDelete(act.id)}
                    >
                      Remover
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}

        <div className="modal-actions">
          <button type="button" className="btn btn-secondary" onClick={onClose}>
            Fechar
          </button>
        </div>
      </div>
    </div>
  );
}
