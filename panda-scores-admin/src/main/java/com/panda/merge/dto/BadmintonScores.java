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
public class BadmintonScores extends  AbstractSportScores{

    @ScoresProperty(eventName = "盘比分",eventCode ={"badminton_score_change","match_score","set_scores"})
    private CommonItem matchScore ;

    @ScoresProperty(eventName = "局比分")
    private CommonItem setScore ;

    @ScoresProperty(eventName = "得分次数")
    private CommonItem pointsCount;

    @ScoresProperty(eventName = "接收点得分次数")
    private CommonItem receivedPointsCount;

    @ScoresProperty(eventName = "发球得分次数")
    private CommonItem servePointsCount;

    @ScoresProperty(eventName = "接收点得分率")
    private CommonFItem receivedPointsRate;

    @ScoresProperty(eventName = "发球得分率")
    private CommonFItem servePointsCountRate;

    public BadmintonScores( ) {
        super.init(this);
    }

    public void doCalculation(MatchEventInfo data) {
        if(!data.getEventCode().equals("badminton_score_change")){
            return;
        }
        if(matchScore.getHome()<data.getT1()||matchScore.getAway()<data.getT2()){
            log.info(" thirdMatchId:{} ,eventCode:{},before: home:{},away:{}",data.getThirdMatchId(),"matchScore",matchScore.getHome(),matchScore.getAway());
            matchScore.setHome(data.getT1());
            matchScore.setAway(data.getT2());
            log.info(" thirdMatchId:{} ,eventCode:{},after: home:{},away:{}",data.getThirdMatchId(),"matchScore",matchScore.getHome(),matchScore.getAway());
        }
//        if(setScore.getHome()<data.getFirstT1()||setScore.getAway()<data.getFirstT2()){
            setScore.setHome(data.getFirstT1());
            setScore.setAway(data.getFirstT2());
//        }
        if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
            pointsCount.setHome(pointsCount.getHome()+1);
        }else {
            pointsCount.setAway(pointsCount.getAway()+1);
        }
        if(data.getExtraInfo()!=null&&data.getExtraInfo().equals("0")){
            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                receivedPointsCount.setHome(receivedPointsCount.getHome()+1);
            }else {
                receivedPointsCount.setAway(receivedPointsCount.getAway()+1);
            }
        }
        if(data.getHomeAway().equals(data.getAddition3())){
            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                servePointsCount.setHome(servePointsCount.getHome()+1);
            }else {
                servePointsCount.setAway(servePointsCount.getAway()+1);
            }
        }
        Integer homePoints =receivedPointsCount.getHome()+servePointsCount.getHome();
        Integer awayPoints =receivedPointsCount.getAway()+servePointsCount.getAway();
        if(homePoints!=0){
            receivedPointsRate.setHome(receivedPointsCount.getHome()*1000/homePoints/10f);
            servePointsCountRate.setHome(servePointsCount.getHome()*1000/homePoints/10f);
        }
        if(awayPoints!=0){
            receivedPointsRate.setAway(receivedPointsCount.getAway()*1000/awayPoints/10f);
            servePointsCountRate.setAway(servePointsCount.getAway()*1000/awayPoints/10f);
        }
    }

    public void cancelCalculation(MatchEventInfo data, MatchEventInfo oldData, Map<Long, BadmintonScores> allPeriodScores) {
        BadmintonScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        BadmintonScores oldSores= allPeriodScores.get(oldData.getMatchPeriodId());
        //1.计算差值
        Integer deleteST1=oldSores.setScore.getHome()-data.getFirstT1();
        Integer deleteST2=oldSores.setScore.getAway()-data.getFirstT2();
        Integer deleteT1=oldSores.matchScore.getHome()-data.getT1();
        Integer deleteT2=oldSores.matchScore.getAway()-data.getT2();

        oldSores.matchScore.setHome(data.getT1());
        oldSores.matchScore.setAway(data.getT2());
        oldSores.setScore.setHome(data.getFirstT1());
        oldSores.setScore.setAway(data.getFirstT2());

        wholeSores.matchScore.setHome(wholeSores.matchScore.getHome()-deleteT1);
        wholeSores.matchScore.setAway(wholeSores.matchScore.getAway()-deleteT2);
        wholeSores.setScore.setHome(wholeSores.setScore.getHome()-deleteST1);
        wholeSores.setScore.setAway(wholeSores.setScore.getAway()-deleteST2);
        //2.计算取消的指标
        if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
            oldSores.pointsCount.setHome(pointsCount.getHome()+1);
            wholeSores.pointsCount.setHome(pointsCount.getHome()+1);
        }else {
            oldSores.pointsCount.setAway(pointsCount.getAway()+1);
            wholeSores.pointsCount.setAway(pointsCount.getAway()+1);
        }
        if(data.getExtraInfo()!=null&&data.getExtraInfo().equals("0")){
            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                oldSores.receivedPointsCount.setHome(receivedPointsCount.getHome()+1);
                wholeSores.receivedPointsCount.setHome(receivedPointsCount.getHome()+1);
            }else {
                oldSores.receivedPointsCount.setAway(receivedPointsCount.getAway()+1);
                wholeSores.receivedPointsCount.setAway(receivedPointsCount.getAway()+1);
            }
        }
        if(data.getHomeAway().equals(data.getAddition3())){
            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                oldSores.servePointsCount.setHome(servePointsCount.getHome()+1);
                wholeSores.servePointsCount.setHome(servePointsCount.getHome()+1);
            }else {
                oldSores.servePointsCount.setAway(servePointsCount.getAway()+1);
                wholeSores.servePointsCount.setAway(servePointsCount.getAway()+1);
            }
        }
        //3.计算修正后的指标
        if(oldData.getHomeAway().equals(TeamTypeConstant.HOME)){
            oldSores.pointsCount.setHome(pointsCount.getHome()-1);
            wholeSores.pointsCount.setHome(pointsCount.getHome()-1);
        }else {
            oldSores.pointsCount.setAway(pointsCount.getAway()-1);
            wholeSores.pointsCount.setAway(pointsCount.getAway()-1);
        }
        if(oldData.getExtraInfo()!=null&&oldData.getExtraInfo().equals("0")){
            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                oldSores.receivedPointsCount.setHome(receivedPointsCount.getHome()-1);
                wholeSores.receivedPointsCount.setHome(receivedPointsCount.getHome()-1);
            }else {
                oldSores.receivedPointsCount.setAway(receivedPointsCount.getAway()+1);
                wholeSores.receivedPointsCount.setAway(receivedPointsCount.getAway()+1);
            }
        }
        if(oldData.getHomeAway().equals(oldData.getAddition3())){
            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                oldSores.servePointsCount.setHome(servePointsCount.getHome()-1);
                wholeSores.servePointsCount.setHome(servePointsCount.getHome()-1);
            }else {
                oldSores.servePointsCount.setAway(servePointsCount.getAway()-1);
                wholeSores.servePointsCount.setAway(servePointsCount.getAway()-1);
            }
        }
            //2.1 改阶段
            //2.2 改全局
    }

}
