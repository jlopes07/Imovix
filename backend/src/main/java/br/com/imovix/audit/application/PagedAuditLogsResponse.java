package br.com.imovix.audit.application;

import java.util.List;

/** DTO paginado com a lista de logs de auditoria. */
public record PagedAuditLogsResponse(
    List<AuditLogResponse> content, int page, int size, long totalElements, int totalPages) {}
