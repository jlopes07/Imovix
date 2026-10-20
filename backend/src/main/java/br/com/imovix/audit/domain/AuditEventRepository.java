package br.com.imovix.audit.domain;

import java.util.List;
import java.util.UUID;

/** Porta de persistencia do dominio para eventos de auditoria. */
public interface AuditEventRepository {

  AuditEvent save(AuditEvent event);

  List<AuditEvent> findByTenantId(UUID tenantId, int page, int size);

  long countByTenantId(UUID tenantId);
}
