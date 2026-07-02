package com.panda.merge.validator;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Set;

/**
 * ValidatorUtils
 *
 * @description: 校验工具类
 * @date: 3/8/2025
 **/

public final class ValidatorUtils {

    public static <T> void validate(Validator validator, T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations.toString());
        }
    }

}
