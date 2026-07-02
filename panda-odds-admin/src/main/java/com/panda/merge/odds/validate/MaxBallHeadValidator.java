package com.panda.merge.odds.validate;

import java.math.BigDecimal;

/**
 * MaxBallHeadValidator
 *
 * @description:
 * @date: 5/17/2025
 **/
public class MaxBallHeadValidator implements BallHeadValidator {

    public static MaxBallHeadValidator MAX_125 = new MaxBallHeadValidator(new BigDecimal("1.25"));

    public static MaxBallHeadValidator MAX_1 = new MaxBallHeadValidator(new BigDecimal("1"));

    public static MaxBallHeadValidator MAX_2 = new MaxBallHeadValidator(new BigDecimal("2"));

    public static MaxBallHeadValidator MAX_25 = new MaxBallHeadValidator(new BigDecimal("2.5"));

    private final BigDecimal maxDiff;

    public MaxBallHeadValidator(BigDecimal maxDiff) {this.maxDiff = maxDiff;}

    @Override
    public boolean validate(BigDecimal ballHead,Integer scoreSum) {
        if (ballHead == null || scoreSum == null) {
            return true;
        }

        return ballHead.compareTo(maxDiff.add(BigDecimal.valueOf(scoreSum))) <= 0;
    }

}
