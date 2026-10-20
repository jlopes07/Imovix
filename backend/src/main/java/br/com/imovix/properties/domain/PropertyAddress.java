package br.com.imovix.properties.domain;

/** Value Object imutável representando o endereço de um imóvel. */
public record PropertyAddress(
    String zipCode,
    String street,
    String number,
    String complement,
    String neighborhood,
    String city,
    String state) {

  public static PropertyAddress of(
      String zipCode,
      String street,
      String number,
      String complement,
      String neighborhood,
      String city,
      String state) {
    return new PropertyAddress(
        clean(zipCode),
        clean(street),
        clean(number),
        clean(complement),
        clean(neighborhood),
        clean(city),
        state != null ? state.trim().toUpperCase() : null);
  }

  private static String clean(String value) {
    return value != null && !value.isBlank() ? value.trim() : null;
  }
}
