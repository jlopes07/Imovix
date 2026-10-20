export interface ProblemDetails {
  title?: string;
  detail?: string;
  status?: number;
  instance?: string;
  violations?: Record<string, string>;
}

export class ApiError extends Error {
  public status: number;
  public details: ProblemDetails;

  constructor(status: number, details: ProblemDetails) {
    super(details.detail || details.title || `Erro HTTP ${status}`);
    this.name = 'ApiError';
    this.status = status;
    this.details = details;
  }
}

const STORAGE_KEYS = {
  USER_ID: 'imovix_user_id',
  TENANT_ID: 'imovix_tenant_id',
  USER_DATA: 'imovix_user_data',
  COMPANY_DATA: 'imovix_company_data',
};

export const SessionStorageManager = {
  getUserId: (): string | null => localStorage.getItem(STORAGE_KEYS.USER_ID),
  setUserId: (userId: string) => localStorage.setItem(STORAGE_KEYS.USER_ID, userId),

  getTenantId: (): string | null => localStorage.getItem(STORAGE_KEYS.TENANT_ID),
  setTenantId: (tenantId: string) => localStorage.setItem(STORAGE_KEYS.TENANT_ID, tenantId),

  getUserData: <T>(): T | null => {
    const raw = localStorage.getItem(STORAGE_KEYS.USER_DATA);
    return raw ? JSON.parse(raw) : null;
  },
  setUserData: <T>(data: T) => localStorage.setItem(STORAGE_KEYS.USER_DATA, JSON.stringify(data)),

  getCompanyData: <T>(): T | null => {
    const raw = localStorage.getItem(STORAGE_KEYS.COMPANY_DATA);
    return raw ? JSON.parse(raw) : null;
  },
  setCompanyData: <T>(data: T) => localStorage.setItem(STORAGE_KEYS.COMPANY_DATA, JSON.stringify(data)),

  clear: () => {
    localStorage.removeItem(STORAGE_KEYS.USER_ID);
    localStorage.removeItem(STORAGE_KEYS.TENANT_ID);
    localStorage.removeItem(STORAGE_KEYS.USER_DATA);
    localStorage.removeItem(STORAGE_KEYS.COMPANY_DATA);
  },
};

export async function apiFetch<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const userId = SessionStorageManager.getUserId();
  const tenantId = SessionStorageManager.getTenantId();

  const headers = new Headers(options.headers || {});
  headers.set('Content-Type', 'application/json');

  if (userId) {
    headers.set('X-User-Id', userId);
  }
  if (tenantId) {
    headers.set('X-Tenant-Id', tenantId);
    headers.set('X-Company-Id', tenantId);
  }

  const response = await fetch(endpoint, {
    ...options,
    headers,
  });

  if (response.status === 204) {
    return {} as T;
  }

  const contentType = response.headers.get('content-type') || '';
  const isJson = contentType.includes('application/json') || contentType.includes('application/problem+json');

  if (!response.ok) {
    let problemDetails: ProblemDetails = {
      status: response.status,
      title: response.statusText,
    };

    if (isJson) {
      try {
        problemDetails = await response.json();
      } catch {
        // fallback
      }
    } else {
      const text = await response.text();
      problemDetails.detail = text;
    }

    throw new ApiError(response.status, problemDetails);
  }

  return isJson ? response.json() : ({} as T);
}
