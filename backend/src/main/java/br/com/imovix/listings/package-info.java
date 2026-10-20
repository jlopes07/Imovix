/**
 * Publicação pública de imóveis: somente campos explicitamente aprovados para exibição pública.
 *
 * <p>Rotas públicas em {@code /api/v1/public/**}; nunca expor dados internos ou pessoais.
 *
 * <p>Organização interna (quando aplicável): {@code api} (HTTP/DTOs), {@code application} (casos de
 * uso e transações), {@code domain} (entidades e regras) e {@code infrastructure} (persistência e
 * integrações). Ver {@code docs/architecture/conventions.md}.
 */
package br.com.imovix.listings;
