package com.projectportfolio.core.domain;

public enum ProjectStatus {
    EM_ANALISE,
    ANALISE_REALIZADA,
    ANALISE_APROVADA,
    INICIADO,
    PLANEJADO,
    EM_ANDAMENTO,
    ENCERRADO,
    CANCELADO;

    public boolean canTransitionTo(ProjectStatus target) {
        if (target == null || target == this) {
            return false;
        }
        if (target == CANCELADO) {
            return true;
        }
        return switch (this) {
            case EM_ANALISE -> target == ANALISE_REALIZADA;
            case ANALISE_REALIZADA -> target == ANALISE_APROVADA;
            case ANALISE_APROVADA -> target == INICIADO;
            case INICIADO -> target == PLANEJADO;
            case PLANEJADO -> target == EM_ANDAMENTO;
            case EM_ANDAMENTO -> target == ENCERRADO;
            default -> false;
        };
    }

    public boolean canBeDeleted() {
        return this != INICIADO && this != EM_ANDAMENTO && this != ENCERRADO;
    }
}
