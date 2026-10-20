package br.com.imovix.companies.api;

import br.com.imovix.companies.application.AddMemberUseCase;
import br.com.imovix.companies.application.CompanyMemberResponse;
import br.com.imovix.companies.application.ListCompanyMembersUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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

/** Endpoint REST para gerenciamento de membros de uma empresa imobiliária. */
@RestController
@RequestMapping
public class CompanyMemberController {

  private final ListCompanyMembersUseCase listCompanyMembersUseCase;
  private final AddMemberUseCase addMemberUseCase;

  public CompanyMemberController(
      ListCompanyMembersUseCase listCompanyMembersUseCase, AddMemberUseCase addMemberUseCase) {
    this.listCompanyMembersUseCase = listCompanyMembersUseCase;
    this.addMemberUseCase = addMemberUseCase;
  }

  @GetMapping("/api/v1/companies/{companyId}/members")
  public ResponseEntity<List<CompanyMemberResponse>> listMembers(@PathVariable UUID companyId) {
    List<CompanyMemberResponse> members = listCompanyMembersUseCase.execute(companyId);
    return ResponseEntity.ok(members);
  }

  @PostMapping("/api/v1/companies/{companyId}/members")
  public ResponseEntity<CompanyMemberResponse> addMember(
      @PathVariable UUID companyId, @Valid @RequestBody AddMemberRequest request) {
    CompanyMemberResponse response = addMemberUseCase.execute(request.toCommand(companyId));
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.membershipId())
            .toUri();
    return ResponseEntity.created(location).body(response);
  }

  @GetMapping("/api/v1/members")
  public ResponseEntity<List<CompanyMemberResponse>> listCurrentMembers(
      @RequestHeader(value = "X-Company-Id", required = false) UUID companyId) {
    if (companyId == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "O cabeçalho X-Company-Id é obrigatório para listar os membros da empresa.");
    }
    List<CompanyMemberResponse> members = listCompanyMembersUseCase.execute(companyId);
    return ResponseEntity.ok(members);
  }
}
