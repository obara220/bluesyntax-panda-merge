package com.panda.merge.odds.validate;

import java.math.BigDecimal;

/**
 * RangeBallHeadValidator
 *
 * @description:
 * @date: 5/17/2025
 **/
public class RangeBallHeadValidator implements BallHeadValidator {

    public static RangeBallHeadValidator RANGE_05 =
            new RangeBallHeadValidator(new BigDecimal("-0.5"), new BigDecimal("0.5"));

    public static RangeBallHeadValidator RANGE_075 =
            new RangeBallHeadValidator(new BigDecimal("-0.75"), new BigDecimal("0.75"));

    public static RangeBallHeadValidator RANGE_1 =
            new RangeBallHeadValidator(new BigDecimal("-1"), new BigDecimal("1"));

    public static RangeBallHeadValidator RANGE_125 =
            new RangeBallHeadValidator(new BigDecimal("-1.25"), new BigDecimal("1.25"));

    public static RangeBallHeadValidator RANGE_15 =
            new RangeBallHeadValidator(new BigDecimal("-1.5"), new BigDecimal("1.5"));

    public static RangeBallHeadValidator RANGE_2 =
            new RangeBallHeadValidator(new BigDecimal("-2"), new BigDecimal("2"));

    public static RangeBallHeadValidator RANGE_25 =
            new RangeBallHeadValidator(new BigDecimal("-2.5"), new BigDecimal("2.5"));

    private final BigDecimal minBallHead;

    private final BigDecimal maxBallHead;

    public RangeBallHeadValidator(BigDecimal minBallHead, BigDecimal maxBallHead) {
        this.minBallHead = minBallHead;
        this.maxBallHead = maxBallHead;
    }

    @Override
    public boolean validate(BigDecimal ballHead, Integer scoreSum) {
        if (ballHead == null) {
            return true;
        }
        return ballHead.compareTo(minBallHead) >= 0 && ballHead.compareTo(maxBallHead) <= 0;
    }

}
