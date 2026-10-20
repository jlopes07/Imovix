package br.com.imovix.crm.application;

import br.com.imovix.crm.domain.ContactType;
import br.com.imovix.crm.domain.Lead;
import br.com.imovix.crm.domain.LeadRepository;
import br.com.imovix.crm.domain.LeadSource;
import br.com.imovix.crm.domain.LeadStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Caso de uso para cadastrar um novo lead vinculado ao tenant ativo. */
@Service
public class CreateLeadUseCase {

  private final LeadRepository leadRepository;

  public CreateLeadUseCase(LeadRepository leadRepository) {
    this.leadRepository = leadRepository;
  }

  @Transactional
  public LeadResponse execute(CreateLeadCommand command) {
    Lead lead =
        new Lead(
            null,
            command.tenantId(),
            command.name(),
            command.email(),
            command.phone(),
            command.type(),
            command.status() != null ? command.status() : LeadStatus.NEW,
            command.source() != null ? command.source() : LeadSource.WEBSITE,
            command.assignedUserId(),
            command.interestedPropertyId(),
            command.budgetMin(),
            command.budgetMax(),
            command.notes(),
            command.creatorUserId(),
            command.creatorUserId(),
            null,
            null);

    Lead saved = leadRepository.save(lead);
    return LeadResponse.fromDomain(saved);
  }

  public record CreateLeadCommand(
      UUID tenantId,
      String name,
      String email,
      String phone,
      ContactType type,
      LeadStatus status,
      LeadSource source,
      UUID assignedUserId,
      UUID interestedPropertyId,
      BigDecimal budgetMin,
      BigDecimal budgetMax,
      String notes,
      UUID creatorUserId) {}
}
