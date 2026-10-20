import { useState, useEffect, useCallback } from 'react';
import './listings.css';
import type { PublicListing } from '../../types/listing';
import { ListingService, type ListingFilterParams } from '../../services/listingService';
import { ListingCard } from './components/ListingCard';
import { ListingFilterBar } from './components/ListingFilterBar';
import { InquiryModal } from './components/InquiryModal';

export function PublicCatalogPage() {
  const [listings, setListings] = useState<PublicListing[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [filters, setFilters] = useState<ListingFilterParams>({
    page: 0,
    size: 12,
  });

  const [selectedListing, setSelectedListing] = useState<PublicListing | null>(null);

  const loadListings = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      const res = await ListingService.listPublic(filters);
      setListings(res.content);
      setTotalElements(res.totalElements);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Ocorreu um erro ao carregar os imoveis disponiveis.');
      }
    } finally {
      setIsLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    loadListings();
  }, [loadListings]);

  const handleClearFilters = () => {
    setFilters({
      page: 0,
      size: 12,
    });
  };

  return (
    <div className="public-catalog-container">
      {/* Hero Banner Comercial */}
      <section className="public-hero">
        <div className="hero-tagline">Portal Publico de Imoveis</div>
        <h1 className="hero-main-title">Encontre o Imovel Ideal para Voce</h1>
        <p className="hero-desc">
          Explore nossa selecao exclusiva de apartamentos, casas e terrenos para compra e locacao. Agende visitas diretamente com nossos corretores autorizados.
        </p>
      </section>

      {/* Barra de Filtros */}
      <ListingFilterBar
        filters={filters}
        onChange={(updated) => setFilters(updated)}
        onClear={handleClearFilters}
      />

      {/* Cabecalho de Resultados */}
      <div className="listings-results-header">
        <span className="listings-results-count">
          {isLoading
            ? 'Buscando imoveis disponiveis...'
            : `${totalElements} imoveis encontrados`}
        </span>
      </div>

      {error && <div className="alert-box error-alert">{error}</div>}

      {/* Grid de Imoveis */}
      {isLoading ? (
        <div className="loading-state">Carregando catálogo de imóveis...</div>
      ) : listings.length === 0 ? (
        <div className="empty-state-box">
          <h3>Nenhum imovel encontrado com os filtros selecionados</h3>
          <p>Tente ajustar os critérios de busca ou limpar os filtros para ver todas as opções disponíveis.</p>
          <button type="button" className="btn btn-secondary" onClick={handleClearFilters}>
            Ver Todos os Imoveis
          </button>
        </div>
      ) : (
        <div className="listings-grid">
          {listings.map((item) => (
            <ListingCard
              key={item.id}
              listing={item}
              onInquire={(listing) => setSelectedListing(listing)}
            />
          ))}
        </div>
      )}

      {/* Modal de Interesse / Captura de Lead */}
      {selectedListing && (
        <InquiryModal
          isOpen={Boolean(selectedListing)}
          onClose={() => setSelectedListing(null)}
          listing={selectedListing}
        />
      )}
    </div>
  );
}
