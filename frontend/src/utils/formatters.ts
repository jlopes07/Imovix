export function formatCurrency(value?: number | null): string {
  if (value === undefined || value === null) return '—';
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
    minimumFractionDigits: 2,
  }).format(value);
}

export function formatArea(value?: number | null): string {
  if (value === undefined || value === null) return '—';
  return `${value.toLocaleString('pt-BR', { minimumFractionDigits: 0, maximumFractionDigits: 2 })} m²`;
}

export function formatZipCode(zip?: string | null): string {
  if (!zip) return '';
  const digits = zip.replace(/\D/g, '');
  if (digits.length === 8) {
    return `${digits.slice(0, 5)}-${digits.slice(5)}`;
  }
  return zip;
}
