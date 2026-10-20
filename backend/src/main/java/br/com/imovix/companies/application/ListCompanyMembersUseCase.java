package br.com.imovix.companies.application;

import br.com.imovix.companies.domain.CompanyMembership;
import br.com.imovix.companies.domain.CompanyMembershipRepository;
import br.com.imovix.companies.domain.CompanyNotFoundException;
import br.com.imovix.companies.domain.CompanyRepository;
import br.com.imovix.identity.domain.User;
import br.com.imovix.identity.domain.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para listar todos os membros vinculados a uma empresa. */
@Service
public class ListCompanyMembersUseCase {

  private final CompanyRepository companyRepository;
  private final CompanyMembershipRepository membershipRepository;
  private final UserRepository userRepository;

  public ListCompanyMembersUseCase(
      CompanyRepository companyRepository,
      CompanyMembershipRepository membershipRepository,
      UserRepository userRepository) {
    this.companyRepository = companyRepository;
    this.membershipRepository = membershipRepository;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public List<CompanyMemberResponse> execute(UUID companyId) {
    companyRepository
        .findById(companyId)
        .orElseThrow(() -> new CompanyNotFoundException(companyId));

    List<CompanyMembership> memberships = membershipRepository.findByCompanyId(companyId);

    return memberships.stream()
        .map(
            membership -> {
              Optional<User> userOpt = userRepository.findById(membership.getUserId());
              String name = userOpt.map(User::getName).orElse("Usuário Removido");
              String email = userOpt.map(User::getEmail).orElse("-");
              return CompanyMemberResponse.fromDomain(membership, name, email);
            })
        .toList();
  }
}
