package br.com.imovix.identity.infrastructure.persistence;

import br.com.imovix.identity.domain.User;
import br.com.imovix.identity.domain.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Adaptador de persistência que implementa {@link UserRepository} usando Spring Data JPA. */
@Component
public class JpaUserRepositoryAdapter implements UserRepository {

  private final SpringDataUserRepository springDataUserRepository;

  public JpaUserRepositoryAdapter(SpringDataUserRepository springDataUserRepository) {
    this.springDataUserRepository = springDataUserRepository;
  }

  @Override
  public User save(User user) {
    UserJpaEntity entity = UserJpaEntity.fromDomain(user);
    UserJpaEntity savedEntity = springDataUserRepository.save(entity);
    return savedEntity.toDomain();
  }

  @Override
  public Optional<User> findById(UUID id) {
    return springDataUserRepository.findById(id).map(UserJpaEntity::toDomain);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return springDataUserRepository.findByEmail(email).map(UserJpaEntity::toDomain);
  }

  @Override
  public boolean existsByEmail(String email) {
    return springDataUserRepository.existsByEmail(email);
  }
}
