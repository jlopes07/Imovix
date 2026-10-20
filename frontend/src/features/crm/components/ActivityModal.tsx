import { useState, type FormEvent } from 'react';
import type { ActivityType, ScheduleActivityPayload } from '../../../types/crm';
import { ACTIVITY_TYPE_LABELS } from '../../../types/crm';

interface ActivityModalProps {
  isOpen: boolean;
  onClose: () => void;
  leadId: string;
  leadName: string;
  onSchedule: (payload: ScheduleActivityPayload) => Promise<void>;
}

export function ActivityModal({
  isOpen,
  onClose,
  leadId,
  leadName,
  onSchedule,
}: ActivityModalProps) {
  const [type, setType] = useState<ActivityType>('VISIT');
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [scheduledAt, setScheduledAt] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!title.trim()) {
      setError('O titulo da atividade e obrigatorio.');
      return;
    }
    if (!scheduledAt) {
      setError('A data e hora do agendamento sao obrigatorias.');
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);

      // Converte data local para ISO
      const isoDate = new Date(scheduledAt).toISOString();

      await onSchedule({
        leadId,
        type,
        title: title.trim(),
        description: description.trim() || undefined,
        scheduledAt: isoDate,
      });

      onClose();
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Ocorreu um erro ao agendar a atividade.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-container crm-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Agendar Atividade / Visita</h2>
          <button type="button" className="close-btn" onClick={onClose} aria-label="Fechar">
            X
          </button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          {error && <div className="alert-box error-alert">{error}</div>}

          <div className="lead-summary-pill">
            <span className="pill-label">Contato Vinculado:</span>
            <span className="pill-name">{leadName}</span>
          </div>

          <div className="form-group">
            <label htmlFor="activity-type">Tipo de Compromisso</label>
            <select
              id="activity-type"
              value={type}
              onChange={(e) => setType(e.target.value as ActivityType)}
            >
              {Object.entries(ACTIVITY_TYPE_LABELS).map(([key, label]) => (
                <option key={key} value={key}>
                  {label}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="activity-title">Titulo do Compromisso *</label>
            <input
              id="activity-title"
              type="text"
              required
              placeholder="Ex: Visita ao imovel com o cliente e apresentacao de proposta"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label htmlFor="activity-datetime">Data e Horario Marcado *</label>
            <input
              id="activity-datetime"
              type="datetime-local"
              required
              value={scheduledAt}
              onChange={(e) => setScheduledAt(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label htmlFor="activity-desc">Pauta / Observacoes</label>
            <textarea
              id="activity-desc"
              rows={3}
              placeholder="Ex: Confirmar presenca com 2 horas de antecedencia via WhatsApp."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
            />
          </div>

          <div className="modal-actions">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={isSubmitting}>
              Cancelar
            </button>
            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
              {isSubmitting ? 'Agendando...' : 'Confirmar Agendamento'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
