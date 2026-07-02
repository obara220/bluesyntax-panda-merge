package com.panda.merge.odds.constants;

import com.panda.merge.odds.validate.BallHeadValidator;
import com.panda.merge.odds.validate.MaxBallHeadValidator;
import com.panda.merge.odds.validate.RangeBallHeadValidator;

import java.util.HashMap;
import java.util.Map;

/**
 * ValidatorConstant
 *
 * @description:
 * @date: 7/12/2025
 **/
public final class ValidatorConstant {

    public static final Map<Long, BallHeadValidator> BALLHEAD_VALIDATOR_MAP_HT = new HashMap<>();

    public static final Map<Long, BallHeadValidator> BALLHEAD_VALIDATOR_MAP_FT = new HashMap<>();

    public static final Map<Long, BallHeadValidator> BALLHEAD_VALIDATOR_MAP_OVERTIME_HT = new HashMap<>();

    public static final Map<Long, BallHeadValidator> BALLHEAD_VALIDATOR_MAP_OVERTIME_FT = new HashMap<>();

    static  {
        BALLHEAD_VALIDATOR_MAP_HT.put(18L, MaxBallHeadValidator.MAX_125);
        BALLHEAD_VALIDATOR_MAP_HT.put(122L, MaxBallHeadValidator.MAX_25);
        BALLHEAD_VALIDATOR_MAP_HT.put(309L, MaxBallHeadValidator.MAX_1);

        BALLHEAD_VALIDATOR_MAP_HT.put(19L, RangeBallHeadValidator.RANGE_075);
        BALLHEAD_VALIDATOR_MAP_HT.put(121L, RangeBallHeadValidator.RANGE_15);
        BALLHEAD_VALIDATOR_MAP_HT.put(308L, RangeBallHeadValidator.RANGE_05);

        BALLHEAD_VALIDATOR_MAP_FT.put(2L, MaxBallHeadValidator.MAX_125);
        BALLHEAD_VALIDATOR_MAP_FT.put(114L, MaxBallHeadValidator.MAX_25);
        BALLHEAD_VALIDATOR_MAP_FT.put(307L, MaxBallHeadValidator.MAX_1);

        BALLHEAD_VALIDATOR_MAP_FT.put(4L, RangeBallHeadValidator.RANGE_075);
        BALLHEAD_VALIDATOR_MAP_FT.put(113L, RangeBallHeadValidator.RANGE_15);
        BALLHEAD_VALIDATOR_MAP_FT.put(306L, RangeBallHeadValidator.RANGE_05);



        BALLHEAD_VALIDATOR_MAP_OVERTIME_HT.put(332L, MaxBallHeadValidator.MAX_1);
        BALLHEAD_VALIDATOR_MAP_OVERTIME_HT.put(1100417L, MaxBallHeadValidator.MAX_2);
        BALLHEAD_VALIDATOR_MAP_OVERTIME_HT.put(1100410L, MaxBallHeadValidator.MAX_1);

        BALLHEAD_VALIDATOR_MAP_OVERTIME_HT.put(130L, RangeBallHeadValidator.RANGE_05);
        BALLHEAD_VALIDATOR_MAP_OVERTIME_HT.put(1100416L, RangeBallHeadValidator.RANGE_1);
        BALLHEAD_VALIDATOR_MAP_OVERTIME_HT.put(1100409L, RangeBallHeadValidator.RANGE_05);

        BALLHEAD_VALIDATOR_MAP_OVERTIME_FT.put(127L, MaxBallHeadValidator.MAX_1);
        BALLHEAD_VALIDATOR_MAP_OVERTIME_FT.put(331L, MaxBallHeadValidator.MAX_2);
        BALLHEAD_VALIDATOR_MAP_OVERTIME_FT.put(1100407L, MaxBallHeadValidator.MAX_1);

        BALLHEAD_VALIDATOR_MAP_OVERTIME_FT.put(128L, RangeBallHeadValidator.RANGE_05);
        BALLHEAD_VALIDATOR_MAP_OVERTIME_FT.put(1100414L, RangeBallHeadValidator.RANGE_1);
        BALLHEAD_VALIDATOR_MAP_OVERTIME_FT.put(1100406L, RangeBallHeadValidator.RANGE_05);
    }

}
