package com.unisys.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Define a pointcut for controller methods
    @Pointcut("execution(* com.unisys.controller.UserResource.*(..))")
    public void controllerMethods() {}

    // Before executing a controller method, log the method name
    @Before("controllerMethods()")
    public void logBefore() {
        logger.info("Controller method execution started.");
    }

    // After executing a controller method, log the successful return
    @AfterReturning("controllerMethods()")
    public void logAfterReturning() {
        logger.info("Controller method execution completed successfully.");
    }

    // After throwing an exception, log the error
    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex) {
        logger.error("Exception in controller method: {}", ex.getMessage());
    }
}
