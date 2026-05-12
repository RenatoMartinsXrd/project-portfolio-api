package com.projectportfolio.adapters.controllers;

import com.projectportfolio.core.domain.Member;
import com.projectportfolio.core.usecases.ExternalMemberUseCaseService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExternalMemberControllerAdapter {

    private final ExternalMemberUseCaseService externalMemberUseCaseService;

    public ExternalMemberControllerAdapter(ExternalMemberUseCaseService externalMemberUseCaseService) {
        this.externalMemberUseCaseService = externalMemberUseCaseService;
    }

    public List<Member> listAll() {
        return externalMemberUseCaseService.listAll();
    }
}
