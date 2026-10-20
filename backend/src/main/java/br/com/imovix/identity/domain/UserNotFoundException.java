package br.com.imovix.identity.domain;

import java.util.UUID;

/** Exceção lançada quando um usuário solicitado não é encontrado no sistema. */
public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(UUID id) {
    super("Usuário não encontrado com o identificador: " + id);
  }

  public UserNotFoundException(String email) {
    super("Usuário não encontrado com o e-mail: " + email);
  }
}
