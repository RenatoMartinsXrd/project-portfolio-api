package com.projectportfolio.frameworks.jpa.repositories;

import com.projectportfolio.core.domain.ProjectStatus;
import com.projectportfolio.frameworks.jpa.entities.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataProjectRepository extends JpaRepository<ProjectEntity, Long> {
    @EntityGraph(attributePaths = "memberIds")
    Optional<ProjectEntity> findWithMemberIdsById(Long id);
    @EntityGraph(attributePaths = "memberIds")
    List<ProjectEntity> findAllWithMemberIdsBy();
    @EntityGraph(attributePaths = "memberIds")
    Page<ProjectEntity> findAll(Pageable pageable);

    @Query("""
            SELECT COUNT(p)
            FROM ProjectEntity p
            JOIN p.memberIds m
            WHERE m = :memberId
              AND p.status IN :activeStatuses
            """)
    long countMemberActiveAllocations(@Param("memberId") Long memberId, @Param("activeStatuses") List<ProjectStatus> activeStatuses);
}
