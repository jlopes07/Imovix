package br.com.imovix.companies.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositório Spring Data JPA para a entidade {@link CompanyJpaEntity}. */
@Repository
public interface SpringDataCompanyRepository extends JpaRepository<CompanyJpaEntity, UUID> {

  boolean existsByDocumentNumber(String documentNumber);
}
