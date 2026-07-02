package com.panda.merge.dto;

import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Data
@Slf4j
public class BaseballScores extends  AbstractSportScores{

    @ScoresProperty(eventName = "盘比分",eventCode ={"run_scored","match_score","set_scores"})
    private CommonItem matchScore ;

    @ScoresProperty(eventName = "半局比分")
    private CommonItem setScore ;

    @ScoresProperty(eventName = "安打" ,eventCode ={"run_scored","batter_advances_to_base_x"})
    private CommonItem hit ;

    @ScoresProperty(eventName = "好球" ,eventCode ={"run_scored","runner_out","batter_advances_to_base_x","batter_out","foul_ball","strike","ball"})
    private Integer goodBall ;

    @ScoresProperty(eventName = "坏球" ,eventCode ={"run_scored","runner_out","batter_advances_to_base_x","batter_out","foul_ball","strike","ball"})
    private Integer badBall ;

    @ScoresProperty(eventName = "跑垒出局" ,eventCode ={"runner_out","batter_out"})
    private Integer runnerOut ;

    @ScoresProperty(eventName = "一垒" ,eventCode ={"runner_out","run_scored","batter_advances_to_base_x","checked_runner","runner_advances_to_base_x"})
    private Integer firstBase ;

    @ScoresProperty(eventName = "二垒" ,eventCode ={"runner_out","run_scored","batter_advances_to_base_x","checked_runner","runner_advances_to_base_x"})
    private Integer secondBase ;

    @ScoresProperty(eventName = "三垒" ,eventCode ={"runner_out","run_scored","batter_advances_to_base_x","checked_runner","runner_advances_to_base_x"})
    private Integer thirdBase ;

    @ScoresProperty(eventName = "本垒打")
    private Integer baseHit ;

    @ScoresProperty(eventName = "得分" )
    private Integer score ;

    @ScoresProperty(eventName = "打者数")
    private Integer hitNumber ;

    @ScoresProperty(eventName = "残垒数")
    private Integer baseNumber ;

    @ScoresProperty(eventName = "投球数")
    private Integer ballNumber ;

    @ScoresProperty(eventName = "保送" )
    private Integer safeBall ;

    @ScoresProperty(eventName = "触身球")
    private Integer bodyBall ;

    public BaseballScores( ) {
        super.init(this);
    }

    /**
     * 先对接基本数据入库
     * */
    public void updateEvent(MatchEventInfo data,Map<Long, BaseballScores> allPeriodScores){
        BaseballScores periodScores= allPeriodScores.get(data.getMatchPeriodId().longValue());
        if(data.getEventCode().equals("match_status")){
            //阶段安打数必须率先计算
            updatePeriodHit(data,periodScores,allPeriodScores);
            cleanBase();
            //这里会重置全局安打数
            runScored(data,periodScores);
        }
        if(data.getEventCode().equals("play_start")){
            periodScores.ballNumber++;
            ballNumber= periodScores.ballNumber;
        }

        if(data.getEventCode().equals("run_scored")){
            updatePeriodHit(data,periodScores,allPeriodScores);
            runScored(data,periodScores);
            setBall(data);
            setBase(data);
            periodScores.setBall(data);
            periodScores.setBase(data);
        }
        if(data.getEventCode().equals("batter_advances_to_base_x")){
            updatePeriodHit(data,periodScores,allPeriodScores);
            runScored(data,periodScores);
            setBall(data);
            setBase(data);
            periodScores.setBall(data);
            periodScores.setBase(data);
        }
        if(data.getEventCode().equals("runner_out")){
            updatePeriodHit(data,periodScores,allPeriodScores);
            setBall(data);
            runnerOut(data);
            setBase(data);
            cleanBall(data);
            periodScores.setBall(data);
            periodScores.runnerOut(data);
            periodScores.setBase(data);
            periodScores.cleanBall(data);
            runScored(data,periodScores);
        }
        if(data.getEventCode().equals("batter_out")){
            updatePeriodHit(data,periodScores,allPeriodScores);
            setBall(data);
            runnerOut(data);
            cleanBall(data);
            periodScores.setBall(data);
            periodScores.runnerOut(data);
            periodScores.cleanBall(data);
            runScored(data,periodScores);
        }
        if(data.getEventCode().equals("foul_ball")){
            updatePeriodHit(data,periodScores,allPeriodScores);
            setBall(data);
            periodScores.setBall(data);
            runScored(data,periodScores);
        }
        if(data.getEventCode().equals("strike")){
            updatePeriodHit(data,periodScores,allPeriodScores);
            setBall(data);
            periodScores.setBall(data);
            runScored(data,periodScores);
        }
        if(data.getEventCode().equals("ball")){
            updatePeriodHit(data,periodScores,allPeriodScores);
            setBall(data);
            periodScores.setBall(data);
            runScored(data,periodScores);
        }
        if(data.getEventCode().equals("checked_runner")){
            updatePeriodHit(data,periodScores,allPeriodScores);
            setBase(data);
            periodScores.setBase(data);
            runScored(data,periodScores);
        }
        if(data.getEventCode().equals("runner_advances_to_base_x")){
            updatePeriodHit(data,periodScores,allPeriodScores);
            setBase(data);
            periodScores.setBase(data);
            runScored(data,periodScores);
        }

    }

