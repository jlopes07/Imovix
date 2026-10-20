/**
 * Utilitários para formatação e manipulação de Moeda em Real Brasileiro (BRL / R$)
 */

export function formatBRL(value?: number | null): string {
  if (value === undefined || value === null) return '';
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(value);
}

export function parseBRL(value: string): number | null {
  if (!value || value.trim() === '') return null;
  // Remove tudo que não for dígito e vírgula/ponto
  const clean = value
    .replace(/[R$\s]/g, '')
    .replace(/\./g, '')
    .replace(',', '.');

  const parsed = parseFloat(clean);
  return isNaN(parsed) ? null : parsed;
}

/**
 * Aplica máscara bancária estilo calculadora:
 * Ao digitar dígitos, preenche da direita para a esquerda nos centavos.
 * Exemplo: "100" -> "1,00" | "125050" -> "1.250,50"
 */
export function maskBRL(inputValue: string): string {
  const digits = inputValue.replace(/\D/g, '');
  if (!digits) return '';

  const cents = parseInt(digits, 10);
  if (isNaN(cents)) return '';

  const floatValue = cents / 100;
  return floatValue.toLocaleString('pt-BR', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
}
