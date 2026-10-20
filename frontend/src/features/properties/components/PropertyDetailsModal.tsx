import React from 'react';
import type { Property } from '../../../types/property';
import {
  PROPERTY_TYPE_LABELS,
  PROPERTY_PURPOSE_LABELS,
  PROPERTY_STATUS_LABELS,
} from '../../../types/property';
import { formatCurrency, formatArea, formatZipCode } from '../../../utils/formatters';

interface PropertyDetailsModalProps {
  property: Property | null;
  onClose: () => void;
  onEdit: (property: Property) => void;
}

export const PropertyDetailsModal: React.FC<PropertyDetailsModalProps> = ({
  property,
  onClose,
  onEdit,
}) => {
  if (!property) return null;

  const typeInfo = PROPERTY_TYPE_LABELS[property.type] || { label: property.type, icon: 'IMO' };
  const purposeInfo = PROPERTY_PURPOSE_LABELS[property.purpose] || { label: property.purpose, badgeClass: '' };
  const statusInfo = PROPERTY_STATUS_LABELS[property.status] || { label: property.status, badgeClass: '' };

  return (
    <div className="modal-overlay" onClick={onClose} role="dialog" aria-modal="true">
      <div className="modal-dialog" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <span className="card-code-tag">{property.code}</span>
            <h2 className="modal-title">{property.title}</h2>
          </div>
          <button className="modal-close-btn" onClick={onClose} aria-label="Fechar modal">
            X
          </button>
        </div>

        <div className="modal-body">
          {/* Header Badges */}
          <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', alignItems: 'center' }}>
            <span className={`badge-status ${purposeInfo.badgeClass}`}>
              {purposeInfo.label}
            </span>
            <span className={`badge-status ${statusInfo.badgeClass}`}>
              {statusInfo.label}
            </span>
            <span className="badge" style={{ background: 'rgba(255, 255, 255, 0.08)', color: '#f8fafc' }}>
              [{typeInfo.icon}] {typeInfo.label}
            </span>
          </div>

          {/* Pricing Highlight Card */}
          <div
            style={{
              background: 'linear-gradient(135deg, rgba(16, 185, 129, 0.12) 0%, rgba(56, 189, 248, 0.08) 100%)',
              border: '1px solid rgba(16, 185, 129, 0.25)',
              borderRadius: '12px',
              padding: '1.25rem',
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(130px, 1fr))',
              gap: '1rem',
            }}
          >
            <div>
              <div style={{ fontSize: '0.75rem', color: '#94a3b8', textTransform: 'uppercase' }}>Venda</div>
              <div style={{ fontSize: '1.35rem', fontWeight: 700, color: '#10b981' }}>
                {formatCurrency(property.pricing?.salePrice)}
              </div>
            </div>
            <div>
              <div style={{ fontSize: '0.75rem', color: '#94a3b8', textTransform: 'uppercase' }}>Locação / mês</div>
              <div style={{ fontSize: '1.35rem', fontWeight: 700, color: '#38bdf8' }}>
                {formatCurrency(property.pricing?.rentalPrice)}
              </div>
            </div>
            <div>
              <div style={{ fontSize: '0.75rem', color: '#94a3b8', textTransform: 'uppercase' }}>Condomínio</div>
              <div style={{ fontSize: '1.1rem', fontWeight: 600, color: '#f8fafc' }}>
                {formatCurrency(property.pricing?.condoFee)}
              </div>
            </div>
            <div>
              <div style={{ fontSize: '0.75rem', color: '#94a3b8', textTransform: 'uppercase' }}>IPTU</div>
              <div style={{ fontSize: '1.1rem', fontWeight: 600, color: '#f8fafc' }}>
                {formatCurrency(property.pricing?.iptu)}
              </div>
            </div>
          </div>

          {/* Description */}
          {property.description && (
            <div>
              <h3 className="form-section-title">Descrição</h3>
              <p style={{ color: '#cbd5e1', fontSize: '0.925rem', lineHeight: '1.6', margin: 0, whiteSpace: 'pre-wrap' }}>
                {property.description}
              </p>
            </div>
          )}

          {/* Dimensões & Características */}
          <div>
            <h3 className="form-section-title">Características do Imóvel</h3>
            <div
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))',
                gap: '0.75rem',
                marginTop: '0.5rem',
              }}
            >
              <div className="glass-card" style={{ padding: '0.75rem' }}>
                <small style={{ color: '#94a3b8' }}>Quartos</small>
                <div style={{ fontSize: '1.1rem', fontWeight: 600 }}>{property.dimensions?.bedrooms ?? 0}</div>
              </div>
              <div className="glass-card" style={{ padding: '0.75rem' }}>
                <small style={{ color: '#94a3b8' }}>Suítes</small>
                <div style={{ fontSize: '1.1rem', fontWeight: 600 }}>{property.dimensions?.suites ?? 0}</div>
              </div>
              <div className="glass-card" style={{ padding: '0.75rem' }}>
                <small style={{ color: '#94a3b8' }}>Banheiros</small>
                <div style={{ fontSize: '1.1rem', fontWeight: 600 }}>{property.dimensions?.bathrooms ?? 0}</div>
              </div>
              <div className="glass-card" style={{ padding: '0.75rem' }}>
                <small style={{ color: '#94a3b8' }}>Vagas Garagem</small>
                <div style={{ fontSize: '1.1rem', fontWeight: 600 }}>{property.dimensions?.parkingSpaces ?? 0}</div>
              </div>
              <div className="glass-card" style={{ padding: '0.75rem' }}>
                <small style={{ color: '#94a3b8' }}>Área Útil</small>
                <div style={{ fontSize: '1.1rem', fontWeight: 600 }}>{formatArea(property.dimensions?.usableArea)}</div>
              </div>
              <div className="glass-card" style={{ padding: '0.75rem' }}>
                <small style={{ color: '#94a3b8' }}>Área Total</small>
                <div style={{ fontSize: '1.1rem', fontWeight: 600 }}>{formatArea(property.dimensions?.totalArea)}</div>
              </div>
            </div>
          </div>

          {/* Endereço */}
          <div>
            <h3 className="form-section-title">Localização</h3>
            <div style={{ color: '#e2e8f0', fontSize: '0.9rem', lineHeight: '1.5' }}>
              <div>
                <strong>Endereço: </strong>
                {[
                  property.address?.street,
                  property.address?.number,
                  property.address?.complement,
                ]
                  .filter(Boolean)
                  .join(', ') || 'Não informado'}
              </div>
              <div>
                <strong>Bairro: </strong> {property.address?.neighborhood || 'Não informado'}
              </div>
              <div>
                <strong>Cidade/UF: </strong>
                {property.address?.city && property.address?.state
                  ? `${property.address.city} - ${property.address.state}`
                  : property.address?.city || 'Não informado'}
              </div>
              <div>
                <strong>CEP: </strong> {formatZipCode(property.address?.zipCode) || 'Não informado'}
              </div>
            </div>
          </div>

          {/* Auditoria / Metadados */}
          <div
            style={{
              fontSize: '0.75rem',
              color: '#64748b',
              borderTop: '1px solid rgba(255, 255, 255, 0.06)',
              paddingTop: '0.75rem',
              display: 'flex',
              justifyContent: 'space-between',
            }}
          >
            <span>ID: {property.id}</span>
            <span>Cadastrado em: {new Date(property.createdAt).toLocaleDateString('pt-BR')}</span>
          </div>
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>
            Fechar
          </button>
          <button
            className="btn btn-primary"
            onClick={() => {
              onClose();
              onEdit(property);
            }}
          >
            Editar Imóvel
          </button>
        </div>
      </div>
    </div>
  );
};
