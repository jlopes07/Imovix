package br.com.imovix.crm.api;

import br.com.imovix.crm.application.ActivityResponse;
import br.com.imovix.crm.application.CompleteActivityUseCase;
import br.com.imovix.crm.application.DeleteActivityUseCase;
import br.com.imovix.crm.application.ListActivitiesUseCase;
import br.com.imovix.crm.application.ScheduleActivityUseCase;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Endpoint REST para agendamento, acompanhamento e conclusao de atividades e visitas. */
@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {

  private final ScheduleActivityUseCase scheduleActivityUseCase;
  private final CompleteActivityUseCase completeActivityUseCase;
  private final ListActivitiesUseCase listActivitiesUseCase;
  private final DeleteActivityUseCase deleteActivityUseCase;

  public ActivityController(
      ScheduleActivityUseCase scheduleActivityUseCase,
      CompleteActivityUseCase completeActivityUseCase,
      ListActivitiesUseCase listActivitiesUseCase,
      DeleteActivityUseCase deleteActivityUseCase) {
    this.scheduleActivityUseCase = scheduleActivityUseCase;
    this.completeActivityUseCase = completeActivityUseCase;
    this.listActivitiesUseCase = listActivitiesUseCase;
    this.deleteActivityUseCase = deleteActivityUseCase;
  }

  @PostMapping
  public ResponseEntity<ActivityResponse> schedule(
      @Valid @RequestBody ScheduleActivityRequest request) {
    UUID tenantId = TenantContext.requireId();
    ActivityResponse response = scheduleActivityUseCase.execute(request.toCommand(tenantId));
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @GetMapping
  public ResponseEntity<List<ActivityResponse>> listUpcoming(
      @RequestParam(value = "daysAhead", defaultValue = "30") int daysAhead) {
    UUID tenantId = TenantContext.requireId();
    return ResponseEntity.ok(listActivitiesUseCase.listUpcoming(tenantId, daysAhead));
  }

  @PatchMapping("/{id}/complete")
  public ResponseEntity<ActivityResponse> complete(
      @PathVariable("id") UUID id, @RequestBody(required = false) CompleteActivityRequest request) {
    UUID tenantId = TenantContext.requireId();
    String feedback = request != null ? request.feedback() : null;
    var completedAt = request != null ? request.completedAt() : null;
    ActivityResponse response =
        completeActivityUseCase.execute(tenantId, id, feedback, completedAt);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
    UUID tenantId = TenantContext.requireId();
    deleteActivityUseCase.execute(tenantId, id);
    return ResponseEntity.noContent().build();
  }
}
