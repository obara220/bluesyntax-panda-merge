package com.panda.merge.dto;

import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.constant.TeamTypeConstant;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Data
@Slf4j
public class UKFootballScores extends  AbstractSportScores{

    @ScoresProperty(eventName = "比分",eventCode ={"score_change","match_score","set_scores"})
    private CommonItem matchScore ;


    public UKFootballScores() {
        super.init(this);
    }

    public void updateScores(MatchEventInfo data, Map<Long, UKFootballScores> allPeriodScores){
        if(data.getEventCode().equals("try")||data.getEventCode().equals("penalty_try")||data.getEventCode().equals("conversion")||data.getEventCode().equals("penalty_points")
                ||data.getEventCode().equals("drop_goal")|| data.getEventCode().equals("penalty_comp_goal")){
            UKFootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
            Integer addHome = data.getT1() - wholeSores.matchScore.getHome();
            Integer addAway = data.getT2() - wholeSores.matchScore.getAway();

            wholeSores.matchScore.setHome(data.getT1());
            wholeSores.matchScore.setAway(data.getT2());
            this.matchScore.setHome(matchScore.getHome() + addHome);
            this.matchScore.setAway(matchScore.getAway() + addAway);

        }

    }
}
