package com.panda.merge.dto;

import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Data
@Slf4j
public class TableTennisScores extends  AbstractSportScores{

    @ScoresProperty(eventName = "盘比分",eventCode ={"table_tennis_score_change","match_score","set_scores"})
    private CommonItem matchScore ;

    @ScoresProperty(eventName = "局比分")
    private CommonItem setScore ;

    @ScoresProperty(eventName = "红牌",eventCode ={"red_card"})
    private CommonItem redCard ;

    @ScoresProperty(eventName = "黄牌",eventCode ={"yellow_card"})
    private CommonItem yellowCard ;

    public TableTennisScores() {
        super.init(this);
    }

    public void doCalculation(MatchEventInfo data, Map<Long, TableTennisScores> allPeriodScores) {
        TableTennisScores wholeSores = allPeriodScores.get(WHOLE_MATCH);
        if(data.getEventCode().equals("table_tennis_score_change")) {
            Integer addHome = data.getT1() - wholeSores.getMatchScore().getHome();
            Integer addAway = data.getT2() - wholeSores.getMatchScore().getAway();
//            if(data.getFirstT1()<wholeSores.getSetScore().getHome()&&data.getFirstT2()<wholeSores.getSetScore().getAway()&&
//                data.getT1()<wholeSores.getMatchScore().getHome()&&data.getT2()<wholeSores.getMatchScore().getAway()){
//                log.info(" {} ,消费顺序有问题", data.getLinkId());
//                return;
//            }
            if(data.getFirstT1()==null || data.getFirstT2()==null){
                log.info(" {} ,数据异常，比分事件无阶段比分。", data.getLinkId());
                return;
            }
            if(data.getFirstT1()<setScore.getHome() || data.getFirstT2()<setScore.getAway()){
                log.info(" {} ,消费顺序有问题，已存在数据：{}，事件数据：{}:{}", data.getLinkId(),setScore.doCountScoreStr(),data.getFirstT1(),data.getFirstT2());
                return;
            }
            log.info("{}, thirdMatchId:{} ,eventCode:{},before: home:{},away:{}",data.getLinkId(), data.getThirdMatchId(), "matchScore", matchScore.getHome(), matchScore.getAway());
            wholeSores.matchScore.setHome(data.getT1());
            wholeSores.matchScore.setAway(data.getT2());
            this.matchScore.setHome(matchScore.getHome() + addHome);
            this.matchScore.setAway(matchScore.getAway() + addAway);

            wholeSores.setScore.setHome(data.getFirstT1());
            wholeSores.setScore.setAway(data.getFirstT2());
            setScore.setHome(data.getFirstT1());
            setScore.setAway(data.getFirstT2());
            log.info("{},  thirdMatchId:{} ,eventCode:{},after: home:{},away:{}",data.getLinkId(), data.getThirdMatchId(), "matchScore", matchScore.getHome(), matchScore.getAway());
        }
        if(data.getEventCode().equals("yellow_card")) {
            this.yellowCard.setHome(data.getT1());
            this.yellowCard.setAway(data.getT2());
            wholeSores.yellowCard.setHome(data.getT1());
            wholeSores.yellowCard.setAway(data.getT2());
        }
        if(data.getEventCode().equals("red_card")) {
            this.redCard.setHome(data.getT1());
            this.redCard.setAway(data.getT2());
            wholeSores.redCard.setHome(data.getT1());
            wholeSores.redCard.setAway(data.getT2());
        }

        wholeSores.setScore.setHome(0);
        wholeSores.setScore.setAway(0);
        for (Map.Entry<Long, TableTennisScores> longTableTennisScoresEntry : allPeriodScores.entrySet()) {
            if(longTableTennisScoresEntry.getKey().equals(WHOLE_MATCH)){
                continue;
            }
            wholeSores.setScore.setHome( wholeSores.setScore.getHome()+longTableTennisScoresEntry.getValue().getSetScore().getHome());
            wholeSores.setScore.setAway( wholeSores.setScore.getAway()+longTableTennisScoresEntry.getValue().getSetScore().getAway());
        }
    }

    public void cancelCalculation(MatchEventInfo data, MatchEventInfo oldMatchInfo, Map<Long, TableTennisScores> allPeriodScores) {
        TableTennisScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        Integer addHome = data.getT1()-wholeSores.getMatchScore().getHome();
        Integer addAway = data.getT2()-wholeSores.getMatchScore().getAway();
        wholeSores.matchScore.setHome(data.getT1());
        wholeSores.matchScore.setAway(data.getT2());
        this.matchScore.setHome(matchScore.getHome()+addHome);
        this.matchScore.setAway(matchScore.getAway()+addAway);

        wholeSores.setScore.setHome(data.getFirstT1());
        wholeSores.setScore.setAway(data.getFirstT2());
        setScore.setHome(data.getFirstT1());
        setScore.setAway(data.getFirstT2());
    }
}
