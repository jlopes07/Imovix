package br.com.imovix.companies.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.imovix.companies.domain.Company;
import br.com.imovix.companies.domain.CompanyMembership;
import br.com.imovix.companies.domain.CompanyMembershipRepository;
import br.com.imovix.companies.domain.CompanyRepository;
import br.com.imovix.companies.domain.CompanyStatus;
import br.com.imovix.identity.domain.User;
import br.com.imovix.identity.domain.UserNotFoundException;
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
@DisplayName("Testes de Aplicação: CreateCompanyUseCase")
class CreateCompanyUseCaseTest {

  @Mock private CompanyRepository companyRepository;
  @Mock private CompanyMembershipRepository membershipRepository;
  @Mock private UserRepository userRepository;
  @InjectMocks private CreateCompanyUseCase createCompanyUseCase;

  @Test
  @DisplayName("Deve criar empresa e vincular criador como ADMIN")
  void shouldCreateCompanyAndLinkCreatorAsAdmin() {
    UUID userId = UUID.randomUUID();
    User user =
        new User(userId, "admin@imovix.com", "Admin User", null, UserStatus.ACTIVE, null, null);

    CreateCompanyCommand command =
        new CreateCompanyCommand(
            "Imobiliária Top Ltda", "Top Imóveis", "12.345.678/0001-99", "12345", userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(companyRepository.existsByDocumentNumber("12345678000199")).thenReturn(false);
    when(companyRepository.save(any(Company.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CompanyResponse response = createCompanyUseCase.execute(command);

    assertThat(response).isNotNull();
    assertThat(response.name()).isEqualTo("Imobiliária Top Ltda");
    assertThat(response.tradeName()).isEqualTo("Top Imóveis");
    assertThat(response.documentNumber()).isEqualTo("12345678000199");
    assertThat(response.status()).isEqualTo(CompanyStatus.ACTIVE);

    verify(companyRepository).save(any(Company.class));
    verify(membershipRepository).save(any(CompanyMembership.class));
  }

  @Test
  @DisplayName("Deve falhar se o criador informado não existir")
  void shouldThrowWhenCreatorNotFound() {
    UUID missingUserId = UUID.randomUUID();
    CreateCompanyCommand command =
        new CreateCompanyCommand("Empresa", null, null, null, missingUserId);

    when(userRepository.findById(missingUserId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> createCompanyUseCase.execute(command))
        .isInstanceOf(UserNotFoundException.class);
  }
}
