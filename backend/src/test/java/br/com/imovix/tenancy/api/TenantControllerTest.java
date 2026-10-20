package br.com.imovix.tenancy.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.imovix.companies.domain.Company;
import br.com.imovix.companies.domain.CompanyRepository;
import br.com.imovix.companies.domain.CompanyStatus;
import br.com.imovix.security.CorsProperties;
import br.com.imovix.shared.error.GlobalExceptionHandler;
import br.com.imovix.tenancy.application.TenantContext;
import br.com.imovix.tenancy.domain.TenantId;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = TenantController.class)
@Import(GlobalExceptionHandler.class)
@EnableConfigurationProperties(CorsProperties.class)
@DisplayName("Testes WebMvc: TenantController")
class TenantControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CompanyRepository companyRepository;

  @AfterEach
  void tearDown() {
    TenantContext.clear();
  }

  @Test
  @WithMockUser
  @DisplayName("GET /api/v1/tenants/current com tenant ativo deve retornar dados do tenant")
  void shouldReturnCurrentTenant() throws Exception {
    UUID tenantId = UUID.randomUUID();
    TenantContext.set(TenantId.from(tenantId));

    Company company =
        new Company(
            tenantId,
            "Imobiliária Modelo",
            "Modelo Imóveis",
            "12345678000100",
            "CRECI-999",
            CompanyStatus.ACTIVE,
            null,
            null);

    when(companyRepository.findById(tenantId)).thenReturn(Optional.of(company));

    mockMvc
        .perform(get("/api/v1/tenants/current"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tenantId").value(tenantId.toString()))
        .andExpect(jsonPath("$.companyName").value("Imobiliária Modelo"));
  }

  @Test
  @WithMockUser
  @DisplayName("GET /api/v1/tenants/current sem tenant ativo deve retornar 400 Problem Details")
  void shouldReturn400WhenNoTenantActive() throws Exception {
    TenantContext.clear();

    mockMvc
        .perform(get("/api/v1/tenants/current"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Contexto de Tenant Não Resolvido"));
  }
}
