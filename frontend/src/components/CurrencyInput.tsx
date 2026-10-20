import React from 'react';
import { maskBRL } from '../utils/currency';

interface CurrencyInputProps {
  id?: string;
  name: string;
  value: string | number | null | undefined;
  onChange: (value: string) => void;
  placeholder?: string;
  className?: string;
  disabled?: boolean;
}

export const CurrencyInput: React.FC<CurrencyInputProps> = ({
  id,
  name,
  value,
  onChange,
  placeholder = '0,00',
  className = 'form-control',
  disabled = false,
}) => {
  // Converte valor recebido para formato formatado
  const displayValue = React.useMemo(() => {
    if (value === null || value === undefined || value === '') return '';
    if (typeof value === 'number') {
      return value.toLocaleString('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
      });
    }
    // se já vier com vírgula ou dígitos
    return maskBRL(String(value));
  }, [value]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const rawVal = e.target.value;
    const formatted = maskBRL(rawVal);
    onChange(formatted);
  };

  return (
    <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
      <span
        style={{
          position: 'absolute',
          left: '0.85rem',
          color: '#10b981',
          fontWeight: 600,
          fontSize: '0.85rem',
          pointerEvents: 'none',
          userSelect: 'none',
        }}
      >
        R$
      </span>
      <input
        type="text"
        inputMode="numeric"
        id={id}
        name={name}
        value={displayValue}
        onChange={handleInputChange}
        placeholder={placeholder}
        disabled={disabled}
        className={className}
        style={{ paddingLeft: '2.5rem' }}
      />
    </div>
  );
};
