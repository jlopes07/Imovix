package br.com.imovix.tenancy.domain;

import java.util.UUID;

/**
 * Exceção de segurança lançada quando um usuário tenta acessar recursos ou definir um tenant para o
 * qual não possui associação ativa ou autorização concedida.
 */
public class CrossTenantAccessException extends RuntimeException {

  public CrossTenantAccessException(UUID userId, UUID requestedTenantId) {
    super(
        String.format(
            "Acesso negado: o usuário %s não possui associação ativa com o tenant %s.",
            userId, requestedTenantId));
  }

  public CrossTenantAccessException(String message) {
    super(message);
  }
}
