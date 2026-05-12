package com.projectportfolio.core.domain;

public record Member(Long id, String name, String role) {

    public boolean isEmployee() {
        return "funcionario".equalsIgnoreCase(role);
    }
}
