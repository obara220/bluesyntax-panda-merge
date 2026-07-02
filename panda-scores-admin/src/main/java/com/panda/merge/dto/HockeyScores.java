package com.panda.merge.dto;

import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;

import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Data
public class HockeyScores  extends  AbstractSportScores{
    @ScoresProperty(eventName = "全场比分",eventCode ={"goal","match_score"})
    private CommonItem matchScore ;

    public HockeyScores( ) {
        super.init(this);
    }

    public void updateScores(MatchEventInfo data, Map<Long, HockeyScores> allPeriodScores){
        if(data.getEventCode().equals("goal")){
            HockeyScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
            Integer addHome = data.getT1() - wholeSores.matchScore.getHome();
            Integer addAway = data.getT2() - wholeSores.matchScore.getAway();

            wholeSores.matchScore.setHome(data.getT1());
            wholeSores.matchScore.setAway(data.getT2());
            this.matchScore.setHome(matchScore.getHome() + addHome);
            this.matchScore.setAway(matchScore.getAway() + addAway);

        }

    }


}
