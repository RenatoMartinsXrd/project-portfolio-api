package com.projectportfolio.adapters.presenters;

import com.projectportfolio.core.usecases.ProjectUseCaseService;
import com.projectportfolio.frameworks.rest.dto.response.PortfolioReportResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PortfolioReportConverter {

    public PortfolioReportResponse toResponse(ProjectUseCaseService.PortfolioReport report) {
        Map<String, Long> qty = report.quantityByStatus().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));
        Map<String, java.math.BigDecimal> budget = report.totalBudgetByStatus().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));
        return new PortfolioReportResponse(qty, budget, report.averageClosedProjectsDurationDays(), report.totalUniqueAllocatedMembers());
    }
}
