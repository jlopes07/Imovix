import React, { useState, useRef } from 'react';
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
import { CurrencyInput } from '../../../components/CurrencyInput';
import { CepService } from '../../../services/cepService';
import { maskBRL } from '../../../utils/currency';

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

function formatInitialCurrency(value?: number | null): string {
  if (value === undefined || value === null) return '';
  return maskBRL(Math.round(value * 100).toString());
}

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
        salePrice: formatInitialCurrency(initialData.pricing?.salePrice),
        rentalPrice: formatInitialCurrency(initialData.pricing?.rentalPrice),
        condoFee: formatInitialCurrency(initialData.pricing?.condoFee),
        iptu: formatInitialCurrency(initialData.pricing?.iptu),
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
  const [isLookingUpCep, setIsLookingUpCep] = useState(false);
  const [cepStatusMessage, setCepStatusMessage] = useState<string | null>(null);

  const numberInputRef = useRef<HTMLInputElement>(null);

  if (!isOpen) return null;

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleCurrencyChange = (field: 'salePrice' | 'rentalPrice' | 'condoFee' | 'iptu', formattedValue: string) => {
    setFormData((prev) => ({ ...prev, [field]: formattedValue }));
  };

  // Busca e preenchimento de endereço automático via ViaCEP
  const handleCepLookup = async (cepToSearch: string) => {
    const cleanCep = cepToSearch.replace(/\D/g, '');
    if (cleanCep.length !== 8) return;

    try {
      setIsLookingUpCep(true);
      setCepStatusMessage('Buscando endereço pelo CEP...');
      const address = await CepService.lookup(cleanCep);

      if (address) {
        setFormData((prev) => ({
          ...prev,
          street: address.street || prev.street,
          neighborhood: address.neighborhood || prev.neighborhood,
          city: address.city || prev.city,
          state: address.state || prev.state,
          complement: address.complement || prev.complement,
        }));
        setCepStatusMessage('Endereço localizado com sucesso!');
        setTimeout(() => setCepStatusMessage(null), 3500);

        // Foca automaticamente no campo Número após carregar o CEP
        setTimeout(() => {
          numberInputRef.current?.focus();
        }, 100);
      } else {
        setCepStatusMessage('CEP não localizado na base. Preencha manualmente.');
        setTimeout(() => setCepStatusMessage(null), 4000);
      }
    } catch {
      setCepStatusMessage('Erro ao consultar CEP. Preencha manualmente.');
      setTimeout(() => setCepStatusMessage(null), 4000);
    } finally {
      setIsLookingUpCep(false);
    }
  };

  const handleZipCodeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const digits = e.target.value.replace(/\D/g, '').slice(0, 8);
    let formattedZip = digits;
    if (digits.length > 5) {
      formattedZip = `${digits.slice(0, 5)}-${digits.slice(5)}`;
    }
    setFormData((prev) => ({ ...prev, zipCode: formattedZip }));

    // Se completou os 8 dígitos do CEP, busca automaticamente
    if (digits.length === 8) {
      void handleCepLookup(digits);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

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
            X
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
                Erro: {errorMessage}
              </div>
            )}

            {/* Seção 1: Dados Principais */}
            <h3 className="form-section-title">1. Dados Principais</h3>

            {/* Código do imóvel gerado automaticamente pelo sistema (não digitado pelo usuário) */}
            <div
              style={{
                background: 'rgba(56, 189, 248, 0.08)',
                border: '1px solid rgba(56, 189, 248, 0.2)',
                borderRadius: '8px',
                padding: '0.75rem 1rem',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                fontSize: '0.85rem',
                color: '#cbd5e1',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <span>
                  <strong>Código do Imóvel:</strong>{' '}
                  {initialData ? (
                    <span className="card-code-tag">{initialData.code}</span>
                  ) : (
                    <span style={{ color: '#38bdf8' }}>
                      Gerado automaticamente pelo sistema (ex: AP-0001, CS-0002)
                    </span>
                  )}
                </span>
              </div>
              {initialData && (
                <small style={{ color: '#94a3b8' }}>(Identificador interno fixo)</small>
              )}
            </div>

            <div className="form-grid-2">
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
                      [{PROPERTY_TYPE_LABELS[t].icon}] {PROPERTY_TYPE_LABELS[t].label}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">
                  Finalidade Comercial <span className="required">*</span>
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

            <div className="form-group">
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

            {/* Seção 2: Precificação em Real Brasileiro (BRL / R$) */}
            <h3 className="form-section-title">2. Valores & Condomínio (R$ — Real Brasileiro)</h3>
            <div className="form-grid-4">
              <div className="form-group">
                <label className="form-label">Preço de Venda</label>
                <CurrencyInput
                  name="salePrice"
                  value={formData.salePrice}
                  onChange={(val) => handleCurrencyChange('salePrice', val)}
                  placeholder="0,00"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Preço de Locação</label>
                <CurrencyInput
                  name="rentalPrice"
                  value={formData.rentalPrice}
                  onChange={(val) => handleCurrencyChange('rentalPrice', val)}
                  placeholder="0,00"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Taxa Condomínio</label>
                <CurrencyInput
                  name="condoFee"
                  value={formData.condoFee}
                  onChange={(val) => handleCurrencyChange('condoFee', val)}
                  placeholder="0,00"
                />
              </div>

              <div className="form-group">
                <label className="form-label">IPTU Anual</label>
                <CurrencyInput
                  name="iptu"
                  value={formData.iptu}
                  onChange={(val) => handleCurrencyChange('iptu', val)}
                  placeholder="0,00"
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
                  min="0"
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
                  min="0"
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
                  min="0"
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
                  min="0"
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
                  min="0"
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
                  min="0"
                  name="totalArea"
                  className="form-control"
                  placeholder="Ex: 110"
                  value={formData.totalArea}
                  onChange={handleChange}
                />
              </div>
            </div>

            {/* Seção 4: Localização com busca automática de CEP */}
            <h3 className="form-section-title">
              4. Localização & Endereço (Busca Automática por CEP)
            </h3>

            {cepStatusMessage && (
              <div
                style={{
                  background: isLookingUpCep ? 'rgba(56, 189, 248, 0.15)' : 'rgba(16, 185, 129, 0.15)',
                  border: isLookingUpCep ? '1px solid rgba(56, 189, 248, 0.3)' : '1px solid rgba(16, 185, 129, 0.3)',
                  color: isLookingUpCep ? '#38bdf8' : '#34d399',
                  padding: '0.5rem 0.85rem',
                  borderRadius: '6px',
                  fontSize: '0.8rem',
                }}
              >
                {cepStatusMessage}
              </div>
            )}

            <div className="form-grid-3">
              <div className="form-group">
                <label className="form-label">
                  CEP <small style={{ color: '#38bdf8' }}>(digite para buscar)</small>
                </label>
                <div style={{ position: 'relative' }}>
                  <input
                    type="text"
                    name="zipCode"
                    className="form-control"
                    placeholder="00000-000"
                    maxLength={9}
                    value={formData.zipCode}
                    onChange={handleZipCodeChange}
                    onBlur={() => handleCepLookup(formData.zipCode)}
                  />
                  {isLookingUpCep && (
                    <span
                      style={{
                        position: 'absolute',
                        right: '0.75rem',
                        top: '50%',
                        transform: 'translateY(-50%)',
                        fontSize: '0.75rem',
                        color: '#38bdf8',
                      }}
                    >
                      Buscando...
                    </span>
                  )}
                </div>
              </div>

              <div className="form-group" style={{ gridColumn: 'span 2' }}>
                <label className="form-label">Logradouro / Rua</label>
                <input
                  type="text"
                  name="street"
                  className="form-control"
                  placeholder="Ex: Av. Atlântica"
                  value={formData.street}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div className="form-grid-4">
              <div className="form-group">
                <label className="form-label">
                  Número <span className="required">*</span>
                </label>
                <input
                  ref={numberInputRef}
                  type="text"
                  name="number"
                  className="form-control"
                  placeholder="1500"
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
                  placeholder="Apto 101"
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
                  placeholder="Gonzaga"
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
                    placeholder="Santos"
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
