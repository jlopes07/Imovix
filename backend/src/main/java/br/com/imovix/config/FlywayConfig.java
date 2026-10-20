package br.com.imovix.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração explícita do Flyway para garantir execução confiável das migrações antes da
 * inicialização e validação de schema do Hibernate JPA.
 */
@Configuration(proxyBeanMethods = false)
public class FlywayConfig {

  @Bean(initMethod = "migrate")
  @ConditionalOnProperty(
      name = "spring.flyway.enabled",
      havingValue = "true",
      matchIfMissing = true)
  public Flyway flyway(DataSource dataSource) {
    return Flyway.configure()
        .dataSource(dataSource)
        .schemas("imovix")
        .defaultSchema("imovix")
        .locations("classpath:db/migration")
        .baselineOnMigrate(true)
        .load();
  }

  @Bean
  @ConditionalOnProperty(
      name = "spring.flyway.enabled",
      havingValue = "true",
      matchIfMissing = true)
  public static BeanFactoryPostProcessor entityManagerFactoryDependsOnFlyway() {
    return beanFactory -> {
      for (String beanName : beanFactory.getBeanNamesForType(EntityManagerFactory.class)) {
        BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
        String[] dependsOn = bd.getDependsOn();
        if (dependsOn == null || dependsOn.length == 0) {
          bd.setDependsOn("flyway");
        } else {
          String[] newDependsOn = new String[dependsOn.length + 1];
          System.arraycopy(dependsOn, 0, newDependsOn, 0, dependsOn.length);
          newDependsOn[dependsOn.length] = "flyway";
          bd.setDependsOn(newDependsOn);
        }
      }
    };
  }
}
