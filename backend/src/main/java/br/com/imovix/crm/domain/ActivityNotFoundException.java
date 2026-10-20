package br.com.imovix.crm.domain;

import java.util.UUID;

/** Excecao lancada quando uma atividade nao e encontrada para o tenant informado. */
public class ActivityNotFoundException extends RuntimeException {

  public ActivityNotFoundException(UUID id) {
    super("Atividade nao encontrada com o identificador: " + id);
  }
}
