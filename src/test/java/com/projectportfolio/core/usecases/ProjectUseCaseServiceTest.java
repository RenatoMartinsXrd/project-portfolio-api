package com.projectportfolio.core.usecases;

import com.projectportfolio.core.domain.Member;
import com.projectportfolio.core.domain.Project;
import com.projectportfolio.core.domain.ProjectStatus;
import com.projectportfolio.core.domain.RiskLevel;
import com.projectportfolio.core.ports.gateways.MemberGatewayPort;
import com.projectportfolio.core.ports.gateways.ProjectGatewayPort;
import com.projectportfolio.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ProjectUseCaseServiceTest {

    private InMemoryProjectGateway projectGateway;
    private InMemoryMemberGateway memberGateway;
    private ProjectUseCaseService service;

    @BeforeEach
    void setUp() {
        projectGateway = new InMemoryProjectGateway();
        memberGateway = new InMemoryMemberGateway();
        service = new ProjectUseCaseService(projectGateway, memberGateway);
    }

    @Test
    void shouldCalculateLowRisk() {
        Project project = baseProject(ProjectStatus.EM_ANALISE, BigDecimal.valueOf(100000), LocalDate.now().plusMonths(3));

        assertEquals(RiskLevel.BAIXO, project.calculateRisk());
    }

    @Test
    void shouldCalculateMediumRiskByBudget() {
        Project project = baseProject(ProjectStatus.EM_ANALISE, BigDecimal.valueOf(250000), LocalDate.now().plusMonths(2));

        assertEquals(RiskLevel.MEDIO, project.calculateRisk());
    }

    @Test
    void shouldCalculateMediumRiskByDeadline() {
        Project project = baseProject(ProjectStatus.EM_ANALISE, BigDecimal.valueOf(90000), LocalDate.now().plusMonths(5));

        assertEquals(RiskLevel.MEDIO, project.calculateRisk());
    }

    @Test
    void shouldCalculateHighRiskByBudget() {
        Project project = baseProject(ProjectStatus.EM_ANALISE, BigDecimal.valueOf(600000), LocalDate.now().plusMonths(2));

        assertEquals(RiskLevel.ALTO, project.calculateRisk());
    }

    @Test
    void shouldCalculateHighRiskByDeadline() {
        Project project = baseProject(ProjectStatus.EM_ANALISE, BigDecimal.valueOf(90000), LocalDate.now().plusMonths(7));

        assertEquals(RiskLevel.ALTO, project.calculateRisk());
    }

    @Test
    void shouldBlockDeletingStartedProject() {
        Project created = service.create(baseProject(ProjectStatus.INICIADO));

        assertThrows(BusinessException.class, () -> service.delete(created.getId()));
    }

    @Test
    void shouldBlockDeletingInProgressProject() {
        Project created = service.create(baseProject(ProjectStatus.EM_ANDAMENTO));

        assertThrows(BusinessException.class, () -> service.delete(created.getId()));
    }

    @Test
    void shouldBlockDeletingClosedProject() {
        Project created = service.create(baseProject(ProjectStatus.ENCERRADO));

        assertThrows(BusinessException.class, () -> service.delete(created.getId()));
    }

    @Test
    void shouldAllowDeletingProjectInAnalysis() {
        Project created = service.create(baseProject(ProjectStatus.EM_ANALISE));

        service.delete(created.getId());

        assertTrue(projectGateway.findById(created.getId()).isEmpty());
    }

    @Test
    void shouldAllowSequentialStatusTransition() {
        Project created = service.create(baseProject(ProjectStatus.EM_ANALISE));

        Project updated = service.updateStatus(created.getId(), ProjectStatus.ANALISE_REALIZADA);

        assertEquals(ProjectStatus.ANALISE_REALIZADA, updated.getStatus());
    }

    @Test
    void shouldBlockInvalidStatusTransition() {
        Project created = service.create(baseProject(ProjectStatus.EM_ANALISE));

        assertThrows(IllegalArgumentException.class, () -> service.updateStatus(created.getId(), ProjectStatus.PLANEJADO));
    }

    @Test
    void shouldAllowCancelFromAnyStatus() {
        Project created = service.create(baseProject(ProjectStatus.ANALISE_APROVADA));

        Project updated = service.updateStatus(created.getId(), ProjectStatus.CANCELADO);

        assertEquals(ProjectStatus.CANCELADO, updated.getStatus());
    }

    @Test
    void shouldAllowAssignEmployeeMember() {
        Member member = memberGateway.create("João", "funcionario");
        Project created = service.create(baseProject(ProjectStatus.EM_ANALISE, new ArrayList<>()));

        Project updated = service.assignMember(created.getId(), member.id());

        assertTrue(updated.getMemberIds().contains(member.id()));
    }

    @Test
    void shouldRejectNonEmployeeMember() {
        Member member = memberGateway.create("Ana", "gerente");
        Project created = service.create(baseProject(ProjectStatus.EM_ANALISE, new ArrayList<>()));

        assertThrows(BusinessException.class, () -> service.assignMember(created.getId(), member.id()));
    }

    @Test
    void shouldBlockMemberWithMoreThanThreeActiveProjects() {
        Member member = memberGateway.create("João", "funcionario");

        service.create(baseProject(ProjectStatus.EM_ANALISE, new ArrayList<>(List.of(member.id()))));
        service.create(baseProject(ProjectStatus.ANALISE_REALIZADA, new ArrayList<>(List.of(member.id()))));
        service.create(baseProject(ProjectStatus.PLANEJADO, new ArrayList<>(List.of(member.id()))));

        Project fourthProject = service.create(baseProject(ProjectStatus.EM_ANALISE, new ArrayList<>()));

        assertThrows(BusinessException.class, () -> service.assignMember(fourthProject.getId(), member.id()));
    }

    @Test
    void shouldRejectProjectWithExpectedEndDateBeforeStartDate() {
        Project project = new Project(
                null,
                "Projeto inválido",
                LocalDate.of(2026, 5, 10),
                LocalDate.of(2026, 5, 1),
                null,
                BigDecimal.valueOf(100000),
                "descricao",
                1L,
                ProjectStatus.EM_ANALISE,
                new ArrayList<>()
        );

        assertThrows(IllegalArgumentException.class, () -> service.create(project));
    }

    @Test
    void shouldRejectProjectWithoutRequiredFields() {
        Project project = new Project(
                null,
                "",
                null,
                null,
                null,
                null,
                "descricao",
                1L,
                null,
                new ArrayList<>()
        );

        assertThrows(IllegalArgumentException.class, () -> service.create(project));
    }

    @Test
    void shouldGeneratePortfolioReport() {
        service.create(baseProject(ProjectStatus.EM_ANALISE, BigDecimal.valueOf(100000), LocalDate.now().plusMonths(2), List.of(10L)));
        service.create(baseProject(ProjectStatus.ENCERRADO, BigDecimal.valueOf(200000), LocalDate.now().plusMonths(4), List.of(11L)));

        var report = service.generateReport();

        assertTrue(report.quantityByStatus().get(ProjectStatus.EM_ANALISE) >= 1);
        assertTrue(report.quantityByStatus().get(ProjectStatus.ENCERRADO) >= 1);
        assertTrue(report.totalBudgetByStatus().get(ProjectStatus.EM_ANALISE).compareTo(BigDecimal.ZERO) > 0);
        assertTrue(report.totalUniqueAllocatedMembers() >= 2);
    }

    private Project baseProject(ProjectStatus status) {
        return baseProject(status, BigDecimal.valueOf(120000), LocalDate.now().plusMonths(2), List.of(10L));
    }

    private Project baseProject(ProjectStatus status, List<Long> memberIds) {
        return baseProject(status, BigDecimal.valueOf(120000), LocalDate.now().plusMonths(2), memberIds);
    }

    private Project baseProject(ProjectStatus status, BigDecimal budget, LocalDate expectedEndDate) {
        return baseProject(status, budget, expectedEndDate, List.of(10L));
    }

    private Project baseProject(ProjectStatus status, BigDecimal budget, LocalDate expectedEndDate, List<Long> memberIds) {
        return new Project(
                null,
                "Projeto Teste",
                LocalDate.now(),
                expectedEndDate,
                status == ProjectStatus.ENCERRADO ? expectedEndDate : null,
                budget,
                "descricao",
                1L,
                status,
                new ArrayList<>(memberIds)
        );
    }

    static class InMemoryProjectGateway implements ProjectGatewayPort {
        private final Map<Long, Project> db = new HashMap<>();
        private long seq = 1L;

        @Override
        public Project save(Project project) {
            if (project.getId() == null) {
                project.setId(seq++);
            }
            db.put(project.getId(), project);
            return project;
        }

        @Override
        public Optional<Project> findById(Long id) {
            return Optional.ofNullable(db.get(id));
        }

        @Override
        public void deleteById(Long id) {
            db.remove(id);
        }

        @Override
        public Page<Project> findAll(String nameFilter, ProjectStatus statusFilter, Pageable pageable) {
            List<Project> filtered = db.values().stream()
                    .filter(project -> nameFilter == null || project.getName().toLowerCase().contains(nameFilter.toLowerCase()))
                    .filter(project -> statusFilter == null || project.getStatus() == statusFilter)
                    .toList();

            return new PageImpl<>(filtered, pageable, filtered.size());
        }

        @Override
        public long countMemberActiveAllocations(Long memberId) {
            return db.values().stream()
                    .filter(project -> project.getMemberIds().contains(memberId))
                    .filter(project -> project.getStatus() != ProjectStatus.ENCERRADO)
                    .filter(project -> project.getStatus() != ProjectStatus.CANCELADO)
                    .count();
        }

        @Override
        public List<Project> findAllWithoutPagination() {
            return db.values().stream().toList();
        }
    }

    static class InMemoryMemberGateway implements MemberGatewayPort {
        private final Map<Long, Member> db = new HashMap<>();
        private long seq = 1L;

        @Override
        public Member create(String name, String role) {
            Member member = new Member(seq++, name, role);
            db.put(member.id(), member);
            return member;
        }

        @Override
        public Optional<Member> findById(Long id) {
            return Optional.ofNullable(db.get(id));
        }

        @Override
        public List<Member> findAll() {
            return db.values().stream()
                    .sorted(Comparator.comparing(Member::id))
                    .toList();
        }
    }
}