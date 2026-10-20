package br.com.imovix.identity.application;

import br.com.imovix.identity.domain.EmailAlreadyInUseException;
import br.com.imovix.identity.domain.User;
import br.com.imovix.identity.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para registrar um novo usuário com validação de unicidade de e-mail. */
@Service
public class RegisterUserUseCase {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public RegisterUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public UserResponse execute(RegisterUserCommand command) {
    String normalizedEmail = User.normalizeEmail(command.email());
    if (userRepository.existsByEmail(normalizedEmail)) {
      throw new EmailAlreadyInUseException(normalizedEmail);
    }

    String passwordHash =
        command.password() != null && !command.password().isBlank()
            ? passwordEncoder.encode(command.password())
            : null;

    User newUser = User.createNew(normalizedEmail, command.name(), passwordHash);
    User savedUser = userRepository.save(newUser);
    return UserResponse.fromDomain(savedUser);
  }
}
