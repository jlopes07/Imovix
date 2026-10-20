package br.com.imovix.identity.domain;

import java.util.Optional;
import java.util.UUID;

/** Porta de persistência (abstração de repositório) para o agregado {@link User}. */
public interface UserRepository {

  User save(User user);

  Optional<User> findById(UUID id);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}
