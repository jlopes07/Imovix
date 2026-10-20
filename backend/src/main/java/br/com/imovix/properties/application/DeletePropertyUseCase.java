package br.com.imovix.properties.application;

import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyNotFoundException;
import br.com.imovix.properties.domain.PropertyRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para desativação/remoção lógica de um imóvel com escopo do tenant ativo. */
@Service
public class DeletePropertyUseCase {

  private final PropertyRepository propertyRepository;

  public DeletePropertyUseCase(PropertyRepository propertyRepository) {
    this.propertyRepository = propertyRepository;
  }

  @Transactional
  public void execute(UUID tenantId, UUID propertyId, UUID updaterUserId) {
    Property property =
        propertyRepository
            .findByTenantIdAndId(tenantId, propertyId)
            .orElseThrow(() -> new PropertyNotFoundException(propertyId));

    property.deactivate(updaterUserId);
    propertyRepository.save(property);
  }
}
