package br.com.imovix.listings.application;

import br.com.imovix.crm.application.CreateLeadUseCase;
import br.com.imovix.crm.application.CreateLeadUseCase.CreateLeadCommand;
import br.com.imovix.crm.application.LeadResponse;
import br.com.imovix.crm.domain.ContactType;
import br.com.imovix.crm.domain.LeadSource;
import br.com.imovix.crm.domain.LeadStatus;
import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyNotFoundException;
import br.com.imovix.properties.domain.PropertyPurpose;
import br.com.imovix.properties.domain.PropertyRepository;
import br.com.imovix.properties.domain.PropertyStatus;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para processar manifestacao publica de interesse em um imovel, capturando os dados do
 * interessado e gerando automaticamente um Lead no CRM da imobiliaria.
 */
@Service
public class SubmitLeadInquiryUseCase {

  private final PropertyRepository propertyRepository;
  private final CreateLeadUseCase createLeadUseCase;

  public SubmitLeadInquiryUseCase(
      PropertyRepository propertyRepository, CreateLeadUseCase createLeadUseCase) {
    this.propertyRepository = propertyRepository;
    this.createLeadUseCase = createLeadUseCase;
  }

  @Transactional
  public InquiryResponse execute(SubmitInquiryCommand command) {
    // 1. Valida existencia e disponibilidade do imovel no tenant informado
    Property property =
        propertyRepository
            .findByTenantIdAndId(command.tenantId(), command.propertyId())
            .filter(p -> p.getStatus() == PropertyStatus.AVAILABLE)
            .orElseThrow(() -> new PropertyNotFoundException(command.propertyId()));

    // 2. Define o tipo de contato com base na finalidade do imovel
    ContactType contactType =
        property.getPurpose() == PropertyPurpose.RENT
            ? ContactType.TENANT_SEEKER
            : ContactType.BUYER;

    // 3. Monta observacoes com a mensagem do cliente e mencao explicita ao consentimento LGPD
    String notes =
        "Interesse no imovel "
            + property.getCode()
            + " ("
            + property.getTitle()
            + ")\n"
            + "Mensagem do visitante: "
            + (command.message() != null && !command.message().isBlank()
                ? command.message().trim()
                : "Sem mensagem adicional")
            + "\nConsentimento LGPD aceito em "
            + Instant.now();

    // 4. Cria o Lead no funil comercial do CRM
    CreateLeadCommand leadCommand =
        new CreateLeadCommand(
            command.tenantId(),
            command.name(),
            command.email(),
            command.phone(),
            contactType,
            LeadStatus.NEW,
            LeadSource.WEBSITE,
            null,
            property.getId(),
            null,
            property.getPricing() != null ? property.getPricing().salePrice() : null,
            notes,
            null);

    LeadResponse lead = createLeadUseCase.execute(leadCommand);

    return new InquiryResponse(
        lead.id(),
        "Sua solicitacao de contato foi recebida com sucesso. Em breve um de nossos corretores entrara em contato.",
        Instant.now());
  }

  public record SubmitInquiryCommand(
      UUID tenantId,
      UUID propertyId,
      String name,
      String email,
      String phone,
      String message,
      boolean lgpdConsent) {}
}
