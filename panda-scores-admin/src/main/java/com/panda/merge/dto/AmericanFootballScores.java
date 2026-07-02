package com.panda.merge.dto;

import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.constant.TeamTypeConstant;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Data
@Slf4j
public class AmericanFootballScores extends  AbstractSportScores{

    @ScoresProperty(eventName = "比分",eventCode ={"score_change","match_score","set_scores"})
    private CommonItem matchScore ;

    @ScoresProperty(eventName = "冲球数",eventCode ={"rush","rush_count"})
    private CommonItem rushCount ;

    @ScoresProperty(eventName = "射门比分",eventCode ={"field_goal"})
    private CommonItem fieldGoal ;

    @ScoresProperty(eventName = "进攻比分",eventCode ={"play_start","play_start_count"})
    private CommonItem playStartCount ;

    @ScoresProperty(eventName = "达阵比分",eventCode ={"touchdown"})
    private CommonItem touchdown ;
    //事件编码
    public static HashSet<String> AMERICAN_FOOTBALL_EVENT_CODES=new HashSet<String>(Arrays.asList("match_score","field_goal","touchdown","play_start","rush"));

    public AmericanFootballScores() {
        super.init(this);
    }

    public void setScores(Integer home,Integer away){
        matchScore.setAway(away);
        matchScore.setHome(home);
    }

