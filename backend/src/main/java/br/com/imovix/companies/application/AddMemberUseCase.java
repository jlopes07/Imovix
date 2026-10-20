package br.com.imovix.companies.application;

import br.com.imovix.companies.domain.CompanyMembership;
import br.com.imovix.companies.domain.CompanyMembershipRepository;
import br.com.imovix.companies.domain.CompanyNotFoundException;
import br.com.imovix.companies.domain.CompanyRepository;
import br.com.imovix.companies.domain.MembershipAlreadyExistsException;
import br.com.imovix.identity.domain.User;
import br.com.imovix.identity.domain.UserNotFoundException;
import br.com.imovix.identity.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para adicionar ou associar um novo membro a uma empresa. */
@Service
public class AddMemberUseCase {

  private final CompanyRepository companyRepository;
  private final CompanyMembershipRepository membershipRepository;
  private final UserRepository userRepository;

  public AddMemberUseCase(
      CompanyRepository companyRepository,
      CompanyMembershipRepository membershipRepository,
      UserRepository userRepository) {
    this.companyRepository = companyRepository;
    this.membershipRepository = membershipRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public CompanyMemberResponse execute(AddMemberCommand command) {
    companyRepository
        .findById(command.companyId())
        .orElseThrow(() -> new CompanyNotFoundException(command.companyId()));

    User user =
        userRepository
            .findById(command.userId())
            .orElseThrow(() -> new UserNotFoundException(command.userId()));

    if (membershipRepository.existsByCompanyIdAndUserId(command.companyId(), command.userId())) {
      throw new MembershipAlreadyExistsException(command.companyId(), command.userId());
    }

    CompanyMembership membership =
        CompanyMembership.createActive(command.companyId(), command.userId(), command.role());
    CompanyMembership saved = membershipRepository.save(membership);

    return CompanyMemberResponse.fromDomain(saved, user.getName(), user.getEmail());
  }
}
