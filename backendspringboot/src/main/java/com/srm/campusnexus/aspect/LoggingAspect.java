package com.srm.campusnexus.aspect;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Pointcut for all service methods
     */
    @Pointcut("execution(* com.srm.campusnexus.service.*.*(..))")
    public void serviceLayer() {
    }

    /**
     * Pointcut for all controller methods
     */
    @Pointcut("execution(* com.srm.campusnexus.controller.*.*(..))")
    public void controllerLayer() {
    }

    /**
     * Pointcut for all repository methods
     */
    @Pointcut("execution(* com.srm.campusnexus.repository.*.*(..))")
    public void repositoryLayer() {
    }

    /**
     * Log method entry
     */
    @Before("serviceLayer() || controllerLayer()")
    public void logBefore(JoinPoint joinPoint) {
        logger.debug("Entering method: {} with arguments: {}",
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * Log method exit
     */
    @AfterReturning(pointcut = "serviceLayer() || controllerLayer()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.debug("Method {} completed successfully",
                joinPoint.getSignature().toShortString());
    }

    /**
     * Log exceptions
     */
    @AfterThrowing(pointcut = "serviceLayer() || controllerLayer()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in method: {} with message: {}",
                joinPoint.getSignature().toShortString(),
                exception.getMessage());
    }

    /**
     * Log method execution time
     */
    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            logger.debug("Method {} executed in {} ms",
                    joinPoint.getSignature().toShortString(),
                    executionTime);

            if (executionTime > 1000) {
                logger.warn("Slow method detected: {} took {} ms",
                        joinPoint.getSignature().toShortString(),
                        executionTime);
            }

            return result;
        } catch (Throwable throwable) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            logger.error("Method {} failed after {} ms with exception: {}",
                    joinPoint.getSignature().toShortString(),
                    executionTime,
                    throwable.getMessage());

            throw throwable;
        }
    }

    /**
     * Log database operations
     */
    @Around("repositoryLayer()")
    public Object logDatabaseOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        logger.debug("Database operation started: {}", joinPoint.getSignature().toShortString());

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            logger.debug("Database operation completed: {} in {} ms",
                    joinPoint.getSignature().toShortString(),
                    executionTime);

            return result;
        } catch (Throwable throwable) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            logger.error("Database operation failed: {} after {} ms with exception: {}",
                    joinPoint.getSignature().toShortString(),
                    executionTime,
                    throwable.getMessage());

            throw throwable;
        }
    }
}