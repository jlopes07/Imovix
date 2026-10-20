package br.com.imovix.companies.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.imovix.companies.application.AddMemberCommand;
import br.com.imovix.companies.application.AddMemberUseCase;
import br.com.imovix.companies.application.CompanyMemberResponse;
import br.com.imovix.companies.application.ListCompanyMembersUseCase;
import br.com.imovix.companies.domain.MembershipRole;
import br.com.imovix.companies.domain.MembershipStatus;
import br.com.imovix.security.CorsProperties;
import br.com.imovix.shared.error.GlobalExceptionHandler;
import java.time.Instant;
import java.util.List;
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

@WebMvcTest(controllers = CompanyMemberController.class)
@Import(GlobalExceptionHandler.class)
@EnableConfigurationProperties(CorsProperties.class)
@DisplayName("Testes WebMvc: CompanyMemberController")
class CompanyMemberControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ListCompanyMembersUseCase listCompanyMembersUseCase;
  @MockitoBean private AddMemberUseCase addMemberUseCase;

  @Test
  @WithMockUser
  @DisplayName("GET /api/v1/companies/{companyId}/members deve listar membros da empresa")
  void shouldListCompanyMembers() throws Exception {
    UUID companyId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID membershipId = UUID.randomUUID();

    CompanyMemberResponse member =
        new CompanyMemberResponse(
            membershipId,
            companyId,
            userId,
            "Roberto Gerente",
            "gerente@imovix.com.br",
            MembershipRole.MANAGER,
            MembershipStatus.ACTIVE,
            Instant.now());

    when(listCompanyMembersUseCase.execute(companyId)).thenReturn(List.of(member));

    mockMvc
        .perform(get("/api/v1/companies/" + companyId + "/members"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].membershipId").value(membershipId.toString()))
        .andExpect(jsonPath("$[0].companyId").value(companyId.toString()))
        .andExpect(jsonPath("$[0].userId").value(userId.toString()))
        .andExpect(jsonPath("$[0].userName").value("Roberto Gerente"))
        .andExpect(jsonPath("$[0].role").value("MANAGER"));
  }

  @Test
  @WithMockUser
  @DisplayName("POST /api/v1/companies/{companyId}/members deve adicionar membro")
  void shouldAddCompanyMember() throws Exception {
    UUID companyId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID membershipId = UUID.randomUUID();

    CompanyMemberResponse response =
        new CompanyMemberResponse(
            membershipId,
            companyId,
            userId,
            "Ana Corretora",
            "ana@imovix.com.br",
            MembershipRole.REALTOR,
            MembershipStatus.ACTIVE,
            Instant.now());

    when(addMemberUseCase.execute(any(AddMemberCommand.class))).thenReturn(response);

    String payload =
        String.format(
            """
            {
              "userId": "%s",
              "role": "REALTOR"
            }
            """,
            userId);

    mockMvc
        .perform(
            post("/api/v1/companies/" + companyId + "/members")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.membershipId").value(membershipId.toString()))
        .andExpect(jsonPath("$.role").value("REALTOR"));
  }

  @Test
  @WithMockUser
  @DisplayName("GET /api/v1/members com cabeçalho X-Company-Id deve listar membros")
  void shouldListCurrentCompanyMembers() throws Exception {
    UUID companyId = UUID.randomUUID();
    when(listCompanyMembersUseCase.execute(companyId)).thenReturn(List.of());

    mockMvc
        .perform(get("/api/v1/members").header("X-Company-Id", companyId.toString()))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  @DisplayName("GET /api/v1/members sem cabeçalho X-Company-Id deve retornar 400 Bad Request")
  void shouldReturn400WhenCompanyHeaderMissing() throws Exception {
    mockMvc.perform(get("/api/v1/members")).andExpect(status().isBadRequest());
  }
}
