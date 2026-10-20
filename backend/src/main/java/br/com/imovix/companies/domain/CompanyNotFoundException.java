package br.com.imovix.companies.domain;

import java.util.UUID;

/** Exceção lançada quando uma empresa solicitada não é encontrada. */
public class CompanyNotFoundException extends RuntimeException {

  public CompanyNotFoundException(UUID id) {
    super("Empresa não encontrada com o identificador: " + id);
  }
}
