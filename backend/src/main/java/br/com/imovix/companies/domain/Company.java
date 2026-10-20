package br.com.imovix.companies.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Entidade de domínio representando uma Empresa / Imobiliária cadastrada na plataforma. */
public class Company {

  private final UUID id;
  private String name;
  private String tradeName;
  private String documentNumber;
  private String creci;
  private CompanyStatus status;
  private final Instant createdAt;
  private Instant updatedAt;

  public Company(
      UUID id,
      String name,
      String tradeName,
      String documentNumber,
      String creci,
      CompanyStatus status,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id != null ? id : UUID.randomUUID();
    this.name = validateName(name);
    this.tradeName = tradeName != null ? tradeName.trim() : null;
    this.documentNumber = cleanDocumentNumber(documentNumber);
    this.creci = creci != null ? creci.trim() : null;
    this.status = status != null ? status : CompanyStatus.ACTIVE;
    Instant now = Instant.now();
    this.createdAt = createdAt != null ? createdAt : now;
    this.updatedAt = updatedAt != null ? updatedAt : now;
  }

  public static Company createNew(
      String name, String tradeName, String documentNumber, String creci) {
    return new Company(
        null, name, tradeName, documentNumber, creci, CompanyStatus.ACTIVE, null, null);
  }

  private static String validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("O nome ou razão social da empresa não pode ser vazio.");
    }
    return name.trim();
  }

  public static String cleanDocumentNumber(String documentNumber) {
    if (documentNumber == null || documentNumber.isBlank()) {
      return null;
    }
    // Remove pontuação de CNPJ/CPF deixando apenas dígitos
    return documentNumber.replaceAll("\\D", "");
  }

  public void updateDetails(String name, String tradeName, String documentNumber, String creci) {
    this.name = validateName(name);
    this.tradeName = tradeName != null ? tradeName.trim() : null;
    this.documentNumber = cleanDocumentNumber(documentNumber);
    this.creci = creci != null ? creci.trim() : null;
    this.updatedAt = Instant.now();
  }

  public void deactivate() {
    this.status = CompanyStatus.INACTIVE;
    this.updatedAt = Instant.now();
  }

  public void activate() {
    this.status = CompanyStatus.ACTIVE;
    this.updatedAt = Instant.now();
  }

  public boolean isActive() {
    return this.status == CompanyStatus.ACTIVE;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Company company = (Company) o;
    return Objects.equals(id, company.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
