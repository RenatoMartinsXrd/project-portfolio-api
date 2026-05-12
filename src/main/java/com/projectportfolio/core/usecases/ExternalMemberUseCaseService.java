package com.projectportfolio.core.usecases;

import com.projectportfolio.core.domain.Member;
import com.projectportfolio.core.ports.gateways.MemberGatewayPort;

import java.util.List;

public class ExternalMemberUseCaseService {

    private final MemberGatewayPort memberGatewayPort;

    public ExternalMemberUseCaseService(MemberGatewayPort memberGatewayPort) {
        this.memberGatewayPort = memberGatewayPort;
    }

    public List<Member> listAll() {
        return memberGatewayPort.findAll();
    }
}
