package br.com.imovix.properties.api;

import br.com.imovix.properties.application.CreatePropertyCommand;
import br.com.imovix.properties.domain.PropertyAddress;
import br.com.imovix.properties.domain.PropertyDimensions;
import br.com.imovix.properties.domain.PropertyPricing;
import br.com.imovix.properties.domain.PropertyPurpose;
import br.com.imovix.properties.domain.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.UUID;

/** Payload de requisição para cadastro de novo imóvel. */
public record CreatePropertyRequest(
    @Size(max = 50, message = "O código interno deve ter no máximo 50 caracteres.") String code,
    @NotBlank(message = "O título do anúncio é obrigatório.")
        @Size(min = 3, max = 255, message = "O título deve ter entre 3 e 255 caracteres.")
        String title,
    String description,
    @NotNull(message = "O tipo do imóvel é obrigatório.") PropertyType type,
    @NotNull(message = "A finalidade do imóvel é obrigatória.") PropertyPurpose purpose,
    @PositiveOrZero(message = "O preço de venda não pode ser negativo.") BigDecimal salePrice,
    @PositiveOrZero(message = "O preço de locação não pode ser negativo.") BigDecimal rentalPrice,
    @PositiveOrZero(message = "A taxa de condomínio não pode ser negativa.") BigDecimal condoFee,
    @PositiveOrZero(message = "O valor de IPTU não pode ser negativo.") BigDecimal iptu,
    @PositiveOrZero(message = "A quantidade de quartos não pode ser negativa.") Integer bedrooms,
    @PositiveOrZero(message = "A quantidade de banheiros não pode ser negativa.") Integer bathrooms,
    @PositiveOrZero(message = "A quantidade de suítes não pode ser negativa.") Integer suites,
    @PositiveOrZero(message = "A quantidade de vagas não pode ser negativa.") Integer parkingSpaces,
    @PositiveOrZero(message = "A área útil não pode ser negativa.") BigDecimal usableArea,
    @PositiveOrZero(message = "A área total não pode ser negativa.") BigDecimal totalArea,
    String zipCode,
    String street,
    String number,
    String complement,
    String neighborhood,
    String city,
    String state) {

  public CreatePropertyCommand toCommand(UUID tenantId, UUID creatorUserId) {
    PropertyPricing pricing = new PropertyPricing(salePrice, rentalPrice, condoFee, iptu);
    PropertyDimensions dimensions =
        new PropertyDimensions(bedrooms, bathrooms, suites, parkingSpaces, usableArea, totalArea);
    PropertyAddress address =
        PropertyAddress.of(zipCode, street, number, complement, neighborhood, city, state);

    return new CreatePropertyCommand(
        tenantId,
        code,
        title,
        description,
        type,
        purpose,
        pricing,
        dimensions,
        address,
        creatorUserId);
  }
}
