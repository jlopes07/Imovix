package br.com.imovix.crm.api;

import java.time.Instant;

/** DTO de entrada para conclusao de atividade ou visita com feedback. */
public record CompleteActivityRequest(String feedback, Instant completedAt) {}
