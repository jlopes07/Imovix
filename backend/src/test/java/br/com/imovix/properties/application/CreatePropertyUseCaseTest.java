package br.com.imovix.properties.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.imovix.properties.domain.DuplicatePropertyCodeException;
import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyPurpose;
import br.com.imovix.properties.domain.PropertyRepository;
import br.com.imovix.properties.domain.PropertyStatus;
import br.com.imovix.properties.domain.PropertyType;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Aplicação: CreatePropertyUseCase")
class CreatePropertyUseCaseTest {

  @Mock private PropertyRepository propertyRepository;
  @InjectMocks private CreatePropertyUseCase createPropertyUseCase;

  @Test
  @DisplayName("Deve cadastrar novo imóvel no tenant com sucesso")
  void shouldCreatePropertySuccessfully() {
    UUID tenantId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    CreatePropertyCommand command =
        new CreatePropertyCommand(
            tenantId,
            "AP-200",
            "Apartamento 2 Dormitórios",
            "Excelente imóvel",
            PropertyType.APARTMENT,
            PropertyPurpose.SALE,
            null,
            null,
            null,
            userId);

    when(propertyRepository.existsByTenantIdAndCode(tenantId, "AP-200")).thenReturn(false);
    when(propertyRepository.save(any(Property.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    PropertyResponse response = createPropertyUseCase.execute(command);

    assertThat(response).isNotNull();
    assertThat(response.tenantId()).isEqualTo(tenantId);
    assertThat(response.code()).isEqualTo("AP-200");
    assertThat(response.title()).isEqualTo("Apartamento 2 Dormitórios");
    assertThat(response.status()).isEqualTo(PropertyStatus.AVAILABLE);

    verify(propertyRepository).save(any(Property.class));
  }

  @Test
  @DisplayName("Deve lançar DuplicatePropertyCodeException se código já existir no mesmo tenant")
  void shouldThrowWhenCodeAlreadyExistsInTenant() {
    UUID tenantId = UUID.randomUUID();

    CreatePropertyCommand command =
        new CreatePropertyCommand(
            tenantId,
            "AP-200",
            "Apartamento",
            null,
            PropertyType.APARTMENT,
            PropertyPurpose.SALE,
            null,
            null,
            null,
            null);

    when(propertyRepository.existsByTenantIdAndCode(tenantId, "AP-200")).thenReturn(true);

    assertThatThrownBy(() -> createPropertyUseCase.execute(command))
        .isInstanceOf(DuplicatePropertyCodeException.class)
        .hasMessageContaining("AP-200");
  }
}
