package br.com.imovix.companies.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.imovix.companies.domain.Company;
import br.com.imovix.companies.domain.CompanyMembership;
import br.com.imovix.companies.domain.CompanyMembershipRepository;
import br.com.imovix.companies.domain.CompanyNotFoundException;
import br.com.imovix.companies.domain.CompanyRepository;
import br.com.imovix.companies.domain.CompanyStatus;
import br.com.imovix.companies.domain.MembershipAlreadyExistsException;
import br.com.imovix.companies.domain.MembershipRole;
import br.com.imovix.identity.domain.User;
import br.com.imovix.identity.domain.UserRepository;
import br.com.imovix.identity.domain.UserStatus;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Aplicação: AddMemberUseCase")
class AddMemberUseCaseTest {

  @Mock private CompanyRepository companyRepository;
  @Mock private CompanyMembershipRepository membershipRepository;
  @Mock private UserRepository userRepository;
  @InjectMocks private AddMemberUseCase addMemberUseCase;

  @Test
  @DisplayName("Deve adicionar novo membro com sucesso")
  void shouldAddMemberSuccessfully() {
    UUID companyId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    Company company =
        new Company(companyId, "Imobiliária", null, null, null, CompanyStatus.ACTIVE, null, null);
    User user =
        new User(userId, "corretor@imovix.com", "Corretor", null, UserStatus.ACTIVE, null, null);

    AddMemberCommand command = new AddMemberCommand(companyId, userId, MembershipRole.REALTOR);

    when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(membershipRepository.existsByCompanyIdAndUserId(companyId, userId)).thenReturn(false);
    when(membershipRepository.save(any(CompanyMembership.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CompanyMemberResponse response = addMemberUseCase.execute(command);

    assertThat(response).isNotNull();
    assertThat(response.companyId()).isEqualTo(companyId);
    assertThat(response.userId()).isEqualTo(userId);
    assertThat(response.role()).isEqualTo(MembershipRole.REALTOR);
    assertThat(response.userName()).isEqualTo("Corretor");
    assertThat(response.userEmail()).isEqualTo("corretor@imovix.com");

    verify(membershipRepository).save(any(CompanyMembership.class));
  }

  @Test
  @DisplayName("Deve lançar MembershipAlreadyExistsException se o usuário já for membro")
  void shouldThrowWhenMembershipAlreadyExists() {
    UUID companyId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    Company company =
        new Company(companyId, "Imobiliária", null, null, null, CompanyStatus.ACTIVE, null, null);
    User user =
        new User(userId, "corretor@imovix.com", "Corretor", null, UserStatus.ACTIVE, null, null);

    AddMemberCommand command = new AddMemberCommand(companyId, userId, MembershipRole.REALTOR);

    when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(membershipRepository.existsByCompanyIdAndUserId(companyId, userId)).thenReturn(true);

    assertThatThrownBy(() -> addMemberUseCase.execute(command))
        .isInstanceOf(MembershipAlreadyExistsException.class);
  }

  @Test
  @DisplayName("Deve lançar CompanyNotFoundException se a empresa não existir")
  void shouldThrowWhenCompanyNotFound() {
    UUID companyId = UUID.randomUUID();
    AddMemberCommand command =
        new AddMemberCommand(companyId, UUID.randomUUID(), MembershipRole.REALTOR);

    when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> addMemberUseCase.execute(command))
        .isInstanceOf(CompanyNotFoundException.class);
  }
}
