package br.com.imovix.companies.domain;

import java.util.UUID;

/** Exceção lançada quando um usuário já possui vínculo com a empresa. */
public class MembershipAlreadyExistsException extends RuntimeException {

  public MembershipAlreadyExistsException(UUID companyId, UUID userId) {
    super(
        String.format(
            "O usuário %s já possui vínculo cadastrado na empresa %s.", userId, companyId));
  }
}
