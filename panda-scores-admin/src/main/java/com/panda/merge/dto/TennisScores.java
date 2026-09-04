package com.panda.merge.dto;


import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.constant.TeamTypeConstant;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;


@Slf4j
@Data
public class TennisScores extends  AbstractSportScores{

    @ScoresProperty(eventName = "盘比分",eventCode ={"tennis_score_change","match_score","set_scores"})
    private CommonItem matchScore ;

    @ScoresProperty(eventName = "局比分")
    private CommonItem setScore ;

    @ScoresProperty(eventName = "当前局比分")
    private CommonItem currentScore;

    @ScoresProperty(eventName = "抢7或者抢10比分")
    private CommonItem qiangScore;

    @ScoresProperty(eventName = "得分次数")
    private CommonItem scoreNumber;

    @ScoresProperty(eventName = "发球得分次数",extrainInfo ="1")
    private CommonItem servesScoredCount;

    @ScoresProperty(eventName = "发球失败次数",eventCode = "tennis_service_fault")
    private CommonItem servesFaultCount;

    @ScoresProperty(eventName = "破发成功次数",eventCode = "break_success")
    private CommonItem breakSuccessCount;

    @ScoresProperty(eventName = "破发点",eventCode = "break_point")
    private CommonItem breakPointCount;

    @ScoresProperty(eventName = "破发率")
    private CommonItem breakSuccessRate;
    @ScoresProperty(eventName = "双发失误得分")
    private CommonItem doubleFoolScore;


    public TennisScores() {
            super.init(this);
    }
    public TennisScores(Long periodId) {
        super.init(this);
    }

    public void doCalculation(TennisMatchEventInfoDTO tennisMatchEventInfo) {

        if(tennisMatchEventInfo.isBreakPoint()){
            //新增破发点事件
            if(tennisMatchEventInfo.getHomeAwayBreakPoint().equals(TeamTypeConstant.HOME)){
                breakPointCount.setHome(breakPointCount.getHome()+1);
            }
            if(tennisMatchEventInfo.getHomeAwayBreakPoint().equals(TeamTypeConstant.AWAY)){
                breakPointCount.setAway(breakPointCount.getAway()+1);
            }
        }
        if(tennisMatchEventInfo.isBreakSuccess()){
            //新增破发事件
            if(tennisMatchEventInfo.getHomeAwayBreakSuccess().equals(TeamTypeConstant.HOME)){
                breakSuccessCount.setHome(breakSuccessCount.getHome()+1);
            }
            if(tennisMatchEventInfo.getHomeAwayBreakSuccess().equals(TeamTypeConstant.AWAY)){
                breakSuccessCount.setAway(breakSuccessCount.getAway()+1);
            }
        }
        // 计算破发率
        if(breakPointCount.getAway()!=0){
            breakSuccessRate.setAway(breakSuccessCount.getAway()*100/breakPointCount.getAway());
        }
        if(breakPointCount.getHome()!=0){
            breakSuccessRate.setHome(breakSuccessCount.getHome()*100/breakPointCount.getHome());
        }
    }



    public boolean setFieldByEvent( MatchEventInfo data){
        if(data.getEventCode().equals("tennis_score_change")){
//            log.info(" thirdMatchId:{} ,eventCode:{},before: home:{},away:{}",data.getThirdMatchId(),"matchScore",matchScore.getHome(),matchScore.getAway());
            this.setScore.setHome(data.getFirstT1());
            this.setScore.setAway(data.getFirstT2());
            this.matchScore.setHome(data.getT1());
            this.matchScore.setAway(data.getT2());
            /**抢七规则判断*/
            if(data.getSecondNum()>12&& ((data.getSecondT1()>0&&data.getSecondT1()<=10)||(data.getSecondT2()>0&&data.getSecondT2()<=10))){
                qiangScore.setHome(data.getSecondT1());
                qiangScore.setAway(data.getSecondT2());
            }else {
                currentScore.setHome(data.getSecondT1());
                currentScore.setAway(data.getSecondT2());
            }

            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                scoreNumber.setHome(scoreNumber.getHome()+1);
                //1.破发计算 home 发球
//                if((data.getSecondT2()==40||data.getSecondT2()==50)&&(!(data.getSecondT2()==40&&data.getSecondT1()==40))){
//                    // away 破发点
//                    breakPointCount.setAway(breakPointCount.getAway()+1);
//                }
            }else {
                scoreNumber.setAway(scoreNumber.getAway()+1);
                //1.破发计算 away 发球
//                if((data.getSecondT1()==40||data.getSecondT1()==50)&&(!(data.getSecondT2()==40&&data.getSecondT1()==40))){
//                    // home 破发点
//                    breakPointCount.setHome(breakPointCount.getHome()+1);
//                }
            }

            if(data.getExtraInfo()!=null&&data.getExtraInfo().equals("1")){
                if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                    servesScoredCount.setHome(servesScoredCount.getHome()+1);
                }else {
                    servesScoredCount.setAway(servesScoredCount.getAway()+1);
                }
            }
            if(data.getExtraInfo()!=null&&data.getExtraInfo().equals("2")){
                if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                    doubleFoolScore.setHome(doubleFoolScore.getHome()+1);
                }else {
                    doubleFoolScore.setAway(doubleFoolScore.getAway()+1);
                }
            }
