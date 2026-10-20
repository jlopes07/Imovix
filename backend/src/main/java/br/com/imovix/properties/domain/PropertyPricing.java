package br.com.imovix.properties.domain;

import java.math.BigDecimal;

/** Value Object imutável representando a precificação e encargos de um imóvel. */
public record PropertyPricing(
    BigDecimal salePrice, BigDecimal rentalPrice, BigDecimal condoFee, BigDecimal iptu) {

  public PropertyPricing {
    validateNonNegative(salePrice, "preço de venda");
    validateNonNegative(rentalPrice, "preço de locação");
    validateNonNegative(condoFee, "taxa de condomínio");
    validateNonNegative(iptu, "valor de IPTU");
  }

  private static void validateNonNegative(BigDecimal value, String fieldName) {
    if (value != null && value.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException(
          String.format("O %s não pode conter um valor negativo.", fieldName));
    }
  }

  public static PropertyPricing empty() {
    return new PropertyPricing(null, null, null, null);
  }
}
