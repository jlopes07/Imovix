package br.com.imovix.properties.application;

import br.com.imovix.properties.domain.DuplicatePropertyCodeException;
import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para cadastro de novos imóveis vinculados ao tenant ativo. */
@Service
public class CreatePropertyUseCase {

  private final PropertyRepository propertyRepository;

  public CreatePropertyUseCase(PropertyRepository propertyRepository) {
    this.propertyRepository = propertyRepository;
  }

  @Transactional
  public PropertyResponse execute(CreatePropertyCommand command) {
    String normalizedCode = command.code().trim().toUpperCase();

    if (propertyRepository.existsByTenantIdAndCode(command.tenantId(), normalizedCode)) {
      throw new DuplicatePropertyCodeException(normalizedCode);
    }

    Property property =
        Property.createNew(
            command.tenantId(),
            normalizedCode,
            command.title(),
            command.description(),
            command.type(),
            command.purpose(),
            command.pricing(),
            command.dimensions(),
            command.address(),
            command.creatorUserId());

    Property saved = propertyRepository.save(property);
    return PropertyResponse.fromDomain(saved);
  }
}
