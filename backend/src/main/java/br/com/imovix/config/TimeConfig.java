package br.com.imovix.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Fonte única de tempo da aplicação, sempre em UTC.
 *
 * <p>Injete {@link Clock} em vez de chamar {@code Instant.now()} diretamente: facilita testes
 * determinísticos e garante timestamps consistentes.
 */
@Configuration(proxyBeanMethods = false)
class TimeConfig {

  @Bean
  Clock clock() {
    return Clock.systemUTC();
  }
}
