package br.com.imovix.listings.api;

import br.com.imovix.listings.application.GetPublicListingUseCase;
import br.com.imovix.listings.application.InquiryResponse;
import br.com.imovix.listings.application.ListPublicListingsUseCase;
import br.com.imovix.listings.application.PagedPublicListingsResponse;
import br.com.imovix.listings.application.PublicListingResponse;
import br.com.imovix.listings.application.SubmitLeadInquiryUseCase;
import br.com.imovix.properties.domain.PropertyPurpose;
import br.com.imovix.properties.domain.PropertyType;
import br.com.imovix.tenancy.application.TenantContext;
import br.com.imovix.tenancy.domain.TenantId;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint REST publico para catalogo de imoveis e captacao automatica de leads. Nao requer
 * autenticacao interna de corretor.
 */
@RestController
@RequestMapping("/api/v1/public/listings")
public class PublicListingController {

  private static final UUID DEFAULT_FALLBACK_TENANT =
      UUID.fromString("00000000-0000-0000-0000-000000000001");

  private final ListPublicListingsUseCase listPublicListingsUseCase;
  private final GetPublicListingUseCase getPublicListingUseCase;
  private final SubmitLeadInquiryUseCase submitLeadInquiryUseCase;

  public PublicListingController(
      ListPublicListingsUseCase listPublicListingsUseCase,
      GetPublicListingUseCase getPublicListingUseCase,
      SubmitLeadInquiryUseCase submitLeadInquiryUseCase) {
    this.listPublicListingsUseCase = listPublicListingsUseCase;
    this.getPublicListingUseCase = getPublicListingUseCase;
    this.submitLeadInquiryUseCase = submitLeadInquiryUseCase;
  }

  @GetMapping
  public ResponseEntity<PagedPublicListingsResponse> list(
      @RequestParam(value = "tenantId", required = false) UUID tenantParam,
      @RequestHeader(value = "X-Tenant-Id", required = false) UUID tenantHeader,
      @RequestParam(value = "type", required = false) PropertyType type,
      @RequestParam(value = "purpose", required = false) PropertyPurpose purpose,
      @RequestParam(value = "city", required = false) String city,
      @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
      @RequestParam(value = "minBedrooms", required = false) Integer minBedrooms,
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "12") int size) {

    UUID resolvedTenant = resolveTenant(tenantHeader, tenantParam);
    return ResponseEntity.ok(
        listPublicListingsUseCase.execute(
            resolvedTenant, type, purpose, city, maxPrice, minBedrooms, page, size));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PublicListingResponse> getById(
      @PathVariable("id") UUID id,
      @RequestParam(value = "tenantId", required = false) UUID tenantParam,
      @RequestHeader(value = "X-Tenant-Id", required = false) UUID tenantHeader) {

    UUID resolvedTenant = resolveTenant(tenantHeader, tenantParam);
    return ResponseEntity.ok(getPublicListingUseCase.execute(resolvedTenant, id));
  }

  @PostMapping("/{id}/inquire")
  public ResponseEntity<InquiryResponse> inquire(
      @PathVariable("id") UUID id,
      @RequestHeader(value = "X-Tenant-Id", required = false) UUID tenantHeader,
      @RequestParam(value = "tenantId", required = false) UUID tenantParam,
      @Valid @RequestBody SubmitInquiryRequest request) {

    UUID resolvedTenant = resolveTenant(tenantHeader, tenantParam);
    InquiryResponse response =
        submitLeadInquiryUseCase.execute(request.toCommand(resolvedTenant, id));
    return ResponseEntity.ok(response);
  }

  private UUID resolveTenant(UUID headerTenant, UUID paramTenant) {
    return TenantContext.get()
        .map(TenantId::value)
        .orElse(
            headerTenant != null
                ? headerTenant
                : (paramTenant != null ? paramTenant : DEFAULT_FALLBACK_TENANT));
  }
}
