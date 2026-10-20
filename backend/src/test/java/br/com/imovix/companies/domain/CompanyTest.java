package br.com.imovix.companies.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes de Domínio: Company")
class CompanyTest {

  @Test
  @DisplayName("Deve criar nova empresa com dados limpos e status ACTIVE")
  void shouldCreateNewCompanyWithCleanData() {
    Company company =
        Company.createNew(
            "  Imobiliária Prime Ltda  ",
            "Prime Imóveis",
            "12.345.678/0001-90",
            "  CRECI-SP 12345-J  ");

    assertThat(company.getName()).isEqualTo("Imobiliária Prime Ltda");
    assertThat(company.getTradeName()).isEqualTo("Prime Imóveis");
    assertThat(company.getDocumentNumber()).isEqualTo("12345678000190");
    assertThat(company.getCreci()).isEqualTo("CRECI-SP 12345-J");
    assertThat(company.getStatus()).isEqualTo(CompanyStatus.ACTIVE);
    assertThat(company.getId()).isNotNull();
    assertThat(company.getCreatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Deve rejeitar razão social em branco")
  void shouldRejectBlankName() {
    assertThatThrownBy(() -> Company.createNew("   ", "Nome", "123", "CRECI"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("razão social");
  }

  @Test
  @DisplayName("Deve desativar e ativar empresa")
  void shouldToggleStatus() {
    Company company = Company.createNew("Empresa A", "A", "123", "CRECI");
    assertThat(company.isActive()).isTrue();

    company.deactivate();
    assertThat(company.isActive()).isFalse();
    assertThat(company.getStatus()).isEqualTo(CompanyStatus.INACTIVE);

    company.activate();
    assertThat(company.isActive()).isTrue();
    assertThat(company.getStatus()).isEqualTo(CompanyStatus.ACTIVE);
  }

  @Test
  @DisplayName("Duas empresas com mesmo ID são consideradas iguais")
  void shouldBeEqualWhenSameId() {
    UUID id = UUID.randomUUID();
    Company c1 = new Company(id, "C1", null, null, null, CompanyStatus.ACTIVE, null, null);
    Company c2 = new Company(id, "C2", null, null, null, CompanyStatus.INACTIVE, null, null);

    assertThat(c1).isEqualTo(c2);
    assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
  }
}
