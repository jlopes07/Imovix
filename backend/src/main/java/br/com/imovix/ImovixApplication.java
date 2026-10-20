package br.com.imovix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

/**
 * Ponto de entrada da API do Imovix (monólito modular).
 *
 * <p>{@link UserDetailsServiceAutoConfiguration} é excluída para que o Spring Boot não gere um
 * usuário/senha padrão em memória. A autenticação será fornecida pelo módulo {@code identity}.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class ImovixApplication {

  public static void main(String[] args) {
    SpringApplication.run(ImovixApplication.class, args);
  }
}
