package br.com.imovix.tenancy.domain;

import java.util.Objects;
import java.util.UUID;

/** Value Object representando o identificador imutável de um Tenant (Empresa / Imobiliária). */
public record TenantId(UUID value) {

  public TenantId {
    Objects.requireNonNull(value, "O valor do identificador do tenant não pode ser nulo.");
  }

  public static TenantId from(UUID value) {
    return new TenantId(value);
  }

  public static TenantId fromString(String value) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(
          "O identificador do tenant não pode ser nulo ou em branco.");
    }
    return new TenantId(UUID.fromString(value.trim()));
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
