package br.com.imovix.properties.domain;

/** Exceção lançada quando já existe um imóvel com o mesmo código interno no tenant. */
public class DuplicatePropertyCodeException extends RuntimeException {

  public DuplicatePropertyCodeException(String code) {
    super(String.format("Já existe um imóvel cadastrado com o código '%s' nesta empresa.", code));
  }
}
