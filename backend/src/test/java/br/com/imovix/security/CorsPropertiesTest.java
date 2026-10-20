package br.com.imovix.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;

class CorsPropertiesTest {

  @Test
  void rejectsWildcardOrigin() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new CorsProperties(List.of("*"), false, null));
  }

  @Test
  void rejectsWildcardSubdomainPattern() {
    assertThatIllegalArgumentException()
        .isThrownBy(() -> new CorsProperties(List.of("https://*.example.com"), false, null));
  }

  @Test
  void normalizesOriginsAndAppliesDefaults() {
    var properties = new CorsProperties(List.of(" http://localhost:5173 ", ""), false, null);

    assertThat(properties.allowedOrigins()).containsExactly("http://localhost:5173");
    assertThat(properties.maxAge()).isEqualTo(Duration.ofHours(1));
  }

  @Test
  void defaultsToNoOrigins() {
    assertThat(new CorsProperties(null, false, null).allowedOrigins()).isEmpty();
  }
}
