import { apiFetch } from './api';
import type { PagedPropertiesResponse, Property, PropertyFormData } from '../types/property';

function parseNumber(value: string): number | null {
  if (!value || value.trim() === '') return null;
  const clean = value.replace(/\./g, '').replace(',', '.').replace(/[^0-9.-]/g, '');
  const parsed = parseFloat(clean);
  return isNaN(parsed) ? null : parsed;
}

function parseIntOrNull(value: string): number | null {
  if (!value || value.trim() === '') return null;
  const parsed = parseInt(value, 10);
  return isNaN(parsed) ? null : parsed;
}

export const PropertyService = {
  async list(page = 0, size = 20): Promise<PagedPropertiesResponse> {
    return await apiFetch<PagedPropertiesResponse>(`/api/v1/properties?page=${page}&size=${size}`);
  },

  async getById(id: string): Promise<Property> {
    return await apiFetch<Property>(`/api/v1/properties/${id}`);
  },

  async create(formData: PropertyFormData): Promise<Property> {
    const payload = {
      code: formData.code?.trim() || null,
      title: formData.title.trim(),
      description: formData.description.trim() || null,
      type: formData.type,
      purpose: formData.purpose,
      salePrice: parseNumber(formData.salePrice),
      rentalPrice: parseNumber(formData.rentalPrice),
      condoFee: parseNumber(formData.condoFee),
      iptu: parseNumber(formData.iptu),
      bedrooms: parseIntOrNull(formData.bedrooms),
      bathrooms: parseIntOrNull(formData.bathrooms),
      suites: parseIntOrNull(formData.suites),
      parkingSpaces: parseIntOrNull(formData.parkingSpaces),
      usableArea: parseNumber(formData.usableArea),
      totalArea: parseNumber(formData.totalArea),
      zipCode: formData.zipCode.trim() || null,
      street: formData.street.trim() || null,
      number: formData.number.trim() || null,
      complement: formData.complement.trim() || null,
      neighborhood: formData.neighborhood.trim() || null,
      city: formData.city.trim() || null,
      state: formData.state.trim().toUpperCase() || null,
    };

    return await apiFetch<Property>('/api/v1/properties', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  async update(id: string, formData: PropertyFormData): Promise<Property> {
    const payload = {
      code: formData.code?.trim() || '',
      title: formData.title.trim(),
      description: formData.description.trim() || null,
      type: formData.type,
      purpose: formData.purpose,
      status: formData.status || 'AVAILABLE',
      salePrice: parseNumber(formData.salePrice),
      rentalPrice: parseNumber(formData.rentalPrice),
      condoFee: parseNumber(formData.condoFee),
      iptu: parseNumber(formData.iptu),
      bedrooms: parseIntOrNull(formData.bedrooms),
      bathrooms: parseIntOrNull(formData.bathrooms),
      suites: parseIntOrNull(formData.suites),
      parkingSpaces: parseIntOrNull(formData.parkingSpaces),
      usableArea: parseNumber(formData.usableArea),
      totalArea: parseNumber(formData.totalArea),
      zipCode: formData.zipCode.trim() || null,
      street: formData.street.trim() || null,
      number: formData.number.trim() || null,
      complement: formData.complement.trim() || null,
      neighborhood: formData.neighborhood.trim() || null,
      city: formData.city.trim() || null,
      state: formData.state.trim().toUpperCase() || null,
    };

    return await apiFetch<Property>(`/api/v1/properties/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    });
  },

  async delete(id: string): Promise<void> {
    await apiFetch<void>(`/api/v1/properties/${id}`, {
      method: 'DELETE',
    });
  },
};
