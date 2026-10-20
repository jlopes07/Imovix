package br.com.imovix.identity.domain;

/** Exceção lançada quando é tentado cadastrar um e-mail que já existe no sistema. */
public class EmailAlreadyInUseException extends RuntimeException {

  public EmailAlreadyInUseException(String email) {
    super("O e-mail informado já está em uso: " + email);
  }
}
