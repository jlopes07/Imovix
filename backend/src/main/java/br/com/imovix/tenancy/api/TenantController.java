package br.com.imovix.tenancy.api;

import br.com.imovix.companies.domain.Company;
import br.com.imovix.companies.domain.CompanyRepository;
import br.com.imovix.tenancy.application.TenantContext;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint REST para consulta do contexto do tenant ativo na requisição corrente. */
@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

  private final CompanyRepository companyRepository;

  public TenantController(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  @GetMapping("/current")
  public ResponseEntity<CurrentTenantResponse> getCurrentTenant() {
    UUID tenantId = TenantContext.requireId();
    String companyName =
        companyRepository.findById(tenantId).map(Company::getName).orElse("Empresa Desconhecida");

    return ResponseEntity.ok(new CurrentTenantResponse(tenantId, companyName));
  }
}
