package com.projectportfolio.frameworks.rest.dto.request;

import com.projectportfolio.core.domain.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateOrUpdateProjectRequest(
        @NotBlank(message = "Nome é obrigatório")
        String name,
        @NotNull(message = "Data de início é obrigatória")
        LocalDate startDate,
        @NotNull(message = "Previsão de término é obrigatória")
        LocalDate expectedEndDate,
        LocalDate actualEndDate,
        @NotNull(message = "Orçamento total é obrigatório")
        BigDecimal totalBudget,
        String description,
        @NotNull(message = "Gerente responsável é obrigatório")
        Long managerId,
        @NotNull(message = "Status é obrigatório")
        ProjectStatus status
) {
}
