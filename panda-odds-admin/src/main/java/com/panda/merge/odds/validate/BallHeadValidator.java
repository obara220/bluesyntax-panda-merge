package com.panda.merge.odds.validate;

import java.math.BigDecimal;

/**
 * BallHeadValidator
 *
 * @description:
 * @date: 5/17/2025
 **/
public interface BallHeadValidator {


    boolean validate(BigDecimal ballHead,Integer scoreSum);

}
