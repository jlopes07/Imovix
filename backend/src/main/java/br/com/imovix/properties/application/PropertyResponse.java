package br.com.imovix.properties.application;

import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyAddress;
import br.com.imovix.properties.domain.PropertyDimensions;
import br.com.imovix.properties.domain.PropertyPricing;
import br.com.imovix.properties.domain.PropertyPurpose;
import br.com.imovix.properties.domain.PropertyStatus;
import br.com.imovix.properties.domain.PropertyType;
import java.time.Instant;
import java.util.UUID;

/** DTO imutável de saída contendo os detalhes de um imóvel. */
public record PropertyResponse(
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

  public static PropertyResponse fromDomain(Property property) {
    return new PropertyResponse(
        property.getId(),
        property.getTenantId(),
        property.getCode(),
        property.getTitle(),
        property.getDescription(),
        property.getType(),
        property.getPurpose(),
        property.getStatus(),
        property.getPricing(),
        property.getDimensions(),
        property.getAddress(),
        property.getCreatedBy(),
        property.getUpdatedBy(),
        property.getCreatedAt(),
        property.getUpdatedAt());
  }
}
