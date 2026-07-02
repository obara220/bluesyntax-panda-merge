package com.panda.merge.dto;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.common.enums.EventCodeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.constant.TeamTypeConstant;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.utils.JsonMapUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Slf4j
@Data
public class FootballScores extends  AbstractSportScores{

    @ScoresProperty(eventName = "角球",eventCode ={"corner","corner_score"})
    private CommonItem corner ;

    @ScoresProperty(eventName = "红牌",eventCode ={"red_card","yellow_red_card","red_card_score"})
    private CommonItem redCard ;

    @ScoresProperty(eventName = "黄牌",eventCode ={"yellow_card","yellow_card_score"})
    private CommonItem yellowCard ;

    @ScoresProperty(eventName = "罚牌",eventCode ={"fa_card"})
    private CommonItem faCard ;

    @ScoresProperty(eventName = "进球",eventCode ={"goal","match_score"})
    private CommonItem goal ;

    @ScoresProperty(eventName = "进攻",eventCode ={"attack"})
    private CommonItem attack ;

    @ScoresProperty(eventName = "危险进攻",eventCode ={"dangerous_attack","dangerous_attack_score"})
    private CommonItem dangerousAttack ;

    @ScoresProperty(eventName = "控球权",eventCode ={"possession"})
    private CommonItem possession ;

    @ScoresProperty(eventName = "控球率",eventCode ={"ball_possession_percentage"})
    private CommonItem ballPossessionPercentage ;

    @ScoresProperty(eventName = "持球数(持球事件下发次数)", eventCode = {"possession_count"})
    private CommonItem possessionCount;

    @ScoresProperty(eventName = "射正",eventCode ={"shot_on_target","shot_on_target_Score"})
    private CommonItem shotOn ;

    @ScoresProperty(eventName = "射偏",eventCode ={"shot_off_target","shot_off_target_score"})
    private CommonItem shotOff ;

    @ScoresProperty(eventName = "射门")
    private CommonItem shot;

    @ScoresProperty(eventName = "换人",eventCode ={"substitution"})
    private CommonItem substitution ;

    @ScoresProperty(eventName = "越位",eventCode ={"offside"})
    private CommonItem offside ;

    @ScoresProperty(eventName = "点球",eventCode ={"penalty_awarded","penalty_score", "penalty_goal"})
    private CommonItem penaltyAwarded;

    @ScoresProperty(eventName = "任意球",eventCode ={"free_kick_score","free_kick"})
    private CommonItem freeKickScore;

    @ScoresProperty(eventName = "开球",eventCode = {"kick_off"})
    private CommonItem  kickOff ;

    @ScoresProperty(eventName = "界外球", eventCode = {"throw_in"})
    private CommonItem throwIn;

    @ScoresProperty(eventName = "球门球", eventCode = {"goal_kick"})
    private CommonItem goalKick;

    @ScoresProperty(eventName = "红黄牌", eventCode = {"yellow_red_card"})
    private CommonItem yellowRedCard;

    @ScoresProperty(eventName = "总共点球",eventCode ={"penalty_awarded_total","penalty"})
    private CommonItem penaltyAwardedTotal;

    @ScoresProperty(eventName = "控球时间",eventCode ={"possession_time"})
    private CommonItem possessionTime ;
    @ScoresProperty(eventName = "预期失球",eventCode ={"expectation_loss"})
    private CommonItemBigDecimal expectationLoss ;
    @ScoresProperty(eventName = "预期进球",eventCode ={"expectation_xg"})
    private CommonItemBigDecimal expectationXg ;

    /**
     * 公共事件保持数据一致性 规定 主队存放公共事件1，客队存放公共事件2
     */
    @ScoresProperty(eventName = "公共事件",eventCode ={"public_event"})
    private CommonItem publicEvent;

    private FootballScores() {
        }
    public FootballScores(Long periodId) {
            super.init(this);
    }

    public static FootballScores createMinFootballScores() {
        FootballScores footballScores=new FootballScores();
        CommonItem corner = new CommonItem();
        CommonItem goal = new CommonItem();
        footballScores.corner=corner;
        footballScores.goal=goal;
        CommonItem redCard = new CommonItem();
        CommonItem yellowCard = new CommonItem();
        CommonItem faCard = new CommonItem();
        footballScores.redCard=redCard;
        footballScores.faCard=faCard;
        footballScores.yellowCard=yellowCard;
        CommonItem yellowRedCard = new CommonItem();
        footballScores.yellowRedCard = yellowRedCard;
        CommonItem throwIn = new CommonItem();
        footballScores.throwIn = throwIn;
        CommonItem possession = new CommonItem();
        footballScores.possession = possession;
        CommonItem ballPossessionPercentage = new CommonItem();
        footballScores.ballPossessionPercentage = ballPossessionPercentage;
        CommonItem possessionCount = new CommonItem();
        footballScores.possessionCount = possessionCount;
        CommonItem publicEvent = new CommonItem();
        footballScores.publicEvent = publicEvent;
        CommonItem attack = new CommonItem();
        footballScores.attack = attack;
        CommonItem gaolKick = new CommonItem();
        footballScores.goalKick = gaolKick;
        CommonItem offSide = new CommonItem();
        footballScores.offside = offSide;
        footballScores.dangerousAttack = new CommonItem();
        footballScores.penaltyAwarded = new CommonItem();
        footballScores.penaltyAwardedTotal = new CommonItem();
        footballScores.freeKickScore = new CommonItem();
        footballScores.shotOn = new CommonItem();
        footballScores.shotOff = new CommonItem();
        footballScores.possessionTime = new CommonItem();
        footballScores.expectationLoss = new CommonItemBigDecimal();
        footballScores.expectationXg = new CommonItemBigDecimal();
        return footballScores;
    }

