package br.com.imovix.properties.domain;

import java.math.BigDecimal;

/** Value Object imutável representando cômodos e metragens de um imóvel. */
public record PropertyDimensions(
    Integer bedrooms,
    Integer bathrooms,
    Integer suites,
    Integer parkingSpaces,
    BigDecimal usableArea,
    BigDecimal totalArea) {

  public PropertyDimensions {
    validateNonNegative(bedrooms, "quartos");
    validateNonNegative(bathrooms, "banheiros");
    validateNonNegative(suites, "suítes");
    validateNonNegative(parkingSpaces, "vagas de garagem");
    validateNonNegative(usableArea, "área útil");
    validateNonNegative(totalArea, "área total");
  }

  private static void validateNonNegative(Integer value, String fieldName) {
    if (value != null && value < 0) {
      throw new IllegalArgumentException(
          String.format("A quantidade de %s não pode ser negativa.", fieldName));
    }
  }

  private static void validateNonNegative(BigDecimal value, String fieldName) {
    if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException(
          String.format("A metragem de %s não pode ser negativa.", fieldName));
    }
  }

  public static PropertyDimensions empty() {
    return new PropertyDimensions(null, null, null, null, null, null);
  }
}
