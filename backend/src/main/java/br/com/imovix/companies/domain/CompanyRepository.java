package br.com.imovix.companies.domain;

import java.util.Optional;
import java.util.UUID;

/** Porta de persistência para o agregado {@link Company}. */
public interface CompanyRepository {

  Company save(Company company);

  Optional<Company> findById(UUID id);

  boolean existsByDocumentNumber(String documentNumber);
}
