package br.com.imovix.companies.infrastructure.persistence;

import br.com.imovix.companies.domain.CompanyMembership;
import br.com.imovix.companies.domain.CompanyMembershipRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Adaptador que implementa {@link CompanyMembershipRepository} usando Spring Data JPA. */
@Component
public class JpaCompanyMembershipRepositoryAdapter implements CompanyMembershipRepository {

  private final SpringDataCompanyMembershipRepository repository;

  public JpaCompanyMembershipRepositoryAdapter(SpringDataCompanyMembershipRepository repository) {
    this.repository = repository;
  }

  @Override
  public CompanyMembership save(CompanyMembership membership) {
    CompanyMembershipJpaEntity entity = CompanyMembershipJpaEntity.fromDomain(membership);
    CompanyMembershipJpaEntity saved = repository.save(entity);
    return saved.toDomain();
  }

  @Override
  public Optional<CompanyMembership> findByCompanyIdAndUserId(UUID companyId, UUID userId) {
    return repository
        .findByCompanyIdAndUserId(companyId, userId)
        .map(CompanyMembershipJpaEntity::toDomain);
  }

  @Override
  public List<CompanyMembership> findByCompanyId(UUID companyId) {
    return repository.findByCompanyId(companyId).stream()
        .map(CompanyMembershipJpaEntity::toDomain)
        .toList();
  }

  @Override
  public List<CompanyMembership> findByUserId(UUID userId) {
    return repository.findByUserId(userId).stream()
        .map(CompanyMembershipJpaEntity::toDomain)
        .toList();
  }

  @Override
  public boolean existsByCompanyIdAndUserId(UUID companyId, UUID userId) {
    return repository.existsByCompanyIdAndUserId(companyId, userId);
  }
}