    /**
     * 1.根据code设置参数比分
     * @param data   当前事件信息
     * @param allPeriodScores 库中全场比分数据
     * */
    public void setFieldByEventCode(MatchEventInfo data, Map<Long, FootballScores> allPeriodScores){
        //全场(-1)比分处理
        FootballScores wholeScore = allPeriodScores.get(WHOLE_MATCH);

        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            ScoresProperty item = field.getAnnotation(ScoresProperty.class);
            try {
                for (String code : item.eventCode()) {
//                    if(EventCodeEnum.CORNER.code.equals(code) || EventCodeEnum.GOAL.code.equals(code) || EventCodeEnum.YELLOW_CARD.code.equals(code) || EventCodeEnum.RED_CARD.code.equals(code)){
//                        //角球、进球、红牌、黄牌单独处理
//                        log.info("{},setFieldByEventCode,单条事件逻辑处理 角球、进球、红牌、黄牌单独处理.,thirdMatchId={},evemtCode={}",data.getLinkId(),data.getThirdMatchId(),data.getEventCode());
//                        break;
//                    }
                    if (code.equals(data.getEventCode())) {
                        //当前阶段比分
                        Object commonItemObj = field.get(this);
                        if(commonItemObj == null){
                            log.info("setFieldByEventCode,单条事件逻辑处理,eventCode="+data.getEventCode()+",linkId="+data.getLinkId()+",thirdMatchId:{} setFieldByEventCode:{} 找不到属性!",data.getThirdMatchId(),data.getEventCode());
//                            return false;
                            return;
                        }
                        CommonItem commonItem = (com.panda.merge.dto.CommonItem)commonItemObj;
                        //全场比分
                        CommonItem wholeItem = (com.panda.merge.dto.CommonItem)field.get(wholeScore);
                        if(commonItem.getHome() >= data.getT1() && commonItem.getAway() >= data.getT2()){
                            log.info("setFieldByEventCode,单条事件逻辑处理,linkId="+data.getLinkId()+",消费顺序有误!"+data.getEventCode(),"已存在：" +data.getEventCode()+
                                    " "+data.getT1()+"-"+data.getT2());
//                            return  false;
                            return;
                        }
//                        log.info(" thirdMatchId:{} ,eventCode:{},before: home:{},away:{}",data.getThirdMatchId(),data.getEventCode(),commonItem.getHome(),commonItem.getAway());
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

                        log.info("setFieldByEventCode,单条事件逻辑处理,linkId="+data.getLinkId()+" thirdMatchId:{} ,eventCode:{},after: home:{},away:{}, wholeItem.home={},wholeItem.away={}",
                                data.getThirdMatchId(),data.getEventCode(),commonItem.getHome(),commonItem.getAway(),wholeItem.getHome(),wholeItem.getAway());
                        return;
                    }
                }

                if(EventCodeEnum.CORNER.code.equals(data.getEventCode())){
                    log.info("setFieldByEventCode,角球统计1,linkId="+data.getLinkId()+"，当前阶段角球："+this.corner+"，全场角球："+wholeScore.corner);
                    if(data.getFirstT1()==null && data.getFirstT2()==null){
                        if(data.getMatchPeriodId()==6L){
                            this.corner.setHome(data.getT1());
                            this.corner.setAway(data.getT2());
                        }else{
                            Integer addH = data.getT1() - wholeScore.corner.getHome();
                            Integer addW = data.getT2() - wholeScore.corner.getAway();
                            if(addH<0 || addW<0){
                                log.info("阶段:"+data.getMatchPeriodId()+" setFieldByEventCode,linkId="+data.getLinkId()+",消费顺序有误:"+addH+"----"+addW);
                                return;
                            }
                            this.corner.setHome(this.corner.getHome()+addH);
                            this.corner.setAway(this.corner.getAway()+addW);
                        }
                    }else{
                        if(data.getFirstT1()<this.corner.getHome() || data.getFirstT2()<this.corner.getAway()){
                            log.info("setFieldByEventCode,，已消费到更大的比分数据，本次不处理，linkId="+data.getLinkId()+"，当前阶段角球："+this.corner+"，全场角球："+wholeScore.corner);
                            return;
                        }
                        //当前阶段
                        this.corner.setHome(data.getFirstT1());
                        this.corner.setAway(data.getFirstT2());
                    }
                    //全场
                    wholeScore.corner.setHome(data.getT1());
                    wholeScore.corner.setAway(data.getT2());
                    log.info("setFieldByEventCode,角球统计2,linkId="+data.getLinkId()+"，当前阶段角球："+this.corner+"，全场角球："+wholeScore.corner);
                    return;
                }
                if(EventCodeEnum.GOAL.code.equals(data.getEventCode())){
                    if(data.getFirstT1()==null && data.getFirstT2()==null){
                        if(data.getMatchPeriodId()==6L){
                            this.goal.setHome(data.getT1());
                            this.goal.setAway(data.getT2());
                        }else{
                            Integer addH = data.getT1() - wholeScore.goal.getHome();
                            Integer addW = data.getT2() - wholeScore.goal.getAway();
                            if(addH<0 || addW<0){
                                log.info("阶段:"+data.getMatchPeriodId()+" setFieldByEventCode,linkId="+data.getLinkId()+",消费顺序有误:"+addH+"----"+addW);
                                return;
                            }
                            this.goal.setHome(this.goal.getHome()+addH);
                            this.goal.setAway(this.goal.getAway()+addW);
                        }
                    }else{
                        if(data.getFirstT1()<this.goal.getHome() || data.getFirstT2()<this.goal.getAway()){
                            log.info("setFieldByEventCode,，已消费到更大的比分数据，本次不处理，linkId="+data.getLinkId()+"，当前阶段进球："+this.corner+"，全场进球："+wholeScore.corner);
                            return;
                        }
                        //当前阶段
                        this.goal.setHome(data.getFirstT1());
                        this.goal.setAway(data.getFirstT2());
                    }
                    //全场
                    wholeScore.goal.setHome(data.getT1());
                    wholeScore.goal.setAway(data.getT2());
                    log.info("setFieldByEventCode,进球统计,linkId="+data.getLinkId()+"，当前阶段进球："+this.goal+"，全场进球："+wholeScore.goal);
                    return;
                }
                if(EventCodeEnum.RED_CARD.code.equals(data.getEventCode())){
                    if(data.getFirstT1()==null && data.getFirstT2()==null) {
                        if (data.getMatchPeriodId() == 6L) {
                            if (this.redCard.getHome() > data.getT1() || this.redCard.getAway() > data.getT2()) {
                                log.info("阶段:" + data.getMatchPeriodId() + " setFieldByEventCode,单条事件逻辑处理,linkId=" + data.getLinkId() + ",消费顺序有误," +
                                        "已存在红牌：" + this.redCard.doCountScoreStr() + ",事件红牌：" + data.getT1() + "-" + data.getT2());
                                //                    return false;
                                return;
                            }
                            this.redCard.setHome(data.getT1());
                            this.redCard.setAway(data.getT2());
                        } else {
                            Integer addH = data.getT1() - wholeScore.redCard.getHome();
                            Integer addW = data.getT2() - wholeScore.redCard.getAway();
                            this.redCard.setHome(this.redCard.getHome() + addH);
                            this.redCard.setAway(this.redCard.getAway() + addW);
                        }
                    }else{
                        if(data.getFirstT1()<this.redCard.getHome() || data.getFirstT2()<this.redCard.getAway()){
                            log.info("setFieldByEventCode,，已消费到更大的比分数据，本次不处理，linkId="+data.getLinkId()+"，当前阶段红牌："+this.redCard+"，全场红牌："+wholeScore.redCard);
                            return;
                        }
                        //当前阶段
                        this.redCard.setHome(data.getFirstT1());
                        this.redCard.setAway(data.getFirstT2());
                    }
                    //全场
                    wholeScore.redCard.setHome(data.getT1());
                    wholeScore.redCard.setAway(data.getT2());
                    log.info("setFieldByEventCode,红牌统计,linkId="+data.getLinkId()+"，当前阶段红牌："+this.redCard+"，全场红牌："+wholeScore.redCard);
                    //全场比分罚牌计算
                    wholeScore.countFaCard();
                    //罚牌计算
                    countFaCard();
                    return;
                }
                if(EventCodeEnum.YELLOW_CARD.code.equals(data.getEventCode())){
                    if(data.getFirstT1()==null && data.getFirstT2()==null) {
                        if(data.getMatchPeriodId()==6L){
                            if(this.yellowCard.getHome()>data.getT1() || this.yellowCard.getAway()>data.getT2()){
                                log.info("阶段:"+data.getMatchPeriodId()+" setFieldByEventCode,单条事件逻辑处理,linkId="+data.getLinkId()+",消费顺序有误," +
                                        "已存在黄牌："+this.yellowCard.doCountScoreStr()+",事件黄牌："+data.getT1()+"-"+data.getT2());
                                //                    return false;
                                return;
                            }
                            this.yellowCard.setHome(data.getT1());
                            this.yellowCard.setAway(data.getT2());
                        }else{
                            Integer addH = data.getT1() - wholeScore.yellowCard.getHome();
                            Integer addW = data.getT2() - wholeScore.yellowCard.getAway();
                            this.yellowCard.setHome(this.yellowCard.getHome()+addH);
                            this.yellowCard.setAway(this.yellowCard.getAway()+addW);
                        }
                    }else{
                        if(data.getFirstT1()<this.yellowCard.getHome() || data.getFirstT2()<this.yellowCard.getAway()){
                            log.info("setFieldByEventCode,，已消费到更大的比分数据，本次不处理，linkId="+data.getLinkId()+"，当前阶段黄牌："+this.yellowCard+"，全场黄牌："+wholeScore.yellowCard);
                            return;
                        }
                        //当前阶段
                        this.yellowCard.setHome(data.getFirstT1());
                        this.yellowCard.setAway(data.getFirstT2());
                    }
                    //全场
                    wholeScore.yellowCard.setHome(data.getT1());
                    wholeScore.yellowCard.setAway(data.getT2());
                    log.info("setFieldByEventCode,黄牌统计,linkId="+data.getLinkId()+"，当前阶段黄牌："+this.yellowCard+"，全场黄牌："+wholeScore.yellowCard);
                    //全场比分罚牌计算
                    wholeScore.countFaCard();
                    //罚牌计算
                    countFaCard();
                    return;
                }


            }catch (Exception e) {
                log.error("setFieldByEventCode,单条事件逻辑处理,linkId="+data.getLinkId()+",反射处理异常,Exception:", e);
            }
        }
//        return  false;
    }

    public  CommonItem doCalculation(Long thirdMatchId,MatchEventInfo data , Map<Long, FootballScores> periodFootballScores ,Boolean isReissue)  {
        String eventCode = data.getEventCode();
        Long period = data.getMatchPeriodId();
        FootballScores periodScore= periodFootballScores.get(data.getMatchPeriodId());
        //1.判断阶段是否是全局 是则直接返回
        if(period.equals(SportPeriodConstant.SportPeriod.WHOLE_MATCH)){
            return null;
        }
        // 2. 获取全部分数
        FootballScores footballScores=periodFootballScores.get(SportPeriodConstant.SportPeriod.WHOLE_MATCH);
        //3. 判断所求阶段 是 上下半场 还是 加时赛等
        Integer index= SportPeriodConstant.FootballPeriod.getIndexByPeriod(period);
        //非开局数据
        if(index<0){
            return null;
        }
        FootballScores periodScores=periodFootballScores.get(period);
        CommonItem wholeItem=footballScores.getEventScores(eventCode);
        if(wholeItem==null){
            wholeItem =footballScores.createCommonItem(eventCode);
            if(wholeItem==null) {
//                log.error("wholeItem 事件找不到属性:" + eventCode);
                return null;
            }
        }
        CommonItem periodItem=periodScores.getEventScores(eventCode);
        if(periodItem==null){
            periodItem=periodScores.createCommonItem(eventCode);
            if(periodItem==null) {
//                log.error("periodItem 事件找不到属性:" + eventCode);
                return null;
            }
        }
        log.info(" 阶段比分计算doCalculation: thirdMatchId:{} ,eventCode:{},before: home:{},away:{}",thirdMatchId,eventCode,periodItem.getHome(),periodItem.getAway());
        periodItem.setAway(wholeItem.getAway());
        periodItem.setHome(wholeItem.getHome());
        //4. 根据所求阶段计算
        for(int i=0;i<index;i++){
            Long periodDelete= SportPeriodConstant.FootballPeriod.WHOLE_PERIODS[i];
            FootballScores periodDeleteScores=periodFootballScores.get(periodDelete);
            if(periodDeleteScores==null){
                 periodDeleteScores=new FootballScores(periodDelete);
                periodFootballScores.put(periodDelete,periodDeleteScores);
                log.info("事件periodDeleteScores 阶段找不到:"+periodDelete);
            }
            CommonItem periodDeleteItem=periodDeleteScores.getEventScores(eventCode);
            if(periodDeleteItem==null){
                continue;
            }
            periodItem.setAway(periodItem.getAway()-periodDeleteItem.getAway());
            periodItem.setHome(periodItem.getHome()-periodDeleteItem.getHome());
        }
        log.info(" 阶段比分计算doCalculation: thirdMatchId:{} ,eventCode:{},after: home:{},away:{}",
                thirdMatchId,eventCode,periodItem.getHome(),periodItem.getAway());

        if(EventCodeEnum.CORNER.code.equals(eventCode)){
            if(isReissue){
                if(data.getFirstT1()<this.corner.getHome() || data.getFirstT2()<this.corner.getAway()){
                    log.info("setFieldByEventCode,，已消费到更大的比分数据，本次不处理，linkId="+data.getLinkId()+"，当前阶段角球："+this.corner+"，全场角球："+this.corner);
                    return periodItem;
                }
            }
            //当前阶段
            periodScore.corner.setHome(data.getFirstT1()!=null?data.getFirstT1():periodScore.corner.getHome());
            periodScore.corner.setAway(data.getFirstT2()!=null?data.getFirstT2():periodScore.corner.getAway());
            //全场
            this.corner.setHome(data.getT1());
            this.corner.setAway(data.getT2());
            log.info("setFieldByEventCode,角球统计,linkId="+data.getLinkId()+"，当前阶段角球："+this.corner+"，全场角球："+this.corner);
        }
        if(EventCodeEnum.GOAL.code.equals(data.getEventCode()) && period!=50L){
            if(isReissue){
                if(data.getFirstT1()<this.goal.getHome() || data.getFirstT2()<this.goal.getAway()){
                    log.info("setFieldByEventCode,，已消费到更大的比分数据，本次不处理，linkId="+data.getLinkId()+"，当前阶段进球："+this.goal+"，全场进球："+this.corner);
                    return periodItem;
                }
            }
            //当前阶段
            periodScore.goal.setHome(data.getFirstT1()!=null?data.getFirstT1():periodScore.goal.getHome());
            periodScore.goal.setAway(data.getFirstT2()!=null?data.getFirstT2():periodScore.goal.getAway());
            //全场
            this.goal.setHome(data.getT1());
            this.goal.setAway(data.getT2());
            log.info("setFieldByEventCode,进球统计,linkId="+data.getLinkId()+"，当前阶段进球："+this.goal+"，全场进球："+this.goal);
        }
        if(EventCodeEnum.RED_CARD.code.equals(data.getEventCode())){
            if(isReissue){
                if(data.getFirstT1()<this.redCard.getHome() || data.getFirstT2()<this.redCard.getAway()){
                    log.info("setFieldByEventCode,，已消费到更大的比分数据，本次不处理，linkId="+data.getLinkId()+"，当前阶段红牌："+this.redCard+"，全场红牌："+this.redCard);
                    return periodItem;
                }
            }
            //当前阶段
            periodScore.redCard.setHome(data.getFirstT1()!=null?data.getFirstT1():periodScore.redCard.getHome());
            periodScore.redCard.setAway(data.getFirstT2()!=null?data.getFirstT2():periodScore.redCard.getAway());
            //全场
            this.redCard.setHome(data.getT1());
            this.redCard.setAway(data.getT2());
            log.info("setFieldByEventCode,红牌统计,linkId="+data.getLinkId()+"，当前阶段红牌："+this.redCard+"，全场红牌："+this.redCard);
            //全场比分罚牌计算
            this.countFaCard();
            //罚牌计算
            countFaCard();
        }
        if(EventCodeEnum.YELLOW_CARD.code.equals(data.getEventCode())){
            if(isReissue){
                if(data.getFirstT1()<this.yellowCard.getHome() || data.getFirstT2()<this.yellowCard.getAway()){
                    log.info("setFieldByEventCode,，已消费到更大的比分数据，本次不处理，linkId="+data.getLinkId()+"，当前阶段黄牌："+this.yellowCard+"，全场黄牌："+this.yellowCard);
                    return periodItem;
                }
            }
            //当前阶段
            periodScore.yellowCard.setHome(data.getFirstT1()!=null?data.getFirstT1():periodScore.yellowCard.getHome());
            periodScore.yellowCard.setAway(data.getFirstT2()!=null?data.getFirstT2():periodScore.yellowCard.getAway());
            //全场
            this.yellowCard.setHome(data.getT1());
            this.yellowCard.setAway(data.getT2());
            log.info("setFieldByEventCode,黄牌统计,linkId="+data.getLinkId()+"，当前阶段黄牌："+this.yellowCard+"，全场黄牌："+this.yellowCard);
            //全场比分罚牌计算
            this.countFaCard();
            //罚牌计算
            countFaCard();
        }

        return periodItem;
    }
    public void saveStatisticsInfo(String code, Integer t1, Integer t2) {
        CommonItem periodDeleteItem=this.getEventScores(code);
        if(periodDeleteItem==null){
            log.error("事件periodDeleteScores 阶段找不到属性"+":"+code);
           return;
        }
        periodDeleteItem.setHome(t1);
        periodDeleteItem.setAway(t2);
    }
    public void saveStatisticsInfo(MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO, Map<Long, FootballScores> allPeriodScores) {
        FootballScores wholeScore=allPeriodScores.get(SportPeriodConstant.SportPeriod.WHOLE_MATCH);
        CommonItem periodItem=this.getEventScores(matchStatisticsInfoDetailDTO.getCode());
        CommonItem wholeScoreItem=wholeScore.getEventScores(matchStatisticsInfoDetailDTO.getCode());
        if(periodItem==null){
//            log.error("事件periodDeleteScores 阶段找不到属性"+":"+matchStatisticsInfoDetailDTO.getCode());
            return;
        }
        Integer addHome =matchStatisticsInfoDetailDTO.getT1()-wholeScoreItem.getHome();
        Integer addAway =matchStatisticsInfoDetailDTO.getT2()-wholeScoreItem.getAway();
        wholeScoreItem.setHome(matchStatisticsInfoDetailDTO.getT1());
        wholeScoreItem.setAway(matchStatisticsInfoDetailDTO.getT2());
        periodItem.setHome(periodItem.getHome()+addHome);
        periodItem.setAway(periodItem.getAway()+addAway);
    }

    private   CommonItem createCommonItem(String eventCode){
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            String filedName = field.getName();
            field.setAccessible(true);
            // 被StaticsItem修饰的属性才需要做统计
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            try {
                /***  获取统计项注解上的统计数据的event_code值   ***/
                ScoresProperty item = field.getAnnotation(ScoresProperty.class);
                for (String code : item.eventCode()) {
                    if (code.equals(eventCode)) {
                        CommonItem commonItem=new CommonItem();
                        commonItem.setAway(0);
                        commonItem.setHome(0);
                        field.set(this,commonItem);
                        return  commonItem;
                    }
                }

            } catch (Exception e) {

                String msg = "FootballScoresDto" + ";" + filedName + ":统计出错";
            }
        }
        return null;
    }

    public void  doShot(){
        this.shot.setHome(this.shotOn.getHome()+this.shotOff.getHome());
        this.shot.setAway(this.shotOn.getAway()+this.shotOff.getAway());
    }



    /**
     * 1.根据code设置参数比分
     * */
    public boolean set15MinuteFieldByEventCode(MatchEventInfo data, Map<Long, FootballScores> allPeriodScores){
        FootballScores wholeScore =allPeriodScores.get(WHOLE_MATCH);

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
                        if(field.get(this)==null){
                            log.error(data.getLinkId()+"thirdMatchId:{} setFieldByEventCode:{} 找不到属性",data.getThirdMatchId(),data.getEventCode());
                            return false;
                        }
                        CommonItem commonItem = (CommonItem)field.get(this);
                        CommonItem wholeItem = (CommonItem)field.get(wholeScore);
                        log.info(data.getLinkId()+" thirdMatchId:{} ,eventCode:{},before: home:{},away:{}",data.getThirdMatchId(),data.getEventCode(),wholeItem.getHome(),wholeItem.getAway());
                        //求差值
                        Integer addH = data.getT1()-wholeItem.getHome();
                        Integer addW = data.getT2()-wholeItem.getAway();
                        if(commonItem.getHome()+addH<0){
                            commonItem.setHome(0);
                        }else {
                            if(commonItem.getHome()+addH<commonItem.getHome()){
                                if(data.getCanceled()!=1){
                                    log.info(data.getLinkId()+"{} set15MinuteFieldByEventCode，非删除事件比分异常，home原比分：{},新比分：{}，本次不处理",data.getLinkId(),commonItem.getHome(),commonItem.getHome()+addH);
                                    return false;
                                }
                            }
                            commonItem.setHome(commonItem.getHome()+addH);
                        }
                        if(commonItem.getAway()+addW<0){
                            commonItem.setAway(0);
                        }else {
                            if(commonItem.getAway()+addW<commonItem.getAway()) {
                                if (data.getCanceled() != 1) {
                                    log.info(data.getLinkId()+"{} set15MinuteFieldByEventCode，非删除事件比分异常，home原比分：{},新比分：{}，本次不处理", data.getLinkId(), commonItem.getAway(), commonItem.getAway() + addH);
                                    return false;
                                }
                            }
                            commonItem.setAway(commonItem.getAway()+addW);
                        }
                        log.info(data.getLinkId()+"{} set15MinuteFieldByEventCode 修改15分钟比分 thirdMatchId:{} ,eventCode:{},after: home:{},away:{}",data.getLinkId(),data.getThirdMatchId(),data.getEventCode(),commonItem.getHome(),commonItem.getAway());
                        return  true;
                    }
                }
            }catch (IllegalAccessException e) {
                log.error(":处理数据发生异常:", e);
            } catch (Exception e) {
                log.error(":处理数据发生异常:", e);
            }
        }
        return  false;
    }

    /**
     * 1.根据code设置参数比分
     * */
    public boolean set5MinuteFieldByEventCode(MatchEventInfo data, Map<Long, FootballScores> allPeriodScores){
        FootballScores wholeScore =allPeriodScores.get(WHOLE_MATCH);

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
                        if(field.get(this)==null){
                            log.error(data.getLinkId()+"thirdMatchId:{} setFieldByEventCode:{} 找不到属性",data.getThirdMatchId(),data.getEventCode());
                            return false;
                        }
                        CommonItem commonItem = (com.panda.merge.dto.CommonItem)field.get(this);
                        CommonItem wholeItem = (com.panda.merge.dto.CommonItem)field.get(wholeScore);
                        log.info(data.getLinkId()+" thirdMatchId:{} ,eventCode:{},before: home:{},away:{}",data.getThirdMatchId(),data.getEventCode(),wholeItem.getHome(),wholeItem.getAway());
                        //求差值
                        Integer addH = data.getT1()-wholeItem.getHome();
                        Integer addW = data.getT2()-wholeItem.getAway();
                        if(commonItem.getHome()+addH<0){
                            commonItem.setHome(0);
                        }else {
                            if(commonItem.getHome()+addH<commonItem.getHome()){
                                if(data.getCanceled()!=1){
                                    log.info(data.getLinkId()+"{} set5MinuteFieldByEventCode，非删除事件比分异常，home原比分：{},新比分：{}，本次不处理",data.getLinkId(),commonItem.getHome(),commonItem.getHome()+addH);
                                    return false;
                                }
                            }
                            commonItem.setHome(commonItem.getHome()+addH);
                        }
                        if(commonItem.getAway()+addW<0){
                            commonItem.setAway(0);
                        }else {
                            if(commonItem.getAway()+addW<commonItem.getAway()) {
                                if (data.getCanceled() != 1) {
                                    log.info(data.getLinkId()+"{} set5MinuteFieldByEventCode，非删除事件比分异常，home原比分：{},新比分：{}，本次不处理", data.getLinkId(), commonItem.getAway(), commonItem.getAway() + addH);
                                    return false;
                                }
                            }
                            commonItem.setAway(commonItem.getAway()+addW);
                        }
                        log.info(data.getLinkId()+" thirdMatchId:{} ,eventCode:{},after: home:{},away:{}",data.getThirdMatchId(),data.getEventCode(),commonItem.getHome(),commonItem.getAway());
                        return  true;
                    }
                }
            }catch (IllegalAccessException e) {

                log.error(":处理数据发生异常:", e);
            } catch (Exception e) {

                log.error(":处理数据发生异常:", e);
            }
        }
        return  false;
    }


    public void countFaCard(){
        try {
            Integer home = 0;
            Integer away = 0;
            home = redCard.getHome() * 2 + yellowCard.getHome();
            away = redCard.getAway() * 2 + yellowCard.getAway();
            faCard.setHome(home);
            faCard.setAway(away);
        }catch (Exception e){
            log.error(":计算罚牌比分异常：", e);
        }
    }

    public  static  void main(String []  xx) {
        FootballScores footballScoresDto = new FootballScores(50l);
        FootballScores t= ((JSONObject) JSONObject.toJSON(footballScoresDto)).toJavaObject(FootballScores.class);
        Map<Long, FootballScores> X=new HashMap<>();
        X.put(50l,footballScoresDto);
        X.put(0l,t);
        System.out.println( SportPeriodConstant.FootballPeriod.getIndexByPeriod(0l));
    }


    public void countKickOff(MatchEventInfo data) {
        if(data.getEventCode().equals("kick_off_team")){
            if(data.getHomeAway().equals("home")){
                this.getKickOff().setHome(1);
                this.getKickOff().setAway(0);
            }else {
                this.getKickOff().setHome(0);
                this.getKickOff().setAway(1);
            }
        }
    }

    public CommonItem getFieldByEventCode(String eventCode) throws IllegalAccessException {
        Field[] fields = this.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(ScoresProperty.class)) {
                continue;
            }
            ScoresProperty item = field.getAnnotation(ScoresProperty.class);
            if (Arrays.asList(item.eventCode()).contains(eventCode)){
                return (com.panda.merge.dto.CommonItem) field.get(this);
            }
        }
        return null;
    }

    public void doModifyTimeScores(MatchEventInfo data, Map<Long, FootballScores> allPeriodScores,int timeInterval,MatchEventInfo lastEvent) {
        Long time = data.getSecondsFromStart();
        Long lastEventTime = 0L;
        if(lastEvent.getSecondsFromStart()!=null){
            lastEventTime = lastEvent.getSecondsFromStart();
        }
        if(lastEventTime==null || lastEventTime==0){
            log.info("{},doModifyTimeScores修改比分时间，获取比分区间失败,linkId={}，lastEventTime:{} ",timeInterval, data.getLinkId(), lastEventTime);
            return;
        }
        Long periodMinOld = null;
        Long periodMinNew = null;
        if(timeInterval==15) {
            periodMinOld = SportPeriodConstant.FootballPeriod.get15MinPeriod(lastEvent.getMatchPeriodId(), lastEventTime);
            periodMinNew = SportPeriodConstant.FootballPeriod.get15MinPeriod(data.getMatchPeriodId(), time);
        }else if(timeInterval==5) {
            periodMinOld = SportPeriodConstant.FootballPeriod.get5MinPeriod(lastEvent.getMatchPeriodId(),lastEventTime);
            periodMinNew = SportPeriodConstant.FootballPeriod.get5MinPeriod(data.getMatchPeriodId(),time);
        }else{
            periodMinOld = lastEvent.getMatchPeriodId();
            periodMinNew = data.getMatchPeriodId();
        }
        if (periodMinNew == null || periodMinOld == null) {
            log.info("{},doModifyTimeScores修改比分时间，获取比分区间失败,linkId={}，新：{}，旧：{} ",timeInterval, data.getLinkId(), time,lastEventTime);
            return;
        }
        if (periodMinOld.equals(periodMinNew)) {
            log.info("{},doModifyTimeScores修改比分时间，比分区间相同，无需处理,linkId={}，新：{}，旧：{}",timeInterval, data.getLinkId(), time,lastEventTime);
            return;
        }
        //旧的比分区间比分扣减1
        FootballScores periodScores15Old = allPeriodScores.get(periodMinOld);
        if(periodScores15Old==null){
            periodScores15Old = new FootballScores(periodMinOld);
        }
        //新的比分区间比分加1
        FootballScores periodScores15New = allPeriodScores.get(periodMinNew);
        if(periodScores15New==null){
            periodScores15New = new FootballScores(periodMinNew);
        }
        if(EventCodeEnum.CONNER_TIME_MODIFIED.code.equals(data.getEventCode())){
            if(TeamTypeConstant.HOME.equals(data.getHomeAway())){
                periodScores15Old.getCorner().setHome(periodScores15Old.getCorner().getHome()-1);
                periodScores15New.getCorner().setHome(periodScores15New.getCorner().getHome()+1);
                if(periodScores15Old.getCorner().getHome()<0){
                    periodScores15Old.getCorner().setHome(0);
                }
            }else{
                periodScores15Old.getCorner().setAway(periodScores15Old.getCorner().getAway()-1);
                periodScores15New.getCorner().setAway(periodScores15New.getCorner().getAway()+1);
                if(periodScores15Old.getCorner().getAway()<0){
                    periodScores15Old.getCorner().setAway(0);
                }
            }
        }else if(EventCodeEnum.GOAL_TIME_MODIFIED.code.equals(data.getEventCode())){
            if(TeamTypeConstant.HOME.equals(data.getHomeAway())){
                periodScores15Old.getGoal().setHome(periodScores15Old.getGoal().getHome()-1);
                periodScores15New.getGoal().setHome(periodScores15New.getGoal().getHome()+1);
                if(periodScores15Old.getGoal().getHome()<0){
                    periodScores15Old.getGoal().setHome(0);
                }
            }else{
                periodScores15Old.getGoal().setAway(periodScores15Old.getGoal().getAway()-1);
                periodScores15New.getGoal().setAway(periodScores15New.getGoal().getAway()+1);
                if(periodScores15Old.getGoal().getAway()<0){
                    periodScores15Old.getGoal().setAway(0);
                }
            }
        }else if(EventCodeEnum.YELLOWCARD_TIME_MODIFIED.code.equals(data.getEventCode())){
            if(TeamTypeConstant.HOME.equals(data.getHomeAway())){
                periodScores15Old.getYellowCard().setHome(periodScores15Old.getYellowCard().getHome()-1);
                periodScores15New.getYellowCard().setHome(periodScores15New.getYellowCard().getHome()+1);
                if(periodScores15Old.getYellowCard().getHome()<0){
                    periodScores15Old.getYellowCard().setHome(0);
                }
            }else{
                periodScores15Old.getYellowCard().setAway(periodScores15Old.getYellowCard().getAway()-1);
                periodScores15New.getYellowCard().setAway(periodScores15New.getYellowCard().getAway()+1);
                if(periodScores15Old.getYellowCard().getAway()<0){
                    periodScores15Old.getYellowCard().setAway(0);
                }
            }
        }else if(EventCodeEnum.REDCARD_TIME_MODIFIED.code.equals(data.getEventCode())){
            if(TeamTypeConstant.HOME.equals(data.getHomeAway())){
                periodScores15Old.getRedCard().setHome(periodScores15Old.getRedCard().getHome()-1);
                periodScores15New.getRedCard().setHome(periodScores15New.getRedCard().getHome()+1);
                if(periodScores15Old.getRedCard().getHome()<0){
                    periodScores15Old.getRedCard().setHome(0);
                }
            }else{
                periodScores15Old.getRedCard().setAway(periodScores15Old.getRedCard().getAway()-1);
                periodScores15New.getRedCard().setAway(periodScores15New.getRedCard().getAway()+1);
                if(periodScores15Old.getRedCard().getAway()<0){
                    periodScores15Old.getRedCard().setAway(0);
                }
            }
        }
        allPeriodScores.put(periodMinOld,periodScores15Old);
        allPeriodScores.put(periodMinNew,periodScores15New);
    }

}
