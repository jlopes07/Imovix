package br.com.imovix.tenancy.application;

import br.com.imovix.companies.domain.CompanyMembership;
import br.com.imovix.companies.domain.CompanyMembershipRepository;
import br.com.imovix.tenancy.domain.CrossTenantAccessException;
import br.com.imovix.tenancy.domain.TenantId;
import br.com.imovix.tenancy.domain.TenantNotResolvedException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por determinar com segurança a empresa ativa (Tenant) do usuário.
 *
 * <p><strong>Regra de Segurança Estrita:</strong> Nunca confiar no cabeçalho ou parâmetro enviado
 * pelo cliente sem antes checar se o usuário autenticado possui vínculo ativo com a empresa.
 */
@Service
public class TenantResolver {

  private final CompanyMembershipRepository membershipRepository;

  public TenantResolver(CompanyMembershipRepository membershipRepository) {
    this.membershipRepository = membershipRepository;
  }

  public TenantId resolve(UUID userId, UUID requestedTenantId) {
    if (userId == null) {
      throw new TenantNotResolvedException(
          "Identificador do usuário não fornecido para resolução de tenant.");
    }

    List<CompanyMembership> activeMemberships =
        membershipRepository.findByUserId(userId).stream()
            .filter(CompanyMembership::isActive)
            .toList();

    if (activeMemberships.isEmpty()) {
      throw new TenantNotResolvedException(
          String.format("O usuário %s não possui vínculo ativo com nenhuma empresa.", userId));
    }

    if (requestedTenantId != null) {
      boolean hasAccess =
          activeMemberships.stream().anyMatch(m -> m.getCompanyId().equals(requestedTenantId));

      if (!hasAccess) {
        throw new CrossTenantAccessException(userId, requestedTenantId);
      }
      return TenantId.from(requestedTenantId);
    }

    if (activeMemberships.size() == 1) {
      return TenantId.from(activeMemberships.getFirst().getCompanyId());
    }

    throw new TenantNotResolvedException(
        "O usuário possui acesso a múltiplas empresas. É obrigatório informar o cabeçalho X-Tenant-Id.");
  }
}
