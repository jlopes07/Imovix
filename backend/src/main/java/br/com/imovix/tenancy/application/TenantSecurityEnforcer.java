package br.com.imovix.tenancy.application;

import br.com.imovix.tenancy.domain.CrossTenantAccessException;
import br.com.imovix.tenancy.domain.TenantScoped;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Utilitário de segurança para garantir que entidades e recursos acessados pertencem estritamente
 * ao tenant ativo no {@link TenantContext}.
 */
@Component
public class TenantSecurityEnforcer {

  public void enforce(TenantScoped resource) {
    if (resource == null) {
      return;
    }

    UUID currentTenantId = TenantContext.requireId();
    if (!currentTenantId.equals(resource.getTenantId())) {
      throw new CrossTenantAccessException(
          String.format(
              "Tentativa de violação multi-tenant: recurso pertence ao tenant %s, mas a requisição está no contexto %s.",
              resource.getTenantId(), currentTenantId));
    }
  }

  public void enforce(UUID resourceTenantId) {
    UUID currentTenantId = TenantContext.requireId();
    if (resourceTenantId == null || !currentTenantId.equals(resourceTenantId)) {
      throw new CrossTenantAccessException(
          String.format(
              "Tentativa de violação multi-tenant: recurso pertence ao tenant %s, mas a requisição está no contexto %s.",
              resourceTenantId, currentTenantId));
    }
  }
}
