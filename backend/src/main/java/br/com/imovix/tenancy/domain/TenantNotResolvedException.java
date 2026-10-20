package br.com.imovix.tenancy.domain;

/**
 * Exceção lançada quando uma operação de negócio requer um tenant ativo, mas nenhum contexto de
 * tenant foi resolvido na requisição.
 */
public class TenantNotResolvedException extends RuntimeException {

  public TenantNotResolvedException(String message) {
    super(message);
  }

  public TenantNotResolvedException() {
    super("Nenhum tenant ativo foi resolvido para a requisição corrente.");
  }
}
