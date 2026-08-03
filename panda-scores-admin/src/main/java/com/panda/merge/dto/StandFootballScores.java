package com.panda.merge.dto;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.common.enums.EventCodeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Slf4j
@Data
public class StandFootballScores extends  AbstractSportScores{

    @ScoresProperty(eventName = "角球",eventCode ={"corner","corner_score"})
    private CommonItem corner ;

    @ScoresProperty(eventName = "红牌",eventCode ={"red_card","yellow_red_card","red_card_score"})
    private CommonItem redCard ;

    @ScoresProperty(eventName = "黄牌",eventCode ={"yellow_card","yellow_card_score"})
    private CommonItem yellowCard ;

    @ScoresProperty(eventName = "进球",eventCode ={"goal","match_score"})
    private CommonItem goal ;

    public StandFootballScores(Long periodId) {
        super.init(this);
    }
}
