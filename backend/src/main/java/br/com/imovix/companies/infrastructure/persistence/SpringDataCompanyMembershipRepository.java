package br.com.imovix.companies.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositório Spring Data JPA para a entidade {@link CompanyMembershipJpaEntity}. */
@Repository
public interface SpringDataCompanyMembershipRepository
    extends JpaRepository<CompanyMembershipJpaEntity, UUID> {

  Optional<CompanyMembershipJpaEntity> findByCompanyIdAndUserId(UUID companyId, UUID userId);

  List<CompanyMembershipJpaEntity> findByCompanyId(UUID companyId);

  List<CompanyMembershipJpaEntity> findByUserId(UUID userId);

  boolean existsByCompanyIdAndUserId(UUID companyId, UUID userId);
}
