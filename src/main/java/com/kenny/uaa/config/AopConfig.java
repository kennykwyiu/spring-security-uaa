package com.kenny.uaa.config;

import com.kenny.uaa.aspect.RoleHierarchyReloadAspect;
import com.kenny.uaa.security.rolehierarchy.RoleHierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@RequiredArgsConstructor
@Configuration
@EnableAspectJAutoProxy
public class AopConfig {
    private final RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    private final RoleHierarchyService roleHierarchyService;

    @Bean
    public RoleHierarchyReloadAspect roleHierarchyReloadAspect() {
        return new RoleHierarchyReloadAspect(roleHierarchy, roleHierarchyService);
    }
}
