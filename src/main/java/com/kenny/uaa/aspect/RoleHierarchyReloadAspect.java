package com.kenny.uaa.aspect;

import com.kenny.uaa.security.rolehierarchy.RoleHierarchyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

@Slf4j
@RequiredArgsConstructor
@Aspect
public class RoleHierarchyReloadAspect {
    private final RoleHierarchyImpl roleHierarchy;
    private final RoleHierarchyService roleHierarchyService;

    /**
     In the expression <code>* com.kenny.uaa.service.admin..(..)</code>,
     the first * represents the method's return type, where * denotes any type.
     Then com.kenny.uaa.service.admin.. specifies the method,
     requiring the complete method name. The . indicates any method in the package.
     Finally, (..) specifies the method parameters.
     */
    @Pointcut("execution(* com.kenny.uaa.service.admin.*.*(..))")
    public void applicationPackagePointcut() {

    }

    @AfterReturning("applicationPackagePointcut() && @annotation(com.kenny.uaa.annotation.ReloadRoleHierarchy)")
    public void reloadRoleHierarchy() {
        String roleMap = roleHierarchyService.getRoleHierarchyExpr();
        roleHierarchy.setHierarchy(roleMap);
        log.debug("RoleHierarchy Reloaded");
    }
}
