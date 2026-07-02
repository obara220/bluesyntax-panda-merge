package com.panda.merge.dto;

import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class BeachVolleyballScores extends  AbstractSportScores{



    @ScoresProperty(eventName = "得分",eventCode ={"score_change"})
    private CommonItem matchScore ;
    @ScoresProperty(eventName = "小节得分",eventCode ={"score_change"})
    private CommonItem setScore;





    private BeachVolleyballScores() {
        }
    public BeachVolleyballScores(Long periodId) {
            super.init(this);
    }


    public void updateScores(MatchEventInfo data, BeachVolleyballScores wholeSores) {
        if(data.getEventCode().equals("score_change")) {
            Integer addHome = data.getT1() - wholeSores.matchScore.getHome();
            Integer addAway = data.getT2() - wholeSores.matchScore.getAway();

            log.info(" thirdMatchId:{} ,eventCode:{},before: home:{},away:{}", data.getThirdMatchId(), "matchScore", wholeSores.matchScore.getHome(), wholeSores.matchScore.getAway());
            wholeSores.matchScore.setHome(data.getT1());
            wholeSores.matchScore.setAway(data.getT2());
            this.matchScore.setHome(matchScore.getHome() + addHome);
            this.matchScore.setAway(matchScore.getAway() + addAway);

            wholeSores.setScore.setHome(data.getFirstT1());
            wholeSores.setScore.setAway(data.getFirstT2());

            this.setScore.setHome(data.getFirstT1());
            this.setScore.setAway(data.getFirstT2());

            log.info(" thirdMatchId:{} ,eventCode:{},after: home:{},away:{}", data.getThirdMatchId(), "matchScore", wholeSores.matchScore.getHome(), wholeSores.matchScore.getAway());
        }
    }
}
