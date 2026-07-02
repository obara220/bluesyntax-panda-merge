package com.panda.merge.dto;

import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;

import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Data
public class WaterballScores extends  AbstractSportScores{

    @ScoresProperty(eventName = "比分",eventCode ={"score_change","match_score","set_scores"})
    private CommonItem matchScore ;

    public WaterballScores() {
        super.init(this);
    }


    public void updateScores(MatchEventInfo data, Map<Long, WaterballScores> allPeriodScores){
        if(data.getEventCode().equals("waterpolo_score_change")){
            WaterballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
            Integer addHome = data.getT1() - wholeSores.matchScore.getHome();
            Integer addAway = data.getT2() - wholeSores.matchScore.getAway();

            wholeSores.matchScore.setHome(data.getT1());
            wholeSores.matchScore.setAway(data.getT2());
            this.matchScore.setHome(matchScore.getHome() + addHome);
            this.matchScore.setAway(matchScore.getAway() + addAway);

        }

    }
}
