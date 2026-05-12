package com.projectportfolio.frameworks.rest.dto.response;

import com.projectportfolio.core.domain.ProjectStatus;
import com.projectportfolio.core.domain.RiskLevel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProjectResponse(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate expectedEndDate,
        LocalDate actualEndDate,
        BigDecimal totalBudget,
        String description,
        Long managerId,
        ProjectStatus status,
        RiskLevel risk,
        List<Long> memberIds
) {
}
