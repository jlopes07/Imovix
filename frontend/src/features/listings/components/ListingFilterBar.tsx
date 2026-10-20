import type { PropertyType } from '../../../types/property';
import type { ListingFilterParams } from '../../../services/listingService';

interface ListingFilterBarProps {
  filters: ListingFilterParams;
  onChange: (updated: ListingFilterParams) => void;
  onClear: () => void;
}

export function ListingFilterBar({ filters, onChange, onClear }: ListingFilterBarProps) {
  return (
    <div className="listing-filter-bar">
      {/* Abas de Finalidade */}
      <div className="purpose-segmented-control">
        <button
          type="button"
          className={`seg-btn ${!filters.purpose ? 'active' : ''}`}
          onClick={() => onChange({ ...filters, purpose: undefined, page: 0 })}
        >
          Todos os Imoveis
        </button>
        <button
          type="button"
          className={`seg-btn ${filters.purpose === 'SALE' ? 'active' : ''}`}
          onClick={() => onChange({ ...filters, purpose: 'SALE', page: 0 })}
        >
          Comprar
        </button>
        <button
          type="button"
          className={`seg-btn ${filters.purpose === 'RENT' ? 'active' : ''}`}
          onClick={() => onChange({ ...filters, purpose: 'RENT', page: 0 })}
        >
          Alugar
        </button>
      </div>

      {/* Inputs de Filtro */}
      <div className="filter-inputs-grid">
        <div className="filter-field">
          <label htmlFor="filter-type">Tipo de Imovel</label>
          <select
            id="filter-type"
            value={filters.type || ''}
            onChange={(e) =>
              onChange({
                ...filters,
                type: (e.target.value as PropertyType) || undefined,
                page: 0,
              })
            }
          >
            <option value="">Todos os Tipos</option>
            <option value="APARTMENT">Apartamento</option>
            <option value="HOUSE">Casa</option>
            <option value="LAND">Terreno</option>
            <option value="COMMERCIAL">Comercial</option>
            <option value="RURAL">Rural</option>
          </select>
        </div>

        <div className="filter-field">
          <label htmlFor="filter-city">Cidade ou Regiao</label>
          <input
            id="filter-city"
            type="text"
            placeholder="Ex: Sao Paulo, Campinas..."
            value={filters.city || ''}
            onChange={(e) =>
              onChange({
                ...filters,
                city: e.target.value || undefined,
                page: 0,
              })
            }
          />
        </div>

        <div className="filter-field">
          <label htmlFor="filter-bedrooms">Quartos (Minimo)</label>
          <select
            id="filter-bedrooms"
            value={filters.minBedrooms !== undefined ? String(filters.minBedrooms) : ''}
            onChange={(e) =>
              onChange({
                ...filters,
                minBedrooms: e.target.value ? parseInt(e.target.value, 10) : undefined,
                page: 0,
              })
            }
          >
            <option value="">Qualquer</option>
            <option value="1">1+ quarto</option>
            <option value="2">2+ quartos</option>
            <option value="3">3+ quartos</option>
            <option value="4">4+ quartos</option>
          </select>
        </div>

        <div className="filter-actions-col">
          <button type="button" className="btn btn-secondary btn-clear-filter" onClick={onClear}>
            Limpar Filtros
          </button>
        </div>
      </div>
    </div>
  );
}
