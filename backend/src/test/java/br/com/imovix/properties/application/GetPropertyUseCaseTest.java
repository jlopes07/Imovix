package br.com.imovix.properties.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyNotFoundException;
import br.com.imovix.properties.domain.PropertyPurpose;
import br.com.imovix.properties.domain.PropertyRepository;
import br.com.imovix.properties.domain.PropertyType;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Aplicação: GetPropertyUseCase (Isolamento Multi-Tenant)")
class GetPropertyUseCaseTest {

  @Mock private PropertyRepository propertyRepository;
  @InjectMocks private GetPropertyUseCase getPropertyUseCase;

  @Test
  @DisplayName("Deve retornar imóvel quando pertencer ao tenant requisitado")
  void shouldReturnPropertyWhenSameTenant() {
    UUID tenantId = UUID.randomUUID();
    UUID propertyId = UUID.randomUUID();

    Property property =
        new Property(
            propertyId,
            tenantId,
            "CS-10",
            "Casa Jardim",
            null,
            PropertyType.HOUSE,
            PropertyPurpose.SALE,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    when(propertyRepository.findByTenantIdAndId(tenantId, propertyId))
        .thenReturn(Optional.of(property));

    PropertyResponse response = getPropertyUseCase.execute(tenantId, propertyId);

    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(propertyId);
    assertThat(response.tenantId()).isEqualTo(tenantId);
    assertThat(response.code()).isEqualTo("CS-10");
  }

  @Test
  @DisplayName(
      "TESTE DE SEGURANÇA: Deve lançar PropertyNotFoundException se o imóvel pertencer a outro tenant")
  void shouldThrowWhenPropertyBelongsToAnotherTenant() {
    UUID tenantA = UUID.randomUUID();
    UUID foreignPropertyId = UUID.randomUUID();

    // Repositório com escopo do tenant A não localiza recurso do tenant B
    when(propertyRepository.findByTenantIdAndId(tenantA, foreignPropertyId))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> getPropertyUseCase.execute(tenantA, foreignPropertyId))
        .isInstanceOf(PropertyNotFoundException.class)
        .hasMessageContaining(foreignPropertyId.toString());
  }
}
