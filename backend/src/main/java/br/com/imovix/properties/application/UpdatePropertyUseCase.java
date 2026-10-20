package br.com.imovix.properties.application;

import br.com.imovix.properties.domain.DuplicatePropertyCodeException;
import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyNotFoundException;
import br.com.imovix.properties.domain.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para atualização dos dados de um imóvel do tenant. */
@Service
public class UpdatePropertyUseCase {

  private final PropertyRepository propertyRepository;

  public UpdatePropertyUseCase(PropertyRepository propertyRepository) {
    this.propertyRepository = propertyRepository;
  }

  @Transactional
  public PropertyResponse execute(UpdatePropertyCommand command) {
    Property property =
        propertyRepository
            .findByTenantIdAndId(command.tenantId(), command.id())
            .orElseThrow(() -> new PropertyNotFoundException(command.id()));

    String normalizedCode = command.code().trim().toUpperCase();
    if (!property.getCode().equalsIgnoreCase(normalizedCode)
        && propertyRepository.existsByTenantIdAndCode(command.tenantId(), normalizedCode)) {
      throw new DuplicatePropertyCodeException(normalizedCode);
    }

    property.update(
        normalizedCode,
        command.title(),
        command.description(),
        command.type(),
        command.purpose(),
        command.status(),
        command.pricing(),
        command.dimensions(),
        command.address(),
        command.updaterUserId());

    Property saved = propertyRepository.save(property);
    return PropertyResponse.fromDomain(saved);
  }
}
