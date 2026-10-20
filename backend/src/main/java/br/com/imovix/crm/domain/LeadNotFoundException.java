package br.com.imovix.crm.domain;

import java.util.UUID;

/** Excecao lancada quando um lead nao e encontrado para o tenant informado. */
public class LeadNotFoundException extends RuntimeException {

  public LeadNotFoundException(UUID id) {
    super("Lead nao encontrado com o identificador: " + id);
  }
}
