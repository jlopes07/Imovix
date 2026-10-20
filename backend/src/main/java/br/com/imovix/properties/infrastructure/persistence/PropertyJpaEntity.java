package br.com.imovix.properties.infrastructure.persistence;

import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyAddress;
import br.com.imovix.properties.domain.PropertyDimensions;
import br.com.imovix.properties.domain.PropertyPricing;
import br.com.imovix.properties.domain.PropertyPurpose;
import br.com.imovix.properties.domain.PropertyStatus;
import br.com.imovix.properties.domain.PropertyType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Entidade JPA para mapeamento da tabela imovix.properties. */
@Entity
@Table(name = "properties", schema = "imovix")
public class PropertyJpaEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "tenant_id", nullable = false, updatable = false)
  private UUID tenantId;

  @Column(name = "code", nullable = false, length = 50)
  private String code;

  @Column(name = "title", nullable = false, length = 255)
  private String title;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 50)
  private PropertyType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "purpose", nullable = false, length = 50)
  private PropertyPurpose purpose;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 50)
  private PropertyStatus status;

  @Column(name = "sale_price", precision = 14, scale = 2)
  private BigDecimal salePrice;

  @Column(name = "rental_price", precision = 14, scale = 2)
  private BigDecimal rentalPrice;

  @Column(name = "condo_fee", precision = 14, scale = 2)
  private BigDecimal condoFee;

  @Column(name = "iptu", precision = 14, scale = 2)
  private BigDecimal iptu;

  @Column(name = "bedrooms")
  private Integer bedrooms;

  @Column(name = "bathrooms")
  private Integer bathrooms;

  @Column(name = "suites")
  private Integer suites;

  @Column(name = "parking_spaces")
  private Integer parkingSpaces;

  @Column(name = "usable_area", precision = 10, scale = 2)
  private BigDecimal usableArea;

  @Column(name = "total_area", precision = 10, scale = 2)
  private BigDecimal totalArea;

  @Column(name = "zip_code", length = 10)
  private String zipCode;

  @Column(name = "street", length = 255)
  private String street;

  @Column(name = "number", length = 30)
  private String number;

  @Column(name = "complement", length = 100)
  private String complement;

  @Column(name = "neighborhood", length = 100)
  private String neighborhood;

  @Column(name = "city", length = 100)
  private String city;

  @Column(name = "state", length = 2)
  private String state;

  @Column(name = "created_by")
  private UUID createdBy;

  @Column(name = "updated_by")
  private UUID updatedBy;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public PropertyJpaEntity() {}

  public static PropertyJpaEntity fromDomain(Property property) {
    PropertyJpaEntity entity = new PropertyJpaEntity();
    entity.id = property.getId();
    entity.tenantId = property.getTenantId();
    entity.code = property.getCode();
    entity.title = property.getTitle();
    entity.description = property.getDescription();
    entity.type = property.getType();
    entity.purpose = property.getPurpose();
    entity.status = property.getStatus();

    if (property.getPricing() != null) {
      entity.salePrice = property.getPricing().salePrice();
      entity.rentalPrice = property.getPricing().rentalPrice();
      entity.condoFee = property.getPricing().condoFee();
      entity.iptu = property.getPricing().iptu();
    }

    if (property.getDimensions() != null) {
      entity.bedrooms = property.getDimensions().bedrooms();
      entity.bathrooms = property.getDimensions().bathrooms();
      entity.suites = property.getDimensions().suites();
      entity.parkingSpaces = property.getDimensions().parkingSpaces();
      entity.usableArea = property.getDimensions().usableArea();
      entity.totalArea = property.getDimensions().totalArea();
    }

    if (property.getAddress() != null) {
      entity.zipCode = property.getAddress().zipCode();
      entity.street = property.getAddress().street();
      entity.number = property.getAddress().number();
      entity.complement = property.getAddress().complement();
      entity.neighborhood = property.getAddress().neighborhood();
      entity.city = property.getAddress().city();
      entity.state = property.getAddress().state();
    }

    entity.createdBy = property.getCreatedBy();
    entity.updatedBy = property.getUpdatedBy();
    entity.createdAt = property.getCreatedAt();
    entity.updatedAt = property.getUpdatedAt();
    return entity;
  }

  public Property toDomain() {
    PropertyPricing pricing = new PropertyPricing(salePrice, rentalPrice, condoFee, iptu);

    PropertyDimensions dimensions =
        new PropertyDimensions(bedrooms, bathrooms, suites, parkingSpaces, usableArea, totalArea);

    PropertyAddress address =
        PropertyAddress.of(zipCode, street, number, complement, neighborhood, city, state);

    return new Property(
        id,
        tenantId,
        code,
        title,
        description,
        type,
        purpose,
        status,
        pricing,
        dimensions,
        address,
        createdBy,
        updatedBy,
        createdAt,
        updatedAt);
  }

  public UUID getId() {
    return id;
  }

  public UUID getTenantId() {
    return tenantId;
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

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
