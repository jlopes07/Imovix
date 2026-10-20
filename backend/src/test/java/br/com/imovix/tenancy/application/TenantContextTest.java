package br.com.imovix.tenancy.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.imovix.tenancy.domain.TenantId;
import br.com.imovix.tenancy.domain.TenantNotResolvedException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes Unitários: TenantContext")
class TenantContextTest {

  @AfterEach
  void tearDown() {
    TenantContext.clear();
  }

  @Test
  @DisplayName("Deve definir, consultar e limpar o tenant na thread atual")
  void shouldSetGetAndClearTenant() {
    UUID tenantUuid = UUID.randomUUID();
    TenantId tenantId = TenantId.from(tenantUuid);

    assertThat(TenantContext.get()).isEmpty();

    TenantContext.set(tenantId);
    assertThat(TenantContext.get()).contains(tenantId);
    assertThat(TenantContext.requireId()).isEqualTo(tenantUuid);

    TenantContext.clear();
    assertThat(TenantContext.get()).isEmpty();
  }

  @Test
  @DisplayName(
      "require() deve lançar TenantNotResolvedException quando nenhum tenant estiver ativo")
  void shouldThrowWhenNoTenantSet() {
    assertThatThrownBy(TenantContext::require)
        .isInstanceOf(TenantNotResolvedException.class)
        .hasMessageContaining("Nenhum tenant ativo");
  }

  @Test
  @DisplayName("Deve isolar completamente o contexto entre threads distintas")
  void shouldIsolateContextBetweenThreads() throws InterruptedException {
    UUID mainThreadTenant = UUID.randomUUID();
    UUID workerThreadTenant = UUID.randomUUID();

    TenantContext.set(TenantId.from(mainThreadTenant));

    AtomicReference<UUID> workerResolvedTenant = new AtomicReference<>();
    CountDownLatch latch = new CountDownLatch(1);

    Thread worker =
        new Thread(
            () -> {
              try {
                TenantContext.set(TenantId.from(workerThreadTenant));
                workerResolvedTenant.set(TenantContext.requireId());
              } finally {
                TenantContext.clear();
                latch.countDown();
              }
            });

    worker.start();
    latch.await();

    // A thread filha viu o seu próprio tenant
    assertThat(workerResolvedTenant.get()).isEqualTo(workerThreadTenant);
    // A thread principal continua vendo intacto o seu próprio tenant
    assertThat(TenantContext.requireId()).isEqualTo(mainThreadTenant);
  }
}
