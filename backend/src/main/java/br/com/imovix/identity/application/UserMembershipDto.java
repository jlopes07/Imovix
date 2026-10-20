package br.com.imovix.identity.application;

import br.com.imovix.companies.domain.MembershipRole;
import br.com.imovix.companies.domain.MembershipStatus;
import java.util.UUID;

/** DTO representando uma empresa e papel do usuário. */
public record UserMembershipDto(
    UUID companyId, String companyName, MembershipRole role, MembershipStatus status) {}
