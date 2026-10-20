import type { PropertyPurpose, PropertyType } from './property';

export interface PublicListing {
  id: string;
  code: string;
  title: string;
  description: string | null;
  type: PropertyType;
  purpose: PropertyPurpose;
  salePrice: number | null;
  rentalPrice: number | null;
  condoFee: number | null;
  iptu: number | null;
  bedrooms: number | null;
  bathrooms: number | null;
  suites: number | null;
  parkingSpaces: number | null;
  usableArea: number | null;
  totalArea: number | null;
  neighborhood: string | null;
  city: string | null;
  state: string | null;
  createdAt: string;
}

export interface PagedPublicListings {
  content: PublicListing[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface SubmitInquiryPayload {
  name: string;
  email?: string;
  phone: string;
  message?: string;
  lgpdConsent: boolean;
}

export interface InquiryConfirmation {
  leadId: string;
  message: string;
  receivedAt: string;
}
