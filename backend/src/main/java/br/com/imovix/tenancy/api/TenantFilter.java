package br.com.imovix.tenancy.api;

import br.com.imovix.tenancy.application.TenantContext;
import br.com.imovix.tenancy.application.TenantResolver;
import br.com.imovix.tenancy.domain.TenantId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro HTTP responsável por extrair, validar e associar o Tenant ativo ao {@link TenantContext} e
 * aos logs via MDC.
 *
 * <p>Garante que o {@link TenantContext} seja sempre limpo no bloco {@code finally}.
 */
public class TenantFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);

  public static final String TENANT_HEADER = "X-Tenant-Id";
  public static final String COMPANY_HEADER = "X-Company-Id";
  public static final String MDC_TENANT_KEY = "tenantId";

  private final TenantResolver tenantResolver;

  public TenantFilter(TenantResolver tenantResolver) {
    this.tenantResolver = tenantResolver;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String path = request.getRequestURI();
    if (isBypassPath(path)) {
      filterChain.doFilter(request, response);
      return;
    }

    UUID userId = extractUserId(request);
    UUID requestedTenantId = extractRequestedTenantId(request);

    try {
      if (userId != null) {
        try {
          TenantId tenantId = tenantResolver.resolve(userId, requestedTenantId);
          TenantContext.set(tenantId);
          MDC.put(MDC_TENANT_KEY, tenantId.toString());
          response.setHeader(TENANT_HEADER, tenantId.toString());
        } catch (Exception ex) {
          // Se houver falha ao resolver e a rota não for opcional, loga em debug e propaga para o
          // handler
          log.debug(
              "Não foi possível resolver o tenant para o usuário {}: {}", userId, ex.getMessage());
          // Se o usuário passou um tenant inválido explicitamente, lança para retornar 403
          if (requestedTenantId != null) {
            throw ex;
          }
        }
      }

      filterChain.doFilter(request, response);
    } finally {
      TenantContext.clear();
      MDC.remove(MDC_TENANT_KEY);
    }
  }

  private boolean isBypassPath(String path) {
    return path.startsWith("/actuator") || path.equals("/api/v1/users");
  }

  private UUID extractUserId(HttpServletRequest request) {
    String headerUserId = request.getHeader("X-User-Id");
    if (headerUserId != null && !headerUserId.isBlank()) {
      try {
        return UUID.fromString(headerUserId.trim());
      } catch (IllegalArgumentException ignored) {
        return null;
      }
    }

    Principal principal = request.getUserPrincipal();
    if (principal != null) {
      try {
        return UUID.fromString(principal.getName().trim());
      } catch (IllegalArgumentException ignored) {
        return null;
      }
    }

    return null;
  }

  private UUID extractRequestedTenantId(HttpServletRequest request) {
    String tenantHeader = request.getHeader(TENANT_HEADER);
    if (tenantHeader == null || tenantHeader.isBlank()) {
      tenantHeader = request.getHeader(COMPANY_HEADER);
    }

    if (tenantHeader != null && !tenantHeader.isBlank()) {
      try {
        return UUID.fromString(tenantHeader.trim());
      } catch (IllegalArgumentException ignored) {
        return null;
      }
    }
    return null;
  }
}
