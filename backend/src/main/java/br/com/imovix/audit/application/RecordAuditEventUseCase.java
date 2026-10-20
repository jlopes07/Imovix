package br.com.imovix.audit.application;

import br.com.imovix.audit.domain.AuditEvent;
import br.com.imovix.audit.domain.AuditEventRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para registrar eventos na trilha de auditoria. */
@Service
public class RecordAuditEventUseCase {

  private final AuditEventRepository auditEventRepository;

  public RecordAuditEventUseCase(AuditEventRepository auditEventRepository) {
    this.auditEventRepository = auditEventRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public AuditLogResponse execute(
      UUID tenantId,
      UUID userId,
      String userEmail,
      String action,
      String resourceType,
      String resourceId,
      String details,
      String ipAddress,
      String correlationId) {
    AuditEvent event =
        AuditEvent.create(
            tenantId,
            userId,
            userEmail,
            action,
            resourceType,
            resourceId,
            details,
            ipAddress,
            correlationId);

    AuditEvent saved = auditEventRepository.save(event);
    return AuditLogResponse.fromDomain(saved);
  }
}
