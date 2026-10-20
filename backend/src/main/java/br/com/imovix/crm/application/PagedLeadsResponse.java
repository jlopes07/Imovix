package br.com.imovix.crm.application;

import java.util.List;

/** Resposta paginada de leads. */
public record PagedLeadsResponse(
    List<LeadResponse> content, int page, int size, long totalElements, int totalPages) {}
