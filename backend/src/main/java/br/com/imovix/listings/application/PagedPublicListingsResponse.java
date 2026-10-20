package br.com.imovix.listings.application;

import java.util.List;

/** Resposta paginada de anuncios publicos de imoveis. */
public record PagedPublicListingsResponse(
    List<PublicListingResponse> content, int page, int size, long totalElements, int totalPages) {}
