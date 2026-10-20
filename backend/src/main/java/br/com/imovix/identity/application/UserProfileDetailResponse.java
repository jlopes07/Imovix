package br.com.imovix.identity.application;

import br.com.imovix.identity.domain.UserStatus;
import java.util.List;
import java.util.UUID;

/** DTO detalhado para o endpoint /api/v1/me, contendo perfil do usuário e empresas vinculadas. */
public record UserProfileDetailResponse(
    UUID id, String email, String name, UserStatus status, List<UserMembershipDto> companies) {}
