package br.com.imovix.companies.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Porta de persistência para a entidade {@link CompanyMembership}. */
public interface CompanyMembershipRepository {

  CompanyMembership save(CompanyMembership membership);

  Optional<CompanyMembership> findByCompanyIdAndUserId(UUID companyId, UUID userId);

  List<CompanyMembership> findByCompanyId(UUID companyId);

  List<CompanyMembership> findByUserId(UUID userId);

  boolean existsByCompanyIdAndUserId(UUID companyId, UUID userId);
}
