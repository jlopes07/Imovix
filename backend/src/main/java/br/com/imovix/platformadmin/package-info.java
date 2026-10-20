/**
 * Administração do SaaS pela equipe operadora: empresas, planos, suporte e métricas.
 *
 * <p>Rotas em {@code /api/v1/platform/**} com autorização separada; acessos excepcionais a dados de
 * clientes devem ser auditados.
 *
 * <p>Organização interna (quando aplicável): {@code api} (HTTP/DTOs), {@code application} (casos de
 * uso e transações), {@code domain} (entidades e regras) e {@code infrastructure} (persistência e
 * integrações). Ver {@code docs/architecture/conventions.md}.
 */
package br.com.imovix.platformadmin;
