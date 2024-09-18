package com.xyz.engineering.coupon.annotation;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 *  方法执行时间记录切面
 */
@Slf4j
@Aspect
@Component
public class TimeAspect {

    /**
     * 切入点
     */
    @Pointcut("@annotation(com.xyz.engineering.coupon.annotation.Time)")
    private void pointcut() {}


    /**
     * 环绕通知
     * @param joinPoint 连接点
     * @return
     * @throws Throwable
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取目标类的名称、方法名称
        String clazzName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();

        Stopwatch sw = Stopwatch.createStarted();
        // 调用目标方法
        Object result = joinPoint.proceed();
        // 记录日志
        log.info("{}#{} elapsed: {}ms", clazzName, methodName, sw.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }
}
