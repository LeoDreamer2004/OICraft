package org.dindier.oicraft.service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PerformanceAspect {

    @Around("execution(* org.dindier.oicraft.service.ProblemService.testCode(..)) ")
    public Object logTestTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        log.info("Created code testing thread in {}ms", executionTime);
        return proceed;
    }

    @Around("execution(* org.dindier.oicraft.service.IDEService.runCode(..))")
    public Object logIDETime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        log.info("Running code in IDE finished in {}ms", executionTime);
        return proceed;
    }
}