//            log.info(" thirdMatchId:{} ,eventCode:{},after: home:{},away:{}",data.getThirdMatchId(),"matchScore",matchScore.getHome(),matchScore.getAway());


        }
        if(data.getEventCode().equals("tennis_service_fault")&& StringUtils.isNotEmpty(data.getExtraInfo())&&data.getExtraInfo().equals("2")){
            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                servesFaultCount.setHome(servesFaultCount.getHome()+1);
            }else {
                servesFaultCount.setAway(servesFaultCount.getAway()+1);
            }
        }
        return  false;
    }

    public void reSetEvent(Map<Long, TennisScores> allPeriodScores, TennisMatchEventInfoDTO tennisMatchEventInfo) {
        //1.比分重置
        TennisScores wholeScores= allPeriodScores.get(SportPeriodConstant.SportPeriod.WHOLE_MATCH);
        TennisScores periodScores= allPeriodScores.get(tennisMatchEventInfo.getMatchEventInfo().getMatchPeriodId());
        Integer deleteST1=periodScores.setScore.getHome()-tennisMatchEventInfo.getMatchEventInfo().getFirstT1();
        Integer deleteST2=periodScores.setScore.getAway()-tennisMatchEventInfo.getMatchEventInfo().getFirstT2();
        Integer deleteT1=periodScores.matchScore.getHome()-tennisMatchEventInfo.getMatchEventInfo().getT1();
        Integer deleteT2=periodScores.matchScore.getAway()-tennisMatchEventInfo.getMatchEventInfo().getT2();
        periodScores.setScore.setHome(tennisMatchEventInfo.getMatchEventInfo().getFirstT1());
        periodScores.setScore.setAway(tennisMatchEventInfo.getMatchEventInfo().getFirstT2());
        periodScores.matchScore.setHome(tennisMatchEventInfo.getMatchEventInfo().getT1());
        periodScores.matchScore.setAway(tennisMatchEventInfo.getMatchEventInfo().getT2());

        wholeScores.setScore.setHome( wholeScores.setScore.getHome()-deleteST1);
        wholeScores.setScore.setAway( wholeScores.setScore.getAway()-deleteST2);
        wholeScores.matchScore.setHome(wholeScores.matchScore.getHome()-deleteT1);
        wholeScores.matchScore.setAway(wholeScores.matchScore.getAway()-deleteT2);
        //2.-1
        if(tennisMatchEventInfo.isBreakPoint()){
           if(tennisMatchEventInfo.getHomeAwayBreakPoint().equals(TeamTypeConstant.HOME)){
               periodScores.breakPointCount.setHome(periodScores.breakPointCount.getHome()-1);
               wholeScores.breakPointCount.setHome(wholeScores.breakPointCount.getHome()-1);
           }else {
               periodScores.breakPointCount.setAway(periodScores.breakPointCount.getAway()-1);
               wholeScores.breakPointCount.setAway(wholeScores.breakPointCount.getAway()-1);
           }
        }
        if(tennisMatchEventInfo.isBreakSuccess()){
            if(tennisMatchEventInfo.getHomeAwayBreakSuccess().equals(TeamTypeConstant.HOME)){
                periodScores.breakSuccessCount.setHome(periodScores.breakSuccessCount.getHome()-1);
                wholeScores.breakSuccessCount.setHome(wholeScores.breakSuccessCount.getHome()-1);
            }else {
                periodScores.breakSuccessCount.setAway(periodScores.breakSuccessCount.getAway()-1);
                wholeScores.breakSuccessCount.setAway(wholeScores.breakSuccessCount.getAway()-1);
            }
        }
        if(periodScores.breakPointCount.getAway()!=0)
        periodScores.breakSuccessRate.setAway(periodScores.breakSuccessCount.getAway()*100/periodScores.breakPointCount.getAway());
        if(periodScores.breakPointCount.getHome()!=0)
        periodScores.breakSuccessRate.setHome(periodScores.breakSuccessCount.getHome()*100/periodScores.breakPointCount.getHome());
        if(wholeScores.breakPointCount.getAway()!=0)
        wholeScores.breakSuccessRate.setAway(wholeScores.breakSuccessCount.getAway()*100/wholeScores.breakPointCount.getAway());
        if(wholeScores.breakPointCount.getHome()!=0)
        wholeScores.breakSuccessRate.setHome(wholeScores.breakSuccessCount.getHome()*100/wholeScores.breakPointCount.getHome());
    }


}
