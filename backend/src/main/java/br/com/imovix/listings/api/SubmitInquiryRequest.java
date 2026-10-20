package br.com.imovix.listings.api;

import br.com.imovix.listings.application.SubmitLeadInquiryUseCase.SubmitInquiryCommand;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/** DTO de solicitacao publica de interesse em um imovel. */
public record SubmitInquiryRequest(
    @NotBlank(message = "O nome e obrigatorio.")
        @Size(max = 255, message = "O nome deve ter no maximo 255 caracteres.")
        String name,
    @Email(message = "O formato do e-mail e invalido.")
        @Size(max = 255, message = "O e-mail deve ter no maximo 255 caracteres.")
        String email,
    @NotBlank(message = "O telefone de contato e obrigatorio.")
        @Size(max = 50, message = "O telefone deve ter no maximo 50 caracteres.")
        String phone,
    @Size(max = 1000, message = "A mensagem deve ter no maximo 1000 caracteres.") String message,
    @AssertTrue(
            message =
                "O consentimento para comunicacao comercial nos termos da LGPD e obrigatorio.")
        Boolean lgpdConsent) {

  public SubmitInquiryCommand toCommand(UUID tenantId, UUID propertyId) {
    return new SubmitInquiryCommand(
        tenantId,
        propertyId,
        this.name,
        this.email,
        this.phone,
        this.message,
        Boolean.TRUE.equals(this.lgpdConsent));
  }
}
