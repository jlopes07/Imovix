package br.com.imovix.properties.domain;

import br.com.imovix.tenancy.domain.TenantScoped;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Entidade de domínio representando um Imóvel cadastrado no sistema. */
public class Property implements TenantScoped {

  private final UUID id;
  private final UUID tenantId;
  private String code;
  private String title;
  private String description;
  private PropertyType type;
  private PropertyPurpose purpose;
  private PropertyStatus status;
  private PropertyPricing pricing;
  private PropertyDimensions dimensions;
  private PropertyAddress address;
  private UUID createdBy;
  private UUID updatedBy;
  private final Instant createdAt;
  private Instant updatedAt;

  public Property(
      UUID id,
      UUID tenantId,
      String code,
      String title,
      String description,
      PropertyType type,
      PropertyPurpose purpose,
      PropertyStatus status,
      PropertyPricing pricing,
      PropertyDimensions dimensions,
      PropertyAddress address,
      UUID createdBy,
      UUID updatedBy,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id != null ? id : UUID.randomUUID();
    this.tenantId = Objects.requireNonNull(tenantId, "O identificador do tenant é obrigatório.");
    this.code = validateCode(code);
    this.title = validateTitle(title);
    this.description = description != null ? description.trim() : null;
    this.type = Objects.requireNonNull(type, "O tipo do imóvel é obrigatório.");
    this.purpose = Objects.requireNonNull(purpose, "A finalidade do imóvel é obrigatória.");
    this.status = status != null ? status : PropertyStatus.AVAILABLE;
    this.pricing = pricing != null ? pricing : PropertyPricing.empty();
    this.dimensions = dimensions != null ? dimensions : PropertyDimensions.empty();
    this.address = address;
    this.createdBy = createdBy;
    this.updatedBy = updatedBy;
    Instant now = Instant.now();
    this.createdAt = createdAt != null ? createdAt : now;
    this.updatedAt = updatedAt != null ? updatedAt : now;
  }

  public static Property createNew(
      UUID tenantId,
      String code,
      String title,
      String description,
      PropertyType type,
      PropertyPurpose purpose,
      PropertyPricing pricing,
      PropertyDimensions dimensions,
      PropertyAddress address,
      UUID creatorUserId) {
    return new Property(
        null,
        tenantId,
        code,
        title,
        description,
        type,
        purpose,
        PropertyStatus.AVAILABLE,
        pricing,
        dimensions,
        address,
        creatorUserId,
        creatorUserId,
        null,
        null);
  }

  public void update(
      String code,
      String title,
      String description,
      PropertyType type,
      PropertyPurpose purpose,
      PropertyStatus status,
      PropertyPricing pricing,
      PropertyDimensions dimensions,
      PropertyAddress address,
      UUID updaterUserId) {
    this.code = validateCode(code);
    this.title = validateTitle(title);
    this.description = description != null ? description.trim() : null;
    this.type = Objects.requireNonNull(type, "O tipo do imóvel é obrigatório.");
    this.purpose = Objects.requireNonNull(purpose, "A finalidade do imóvel é obrigatória.");
    if (status != null) {
      this.status = status;
    }
    this.pricing = pricing != null ? pricing : PropertyPricing.empty();
    this.dimensions = dimensions != null ? dimensions : PropertyDimensions.empty();
    this.address = address;
    this.updatedBy = updaterUserId;
    this.updatedAt = Instant.now();
  }

  public void changeStatus(PropertyStatus newStatus, UUID updaterUserId) {
    this.status = Objects.requireNonNull(newStatus, "O novo status não pode ser nulo.");
    this.updatedBy = updaterUserId;
    this.updatedAt = Instant.now();
  }

  public void deactivate(UUID updaterUserId) {
    changeStatus(PropertyStatus.INACTIVE, updaterUserId);
  }

  public boolean isActive() {
    return this.status != PropertyStatus.INACTIVE;
  }

  private static String validateCode(String code) {
    if (code == null || code.isBlank()) {
      throw new IllegalArgumentException("O código interno do imóvel não pode ser vazio.");
    }
    return code.trim().toUpperCase();
  }

  private static String validateTitle(String title) {
    if (title == null || title.isBlank()) {
      throw new IllegalArgumentException("O título do anúncio do imóvel não pode ser vazio.");
    }
    return title.trim();
  }

  @Override
  public UUID getTenantId() {
    return tenantId;
  }

  public UUID getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public PropertyType getType() {
    return type;
  }

  public PropertyPurpose getPurpose() {
    return purpose;
  }

  public PropertyStatus getStatus() {
    return status;
  }

  public PropertyPricing getPricing() {
    return pricing;
  }

  public PropertyDimensions getDimensions() {
    return dimensions;
  }

  public PropertyAddress getAddress() {
    return address;
  }

  public UUID getCreatedBy() {
    return createdBy;
  }

  public UUID getUpdatedBy() {
    return updatedBy;
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
    Property property = (Property) o;
    return Objects.equals(id, property.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
