package br.com.imovix.audit.api;

import br.com.imovix.audit.application.ListAuditEventsUseCase;
import br.com.imovix.audit.application.PagedAuditLogsResponse;
import br.com.imovix.tenancy.application.TenantContext;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controller REST para consulta da trilha de auditoria e conformidade LGPD. */
@RestController
@RequestMapping("/api/v1/audit/events")
public class AuditController {

  private final ListAuditEventsUseCase listAuditEventsUseCase;

  public AuditController(ListAuditEventsUseCase listAuditEventsUseCase) {
    this.listAuditEventsUseCase = listAuditEventsUseCase;
  }

  @GetMapping
  public ResponseEntity<PagedAuditLogsResponse> listAuditEvents(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    UUID tenantId = TenantContext.requireId();
    PagedAuditLogsResponse response = listAuditEventsUseCase.execute(tenantId, page, size);
    return ResponseEntity.ok(response);
  }
}
