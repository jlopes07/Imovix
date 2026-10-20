export interface User {
  id: string;
  email: string;
  name: string;
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;
  updatedAt: string;
}

export interface Company {
  id: string;
  name: string;
  tradeName?: string | null;
  documentNumber?: string | null;
  creci?: string | null;
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;
  updatedAt: string;
}
