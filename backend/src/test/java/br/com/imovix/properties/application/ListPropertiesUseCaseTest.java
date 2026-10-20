package br.com.imovix.properties.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyPurpose;
import br.com.imovix.properties.domain.PropertyRepository;
import br.com.imovix.properties.domain.PropertyType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Aplicação: ListPropertiesUseCase")
class ListPropertiesUseCaseTest {

  @Mock private PropertyRepository propertyRepository;
  @InjectMocks private ListPropertiesUseCase listPropertiesUseCase;

  @Test
  @DisplayName("Deve listar imóveis paginados com escopo forçado no tenant")
  void shouldListPropertiesWithPagination() {
    UUID tenantId = UUID.randomUUID();

    Property p1 =
        new Property(
            UUID.randomUUID(),
            tenantId,
            "AP-01",
            "Apto 1",
            null,
            PropertyType.APARTMENT,
            PropertyPurpose.SALE,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);

    when(propertyRepository.findByTenantId(tenantId, 0, 10)).thenReturn(List.of(p1));
    when(propertyRepository.countByTenantId(tenantId)).thenReturn(1L);

    PagedPropertiesResponse response = listPropertiesUseCase.execute(tenantId, 0, 10);

    assertThat(response.items()).hasSize(1);
    assertThat(response.totalElements()).isEqualTo(1L);
    assertThat(response.page()).isEqualTo(0);
    assertThat(response.size()).isEqualTo(10);
    assertThat(response.totalPages()).isEqualTo(1);
  }
}
