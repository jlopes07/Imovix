package br.com.imovix.listings.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.imovix.crm.application.CreateLeadUseCase;
import br.com.imovix.crm.application.CreateLeadUseCase.CreateLeadCommand;
import br.com.imovix.crm.application.LeadResponse;
import br.com.imovix.crm.domain.ContactType;
import br.com.imovix.crm.domain.LeadSource;
import br.com.imovix.crm.domain.LeadStatus;
import br.com.imovix.listings.application.SubmitLeadInquiryUseCase.SubmitInquiryCommand;
import br.com.imovix.properties.domain.Property;
import br.com.imovix.properties.domain.PropertyNotFoundException;
import br.com.imovix.properties.domain.PropertyPurpose;
import br.com.imovix.properties.domain.PropertyRepository;
import br.com.imovix.properties.domain.PropertyStatus;
import br.com.imovix.properties.domain.PropertyType;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubmitLeadInquiryUseCaseTest {

  @Mock private PropertyRepository propertyRepository;
  @Mock private CreateLeadUseCase createLeadUseCase;

  private SubmitLeadInquiryUseCase useCase;

  @BeforeEach
  void setUp() {
    useCase = new SubmitLeadInquiryUseCase(propertyRepository, createLeadUseCase);
  }

  @Test
  @DisplayName("Deve submeter interesse e gerar lead no CRM associado ao imovel")
  void shouldSubmitInquiryAndCreateLead() {
    UUID tenantId = UUID.randomUUID();
    UUID propertyId = UUID.randomUUID();
    UUID leadId = UUID.randomUUID();

    Property property =
        new Property(
            propertyId,
            tenantId,
            "AP-0001",
            "Apartamento Vista Mar",
            "Excelente acabamento",
            PropertyType.APARTMENT,
            PropertyPurpose.SALE,
            PropertyStatus.AVAILABLE,
            null,
            null,
            null,
            null,
            null,
            Instant.now(),
            Instant.now());

    when(propertyRepository.findByTenantIdAndId(tenantId, propertyId))
        .thenReturn(Optional.of(property));

    LeadResponse mockLeadResponse =
        new LeadResponse(
            leadId,
            tenantId,
            "Ana Souza",
            "ana@exemplo.com",
            "(11) 99999-8888",
            ContactType.BUYER,
            LeadStatus.NEW,
            LeadSource.WEBSITE,
            null,
            propertyId,
            null,
            null,
            "Notas",
            null,
            null,
            Instant.now(),
            Instant.now());

    when(createLeadUseCase.execute(any(CreateLeadCommand.class))).thenReturn(mockLeadResponse);

    SubmitInquiryCommand command =
        new SubmitInquiryCommand(
            tenantId,
            propertyId,
            "Ana Souza",
            "ana@exemplo.com",
            "(11) 99999-8888",
            "Gostaria de agendar uma visita no sabado",
            true);

    InquiryResponse response = useCase.execute(command);

    assertNotNull(response);
    assertEquals(leadId, response.leadId());
    assertNotNull(response.receivedAt());

    verify(createLeadUseCase).execute(any(CreateLeadCommand.class));
  }

  @Test
  @DisplayName("Deve lancar PropertyNotFoundException se o imovel nao estiver disponivel")
  void shouldThrowWhenPropertyNotAvailable() {
    UUID tenantId = UUID.randomUUID();
    UUID propertyId = UUID.randomUUID();

    Property soldProperty =
        new Property(
            propertyId,
            tenantId,
            "CS-0002",
            "Casa Vendida",
            null,
            PropertyType.HOUSE,
            PropertyPurpose.SALE,
            PropertyStatus.SOLD,
            null,
            null,
            null,
            null,
            null,
            Instant.now(),
            Instant.now());

    when(propertyRepository.findByTenantIdAndId(tenantId, propertyId))
        .thenReturn(Optional.of(soldProperty));

    SubmitInquiryCommand command =
        new SubmitInquiryCommand(
            tenantId,
            propertyId,
            "Lucas Mendes",
            "lucas@exemplo.com",
            "(11) 97777-6666",
            "Ainda esta disponivel?",
            true);

    assertThrows(PropertyNotFoundException.class, () -> useCase.execute(command));
  }
}
