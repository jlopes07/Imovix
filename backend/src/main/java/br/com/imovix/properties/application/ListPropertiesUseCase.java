package br.com.imovix.properties.application;

import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para listagem paginada de imóveis do tenant ativo. */
@Service
public class ListPropertiesUseCase {

  private final PropertyRepository propertyRepository;

  public ListPropertiesUseCase(PropertyRepository propertyRepository) {
    this.propertyRepository = propertyRepository;
  }

  @Transactional(readOnly = true)
  public PagedPropertiesResponse execute(UUID tenantId, int page, int size) {
    int validPage = Math.max(page, 0);
    int validSize = size > 0 && size <= 100 ? size : 20;

    List<Property> properties = propertyRepository.findByTenantId(tenantId, validPage, validSize);
    long totalElements = propertyRepository.countByTenantId(tenantId);
    int totalPages = (int) Math.ceil((double) totalElements / validSize);

    List<PropertyResponse> responses =
        properties.stream().map(PropertyResponse::fromDomain).toList();

    return new PagedPropertiesResponse(responses, validPage, validSize, totalElements, totalPages);
  }
}
