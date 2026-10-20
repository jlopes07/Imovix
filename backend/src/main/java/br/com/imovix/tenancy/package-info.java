/**
 * Contexto e isolamento multi-tenant: resolve a empresa ativa a partir de uma associação autorizada
 * do usuário.
 *
 * <p>Nunca confiar em {@code tenant_id} enviado pelo cliente. Toda leitura/escrita de dados de
 * negócio deve aplicar o escopo do tenant.
 *
 * <p>Organização interna (quando aplicável): {@code api} (HTTP/DTOs), {@code application} (casos de
 * uso e transações), {@code domain} (entidades e regras) e {@code infrastructure} (persistência e
 * integrações). Ver {@code docs/architecture/conventions.md}.
 */
package br.com.imovix.tenancy;
