package br.com.imovix.identity.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.imovix.identity.domain.EmailAlreadyInUseException;
import br.com.imovix.identity.domain.User;
import br.com.imovix.identity.domain.UserRepository;
import br.com.imovix.identity.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Aplicação: RegisterUserUseCase")
class RegisterUserUseCaseTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @InjectMocks private RegisterUserUseCase registerUserUseCase;

  @Test
  @DisplayName("Deve registrar novo usuário com sucesso e salvar senha criptografada")
  void shouldRegisterNewUserSuccessfully() {
    RegisterUserCommand command =
        new RegisterUserCommand("NOVO@imovix.com.br", "Novo Usuário", "senhaForte123");

    when(userRepository.existsByEmail("novo@imovix.com.br")).thenReturn(false);
    when(passwordEncoder.encode("senhaForte123")).thenReturn("encodedPasswordHash");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    UserResponse response = registerUserUseCase.execute(command);

    assertThat(response).isNotNull();
    assertThat(response.email()).isEqualTo("novo@imovix.com.br");
    assertThat(response.name()).isEqualTo("Novo Usuário");
    assertThat(response.status()).isEqualTo(UserStatus.ACTIVE);

    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("Deve lançar EmailAlreadyInUseException se e-mail já estiver em uso")
  void shouldThrowWhenEmailAlreadyExists() {
    RegisterUserCommand command =
        new RegisterUserCommand("duplicado@imovix.com.br", "Usuário", "senha123");

    when(userRepository.existsByEmail("duplicado@imovix.com.br")).thenReturn(true);

    assertThatThrownBy(() -> registerUserUseCase.execute(command))
        .isInstanceOf(EmailAlreadyInUseException.class)
        .hasMessageContaining("duplicado@imovix.com.br");
  }
}
