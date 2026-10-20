package br.com.imovix.identity.api;

import br.com.imovix.identity.application.GetMyProfileUseCase;
import br.com.imovix.identity.application.GetUserProfileUseCase;
import br.com.imovix.identity.application.UserProfileDetailResponse;
import br.com.imovix.identity.application.UserResponse;
import java.security.Principal;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Endpoint REST para consulta do perfil e contexto do usuário autenticado (/api/v1/me). */
@RestController
@RequestMapping("/api/v1/me")
public class MeController {

  private final GetMyProfileUseCase getMyProfileUseCase;
  private final GetUserProfileUseCase getUserProfileUseCase;

  public MeController(
      GetMyProfileUseCase getMyProfileUseCase, GetUserProfileUseCase getUserProfileUseCase) {
    this.getMyProfileUseCase = getMyProfileUseCase;
    this.getUserProfileUseCase = getUserProfileUseCase;
  }

  @GetMapping
  public ResponseEntity<UserProfileDetailResponse> getMyProfile(
      @RequestHeader(value = "X-User-Id", required = false) UUID headerUserId,
      @RequestHeader(value = "X-User-Email", required = false) String headerUserEmail,
      Principal principal) {

    UUID userId = resolveUserId(headerUserId, headerUserEmail, principal);

    UserProfileDetailResponse response = getMyProfileUseCase.execute(userId);
    return ResponseEntity.ok(response);
  }

  private UUID resolveUserId(UUID headerUserId, String headerUserEmail, Principal principal) {
    if (headerUserId != null) {
      return headerUserId;
    }

    String identifier = principal != null ? principal.getName() : headerUserEmail;
    if (identifier == null || identifier.isBlank()) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Contexto de usuário não autenticado.");
    }

    try {
      return UUID.fromString(identifier);
    } catch (IllegalArgumentException e) {
      // Se não for UUID, assume que é e-mail
      UserResponse user = getUserProfileUseCase.getByEmail(identifier);
      return user.id();
    }
  }
}
