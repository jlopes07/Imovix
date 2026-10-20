package br.com.imovix.companies.application;

import br.com.imovix.companies.domain.Company;
import br.com.imovix.companies.domain.CompanyMembership;
import br.com.imovix.companies.domain.CompanyMembershipRepository;
import br.com.imovix.companies.domain.CompanyRepository;
import br.com.imovix.companies.domain.MembershipRole;
import br.com.imovix.identity.domain.UserNotFoundException;
import br.com.imovix.identity.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para criação de uma nova empresa, associando o criador como ADMIN. */
@Service
public class CreateCompanyUseCase {

  private final CompanyRepository companyRepository;
  private final CompanyMembershipRepository membershipRepository;
  private final UserRepository userRepository;

  public CreateCompanyUseCase(
      CompanyRepository companyRepository,
      CompanyMembershipRepository membershipRepository,
      UserRepository userRepository) {
    this.companyRepository = companyRepository;
    this.membershipRepository = membershipRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public CompanyResponse execute(CreateCompanyCommand command) {
    if (command.creatorUserId() != null) {
      userRepository
          .findById(command.creatorUserId())
          .orElseThrow(() -> new UserNotFoundException(command.creatorUserId()));
    }

    String cleanDoc = Company.cleanDocumentNumber(command.documentNumber());
    if (cleanDoc != null
        && !cleanDoc.isBlank()
        && companyRepository.existsByDocumentNumber(cleanDoc)) {
      throw new IllegalArgumentException(
          "Já existe uma empresa cadastrada com o documento informado.");
    }

    Company company =
        Company.createNew(command.name(), command.tradeName(), cleanDoc, command.creci());
    Company savedCompany = companyRepository.save(company);

    if (command.creatorUserId() != null) {
      CompanyMembership adminMembership =
          CompanyMembership.createActive(
              savedCompany.getId(), command.creatorUserId(), MembershipRole.ADMIN);
      membershipRepository.save(adminMembership);
    }

    return CompanyResponse.fromDomain(savedCompany);
  }
}
