package br.com.imovix.subscriptions.api;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/** DTO de requisicao para troca de plano. */
public record ChangePlanRequest(
    @NotNull(message = "O identificador do plano e obrigatorio.") UUID planId) {}
