package br.com.imovix.properties.application;

import java.util.List;

/** DTO para respostas paginadas de listagem de imóveis. */
public record PagedPropertiesResponse(
    List<PropertyResponse> items, int page, int size, long totalElements, int totalPages) {}
