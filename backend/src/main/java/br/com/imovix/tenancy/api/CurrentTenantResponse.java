package br.com.imovix.tenancy.api;

import java.util.UUID;

/** DTO representando as informações do tenant ativo resolvido na requisição. */
public record CurrentTenantResponse(UUID tenantId, String companyName) {}
