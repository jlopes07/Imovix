export interface ViaCepResponse {
  cep: string;
  logradouro: string;
  complemento: string;
  bairro: string;
  localidade: string;
  uf: string;
  ibge?: string;
  gia?: string;
  ddd?: string;
  siafi?: string;
  erro?: boolean | string;
}

export interface AddressLookupResult {
  zipCode: string;
  street: string;
  neighborhood: string;
  city: string;
  state: string;
  complement?: string;
}

export const CepService = {
  async lookup(rawCep: string): Promise<AddressLookupResult | null> {
    const cleanCep = rawCep.replace(/\D/g, '');

    if (cleanCep.length !== 8) {
      return null;
    }

    try {
      const response = await fetch(`https://viacep.com.br/ws/${cleanCep}/json/`);
      if (!response.ok) {
        return null;
      }

      const data: ViaCepResponse = await response.json();

      if (data.erro === true || data.erro === 'true') {
        return null;
      }

      return {
        zipCode: data.cep || cleanCep,
        street: data.logradouro || '',
        neighborhood: data.bairro || '',
        city: data.localidade || '',
        state: data.uf || '',
        complement: data.complemento || '',
      };
    } catch (err) {
      console.warn('Erro ao consultar ViaCEP:', err);
      return null;
    }
  },
};
