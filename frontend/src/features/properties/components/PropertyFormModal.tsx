import React, { useState } from 'react';
import type {
  Property,
  PropertyFormData,
  PropertyType,
  PropertyPurpose,
  PropertyStatus,
} from '../../../types/property';
import {
  PROPERTY_TYPE_LABELS,
  PROPERTY_PURPOSE_LABELS,
  PROPERTY_STATUS_LABELS,
} from '../../../types/property';

interface PropertyFormModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (formData: PropertyFormData) => Promise<void>;
  initialData?: Property | null;
}

const DEFAULT_FORM: PropertyFormData = {
  code: '',
  title: '',
  description: '',
  type: 'APARTMENT',
  purpose: 'SALE',
  status: 'AVAILABLE',
  salePrice: '',
  rentalPrice: '',
  condoFee: '',
  iptu: '',
  bedrooms: '2',
  bathrooms: '2',
  suites: '1',
  parkingSpaces: '1',
  usableArea: '',
  totalArea: '',
  zipCode: '',
  street: '',
  number: '',
  complement: '',
  neighborhood: '',
  city: '',
  state: 'SP',
};

export const PropertyFormModal: React.FC<PropertyFormModalProps> = ({
  isOpen,
  onClose,
  onSubmit,
  initialData,
}) => {
  const [formData, setFormData] = useState<PropertyFormData>(() => {
    if (initialData) {
      return {
        code: initialData.code || '',
        title: initialData.title || '',
        description: initialData.description || '',
        type: initialData.type,
        purpose: initialData.purpose,
        status: initialData.status,
        salePrice: initialData.pricing?.salePrice ? String(initialData.pricing.salePrice) : '',
        rentalPrice: initialData.pricing?.rentalPrice ? String(initialData.pricing.rentalPrice) : '',
        condoFee: initialData.pricing?.condoFee ? String(initialData.pricing.condoFee) : '',
        iptu: initialData.pricing?.iptu ? String(initialData.pricing.iptu) : '',
        bedrooms: initialData.dimensions?.bedrooms !== undefined ? String(initialData.dimensions.bedrooms) : '',
        bathrooms: initialData.dimensions?.bathrooms !== undefined ? String(initialData.dimensions.bathrooms) : '',
        suites: initialData.dimensions?.suites !== undefined ? String(initialData.dimensions.suites) : '',
        parkingSpaces: initialData.dimensions?.parkingSpaces !== undefined ? String(initialData.dimensions.parkingSpaces) : '',
        usableArea: initialData.dimensions?.usableArea ? String(initialData.dimensions.usableArea) : '',
        totalArea: initialData.dimensions?.totalArea ? String(initialData.dimensions.totalArea) : '',
        zipCode: initialData.address?.zipCode || '',
        street: initialData.address?.street || '',
        number: initialData.address?.number || '',
        complement: initialData.address?.complement || '',
        neighborhood: initialData.address?.neighborhood || '',
        city: initialData.address?.city || '',
        state: initialData.address?.state || 'SP',
      };
    }
    return DEFAULT_FORM;
  });

  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    if (!formData.code.trim()) {
      setErrorMessage('O código interno do imóvel é obrigatório.');
      return;
    }
    if (!formData.title.trim()) {
      setErrorMessage('O título do anúncio é obrigatório.');
      return;
    }

    try {
      setLoading(true);
      await onSubmit(formData);
      onClose();
    } catch (err: unknown) {
      if (err instanceof Error) {
        setErrorMessage(err.message);
      } else {
        setErrorMessage('Erro ao salvar os dados do imóvel.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose} role="dialog" aria-modal="true">
      <div className="modal-dialog" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2 className="modal-title">
            {initialData ? `Editar Imóvel (${initialData.code})` : 'Novo Cadastro de Imóvel'}
          </h2>
          <button className="modal-close-btn" onClick={onClose} aria-label="Fechar modal">
            ✕
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            {errorMessage && (
              <div
                style={{
                  background: 'rgba(244, 63, 94, 0.15)',
                  border: '1px solid rgba(244, 63, 94, 0.3)',
                  color: '#fda4af',
                  padding: '0.75rem 1rem',
                  borderRadius: '8px',
                  fontSize: '0.875rem',
                }}
              >
                ⚠️ {errorMessage}
              </div>
            )}

            {/* Seção 1: Dados Principais */}
            <h3 className="form-section-title">1. Dados Principais</h3>
            <div className="form-grid-3">
              <div className="form-group">
                <label className="form-label">
                  Código <span className="required">*</span>
                </label>
                <input
                  type="text"
                  name="code"
                  className="form-control"
                  placeholder="Ex: AP-102"
                  value={formData.code}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">
                  Tipo de Imóvel <span className="required">*</span>
                </label>
                <select
                  name="type"
                  className="form-control"
                  value={formData.type}
                  onChange={handleChange}
                >
                  {(Object.keys(PROPERTY_TYPE_LABELS) as PropertyType[]).map((t) => (
                    <option key={t} value={t}>
                      {PROPERTY_TYPE_LABELS[t].icon} {PROPERTY_TYPE_LABELS[t].label}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">
                  Finalidade <span className="required">*</span>
                </label>
                <select
                  name="purpose"
                  className="form-control"
                  value={formData.purpose}
                  onChange={handleChange}
                >
                  {(Object.keys(PROPERTY_PURPOSE_LABELS) as PropertyPurpose[]).map((p) => (
                    <option key={p} value={p}>
                      {PROPERTY_PURPOSE_LABELS[p].label}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-grid-2">
              <div className="form-group" style={{ gridColumn: 'span 2' }}>
                <label className="form-label">
                  Título do Anúncio <span className="required">*</span>
                </label>
                <input
                  type="text"
                  name="title"
                  className="form-control"
                  placeholder="Ex: Apartamento Alto Padrão com Varanda Gourmet"
                  value={formData.title}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            {initialData && (
              <div className="form-group">
                <label className="form-label">Status da Negociação</label>
                <select
                  name="status"
                  className="form-control"
                  value={formData.status}
                  onChange={handleChange}
                >
                  {(Object.keys(PROPERTY_STATUS_LABELS) as PropertyStatus[]).map((s) => (
                    <option key={s} value={s}>
                      {PROPERTY_STATUS_LABELS[s].label}
                    </option>
                  ))}
                </select>
              </div>
            )}

            <div className="form-group">
              <label className="form-label">Descrição Completa</label>
              <textarea
                name="description"
                rows={3}
                className="form-control"
                placeholder="Detalhes sobre localização, acabamentos, luminosidade, etc."
                value={formData.description}
                onChange={handleChange}
              />
            </div>

            {/* Seção 2: Precificação */}
            <h3 className="form-section-title">2. Valores & Condomínio (R$)</h3>
            <div className="form-grid-4">
              <div className="form-group">
                <label className="form-label">Preço Venda</label>
                <input
                  type="number"
                  step="0.01"
                  name="salePrice"
                  className="form-control"
                  placeholder="0,00"
                  value={formData.salePrice}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Preço Locação</label>
                <input
                  type="number"
                  step="0.01"
                  name="rentalPrice"
                  className="form-control"
                  placeholder="0,00"
                  value={formData.rentalPrice}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Condomínio</label>
                <input
                  type="number"
                  step="0.01"
                  name="condoFee"
                  className="form-control"
                  placeholder="0,00"
                  value={formData.condoFee}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label className="form-label">IPTU Anual</label>
                <input
                  type="number"
                  step="0.01"
                  name="iptu"
                  className="form-control"
                  placeholder="0,00"
                  value={formData.iptu}
                  onChange={handleChange}
                />
              </div>
            </div>

            {/* Seção 3: Dimensões e Cômodos */}
            <h3 className="form-section-title">3. Dimensões & Cômodos</h3>
            <div className="form-grid-3">
              <div className="form-group">
                <label className="form-label">Quartos</label>
                <input
                  type="number"
                  name="bedrooms"
                  className="form-control"
                  value={formData.bedrooms}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Suítes</label>
                <input
                  type="number"
                  name="suites"
                  className="form-control"
                  value={formData.suites}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Banheiros</label>
                <input
                  type="number"
                  name="bathrooms"
                  className="form-control"
                  value={formData.bathrooms}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div className="form-grid-3">
              <div className="form-group">
                <label className="form-label">Vagas Garagem</label>
                <input
                  type="number"
                  name="parkingSpaces"
                  className="form-control"
                  value={formData.parkingSpaces}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Área Útil (m²)</label>
                <input
                  type="number"
                  step="0.1"
                  name="usableArea"
                  className="form-control"
                  placeholder="Ex: 85.5"
                  value={formData.usableArea}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Área Total (m²)</label>
                <input
                  type="number"
                  step="0.1"
                  name="totalArea"
                  className="form-control"
                  placeholder="Ex: 110"
                  value={formData.totalArea}
                  onChange={handleChange}
                />
              </div>
            </div>

            {/* Seção 4: Localização */}
            <h3 className="form-section-title">4. Localização & Endereço</h3>
            <div className="form-grid-3">
              <div className="form-group">
                <label className="form-label">CEP</label>
                <input
                  type="text"
                  name="zipCode"
                  className="form-control"
                  placeholder="00000-000"
                  value={formData.zipCode}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group" style={{ gridColumn: 'span 2' }}>
                <label className="form-label">Logradouro / Rua</label>
                <input
                  type="text"
                  name="street"
                  className="form-control"
                  placeholder="Ex: Av. Paulista"
                  value={formData.street}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div className="form-grid-4">
              <div className="form-group">
                <label className="form-label">Número</label>
                <input
                  type="text"
                  name="number"
                  className="form-control"
                  placeholder="1000"
                  value={formData.number}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Complemento</label>
                <input
                  type="text"
                  name="complement"
                  className="form-control"
                  placeholder="Apto 42"
                  value={formData.complement}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Bairro</label>
                <input
                  type="text"
                  name="neighborhood"
                  className="form-control"
                  placeholder="Bela Vista"
                  value={formData.neighborhood}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Cidade / UF</label>
                <div style={{ display: 'flex', gap: '0.25rem' }}>
                  <input
                    type="text"
                    name="city"
                    className="form-control"
                    placeholder="São Paulo"
                    value={formData.city}
                    onChange={handleChange}
                    style={{ flex: 2 }}
                  />
                  <input
                    type="text"
                    name="state"
                    className="form-control"
                    placeholder="SP"
                    maxLength={2}
                    value={formData.state}
                    onChange={handleChange}
                    style={{ flex: 1, textTransform: 'uppercase' }}
                  />
                </div>
              </div>
            </div>
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
              type="submit"
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? 'Salvando...' : initialData ? 'Salvar Alterações' : 'Cadastrar Imóvel'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
