package br.com.imovix.properties.application;

import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyNotFoundException;
import br.com.imovix.properties.domain.PropertyRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para consulta detalhada de um imóvel com isolamento por tenant. */
@Service
public class GetPropertyUseCase {

  private final PropertyRepository propertyRepository;

  public GetPropertyUseCase(PropertyRepository propertyRepository) {
    this.propertyRepository = propertyRepository;
  }

  @Transactional(readOnly = true)
  public PropertyResponse execute(UUID tenantId, UUID propertyId) {
    Property property =
        propertyRepository
            .findByTenantIdAndId(tenantId, propertyId)
            .orElseThrow(() -> new PropertyNotFoundException(propertyId));

    return PropertyResponse.fromDomain(property);
  }
}
