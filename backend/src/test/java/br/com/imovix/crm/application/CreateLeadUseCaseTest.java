package br.com.imovix.crm.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.imovix.crm.application.CreateLeadUseCase.CreateLeadCommand;
import br.com.imovix.crm.domain.ContactType;
import br.com.imovix.crm.domain.Lead;
import br.com.imovix.crm.domain.LeadRepository;
import br.com.imovix.crm.domain.LeadSource;
import br.com.imovix.crm.domain.LeadStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateLeadUseCaseTest {

  @Mock private LeadRepository leadRepository;

  private CreateLeadUseCase createLeadUseCase;

  @BeforeEach
  void setUp() {
    createLeadUseCase = new CreateLeadUseCase(leadRepository);
  }

  @Test
  @DisplayName("Deve cadastrar lead e persistir no tenant correto")
  void shouldCreateLeadSuccessfully() {
    UUID tenantId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    when(leadRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

    CreateLeadCommand command =
        new CreateLeadCommand(
            tenantId,
            "Roberto Carlos",
            "roberto@exemplo.com",
            "(11) 97777-6666",
            ContactType.BUYER,
            LeadStatus.NEW,
            LeadSource.WEBSITE,
            null,
            null,
            new BigDecimal("300000"),
            new BigDecimal("600000"),
            "Cliente busca financiamento CEF",
            userId);

    LeadResponse response = createLeadUseCase.execute(command);

    assertNotNull(response.id());
    assertEquals(tenantId, response.tenantId());
    assertEquals("Roberto Carlos", response.name());
    assertEquals(LeadStatus.NEW, response.status());

    verify(leadRepository).save(any(Lead.class));
  }
}
