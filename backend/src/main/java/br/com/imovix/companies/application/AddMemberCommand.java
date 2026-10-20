package br.com.imovix.companies.application;

import br.com.imovix.companies.domain.MembershipRole;
import java.util.UUID;

/** Comando para adicionar ou associar um membro à empresa com determinado papel. */
public record AddMemberCommand(UUID companyId, UUID userId, MembershipRole role) {}