    /**
     * 更新事件数据
     * @param data
     * @param allPeriodScores
     */
    public void updateEvent(MatchEventInfo data, Map<Long, AmericanFootballScores> allPeriodScores){
        AmericanFootballScores periodScores=allPeriodScores.get(data.getMatchPeriodId());
        if(data.getT1()==0 && data.getT2() == 0){
            return;
        }
        if(!SportPeriodConstant.AmericanFootballPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        Integer period13HomeScore = allPeriodScores.get(13L)==null?0:allPeriodScores.get(13L).getMatchScore().getHome();
        Integer period13AwayScore = allPeriodScores.get(13L)==null?0:allPeriodScores.get(13L).getMatchScore().getAway();
        Integer period14HomeScore = allPeriodScores.get(14L)==null?0:allPeriodScores.get(14L).getMatchScore().getHome();
        Integer period14AwayScore = allPeriodScores.get(14L)==null?0:allPeriodScores.get(14L).getMatchScore().getAway();
        Integer period15HomeScore = allPeriodScores.get(15L)==null?0:allPeriodScores.get(15L).getMatchScore().getHome();
        Integer period15AwayScore = allPeriodScores.get(15L)==null?0:allPeriodScores.get(15L).getMatchScore().getAway();
        Integer oldHomeScore = 0;
        Integer oldAwayScore = 0;
        if(data.getMatchPeriodId()==14L){
            oldHomeScore = period13HomeScore;
            oldAwayScore = period13AwayScore;
        }else if(data.getMatchPeriodId()==15L){
            oldHomeScore = period13HomeScore+period14HomeScore;
            oldAwayScore = period13AwayScore+period14AwayScore;
        }else if(data.getMatchPeriodId()==16L){
            oldHomeScore = period13HomeScore+period14HomeScore+period15HomeScore;
            oldAwayScore = period13AwayScore+period14AwayScore+period15AwayScore;
        }
        this.matchScore.setHome(data.getT1());
        this.matchScore.setAway(data.getT2());
        periodScores.matchScore.setHome(data.getT1() - oldHomeScore);
        periodScores.matchScore.setAway(data.getT2() - oldAwayScore);
        //历史比分
        if("field_goal".equals(data.getEventCode())){

            //全局射门比分
            this.fieldGoal.setHome(data.getT1());
            this.fieldGoal.setAway(data.getT2());
            //当前阶段射门比分 = 当前最新比分-历史阶段比分
            periodScores.fieldGoal.setHome( data.getT1() - oldHomeScore);
            periodScores.fieldGoal.setAway( data.getT2() - oldAwayScore);

            return;
        }
        if("rush".equals(data.getEventCode())){
            CommonItem commonItem = allPeriodScores.get(data.getMatchPeriodId()).rushCount;
            CommonItem wholeItem =  allPeriodScores.get(-1L).rushCount;
            if(commonItem==null){
                commonItem = new CommonItem();
            }
            if(wholeItem==null){
                wholeItem = new CommonItem();
            }
            Integer addH = data.getT1()-wholeItem.getHome();
            Integer addW = data.getT2()-wholeItem.getAway();

            this.rushCount.setHome(data.getT1());
            this.rushCount.setAway(data.getT2());
            if(commonItem.getHome()+addH<0){
                commonItem.setHome(0);
            }else{
                if(commonItem.getHome()+addH<commonItem.getHome()){
                    log.info("{} updateEvent，非删除事件比分异常，home原比分：{},新比分：{}，本次不处理",data.getLinkId(),commonItem.getHome(),commonItem.getHome()+addH);
                    return ;
                }
            }
            if(commonItem.getAway()+addW<0){
                commonItem.setAway(0);
            }else{
                if(commonItem.getAway()+addW<commonItem.getAway()){
                    log.info("{} updateEvent，非删除事件比分异常，away原比分：{},新比分：{}，本次不处理",data.getLinkId(),commonItem.getAway(),commonItem.getAway()+addW);
                    return ;
                }
            }
            log.info("{} updateEvent，本次处理美足"+data.getEventCode()+"事件，home原比分：{},新比分：{}",data.getLinkId(),commonItem.getHome(),commonItem.getHome()+addH);
            log.info("{} updateEvent，本次处理美足"+data.getEventCode()+"事件，away原比分：{},新比分：{}",data.getLinkId(),commonItem.getAway(),commonItem.getAway()+addW);
            periodScores.rushCount.setHome(commonItem.getHome()+addH);
            periodScores.rushCount.setAway(commonItem.getAway()+addW);
            return;
        }
        if("play_start".equals(data.getEventCode())){
            CommonItem commonItem = allPeriodScores.get(data.getMatchPeriodId()).playStartCount;
            CommonItem wholeItem =  allPeriodScores.get(-1L).playStartCount;
            if(commonItem==null){
                commonItem = new CommonItem();
            }
            if(wholeItem==null){
                wholeItem = new CommonItem();
            }
            Integer addH = data.getT1()-wholeItem.getHome();
            Integer addW = data.getT2()-wholeItem.getAway();
            this.playStartCount.setHome(data.getT1() );
            this.playStartCount.setAway(data.getT2() );
            if(commonItem.getHome()+addH<0){
                commonItem.setHome(0);
            }else{
                if(commonItem.getHome()+addH<commonItem.getHome()){
                    log.info("{} updateEvent，非删除事件比分异常，home原比分：{},新比分：{}，本次不处理",data.getLinkId(),commonItem.getHome(),commonItem.getHome()+addH);
                    return ;
                }
            }
            if(commonItem.getAway()+addW<0){
                commonItem.setAway(0);
            }else{
                if(commonItem.getAway()+addW<commonItem.getAway()){
                    log.info("{} updateEvent，非删除事件比分异常，away原比分：{},新比分：{}，本次不处理",data.getLinkId(),commonItem.getAway(),commonItem.getAway()+addW);
                    return ;
                }
            }
            log.info("{} updateEvent，本次处理美足"+data.getEventCode()+"事件，home原比分：{},新比分：{}",data.getLinkId(),commonItem.getHome(),commonItem.getHome()+addH);
            log.info("{} updateEvent，本次处理美足"+data.getEventCode()+"事件，away原比分：{},新比分：{}",data.getLinkId(),commonItem.getAway(),commonItem.getAway()+addW);
            periodScores.playStartCount.setHome(commonItem.getHome()+addH);
            periodScores.playStartCount.setAway(commonItem.getAway()+addW);
            return;
        }

        if("touchdown".equals(data.getEventCode())){
            CommonItem commonItem = allPeriodScores.get(data.getMatchPeriodId()).playStartCount;
            CommonItem wholeItem =  allPeriodScores.get(-1L).playStartCount;
            if(commonItem==null){
                commonItem = new CommonItem();
            }
            if(wholeItem==null){
                wholeItem = new CommonItem();
            }
            Integer addH = data.getT1()-wholeItem.getHome();
            Integer addW = data.getT2()-wholeItem.getAway();
            this.touchdown.setHome(data.getT1() );
            this.touchdown.setAway(data.getT2() );
            if(commonItem.getHome()+addH<0){
                commonItem.setHome(0);
            }else{
                if(commonItem.getHome()+addH<commonItem.getHome()){
                    log.info("{} updateEvent，非删除事件比分异常，home原比分：{},新比分：{}，本次不处理",data.getLinkId(),commonItem.getHome(),commonItem.getHome()+addH);
                    return ;
                }
            }
            if(commonItem.getAway()+addW<0){
                commonItem.setAway(0);
            }else{
                if(commonItem.getAway()+addW<commonItem.getAway()){
                    log.info("{} updateEvent，非删除事件比分异常，away原比分：{},新比分：{}，本次不处理",data.getLinkId(),commonItem.getAway(),commonItem.getAway()+addW);
                    return ;
                }
            }
            log.info("{} updateEvent，本次处理美足"+data.getEventCode()+"事件，home原比分：{},新比分：{}",data.getLinkId(),commonItem.getHome(),commonItem.getHome()+addH);
            log.info("{} updateEvent，本次处理美足"+data.getEventCode()+"事件，away原比分：{},新比分：{}",data.getLinkId(),commonItem.getAway(),commonItem.getAway()+addW);
            periodScores.touchdown.setHome(commonItem.getHome()+addH);
            periodScores.touchdown.setAway(commonItem.getAway()+addW);
            return;
        }
    }
    /**
     * 获取最新阶段比分
     * @param allPeriodScores
     * @param period
     * @param newScore
     * @return
     */
    private Integer getNewScores(Map<Long, AmericanFootballScores> allPeriodScores,Long period,Integer newScore){
        if(allPeriodScores.get(period)!=null){
            int home = allPeriodScores.get(period).getMatchScore().getHome();
            int away = allPeriodScores.get(period).getMatchScore().getAway();
            newScore = newScore - (home+ away);
        }
        return newScore;
    }
    /**
     * 初始化比分
     * @param allPeriodScores
     * @param data
     */
    public void doCalculation(Map<Long, AmericanFootballScores> allPeriodScores, MatchEventInfo data) {
//        AmericanFootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        Integer home =allPeriodScores.get(data.getMatchPeriodId()).matchScore.getHome();
        Integer away=allPeriodScores.get(data.getMatchPeriodId()).matchScore.getAway();
//        for (Map.Entry<Long, AmericanFootballScores> entry : allPeriodScores.entrySet()) {
//            if(entry.getKey().equals(WHOLE_MATCH)){
//                continue;
//            }
//            if(entry.getValue()==this){
//                continue;
//            }
//            if(entry.getKey()>data.getMatchPeriodId()){
//                continue;
//            }
//            home=home-entry.getValue().matchScore.getHome();
//            away=away-entry.getValue().matchScore.getAway();
//        }
        this.matchScore.setHome(home);
        this.matchScore.setAway(away);
    }

    /**
     * 取消事件比分修改
     * @param data
     * @param oldMatchInfo
     * @param allPeriodScores
     */
    public void cancelCalculation(MatchEventInfo data, MatchEventInfo oldMatchInfo, Map<Long, AmericanFootballScores> allPeriodScores) {

        AmericanFootballScores periodScores=allPeriodScores.get(data.getMatchPeriodId());


        //1.分数纠正
        if(AMERICAN_FOOTBALL_EVENT_CODES.contains(data.getEventCode())){
            Integer addHome =data.getT1()-this.getMatchScore().getHome();
            Integer addAway =data.getT2()-this.getMatchScore().getAway();
            this.matchScore.setHome(data.getT1());
            this.matchScore.setAway(data.getT2());
            periodScores.matchScore.setHome(periodScores.matchScore.getHome()+addHome);
            periodScores.matchScore.setAway(periodScores.matchScore.getAway()+addAway);
            if(oldMatchInfo.getEventCode().equals("field_goal")){
                this.fieldGoal.setHome(data.getT1());
                this.fieldGoal.setAway(data.getT2());
                periodScores.fieldGoal.setHome(periodScores.fieldGoal.getHome()+addHome);
                periodScores.fieldGoal.setAway(periodScores.fieldGoal.getAway()+addAway);

            }else if(oldMatchInfo.getEventCode().equals("touchdown")){
                if(oldMatchInfo.getHomeAway().equals(TeamTypeConstant.HOME)){
                    touchdown.setHome(touchdown.getHome()-1);
                    periodScores.touchdown.setHome(touchdown.getHome()-1);
                }else {
                    touchdown.setAway(touchdown.getAway()-1);
                    periodScores.touchdown.setAway(touchdown.getAway()-1);
                }
            }else if(oldMatchInfo.getEventCode().equals("play_start")) {
                if(oldMatchInfo.getHomeAway().equals(TeamTypeConstant.HOME)){
                    playStartCount.setHome(playStartCount.getHome()-1);
                    periodScores.playStartCount.setHome(playStartCount.getHome()-1);
                }else {
                    playStartCount.setAway(playStartCount.getAway()-1);
                    periodScores.playStartCount.setAway(playStartCount.getAway()-1);
                }
            }else if(oldMatchInfo.getEventCode().equals("rush")) {
                if(oldMatchInfo.getHomeAway().equals(TeamTypeConstant.HOME)){
                    rushCount.setHome(rushCount.getHome()-1);
                    periodScores.rushCount.setHome(rushCount.getHome()-1);
                }else {
                    rushCount.setAway(rushCount.getAway()-1);
                    periodScores.rushCount.setAway(rushCount.getAway()-1);
                }
            }
        }
    }

}
