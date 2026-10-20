package br.com.imovix.identity.api;

import br.com.imovix.identity.application.RegisterUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Payload de requisição para cadastro de novo usuário. */
public record CreateUserRequest(
    @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail informado é inválido.")
        String email,
    @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 2, max = 255, message = "O nome deve conter entre 2 e 255 caracteres.")
        String name,
    @Size(min = 6, max = 100, message = "A senha deve conter no mínimo 6 caracteres.")
        String password) {

  public RegisterUserCommand toCommand() {
    return new RegisterUserCommand(email, name, password);
  }
}
