package br.com.imovix.properties.domain;

import java.util.UUID;

/** Exceção lançada quando um imóvel solicitado não é encontrado no tenant ativo. */
public class PropertyNotFoundException extends RuntimeException {

  public PropertyNotFoundException(UUID id) {
    super("Imóvel não encontrado com o identificador: " + id);
  }
}
