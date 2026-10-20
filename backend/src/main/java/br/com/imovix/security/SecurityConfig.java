package br.com.imovix.security;

import br.com.imovix.tenancy.api.TenantFilter;
import java.util.Optional;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.web.cors.CorsConfigurationSource;

/** Configuração base de segurança HTTP: <strong>fail-closed</strong>. */
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
@EnableConfigurationProperties(CorsProperties.class)
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /** CSP para respostas de API: nada é carregado, nada pode embutir a resposta. */
  static final String CONTENT_SECURITY_POLICY =
      "default-src 'none'; frame-ancestors 'none'; base-uri 'none'; form-action 'none'";

  @Bean
  SecurityFilterChain apiSecurityFilterChain(
      HttpSecurity http,
      CorsConfigurationSource corsConfigurationSource,
      CorrelationIdFilter correlationIdFilter,
      UserAuthenticationFilter userAuthenticationFilter,
      Optional<TenantFilter> tenantFilterOpt)
      throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource))
        .csrf(AbstractHttpConfigurer::disable)
        .addFilterBefore(correlationIdFilter, SecurityContextHolderFilter.class)
        .addFilterAfter(userAuthenticationFilter, SecurityContextHolderFilter.class);

    tenantFilterOpt.ifPresent(
        filter -> http.addFilterAfter(filter, UserAuthenticationFilter.class));

    return http.httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            exceptions ->
                exceptions.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
        .headers(
            headers ->
                headers
                    .contentSecurityPolicy(csp -> csp.policyDirectives(CONTENT_SECURITY_POLICY))
                    .referrerPolicy(referrer -> referrer.policy(ReferrerPolicy.NO_REFERRER))
                    .frameOptions(frame -> frame.deny()))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/actuator/health",
                        "/actuator/health/**",
                        "/actuator/info",
                        "/actuator/prometheus")
                    .permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/users")
                    .permitAll()
                    .requestMatchers("/api/v1/public/**", "/api/v1/plans")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .build();
  }
}
