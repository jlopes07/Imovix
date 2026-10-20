package br.com.imovix.companies.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Entidade de domínio representando a associação explícita entre um Usuário e uma Empresa. */
public class CompanyMembership {

  private final UUID id;
  private final UUID companyId;
  private final UUID userId;
  private MembershipRole role;
  private MembershipStatus status;
  private final Instant createdAt;
  private Instant updatedAt;

  public CompanyMembership(
      UUID id,
      UUID companyId,
      UUID userId,
      MembershipRole role,
      MembershipStatus status,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id != null ? id : UUID.randomUUID();
    this.companyId = Objects.requireNonNull(companyId, "O companyId não pode ser nulo.");
    this.userId = Objects.requireNonNull(userId, "O userId não pode ser nulo.");
    this.role = role != null ? role : MembershipRole.REALTOR;
    this.status = status != null ? status : MembershipStatus.ACTIVE;
    Instant now = Instant.now();
    this.createdAt = createdAt != null ? createdAt : now;
    this.updatedAt = updatedAt != null ? updatedAt : now;
  }

  public static CompanyMembership createActive(UUID companyId, UUID userId, MembershipRole role) {
    return new CompanyMembership(
        null, companyId, userId, role, MembershipStatus.ACTIVE, null, null);
  }

  public void changeRole(MembershipRole newRole) {
    this.role = Objects.requireNonNull(newRole, "O novo papel não pode ser nulo.");
    this.updatedAt = Instant.now();
  }

  public void deactivate() {
    this.status = MembershipStatus.INACTIVE;
    this.updatedAt = Instant.now();
  }

  public void activate() {
    this.status = MembershipStatus.ACTIVE;
    this.updatedAt = Instant.now();
  }

  public boolean isActive() {
    return this.status == MembershipStatus.ACTIVE;
  }

  public boolean isAdmin() {
    return this.role == MembershipRole.ADMIN;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CompanyMembership that = (CompanyMembership) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
