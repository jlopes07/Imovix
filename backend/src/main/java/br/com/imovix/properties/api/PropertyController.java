package br.com.imovix.properties.api;

import br.com.imovix.properties.application.CreatePropertyUseCase;
import br.com.imovix.properties.application.DeletePropertyUseCase;
import br.com.imovix.properties.application.GetPropertyUseCase;
import br.com.imovix.properties.application.ListPropertiesUseCase;
import br.com.imovix.properties.application.PagedPropertiesResponse;
import br.com.imovix.properties.application.PropertyResponse;
import br.com.imovix.properties.application.UpdatePropertyUseCase;
import br.com.imovix.tenancy.application.TenantContext;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Endpoint REST para o CRUD completo e gestão de imóveis com isolamento multi-tenant. */
@RestController
@RequestMapping("/api/v1/properties")
public class PropertyController {

  private final CreatePropertyUseCase createPropertyUseCase;
  private final GetPropertyUseCase getPropertyUseCase;
  private final ListPropertiesUseCase listPropertiesUseCase;
  private final UpdatePropertyUseCase updatePropertyUseCase;
  private final DeletePropertyUseCase deletePropertyUseCase;

  public PropertyController(
      CreatePropertyUseCase createPropertyUseCase,
      GetPropertyUseCase getPropertyUseCase,
      ListPropertiesUseCase listPropertiesUseCase,
      UpdatePropertyUseCase updatePropertyUseCase,
      DeletePropertyUseCase deletePropertyUseCase) {
    this.createPropertyUseCase = createPropertyUseCase;
    this.getPropertyUseCase = getPropertyUseCase;
    this.listPropertiesUseCase = listPropertiesUseCase;
    this.updatePropertyUseCase = updatePropertyUseCase;
    this.deletePropertyUseCase = deletePropertyUseCase;
  }

  @PostMapping
  public ResponseEntity<PropertyResponse> create(
      @Valid @RequestBody CreatePropertyRequest request,
      @RequestHeader(value = "X-User-Id", required = false) UUID creatorUserId) {
    UUID tenantId = TenantContext.requireId();

    PropertyResponse response =
        createPropertyUseCase.execute(request.toCommand(tenantId, creatorUserId));
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @GetMapping
  public ResponseEntity<PagedPropertiesResponse> list(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "20") int size) {
    UUID tenantId = TenantContext.requireId();
    PagedPropertiesResponse response = listPropertiesUseCase.execute(tenantId, page, size);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PropertyResponse> getById(@PathVariable UUID id) {
    UUID tenantId = TenantContext.requireId();
    PropertyResponse response = getPropertyUseCase.execute(tenantId, id);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<PropertyResponse> update(
      @PathVariable UUID id,
      @Valid @RequestBody UpdatePropertyRequest request,
      @RequestHeader(value = "X-User-Id", required = false) UUID updaterUserId) {
    UUID tenantId = TenantContext.requireId();
    PropertyResponse response =
        updatePropertyUseCase.execute(request.toCommand(id, tenantId, updaterUserId));
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID id,
      @RequestHeader(value = "X-User-Id", required = false) UUID updaterUserId) {
    UUID tenantId = TenantContext.requireId();
    deletePropertyUseCase.execute(tenantId, id, updaterUserId);
    return ResponseEntity.noContent().build();
  }
}
