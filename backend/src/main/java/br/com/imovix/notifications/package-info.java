/**
 * Comunicação: e-mails, avisos internos e fila de envio (outbox).
 *
 * <p>Tarefas assíncronas devem carregar e validar o contexto do tenant.
 *
 * <p>Organização interna (quando aplicável): {@code api} (HTTP/DTOs), {@code application} (casos de
 * uso e transações), {@code domain} (entidades e regras) e {@code infrastructure} (persistência e
 * integrações). Ver {@code docs/architecture/conventions.md}.
 */
package br.com.imovix.notifications;
