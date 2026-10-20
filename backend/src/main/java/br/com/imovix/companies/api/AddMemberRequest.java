package br.com.imovix.companies.api;

import br.com.imovix.companies.application.AddMemberCommand;
import br.com.imovix.companies.domain.MembershipRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/** Payload de requisição para vincular um novo membro a uma empresa. */
public record AddMemberRequest(
    @NotNull(message = "O identificador do usuário é obrigatório.") UUID userId,
    @NotNull(message = "O papel do membro na empresa é obrigatório.") MembershipRole role) {

  public AddMemberCommand toCommand(UUID companyId) {
    return new AddMemberCommand(companyId, userId, role);
  }
}
