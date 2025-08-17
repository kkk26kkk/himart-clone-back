package com.himartclone.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
public class Aspects {

    @Aspect
    @Order(2)
    @Component
    public static class executionTimeAspect {
        @Around("com.himartclone.common.aop.Pointcuts.goodsAndController()")
        public Object executionTime(ProceedingJoinPoint joinPoint) throws Throwable {
            long startTime = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.info("[executionTime] : {}ms", (endTime - startTime));
            return result;
        }
    }

    @Aspect
    @Order(1)
    @Component
    public static class methodCallAspect {
        @Around("com.himartclone.common.aop.Pointcuts.allGoods()")
        public Object methodCall(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[methodCall] : {}", joinPoint.getSignature().getName());
            return joinPoint.proceed();
        }
    }
}
