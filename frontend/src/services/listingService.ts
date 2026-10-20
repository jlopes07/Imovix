import { apiFetch } from './api';
import type {
  InquiryConfirmation,
  PagedPublicListings,
  PublicListing,
  SubmitInquiryPayload,
} from '../types/listing';
import type { PropertyPurpose, PropertyType } from '../types/property';

export interface ListingFilterParams {
  type?: PropertyType;
  purpose?: PropertyPurpose;
  city?: string;
  maxPrice?: number;
  minBedrooms?: number;
  page?: number;
  size?: number;
}

export const ListingService = {
  async listPublic(params: ListingFilterParams = {}): Promise<PagedPublicListings> {
    const query = new URLSearchParams();
    if (params.page !== undefined) query.append('page', params.page.toString());
    if (params.size !== undefined) query.append('size', params.size.toString());
    if (params.type) query.append('type', params.type);
    if (params.purpose) query.append('purpose', params.purpose);
    if (params.city) query.append('city', params.city);
    if (params.maxPrice !== undefined) query.append('maxPrice', params.maxPrice.toString());
    if (params.minBedrooms !== undefined) query.append('minBedrooms', params.minBedrooms.toString());

    return await apiFetch<PagedPublicListings>(`/api/v1/public/listings?${query.toString()}`);
  },

  async getPublicById(id: string): Promise<PublicListing> {
    return await apiFetch<PublicListing>(`/api/v1/public/listings/${id}`);
  },

  async submitInquiry(propertyId: string, payload: SubmitInquiryPayload): Promise<InquiryConfirmation> {
    return await apiFetch<InquiryConfirmation>(`/api/v1/public/listings/${propertyId}/inquire`, {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },
};
