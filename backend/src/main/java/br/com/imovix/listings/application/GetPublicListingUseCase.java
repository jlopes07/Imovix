package br.com.imovix.listings.application;

import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyNotFoundException;
import br.com.imovix.properties.domain.PropertyRepository;
import br.com.imovix.properties.domain.PropertyStatus;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para obter detalhes publicos de um imovel disponivel. */
@Service
public class GetPublicListingUseCase {

  private final PropertyRepository propertyRepository;

  public GetPublicListingUseCase(PropertyRepository propertyRepository) {
    this.propertyRepository = propertyRepository;
  }

  @Transactional(readOnly = true)
  public PublicListingResponse execute(UUID tenantId, UUID propertyId) {
    Property property =
        propertyRepository
            .findByTenantIdAndId(tenantId, propertyId)
            .filter(p -> p.getStatus() == PropertyStatus.AVAILABLE)
            .orElseThrow(() -> new PropertyNotFoundException(propertyId));

    return PublicListingResponse.fromDomain(property);
  }
}
