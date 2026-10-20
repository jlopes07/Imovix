package br.com.imovix.tenancy.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.imovix.tenancy.application.TenantContext;
import br.com.imovix.tenancy.application.TenantResolver;
import br.com.imovix.tenancy.domain.CrossTenantAccessException;
import br.com.imovix.tenancy.domain.TenantId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Integração do Filtro: TenantFilter")
class TenantFilterTest {

  @Mock private TenantResolver tenantResolver;
  @Mock private FilterChain filterChain;
  @InjectMocks private TenantFilter tenantFilter;

  @AfterEach
  void tearDown() {
    TenantContext.clear();
  }

  @Test
  @DisplayName("Deve extrair usuário e tenant, propagar no header e limpar o contexto ao final")
  void shouldResolveTenantAndCleanUpAfterExecution() throws ServletException, IOException {
    UUID userId = UUID.randomUUID();
    UUID tenantId = UUID.randomUUID();

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/properties");
    request.addHeader("X-User-Id", userId.toString());
    request.addHeader("X-Tenant-Id", tenantId.toString());

    MockHttpServletResponse response = new MockHttpServletResponse();

    when(tenantResolver.resolve(userId, tenantId)).thenReturn(TenantId.from(tenantId));

    tenantFilter.doFilter(request, response, filterChain);

    assertThat(response.getHeader(TenantFilter.TENANT_HEADER)).isEqualTo(tenantId.toString());
    verify(filterChain).doFilter(request, response);

    // Garante que a thread foi limpa após o fim da requisição
    assertThat(TenantContext.get()).isEmpty();
  }

  @Test
  @DisplayName(
      "Deve propagar CrossTenantAccessException e garantir que a thread foi limpa no finally")
  void shouldPropagateSecurityExceptionAndClearContextOnViolation() {
    UUID userId = UUID.randomUUID();
    UUID foreignTenantId = UUID.randomUUID();

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/properties");
    request.addHeader("X-User-Id", userId.toString());
    request.addHeader("X-Tenant-Id", foreignTenantId.toString());

    MockHttpServletResponse response = new MockHttpServletResponse();

    when(tenantResolver.resolve(userId, foreignTenantId))
        .thenThrow(new CrossTenantAccessException(userId, foreignTenantId));

    assertThatThrownBy(() -> tenantFilter.doFilter(request, response, filterChain))
        .isInstanceOf(CrossTenantAccessException.class);

    // Garante que o contexto não ficou contaminado
    assertThat(TenantContext.get()).isEmpty();
  }

  @Test
  @DisplayName("Rotas de bypass (/actuator) não devem exigir resolução de tenant")
  void shouldBypassActuatorRoutes() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
    MockHttpServletResponse response = new MockHttpServletResponse();

    tenantFilter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertThat(TenantContext.get()).isEmpty();
  }
}
