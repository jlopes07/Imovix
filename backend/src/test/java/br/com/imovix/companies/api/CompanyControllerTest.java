package br.com.imovix.companies.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.imovix.companies.application.CompanyResponse;
import br.com.imovix.companies.application.CreateCompanyCommand;
import br.com.imovix.companies.application.CreateCompanyUseCase;
import br.com.imovix.companies.application.GetCompanyUseCase;
import br.com.imovix.companies.domain.CompanyStatus;
import br.com.imovix.security.CorsProperties;
import br.com.imovix.shared.error.GlobalExceptionHandler;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CompanyController.class)
@Import(GlobalExceptionHandler.class)
@EnableConfigurationProperties(CorsProperties.class)
@DisplayName("Testes WebMvc: CompanyController")
class CompanyControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CreateCompanyUseCase createCompanyUseCase;
  @MockitoBean private GetCompanyUseCase getCompanyUseCase;

  @Test
  @WithMockUser
  @DisplayName("POST /api/v1/companies deve criar empresa e retornar 201 Created")
  void shouldCreateCompanySuccessfully() throws Exception {
    UUID companyId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    CompanyResponse response =
        new CompanyResponse(
            companyId,
            "Imobiliária Sucesso Ltda",
            "Sucesso Imóveis",
            "12345678000199",
            "CRECI-12345",
            CompanyStatus.ACTIVE,
            Instant.now(),
            Instant.now());

    when(createCompanyUseCase.execute(any(CreateCompanyCommand.class))).thenReturn(response);

    String payload =
        """
        {
          "name": "Imobiliária Sucesso Ltda",
          "tradeName": "Sucesso Imóveis",
          "documentNumber": "12.345.678/0001-99",
          "creci": "CRECI-12345"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/companies")
                .with(csrf())
                .header("X-User-Id", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.id").value(companyId.toString()))
        .andExpect(jsonPath("$.name").value("Imobiliária Sucesso Ltda"))
        .andExpect(jsonPath("$.tradeName").value("Sucesso Imóveis"));
  }

  @Test
  @WithMockUser
  @DisplayName("GET /api/v1/companies/{id} deve retornar detalhes da empresa")
  void shouldGetCompanyById() throws Exception {
    UUID companyId = UUID.randomUUID();
    CompanyResponse response =
        new CompanyResponse(
            companyId,
            "Imobiliária Sucesso Ltda",
            "Sucesso Imóveis",
            "12345678000199",
            "CRECI-12345",
            CompanyStatus.ACTIVE,
            Instant.now(),
            Instant.now());

    when(getCompanyUseCase.execute(companyId)).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/companies/" + companyId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(companyId.toString()))
        .andExpect(jsonPath("$.name").value("Imobiliária Sucesso Ltda"));
  }

  @Test
  @WithMockUser
  @DisplayName("GET /api/v1/companies/current sem X-Company-Id deve retornar 400 Bad Request")
  void shouldReturn400WhenCurrentCompanyHeaderMissing() throws Exception {
    mockMvc.perform(get("/api/v1/companies/current")).andExpect(status().isBadRequest());
  }
}
