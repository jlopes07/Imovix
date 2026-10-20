package br.com.imovix.properties.application;

import br.com.imovix.properties.domain.PropertyAddress;
import br.com.imovix.properties.domain.PropertyDimensions;
import br.com.imovix.properties.domain.PropertyPricing;
import br.com.imovix.properties.domain.PropertyPurpose;
import br.com.imovix.properties.domain.PropertyStatus;
import br.com.imovix.properties.domain.PropertyType;
import java.util.UUID;

/** Comando de entrada para atualização de um imóvel existente. */
public record UpdatePropertyCommand(
    UUID id,
    UUID tenantId,
    String code,
    String title,
    String description,
    PropertyType type,
    PropertyPurpose purpose,
    PropertyStatus status,
    PropertyPricing pricing,
    PropertyDimensions dimensions,
    PropertyAddress address,
    UUID updaterUserId) {}
