package br.com.imovix.subscriptions.domain;

/**
 * Excecao lancada quando uma acao ultrapassa o limite permitido pelo plano contratado da empresa.
 */
public class PlanLimitExceededException extends RuntimeException {

  public PlanLimitExceededException(String resourceName, int currentUsage, int maxAllowed) {
    super(
        String.format(
            "Limite do plano atingido para %s: uso atual de %d, capacidade maxima permitida de %d. Faca upgrade do seu plano para cadastrar mais itens.",
            resourceName, currentUsage, maxAllowed));
  }
}
