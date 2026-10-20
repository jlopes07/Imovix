package br.com.imovix.shared.error;

import br.com.imovix.security.CorrelationIdFilter;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Tratamento global centralizado de exceções (RFC 7807 - Problem Details).
 *
 * <p>Diretrizes de segurança:
 *
 * <ul>
 *   <li>Nunca expor detalhes internos, nomes de tabelas ou stack traces ao cliente.
 *   <li>Sempre incluir {@code correlationId} e {@code timestamp} UTC em cada resposta de erro.
 *   <li>Registrar logs com o correlationId para correlação imediata de incidentes.
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private static final URI BAD_REQUEST_TYPE =
      URI.create("https://api.imovix.com.br/errors/bad-request");
  private static final URI VALIDATION_TYPE =
      URI.create("https://api.imovix.com.br/errors/validation-failed");
  private static final URI FORBIDDEN_TYPE =
      URI.create("https://api.imovix.com.br/errors/forbidden");
  private static final URI INTERNAL_ERROR_TYPE =
      URI.create("https://api.imovix.com.br/errors/internal-error");

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Um ou mais campos contêm valores inválidos");
    problem.setType(VALIDATION_TYPE);
    problem.setTitle("Erro de Validação");

    Map<String, String> fieldErrors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(error.getField(), error.getDefaultMessage());
    }

    problem.setProperty("invalidParams", fieldErrors);
    enrichProblemDetail(problem);

    log.warn("Falha de validação em requisição [cid:{}]: {}", getCorrelationId(), fieldErrors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(headers).body(problem);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex) {
    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este recurso");
    problem.setType(FORBIDDEN_TYPE);
    problem.setTitle("Acesso Negado");
    enrichProblemDetail(problem);

    log.warn("Acesso negado [cid:{}]: {}", getCorrelationId(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ProblemDetail> handleResponseStatus(ResponseStatusException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
    problem.setTitle(HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase());
    enrichProblemDetail(problem);

    return ResponseEntity.status(ex.getStatusCode()).body(problem);
  }

  @ExceptionHandler({
    br.com.imovix.identity.domain.UserNotFoundException.class,
    br.com.imovix.companies.domain.CompanyNotFoundException.class,
    br.com.imovix.companies.domain.MembershipNotFoundException.class,
    br.com.imovix.properties.domain.PropertyNotFoundException.class
  })
  public ResponseEntity<ProblemDetail> handleNotFoundExceptions(RuntimeException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problem.setType(URI.create("https://api.imovix.com.br/errors/not-found"));
    problem.setTitle("Recurso Não Encontrado");
    enrichProblemDetail(problem);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
  }

  @ExceptionHandler({
    br.com.imovix.identity.domain.EmailAlreadyInUseException.class,
    br.com.imovix.companies.domain.MembershipAlreadyExistsException.class,
    br.com.imovix.properties.domain.DuplicatePropertyCodeException.class
  })
  public ResponseEntity<ProblemDetail> handleConflictExceptions(RuntimeException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    problem.setType(URI.create("https://api.imovix.com.br/errors/conflict"));
    problem.setTitle("Conflito de Dados");
    enrichProblemDetail(problem);

    return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problem.setType(BAD_REQUEST_TYPE);
    problem.setTitle("Requisição Inválida");
    enrichProblemDetail(problem);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  @ExceptionHandler(br.com.imovix.tenancy.domain.CrossTenantAccessException.class)
  public ResponseEntity<ProblemDetail> handleCrossTenantAccess(
      br.com.imovix.tenancy.domain.CrossTenantAccessException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    problem.setType(FORBIDDEN_TYPE);
    problem.setTitle("Acesso Não Autorizado ao Tenant");
    enrichProblemDetail(problem);

    log.warn(
        "Tentativa de acesso cruzado a tenant [cid:{}]: {}", getCorrelationId(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
  }

  @ExceptionHandler(br.com.imovix.tenancy.domain.TenantNotResolvedException.class)
  public ResponseEntity<ProblemDetail> handleTenantNotResolved(
      br.com.imovix.tenancy.domain.TenantNotResolvedException ex) {
    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problem.setType(BAD_REQUEST_TYPE);
    problem.setTitle("Contexto de Tenant Não Resolvido");
    enrichProblemDetail(problem);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleGenericException(Exception ex) {
    String cid = getCorrelationId();
    log.error("Erro inesperado capturado pelo handler global [cid:{}]: ", cid, ex);

    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Ocorreu um erro interno inesperado. Se o problema persistir, contate o suporte informando o Correlation ID.");
    problem.setType(INTERNAL_ERROR_TYPE);
    problem.setTitle("Erro Interno do Servidor");
    enrichProblemDetail(problem);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
  }

  private void enrichProblemDetail(ProblemDetail problem) {
    problem.setProperty("correlationId", getCorrelationId());
    problem.setProperty("timestamp", Instant.now().toString());
  }

  private String getCorrelationId() {
    String cid = MDC.get(CorrelationIdFilter.MDC_KEY);
    return cid != null ? cid : "none";
  }
}
