import { useState, type FormEvent } from 'react';
import type { ContactType, CreateLeadPayload, Lead, LeadSource, LeadStatus } from '../../../types/crm';
import { CONTACT_TYPE_LABELS, LEAD_SOURCE_LABELS, LEAD_STATUS_LABELS } from '../../../types/crm';

interface LeadModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (payload: CreateLeadPayload) => Promise<void>;
  leadToEdit?: Lead | null;
}

export function LeadModal({ isOpen, onClose, onSave, leadToEdit }: LeadModalProps) {
  const [name, setName] = useState(leadToEdit?.name || '');
  const [email, setEmail] = useState(leadToEdit?.email || '');
  const [phone, setPhone] = useState(leadToEdit?.phone || '');
  const [type, setType] = useState<ContactType>(leadToEdit?.type || 'BUYER');
  const [status, setStatus] = useState<LeadStatus>(leadToEdit?.status || 'NEW');
  const [source, setSource] = useState<LeadSource>(leadToEdit?.source || 'WEBSITE');
  const [budgetMin, setBudgetMin] = useState(leadToEdit?.budgetMin ? String(leadToEdit.budgetMin) : '');
  const [budgetMax, setBudgetMax] = useState(leadToEdit?.budgetMax ? String(leadToEdit.budgetMax) : '');
  const [notes, setNotes] = useState(leadToEdit?.notes || '');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      setError('O nome do lead ou contato e obrigatorio.');
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);

      const payload: CreateLeadPayload = {
        name: name.trim(),
        email: email.trim() || undefined,
        phone: phone.trim() || undefined,
        type,
        status,
        source,
        budgetMin: budgetMin ? parseFloat(budgetMin.replace(/[^0-9.]/g, '')) : undefined,
        budgetMax: budgetMax ? parseFloat(budgetMax.replace(/[^0-9.]/g, '')) : undefined,
        notes: notes.trim() || undefined,
      };

      await onSave(payload);
      onClose();
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Ocorreu um erro ao salvar o lead.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-container crm-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{leadToEdit ? 'Editar Lead / Contato' : 'Cadastrar Novo Lead'}</h2>
          <button type="button" className="close-btn" onClick={onClose} aria-label="Fechar">
            X
          </button>
        </div>

        <form onSubmit={handleSubmit} className="modal-form">
          {error && <div className="alert-box error-alert">{error}</div>}

          <div className="form-group">
            <label htmlFor="lead-name">Nome Completo *</label>
            <input
              id="lead-name"
              type="text"
              required
              placeholder="Ex: Carlos Eduardo Silva"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="lead-phone">Telefone / WhatsApp</label>
              <input
                id="lead-phone"
                type="tel"
                placeholder="(11) 98765-4321"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label htmlFor="lead-email">E-mail</label>
              <input
                id="lead-email"
                type="email"
                placeholder="cliente@exemplo.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="lead-type">Tipo de Contato</label>
              <select
                id="lead-type"
                value={type}
                onChange={(e) => setType(e.target.value as ContactType)}
              >
                {Object.entries(CONTACT_TYPE_LABELS).map(([key, label]) => (
                  <option key={key} value={key}>
                    {label}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="lead-source">Origem do Lead</label>
              <select
                id="lead-source"
                value={source}
                onChange={(e) => setSource(e.target.value as LeadSource)}
              >
                {Object.entries(LEAD_SOURCE_LABELS).map(([key, label]) => (
                  <option key={key} value={key}>
                    {label}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="lead-status">Estagio Inicial no Funil</label>
            <select
              id="lead-status"
              value={status}
              onChange={(e) => setStatus(e.target.value as LeadStatus)}
            >
              {Object.entries(LEAD_STATUS_LABELS).map(([key, label]) => (
                <option key={key} value={key}>
                  {label}
                </option>
              ))}
            </select>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="lead-budget-min">Orcamento Minimo Estimado (R$)</label>
              <input
                id="lead-budget-min"
                type="text"
                placeholder="Ex: 300000"
                value={budgetMin}
                onChange={(e) => setBudgetMin(e.target.value)}
              />
            </div>

            <div className="form-group">
              <label htmlFor="lead-budget-max">Orcamento Maximo Estimado (R$)</label>
              <input
                id="lead-budget-max"
                type="text"
                placeholder="Ex: 600000"
                value={budgetMax}
                onChange={(e) => setBudgetMax(e.target.value)}
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="lead-notes">Notas e Preferencias de Busca</label>
            <textarea
              id="lead-notes"
              rows={3}
              placeholder="Ex: Busca apartamento de 2 a 3 quartos com vaga, regiao sul, prefere varanda com churrasqueira."
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
            />
          </div>

          <div className="modal-actions">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={isSubmitting}>
              Cancelar
            </button>
            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
              {isSubmitting ? 'Salvando...' : leadToEdit ? 'Atualizar Lead' : 'Cadastrar Lead'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
