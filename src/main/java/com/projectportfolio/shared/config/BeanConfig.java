package com.projectportfolio.shared.config;

import com.projectportfolio.core.ports.gateways.MemberGatewayPort;
import com.projectportfolio.core.ports.gateways.ProjectGatewayPort;
import com.projectportfolio.core.usecases.ExternalMemberUseCaseService;
import com.projectportfolio.core.usecases.ProjectUseCaseService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ProjectUseCaseService projectUseCaseService(ProjectGatewayPort projectGatewayPort, MemberGatewayPort memberGatewayPort) {
        return new ProjectUseCaseService(projectGatewayPort, memberGatewayPort);
    }

    @Bean
    public ExternalMemberUseCaseService externalMemberUseCaseService(MemberGatewayPort memberGatewayPort) {
        return new ExternalMemberUseCaseService(memberGatewayPort);
    }
}
