package br.com.imovix.companies.application;

import java.util.UUID;

/** Comando para criação de uma nova empresa/imobiliária, vinculando o criador como ADMIN. */
public record CreateCompanyCommand(
    String name, String tradeName, String documentNumber, String creci, UUID creatorUserId) {}
