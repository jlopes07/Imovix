package br.com.imovix.identity.application;

import br.com.imovix.identity.domain.User;
import br.com.imovix.identity.domain.UserNotFoundException;
import br.com.imovix.identity.domain.UserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para obter os dados do perfil de um usuário. */
@Service
public class GetUserProfileUseCase {

  private final UserRepository userRepository;

  public GetUserProfileUseCase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public UserResponse getById(UUID userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    return UserResponse.fromDomain(user);
  }

  @Transactional(readOnly = true)
  public UserResponse getByEmail(String email) {
    String normalizedEmail = User.normalizeEmail(email);
    User user =
        userRepository
            .findByEmail(normalizedEmail)
            .orElseThrow(() -> new UserNotFoundException(normalizedEmail));
    return UserResponse.fromDomain(user);
  }
}
