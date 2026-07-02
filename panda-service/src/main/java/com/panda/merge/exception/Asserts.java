package com.panda.merge.exception;

import com.panda.merge.dto.ResultCode;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 断言处理类，用于抛出各种API异常
 * Created by macro on 2020/2/27.
 */
public class Asserts {
    public static void fail(String message) {
        throw new ApiException(message);
    }

    public static void fail(ResultCode errorCode) {
        throw new ApiException(errorCode);
    }

    /**
     * 字符串非空校验
     * @param str
     * @param message
     */
    public static void validateStringForEmpty(String str,String message) {
        if(StringUtils.isBlank(str)) {
            Asserts.fail(message);
        }
    }

    /**
     * 对象非空校验
     * @param obj
     * @param message
     */
    public static void validateObjectForEmpty(Object obj,String message) {
        if(Objects.isNull(obj)) {
            Asserts.fail(message);
        }
    }

    /**
     * 枚举值校验
     * @param num
     * @param message
     */
    public static void validateEnumForEmpty(Integer num,String message,List<Integer> vals) {
        if(Objects.isNull(num) || !vals.contains(num)) {
            Asserts.fail(message);
        }
    }

    /**
     * 枚举值校验
     * @param num
     * @param message
     */
    public static void validateEnumForEmpty(String num,String message,List<String> vals) {
        if(Objects.isNull(num) || !vals.contains(num)) {
            Asserts.fail(message);
        }
    }

    /**
     * 集合对象校验
     * @param list
     * @param message
     */
    public static void validateListForEmpty(Collection list, String message) {
        if(CollectionUtils.isEmpty(list)) {
            Asserts.fail(message);
        }
    }

    /**
     * 字符串长度校验
     * @param str
     * @param max
     * @param message
     */
    public static void validateStrLenForEmpty(String str,int max,String message) {
        if (StringUtils.isNotBlank(str) && str.length() > max) {
            Asserts.fail(message);
        }
    }

}
