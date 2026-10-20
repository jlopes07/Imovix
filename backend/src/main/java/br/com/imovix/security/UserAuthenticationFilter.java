package br.com.imovix.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro de autenticação que extrai e valida a identidade do usuário a partir do cabeçalho {@code
 * X-User-Id} para requisições autenticadas no contexto multi-tenant.
 */
@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {

  public static final String USER_ID_HEADER = "X-User-Id";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String userIdHeader = request.getHeader(USER_ID_HEADER);

    if (userIdHeader != null && !userIdHeader.isBlank()) {
      try {
        UUID userId = UUID.fromString(userIdHeader.trim());
        var authentication =
            new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (IllegalArgumentException ignored) {
        // Formato UUID inválido: não popula SecurityContextHolder, resultando em 401 se rota
        // protegida
      }
    }

    try {
      filterChain.doFilter(request, response);
    } finally {
      // Limpeza segura para evitar vazamento de contexto entre threads
      if (userIdHeader != null) {
        SecurityContextHolder.clearContext();
      }
    }
  }
}
