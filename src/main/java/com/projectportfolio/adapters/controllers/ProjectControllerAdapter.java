package com.projectportfolio.adapters.controllers;

import com.projectportfolio.core.domain.Project;
import com.projectportfolio.core.domain.ProjectStatus;
import com.projectportfolio.core.usecases.ProjectUseCaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ProjectControllerAdapter {

    private final ProjectUseCaseService projectUseCaseService;

    public ProjectControllerAdapter(ProjectUseCaseService projectUseCaseService) {
        this.projectUseCaseService = projectUseCaseService;
    }

    public Project create(Project project) {
        return projectUseCaseService.create(project);
    }

    public Project update(Long id, Project project) {
        return projectUseCaseService.update(id, project);
    }

    public Project getById(Long id) {
        return projectUseCaseService.getById(id);
    }

    public Page<Project> list(String name, ProjectStatus status, Pageable pageable) {
        return projectUseCaseService.list(name, status, pageable);
    }

    public void delete(Long id) {
        projectUseCaseService.delete(id);
    }

    public Project updateStatus(Long id, ProjectStatus status) {
        return projectUseCaseService.updateStatus(id, status);
    }

    public Project assignMember(Long id, Long memberId) {
        return projectUseCaseService.assignMember(id, memberId);
    }

    public ProjectUseCaseService.PortfolioReport report() {
        return projectUseCaseService.generateReport();
    }
}
