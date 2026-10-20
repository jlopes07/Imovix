package br.com.imovix.audit.application;

import br.com.imovix.audit.domain.AuditEvent;
import br.com.imovix.audit.domain.AuditEventRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para consultar logs de auditoria do tenant autenticado. */
@Service
public class ListAuditEventsUseCase {

  private final AuditEventRepository auditEventRepository;

  public ListAuditEventsUseCase(AuditEventRepository auditEventRepository) {
    this.auditEventRepository = auditEventRepository;
  }

  @Transactional(readOnly = true)
  public PagedAuditLogsResponse execute(UUID tenantId, int page, int size) {
    List<AuditEvent> events = auditEventRepository.findByTenantId(tenantId, page, size);
    long total = auditEventRepository.countByTenantId(tenantId);
    int totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;

    List<AuditLogResponse> dtoList = events.stream().map(AuditLogResponse::fromDomain).toList();
    return new PagedAuditLogsResponse(dtoList, page, size, total, totalPages);
  }
}
