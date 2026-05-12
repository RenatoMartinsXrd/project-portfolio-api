package com.projectportfolio.core.ports.gateways;

import com.projectportfolio.core.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberGatewayPort {
    Member create(String name, String role);
    Optional<Member> findById(Long id);

    List<Member> findAll();
}
