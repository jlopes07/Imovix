/**
 * Trilha de auditoria de eventos relevantes de acesso e alteração.
 *
 * <p>Nunca registrar segredos, tokens, senhas ou conteúdo pessoal desnecessário.
 *
 * <p>Organização interna (quando aplicável): {@code api} (HTTP/DTOs), {@code application} (casos de
 * uso e transações), {@code domain} (entidades e regras) e {@code infrastructure} (persistência e
 * integrações). Ver {@code docs/architecture/conventions.md}.
 */
package br.com.imovix.audit;
