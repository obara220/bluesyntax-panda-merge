//package com.panda.merge.annotation;
//
//import com.panda.merge.config.RedisHelper;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Method;
//
//@Aspect
//@Component
//public class CacheAspect {
//    /**
//     * 注解Pointcut切入点
//     */
//   @Pointcut("@annotation(com.panda.merge.annotation.MultiCacheable)")
//    public void cacheAspect() {
//    }
//
//    //进行自定义配置 注册 redisTemplete
//    //将redisTemplete 加入到config类的map 中 可通过名称获取 redisTemplete
//    @Autowired
//    RedisHelper redisHelper;
//
//
//    /**
//     * 环绕通知
//     * */
//    @Around("cacheAspect()" )
//    public  void doCacheable(JoinPoint joinPoint ) throws Throwable {
//        MethodSignature methodSignature= (MethodSignature) joinPoint.getSignature();
//        Method  method =methodSignature.getMethod();
//        MultiCacheable multiCacheable =method.getAnnotation(MultiCacheable.class);
//        String redisTempleteName =multiCacheable.redisName();
//        Integer cacheSeconds= multiCacheable.cacheSeconds();
//        String[] argNames= methodSignature.getParameterNames();
//        Object[] args = joinPoint.getArgs();
//        //根据 argNames 传参进行缓存key生成
//        //将args 变 json 进行 返回
//        //1.根据redisName 获得 templete
//        RedisTemplate redisTemplate =redisHelper.getRedisTemplateByName(redisTempleteName);
//        //2.根据key 得到缓存
//        //3.如果缓存不存在则执行方法 得到查询结果
//        //4.如果缓存不存在 将查询结果进行缓存
//        //5.返回查询结果
//    }
//}
