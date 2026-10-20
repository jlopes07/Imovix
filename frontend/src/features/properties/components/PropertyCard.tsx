import React from 'react';
import type { Property } from '../../../types/property';
import {
  PROPERTY_TYPE_LABELS,
  PROPERTY_PURPOSE_LABELS,
  PROPERTY_STATUS_LABELS,
} from '../../../types/property';
import { formatCurrency, formatArea } from '../../../utils/formatters';

interface PropertyCardProps {
  property: Property;
  onViewDetails: (property: Property) => void;
  onEdit: (property: Property) => void;
  onDelete: (property: Property) => void;
}

export const PropertyCard: React.FC<PropertyCardProps> = ({
  property,
  onViewDetails,
  onEdit,
  onDelete,
}) => {
  const typeInfo = PROPERTY_TYPE_LABELS[property.type] || { label: property.type, icon: '🏠' };
  const purposeInfo = PROPERTY_PURPOSE_LABELS[property.purpose] || { label: property.purpose, badgeClass: '' };
  const statusInfo = PROPERTY_STATUS_LABELS[property.status] || { label: property.status, badgeClass: '' };

  const addressSummary = [
    property.address?.neighborhood,
    property.address?.city && property.address?.state
      ? `${property.address.city}/${property.address.state}`
      : property.address?.city,
  ]
    .filter(Boolean)
    .join(' • ') || 'Endereço não informado';

  return (
    <article className="property-card" aria-label={`Imóvel ${property.code}: ${property.title}`}>
      {/* Cover / Header */}
      <div className="property-card-cover">
        <span className="card-code-tag">{property.code}</span>
        <div style={{ display: 'flex', gap: '0.4rem' }}>
          <span className={`badge-status ${purposeInfo.badgeClass}`}>
            {purposeInfo.label}
          </span>
          <span className={`badge-status ${statusInfo.badgeClass}`}>
            {statusInfo.label}
          </span>
        </div>
        <div className="property-cover-art" aria-hidden="true">
          {typeInfo.icon}
        </div>
      </div>

      {/* Body */}
      <div className="property-card-body">
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.75rem', color: '#38bdf8' }}>
          <span>{typeInfo.icon}</span>
          <span>{typeInfo.label}</span>
        </div>

        <h3 className="property-card-title" title={property.title}>
          {property.title}
        </h3>

        <div className="property-card-address" title={addressSummary}>
          <span>📍</span>
          <span>{addressSummary}</span>
        </div>

        {/* Pricing Box */}
        <div className="property-pricing-box">
          {property.purpose === 'SALE' && (
            <div className="price-main">{formatCurrency(property.pricing?.salePrice)}</div>
          )}
          {property.purpose === 'RENT' && (
            <div className="price-main">
              {formatCurrency(property.pricing?.rentalPrice)}
              <span style={{ fontSize: '0.8rem', fontWeight: 400 }}> /mês</span>
            </div>
          )}
          {property.purpose === 'BOTH' && (
            <div>
              <div className="price-main">{formatCurrency(property.pricing?.salePrice)}</div>
              <div className="price-sub">
                ou {formatCurrency(property.pricing?.rentalPrice)}/mês
              </div>
            </div>
          )}
          {(property.pricing?.condoFee || property.pricing?.iptu) && (
            <div className="price-sub">
              {property.pricing?.condoFee ? `Cond.: ${formatCurrency(property.pricing.condoFee)}` : ''}
              {property.pricing?.condoFee && property.pricing?.iptu ? ' • ' : ''}
              {property.pricing?.iptu ? `IPTU: ${formatCurrency(property.pricing.iptu)}` : ''}
            </div>
          )}
        </div>

        {/* Features Row */}
        <div className="property-features-row">
          <div className="feature-item" title="Quartos">
            <span>🛏️</span>
            <span>{property.dimensions?.bedrooms ?? 0}</span>
          </div>
          <div className="feature-item" title="Suítes">
            <span>✨</span>
            <span>{property.dimensions?.suites ?? 0}</span>
          </div>
          <div className="feature-item" title="Banheiros">
            <span>🚿</span>
            <span>{property.dimensions?.bathrooms ?? 0}</span>
          </div>
          <div className="feature-item" title="Vagas de Garagem">
            <span>🚗</span>
            <span>{property.dimensions?.parkingSpaces ?? 0}</span>
          </div>
          <div className="feature-item" style={{ marginLeft: 'auto' }} title="Área Útil">
            <span>📐</span>
            <span>{formatArea(property.dimensions?.usableArea || property.dimensions?.totalArea)}</span>
          </div>
        </div>

        {/* Actions Footer */}
        <div className="property-card-footer">
          <button
            className="btn btn-secondary"
            style={{ padding: '0.4rem 0.75rem', fontSize: '0.8rem' }}
            onClick={() => onViewDetails(property)}
          >
            Ver Detalhes
          </button>

          <div className="property-actions-group">
            <button
              className="btn btn-secondary"
              style={{ padding: '0.4rem 0.65rem', fontSize: '0.8rem' }}
              onClick={() => onEdit(property)}
              title="Editar imóvel"
            >
              ✏️
            </button>
            <button
              className="btn btn-secondary"
              style={{ padding: '0.4rem 0.65rem', fontSize: '0.8rem', color: '#f43f5e' }}
              onClick={() => onDelete(property)}
              title="Excluir imóvel"
            >
              🗑️
            </button>
          </div>
        </div>
      </div>
    </article>
  );
};
