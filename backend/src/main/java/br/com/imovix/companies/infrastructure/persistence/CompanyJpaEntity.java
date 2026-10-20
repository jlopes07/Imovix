package br.com.imovix.companies.infrastructure.persistence;

import br.com.imovix.companies.domain.Company;
import br.com.imovix.companies.domain.CompanyStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Entidade JPA para mapeamento da tabela imovix.companies. */
@Entity
@Table(name = "companies", schema = "imovix")
public class CompanyJpaEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "trade_name", length = 255)
  private String tradeName;

  @Column(name = "document_number", length = 32)
  private String documentNumber;

  @Column(name = "creci", length = 50)
  private String creci;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 50)
  private CompanyStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public CompanyJpaEntity() {}

  public static CompanyJpaEntity fromDomain(Company company) {
    CompanyJpaEntity entity = new CompanyJpaEntity();
    entity.id = company.getId();
    entity.name = company.getName();
    entity.tradeName = company.getTradeName();
    entity.documentNumber = company.getDocumentNumber();
    entity.creci = company.getCreci();
    entity.status = company.getStatus();
    entity.createdAt = company.getCreatedAt();
    entity.updatedAt = company.getUpdatedAt();
    return entity;
  }

  public Company toDomain() {
    return new Company(id, name, tradeName, documentNumber, creci, status, createdAt, updatedAt);
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getTradeName() {
    return tradeName;
  }

  public String getDocumentNumber() {
    return documentNumber;
  }

  public String getCreci() {
    return creci;
  }

  public CompanyStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
