package br.com.imovix;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.imovix.security.CorrelationIdFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/** Valida que os endpoints de observabilidade do Actuator estão acessíveis e protegidos. */
@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:actuator_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
      "spring.jpa.hibernate.ddl-auto=none",
      "spring.flyway.enabled=false"
    })
@AutoConfigureMockMvc
class ActuatorHealthTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void healthEndpointReturnsUpAndCorrelationId() throws Exception {
    mockMvc
        .perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"))
        .andExpect(header().exists(CorrelationIdFilter.CORRELATION_ID_HEADER));
  }

  @Test
  void healthEndpointRespectsClientProvidedCorrelationId() throws Exception {
    String clientCid = "cid-test-12345";
    mockMvc
        .perform(
            get("/actuator/health").header(CorrelationIdFilter.CORRELATION_ID_HEADER, clientCid))
        .andExpect(status().isOk())
        .andExpect(header().string(CorrelationIdFilter.CORRELATION_ID_HEADER, clientCid));
  }
}