    public void cleanBase() {
        firstBase=0;
        secondBase=0;
        thirdBase=0;
        //好球,坏球,出局设置0
        goodBall=0 ;
        badBall =0 ;
        runnerOut=0 ;

    }

    private void updatePeriodHit(MatchEventInfo data,BaseballScores periodScores,Map<Long, BaseballScores> allPeriodScores) {
        if(data.getEventCode().equals("match_status")&& SportPeriodConstant.BaseballPeriod.getIndexByPeriod(data.getMatchPeriodId())%2!=0){
            long lastPeriod= SportPeriodConstant.BaseballPeriod.WHOLE_PERIODS[SportPeriodConstant.BaseballPeriod.getIndexByPeriod(data.getMatchPeriodId())-1];
            BaseballScores lastPeriodScore =allPeriodScores.get(lastPeriod);
            if(lastPeriodScore==null){
               return;
            }
            periodScores.hit.setHome(lastPeriodScore.hit.getHome());
            periodScores.hit.setAway(lastPeriodScore.hit.getAway());
        }
        if(data.getSecondT1()!=null&&data.getSecondT2()!=null){
            Integer addHitHome = data.getSecondT1()-hit.getHome();
            Integer addHitAway = data.getSecondT2()-hit.getAway();
            periodScores.hit.setHome(periodScores.hit.getHome()+addHitHome);
            periodScores.hit.setAway(periodScores.hit.getAway()+addHitAway);
        }
    }

    private void cleanBall(MatchEventInfo data) {
        goodBall=0;
        badBall=0;
    }

