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
public class IceHockeyScores extends  AbstractSportScores{

    @ScoresProperty(eventName = "全场比分",eventCode ={"goal","match_score"})
    private CommonItem matchScore ;

    @ScoresProperty(eventName = "射门比分" , eventCode ={"shot_on_target"})
    private CommonItem shotOnTarget ;

    @ScoresProperty(eventName = "大罚比分" ,eventCode = "suspension",extrainInfo = "2")
    private CommonItem suspensionBig;

    @ScoresProperty(eventName = "小罚比分" ,eventCode = "suspension",extrainInfo = "1")
    private CommonItem suspensionSmall;

    @ScoresProperty(eventName = "以多打少" ,eventCode = "suspension",extrainInfo = "9")
    private CommonItem moreHitScores;

    @ScoresProperty(eventName = "以少打多" ,eventCode = "suspension",extrainInfo = "10")
    private CommonItem lessHitScores;

    public IceHockeyScores(Long periodId) {
        super.init(this);
    }

    public IceHockeyScores( ) {
        super.init(this);
    }

    public void updateEvent(MatchEventInfo data,Map<Long, IceHockeyScores> allPeriodScores){
        if(data.getEventCode().equals("goal")){
            goal(data,allPeriodScores);
        }
        if(data.getEventCode().equals("shot_on_target")){
            shotOnTarget(data,allPeriodScores);
        }
        if(data.getEventCode().equals("suspension")){
            suspension(data,allPeriodScores);
        }
        if("5mins_pen".equals(data.getEventCode())){
            suspensionNewEvent(data,allPeriodScores);
        }
        if("2mins_pen".equals(data.getEventCode())){
            suspensionNewEvent(data,allPeriodScores);
        }
    }

    /**
     * http://lan-zentao.sportxxxr1pub.com/zentao/bug-view-459.html?tid=zsq0ldos
     * 1.RB新增冰球的事件
     * 2.客户端统计大罚下发事件  没有对应比分中心下发
     * 3.新增比分大罚小罚的比分统计
     * @param data
     * @param allPeriodScores
     */
    private void suspensionNewEvent(MatchEventInfo data, Map<Long, IceHockeyScores> allPeriodScores) {
        IceHockeyScores periodScores=allPeriodScores.get(data.getMatchPeriodId());
        if("5mins_pen".equals(data.getEventCode())){
            if(TeamTypeConstant.HOME.equals(data.getHomeAway())){
                suspensionBig.setHome(suspensionBig.getHome()+1);
                periodScores.suspensionBig.setHome(suspensionBig.getHome()+1);
            }
            if(TeamTypeConstant.AWAY.equals(data.getHomeAway())){
                suspensionBig.setAway(suspensionBig.getAway()+1);
                periodScores.suspensionBig.setAway(suspensionBig.getAway()+1);
            }
        }
        if("2mins_pen".equals(data.getEventCode())){
            if(TeamTypeConstant.HOME.equals(data.getHomeAway())){
                suspensionSmall.setHome(suspensionSmall.getHome()+1);
                periodScores.suspensionSmall.setHome(suspensionSmall.getHome()+1);
            }
            if(TeamTypeConstant.AWAY.equals(data.getHomeAway())){
                suspensionSmall.setAway(suspensionSmall.getAway()+1);
                periodScores.suspensionSmall.setAway(suspensionSmall.getAway()+1);
            }
        }
    }

