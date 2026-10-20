package br.com.imovix.identity.application;

import br.com.imovix.companies.domain.Company;
import br.com.imovix.companies.domain.CompanyMembership;
import br.com.imovix.companies.domain.CompanyMembershipRepository;
import br.com.imovix.companies.domain.CompanyRepository;
import br.com.imovix.identity.domain.User;
import br.com.imovix.identity.domain.UserNotFoundException;
import br.com.imovix.identity.domain.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para obter o perfil completo do usuário corrente e suas empresas vinculadas. */
@Service
public class GetMyProfileUseCase {

  private final UserRepository userRepository;
  private final CompanyMembershipRepository membershipRepository;
  private final CompanyRepository companyRepository;

  public GetMyProfileUseCase(
      UserRepository userRepository,
      CompanyMembershipRepository membershipRepository,
      CompanyRepository companyRepository) {
    this.userRepository = userRepository;
    this.membershipRepository = membershipRepository;
    this.companyRepository = companyRepository;
  }

  @Transactional(readOnly = true)
  public UserProfileDetailResponse execute(UUID userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    List<CompanyMembership> memberships = membershipRepository.findByUserId(userId);

    List<UserMembershipDto> companies =
        memberships.stream()
            .map(
                m -> {
                  Optional<Company> companyOpt = companyRepository.findById(m.getCompanyId());
                  String companyName =
                      companyOpt.map(Company::getName).orElse("Empresa Desconhecida");
                  return new UserMembershipDto(
                      m.getCompanyId(), companyName, m.getRole(), m.getStatus());
                })
            .toList();

    return new UserProfileDetailResponse(
        user.getId(), user.getEmail(), user.getName(), user.getStatus(), companies);
  }
}
