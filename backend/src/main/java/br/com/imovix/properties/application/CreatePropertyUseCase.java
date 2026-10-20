package br.com.imovix.properties.application;

import br.com.imovix.properties.domain.DuplicatePropertyCodeException;
import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyRepository;
import br.com.imovix.properties.domain.PropertyType;
import java.util.UUID;
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
    String resolvedCode = resolveCode(command.tenantId(), command.code(), command.type());

    if (propertyRepository.existsByTenantIdAndCode(command.tenantId(), resolvedCode)) {
      throw new DuplicatePropertyCodeException(resolvedCode);
    }

    Property property =
        Property.createNew(
            command.tenantId(),
            resolvedCode,
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

  private String resolveCode(UUID tenantId, String providedCode, PropertyType type) {
    if (providedCode != null && !providedCode.isBlank()) {
      return providedCode.trim().toUpperCase();
    }

    String prefix =
        switch (type) {
          case APARTMENT -> "AP";
          case HOUSE -> "CS";
          case LAND -> "TR";
          case COMMERCIAL -> "CM";
          case RURAL -> "RU";
        };

    long nextNumber = propertyRepository.countByTenantId(tenantId) + 1;
    String candidate = String.format("%s-%04d", prefix, nextNumber);
    while (propertyRepository.existsByTenantIdAndCode(tenantId, candidate)) {
      nextNumber++;
      candidate = String.format("%s-%04d", prefix, nextNumber);
    }
    return candidate;
  }
}
