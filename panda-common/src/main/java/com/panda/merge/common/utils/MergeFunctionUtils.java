package com.panda.merge.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @author kepa
 */
@Slf4j
@Component
public class MergeFunctionUtils {

    /**
     * 设置获胜并列数
     * @param targetObject
     * @param numberOfWinners
     * @param <T>
     */
    public static <T> void setNumberOfWinners(T targetObject, Integer numberOfWinners) {
        if ( null == numberOfWinners || numberOfWinners == 0 ) {
            numberOfWinners = 1;
        }
        try {
            Field field = targetObject.getClass().getDeclaredField("numberOfWinners");
            field.setAccessible(true);
            field.set(targetObject, numberOfWinners);
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            log.info("setNumberOfWinners-exception:", exception);
        }
    }
}
