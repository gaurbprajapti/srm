package com.srm.campusnexus.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.srm.campusnexus.security.UserDetailsImpl;

@Aspect
@Component
public class SecurityAspect {

    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");

    /**
     * Pointcut for authentication related methods
     */
    @Pointcut("execution(* com.srm.campusnexus.controller.UserController.login(..))")
    public void loginMethod() {
    }

    @Pointcut("execution(* com.srm.campusnexus.controller.UserController.register(..))")
    public void registerMethod() {
    }

    @Pointcut("execution(* com.srm.campusnexus.controller.UserController.logout(..))")
    public void logoutMethod() {
    }

    /**
     * Pointcut for admin operations
     */
    @Pointcut("execution(* com.srm.campusnexus.controller.*.*(..)) && @annotation(org.springframework.security.access.prepost.PreAuthorize)")
    public void adminOperations() {
    }

    /**
     * Log authentication attempts
     */
    @Before("loginMethod()")
    public void logLoginAttempt(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] != null) {
            securityLogger.info("Login attempt for user: {}", extractUsernameFromLoginRequest(args[0]));
        }
    }

    /**
     * Log successful authentication
     */
    @AfterReturning("loginMethod()")
    public void logSuccessfulLogin(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] != null) {
            securityLogger.info("Successful login for user: {}", extractUsernameFromLoginRequest(args[0]));
        }
    }

    /**
     * Log user registration
     */
    @AfterReturning("registerMethod()")
    public void logUserRegistration(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] != null) {
            securityLogger.info("New user registered: {}", extractUsernameFromRegisterRequest(args[0]));
        }
    }

    /**
     * Log logout events
     */
    @Before("logoutMethod()")
    public void logLogout(JoinPoint joinPoint) {
        String username = getCurrentUsername();
        if (username != null) {
            securityLogger.info("User logout: {}", username);
        }
    }

    /**
     * Log admin operations
     */
    @Before("adminOperations()")
    public void logAdminOperation(JoinPoint joinPoint) {
        String username = getCurrentUsername();
        String operation = joinPoint.getSignature().toShortString();

        securityLogger.warn("Admin operation performed by {}: {}", username, operation);
    }

    /**
     * Log unauthorized access attempts
     */
    @Before("execution(* com.srm.campusnexus.controller.*.*(..))")
    public void logUnauthorizedAccess(JoinPoint joinPoint) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Log if accessing protected endpoint without authentication
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            String endpoint = joinPoint.getSignature().toShortString();
            securityLogger.warn("Unauthorized access attempt to: {}", endpoint);
        }
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) auth.getPrincipal()).getUsername();
        }
        return "unknown";
    }

    private String extractUsernameFromLoginRequest(Object loginRequest) {
        // This would need to be implemented based on your actual login request DTO
        try {
            return loginRequest.toString(); // Placeholder implementation
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String extractUsernameFromRegisterRequest(Object registerRequest) {
        // This would need to be implemented based on your actual register request DTO
        try {
            return registerRequest.toString(); // Placeholder implementation
        } catch (Exception e) {
            return "unknown";
        }
    }
}