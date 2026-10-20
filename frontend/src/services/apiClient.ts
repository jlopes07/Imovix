import type { ApiErrorResponse } from '../types';

/**
 * Cliente HTTP resiliente e tipado para a API REST Imovix (/api/v1).
 *
 * Características:
 *  - Envia X-Correlation-Id para rastreabilidade ponta a ponta.
 *  - Tratamento padronizado de erros e respostas JSON.
 *  - Suporte a credenciais HttpOnly e CORS seguro.
 */

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

function generateCorrelationId(): string {
  if (typeof crypto !== 'undefined' && crypto.randomUUID) {
    return crypto.randomUUID();
  }
  return `req-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;
}

export interface RequestOptions extends RequestInit {
  params?: Record<string, string | number | boolean | undefined>;
}

export class ApiClientError extends Error {
  readonly status: number;
  readonly response: ApiErrorResponse;
  readonly correlationId?: string;

  constructor(status: number, response: ApiErrorResponse, correlationId?: string) {
    super(response.message || `API error with status ${status}`);
    this.name = 'ApiClientError';
    this.status = status;
    this.response = response;
    this.correlationId = correlationId;
  }
}

export async function apiFetch<T>(endpoint: string, options: RequestOptions = {}): Promise<T> {
  const correlationId = generateCorrelationId();
  const url = new URL(endpoint.startsWith('/') ? `${API_BASE_URL}${endpoint}` : `${API_BASE_URL}/${endpoint}`, window.location.origin);

  if (options.params) {
    Object.entries(options.params).forEach(([key, value]) => {
      if (value !== undefined) {
        url.searchParams.append(key, String(value));
      }
    });
  }

  const headers = new Headers(options.headers);
  if (!headers.has('Content-Type') && !(options.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }
  headers.set('Accept', 'application/json');
  headers.set('X-Correlation-Id', correlationId);

  const response = await fetch(url.toString(), {
    ...options,
    headers,
  });

  if (!response.ok) {
    let errorData: ApiErrorResponse;
    try {
      errorData = await response.json();
    } catch {
      errorData = {
        status: response.status,
        message: response.statusText || 'Erro inesperado na comunicação com o servidor',
        timestamp: new Date().toISOString(),
        correlationId,
      };
    }
    throw new ApiClientError(response.status, errorData, correlationId);
  }

  if (response.status === 204) {
    return undefined as unknown as T;
  }

  return response.json() as Promise<T>;
}
