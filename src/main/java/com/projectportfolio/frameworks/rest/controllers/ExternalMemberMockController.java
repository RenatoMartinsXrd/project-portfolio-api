package com.projectportfolio.frameworks.rest.controllers;

import com.projectportfolio.adapters.controllers.ExternalMemberControllerAdapter;
import com.projectportfolio.adapters.presenters.ExternalMemberConverter;
import com.projectportfolio.core.domain.Member;
import com.projectportfolio.core.ports.gateways.MemberGatewayPort;
import com.projectportfolio.frameworks.rest.dto.request.CreateExternalMemberRequest;
import com.projectportfolio.frameworks.rest.dto.response.ExternalMemberResponse;
import com.projectportfolio.shared.exception.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/external/members")
public class ExternalMemberMockController {

    private final MemberGatewayPort memberGatewayPort;
    private final ExternalMemberControllerAdapter externalMemberControllerAdapter;
    private final ExternalMemberConverter externalMemberConverter;

    public ExternalMemberMockController(MemberGatewayPort memberGatewayPort,
                                        ExternalMemberControllerAdapter externalMemberControllerAdapter,
                                        ExternalMemberConverter externalMemberConverter) {
        this.memberGatewayPort = memberGatewayPort;
        this.externalMemberControllerAdapter = externalMemberControllerAdapter;
        this.externalMemberConverter = externalMemberConverter;
    }

    @GetMapping
    public List<ExternalMemberResponse> listAll() {
        return externalMemberConverter.toResponseList(externalMemberControllerAdapter.listAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExternalMemberResponse create(@Valid @RequestBody CreateExternalMemberRequest request) {
        Member created = memberGatewayPort.create(request.name(), request.role());
        return externalMemberConverter.toResponse(created);
    }

    @GetMapping("/{id}")
    public ExternalMemberResponse getById(@PathVariable Long id) {
        Member member = memberGatewayPort.findById(id).orElseThrow(() -> new NotFoundException("Membro não encontrado"));
        return externalMemberConverter.toResponse(member);
    }
}
