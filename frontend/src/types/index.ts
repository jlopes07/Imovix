/**
 * Tipos base do domínio Imovix (compartilhados com o contrato da API REST v1).
 */

export type Role = 'ADMIN' | 'MANAGER' | 'REALTOR' | 'OPERATOR' | 'PLATFORM_ADMIN';

export interface UserProfile {
  id: string;
  name: string;
  email: string;
  status: 'ACTIVE' | 'INVITED' | 'SUSPENDED';
  activeCompanyId?: string;
  role?: Role;
}

export interface Company {
  id: string;
  name: string;
  documentNumber?: string;
  creci?: string;
  status: 'ACTIVE' | 'TRIAL' | 'SUSPENDED';
  planTier: 'STARTER' | 'PROFESSIONAL' | 'ENTERPRISE';
  createdAt: string;
}

export type PropertyType = 'APARTMENT' | 'HOUSE' | 'COMMERCIAL' | 'LAND' | 'PENTHOUSE';
export type PropertyPurpose = 'SALE' | 'RENT' | 'BOTH';
export type PropertyStatus = 'DRAFT' | 'AVAILABLE' | 'RESERVED' | 'SOLD' | 'RENTED' | 'INACTIVE';

export interface Property {
  id: string;
  tenantId: string;
  code: string;
  title: string;
  description: string;
  type: PropertyType;
  purpose: PropertyPurpose;
  status: PropertyStatus;
  price: number;
  usableAreaM2: number;
  bedrooms: number;
  bathrooms: number;
  parkingSpaces: number;
  city: string;
  state: string;
  neighborhood: string;
  coverImageUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ApiErrorResponse {
  status: number;
  message: string;
  correlationId?: string;
  timestamp: string;
  errors?: Record<string, string>;
}
