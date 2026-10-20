package br.com.imovix.tenancy.infrastructure;

import br.com.imovix.tenancy.api.TenantFilter;
import br.com.imovix.tenancy.application.TenantResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Configuração dos beans de infraestrutura e filtros do módulo de tenancy. */
@Configuration(proxyBeanMethods = false)
public class TenancyConfig {

  @Bean
  public TenantFilter tenantFilter(TenantResolver tenantResolver) {
    return new TenantFilter(tenantResolver);
  }
}
