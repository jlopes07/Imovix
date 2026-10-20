package br.com.imovix.audit.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuditEventTest {

  @Test
  @DisplayName("Deve criar evento de auditoria com sucesso")
  void shouldCreateAuditEventSuccessfully() {
    UUID tenantId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    AuditEvent event =
        AuditEvent.create(
            tenantId,
            userId,
            "corretor@imovix.com.br",
            "PROPERTY_CREATED",
            "PROPERTY",
            "prop-123",
            "{\"title\": \"Apartamento Jardins\"}",
            "192.168.1.1",
            "cid-999");

    assertThat(event.getTenantId()).isEqualTo(tenantId);
    assertThat(event.getUserId()).isEqualTo(userId);
    assertThat(event.getUserEmail()).isEqualTo("corretor@imovix.com.br");
    assertThat(event.getAction()).isEqualTo("PROPERTY_CREATED");
    assertThat(event.getResourceType()).isEqualTo("PROPERTY");
    assertThat(event.getResourceId()).isEqualTo("prop-123");
    assertThat(event.getDetails()).contains("Jardins");
    assertThat(event.getIpAddress()).isEqualTo("192.168.1.1");
    assertThat(event.getCorrelationId()).isEqualTo("cid-999");
    assertThat(event.getCreatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Deve rejeitar criacao sem tenantId obrigatorio")
  void shouldRejectCreationWithoutTenantId() {
    assertThatThrownBy(
            () ->
                AuditEvent.create(
                    null,
                    UUID.randomUUID(),
                    "email@test.com",
                    "PROPERTY_CREATED",
                    "PROPERTY",
                    "1",
                    "{}",
                    "127.0.0.1",
                    "cid"))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("TenantId e obrigatorio");
  }

  @Test
  @DisplayName("Deve rejeitar criacao sem action obrigatoria")
  void shouldRejectCreationWithoutAction() {
    assertThatThrownBy(
            () ->
                AuditEvent.create(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "email@test.com",
                    null,
                    "PROPERTY",
                    "1",
                    "{}",
                    "127.0.0.1",
                    "cid"))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Acao e obrigatoria");
  }
}
