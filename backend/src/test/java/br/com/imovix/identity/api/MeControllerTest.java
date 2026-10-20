package br.com.imovix.identity.api;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.imovix.companies.domain.MembershipRole;
import br.com.imovix.companies.domain.MembershipStatus;
import br.com.imovix.identity.application.GetMyProfileUseCase;
import br.com.imovix.identity.application.GetUserProfileUseCase;
import br.com.imovix.identity.application.UserMembershipDto;
import br.com.imovix.identity.application.UserProfileDetailResponse;
import br.com.imovix.identity.domain.UserStatus;
import br.com.imovix.security.CorsProperties;
import br.com.imovix.shared.error.GlobalExceptionHandler;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MeController.class)
@Import(GlobalExceptionHandler.class)
@EnableConfigurationProperties(CorsProperties.class)
@DisplayName("Testes WebMvc: MeController")
class MeControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private GetMyProfileUseCase getMyProfileUseCase;
  @MockitoBean private GetUserProfileUseCase getUserProfileUseCase;

  @Test
  @WithMockUser
  @DisplayName("GET /api/v1/me com header X-User-Id deve retornar perfil e empresas associadas")
  void shouldReturnProfileWithCompanies() throws Exception {
    UUID userId = UUID.randomUUID();
    UUID companyId = UUID.randomUUID();

    UserMembershipDto membership =
        new UserMembershipDto(
            companyId, "Imobiliária Central", MembershipRole.ADMIN, MembershipStatus.ACTIVE);

    UserProfileDetailResponse response =
        new UserProfileDetailResponse(
            userId, "admin@imovix.com.br", "Administrador", UserStatus.ACTIVE, List.of(membership));

    when(getMyProfileUseCase.execute(userId)).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/me").header("X-User-Id", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.email").value("admin@imovix.com.br"))
        .andExpect(jsonPath("$.companies[0].companyId").value(companyId.toString()))
        .andExpect(jsonPath("$.companies[0].companyName").value("Imobiliária Central"))
        .andExpect(jsonPath("$.companies[0].role").value("ADMIN"));
  }
}
