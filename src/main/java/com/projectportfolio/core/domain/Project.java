package com.projectportfolio.core.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Project {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private LocalDate actualEndDate;
    private BigDecimal totalBudget;
    private String description;
    private Long managerId;
    private ProjectStatus status;
    private final List<Long> memberIds;

    public Project(Long id, String name, LocalDate startDate, LocalDate expectedEndDate, LocalDate actualEndDate,
                   BigDecimal totalBudget, String description, Long managerId, ProjectStatus status, List<Long> memberIds) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.expectedEndDate = expectedEndDate;
        this.actualEndDate = actualEndDate;
        this.totalBudget = totalBudget;
        this.description = description;
        this.managerId = managerId;
        this.status = status;
        this.memberIds = memberIds == null ? new ArrayList<>() : new ArrayList<>(memberIds);
    }

    public void validateBasics() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome do projeto é obrigatório");
        }
        if (startDate == null || expectedEndDate == null) {
            throw new IllegalArgumentException("Datas de início e previsão de término são obrigatórias");
        }
        if (expectedEndDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Previsão de término não pode ser antes da data de início");
        }
        if (totalBudget == null || totalBudget.signum() < 0) {
            throw new IllegalArgumentException("Orçamento total deve ser maior ou igual a zero");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status é obrigatório");
        }
    }

    public void validateMembersRange() {
        if (memberIds.size() < 1 || memberIds.size() > 10) {
            throw new IllegalArgumentException("Projeto deve ter entre 1 e 10 membros");
        }
    }

    public void addMember(Long memberId) {
        Objects.requireNonNull(memberId, "Membro é obrigatório");
        if (memberIds.contains(memberId)) {
            return;
        }
        memberIds.add(memberId);
        if (memberIds.size() > 10) {
            throw new IllegalArgumentException("Projeto não pode ter mais de 10 membros");
        }
    }

    public void updateStatus(ProjectStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalArgumentException("Transição de status inválida");
        }
        this.status = newStatus;
    }

    public RiskLevel calculateRisk() {
        long months = ChronoUnit.MONTHS.between(startDate, expectedEndDate);
        if (totalBudget.compareTo(BigDecimal.valueOf(500000)) > 0 || months > 6) {
            return RiskLevel.ALTO;
        }
        if ((totalBudget.compareTo(BigDecimal.valueOf(100001)) >= 0
                && totalBudget.compareTo(BigDecimal.valueOf(500000)) <= 0)
                || (months > 3 && months <= 6)) {
            return RiskLevel.MEDIO;
        }
        return RiskLevel.BAIXO;
    }

    public void updateFrom(Project source) {
        this.name = source.name;
        this.startDate = source.startDate;
        this.expectedEndDate = source.expectedEndDate;
        this.actualEndDate = source.actualEndDate;
        this.totalBudget = source.totalBudget;
        this.description = source.description;
        this.managerId = source.managerId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getExpectedEndDate() { return expectedEndDate; }
    public LocalDate getActualEndDate() { return actualEndDate; }
    public BigDecimal getTotalBudget() { return totalBudget; }
    public String getDescription() { return description; }
    public Long getManagerId() { return managerId; }
    public ProjectStatus getStatus() { return status; }
    public List<Long> getMemberIds() { return new ArrayList<>(memberIds); }
}
