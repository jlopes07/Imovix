import type { PublicListing } from '../../../types/listing';

interface ListingCardProps {
  listing: PublicListing;
  onInquire: (listing: PublicListing) => void;
}

const TYPE_LABELS: Record<string, string> = {
  APARTMENT: 'Apartamento',
  HOUSE: 'Casa',
  LAND: 'Terreno',
  COMMERCIAL: 'Comercial',
  RURAL: 'Rural',
};

export function ListingCard({ listing, onInquire }: ListingCardProps) {
  const formatPrice = (val: number | null) => {
    if (!val) return 'Sob Consulta';
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL',
      maximumFractionDigits: 0,
    }).format(val);
  };

  const isRent = listing.purpose === 'RENT';
  const price = isRent ? listing.rentalPrice : listing.salePrice;

  return (
    <div className="listing-card">
      <div className="listing-card-media-placeholder">
        <div className="listing-badge-type">{TYPE_LABELS[listing.type] || listing.type}</div>
        <div className={`listing-badge-purpose ${listing.purpose.toLowerCase()}`}>
          {isRent ? 'Locacao' : 'Venda'}
        </div>
        <div className="listing-code-overlay">{listing.code}</div>
      </div>

      <div className="listing-card-content">
        <div className="listing-price-row">
          <span className="listing-main-price">
            {formatPrice(price)}
            {isRent && <span className="price-period">/mes</span>}
          </span>
          {listing.condoFee && (
            <span className="listing-condo-fee">
              Cond: {formatPrice(listing.condoFee)}
            </span>
          )}
        </div>

        <h3 className="listing-title" title={listing.title}>
          {listing.title}
        </h3>

        <div className="listing-location">
          {listing.neighborhood ? `${listing.neighborhood}, ` : ''}
          {listing.city || 'Localizacao nao informada'}
          {listing.state ? ` - ${listing.state}` : ''}
        </div>

        {/* Resumo de Comodos e Metragem */}
        <div className="listing-specs-row">
          {listing.usableArea && (
            <div className="spec-item">
              <span className="spec-val">{listing.usableArea} m2</span>
            </div>
          )}
          {listing.bedrooms !== null && listing.bedrooms !== undefined && (
            <div className="spec-item">
              <span className="spec-val">{listing.bedrooms} quarto(s)</span>
            </div>
          )}
          {listing.suites !== null && listing.suites !== undefined && listing.suites > 0 && (
            <div className="spec-item">
              <span className="spec-val">{listing.suites} suite(s)</span>
            </div>
          )}
          {listing.parkingSpaces !== null && listing.parkingSpaces !== undefined && (
            <div className="spec-item">
              <span className="spec-val">{listing.parkingSpaces} vaga(s)</span>
            </div>
          )}
        </div>

        <div className="listing-card-footer">
          <button
            type="button"
            className="btn btn-primary btn-inquire"
            onClick={() => onInquire(listing)}
          >
            Tenho Interesse
          </button>
        </div>
      </div>
    </div>
  );
}
