package com.projectportfolio.frameworks.rest.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record PortfolioReportResponse(
        Map<String, Long> quantityByStatus,
        Map<String, BigDecimal> totalBudgetByStatus,
        BigDecimal averageClosedProjectsDurationDays,
        int totalUniqueAllocatedMembers
) {
}
