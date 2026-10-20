package br.com.imovix.companies.api;

import br.com.imovix.companies.application.CompanyResponse;
import br.com.imovix.companies.application.CreateCompanyUseCase;
import br.com.imovix.companies.application.GetCompanyUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Endpoint REST para gerenciamento de empresas e imobiliárias. */
@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

  private final CreateCompanyUseCase createCompanyUseCase;
  private final GetCompanyUseCase getCompanyUseCase;

  public CompanyController(
      CreateCompanyUseCase createCompanyUseCase, GetCompanyUseCase getCompanyUseCase) {
    this.createCompanyUseCase = createCompanyUseCase;
    this.getCompanyUseCase = getCompanyUseCase;
  }

  @PostMapping
  public ResponseEntity<CompanyResponse> create(
      @Valid @RequestBody CreateCompanyRequest request,
      @RequestHeader(value = "X-User-Id", required = false) UUID creatorUserId) {
    CompanyResponse response = createCompanyUseCase.execute(request.toCommand(creatorUserId));
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CompanyResponse> getById(@PathVariable UUID id) {
    CompanyResponse response = getCompanyUseCase.execute(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/current")
  public ResponseEntity<CompanyResponse> getCurrent(
      @RequestHeader(value = "X-Company-Id", required = false) UUID companyId) {
    if (companyId == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "O cabeçalho X-Company-Id é obrigatório para identificar a empresa ativa.");
    }
    CompanyResponse response = getCompanyUseCase.execute(companyId);
    return ResponseEntity.ok(response);
  }
}
