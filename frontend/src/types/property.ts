export type PropertyType = 'APARTMENT' | 'HOUSE' | 'LAND' | 'COMMERCIAL' | 'RURAL';

export type PropertyPurpose = 'SALE' | 'RENT' | 'BOTH';

export type PropertyStatus = 'AVAILABLE' | 'UNDER_NEGOTIATION' | 'SOLD' | 'RENTED' | 'INACTIVE';

export interface PropertyPricing {
  salePrice?: number | null;
  rentalPrice?: number | null;
  condoFee?: number | null;
  iptu?: number | null;
}

export interface PropertyDimensions {
  bedrooms?: number | null;
  bathrooms?: number | null;
  suites?: number | null;
  parkingSpaces?: number | null;
  usableArea?: number | null;
  totalArea?: number | null;
}

export interface PropertyAddress {
  zipCode?: string | null;
  street?: string | null;
  number?: string | null;
  complement?: string | null;
  neighborhood?: string | null;
  city?: string | null;
  state?: string | null;
}

export interface Property {
  id: string;
  tenantId: string;
  code: string;
  title: string;
  description?: string | null;
  type: PropertyType;
  purpose: PropertyPurpose;
  status: PropertyStatus;
  pricing: PropertyPricing;
  dimensions: PropertyDimensions;
  address: PropertyAddress;
  createdBy?: string | null;
  updatedBy?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PagedPropertiesResponse {
  items: Property[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface PropertyFormData {
  code: string;
  title: string;
  description: string;
  type: PropertyType;
  purpose: PropertyPurpose;
  status?: PropertyStatus;
  salePrice: string;
  rentalPrice: string;
  condoFee: string;
  iptu: string;
  bedrooms: string;
  bathrooms: string;
  suites: string;
  parkingSpaces: string;
  usableArea: string;
  totalArea: string;
  zipCode: string;
  street: string;
  number: string;
  complement: string;
  neighborhood: string;
  city: string;
  state: string;
}

export const PROPERTY_TYPE_LABELS: Record<PropertyType, { label: string; icon: string }> = {
  APARTMENT: { label: 'Apartamento', icon: '🏢' },
  HOUSE: { label: 'Casa', icon: '🏡' },
  LAND: { label: 'Terreno', icon: '📐' },
  COMMERCIAL: { label: 'Comercial', icon: '🏬' },
  RURAL: { label: 'Rural / Fazenda', icon: '🌾' },
};

export const PROPERTY_PURPOSE_LABELS: Record<PropertyPurpose, { label: string; badgeClass: string }> = {
  SALE: { label: 'Venda', badgeClass: 'purpose-sale' },
  RENT: { label: 'Locação', badgeClass: 'purpose-rent' },
  BOTH: { label: 'Venda & Locação', badgeClass: 'purpose-both' },
};

export const PROPERTY_STATUS_LABELS: Record<PropertyStatus, { label: string; badgeClass: string }> = {
  AVAILABLE: { label: 'Disponível', badgeClass: 'status-available' },
  UNDER_NEGOTIATION: { label: 'Em Negociação', badgeClass: 'status-negotiation' },
  SOLD: { label: 'Vendido', badgeClass: 'status-sold' },
  RENTED: { label: 'Locado', badgeClass: 'status-rented' },
  INACTIVE: { label: 'Inativo', badgeClass: 'status-inactive' },
};
