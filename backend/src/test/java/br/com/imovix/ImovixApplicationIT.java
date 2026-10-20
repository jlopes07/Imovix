package br.com.imovix;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.DockerClientFactory;

/**
 * Sobe o contexto completo contra um PostgreSQL real (Testcontainers) quando o Docker está ativo.
 */
@EnabledIf("isDockerAvailable")
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ImovixApplicationIT {

  static boolean isDockerAvailable() {
    try {
      return DockerClientFactory.instance().isDockerAvailable();
    } catch (Throwable ignored) {
      return false;
    }
  }

  @Autowired private JdbcTemplate jdbcTemplate;

  @Test
  void contextLoadsAndConnectsToPostgres() {
    var version = jdbcTemplate.queryForObject("SHOW server_version", String.class);

    assertThat(version).startsWith("18");
  }
}
