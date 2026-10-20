import React, { useState, useEffect, useMemo, useCallback } from 'react';
import './properties.css';
import type { Property, PropertyFormData, PropertyType, PropertyPurpose, PropertyStatus } from '../../types/property';
import type { User, Company } from '../../types/tenant';
import { PropertyService } from '../../services/propertyService';
import { AuthService } from '../../services/authService';
import { TenantBar } from './components/TenantBar';
import { PropertyCard } from './components/PropertyCard';
import { PropertyFormModal } from './components/PropertyFormModal';
import { PropertyDetailsModal } from './components/PropertyDetailsModal';
import { DeletePropertyModal } from './components/DeletePropertyModal';
import { formatCurrency } from '../../utils/formatters';

interface ToastInfo {
  id: string;
  type: 'success' | 'error';
  message: string;
}

export const PropertiesPage: React.FC = () => {
  // Estado de Tenant / Usuário
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [currentCompany, setCurrentCompany] = useState<Company | null>(null);

  // Estado de Imóveis
  const [properties, setProperties] = useState<Property[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [page, setPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [totalElements, setTotalElements] = useState<number>(0);

  // Filtros
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [selectedType, setSelectedType] = useState<PropertyType | 'ALL'>('ALL');
  const [selectedPurpose, setSelectedPurpose] = useState<PropertyPurpose | 'ALL'>('ALL');
  const [selectedStatus, setSelectedStatus] = useState<PropertyStatus | 'ALL'>('ALL');

  // Modais
  const [isCreateModalOpen, setIsCreateModalOpen] = useState<boolean>(false);
  const [editingProperty, setEditingProperty] = useState<Property | null>(null);
  const [viewingProperty, setViewingProperty] = useState<Property | null>(null);
  const [deletingProperty, setDeletingProperty] = useState<Property | null>(null);

  // Toasts
  const [toasts, setToasts] = useState<ToastInfo[]>([]);

  const addToast = (type: 'success' | 'error', message: string) => {
    const id = Math.random().toString(36).substring(2, 9);
    setToasts((prev) => [...prev, { id, type, message }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 4000);
  };

  const loadProperties = useCallback(async (targetPage = 0) => {
    try {
      setLoading(true);
      const response = await PropertyService.list(targetPage, 20);
      setProperties(response.items || []);
      setPage(response.page);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Erro ao listar imóveis da imobiliária';
      addToast('error', msg);
    } finally {
      setLoading(false);
    }
  }, []);

  // Inicialização da sessão e do tenant
  const initSession = useCallback(async () => {
    try {
      setLoading(true);
      const session = await AuthService.bootstrapDemoSession();
      setCurrentUser(session.user);
      setCurrentCompany(session.company);
      await loadProperties(0);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Falha ao autenticar sessão multi-tenant';
      addToast('error', msg);
    } finally {
      setLoading(false);
    }
  }, [loadProperties]);

  // oxlint-disable-next-line react/set-state-in-effect
  useEffect(() => {
    void initSession();
  }, [initSession]);

  // Handlers do CRUD
  const handleCreateProperty = async (data: PropertyFormData) => {
    const created = await PropertyService.create(data);
    addToast('success', `Imóvel "${created.title}" cadastrado com sucesso!`);
    await loadProperties(page);
  };

  const handleUpdateProperty = async (data: PropertyFormData) => {
    if (!editingProperty) return;
    const updated = await PropertyService.update(editingProperty.id, data);
    addToast('success', `Imóvel "${updated.code}" atualizado com sucesso!`);
    setEditingProperty(null);
    await loadProperties(page);
  };

  const handleDeleteProperty = async (propertyId: string) => {
    await PropertyService.delete(propertyId);
    addToast('success', 'Imóvel removido com sucesso!');
    await loadProperties(page);
  };

  // Filtragem local sobre a página
  const filteredProperties = useMemo(() => {
    return properties.filter((p) => {
      const matchesSearch =
        searchTerm.trim() === '' ||
        p.code.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (p.address?.neighborhood && p.address.neighborhood.toLowerCase().includes(searchTerm.toLowerCase())) ||
        (p.address?.city && p.address.city.toLowerCase().includes(searchTerm.toLowerCase()));

      const matchesType = selectedType === 'ALL' || p.type === selectedType;
      const matchesPurpose = selectedPurpose === 'ALL' || p.purpose === selectedPurpose;
      const matchesStatus = selectedStatus === 'ALL' || p.status === selectedStatus;

      return matchesSearch && matchesType && matchesPurpose && matchesStatus;
    });
  }, [properties, searchTerm, selectedType, selectedPurpose, selectedStatus]);

  // KPIs
  const kpis = useMemo(() => {
    const total = totalElements;
    const available = properties.filter((p) => p.status === 'AVAILABLE').length;
    const forSale = properties.filter((p) => p.purpose === 'SALE' || p.purpose === 'BOTH').length;
    const forRent = properties.filter((p) => p.purpose === 'RENT' || p.purpose === 'BOTH').length;
    const totalPortfolioValue = properties.reduce(
      (acc, p) => acc + (p.pricing?.salePrice || 0),
      0
    );

    return { total, available, forSale, forRent, totalPortfolioValue };
  }, [properties, totalElements]);

  return (
    <div className="properties-container">
      {/* Toast notifications container */}
      <div className="toast-container" aria-live="polite">
        {toasts.map((toast) => (
          <div key={toast.id} className={`toast toast-${toast.type}`}>
            <span>{toast.type === 'success' ? '✅' : '❌'}</span>
            <span>{toast.message}</span>
          </div>
        ))}
      </div>

      {/* Tenant Context Bar */}
      <TenantBar
        user={currentUser}
        company={currentCompany}
        onRefreshSession={initSession}
      />

      {/* KPI Cards Header */}
      <div className="kpi-grid">
        <div className="kpi-card">
          <div className="kpi-icon" style={{ background: 'rgba(56, 189, 248, 0.15)', color: '#38bdf8' }}>
            🏢
          </div>
          <div className="kpi-info">
            <span className="kpi-label">Total de Imóveis</span>
            <span className="kpi-value">{kpis.total}</span>
          </div>
        </div>

        <div className="kpi-card">
          <div className="kpi-icon" style={{ background: 'rgba(16, 185, 129, 0.15)', color: '#10b981' }}>
            ✨
          </div>
          <div className="kpi-info">
            <span className="kpi-label">Disponíveis</span>
            <span className="kpi-value">{kpis.available}</span>
          </div>
        </div>

        <div className="kpi-card">
          <div className="kpi-icon" style={{ background: 'rgba(168, 85, 247, 0.15)', color: '#c084fc' }}>
            🏷️
          </div>
          <div className="kpi-info">
            <span className="kpi-label">Para Venda / Locação</span>
            <span className="kpi-value">
              {kpis.forSale} / {kpis.forRent}
            </span>
          </div>
        </div>

        <div className="kpi-card">
          <div className="kpi-icon" style={{ background: 'rgba(245, 158, 11, 0.15)', color: '#f59e0b' }}>
            💰
          </div>
          <div className="kpi-info">
            <span className="kpi-label">Carteira de Venda</span>
            <span className="kpi-value" style={{ fontSize: '1.15rem' }}>
              {formatCurrency(kpis.totalPortfolioValue)}
            </span>
          </div>
        </div>
      </div>

      {/* Toolbar & Filters */}
      <section className="toolbar-section" aria-label="Filtros e ações de imóveis">
        <div className="toolbar-top">
          <div className="search-input-wrapper">
            <span className="search-icon-placeholder">🔍</span>
            <input
              type="text"
              placeholder="Buscar por código (AP-101), título, bairro ou cidade..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <button
              className="btn btn-secondary"
              onClick={() => loadProperties(page)}
              disabled={loading}
              title="Recarregar lista"
            >
              🔄 Atualizar
            </button>
            <button
              className="btn btn-primary"
              onClick={() => setIsCreateModalOpen(true)}
              style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}
            >
              <span>➕</span>
              <span>Novo Imóvel</span>
            </button>
          </div>
        </div>

        <div className="toolbar-filters">
          <span style={{ fontSize: '0.8rem', color: '#94a3b8' }}>Filtrar por:</span>

          <select
            className="filter-select"
            value={selectedType}
            onChange={(e) => setSelectedType(e.target.value as PropertyType | 'ALL')}
          >
            <option value="ALL">Todos os Tipos</option>
            <option value="APARTMENT">Apartamentos</option>
            <option value="HOUSE">Casas</option>
            <option value="LAND">Terrenos</option>
            <option value="COMMERCIAL">Comerciais</option>
            <option value="RURAL">Rurais</option>
          </select>

          <select
            className="filter-select"
            value={selectedPurpose}
            onChange={(e) => setSelectedPurpose(e.target.value as PropertyPurpose | 'ALL')}
          >
            <option value="ALL">Todas as Finalidades</option>
            <option value="SALE">Venda</option>
            <option value="RENT">Locação</option>
            <option value="BOTH">Venda & Locação</option>
          </select>

          <select
            className="filter-select"
            value={selectedStatus}
            onChange={(e) => setSelectedStatus(e.target.value as PropertyStatus | 'ALL')}
          >
            <option value="ALL">Todos os Status</option>
            <option value="AVAILABLE">Disponível</option>
            <option value="UNDER_NEGOTIATION">Em Negociação</option>
            <option value="SOLD">Vendido</option>
            <option value="RENTED">Locado</option>
            <option value="INACTIVE">Inativo</option>
          </select>

          {(searchTerm || selectedType !== 'ALL' || selectedPurpose !== 'ALL' || selectedStatus !== 'ALL') && (
            <button
              className="btn btn-secondary"
              style={{ padding: '0.35rem 0.65rem', fontSize: '0.75rem' }}
              onClick={() => {
                setSearchTerm('');
                setSelectedType('ALL');
                setSelectedPurpose('ALL');
                setSelectedStatus('ALL');
              }}
            >
              Limpar Filtros
            </button>
          )}
        </div>
      </section>

      {/* Grid de Imóveis */}
      {loading && properties.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '3rem', color: '#94a3b8' }}>
          <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>⏳</div>
          <p>Carregando imóveis com isolamento multi-tenant...</p>
        </div>
      ) : filteredProperties.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">🏠</div>
          <h3 className="empty-state-title">Nenhum imóvel encontrado</h3>
          <p className="empty-state-desc">
            {searchTerm || selectedType !== 'ALL' || selectedPurpose !== 'ALL' || selectedStatus !== 'ALL'
              ? 'Nenhum resultado corresponde aos filtros aplicados.'
              : 'Sua imobiliária ainda não possui imóveis cadastrados nesta base. Comece cadastrando seu primeiro imóvel!'}
          </p>
          <button
            className="btn btn-primary"
            onClick={() => setIsCreateModalOpen(true)}
          >
            Cadastrar Primeiro Imóvel
          </button>
        </div>
      ) : (
        <div className="properties-grid">
          {filteredProperties.map((prop) => (
            <PropertyCard
              key={prop.id}
              property={prop}
              onViewDetails={(p) => setViewingProperty(p)}
              onEdit={(p) => setEditingProperty(p)}
              onDelete={(p) => setDeletingProperty(p)}
            />
          ))}
        </div>
      )}

      {/* Paginação */}
      {totalPages > 1 && (
        <div style={{ display: 'flex', justifyContent: 'center', gap: '0.5rem', marginTop: '1.5rem' }}>
          <button
            className="btn btn-secondary"
            disabled={page === 0 || loading}
            onClick={() => loadProperties(page - 1)}
          >
            ← Anterior
          </button>
          <span style={{ display: 'flex', alignItems: 'center', padding: '0 1rem', color: '#94a3b8', fontSize: '0.9rem' }}>
            Página {page + 1} de {totalPages}
          </span>
          <button
            className="btn btn-secondary"
            disabled={page >= totalPages - 1 || loading}
            onClick={() => loadProperties(page + 1)}
          >
            Próxima →
          </button>
        </div>
      )}

      {/* Modal: Novo Imóvel */}
      <PropertyFormModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        onSubmit={handleCreateProperty}
      />

      {/* Modal: Editar Imóvel */}
      {editingProperty && (
        <PropertyFormModal
          isOpen={true}
          initialData={editingProperty}
          onClose={() => setEditingProperty(null)}
          onSubmit={handleUpdateProperty}
        />
      )}

      {/* Modal: Detalhes do Imóvel */}
      <PropertyDetailsModal
        property={viewingProperty}
        onClose={() => setViewingProperty(null)}
        onEdit={(p) => {
          setViewingProperty(null);
          setEditingProperty(p);
        }}
      />

      {/* Modal: Confirmar Exclusão */}
      <DeletePropertyModal
        property={deletingProperty}
        isOpen={Boolean(deletingProperty)}
        onClose={() => setDeletingProperty(null)}
        onConfirm={handleDeleteProperty}
      />
    </div>
  );
};
