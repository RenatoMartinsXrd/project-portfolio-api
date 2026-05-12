package com.projectportfolio.core.usecases;

import com.projectportfolio.core.domain.Member;
import com.projectportfolio.core.domain.Project;
import com.projectportfolio.core.domain.ProjectStatus;
import com.projectportfolio.core.ports.gateways.MemberGatewayPort;
import com.projectportfolio.core.ports.gateways.ProjectGatewayPort;
import com.projectportfolio.shared.exception.BusinessException;
import com.projectportfolio.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectUseCaseService {

    private final ProjectGatewayPort projectGatewayPort;
    private final MemberGatewayPort memberGatewayPort;

    public ProjectUseCaseService(ProjectGatewayPort projectGatewayPort, MemberGatewayPort memberGatewayPort) {
        this.projectGatewayPort = projectGatewayPort;
        this.memberGatewayPort = memberGatewayPort;
    }

    public Project create(Project project) {
        project.validateBasics();
        return projectGatewayPort.save(project);
    }

    public Project update(Long id, Project projectData) {
        projectData.validateBasics();
        Project existing = getById(id);
        existing.updateFrom(projectData);
        return projectGatewayPort.save(existing);
    }

    public Project getById(Long id) {
        return projectGatewayPort.findById(id).orElseThrow(() -> new NotFoundException("Projeto não encontrado"));
    }

    public Page<Project> list(String name, ProjectStatus status, Pageable pageable) {
        return projectGatewayPort.findAll(name, status, pageable);
    }

    public void delete(Long id) {
        Project project = getById(id);
        if (!project.getStatus().canBeDeleted()) {
            throw new BusinessException("Projeto com status iniciado, em andamento ou encerrado não pode ser excluído");
        }
        projectGatewayPort.deleteById(id);
    }

    public Project updateStatus(Long id, ProjectStatus newStatus) {
        Project project = getById(id);
        project.updateStatus(newStatus);
        return projectGatewayPort.save(project);
    }

    public Project assignMember(Long projectId, Long memberId) {
        Project project = getById(projectId);
        Member member = memberGatewayPort.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Membro não encontrado na API externa"));

        if (!member.isEmployee()) {
            throw new BusinessException("Apenas membros com atribuição 'funcionario' podem ser associados");
        }
        if (projectGatewayPort.countMemberActiveAllocations(memberId) >= 3) {
            throw new BusinessException("Membro não pode estar alocado em mais de 3 projetos ativos");
        }

        project.addMember(memberId);
        project.validateMembersRange();
        return projectGatewayPort.save(project);
    }

    public PortfolioReport generateReport() {
        List<Project> projects = projectGatewayPort.findAllWithoutPagination();
        Map<ProjectStatus, Long> quantityByStatus = new EnumMap<>(ProjectStatus.class);
        Map<ProjectStatus, BigDecimal> totalBudgetByStatus = new EnumMap<>(ProjectStatus.class);
        for (ProjectStatus status : ProjectStatus.values()) {
            quantityByStatus.put(status, 0L);
            totalBudgetByStatus.put(status, BigDecimal.ZERO);
        }

        for (Project project : projects) {
            quantityByStatus.compute(project.getStatus(), (k, v) -> v == null ? 1L : v + 1L);
            totalBudgetByStatus.compute(project.getStatus(), (k, v) -> (v == null ? BigDecimal.ZERO : v).add(project.getTotalBudget()));
        }

        double avgClosedDuration = projects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.ENCERRADO && p.getActualEndDate() != null)
                .mapToLong(p -> ChronoUnit.DAYS.between(p.getStartDate(), p.getActualEndDate()))
                .average()
                .orElse(0.0);

        Set<Long> uniqueMembers = projects.stream()
                .flatMap(p -> p.getMemberIds().stream())
                .collect(Collectors.toSet());

        return new PortfolioReport(
                quantityByStatus,
                totalBudgetByStatus,
                BigDecimal.valueOf(avgClosedDuration).setScale(2, RoundingMode.HALF_UP),
                uniqueMembers.size()
        );
    }

    public record PortfolioReport(
            Map<ProjectStatus, Long> quantityByStatus,
            Map<ProjectStatus, BigDecimal> totalBudgetByStatus,
            BigDecimal averageClosedProjectsDurationDays,
            int totalUniqueAllocatedMembers
    ) {
    }
}
