package br.com.imovix.listings.application;

import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyPurpose;
import br.com.imovix.properties.domain.PropertyRepository;
import br.com.imovix.properties.domain.PropertyStatus;
import br.com.imovix.properties.domain.PropertyType;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para listar publicamente imoveis disponiveis com filtros comerciais. */
@Service
public class ListPublicListingsUseCase {

  private final PropertyRepository propertyRepository;

  public ListPublicListingsUseCase(PropertyRepository propertyRepository) {
    this.propertyRepository = propertyRepository;
  }

  @Transactional(readOnly = true)
  public PagedPublicListingsResponse execute(
      UUID tenantId,
      PropertyType type,
      PropertyPurpose purpose,
      String city,
      BigDecimal maxPrice,
      Integer minBedrooms,
      int page,
      int size) {

    int resolvedPage = Math.max(0, page);
    int resolvedSize = size > 0 && size <= 50 ? size : 12;

    // Busca apenas imoveis com status AVAILABLE do tenant
    List<Property> properties =
        propertyRepository.findByTenantIdAndStatus(
            tenantId, PropertyStatus.AVAILABLE, resolvedPage, resolvedSize);

    // Filtros adicionais em memoria se fornecidos
    List<Property> filtered =
        properties.stream()
            .filter(p -> type == null || p.getType() == type)
            .filter(p -> purpose == null || p.getPurpose() == purpose)
            .filter(
                p ->
                    city == null
                        || city.isBlank()
                        || (p.getAddress() != null
                            && p.getAddress().city() != null
                            && p.getAddress()
                                .city()
                                .toLowerCase()
                                .contains(city.trim().toLowerCase())))
            .filter(
                p -> {
                  if (maxPrice == null || p.getPricing() == null) return true;
                  BigDecimal price =
                      p.getPurpose() == PropertyPurpose.RENT
                          ? p.getPricing().rentalPrice()
                          : p.getPricing().salePrice();
                  return price == null || price.compareTo(maxPrice) <= 0;
                })
            .filter(
                p -> {
                  if (minBedrooms == null || p.getDimensions() == null) return true;
                  Integer beds = p.getDimensions().bedrooms();
                  return beds != null && beds >= minBedrooms;
                })
            .toList();

    long totalElements =
        propertyRepository.countByTenantIdAndStatus(tenantId, PropertyStatus.AVAILABLE);
    int totalPages = (int) Math.ceil((double) totalElements / resolvedSize);

    List<PublicListingResponse> content =
        filtered.stream().map(PublicListingResponse::fromDomain).toList();

    return new PagedPublicListingsResponse(
        content, resolvedPage, resolvedSize, totalElements, totalPages);
  }
}
