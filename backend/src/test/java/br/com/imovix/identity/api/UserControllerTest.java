package br.com.imovix.identity.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.imovix.identity.application.RegisterUserCommand;
import br.com.imovix.identity.application.RegisterUserUseCase;
import br.com.imovix.identity.application.UserResponse;
import br.com.imovix.identity.domain.UserStatus;
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

@WebMvcTest(controllers = UserController.class)
@Import(GlobalExceptionHandler.class)
@EnableConfigurationProperties(CorsProperties.class)
@DisplayName("Testes WebMvc: UserController")
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private RegisterUserUseCase registerUserUseCase;

  @Test
  @WithMockUser
  @DisplayName("POST /api/v1/users deve criar usuário e retornar 201 Created")
  void shouldRegisterUserSuccessfully() throws Exception {
    UUID userId = UUID.randomUUID();
    UserResponse response =
        new UserResponse(
            userId,
            "contato@imovix.com.br",
            "Carlos Silva",
            UserStatus.ACTIVE,
            Instant.now(),
            Instant.now());

    when(registerUserUseCase.execute(any(RegisterUserCommand.class))).thenReturn(response);

    String payload =
        """
        {
          "email": "contato@imovix.com.br",
          "name": "Carlos Silva",
          "password": "senhaSegura123"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.email").value("contato@imovix.com.br"))
        .andExpect(jsonPath("$.name").value("Carlos Silva"))
        .andExpect(jsonPath("$.status").value("ACTIVE"));
  }

  @Test
  @WithMockUser
  @DisplayName("POST /api/v1/users com e-mail inválido deve retornar 400 Problem Details")
  void shouldReturn400WhenEmailIsInvalid() throws Exception {
    String payload =
        """
        {
          "email": "email-invalido",
          "name": "Carlos Silva",
          "password": "123"
        }
        """;

    mockMvc
        .perform(
            post("/api/v1/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.title").value("Erro de Validação"))
        .andExpect(jsonPath("$.invalidParams.email").exists())
        .andExpect(jsonPath("$.invalidParams.password").exists());
  }
}
