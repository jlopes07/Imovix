package br.com.imovix.audit.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.imovix.audit.domain.AuditEvent;
import br.com.imovix.audit.domain.AuditEventRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecordAuditEventUseCaseTest {

  @Mock private AuditEventRepository auditEventRepository;

  @InjectMocks private RecordAuditEventUseCase recordAuditEventUseCase;

  @Test
  @DisplayName("Deve registrar evento de auditoria com sucesso")
  void shouldRecordAuditEventSuccessfully() {
    UUID tenantId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    when(auditEventRepository.save(any(AuditEvent.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    AuditLogResponse response =
        recordAuditEventUseCase.execute(
            tenantId,
            userId,
            "corretor@imovix.com.br",
            "PROPERTY_DELETED",
            "PROPERTY",
            "prop-99",
            "{\"reason\": \"Vendido\"}",
            "10.0.0.1",
            "cid-test");

    assertThat(response.tenantId()).isEqualTo(tenantId);
    assertThat(response.userId()).isEqualTo(userId);
    assertThat(response.action()).isEqualTo("PROPERTY_DELETED");
    assertThat(response.resourceType()).isEqualTo("PROPERTY");
    assertThat(response.resourceId()).isEqualTo("prop-99");
    verify(auditEventRepository).save(any(AuditEvent.class));
  }
}
