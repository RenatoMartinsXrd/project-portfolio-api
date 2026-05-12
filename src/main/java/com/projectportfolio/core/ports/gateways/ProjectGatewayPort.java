package com.projectportfolio.core.ports.gateways;

import com.projectportfolio.core.domain.Project;
import com.projectportfolio.core.domain.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProjectGatewayPort {
    Project save(Project project);
    Optional<Project> findById(Long id);
    void deleteById(Long id);
    Page<Project> findAll(String nameFilter, ProjectStatus statusFilter, Pageable pageable);
    long countMemberActiveAllocations(Long memberId);
    List<Project> findAllWithoutPagination();
}
