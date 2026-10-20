package br.com.imovix.properties.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Porta de persistência para o agregado {@link Property} com escopo obrigatório de tenant. */
public interface PropertyRepository {

  Property save(Property property);

  Optional<Property> findByTenantIdAndId(UUID tenantId, UUID id);

  boolean existsByTenantIdAndCode(UUID tenantId, String code);

  List<Property> findByTenantId(UUID tenantId, int page, int size);

  long countByTenantId(UUID tenantId);

  void delete(Property property);
}
