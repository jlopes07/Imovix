package br.com.imovix.properties.infrastructure.persistence;

import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyRepository;
import br.com.imovix.properties.domain.PropertyStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/** Adaptador de persistência que implementa {@link PropertyRepository} usando Spring Data JPA. */
@Component
public class JpaPropertyRepositoryAdapter implements PropertyRepository {

  private final SpringDataPropertyRepository repository;

  public JpaPropertyRepositoryAdapter(SpringDataPropertyRepository repository) {
    this.repository = repository;
  }

  @Override
  public Property save(Property property) {
    PropertyJpaEntity entity = PropertyJpaEntity.fromDomain(property);
    PropertyJpaEntity saved = repository.save(entity);
    return saved.toDomain();
  }

  @Override
  public Optional<Property> findByTenantIdAndId(UUID tenantId, UUID id) {
    return repository.findByTenantIdAndId(tenantId, id).map(PropertyJpaEntity::toDomain);
  }

  @Override
  public boolean existsByTenantIdAndCode(UUID tenantId, String code) {
    return repository.existsByTenantIdAndCode(tenantId, code);
  }

  @Override
  public List<Property> findByTenantId(UUID tenantId, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    return repository
        .findByTenantId(tenantId, pageRequest)
        .map(PropertyJpaEntity::toDomain)
        .getContent();
  }

  @Override
  public List<Property> findByTenantIdAndStatus(
      UUID tenantId, PropertyStatus status, int page, int size) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    return repository
        .findByTenantIdAndStatus(tenantId, status, pageRequest)
        .map(PropertyJpaEntity::toDomain)
        .getContent();
  }

  @Override
  public long countByTenantId(UUID tenantId) {
    return repository.countByTenantId(tenantId);
  }

  @Override
  public long countByTenantIdAndStatus(UUID tenantId, PropertyStatus status) {
    return repository.countByTenantIdAndStatus(tenantId, status);
  }

  @Override
  public void delete(Property property) {
    repository.deleteById(property.getId());
  }
}
