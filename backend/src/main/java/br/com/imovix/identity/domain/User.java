package br.com.imovix.identity.domain;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/** Entidade de domínio representando um Usuário no sistema. */
public class User {

  private final UUID id;
  private String email;
  private String name;
  private String passwordHash;
  private UserStatus status;
  private final Instant createdAt;
  private Instant updatedAt;

  public User(
      UUID id,
      String email,
      String name,
      String passwordHash,
      UserStatus status,
      Instant createdAt,
      Instant updatedAt) {
    this.id = id != null ? id : UUID.randomUUID();
    this.email = normalizeEmail(email);
    this.name = validateName(name);
    this.passwordHash = passwordHash;
    this.status = status != null ? status : UserStatus.ACTIVE;
    Instant now = Instant.now();
    this.createdAt = createdAt != null ? createdAt : now;
    this.updatedAt = updatedAt != null ? updatedAt : now;
  }

  public static User createNew(String email, String name, String passwordHash) {
    return new User(null, email, name, passwordHash, UserStatus.ACTIVE, null, null);
  }

  public static String normalizeEmail(String email) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("O e-mail do usuário não pode ser vazio.");
    }
    return email.trim().toLowerCase(Locale.ROOT);
  }

  private static String validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("O nome do usuário não pode ser vazio.");
    }
    return name.trim();
  }

  public void updateProfile(String newName) {
    this.name = validateName(newName);
    this.updatedAt = Instant.now();
  }

  public void changePassword(String newPasswordHash) {
    if (newPasswordHash == null || newPasswordHash.isBlank()) {
      throw new IllegalArgumentException("O hash de senha não pode ser vazio.");
    }
    this.passwordHash = newPasswordHash;
    this.updatedAt = Instant.now();
  }

  public void deactivate() {
    this.status = UserStatus.INACTIVE;
    this.updatedAt = Instant.now();
  }

  public void activate() {
    this.status = UserStatus.ACTIVE;
    this.updatedAt = Instant.now();
  }

  public boolean isActive() {
    return this.status == UserStatus.ACTIVE;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
