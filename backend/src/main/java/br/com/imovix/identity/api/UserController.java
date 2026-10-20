package br.com.imovix.identity.api;

import br.com.imovix.identity.application.RegisterUserUseCase;
import br.com.imovix.identity.application.UserResponse;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** Endpoint REST para registro e gerenciamento de usuários. */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final RegisterUserUseCase registerUserUseCase;

  public UserController(RegisterUserUseCase registerUserUseCase) {
    this.registerUserUseCase = registerUserUseCase;
  }

  @PostMapping
  public ResponseEntity<UserResponse> register(@Valid @RequestBody CreateUserRequest request) {
    UserResponse response = registerUserUseCase.execute(request.toCommand());
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();
    return ResponseEntity.created(location).body(response);
  }
}
