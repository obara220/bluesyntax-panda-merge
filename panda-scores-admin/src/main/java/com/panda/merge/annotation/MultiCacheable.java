package com.panda.merge.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiCacheable {

    String redisName() default "";

    int cacheSeconds() default 9000;

}
