package com.projectportfolio.frameworks.rest.external;

import com.projectportfolio.core.domain.Member;
import com.projectportfolio.core.ports.gateways.MemberGatewayPort;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MockMemberGatewayAdapter implements MemberGatewayPort {

    private final AtomicLong sequence = new AtomicLong(1L);
    private final Map<Long, Member> members = new ConcurrentHashMap<>();

    @Override
    public Member create(String name, String role) {
        Long id = sequence.getAndIncrement();
        Member member = new Member(id, name, role);
        members.put(id, member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(members.get(id));
    }

    @Override
    public List<Member> findAll() {
        return members.values().stream()
                .sorted(Comparator.comparing(Member::id))
                .toList();
    }
}
