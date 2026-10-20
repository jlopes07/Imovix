package br.com.imovix;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.web.bind.annotation.RestController;

/**
 * Regras arquiteturais verificadas automaticamente (ver {@code docs/architecture/conventions.md}).
 *
 * <p>Camadas de cada módulo: {@code api -> application -> domain <- infrastructure}.
 */
@AnalyzeClasses(packages = "br.com.imovix", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

  private static final String API = "..api..";
  private static final String APPLICATION = "..application..";
  private static final String DOMAIN = "..domain..";
  private static final String INFRASTRUCTURE = "..infrastructure..";

  @ArchTest
  static final ArchRule domainDoesNotDependOnOuterLayers =
      noClasses()
          .that()
          .resideInAPackage(DOMAIN)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(API, APPLICATION, INFRASTRUCTURE)
          .because("o domínio contém as regras essenciais e não conhece detalhes externos");

  @ArchTest
  static final ArchRule domainDoesNotDependOnWebFramework =
      noClasses()
          .that()
          .resideInAPackage(DOMAIN)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("org.springframework.web..", "jakarta.servlet..")
          .because("o domínio não deve conhecer HTTP");

  @ArchTest
  static final ArchRule applicationDoesNotDependOnApiOrInfrastructure =
      noClasses()
          .that()
          .resideInAPackage(APPLICATION)
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage(API, INFRASTRUCTURE)
          .because("casos de uso dependem de abstrações do domínio, não de implementações");

  @ArchTest
  static final ArchRule apiDoesNotDependOnInfrastructure =
      noClasses()
          .that()
          .resideInAPackage(API)
          .should()
          .dependOnClassesThat()
          .resideInAPackage(INFRASTRUCTURE)
          .because("controllers delegam para a camada de aplicação");

  @ArchTest
  static final ArchRule controllersLiveInApiPackages =
      classes()
          .that()
          .areAnnotatedWith(RestController.class)
          .should()
          .resideInAPackage(API)
          .andShould()
          .haveSimpleNameEndingWith("Controller");

  @ArchTest static final ArchRule noFieldInjection = NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

  @ArchTest static final ArchRule noJavaUtilLogging = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

  @ArchTest static final ArchRule noStandardStreams = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
}
