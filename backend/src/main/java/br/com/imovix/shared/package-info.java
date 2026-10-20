/**
 * Componentes realmente transversais (tipos utilitários, objetos de valor genéricos, erros base).
 *
 * <p>Evitar transformar este pacote em depósito genérico: se algo pertence a um domínio, fica no
 * módulo do domínio.
 *
 * <p>Organização interna (quando aplicável): {@code api} (HTTP/DTOs), {@code application} (casos de
 * uso e transações), {@code domain} (entidades e regras) e {@code infrastructure} (persistência e
 * integrações). Ver {@code docs/architecture/conventions.md}.
 */
package br.com.imovix.shared;
