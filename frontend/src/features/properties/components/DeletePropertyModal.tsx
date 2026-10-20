import React, { useState } from 'react';
import type { Property } from '../../../types/property';

interface DeletePropertyModalProps {
  property: Property | null;
  isOpen: boolean;
  onClose: () => void;
  onConfirm: (propertyId: string) => Promise<void>;
}

export const DeletePropertyModal: React.FC<DeletePropertyModalProps> = ({
  property,
  isOpen,
  onClose,
  onConfirm,
}) => {
  const [loading, setLoading] = useState(false);

  if (!isOpen || !property) return null;

  const handleConfirm = async () => {
    try {
      setLoading(true);
      await onConfirm(property.id);
      onClose();
    } catch {
      // erro tratado no componente pai
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose} role="dialog" aria-modal="true">
      <div
        className="modal-dialog"
        style={{ maxWidth: '480px' }}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="modal-header">
          <h2 className="modal-title" style={{ color: '#f43f5e' }}>
            Confirmar Exclusão
          </h2>
          <button className="modal-close-btn" onClick={onClose} aria-label="Fechar modal">
            X
          </button>
        </div>

        <div className="modal-body">
          <p style={{ color: '#cbd5e1', fontSize: '0.95rem', margin: 0, lineHeight: '1.5' }}>
            Você tem certeza que deseja excluir o imóvel abaixo da imobiliária?
          </p>

          <div
            style={{
              background: 'rgba(244, 63, 94, 0.08)',
              border: '1px solid rgba(244, 63, 94, 0.2)',
              borderRadius: '8px',
              padding: '0.85rem',
              marginTop: '0.75rem',
            }}
          >
            <div style={{ color: '#fda4af', fontWeight: 600 }}>{property.title}</div>
            <div style={{ fontSize: '0.8rem', color: '#94a3b8', marginTop: '0.25rem' }}>
              Código: <code>{property.code}</code> • Status atual: <strong>{property.status}</strong>
            </div>
          </div>

          <p style={{ fontSize: '0.8rem', color: '#94a3b8', marginTop: '0.75rem', marginBottom: 0 }}>
            * Esta ação executará a exclusão com conformidade ao isolamento multi-tenant da sua empresa.
          </p>
        </div>

        <div className="modal-footer">
          <button
            type="button"
            className="btn btn-secondary"
            onClick={onClose}
            disabled={loading}
          >
            Cancelar
          </button>
          <button
            type="button"
            className="btn"
            style={{ background: '#e11d48', color: '#ffffff' }}
            onClick={handleConfirm}
            disabled={loading}
          >
            {loading ? 'Excluindo...' : 'Sim, Excluir Imóvel'}
          </button>
        </div>
      </div>
    </div>
  );
};
