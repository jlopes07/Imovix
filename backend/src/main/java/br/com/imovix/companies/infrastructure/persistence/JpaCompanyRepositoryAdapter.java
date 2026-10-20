package br.com.imovix.companies.infrastructure.persistence;

import br.com.imovix.companies.domain.Company;
import br.com.imovix.companies.domain.CompanyRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Adaptador de persistência que implementa {@link CompanyRepository} usando Spring Data JPA. */
@Component
public class JpaCompanyRepositoryAdapter implements CompanyRepository {

  private final SpringDataCompanyRepository springDataCompanyRepository;

  public JpaCompanyRepositoryAdapter(SpringDataCompanyRepository springDataCompanyRepository) {
    this.springDataCompanyRepository = springDataCompanyRepository;
  }

  @Override
  public Company save(Company company) {
    CompanyJpaEntity entity = CompanyJpaEntity.fromDomain(company);
    CompanyJpaEntity saved = springDataCompanyRepository.save(entity);
    return saved.toDomain();
  }

  @Override
  public Optional<Company> findById(UUID id) {
    return springDataCompanyRepository.findById(id).map(CompanyJpaEntity::toDomain);
  }

  @Override
  public boolean existsByDocumentNumber(String documentNumber) {
    return springDataCompanyRepository.existsByDocumentNumber(documentNumber);
  }
}
