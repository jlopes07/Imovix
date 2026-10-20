package br.com.imovix.security;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** Registra a política de CORS somente para a API versionada ({@code /api/**}). */
@Configuration(proxyBeanMethods = false)
class CorsConfig {

  static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

  @Bean
  CorsConfigurationSource corsConfigurationSource(CorsProperties properties) {
    var config = new CorsConfiguration();
    config.setAllowedOrigins(properties.allowedOrigins());
    config.setAllowedMethods(
        List.of(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name()));
    config.setAllowedHeaders(
        List.of(
            HttpHeaders.AUTHORIZATION,
            HttpHeaders.CONTENT_TYPE,
            HttpHeaders.ACCEPT,
            CORRELATION_ID_HEADER));
    config.setExposedHeaders(List.of(HttpHeaders.LOCATION, CORRELATION_ID_HEADER));
    config.setAllowCredentials(properties.allowCredentials());
    config.setMaxAge(properties.maxAge());

    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
  }
}
