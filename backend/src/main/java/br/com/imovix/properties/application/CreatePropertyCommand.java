package br.com.imovix.properties.application;

import br.com.imovix.properties.domain.PropertyAddress;
import br.com.imovix.properties.domain.PropertyDimensions;
import br.com.imovix.properties.domain.PropertyPricing;
import br.com.imovix.properties.domain.PropertyPurpose;
import br.com.imovix.properties.domain.PropertyType;
import java.util.UUID;

/** Comando de entrada para cadastro de um novo imóvel. */
public record CreatePropertyCommand(
    UUID tenantId,
    String code,
    String title,
    String description,
    PropertyType type,
    PropertyPurpose purpose,
    PropertyPricing pricing,
    PropertyDimensions dimensions,
    PropertyAddress address,
    UUID creatorUserId) {}
