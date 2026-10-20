package br.com.imovix.companies.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testes de Domínio: CompanyMembership")
class CompanyMembershipTest {

  @Test
  @DisplayName("Deve criar vínculo ativo de membro com papel especificado")
  void shouldCreateActiveMembership() {
    UUID companyId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    CompanyMembership membership =
        CompanyMembership.createActive(companyId, userId, MembershipRole.MANAGER);

    assertThat(membership.getCompanyId()).isEqualTo(companyId);
    assertThat(membership.getUserId()).isEqualTo(userId);
    assertThat(membership.getRole()).isEqualTo(MembershipRole.MANAGER);
    assertThat(membership.getStatus()).isEqualTo(MembershipStatus.ACTIVE);
    assertThat(membership.isActive()).isTrue();
    assertThat(membership.isAdmin()).isFalse();
    assertThat(membership.getId()).isNotNull();
  }

  @Test
  @DisplayName("Deve rejeitar companyId ou userId nulos")
  void shouldRejectNullCompanyOrUser() {
    UUID id = UUID.randomUUID();
    assertThatThrownBy(() -> CompanyMembership.createActive(null, id, MembershipRole.ADMIN))
        .isInstanceOf(NullPointerException.class);

    assertThatThrownBy(() -> CompanyMembership.createActive(id, null, MembershipRole.ADMIN))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("Deve alterar papel do membro e atualizar data de modificação")
  void shouldChangeRole() {
    CompanyMembership membership =
        CompanyMembership.createActive(
            UUID.randomUUID(), UUID.randomUUID(), MembershipRole.REALTOR);

    assertThat(membership.isAdmin()).isFalse();
    membership.changeRole(MembershipRole.ADMIN);

    assertThat(membership.getRole()).isEqualTo(MembershipRole.ADMIN);
    assertThat(membership.isAdmin()).isTrue();
  }

  @Test
  @DisplayName("Deve desativar e reativar vínculo")
  void shouldToggleMembershipStatus() {
    CompanyMembership membership =
        CompanyMembership.createActive(
            UUID.randomUUID(), UUID.randomUUID(), MembershipRole.OPERATOR);

    membership.deactivate();
    assertThat(membership.isActive()).isFalse();
    assertThat(membership.getStatus()).isEqualTo(MembershipStatus.INACTIVE);

    membership.activate();
    assertThat(membership.isActive()).isTrue();
    assertThat(membership.getStatus()).isEqualTo(MembershipStatus.ACTIVE);
  }
}
