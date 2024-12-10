package com.unisys.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ErrorHandlingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingAspect.class);

    @AfterReturning(pointcut = "execution(* com.unisys.controller.*.*(..))", returning = "result")
    public void handleReturn(JoinPoint joinPoint, Object result) {
        // You can add extra logic here to handle successful responses if needed
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            if (responseEntity.getStatusCode().is5xxServerError()) {
                logger.error("Error occurred in method: {}. Response: {}", joinPoint.getSignature(), responseEntity.getBody());
            }
        }
    }
}
