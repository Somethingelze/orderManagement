package com.some.commonlib.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(com.some.commonlib.annotations.Loggable) || @within(com.some.commonlib.annotations.Loggable)")
    public Object logEverything(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info(">>> Entering [{}]. Arguments: {}", methodName, Arrays.toString(args));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("!!! Exception in [{}]: {}", methodName, e.getMessage());
            throw e;
        }

        log.info("<<< Exiting [{}]. Result: {}", methodName, result);

        return result;
    }
}