package com.projectportfolio.adapters.presenters;

import com.projectportfolio.core.domain.Member;
import com.projectportfolio.frameworks.rest.dto.response.ExternalMemberResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExternalMemberConverter {

    public ExternalMemberResponse toResponse(Member member) {
        return new ExternalMemberResponse(member.id(), member.name(), member.role());
    }

    public List<ExternalMemberResponse> toResponseList(List<Member> members) {
        return members.stream().map(this::toResponse).toList();
    }
}
