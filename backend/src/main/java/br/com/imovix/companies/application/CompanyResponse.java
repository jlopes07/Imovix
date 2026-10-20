package br.com.imovix.companies.application;

import br.com.imovix.companies.domain.Company;
import br.com.imovix.companies.domain.CompanyStatus;
import java.time.Instant;
import java.util.UUID;

/** DTO imutável de saída com os dados de uma empresa. */
public record CompanyResponse(
    UUID id,
    String name,
    String tradeName,
    String documentNumber,
    String creci,
    CompanyStatus status,
    Instant createdAt,
    Instant updatedAt) {

  public static CompanyResponse fromDomain(Company company) {
    return new CompanyResponse(
        company.getId(),
        company.getName(),
        company.getTradeName(),
        company.getDocumentNumber(),
        company.getCreci(),
        company.getStatus(),
        company.getCreatedAt(),
        company.getUpdatedAt());
  }
}
