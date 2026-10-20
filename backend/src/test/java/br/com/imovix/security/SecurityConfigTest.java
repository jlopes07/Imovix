package br.com.imovix.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(
    controllers = SecurityConfigTest.DummySecurityController.class,
    properties = "imovix.security.cors.allowed-origins=http://localhost:5173")
@Import({SecurityConfig.class, CorsConfig.class, CorrelationIdFilter.class})
class SecurityConfigTest {

  @RestController
  static class DummySecurityController {
    @GetMapping("/api/v1/me")
    public String me() {
      return "me";
    }

    @GetMapping("/api/v1/properties")
    public String properties() {
      return "properties";
    }
  }

  private static final String ALLOWED_ORIGIN = "http://localhost:5173";
  private static final String FOREIGN_ORIGIN = "https://evil.example";

  @Autowired private MockMvc mockMvc;

  @Test
  void deniesAnonymousRequestsByDefault() throws Exception {
    mockMvc.perform(get("/api/v1/me")).andExpect(status().isUnauthorized());
  }

  @Test
  void sendsSecurityHeaders() throws Exception {
    mockMvc
        .perform(get("/api/v1/me"))
        .andExpect(header().string("X-Content-Type-Options", "nosniff"))
        .andExpect(header().string("X-Frame-Options", "DENY"))
        .andExpect(header().string("Referrer-Policy", "no-referrer"))
        .andExpect(
            header().string("Content-Security-Policy", SecurityConfig.CONTENT_SECURITY_POLICY));
  }

  @Test
  void doesNotCreateHttpSession() throws Exception {
    mockMvc.perform(get("/api/v1/me")).andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));
  }

  @Test
  void allowsPreflightFromConfiguredOrigin() throws Exception {
    mockMvc
        .perform(
            options("/api/v1/properties")
                .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN));
  }

  @Test
  void rejectsPreflightFromUnknownOrigin() throws Exception {
    mockMvc
        .perform(
            options("/api/v1/properties")
                .header(HttpHeaders.ORIGIN, FOREIGN_ORIGIN)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
        .andExpect(status().isForbidden())
        .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
  }
}
