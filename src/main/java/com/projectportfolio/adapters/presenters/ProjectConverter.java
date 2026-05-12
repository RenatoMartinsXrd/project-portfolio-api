package com.projectportfolio.adapters.presenters;

import com.projectportfolio.core.domain.Project;
import com.projectportfolio.frameworks.rest.dto.request.CreateOrUpdateProjectRequest;
import com.projectportfolio.frameworks.rest.dto.response.ProjectResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ProjectConverter {

    public Project toDomain(CreateOrUpdateProjectRequest request) {
        return new Project(
                null,
                request.name(),
                request.startDate(),
                request.expectedEndDate(),
                request.actualEndDate(),
                request.totalBudget(),
                request.description(),
                request.managerId(),
                request.status(),
                new ArrayList<>()
        );
    }

    public ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getStartDate(),
                project.getExpectedEndDate(),
                project.getActualEndDate(),
                project.getTotalBudget(),
                project.getDescription(),
                project.getManagerId(),
                project.getStatus(),
                project.calculateRisk(),
                project.getMemberIds()
        );
    }
}
