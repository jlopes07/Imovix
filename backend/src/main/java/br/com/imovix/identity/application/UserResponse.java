package br.com.imovix.identity.application;

import br.com.imovix.identity.domain.User;
import br.com.imovix.identity.domain.UserStatus;
import java.time.Instant;
import java.util.UUID;

/** DTO imutável de saída com os dados de um usuário (sem expor hash de senha). */
public record UserResponse(
    UUID id, String email, String name, UserStatus status, Instant createdAt, Instant updatedAt) {

  public static UserResponse fromDomain(User user) {
    return new UserResponse(
        user.getId(),
        user.getEmail(),
        user.getName(),
        user.getStatus(),
        user.getCreatedAt(),
        user.getUpdatedAt());
  }
}
