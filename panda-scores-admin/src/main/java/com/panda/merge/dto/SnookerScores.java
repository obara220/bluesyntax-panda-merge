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
public class SnookerScores extends  AbstractSportScores{

    @ScoresProperty(eventName = "盘比分",eventCode ={"snooker_score_change","match_score","set_scores"})
    private CommonItem matchScore ;

    @ScoresProperty(eventName = "局比分")
    private CommonItem setScore ;

    @ScoresProperty(eventName = "犯规次数",eventCode ={"snooker_foul"})
    private CommonItem snookerFoul ;

    @ScoresProperty(eventName = "单杆最高")
    private CommonItem  highestSingleShot ;
    public SnookerScores( ) {
        super.init(this);
    }

    public void doCalculation(MatchEventInfo data, SnookerScores wholeSores) {
        if(data.getT1()>=matchScore.getHome()||data.getT2()>=matchScore.getAway()){
//            matchScore.setHome(data.getT1());
//            matchScore.setAway(data.getT2());
            setScore.setHome(data.getFirstT1());
            setScore.setAway(data.getFirstT2());
            wholeSores.getMatchScore().setHome(data.getT1());
            wholeSores.getMatchScore().setAway(data.getT2());
        }else if(data.getFirstT1()>setScore.getHome()||data.getFirstT2()>setScore.getAway()){
            setScore.setHome(data.getFirstT1());
            setScore.setAway(data.getFirstT2());
        }
        if(data.getEventCode().equals("snooker_score_change")) {
            log.info(" thirdMatchId:{} ,eventCode:{},before: home:{},away:{}",data.getThirdMatchId(),"matchScore",matchScore.getHome(),matchScore.getAway());

//            matchScore.setHome(data.getT1());
//            matchScore.setAway(data.getT2());

            log.info(" thirdMatchId:{} ,eventCode:{},after: home:{},away:{}",data.getThirdMatchId(),"matchScore",matchScore.getHome(),matchScore.getAway());
        }else if(data.getEventCode().equals("snooker_foul")){
            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                snookerFoul.setHome(snookerFoul.getHome()+1);
                wholeSores.snookerFoul.setHome(wholeSores.snookerFoul.getHome()+1);
            }else {
                snookerFoul.setAway(snookerFoul.getAway()+1);
                wholeSores.snookerFoul.setAway(wholeSores.snookerFoul.getAway()+1);
            }
            if(data.getFirstT1()!=null&&data.getFirstT2()!=null&&( data.getFirstT1()>setScore.getHome()||data.getFirstT2()>setScore.getAway())){
                setScore.setHome(data.getFirstT1());
                setScore.setAway(data.getFirstT2());
            }
        }
    }

    public void cancelCalculation(MatchEventInfo data, MatchEventInfo oldMatchInfo, Map<Long, SnookerScores> allPeriodScores) {
        SnookerScores wholeSores = allPeriodScores.get(WHOLE_MATCH );
        SnookerScores oldSores = allPeriodScores.get(data.getFirstNum()+0l);
        if(oldMatchInfo.getEventCode().equals("snooker_score_change")) {
            //1.计算差值
            Integer deleteST1 = oldSores.setScore.getHome() - data.getFirstT1();
            Integer deleteST2 = oldSores.setScore.getAway() - data.getFirstT2();
            Integer deleteT1 = oldSores.matchScore.getHome() - data.getT1();
            Integer deleteT2 = oldSores.matchScore.getAway() - data.getT2();

            oldSores.matchScore.setHome(data.getT1());
            oldSores.matchScore.setAway(data.getT2());
            oldSores.setScore.setHome(data.getFirstT1());
            oldSores.setScore.setAway(data.getFirstT2());

            wholeSores.matchScore.setHome(data.getT1());
            wholeSores.matchScore.setAway(data.getT2());
            wholeSores.setScore.setHome(wholeSores.setScore.getHome() - deleteST1);
            wholeSores.setScore.setAway(wholeSores.setScore.getAway() - deleteST2);
            //差最高杆
        }else if(data.getEventCode().equals("snooker_foul")){
            Integer deleteST1 = oldSores.snookerFoul.getHome() - data.getFirstT1();
            Integer deleteST2 = oldSores.snookerFoul.getAway() - data.getFirstT2();
            oldSores.snookerFoul.setHome(data.getFirstT1());
            oldSores.snookerFoul.setAway(data.getFirstT2());
            wholeSores.snookerFoul.setHome(wholeSores.snookerFoul.getHome() - deleteST1);
            wholeSores.snookerFoul.setAway(wholeSores.snookerFoul.getAway() - deleteST2);
            //差最高杆
        }
    }

    public void updateHighestSingleShot(Integer high, String homeAway) {
        if(homeAway.equals(TeamTypeConstant.HOME)){
            if(highestSingleShot.getHome()<high)
            highestSingleShot.setHome(high);
        }
        if(homeAway.equals(TeamTypeConstant.AWAY)){
            if(highestSingleShot.getAway()<high)
            highestSingleShot.setAway(high);
        }
    }

}
