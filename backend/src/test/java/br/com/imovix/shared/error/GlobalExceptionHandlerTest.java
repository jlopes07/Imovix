package br.com.imovix.shared.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.imovix.security.CorrelationIdFilter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.DummyController.class)
@Import({
  GlobalExceptionHandlerTest.DummyController.class,
  GlobalExceptionHandler.class,
  CorrelationIdFilter.class
})
class GlobalExceptionHandlerTest {

  @Autowired private WebApplicationContext context;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.webAppContextSetup(context).addFilters(new CorrelationIdFilter()).build();
  }

  public record DummyRequest(@NotBlank(message = "O campo nome é obrigatório") String name) {}

  @RestController
  public static class DummyController {
    @PostMapping("/test/validation")
    public void testValidation(@Valid @RequestBody DummyRequest request) {}
  }

  @Test
  void returnsRfc7807ProblemDetailOnValidationError() throws Exception {
    mockMvc
        .perform(
            post("/test/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .header(CorrelationIdFilter.CORRELATION_ID_HEADER, "test-cid-999"))
        .andExpect(status().isBadRequest())
        .andExpect(header().string(CorrelationIdFilter.CORRELATION_ID_HEADER, "test-cid-999"))
        .andExpect(jsonPath("$.type").value("https://api.imovix.com.br/errors/validation-failed"))
        .andExpect(jsonPath("$.title").value("Erro de Validação"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.correlationId").value("test-cid-999"))
        .andExpect(jsonPath("$.invalidParams.name").value("O campo nome é obrigatório"));
  }
}
