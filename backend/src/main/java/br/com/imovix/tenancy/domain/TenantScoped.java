package br.com.imovix.tenancy.domain;

import java.util.UUID;

/** Contrato de domínio para entidades e recursos que pertencem a um tenant específico. */
public interface TenantScoped {

  UUID getTenantId();
}
