/**
 * Identidade e conta do usuário: usuários, login federado, perfil, convites e recuperação de
 * acesso.
 *
 * <p>Não armazenar senha se o login for delegado a um provedor de identidade externo.
 *
 * <p>Organização interna (quando aplicável): {@code api} (HTTP/DTOs), {@code application} (casos de
 * uso e transações), {@code domain} (entidades e regras) e {@code infrastructure} (persistência e
 * integrações). Ver {@code docs/architecture/conventions.md}.
 */
package br.com.imovix.identity;
