package com.panda.merge.dto;


import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.common.enums.EventCodeEnum;
import com.panda.merge.constant.EffectScoresCode;
import com.panda.merge.constant.TeamTypeConstant;
import com.panda.merge.model.MatchEventInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
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

    @ScoresProperty(eventName = "赢分",eventCode ={"snooker_score_red","snooker_score_yellow","snooker_score_green","snooker_score_brown","snooker_score_blue","snooker_score_pink","snooker_score_black"})
    private CommonItem winingScore;

    @ScoresProperty(eventName = "红球赢分", eventCode ={"snooker_score_red"})
    private CommonItem redWiningScore;

    @ScoresProperty(eventName = "黄球赢分", eventCode ={"snooker_score_yellow"})
    private CommonItem yelloweWiningScore;

    @ScoresProperty(eventName = "绿球赢分", eventCode ={"snooker_score_green"})
    private CommonItem greenWiningScore;

    @ScoresProperty(eventName = "棕球赢分", eventCode ={"snooker_score_brown"})
    private CommonItem brownWiningScore;

    @ScoresProperty(eventName = "蓝球赢分", eventCode ={"snooker_score_blue"})
    private CommonItem blueWiningScore;

    @ScoresProperty(eventName = "粉球赢分", eventCode ={"snooker_score_pink"})
    private CommonItem pinkWiningScore;

    @ScoresProperty(eventName = "黑球赢分", eventCode ={"snooker_score_black"})
    private CommonItem blackWiningScore;

    @ScoresProperty(eventName = "罚分",eventCode ={"snooker_foul_red","snooker_foul_yellow","snooker_foul_green","snooker_foul_brown","snooker_foul_blue","snooker_foul_pink","snooker_foul_black"})
    private CommonItem foulScore;
    @ScoresProperty(eventName = "红球罚分", eventCode ={"snooker_foul_red"})
    private CommonItem redfoulScore;

    @ScoresProperty(eventName = "黄球罚分", eventCode ={"snooker_foul_yellow"})
    private CommonItem yellowfoulScore;

    @ScoresProperty(eventName = "绿球罚分", eventCode ={"snooker_foul_green"})
    private CommonItem greenfoulScore;

    @ScoresProperty(eventName = "棕球罚分", eventCode ={"snooker_foul_brown"})
    private CommonItem brownfoulScore;
    @ScoresProperty(eventName = "蓝球罚分", eventCode ={"snooker_foul_blue"})
    private CommonItem bluefoulScore;

    @ScoresProperty(eventName = "粉球罚分", eventCode ={"snooker_foul_pink"})
    private CommonItem pinkfoulScore;
    @ScoresProperty(eventName = "黑球罚分", eventCode ={"snooker_foul_black"})
    private CommonItem blackfoulScore;
    @ScoresProperty(eventName = "持杆")
    private CommonItem strick;
    @ScoresProperty(eventName = "自由球得分",eventCode ={"free_ball_pot"})
    private CommonItem freeball;
    @ScoresProperty(eventName = "自由球未得分",eventCode ={"free_ball_nopot"})
    private CommonItem freeballNoPot;
    @ScoresProperty(eventName = "开球",eventCode ={"which_team_serves_first"})
    private CommonItem kickoff;

    @ScoresProperty(eventName = "重排",eventCode ={"rerack"})
    private CommonItem rerack;
    @ScoresProperty(eventName = "犯规罚4",eventCode ={"snooker_foul_4"})
    private CommonItem penaltyFour;

    @ScoresProperty(eventName = "犯规罚7",eventCode ={"snooker_foul_7"})
    private CommonItem penaltySeven;

    @ScoresProperty(eventName = "其他犯规")
    private CommonItem penaltyOther;

    @ScoresProperty(eventName = "认输")
    private CommonItem directLoss;





    public SnookerScores( ) {
        super.init(this);
    }

    public void doCalculation(MatchEventInfo data, SnookerScores wholeSores) {

        //斯诺克比分事件
        if(EffectScoresCode.SNOOKER_SCORES_EVENT.contains(data.getEventCode())){
            log.info(" thirdMatchId:{} ,eventCode:{},before: home:{},away:{}",data.getThirdMatchId(),"matchScore",matchScore.getHome(),matchScore.getAway());
            if(data.getT1()>=matchScore.getHome()||data.getT2()>=matchScore.getAway()){
                //-1阶段的盘比分和局比分
                wholeSores.getMatchScore().setHome(data.getT1());
                wholeSores.getMatchScore().setAway(data.getT2());
                wholeSores.getSetScore().setHome(data.getFirstT1());
                wholeSores.getSetScore().setAway(data.getFirstT2());
                //当前阶段的盘比分和局比分
                matchScore.setHome(data.getT1());
                matchScore.setAway(data.getT2());
                setScore.setHome(data.getFirstT1());
                setScore.setAway(data.getFirstT2());
            }else if(data.getFirstT1()>setScore.getHome()||data.getFirstT2()>setScore.getAway()){
                setScore.setHome(data.getFirstT1());
                setScore.setAway(data.getFirstT2());
                matchScore.setHome(data.getFirstT1());
                matchScore.setAway(data.getFirstT2());
            }
            log.info(" thirdMatchId:{} ,eventCode:{},after: home:{},away:{}",data.getThirdMatchId(),"matchScore",matchScore.getHome(),matchScore.getAway());
        }

         if(data.getEventCode().equals("snooker_foul")){
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


    public void setFieldByEventCode(MatchEventInfo data, Map<Long, SnookerScores> allPeriodScores){
        //全场(-1)比分处理
        SnookerScores wholeScore = allPeriodScores.get(WHOLE_MATCH);
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            ScoresProperty item = field.getAnnotation(ScoresProperty.class);
            try {
                for (String code : item.eventCode()) {
                    if (code.equals(data.getEventCode())) {
                        //当前阶段比分
                        Object commonItemObj = field.get(this);
                        if(commonItemObj == null){
                            log.info("setFieldByEventCode,单条事件逻辑处理,eventCode="+data.getEventCode()+",linkId="+data.getLinkId()+",thirdMatchId:{} setFieldByEventCode:{} 找不到属性!",data.getThirdMatchId(),data.getEventCode());
                            return;
                        }
                        CommonItem commonItem = (com.panda.merge.dto.CommonItem)commonItemObj;
                        //全场比分
                        CommonItem wholeItem = (com.panda.merge.dto.CommonItem)field.get(wholeScore);
                        if(commonItem.getHome() >= data.getT1() && commonItem.getAway() >= data.getT2()){
                            log.info("setFieldByEventCode,单条事件逻辑处理,linkId="+data.getLinkId()+",消费顺序有误!"+data.getEventCode(),"已存在：" +data.getEventCode()+
                                    " "+data.getT1()+"-"+data.getT2());
                            return;
                        }
                        //求差值（当前事件传入的比分 - 数据库中比分）
                        Integer addH = data.getT1() - wholeItem.getHome();
                        Integer addW = data.getT2() - wholeItem.getAway();
                        if(commonItem.getHome() + addH < 0){
                            commonItem.setHome(0);
                        }else {
                            commonItem.setHome(commonItem.getHome() + addH);
                        }
                        if(commonItem.getAway() + addW < 0){
                            commonItem.setAway(0);
                        }else {
                            commonItem.setAway(commonItem.getAway() + addW);
                        }
                        //全场比分赋值
                        wholeItem.setHome(data.getT1());
                        wholeItem.setAway(data.getT2());
                        log.info("斯诺克比分setFieldByEventCode,单条事件逻辑处理,linkId="+data.getLinkId()+" thirdMatchId:{} ,eventCode:{},after: home:{},away:{}, wholeItem.home={},wholeItem.away={}",
                                data.getThirdMatchId(),data.getEventCode(),commonItem.getHome(),commonItem.getAway(),wholeItem.getHome(),wholeItem.getAway());
                        return;
                    }
                }
            }catch (Exception e) {
                log.error("斯诺克比分setFieldByEventCode,单条事件逻辑处理,linkId="+data.getLinkId()+",反射处理异常,Exception:", e);
            }
        }
//        return  false;
    }

    public void cancelCalculation(MatchEventInfo data, MatchEventInfo oldMatchInfo, Map<Long, SnookerScores> allPeriodScores) {
        SnookerScores wholeSores = allPeriodScores.get(WHOLE_MATCH );
        SnookerScores oldSores = allPeriodScores.get(data.getFirstNum()+0l);
        if(EffectScoresCode.SNOOKER_SCORES_EVENT.contains(data.getEventCode())){
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
