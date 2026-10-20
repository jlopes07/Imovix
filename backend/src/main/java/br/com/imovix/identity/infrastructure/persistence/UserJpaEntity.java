package br.com.imovix.identity.infrastructure.persistence;

import br.com.imovix.identity.domain.User;
import br.com.imovix.identity.domain.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Entidade JPA para mapeamento da tabela imovix.users. */
@Entity
@Table(name = "users", schema = "imovix")
public class UserJpaEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "email", nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "password_hash", length = 255)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 50)
  private UserStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public UserJpaEntity() {}

  public static UserJpaEntity fromDomain(User user) {
    UserJpaEntity entity = new UserJpaEntity();
    entity.id = user.getId();
    entity.email = user.getEmail();
    entity.name = user.getName();
    entity.passwordHash = user.getPasswordHash();
    entity.status = user.getStatus();
    entity.createdAt = user.getCreatedAt();
    entity.updatedAt = user.getUpdatedAt();
    return entity;
  }

  public User toDomain() {
    return new User(id, email, name, passwordHash, status, createdAt, updatedAt);
  }

  public UUID getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public UserStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
