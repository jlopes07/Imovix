package br.com.imovix.crm.application;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import br.com.imovix.crm.domain.LeadNotFoundException;
import br.com.imovix.crm.domain.LeadRepository;
import br.com.imovix.crm.domain.LeadStatus;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrossTenantCrmTest {

  @Mock private LeadRepository leadRepository;

  private GetLeadUseCase getLeadUseCase;
  private UpdateLeadStatusUseCase updateLeadStatusUseCase;

  @BeforeEach
  void setUp() {
    getLeadUseCase = new GetLeadUseCase(leadRepository);
    updateLeadStatusUseCase = new UpdateLeadStatusUseCase(leadRepository);
  }

  @Test
  @DisplayName("Nao deve permitir obter lead pertencente a outro tenant")
  void shouldNotAllowAccessToLeadFromAnotherTenant() {
    UUID tenantA = UUID.randomUUID();
    UUID tenantB = UUID.randomUUID();
    UUID leadId = UUID.randomUUID();

    when(leadRepository.findByTenantIdAndId(tenantA, leadId)).thenReturn(Optional.empty());

    assertThrows(LeadNotFoundException.class, () -> getLeadUseCase.execute(tenantA, leadId));
  }

  @Test
  @DisplayName("Nao deve permitir alterar status de lead de outro tenant")
  void shouldNotAllowStatusUpdateOnLeadFromAnotherTenant() {
    UUID tenantA = UUID.randomUUID();
    UUID leadId = UUID.randomUUID();

    when(leadRepository.findByTenantIdAndId(tenantA, leadId)).thenReturn(Optional.empty());

    assertThrows(
        LeadNotFoundException.class,
        () -> updateLeadStatusUseCase.execute(tenantA, leadId, LeadStatus.WON, UUID.randomUUID()));
  }
}
