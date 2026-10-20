package br.com.imovix.listings.application;

import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyPurpose;
import br.com.imovix.properties.domain.PropertyType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO publico contendo apenas os dados autorizados para divulgacao externa. Por questoes de
 * privacidade e seguranca (LGPD), numero e complemento de endereco nao sao expostos.
 */
public record PublicListingResponse(
    UUID id,
    String code,
    String title,
    String description,
    PropertyType type,
    PropertyPurpose purpose,
    BigDecimal salePrice,
    BigDecimal rentalPrice,
    BigDecimal condoFee,
    BigDecimal iptu,
    Integer bedrooms,
    Integer bathrooms,
    Integer suites,
    Integer parkingSpaces,
    BigDecimal usableArea,
    BigDecimal totalArea,
    String neighborhood,
    String city,
    String state,
    Instant createdAt) {

  public static PublicListingResponse fromDomain(Property property) {
    var pricing = property.getPricing();
    var dimensions = property.getDimensions();
    var address = property.getAddress();

    return new PublicListingResponse(
        property.getId(),
        property.getCode(),
        property.getTitle(),
        property.getDescription(),
        property.getType(),
        property.getPurpose(),
        pricing != null ? pricing.salePrice() : null,
        pricing != null ? pricing.rentalPrice() : null,
        pricing != null ? pricing.condoFee() : null,
        pricing != null ? pricing.iptu() : null,
        dimensions != null ? dimensions.bedrooms() : null,
        dimensions != null ? dimensions.bathrooms() : null,
        dimensions != null ? dimensions.suites() : null,
        dimensions != null ? dimensions.parkingSpaces() : null,
        dimensions != null ? dimensions.usableArea() : null,
        dimensions != null ? dimensions.totalArea() : null,
        address != null ? address.neighborhood() : null,
        address != null ? address.city() : null,
        address != null ? address.state() : null,
        property.getCreatedAt());
  }
}
