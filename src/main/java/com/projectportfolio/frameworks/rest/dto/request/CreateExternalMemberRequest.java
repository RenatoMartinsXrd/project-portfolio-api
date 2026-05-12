package com.projectportfolio.frameworks.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateExternalMemberRequest(
        @NotBlank(message = "Nome é obrigatório")
        String name,
        @NotBlank(message = "Atribuição é obrigatória")
        String role
) {
}
