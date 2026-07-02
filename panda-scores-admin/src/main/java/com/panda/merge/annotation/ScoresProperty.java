package com.panda.merge.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ScoresProperty {

    String eventName() default "";

    String[] eventCode() default {};

    String extrainInfo() default "";

    boolean isDefaultCreate() default true;

}