    private void suspension(MatchEventInfo data, Map<Long, IceHockeyScores> allPeriodScores) {
        IceHockeyScores periodScores=allPeriodScores.get(data.getMatchPeriodId());
        if(("2").equals(data.getExtraInfo())){
            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                suspensionBig.setHome(suspensionBig.getHome()+1);
                periodScores.suspensionBig.setHome(suspensionBig.getHome()+1);
            }
            if(data.getHomeAway().equals(TeamTypeConstant.AWAY)){
                suspensionBig.setAway(suspensionBig.getAway()+1);
                periodScores.suspensionBig.setAway(suspensionBig.getAway()+1);
            }
        }
        if(("1").equals(data.getExtraInfo())){
            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                suspensionSmall.setHome(suspensionSmall.getHome()+1);
                periodScores.suspensionSmall.setHome(suspensionSmall.getHome()+1);
            }
            if(data.getHomeAway().equals(TeamTypeConstant.AWAY)){
                suspensionSmall.setAway(suspensionSmall.getAway()+1);
                periodScores.suspensionSmall.setAway(suspensionSmall.getAway()+1);
            }
        }
    }

    private void shotOnTarget(MatchEventInfo data, Map<Long, IceHockeyScores> allPeriodScores) {
        IceHockeyScores periodScores=allPeriodScores.get(data.getMatchPeriodId());
        shotOnTarget.setHome(data.getT1());
        shotOnTarget.setAway(data.getT2());
        Integer addHome =data.getT1()-shotOnTarget.getHome();
        Integer addAway =data.getT2()-shotOnTarget.getAway();
        periodScores.shotOnTarget.setHome(periodScores.shotOnTarget.getHome()+addHome);
        periodScores.shotOnTarget.setAway(periodScores.shotOnTarget.getAway()+addAway);
        this.shotOnTarget.setHome(data.getT1());
        this.shotOnTarget.setAway(data.getT2());
    }

    private void goal(MatchEventInfo data, Map<Long, IceHockeyScores> allPeriodScores) {
        log.info(" thirdMatchId:{} ,eventCode:{},before: home:{},away:{}",data.getThirdMatchId(),"matchScore",matchScore.getHome(),matchScore.getAway());
        IceHockeyScores periodScores=allPeriodScores.get(data.getMatchPeriodId());
        if(data.getFirstT1()!=null && data.getFirstT2()!=null){
            periodScores.matchScore.setHome(data.getFirstT1());
            periodScores.matchScore.setAway(data.getFirstT2());
        }else{
            if(data.getT1()>matchScore.getHome()||data.getT2()>matchScore.getAway()) {
                Integer addHome = data.getT1() - matchScore.getHome();
                Integer addAway = data.getT2() - matchScore.getAway();
                periodScores.matchScore.setHome(periodScores.matchScore.getHome() + addHome);
                periodScores.matchScore.setAway(periodScores.matchScore.getAway() + addAway);
            }
        }
        if(!data.getMatchPeriodId().equals(50L)){
            this.matchScore.setHome(data.getT1());
            this.matchScore.setAway(data.getT2());
        }
        if("9".equals(data.getExtraInfo())){
            if(data.getHomeAway().equals("home")){
                moreHitScores.setHome(moreHitScores.getHome()+1);
                periodScores.moreHitScores.setHome(moreHitScores.getHome()+1);
            }else {
                moreHitScores.setAway(moreHitScores.getAway()+1);
                periodScores.moreHitScores.setAway(moreHitScores.getAway()+1);
            }

        }
        if("10".equals(data.getExtraInfo())){
            if(data.getHomeAway().equals("home")){
                lessHitScores.setHome(lessHitScores.getHome()+1);
                periodScores.lessHitScores.setHome(lessHitScores.getHome()+1);
            }else {
                lessHitScores.setAway(lessHitScores.getAway()+1);
                periodScores.lessHitScores.setAway(lessHitScores.getAway()+1);
            }
        }
        log.info(" thirdMatchId:{} ,eventCode:{},after: home:{},away:{}",data.getThirdMatchId(),"matchScore",matchScore.getHome(),matchScore.getAway());

    }

    public void cancelCalculation(MatchEventInfo data, MatchEventInfo oldMatchInfo,  Map<Long,IceHockeyScores> allPeriodScores){
        if(oldMatchInfo.getEventCode().equals("goal")){
            cancelGoal(data,oldMatchInfo,allPeriodScores);
        }
        if(oldMatchInfo.getEventCode().equals("shot_on_target")){
            cancelShotOnTarget(data,oldMatchInfo,allPeriodScores);
        }
        if(oldMatchInfo.getEventCode().equals("suspension")){
            cancelSuspension(data,oldMatchInfo,allPeriodScores);
        }
    }

    private void cancelSuspension(MatchEventInfo data, MatchEventInfo oldMatchInfo, Map<Long,IceHockeyScores> allPeriodScores) {
        if( ("2").equals(oldMatchInfo.getExtraInfo())){
            if(oldMatchInfo.getHomeAway().equals(TeamTypeConstant.HOME)){
                suspensionBig.setHome(suspensionBig.getHome()-1);
            }
            if(oldMatchInfo.getHomeAway().equals(TeamTypeConstant.AWAY)){
                suspensionBig.setAway(suspensionBig.getAway()-1);
            }
        }
        if(("1").equals(oldMatchInfo.getExtraInfo())){
            if(oldMatchInfo.getHomeAway().equals(TeamTypeConstant.HOME)){
                suspensionSmall.setHome(suspensionSmall.getHome()-1);
            }
            if(oldMatchInfo.getHomeAway().equals(TeamTypeConstant.AWAY)){
                suspensionSmall.setAway(suspensionSmall.getAway()-1);
            }
        }
        suspension(data, allPeriodScores);
    }

    private void cancelShotOnTarget(MatchEventInfo data, MatchEventInfo oldMatchInfo, Map<Long,IceHockeyScores> allPeriodScores) {
        shotOnTarget.setHome(data.getT1());
        shotOnTarget.setAway(data.getT2());
    }

    private void cancelGoal(MatchEventInfo data, MatchEventInfo oldMatchInfo, Map<Long,IceHockeyScores> allPeriodScores) {
//        matchScore.setHome(data.getT1());
//        matchScore.setAway(data.getT2());
        if(data.getFirstT1()!=null && data.getFirstT2()!=null){
            this.matchScore.setHome(data.getFirstT1());
            this.matchScore.setAway(data.getFirstT2());
        }else{
            if(data.getT1()>matchScore.getHome()||data.getT2()>matchScore.getAway()) {
                Integer addHome = data.getT1() - matchScore.getHome();
                Integer addAway = data.getT2() - matchScore.getAway();
                this.matchScore.setHome(this.matchScore.getHome() + addHome);
                this.matchScore.setAway(this.matchScore.getAway() + addAway);
            }
        }
        if(!data.getMatchPeriodId().equals(50L)){
            this.matchScore.setHome(data.getT1());
            this.matchScore.setAway(data.getT2());
        }
    }

    public void doCalculation(Map<Long, IceHockeyScores> allPeriodScores) {
        IceHockeyScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        Integer home =wholeSores.matchScore.getHome();
        Integer away=wholeSores.matchScore.getAway();
        Integer suspensionBigHome=wholeSores.suspensionBig.getHome();
        Integer suspensionBigAway=wholeSores.suspensionBig.getAway();
        Integer suspensionSmallHome=wholeSores.suspensionSmall.getHome();
        Integer suspensionSmallAway=wholeSores.suspensionSmall.getAway();

        for (Map.Entry<Long, IceHockeyScores> entry : allPeriodScores.entrySet()) {
            if(entry.getKey().equals(WHOLE_MATCH)){
                continue;
            }
            if(entry.getValue()==this){
                continue;
            }
            home=home-entry.getValue().matchScore.getHome();
            away=away-entry.getValue().matchScore.getAway();
            suspensionBigHome=suspensionBigHome-entry.getValue().suspensionBig.getHome();
            suspensionBigAway=suspensionBigAway-entry.getValue().suspensionBig.getAway();
            suspensionSmallHome=suspensionSmallHome-entry.getValue().suspensionSmall.getHome();
            suspensionSmallAway=suspensionSmallAway-entry.getValue().suspensionSmall.getAway();
        }
        this.matchScore.setHome(home);
        this.matchScore.setHome(away);
        suspensionBig.setHome(suspensionBigHome);
        suspensionBig.setAway(suspensionBigAway);
        suspensionSmall.setHome(suspensionSmallHome);
        suspensionSmall.setAway(suspensionSmallAway);
    }

}
