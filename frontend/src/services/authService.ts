import { apiFetch, SessionStorageManager } from './api';
import type { User, Company } from '../types/tenant';

export const AuthService = {
  async registerUser(name: string, email: string, password = 'Password@123'): Promise<User> {
    const user = await apiFetch<User>('/api/v1/users', {
      method: 'POST',
      body: JSON.stringify({ name, email, password }),
    });
    SessionStorageManager.setUserId(user.id);
    SessionStorageManager.setUserData(user);
    return user;
  },

  async createCompany(
    name: string,
    tradeName: string,
    documentNumber: string,
    creci: string
  ): Promise<Company> {
    const company = await apiFetch<Company>('/api/v1/companies', {
      method: 'POST',
      body: JSON.stringify({ name, tradeName, documentNumber, creci }),
    });
    SessionStorageManager.setTenantId(company.id);
    SessionStorageManager.setCompanyData(company);
    return company;
  },

  async getCurrentCompany(): Promise<Company> {
    return await apiFetch<Company>('/api/v1/companies/current');
  },

  getCurrentSession(): { user: User | null; company: Company | null } {
    return {
      user: SessionStorageManager.getUserData<User>(),
      company: SessionStorageManager.getCompanyData<Company>(),
    };
  },

  async bootstrapDemoSession(): Promise<{ user: User; company: Company }> {
    // 1. Tenta usar a sessão já salva no localStorage
    const savedUser = SessionStorageManager.getUserData<User>();
    const savedCompany = SessionStorageManager.getCompanyData<Company>();

    if (savedUser && savedCompany) {
      SessionStorageManager.setUserId(savedUser.id);
      SessionStorageManager.setTenantId(savedCompany.id);
      return { user: savedUser, company: savedCompany };
    }

    // 2. Cria usuário e empresa demo na base de dados
    const randomSuffix = Math.floor(Math.random() * 9000 + 1000);
    const demoEmail = `corretor.${randomSuffix}@imovix.com.br`;

    const user = await this.registerUser('Corretor Imovix Master', demoEmail);
    const company = await this.createCompany(
      `Imovix Prime Imóveis ${randomSuffix} Ltda`,
      'Imovix Prime Imóveis',
      `45.678.${Math.floor(Math.random() * 900 + 100)}/0001-90`,
      `${randomSuffix}-J`
    );

    return { user, company };
  },
};
