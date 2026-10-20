package br.com.imovix.companies.infrastructure.persistence;

import br.com.imovix.companies.domain.CompanyMembership;
import br.com.imovix.companies.domain.MembershipRole;
import br.com.imovix.companies.domain.MembershipStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Entidade JPA para mapeamento da tabela imovix.company_memberships. */
@Entity
@Table(name = "company_memberships", schema = "imovix")
public class CompanyMembershipJpaEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "company_id", nullable = false)
  private UUID companyId;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 50)
  private MembershipRole role;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 50)
  private MembershipStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public CompanyMembershipJpaEntity() {}

  public static CompanyMembershipJpaEntity fromDomain(CompanyMembership membership) {
    CompanyMembershipJpaEntity entity = new CompanyMembershipJpaEntity();
    entity.id = membership.getId();
    entity.companyId = membership.getCompanyId();
    entity.userId = membership.getUserId();
    entity.role = membership.getRole();
    entity.status = membership.getStatus();
    entity.createdAt = membership.getCreatedAt();
    entity.updatedAt = membership.getUpdatedAt();
    return entity;
  }

  public CompanyMembership toDomain() {
    return new CompanyMembership(id, companyId, userId, role, status, createdAt, updatedAt);
  }

  public UUID getId() {
    return id;
  }

  public UUID getCompanyId() {
    return companyId;
  }

  public UUID getUserId() {
    return userId;
  }

  public MembershipRole getRole() {
    return role;
  }

  public MembershipStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
