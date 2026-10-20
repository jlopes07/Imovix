package br.com.imovix.properties.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes de Domínio: Property")
class PropertyTest {

  @Test
  @DisplayName("Deve criar imóvel com código em maiúsculas e status inicial AVAILABLE")
  void shouldCreatePropertyWithUppercaseCodeAndAvailableStatus() {
    UUID tenantId = UUID.randomUUID();
    UUID creatorId = UUID.randomUUID();

    PropertyPricing pricing =
        new PropertyPricing(
            new BigDecimal("750000.00"), null, new BigDecimal("650.00"), new BigDecimal("2100.00"));

    PropertyDimensions dimensions =
        new PropertyDimensions(3, 2, 1, 2, new BigDecimal("85.50"), new BigDecimal("110.00"));

    PropertyAddress address =
        PropertyAddress.of(
            "01310-100", "Av. Paulista", "1000", "Apto 101", "Bela Vista", "São Paulo", "sp");

    Property property =
        Property.createNew(
            tenantId,
            "  ap-101  ",
            "  Apartamento Reformado na Paulista  ",
            "Excelente vista e localização privilegiada",
            PropertyType.APARTMENT,
            PropertyPurpose.SALE,
            pricing,
            dimensions,
            address,
            creatorId);

    assertThat(property.getTenantId()).isEqualTo(tenantId);
    assertThat(property.getCode()).isEqualTo("AP-101");
    assertThat(property.getTitle()).isEqualTo("Apartamento Reformado na Paulista");
    assertThat(property.getStatus()).isEqualTo(PropertyStatus.AVAILABLE);
    assertThat(property.isActive()).isTrue();
    assertThat(property.getAddress().state()).isEqualTo("SP");
    assertThat(property.getCreatedAt()).isNotNull();
    assertThat(property.getCreatedBy()).isEqualTo(creatorId);
  }

  @Test
  @DisplayName("Deve rejeitar código em branco ou título vazio")
  void shouldRejectBlankCodeOrTitle() {
    UUID tenantId = UUID.randomUUID();

    assertThatThrownBy(
            () ->
                Property.createNew(
                    tenantId,
                    "   ",
                    "Título",
                    null,
                    PropertyType.HOUSE,
                    PropertyPurpose.RENT,
                    null,
                    null,
                    null,
                    null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("código interno");

    assertThatThrownBy(
            () ->
                Property.createNew(
                    tenantId,
                    "CS-001",
                    "   ",
                    null,
                    PropertyType.HOUSE,
                    PropertyPurpose.RENT,
                    null,
                    null,
                    null,
                    null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("título");
  }

  @Test
  @DisplayName("Deve desativar o imóvel e atualizar status para INACTIVE")
  void shouldDeactivateProperty() {
    UUID tenantId = UUID.randomUUID();
    UUID updater = UUID.randomUUID();

    Property property =
        Property.createNew(
            tenantId,
            "CS-10",
            "Casa em Condomínio",
            null,
            PropertyType.HOUSE,
            PropertyPurpose.SALE,
            null,
            null,
            null,
            null);

    assertThat(property.isActive()).isTrue();

    property.deactivate(updater);

    assertThat(property.isActive()).isFalse();
    assertThat(property.getStatus()).isEqualTo(PropertyStatus.INACTIVE);
    assertThat(property.getUpdatedBy()).isEqualTo(updater);
  }

  @Test
  @DisplayName("PropertyPricing deve rejeitar valores negativos")
  void shouldRejectNegativePricing() {
    assertThatThrownBy(() -> new PropertyPricing(new BigDecimal("-100.00"), null, null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("não pode conter um valor negativo");
  }
}
