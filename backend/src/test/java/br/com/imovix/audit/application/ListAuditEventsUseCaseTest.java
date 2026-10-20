package br.com.imovix.audit.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.imovix.audit.domain.AuditEvent;
import br.com.imovix.audit.domain.AuditEventRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListAuditEventsUseCaseTest {

  @Mock private AuditEventRepository auditEventRepository;

  @InjectMocks private ListAuditEventsUseCase listAuditEventsUseCase;

  @Test
  @DisplayName("Deve listar eventos de auditoria paginados corretamente")
  void shouldListAuditEventsPaged() {
    UUID tenantId = UUID.randomUUID();
    AuditEvent event =
        AuditEvent.create(
            tenantId,
            UUID.randomUUID(),
            "admin@imovix.com.br",
            "PLAN_CHANGED",
            "SUBSCRIPTION",
            "sub-1",
            "{}",
            "127.0.0.1",
            "cid-10");

    when(auditEventRepository.findByTenantId(tenantId, 0, 10)).thenReturn(List.of(event));
    when(auditEventRepository.countByTenantId(tenantId)).thenReturn(1L);

    PagedAuditLogsResponse response = listAuditEventsUseCase.execute(tenantId, 0, 10);

    assertThat(response.content()).hasSize(1);
    assertThat(response.totalElements()).isEqualTo(1L);
    assertThat(response.totalPages()).isEqualTo(1);
    assertThat(response.content().getFirst().action()).isEqualTo("PLAN_CHANGED");
    verify(auditEventRepository).findByTenantId(tenantId, 0, 10);
  }
}
