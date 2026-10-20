package br.com.imovix.tenancy.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import br.com.imovix.companies.domain.CompanyMembership;
import br.com.imovix.companies.domain.CompanyMembershipRepository;
import br.com.imovix.companies.domain.MembershipRole;
import br.com.imovix.companies.domain.MembershipStatus;
import br.com.imovix.tenancy.domain.CrossTenantAccessException;
import br.com.imovix.tenancy.domain.TenantId;
import br.com.imovix.tenancy.domain.TenantNotResolvedException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Aplicação: TenantResolver (Isolamento de Dados)")
class TenantResolverTest {

  @Mock private CompanyMembershipRepository membershipRepository;
  @InjectMocks private TenantResolver tenantResolver;

  @Test
  @DisplayName("Deve resolver automaticamente quando o usuário possui apenas 1 empresa ativa")
  void shouldAutoResolveWhenSingleActiveCompany() {
    UUID userId = UUID.randomUUID();
    UUID companyId = UUID.randomUUID();

    CompanyMembership membership =
        CompanyMembership.createActive(companyId, userId, MembershipRole.REALTOR);

    when(membershipRepository.findByUserId(userId)).thenReturn(List.of(membership));

    TenantId resolved = tenantResolver.resolve(userId, null);

    assertThat(resolved).isNotNull();
    assertThat(resolved.value()).isEqualTo(companyId);
  }

  @Test
  @DisplayName("Deve resolver a empresa solicitada se o usuário possuir acesso ativo a ela")
  void shouldResolveRequestedTenantWhenUserHasAccess() {
    UUID userId = UUID.randomUUID();
    UUID companyA = UUID.randomUUID();
    UUID companyB = UUID.randomUUID();

    CompanyMembership m1 = CompanyMembership.createActive(companyA, userId, MembershipRole.REALTOR);
    CompanyMembership m2 = CompanyMembership.createActive(companyB, userId, MembershipRole.MANAGER);

    when(membershipRepository.findByUserId(userId)).thenReturn(List.of(m1, m2));

    TenantId resolved = tenantResolver.resolve(userId, companyB);

    assertThat(resolved).isNotNull();
    assertThat(resolved.value()).isEqualTo(companyB);
  }

  @Test
  @DisplayName(
      "TESTE DE SEGURANÇA: Deve bloquear e lançar CrossTenantAccessException se o usuário tentar acessar empresa alheia")
  void shouldBlockAndThrowWhenUserTriesToAccessForeignTenant() {
    UUID userId = UUID.randomUUID();
    UUID legitimateCompany = UUID.randomUUID();
    UUID foreignCompany = UUID.randomUUID();

    CompanyMembership membership =
        CompanyMembership.createActive(legitimateCompany, userId, MembershipRole.REALTOR);

    when(membershipRepository.findByUserId(userId)).thenReturn(List.of(membership));

    assertThatThrownBy(() -> tenantResolver.resolve(userId, foreignCompany))
        .isInstanceOf(CrossTenantAccessException.class)
        .hasMessageContaining("não possui associação ativa com o tenant");
  }

  @Test
  @DisplayName("TESTE DE SEGURANÇA: Deve ignorar associações com status INACTIVE e bloquear acesso")
  void shouldIgnoreInactiveMemberships() {
    UUID userId = UUID.randomUUID();
    UUID inactiveCompany = UUID.randomUUID();

    CompanyMembership inactive =
        new CompanyMembership(
            UUID.randomUUID(),
            inactiveCompany,
            userId,
            MembershipRole.REALTOR,
            MembershipStatus.INACTIVE,
            null,
            null);

    when(membershipRepository.findByUserId(userId)).thenReturn(List.of(inactive));

    assertThatThrownBy(() -> tenantResolver.resolve(userId, inactiveCompany))
        .isInstanceOf(TenantNotResolvedException.class)
        .hasMessageContaining("não possui vínculo ativo com nenhuma empresa");
  }

  @Test
  @DisplayName(
      "Deve exigir cabeçalho explícito quando o usuário pertencer a mais de uma empresa ativa")
  void shouldRequireHeaderWhenMultipleActiveCompanies() {
    UUID userId = UUID.randomUUID();
    UUID companyA = UUID.randomUUID();
    UUID companyB = UUID.randomUUID();

    CompanyMembership m1 = CompanyMembership.createActive(companyA, userId, MembershipRole.REALTOR);
    CompanyMembership m2 = CompanyMembership.createActive(companyB, userId, MembershipRole.ADMIN);

    when(membershipRepository.findByUserId(userId)).thenReturn(List.of(m1, m2));

    assertThatThrownBy(() -> tenantResolver.resolve(userId, null))
        .isInstanceOf(TenantNotResolvedException.class)
        .hasMessageContaining("múltiplas empresas");
  }
}
