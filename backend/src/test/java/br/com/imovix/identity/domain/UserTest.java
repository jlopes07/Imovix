package br.com.imovix.identity.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes de Domínio: User")
class UserTest {

  @Test
  @DisplayName("Deve normalizar o e-mail em minúsculas e sem espaços ao criar usuário")
  void shouldNormalizeEmailOnCreation() {
    User user = User.createNew("  Joao.SILVA@Imovix.com.BR  ", "João Silva", "hash123");

    assertThat(user.getEmail()).isEqualTo("joao.silva@imovix.com.br");
    assertThat(user.getName()).isEqualTo("João Silva");
    assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    assertThat(user.getId()).isNotNull();
    assertThat(user.getCreatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Deve rejeitar e-mail nulo ou em branco")
  void shouldRejectBlankEmail() {
    assertThatThrownBy(() -> User.createNew("   ", "João", "hash"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("e-mail");
  }

  @Test
  @DisplayName("Deve rejeitar nome nulo ou em branco")
  void shouldRejectBlankName() {
    assertThatThrownBy(() -> User.createNew("user@test.com", "   ", "hash"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("nome");
  }

  @Test
  @DisplayName("Deve alterar status para inativo e ativo corretamente")
  void shouldToggleActiveStatus() {
    User user = User.createNew("teste@imovix.com", "Usuário Teste", "hash");
    assertThat(user.isActive()).isTrue();

    user.deactivate();
    assertThat(user.isActive()).isFalse();
    assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);

    user.activate();
    assertThat(user.isActive()).isTrue();
    assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
  }

  @Test
  @DisplayName("Deve atualizar o perfil alterando o nome")
  void shouldUpdateProfile() {
    User user = User.createNew("teste@imovix.com", "Nome Antigo", "hash");
    user.updateProfile("Nome Novo");

    assertThat(user.getName()).isEqualTo("Nome Novo");
  }

  @Test
  @DisplayName("Dois usuários com mesmo ID são considerados iguais")
  void shouldBeEqualWhenSameId() {
    UUID id = UUID.randomUUID();
    User u1 = new User(id, "u1@test.com", "U1", "h1", UserStatus.ACTIVE, null, null);
    User u2 = new User(id, "u2@test.com", "U2", "h2", UserStatus.INACTIVE, null, null);

    assertThat(u1).isEqualTo(u2);
    assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
  }
}
