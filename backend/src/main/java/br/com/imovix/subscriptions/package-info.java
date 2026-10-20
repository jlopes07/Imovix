/**
 * Planos, assinaturas, limites e cobrança recorrente, incluindo webhooks do provedor de pagamento.
 *
 * <p>Webhooks devem validar assinatura e ser idempotentes. Nunca armazenar dados de cartão.
 *
 * <p>Organização interna (quando aplicável): {@code api} (HTTP/DTOs), {@code application} (casos de
 * uso e transações), {@code domain} (entidades e regras) e {@code infrastructure} (persistência e
 * integrações). Ver {@code docs/architecture/conventions.md}.
 */
package br.com.imovix.subscriptions;
