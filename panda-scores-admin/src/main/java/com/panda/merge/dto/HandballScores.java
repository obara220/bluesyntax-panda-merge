package com.panda.merge.dto;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Slf4j
@Data
public class HandballScores extends  AbstractSportScores{



    @ScoresProperty(eventName = "进球",eventCode ={"goal","match_score"})
    private CommonItem goal ;







    private HandballScores() {
        }
    public HandballScores(Long periodId) {
            super.init(this);
    }


    public void updateScores(MatchEventInfo data, HandballScores wholeSores) {
        if(data.getEventCode().equals("goal")) {
            Integer addHome = data.getT1() - wholeSores.getGoal().getHome();
            Integer addAway = data.getT2() - wholeSores.getGoal().getAway();

            log.info(" thirdMatchId:{} ,eventCode:{},before: home:{},away:{}", data.getThirdMatchId(), "matchScore", wholeSores.getGoal().getHome(), wholeSores.getGoal().getAway());
            wholeSores.getGoal().setHome(data.getT1());
            wholeSores.getGoal().setAway(data.getT2());
            this.getGoal().setHome(getGoal().getHome() + addHome);
            this.getGoal().setAway(getGoal().getAway() + addAway);

            log.info(" thirdMatchId:{} ,eventCode:{},after: home:{},away:{}", data.getThirdMatchId(), "matchScore", wholeSores.getGoal().getHome(), wholeSores.getGoal().getAway());
        }
    }
}
