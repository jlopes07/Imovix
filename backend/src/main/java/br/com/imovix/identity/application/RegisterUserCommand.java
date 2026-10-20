package br.com.imovix.identity.application;

/** Comando de entrada para registro de um novo usuário. */
public record RegisterUserCommand(String email, String name, String password) {}
