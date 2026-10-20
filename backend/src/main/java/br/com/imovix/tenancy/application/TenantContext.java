package br.com.imovix.tenancy.application;

import br.com.imovix.tenancy.domain.TenantId;
import br.com.imovix.tenancy.domain.TenantNotResolvedException;
import java.util.Optional;
import java.util.UUID;

/**
 * Gerenciador de contexto do Tenant ativo para a thread de execução corrente.
 *
 * <p><strong>Atenção:</strong> Sempre limpar o contexto em bloco {@code try/finally} ao final do
 * ciclo de vida da requisição para evitar vazamento em pools de threads.
 */
public final class TenantContext {

  private static final ThreadLocal<TenantId> CURRENT_TENANT = new ThreadLocal<>();

  private TenantContext() {}

  public static void set(TenantId tenantId) {
    if (tenantId == null) {
      clear();
    } else {
      CURRENT_TENANT.set(tenantId);
    }
  }

  public static Optional<TenantId> get() {
    return Optional.ofNullable(CURRENT_TENANT.get());
  }

  public static TenantId require() {
    return get().orElseThrow(TenantNotResolvedException::new);
  }

  public static UUID requireId() {
    return require().value();
  }

  public static void clear() {
    CURRENT_TENANT.remove();
  }
}