    private void setBase(MatchEventInfo data){
        if(StringUtils.isNotEmpty(data.getAddition7())) {
            firstBase=Integer.parseInt(data.getAddition7());
        }
//        else {
//            firstBase=0;
//        }
        if(StringUtils.isNotEmpty(data.getAddition8())) {
            secondBase=Integer.parseInt(data.getAddition8());
        }
//        else {
//            secondBase=0;
//        }
        if(StringUtils.isNotEmpty(data.getAddition9())) {
            thirdBase=Integer.parseInt(data.getAddition9());
        }
//        else {
//            thirdBase=0;
//        }
    }
    private void runnerOut(MatchEventInfo data){
        if(StringUtils.isNotEmpty(data.getAddition3())) {
            Integer runnerOutNum = Integer.parseInt(data.getAddition3());
            runnerOut= runnerOutNum >= 3 ? 3 : runnerOutNum;
        }
    }
//    private void batterAdvancesToBaseX(MatchEventInfo data) {
//        hit.setHome(data.getSecondT1());
//        hit.setAway(data.getSecondT2());
//    }
    private void setBall(MatchEventInfo data){
        if(StringUtils.isNotEmpty(data.getAddition2())){
            goodBall=Integer.parseInt(data.getAddition2());
        }
        if(StringUtils.isNotEmpty(data.getAddition6())) {
            badBall=Integer.parseInt(data.getAddition6());
        }
    }
    private void runScored(MatchEventInfo data,BaseballScores periodScores) {
        log.info(" thirdMatchId:{} ,eventCode:{},before: home:{},away:{}",data.getThirdMatchId(),"matchScore",matchScore.getHome(),matchScore.getAway());
        Integer addHome =data.getT1()-matchScore.getHome();
        Integer addAway =data.getT2()-matchScore.getAway();

        if(matchScore.getHome()>data.getT1()||matchScore.getAway()>data.getT2()){
            return;
        }
        periodScores.matchScore.setHome(periodScores.matchScore.getHome()+addHome);
        periodScores.matchScore.setAway(periodScores.matchScore.getAway()+addAway);
        matchScore.setHome(data.getT1());
        matchScore.setAway(data.getT2());
        setScore.setHome(data.getT1());
        setScore.setAway(data.getT2());
        periodScores.setScore.setHome(data.getFirstT1());
        periodScores.setScore.setAway(data.getFirstT2());


        if(data.getSecondT1()!=null&&data.getSecondT2()!=null){
            this.hit.setHome(data.getSecondT1());
            this.hit.setAway(data.getSecondT2());
        }
        //本垒打
        if("4".equals(data.getExtraInfo())){
            baseHit++;
            periodScores.baseHit++;
        }
        //得分
        periodScores.score++;
        score=periodScores.score;

        log.info(" thirdMatchId:{} ,eventCode:{},after: home:{},away:{}",data.getThirdMatchId(),"matchScore",matchScore.getHome(),matchScore.getAway());
    }

    public void correctionEvent(MatchEventInfo data, Map<Long, BaseballScores> allPeriodScores) {
        BaseballScores whole =allPeriodScores.get(WHOLE_MATCH.longValue());
        if(data.getT1()==null) data.setT1(0);
        if(data.getT2()==null) data.setT2(0);
        whole.matchScore.setHome(data.getT1());
        whole.matchScore.setAway(data.getT2());
        String addition2= data.getAddition2();
        List<CommonItem> scores= transfor(addition2);
        if(scores==null){
            return;
        }
        for(int i=0;i<=scores.size()-1;i++){
            // 这里思考下 这个阶段是不是应该是 long 或者说只是 period 因为period 会带局数 / 然而并非如此MMP TODO
            CommonItem commonItem =scores.get(i);
            if(commonItem.getAway()>=0&&commonItem.getHome()>=0){
                long period= 402l +i*2;
                BaseballScores baseballScores =allPeriodScores.get(period);
                if(baseballScores==null){
                    continue;
                }
                baseballScores.setScore.setAway(commonItem.getAway());
                baseballScores.matchScore.setAway(commonItem.getAway());
                baseballScores.setScore.setHome(commonItem.getHome());
                baseballScores.matchScore.setHome(commonItem.getHome());
            }

        }
    }
    private List<CommonItem> transfor(String addition2){
        if(addition2==null){
            return null;
        }
        String[] arr =addition2.split(",");
        List<CommonItem> list=new ArrayList<>();
        for (String s : arr) {
            CommonItem commonItem=new CommonItem();
            String [] scores= s.split(":");
            commonItem.setHome(Integer.parseInt(scores[0]));
            commonItem.setAway(Integer.parseInt(scores[1]));
            list.add(commonItem);
        }
        return list;
    }


    public void cancelCalculation(MatchEventInfo oldMatchInfo, BaseballScores periodScores) {
        //只对删除比分做修复
        Integer addHome =oldMatchInfo.getT1()-matchScore.getHome();
        Integer addAway =oldMatchInfo.getT2()-matchScore.getAway();

        periodScores.matchScore.setHome(periodScores.matchScore.getHome()+addHome);
        periodScores.matchScore.setAway(periodScores.matchScore.getAway()+addAway);
        matchScore.setHome(oldMatchInfo.getT1());
        matchScore.setAway(oldMatchInfo.getT2());
        setScore.setHome(oldMatchInfo.getFirstT1());
        setScore.setAway(oldMatchInfo.getFirstT2());
        periodScores.setScore.setHome(oldMatchInfo.getFirstT1());
        periodScores.setScore.setAway(oldMatchInfo.getFirstT2());
    }
}
