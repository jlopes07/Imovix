package br.com.imovix.tenancy.application;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.imovix.tenancy.domain.CrossTenantAccessException;
import br.com.imovix.tenancy.domain.TenantId;
import br.com.imovix.tenancy.domain.TenantScoped;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes Unitários: TenantSecurityEnforcer")
class TenantSecurityEnforcerTest {

  private TenantSecurityEnforcer enforcer;
  private UUID currentTenantId;

  @BeforeEach
  void setUp() {
    enforcer = new TenantSecurityEnforcer();
    currentTenantId = UUID.randomUUID();
    TenantContext.set(TenantId.from(currentTenantId));
  }

  @AfterEach
  void tearDown() {
    TenantContext.clear();
  }

  @Test
  @DisplayName("Deve permitir acesso quando recurso pertencer ao tenant corrente")
  void shouldAllowAccessWhenSameTenant() {
    TenantScoped validResource = () -> currentTenantId;

    assertThatCode(() -> enforcer.enforce(validResource)).doesNotThrowAnyException();
    assertThatCode(() -> enforcer.enforce(currentTenantId)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Deve lançar CrossTenantAccessException quando recurso pertencer a outro tenant")
  void shouldThrowWhenResourceBelongsToDifferentTenant() {
    UUID otherTenantId = UUID.randomUUID();
    TenantScoped foreignResource = () -> otherTenantId;

    assertThatThrownBy(() -> enforcer.enforce(foreignResource))
        .isInstanceOf(CrossTenantAccessException.class)
        .hasMessageContaining("Tentativa de violação multi-tenant");

    assertThatThrownBy(() -> enforcer.enforce(otherTenantId))
        .isInstanceOf(CrossTenantAccessException.class)
        .hasMessageContaining("Tentativa de violação multi-tenant");
  }
}
