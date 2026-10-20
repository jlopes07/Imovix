package br.com.imovix.companies.api;

import br.com.imovix.companies.application.CreateCompanyCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/** Payload de requisição para cadastro de uma nova empresa/imobiliária. */
public record CreateCompanyRequest(
    @NotBlank(message = "A razão social ou nome da empresa é obrigatório.")
        @Size(min = 2, max = 255, message = "O nome deve ter entre 2 e 255 caracteres.")
        String name,
    @Size(max = 255, message = "O nome fantasia deve ter no máximo 255 caracteres.")
        String tradeName,
    @Size(max = 32, message = "O documento deve ter no máximo 32 caracteres.")
        String documentNumber,
    @Size(max = 50, message = "O CRECI deve ter no máximo 50 caracteres.") String creci) {

  public CreateCompanyCommand toCommand(UUID creatorUserId) {
    return new CreateCompanyCommand(name, tradeName, documentNumber, creci, creatorUserId);
  }
}
