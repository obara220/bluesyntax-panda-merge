package com.panda.merge.aspect;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
//@Component
public class ConsumerSwitchAspect {

    @Pointcut("@annotation(com.panda.merge.annotation.ConsumerSwitch)")
    public void pointCut() {}

    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;

    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            ConsumerSwitch consumerSwitch = method.getDeclaredAnnotation(ConsumerSwitch.class);
            if (("realtime".equals(consumerSwitch.value()) || "nonrealtime".equals(consumerSwitch.value())) && !realtimeSwitch ) {
                log.info( signature.getDeclaringTypeName()+"."+signature.getName()+" 被拦截(消费开关关闭)");
                return null;
            }
        }
        return joinPoint.proceed();
    }
}
