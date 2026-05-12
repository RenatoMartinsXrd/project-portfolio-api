package com.projectportfolio.frameworks.rest.controllers;

import com.projectportfolio.adapters.controllers.ProjectControllerAdapter;
import com.projectportfolio.adapters.presenters.PortfolioReportConverter;
import com.projectportfolio.adapters.presenters.ProjectConverter;
import com.projectportfolio.core.domain.ProjectStatus;
import com.projectportfolio.frameworks.rest.dto.request.CreateOrUpdateProjectRequest;
import com.projectportfolio.frameworks.rest.dto.request.UpdateStatusRequest;
import com.projectportfolio.frameworks.rest.dto.response.PortfolioReportResponse;
import com.projectportfolio.frameworks.rest.dto.response.ProjectResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectRestController {

    private final ProjectControllerAdapter adapter;
    private final ProjectConverter converter;
    private final PortfolioReportConverter reportConverter;

    public ProjectRestController(ProjectControllerAdapter adapter, ProjectConverter converter, PortfolioReportConverter reportConverter) {
        this.adapter = adapter;
        this.converter = converter;
        this.reportConverter = reportConverter;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody CreateOrUpdateProjectRequest request) {
        return converter.toResponse(adapter.create(converter.toDomain(request)));
    }

    @GetMapping("/{id}")
    public ProjectResponse getById(@PathVariable Long id) {
        return converter.toResponse(adapter.getById(id));
    }

    @GetMapping
    public Page<ProjectResponse> list(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) ProjectStatus status,
                                      Pageable pageable) {
        return adapter.list(name, status, pageable).map(converter::toResponse);
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody CreateOrUpdateProjectRequest request) {
        return converter.toResponse(adapter.update(id, converter.toDomain(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        adapter.delete(id);
    }

    @PatchMapping("/{id}/status")
    public ProjectResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest request) {
        return converter.toResponse(adapter.updateStatus(id, request.status()));
    }

    @PostMapping("/{id}/members/{memberId}")
    public ProjectResponse assignMember(@PathVariable Long id, @PathVariable Long memberId) {
        return converter.toResponse(adapter.assignMember(id, memberId));
    }

    @GetMapping("/portfolio/report")
    public PortfolioReportResponse report() {
        return reportConverter.toResponse(adapter.report());
    }
}
