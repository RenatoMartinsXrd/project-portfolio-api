package com.projectportfolio.frameworks.jpa.adapters;

import com.projectportfolio.core.domain.Project;
import com.projectportfolio.core.domain.ProjectStatus;
import com.projectportfolio.core.ports.gateways.ProjectGatewayPort;
import com.projectportfolio.frameworks.jpa.entities.ProjectEntity;
import com.projectportfolio.frameworks.jpa.repositories.SpringDataProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ProjectJpaGatewayAdapter implements ProjectGatewayPort {

    private static final List<ProjectStatus> ACTIVE_STATUSES = List.of(
            ProjectStatus.EM_ANALISE,
            ProjectStatus.ANALISE_REALIZADA,
            ProjectStatus.ANALISE_APROVADA,
            ProjectStatus.INICIADO,
            ProjectStatus.PLANEJADO,
            ProjectStatus.EM_ANDAMENTO
    );

    private final SpringDataProjectRepository repository;

    public ProjectJpaGatewayAdapter(SpringDataProjectRepository repository) {
        this.repository = repository;
    }

    @Override
    public Project save(Project project) {
        return toDomain(repository.save(toEntity(project)));
    }

    @Override
    public Optional<Project> findById(Long id) {
        return repository.findWithMemberIdsById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Page<Project> findAll(String nameFilter, ProjectStatus statusFilter, Pageable pageable) {
        Page<Project> page = repository.findAll(pageable).map(this::toDomain);
        List<Project> filtered = page.getContent().stream()
                .filter(p -> nameFilter == null || p.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                .filter(p -> statusFilter == null || p.getStatus() == statusFilter)
                .toList();
        return new PageImpl<>(filtered, pageable, page.getTotalElements());
    }

    @Override
    public long countMemberActiveAllocations(Long memberId) {
        return repository.countMemberActiveAllocations(memberId, ACTIVE_STATUSES);
    }

    @Override
    public List<Project> findAllWithoutPagination() {
        return repository.findAllWithMemberIdsBy()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private Project toDomain(ProjectEntity entity) {
        return new Project(
                entity.getId(),
                entity.getName(),
                entity.getStartDate(),
                entity.getExpectedEndDate(),
                entity.getActualEndDate(),
                entity.getTotalBudget(),
                entity.getDescription(),
                entity.getManagerId(),
                entity.getStatus(),
                entity.getMemberIds()
        );
    }

    private ProjectEntity toEntity(Project project) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(project.getId());
        entity.setName(project.getName());
        entity.setStartDate(project.getStartDate());
        entity.setExpectedEndDate(project.getExpectedEndDate());
        entity.setActualEndDate(project.getActualEndDate());
        entity.setTotalBudget(project.getTotalBudget());
        entity.setDescription(project.getDescription());
        entity.setManagerId(project.getManagerId());
        entity.setStatus(project.getStatus());
        entity.setMemberIds(new ArrayList<>(project.getMemberIds()));
        return entity;
    }
}
