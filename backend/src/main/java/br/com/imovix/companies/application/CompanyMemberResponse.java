package br.com.imovix.companies.application;

import br.com.imovix.companies.domain.CompanyMembership;
import br.com.imovix.companies.domain.MembershipRole;
import br.com.imovix.companies.domain.MembershipStatus;
import java.time.Instant;
import java.util.UUID;

/** DTO imutável de saída com os dados de associação de um membro da empresa. */
public record CompanyMemberResponse(
    UUID membershipId,
    UUID companyId,
    UUID userId,
    String userName,
    String userEmail,
    MembershipRole role,
    MembershipStatus status,
    Instant createdAt) {

  public static CompanyMemberResponse fromDomain(
      CompanyMembership membership, String userName, String userEmail) {
    return new CompanyMemberResponse(
        membership.getId(),
        membership.getCompanyId(),
        membership.getUserId(),
        userName,
        userEmail,
        membership.getRole(),
        membership.getStatus(),
        membership.getCreatedAt());
  }
}
