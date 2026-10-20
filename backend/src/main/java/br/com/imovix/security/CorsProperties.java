package br.com.imovix.security;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Política de CORS da API, configurada via {@code imovix.security.cors.*}.
 *
 * <p>CORS não é mecanismo de autenticação; apenas restringe quais origens de navegador podem ler as
 * respostas. Por isso, o curinga {@code *} é proibido.
 *
 * @param allowedOrigins origens exatas permitidas (ex.: {@code https://app.imovix.com.br})
 * @param allowCredentials se cookies/credenciais podem ser enviados em requisições cross-origin
 * @param maxAge tempo de cache do preflight no navegador
 */
@ConfigurationProperties(prefix = "imovix.security.cors")
public record CorsProperties(
    List<String> allowedOrigins, boolean allowCredentials, Duration maxAge) {

  private static final String WILDCARD = "*";
  private static final Duration DEFAULT_MAX_AGE = Duration.ofHours(1);

  public CorsProperties {
    allowedOrigins =
        allowedOrigins == null
            ? List.of()
            : allowedOrigins.stream().map(String::strip).filter(o -> !o.isEmpty()).toList();

    if (allowedOrigins.stream().anyMatch(o -> o.contains(WILDCARD))) {
      throw new IllegalArgumentException(
          "imovix.security.cors.allowed-origins não pode conter '*'; informe origens explícitas.");
    }
    maxAge = maxAge == null ? DEFAULT_MAX_AGE : maxAge;
  }
}
