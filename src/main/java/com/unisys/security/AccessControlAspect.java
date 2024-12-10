package com.unisys.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Aspect
public class AccessControlAspect {

    private static final Logger logger = LoggerFactory.getLogger(AccessControlAspect.class);

    @Around("@annotation(requiresAccessControl)")
    public Object checkAccessControl(ProceedingJoinPoint joinPoint, RequiresAccessControl requiresAccessControl) throws Throwable {
        String requiredRole = requiresAccessControl.role();
        String currentUserRole = getCurrentUserRole();

        // Check if the user role matches the required role
        if (!currentUserRole.equals(requiredRole)) {
            logger.warn("Access denied. User role '{}' does not have permission to perform this action. Required role: '{}'",
                    currentUserRole, requiredRole);
            throw new SecurityException("Access denied! Only users with " + requiredRole + " role can perform this action.");
        }

        // If roles match, proceed with the method execution
        return joinPoint.proceed();
    }

    // Dynamically fetch the current user's role (e.g., from HTTP headers)
    private String getCurrentUserRole() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String role = request.getHeader("X-Role");
        return (role != null && !role.isEmpty()) ? role : "USER"; // Default to "USER" if no role header is found
    }
}
