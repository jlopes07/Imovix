package br.com.imovix.listings.application;

import java.time.Instant;
import java.util.UUID;

/** DTO de retorno para confirmacao de envio de manifestacao de interesse. */
public record InquiryResponse(UUID leadId, String message, Instant receivedAt) {}
