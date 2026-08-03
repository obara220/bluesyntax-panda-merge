package com.panda.merge.component;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.panda.merge.constant.ConstantSystem.FIX;

/**
 * <Description> 非dubbo相关统一异常处理类<br>
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
public class BindingResultAspect {

    @Autowired
    public RedisService redisService;

    @Pointcut("@annotation(com.panda.merge.exception.ExceptionHelper) || execution(public * com.panda.merge.rocketmq.consumer.*.*(..))")
    public void BindingResult() {
    }

    @Around("BindingResult()")
    public Object doAround(ProceedingJoinPoint joinPoint){
        //类名
        String className = joinPoint.getTarget().getClass().getSimpleName();
        //方法名
        String methodName = joinPoint.getSignature().getName();
        //入参
        Object[] reqPars = joinPoint.getArgs();
        //log.info("类名：{}，方法名：{}，入参：{}",className,methodName,JSON.toJSONString(reqPars));
        Object result = null;
        try {
            result = joinPoint.proceed();
        }catch (DuplicateKeyException e){
            //抛出唯一主键冲突，便于重新推送数据
            logOutput(className,methodName,reqPars,e);
//            throw e;
        }catch (Throwable e) {
            //其他异常不重推数据
            logOutput(className,methodName,reqPars,e);
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
    private void logOutput(String className, String methodName, Object[] reqPars, Throwable e){
        for (Object arg : reqPars) {
            if(arg instanceof Request){
                Request request = (Request) arg;
                if(e instanceof DuplicateKeyException){
                    if(StringUtils.isNotBlank(request.getDataType())){
                        //如果是唯一主键冲突异常，解锁linkId，方便重新推送
                        String key = RedisConfig.REDIS_KEY_LINKID + request.getDataType() + FIX + request.getLinkId();
                        redisService.unLock(key,key);
                    }
                }
                String jsonStr = JSON.toJSONString(reqPars);
                //如果入参长度大于5000 则不打印入参
                if(jsonStr.length() > ConstantSystem.HUNDRED * ConstantSystem.TEN * ConstantSystem.FIVES){
                    jsonStr = "入参长度为"+jsonStr.length()+"，自行根据LinkId查询入参！";
                }
                if(StringUtils.isNotBlank(request.getDataSourceCode())){
                    e.printStackTrace();
                    log.error("业务处理异常1【"+ className+" : "+methodName+" : "+request.getDataType()+"】【"+request.getDataSourceCode()+" ::"+request.getLinkId()+"::】,data="+jsonStr+" ,Exception="+getExceptionMessage(e));
                }else{
                    e.printStackTrace();
                    log.error("业务处理异常2【"+ className+" : "+methodName+" : "+request.getDataType()+"】【::"+request.getLinkId()+"::】,data="+jsonStr+"  stack"+ ExceptionUtil.stacktraceToString(e)+"+,Exception:",e );
                }
                continue;
            }
            e.printStackTrace();
            log.error("业务处理异常3【"+ className+" : "+methodName+"】,data="+JSON.toJSONString(reqPars)+" Exception:",e);
        }
    }

    public static String getExceptionMessage(Throwable e)
    {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }
}
