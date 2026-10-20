package br.com.imovix.crm.api;

import br.com.imovix.crm.application.ActivityResponse;
import br.com.imovix.crm.application.CreateLeadUseCase;
import br.com.imovix.crm.application.DeleteLeadUseCase;
import br.com.imovix.crm.application.GetLeadUseCase;
import br.com.imovix.crm.application.LeadResponse;
import br.com.imovix.crm.application.ListActivitiesUseCase;
import br.com.imovix.crm.application.ListLeadsUseCase;
import br.com.imovix.crm.application.PagedLeadsResponse;
import br.com.imovix.crm.application.UpdateLeadStatusUseCase;
import br.com.imovix.crm.application.UpdateLeadUseCase;
import br.com.imovix.crm.domain.LeadStatus;
import br.com.imovix.tenancy.application.TenantContext;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Endpoint REST para o gerenciamento de leads e contatos com isolamento multi-tenant. */
@RestController
@RequestMapping("/api/v1/leads")
public class LeadController {

  private final CreateLeadUseCase createLeadUseCase;
  private final GetLeadUseCase getLeadUseCase;
  private final ListLeadsUseCase listLeadsUseCase;
  private final UpdateLeadUseCase updateLeadUseCase;
  private final UpdateLeadStatusUseCase updateLeadStatusUseCase;
  private final DeleteLeadUseCase deleteLeadUseCase;
  private final ListActivitiesUseCase listActivitiesUseCase;

  public LeadController(
      CreateLeadUseCase createLeadUseCase,
      GetLeadUseCase getLeadUseCase,
      ListLeadsUseCase listLeadsUseCase,
      UpdateLeadUseCase updateLeadUseCase,
      UpdateLeadStatusUseCase updateLeadStatusUseCase,
      DeleteLeadUseCase deleteLeadUseCase,
      ListActivitiesUseCase listActivitiesUseCase) {
    this.createLeadUseCase = createLeadUseCase;
    this.getLeadUseCase = getLeadUseCase;
    this.listLeadsUseCase = listLeadsUseCase;
    this.updateLeadUseCase = updateLeadUseCase;
    this.updateLeadStatusUseCase = updateLeadStatusUseCase;
    this.deleteLeadUseCase = deleteLeadUseCase;
    this.listActivitiesUseCase = listActivitiesUseCase;
  }

  @PostMapping
  public ResponseEntity<LeadResponse> create(
      @Valid @RequestBody CreateLeadRequest request,
      @RequestHeader(value = "X-User-Id", required = false) UUID creatorUserId) {
    UUID tenantId = TenantContext.requireId();
    LeadResponse response = createLeadUseCase.execute(request.toCommand(tenantId, creatorUserId));
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<LeadResponse> getById(@PathVariable("id") UUID id) {
    UUID tenantId = TenantContext.requireId();
    return ResponseEntity.ok(getLeadUseCase.execute(tenantId, id));
  }

  @GetMapping
  public ResponseEntity<PagedLeadsResponse> list(
      @RequestParam(value = "status", required = false) LeadStatus status,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    UUID tenantId = TenantContext.requireId();
    return ResponseEntity.ok(listLeadsUseCase.execute(tenantId, status, page, size));
  }

  @PutMapping("/{id}")
  public ResponseEntity<LeadResponse> update(
      @PathVariable("id") UUID id,
      @Valid @RequestBody UpdateLeadRequest request,
      @RequestHeader(value = "X-User-Id", required = false) UUID updaterUserId) {
    UUID tenantId = TenantContext.requireId();
    LeadResponse response =
        updateLeadUseCase.execute(request.toCommand(tenantId, id, updaterUserId));
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<LeadResponse> updateStatus(
      @PathVariable("id") UUID id,
      @Valid @RequestBody UpdateLeadStatusRequest request,
      @RequestHeader(value = "X-User-Id", required = false) UUID updaterUserId) {
    UUID tenantId = TenantContext.requireId();
    LeadResponse response =
        updateLeadStatusUseCase.execute(tenantId, id, request.status(), updaterUserId);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
    UUID tenantId = TenantContext.requireId();
    deleteLeadUseCase.execute(tenantId, id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}/activities")
  public ResponseEntity<List<ActivityResponse>> listActivities(@PathVariable("id") UUID id) {
    UUID tenantId = TenantContext.requireId();
    return ResponseEntity.ok(listActivitiesUseCase.listByLead(tenantId, id));
  }
}
