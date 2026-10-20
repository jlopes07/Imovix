package br.com.imovix.companies.domain;

import java.util.UUID;

/** Exceção lançada quando um vínculo de membro com uma empresa não é localizado. */
public class MembershipNotFoundException extends RuntimeException {

  public MembershipNotFoundException(UUID companyId, UUID userId) {
    super(
        String.format(
            "Vínculo do usuário %s com a empresa %s não foi encontrado.", userId, companyId));
  }
}
