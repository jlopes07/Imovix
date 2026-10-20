package br.com.imovix.companies.application;

import br.com.imovix.companies.domain.Company;
import br.com.imovix.companies.domain.CompanyNotFoundException;
import br.com.imovix.companies.domain.CompanyRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para obter os detalhes de uma empresa imobiliária. */
@Service
public class GetCompanyUseCase {

  private final CompanyRepository companyRepository;

  public GetCompanyUseCase(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  @Transactional(readOnly = true)
  public CompanyResponse execute(UUID companyId) {
    Company company =
        companyRepository
            .findById(companyId)
            .orElseThrow(() -> new CompanyNotFoundException(companyId));
    return CompanyResponse.fromDomain(company);
  }
}
