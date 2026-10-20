import { useState, type FormEvent } from 'react';
import type { PublicListing } from '../../../types/listing';
import { ListingService } from '../../../services/listingService';

interface InquiryModalProps {
  isOpen: boolean;
  onClose: () => void;
  listing: PublicListing;
}

export function InquiryModal({ isOpen, onClose, listing }: InquiryModalProps) {
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState(
    `Ola! Tenho interesse no imovel ${listing.code} - ${listing.title}. Gostaria de mais detalhes e agendar uma visita.`
  );
  const [lgpdConsent, setLgpdConsent] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      setError('Por favor, informe seu nome completo.');
      return;
    }
    if (!phone.trim()) {
      setError('Por favor, informe seu telefone ou WhatsApp de contato.');
      return;
    }
    if (!lgpdConsent) {
      setError('E necessario autorizar o contato nos termos da LGPD.');
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);

      const res = await ListingService.submitInquiry(listing.id, {
        name: name.trim(),
        phone: phone.trim(),
        email: email.trim() || undefined,
        message: message.trim() || undefined,
        lgpdConsent,
      });

      setSuccessMessage(res.message);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Ocorreu um erro ao enviar sua solicitacao. Tente novamente.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const formatPrice = (val: number | null) => {
    if (!val) return 'Sob Consulta';
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      maximumFractionDigits: 0,
    }).format(val);
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-container inquiry-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Manifestar Interesse no Imovel</h2>
          <button type="button" className="close-btn" onClick={onClose} aria-label="Fechar">
            X
          </button>
        </div>

        {successMessage ? (
          <div className="inquiry-success-box">
            <div className="success-badge-label">Solicitacao Enviada com Sucesso</div>
            <p className="success-text">{successMessage}</p>
            <p className="success-subtext">
              Nossa equipe de corretores ja recebeu suas informacoes no CRM e entrara em contato pelo telefone/WhatsApp informado.
            </p>
            <div className="modal-actions">
              <button type="button" className="btn btn-primary" onClick={onClose}>
                Entendido / Fechar
              </button>
            </div>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="modal-form">
            {error && <div className="alert-box error-alert">{error}</div>}

            <div className="inquiry-property-summary">
              <div className="inquiry-code-tag">{listing.code}</div>
              <div className="inquiry-title-price">
                <span className="summary-title">{listing.title}</span>
                <span className="summary-price">
                  {listing.purpose === 'RENT'
                    ? `${formatPrice(listing.rentalPrice)}/mes`
                    : formatPrice(listing.salePrice)}
                </span>
              </div>
              <div className="summary-location">
                {listing.neighborhood ? `${listing.neighborhood}, ` : ''}
                {listing.city} - {listing.state}
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="inquiry-name">Seu Nome Completo *</label>
              <input
                id="inquiry-name"
                type="text"
                required
                placeholder="Ex: Carlos Eduardo de Oliveira"
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="inquiry-phone">WhatsApp / Telefone *</label>
                <input
                  id="inquiry-phone"
                  type="tel"
                  required
                  placeholder="(11) 98765-4321"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                />
              </div>

              <div className="form-group">
                <label htmlFor="inquiry-email">E-mail</label>
                <input
                  id="inquiry-email"
                  type="email"
                  placeholder="seu.email@exemplo.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="inquiry-message">Mensagem ou Duvidas</label>
              <textarea
                id="inquiry-message"
                rows={3}
                value={message}
                onChange={(e) => setMessage(e.target.value)}
              />
            </div>

            <div className="lgpd-consent-group">
              <input
                id="inquiry-lgpd"
                type="checkbox"
                required
                checked={lgpdConsent}
                onChange={(e) => setLgpdConsent(e.target.checked)}
              />
              <label htmlFor="inquiry-lgpd" className="lgpd-text">
                Autorizo a imobiliaria a utilizar meus dados exclusivamente para contato sobre este imovel e propostas comerciais, em conformidade com a Lei Geral de Protecao de Dados (LGPD - Lei 13.709/2018).
              </label>
            </div>

            <div className="modal-actions">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={onClose}
                disabled={isSubmitting}
              >
                Cancelar
              </button>
              <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
                {isSubmitting ? 'Enviando...' : 'Enviar Mensagem / Tenho Interesse'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
