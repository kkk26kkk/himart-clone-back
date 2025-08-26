package com.himartclone.common.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    @Pointcut("execution(* com.himartclone.goods..*(..))")
    public void allGoods() {}

    @Pointcut("execution(* *..*Controller.*(..))")
    public void allController() {}

    @Pointcut("allGoods() && allController()")
    public void goodsAndController() {}

    @Pointcut("@annotation(ExecutionTimeLog)")
    public void ExecutionTimeLog() {}
}
