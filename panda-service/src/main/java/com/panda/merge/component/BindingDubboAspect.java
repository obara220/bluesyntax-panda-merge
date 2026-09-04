package com.panda.merge.component;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * <Description> dubbo接口统一异常处理类<br>
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/27 <br>
 * @see com.panda.merge.component <br>
 */
@Slf4j
@Aspect
@Component
@Order(2)
public class BindingDubboAspect {

    @Pointcut("@annotation(com.panda.merge.exception.ExceptionDubboHelper) || execution(public * com.panda.merge.dubbo.*.*(..))")
    public void BindingResult() {
    }

    @Around("BindingResult()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //类名
        String className = joinPoint.getTarget().getClass().getSimpleName();
        //方法名
        String methodName = joinPoint.getSignature().getName();
        //入参
        Object[] reqPars = joinPoint.getArgs();
        //log.info("类名：{}，方法名：{}，入参：{}",className,methodName,JSON.toJSONString(reqPars));
        Object result;
        try {
            result = joinPoint.proceed();
        }catch (Throwable e) {
            logOutput(className,methodName,reqPars,e);
            return Response.failed(String.format("第三方数据业务处理失败,请联系开发人员! className: %s methodName: %s",
                                                 className,
                                                 methodName));
        }
        return result;
    }

    /**
     * 日志输出
     * @param  className    类名
     * @param  methodName   方法名
     * @param  reqPars      入参
     * @param  e            异常对象
     * */
    private void logOutput(String className,String methodName,Object[] reqPars,Throwable e){
        for (Object arg : reqPars) {
            if(arg instanceof Request){
                Request request = (Request) arg;
                log.error("业务处理异常1【dubbo ："+ className+" : "+methodName+"】【::"+request.getLinkId()+"::】,data="+JSON.toJSONString(reqPars)+" ,Exception:",e);
                continue;
            }
            log.error("业务处理异常2【dubbo ："+ className+" : "+methodName+"】,data="+JSON.toJSONString(reqPars)+" Exception:",e);
        }
    }
}
