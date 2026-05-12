package com.projectportfolio.frameworks.rest.dto.request;

import com.projectportfolio.core.domain.ProjectStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull(message = "Novo status é obrigatório")
        ProjectStatus status
) {
}
