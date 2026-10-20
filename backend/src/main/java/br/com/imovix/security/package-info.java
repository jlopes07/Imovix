/**
 * Segurança HTTP: cadeia de filtros, CORS, cabeçalhos e, futuramente, integração com o provedor de
 * identidade (OIDC).
 *
 * <p>Política padrão fail-closed: toda rota é negada até ser liberada explicitamente.
 *
 * <p>Organização interna (quando aplicável): {@code api} (HTTP/DTOs), {@code application} (casos de
 * uso e transações), {@code domain} (entidades e regras) e {@code infrastructure} (persistência e
 * integrações). Ver {@code docs/architecture/conventions.md}.
 */
package br.com.imovix.security;
