package com.panda.merge.dto;

import com.panda.merge.cache.CommonItemBigDecimal;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Slf4j
@Data
public class CricketBallScores {

    private CommonItem delivery ;

    private Map<Integer,CommonItem> over = new HashMap<>();

    private CommonItem point;

    private CommonItem wicet;

    private CommonItem toWinTheToss;

    private CommonItem matchScore;

//    private CommonItemBigDecimal pushOver;

}
