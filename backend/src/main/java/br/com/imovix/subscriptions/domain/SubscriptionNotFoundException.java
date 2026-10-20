package br.com.imovix.subscriptions.domain;

import java.util.UUID;

/** Excecao lancada quando uma empresa nao possui assinatura configurada no SaaS. */
public class SubscriptionNotFoundException extends RuntimeException {

  public SubscriptionNotFoundException(UUID tenantId) {
    super("Assinatura nao encontrada para o tenant informado: " + tenantId);
  }
}
