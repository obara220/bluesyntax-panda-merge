package com.panda.merge.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.panda.merge.annotation.ScoresProperty;
import com.panda.merge.common.enums.TeamTypeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.constant.TeamTypeConstant;
import com.panda.merge.dto.advertise.PDBasketBallSendBallDto;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchScoresEventInfo;
import com.panda.merge.utils.JsonMapUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

@Slf4j
@Data
public class BasketballScores extends  AbstractSportScores{

    @ScoresProperty(eventName = "比分",eventCode ={"score_change","match_score","score_correction"})
    private CommonItem matchScore ;


    @ScoresProperty(eventName = "两分球",eventCode ={"score_change","score_miss"},extrainInfo="2")
    private CommonItem twoPointer ;

    @ScoresProperty(eventName = "两分球得分",eventCode ={"score_change"},extrainInfo="2")
    private CommonItem twoPointerMade ;

    @ScoresProperty(eventName = "两分球未命中",eventCode ={"score_change"},extrainInfo="2")
    private CommonItem twoPointerMiss ;

    @ScoresProperty(eventName = "两分球命中率",eventCode ={"score_change"},extrainInfo="2")
    private CommonFItem twoPointerHitRate ;

    @ScoresProperty(eventName = "三分球",eventCode ={"score_change","score_miss"},extrainInfo="3")
    private CommonItem threePointer ;

    @ScoresProperty(eventName = "三分球得分",eventCode ={"score_change"},extrainInfo="3")
    private CommonItem threePointerMade ;

    @ScoresProperty(eventName = "三分球未命中",eventCode ={"score_change"},extrainInfo="3")
    private CommonItem threePointerMiss ;

    @ScoresProperty(eventName = "三分球命中率",eventCode ={"score_change"},extrainInfo="3")
    private CommonFItem threePointerHitRate;

    @ScoresProperty(eventName = "投蓝命中率",eventCode ={"score_change"},extrainInfo="2")
    private CommonFItem pointerHitRate;


    /**
     * 主队总分- (主队两分球次数*2) - （主队三分球次数*3）
     * */
    @ScoresProperty(eventName = "罚球得分命中次数")
    private CommonItem freeThrowMade;

    @ScoresProperty(eventName = "罚球未命中次数",eventCode ={"score_miss"})
    private CommonItem freeThrowMiss ;
    /**
     * freeThrowMade+freeThrowMiss;
     * */
    @ScoresProperty(eventName = "罚球次数")
    private CommonItem freeThrowCount;
    /**
     * freeThrowMade*100/freeThrowCount
     * */
    @ScoresProperty(eventName = "罚球命中率")
    private CommonFItem freeThrowHitRate;

    @ScoresProperty(eventName = "篮板",eventCode = "rebound")
    private CommonItem rebound;

    @ScoresProperty(eventName = "防守篮板",eventCode = "reboundDefense")
    private CommonItem reboundDefense;

    @ScoresProperty(eventName = "进攻篮板",eventCode = "reboundAttack")
    private CommonItem reboundAttack;

    @ScoresProperty(eventName = "控球率",eventCode = "possession")
    private CommonFItem possession;

    @ScoresProperty(eventName = "助攻",eventCode = "assist")
    private CommonItem assist;

    @ScoresProperty(eventName = "失误",eventCode = "turnover")
    private CommonItem turnover;

    @ScoresProperty(eventName = "犯规",eventCode ={"foul"})
    private CommonItem foul ;

    @ScoresProperty(eventName = "暂停",eventCode ={"timeout"})
    private CommonItem timeout ;

    @ScoresProperty(eventName = "抢断",eventCode ={"steal"})
    private CommonItem steal ;

    @ScoresProperty(eventName = "盖帽",eventCode ={"block"})
    private CommonItem block ;

    @ScoresProperty(eventName = "赢得跳球",eventCode ={"won_jump_ball"})
    private CommonItem wonJumpBall ;

    @ScoresProperty(eventName = "控球权",eventCode = "ball_possession")
    private CommonItem ballPossession;

    public void updateEvent(MatchEventInfo data, Map<Long, BasketballScores> periodFootballScores){
        BasketballScores periodScores=periodFootballScores.get(data.getMatchPeriodId());
        if(periodScores==null){
            periodScores = new BasketballScores(data.getMatchPeriodId());
        }
        if(data.getEventCode().equals("period_score")){
            Integer addHome =data.getT1()-periodScores.matchScore.getHome(); //39-13 =26
            Integer addAway =data.getT2()-periodScores.matchScore.getAway();
            periodScores.matchScore.setHome(data.getT1());
            periodScores.matchScore.setAway(data.getT2());
            this.matchScore.setHome(matchScore.getHome()+ addHome);//-24+39 =15
            this.matchScore.setAway(matchScore.getAway()+ addAway);
        }
        if(data.getEventCode().equals("possession")){
            if(this.possession==null){
                this.possession = new CommonFItem();
            }
            if(data.getT1()!=null && data.getT2()!=null ){
                this.possession.setHome(data.getT1()*1.0f/(data.getT1()+data.getT2()));//-24+39 =15
                this.possession.setAway(data.getT2()*1.0f/(data.getT1()+data.getT2()));
            }
        }
        if(data.getEventCode().equals("score_change") || data.getEventCode().equals("score_correction")){
            if(data.getFirstT1()==null || data.getFirstT2()==null){
                log.info("阶段:"+data.getMatchPeriodId()+" setFieldByEventCode,linkId={},比分数据异常，阶段比分为空",data.getLinkId());
                return;
            }
            if(data.getFirstT1()<periodScores.matchScore.getHome() || data.getFirstT2()<periodScores.matchScore.getAway()){
                if(StringUtils.isNotEmpty(data.getAddition3())){
                    if(!Objects.equals(data.getAddition3(), "home") && !Objects.equals(data.getAddition3(), "away")){
                        Long lastTime = new Long(data.getAddition3());
                        log.info("阶段:"+data.getMatchPeriodId()+" setFieldByEventCode,linkId={},lastTime={},eventTime={}",data.getLinkId(),lastTime,data.getEventTime());
                        if(lastTime > data.getEventTime()){
                            //针对中途开售补发事件，消费数据顺序错误问题添加校验，如果已保存比分比事件比分大，则不处理当前事件比分
                            log.info("阶段:"+data.getMatchPeriodId()+" setFieldByEventCode,linkId="+data.getLinkId()+",消费顺序有误:事件比分：" +
                                    ""+data.getFirstT1()+"-"+data.getFirstT2()+",已有比分："+periodScores.matchScore.doCountScoreStr());
                            return;
                        }
                    }
                }
            }
            //由于加减 生产环境事件不稳定，改用 直接使用盘比分作为 节比分
            periodScores.matchScore.setHome(data.getFirstT1()!=null?data.getFirstT1():periodScores.matchScore.getHome());
            periodScores.matchScore.setAway(data.getFirstT2()!=null?data.getFirstT2():periodScores.matchScore.getAway());
            if(periodScores.matchScore.getHome()<0){
                periodScores.matchScore.setHome(0);
            }
            if(periodScores.matchScore.getAway()<0){
                periodScores.matchScore.setAway(0);
            }
            this.matchScore.setHome(data.getT1());
            this.matchScore.setAway(data.getT2());
            log.info(":{}:updateEvent设置篮球事件比分，总分：{},阶段{}：{}",data.getLinkId(),this.matchScore,data.getMatchPeriodId(),periodScores.matchScore);
            if("2".equals(data.getExtraInfo())){
                if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                    twoPointerMade.setHome(twoPointerMade.getHome()+1);
                    periodScores.twoPointerMade.setHome(periodScores.twoPointerMade.getHome()+1);
                    twoPointer.setHome(twoPointer.getHome()+1);
                    periodScores.twoPointer.setHome(periodScores.twoPointer.getHome()+1);
                }else {
                    twoPointerMade.setAway(twoPointerMade.getAway()+1);
                    periodScores.twoPointerMade.setAway(periodScores.twoPointerMade.getAway()+1);
                    twoPointer.setAway(twoPointer.getAway()+1);
                    periodScores.twoPointer.setAway(periodScores.twoPointer.getAway()+1);
                }
            }
            if("3".equals(data.getExtraInfo())){
                if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                    threePointerMade.setHome(threePointerMade.getHome()+1);
                    periodScores.threePointerMade.setHome(periodScores.threePointerMade.getHome()+1);
                    threePointer.setHome(threePointer.getHome()+1);
                    periodScores.threePointer.setHome(periodScores.threePointer.getHome()+1);
                }else {
                    threePointerMade.setAway(threePointerMade.getAway()+1);
                    periodScores.threePointerMade.setAway(periodScores.threePointerMade.getAway()+1);
                    threePointer.setAway(threePointer.getAway()+1);
                    periodScores.threePointer.setAway(periodScores.threePointer.getAway()+1);
                }
            }
            if("1".equals(data.getExtraInfo())){
                if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                    freeThrowMade.setHome(freeThrowMade.getHome()+1);
                    periodScores.freeThrowMade.setHome(periodScores.freeThrowMade.getHome()+1);
                    freeThrowCount.setHome(freeThrowCount.getHome()+1);
                    periodScores.freeThrowCount.setHome(periodScores.freeThrowCount.getHome()+1);
                }else {
                    freeThrowMade.setAway(freeThrowMade.getAway()+1);
                    periodScores.freeThrowMade.setAway(periodScores.freeThrowMade.getAway()+1);
                    freeThrowCount.setAway(freeThrowCount.getAway()+1);
                    periodScores.freeThrowCount.setAway(periodScores.freeThrowCount.getAway()+1);
                }
            }
        }
        if(data.getEventCode().equals("rebound")){
            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                rebound.setHome(rebound.getHome()+1);
                periodScores.rebound.setHome(periodScores.rebound.getHome()+1);
            }else {
                rebound.setAway(rebound.getAway()+1);
                periodScores.rebound.setAway(periodScores.rebound.getAway()+1);
            }
        }
        if(data.getEventCode().equals("foul")){
            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                foul.setHome(foul.getHome()+1);
                periodScores.foul.setHome(periodScores.foul.getHome()+1);
            }else {
                foul.setAway(foul.getAway()+1);
                periodScores. foul.setAway(periodScores.foul.getAway()+1);
            }

        }
        if(data.getEventCode().equals("timeout")){
            if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                timeout.setHome(timeout.getHome()+1);
                periodScores.timeout.setHome(periodScores.timeout.getHome()+1);
            }else {
                timeout.setAway(timeout.getAway()+1);
                periodScores.timeout.setAway(periodScores.timeout.getAway()+1);
            }
        }

        if(data.getEventCode().equals("score_miss")){
            if("1".equals(data.getExtraInfo())){
                if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                    freeThrowMiss.setHome(freeThrowMiss.getHome()+1);
                    periodScores.freeThrowMiss.setHome(periodScores.freeThrowMiss.getHome()+1);
                    freeThrowCount.setHome(freeThrowCount.getHome()+1);
                    periodScores.freeThrowCount.setHome(periodScores.freeThrowCount.getHome()+1);
                }else {
                    freeThrowMiss.setAway(freeThrowMiss.getAway()+1);
                    periodScores.freeThrowMiss.setAway(periodScores.freeThrowMiss.getAway()+1);
                    freeThrowCount.setAway(freeThrowCount.getAway()+1);
                    periodScores.freeThrowCount.setAway(periodScores.freeThrowCount.getAway()+1);
                }
            }else if("2".equals(data.getExtraInfo())){
                if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                    twoPointerMiss.setHome(twoPointerMiss.getHome()+1);
                    periodScores.twoPointerMiss.setHome(periodScores.twoPointerMiss.getHome()+1);
                    twoPointer.setHome(twoPointer.getHome()+1);
                    periodScores.twoPointer.setHome(periodScores.twoPointer.getHome()+1);
                }else {
                    twoPointerMiss.setAway(twoPointerMiss.getAway()+1);
                    periodScores.twoPointerMiss.setAway(periodScores.twoPointerMiss.getAway()+1);
                    twoPointer.setAway(twoPointer.getAway()+1);
                    periodScores.twoPointer.setAway(periodScores.twoPointer.getAway()+1);
                }
            }else if("3".equals(data.getExtraInfo())){
                if(data.getHomeAway().equals(TeamTypeConstant.HOME)){
                    threePointerMiss.setHome(threePointerMiss.getHome()+1);
                    periodScores.threePointerMiss.setHome(periodScores.threePointerMiss.getHome()+1);
                    threePointer.setHome(threePointer.getHome()+1);
                    periodScores.threePointer.setHome(periodScores.threePointer.getHome()+1);
                }else {
                    threePointerMiss.setAway(threePointerMiss.getAway()+1);
                    periodScores.threePointerMiss.setAway(periodScores.threePointerMiss.getAway()+1);
                    threePointer.setAway(threePointer.getAway()+1);
                    periodScores.threePointer.setAway(periodScores.threePointer.getAway()+1);
                }
            }
        }

    }

    public void createScores(MatchEventInfo data, Map<Long, BasketballScores> periodFootballScores){
        BasketballScores periodScores= new BasketballScores(data.getMatchPeriodId());
        Integer firstT1 = data.getFirstT1();
        Integer firstT2 = data.getFirstT2();

        if(data.getEventCode().equals("score_change")){
            //由于加减 生产环境事件不稳定，改用 直接使用盘比分作为 节比分
            periodScores.matchScore.setHome(data.getFirstT1()!=null?data.getFirstT1():periodScores.matchScore.getHome());
            periodScores.matchScore.setAway(data.getFirstT2()!=null?data.getFirstT2():periodScores.matchScore.getAway());
            if(periodScores.matchScore.getHome()<0){
                periodScores.matchScore.setHome(0);
            }
            if(periodScores.matchScore.getAway()<0){
                periodScores.matchScore.setAway(0);
            }
            this.matchScore.setHome(data.getT1());
            this.matchScore.setAway(data.getT2());
        }

    }
    public void cancelEvent(MatchEventInfo oldMatchInfo, MatchEventInfo data, Map<Long, BasketballScores> periodFootballScores){
        BasketballScores periodScores=periodFootballScores.get(data.getMatchPeriodId());
        //1.分数纠正
        if(oldMatchInfo.getEventCode().equals("score_change")){
            periodScores.matchScore.setHome(data.getFirstT1()!=null?data.getFirstT1():periodScores.matchScore.getHome());
            periodScores.matchScore.setAway(data.getFirstT2()!=null?data.getFirstT2():periodScores.matchScore.getAway());
            if(periodScores.matchScore.getHome()<0){
                periodScores.matchScore.setHome(0);
            }
            if(periodScores.matchScore.getAway()<0){
                periodScores.matchScore.setAway(0);
            }
            this.matchScore.setHome(data.getT1());
            this.matchScore.setAway(data.getT2());

            if("2".equals(oldMatchInfo.getExtraInfo())){
                if(oldMatchInfo.getHomeAway().equals(TeamTypeConstant.HOME)){
                    twoPointerMade.setHome(twoPointerMade.getHome()-1);
                    periodScores.twoPointerMade.setHome(periodScores.twoPointerMade.getHome()-1);
                    twoPointer.setHome(twoPointer.getHome()-1);
                    periodScores.twoPointer.setHome(periodScores.twoPointer.getHome()-1);
                }else {
                    twoPointerMade.setAway(twoPointerMade.getAway()-1);
                    periodScores.twoPointerMade.setAway(periodScores.twoPointerMade.getAway()-1);
                    twoPointer.setAway(twoPointer.getAway()-1);
                    periodScores.twoPointer.setAway(periodScores.twoPointer.getAway()-1);
                }
            }
            if("3".equals(oldMatchInfo.getExtraInfo())){
                if(oldMatchInfo.getHomeAway().equals(TeamTypeConstant.HOME)){
                    threePointerMade.setHome(threePointerMade.getHome()-1);
                    periodScores.threePointerMade.setHome(periodScores.threePointerMade.getHome()-1);
                    threePointer.setHome(threePointer.getHome()-1);
                    periodScores.threePointer.setHome(periodScores.threePointer.getHome()-1);
                }else {
                    threePointerMade.setAway(threePointerMade.getAway()-1);
                    periodScores.threePointerMade.setAway(periodScores.threePointerMade.getAway()-1);
                    threePointer.setAway(threePointer.getAway()-1);
                    periodScores.threePointer.setAway(periodScores.threePointer.getAway()-1);
                }
            }
            if("1".equals(oldMatchInfo.getExtraInfo())){
                if(oldMatchInfo.getHomeAway().equals(TeamTypeConstant.HOME)){
                    freeThrowMade.setHome(freeThrowMade.getHome()-1);
                    periodScores.freeThrowMade.setHome(periodScores.freeThrowMade.getHome()-1);

                    freeThrowCount.setHome(freeThrowCount.getHome()-1);
                    periodScores.freeThrowCount.setHome(periodScores.freeThrowCount.getHome()-1);
                }else {
                    freeThrowMade.setAway(freeThrowMade.getAway()-1);
                    periodScores.freeThrowMade.setAway(periodScores.freeThrowMade.getAway()-1);

                    freeThrowCount.setAway(freeThrowCount.getAway()-1);
                    periodScores.freeThrowCount.setAway(periodScores.freeThrowCount.getAway()-1);
                }
            }
        }
        if("foul".equals(data.getEventCode())) {
            if(data.getHomeAway()==null){
                log.info("数据异常，犯规事件无主客队：{}",data.getLinkId());
                return;
            }
            if(TeamTypeEnum.HOME.getCode().equals(data.getHomeAway())){
                foul.setHome(foul.getHome()-1);
                periodScores.foul.setHome(periodScores.foul.getHome()-1);
            }else if(TeamTypeEnum.AWAY.getCode().equals(data.getHomeAway())){
                foul.setAway(foul.getAway()-1);
                periodScores.foul.setAway(periodScores.foul.getAway()-1);
            }
            log.info("删除犯规次数记录：{}，{}，{}",data.getLinkId(),foul,periodScores.foul);
        }

    }
    public void doCalculation(){
//        this.freeThrowCount.setHome(freeThrowMade.getHome()+freeThrowMiss.getHome());
//        this.freeThrowCount.setAway(freeThrowMade.getAway()+freeThrowMiss.getAway());
//        this.twoPointer.setHome(twoPointerMade.getHome()+twoPointerMiss.getHome());
//        this.twoPointer.setAway(twoPointerMade.getAway()+twoPointerMiss.getAway());
//        this.threePointer.setHome(threePointerMade.getHome()+threePointerMiss.getHome());
//        this.threePointer.setAway(threePointerMade.getAway()+threePointerMiss.getAway());
        if(freeThrowCount.getHome()!=0){
            this.freeThrowHitRate.setHome(freeThrowMade.getHome()*1000/freeThrowCount.getHome()/10f);
        }
        if(freeThrowCount.getAway()!=0) {
            this.freeThrowHitRate.setAway(freeThrowMade.getAway() * 1000 / freeThrowCount.getAway() / 10f);
        }
        if(twoPointer.getHome()!=0){
            this.twoPointerHitRate.setHome(twoPointerMade.getHome()*1000/twoPointer.getHome()/10f);
        }
        if(twoPointer.getAway()!=0){
            this.twoPointerHitRate.setAway(twoPointerMade.getAway()*1000/twoPointer.getAway()/10f);
        }
        if(threePointer.getHome()!=0){
            this.threePointerHitRate.setHome(threePointerMade.getHome()*1000/threePointer.getHome()/10f);
        }
        if(threePointer.getAway()!=0){
            this.threePointerHitRate.setAway(threePointerMade.getAway()*1000/threePointer.getAway()/10f);
        }
        if( (twoPointer.getHome() + threePointer.getHome()) > 0 ) {
            this.pointerHitRate.setHome( (twoPointerMade.getHome() + threePointerMade.getHome())*1000 / (twoPointer.getHome() + threePointer.getHome())/10f );
        }
        if( (twoPointer.getAway() + threePointer.getAway()) > 0 ) {
            this.pointerHitRate.setAway( (twoPointerMade.getAway() + threePointerMade.getAway())*1000 / (twoPointer.getAway() + threePointer.getAway())/10f );
        }
    }


    public BasketballScores(Long periodId) {
        super.init(this);
    }
    public BasketballScores(){}

//    public  CommonItem doCalculation(String eventCode , Long period, Map<Long, BasketballScores> periodFootballScores )  {
//        //1.判断阶段是否是全局 是则直接返回
//        if(period.equals(SportPeriodConstant.SportPeriod.WHOLE_MATCH)){
//            return null;
//        }
//        // 2. 获取全部分数
//        BasketballScores footballScores=periodFootballScores.get(SportPeriodConstant.SportPeriod.WHOLE_MATCH);
//        //3. 判断所求阶段 是 上下半场 还是 加时赛等
//        Integer index= SportPeriodConstant.FootballPeriod.getIndexByPeriod(period);
//        //非开局数据
//        if(index<0){
//            return null;
//        }
//        BasketballScores periodScores=periodFootballScores.get(period);
//        CommonItem wholeItem=footballScores.getEventScores(eventCode);
//        if(wholeItem==null){
//            log.error("wholeItem 事件找不到属性:"+eventCode);
//            return null;
//        }
//        CommonItem periodItem=periodScores.getEventScores(eventCode);
//        if(periodItem==null){
////            periodItem=new CommonItem();
//            log.error("periodItem 事件找不到属性:"+eventCode);
//            return null;
//        }
//        Integer home =wholeItem.getHome();
//        Integer away =wholeItem.getAway();
//        //4. 根据所求阶段计算
//        for(int i=0;i<index;i++){
//            Long periodDelete= SportPeriodConstant.FootballPeriod.WHOLE_PERIODS[i];
//            if(periodDelete==null|| periodDelete.equals(SportPeriodConstant.SportPeriod.WHOLE_MATCH) || periodDelete.equals(period)){
//                continue;
//            }
//            BasketballScores periodDeleteScores=periodFootballScores.get(periodDelete);
//            if(periodDeleteScores==null){
//                continue;
//            }
//            CommonItem periodDeleteItem=periodDeleteScores.getEventScores(eventCode);
//            if(periodDeleteItem==null){
//                log.error("事件periodDeleteScores 阶段找不到属性"+periodDelete+":"+eventCode);
//                continue;
//            }
//            home =home -periodDeleteItem.getHome();
//            away= away -periodDeleteItem.getAway();
//        }
//        periodItem.setHome(home);
//        periodItem.setAway(away);
//
//        return periodItem;
//    }

//    public void saveStatisticsInfo(MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO, Map<Long, BasketballScores> allPeriodScores) {
//        BasketballScores wholeScore=allPeriodScores.get(SportPeriodConstant.SportPeriod.WHOLE_MATCH);
//        CommonItem periodItem=this.getEventScores(matchStatisticsInfoDetailDTO.getCode());
//        CommonItem wholeScoreItem=wholeScore.getEventScores(matchStatisticsInfoDetailDTO.getCode());
//        if(periodItem==null){
//            log.error("事件periodDeleteScores 阶段找不到属性"+":"+matchStatisticsInfoDetailDTO.getCode());
//            return;
//        }
//        Integer addHome =matchStatisticsInfoDetailDTO.getT1()-wholeScoreItem.getHome();
//        Integer addAway =matchStatisticsInfoDetailDTO.getT2()-wholeScoreItem.getAway();
//        wholeScoreItem.setHome(matchStatisticsInfoDetailDTO.getT1());
//        wholeScoreItem.setAway(matchStatisticsInfoDetailDTO.getT2());
//        periodItem.setHome(periodItem.getHome()+addHome);
//        periodItem.setAway(periodItem.getAway()+addAway);
//    }
    public void saveStatisticsInfo(String code, Integer t1, Integer t2) {
        CommonItem periodDeleteItem=this.getEventScores(code);
        if(periodDeleteItem==null){
            log.error("事件periodDeleteScores 阶段找不到属性"+":"+code);
            return;
        }
        periodDeleteItem.setHome(t1);
        periodDeleteItem.setAway(t2);
    }

//        public  static  void main(String []  xx) {
////            Long nextPeriod=SportPeriodConstant.BasketballPeriod.getNextPeriod(13L,0);
//
//            String datajson ="{\"canceled\":0,\"createTime\":1731727232107,\"dataSourceCode\":\"F01\",\"eventCode\":\"score_change\",\"eventTime\":1731727230520,\"extraInfo\":\"3\",\"firstT1\":5,\"firstT2\":10,\"homeAway\":\"away\",\"id\":1857624766995369986,\"linkId\":\"F01_0af51951202411160320320149128\",\"matchPeriodId\":13,\"modifyTime\":1731727232107,\"secondT1\":5,\"secondT2\":10,\"secondsFromStart\":500,\"sendData\":\"N\",\"sourceType\":1,\"sportId\":2,\"standardMatchId\":3720307,\"standardTeamId\":14852,\"t1\":5,\"t2\":10,\"thirdEventId\":\"dcd06a2c-234b-4635-bba0-cd7e0d23751d\",\"thirdMatchId\":1848160194528960514,\"thirdMatchSourceId\":\"52537089\",\"thirdTeamId\":1840623228295843841}";
//            MatchEventInfo matchEventInfo= JSON.parseObject(datajson,MatchEventInfo.class);
//            String jsonScore = "{\"13\":{\"twoPointer\":{\"away\":3,\"home\":3},\"steal\":{\"away\":0,\"home\":0},\"twoPointerMiss\":{\"away\":1,\"home\":3},\"possession\":{\"away\":0.0,\"home\":0.0},\"reboundDefense\":{\"away\":0,\"home\":0},\"timeout\":{\"away\":0,\"home\":0},\"foul\":{\"away\":0,\"home\":3},\"rebound\":{\"away\":4,\"home\":6},\"block\":{\"away\":0,\"home\":0},\"threePointerMiss\":{\"away\":3,\"home\":2},\"freeThrowMade\":{\"away\":3,\"home\":0},\"reboundAttack\":{\"away\":0,\"home\":0},\"turnover\":{\"away\":0,\"home\":0},\"freeThrowHitRate\":{\"away\":75.0,\"home\":0.0},\"freeThrowMiss\":{\"away\":1,\"home\":0},\"freeThrowCount\":{\"away\":4,\"home\":0},\"threePointerHitRate\":{\"away\":0.0,\"home\":50.0},\"pointerHitRate\":{\"away\":33.3,\"home\":28.5},\"wonJumpBall\":{\"away\":0,\"home\":0},\"twoPointerHitRate\":{\"away\":66.6,\"home\":0.0},\"twoPointerMade\":{\"away\":2,\"home\":0},\"ballPossession\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":7,\"home\":6},\"threePointer\":{\"away\":3,\"home\":4},\"threePointerMade\":{\"away\":0,\"home\":2},\"assist\":{\"away\":0,\"home\":0}},\"-1\":{\"twoPointer\":{\"away\":3,\"home\":3},\"steal\":{\"away\":0,\"home\":0},\"twoPointerMiss\":{\"away\":1,\"home\":3},\"possession\":{\"away\":0.45454547,\"home\":0.54545456},\"reboundDefense\":{\"away\":0,\"home\":0},\"timeout\":{\"away\":0,\"home\":0},\"foul\":{\"away\":0,\"home\":3},\"rebound\":{\"away\":4,\"home\":6},\"block\":{\"away\":0,\"home\":0},\"threePointerMiss\":{\"away\":3,\"home\":2},\"freeThrowMade\":{\"away\":3,\"home\":0},\"reboundAttack\":{\"away\":0,\"home\":0},\"turnover\":{\"away\":0,\"home\":0},\"freeThrowHitRate\":{\"away\":75.0,\"home\":0.0},\"freeThrowMiss\":{\"away\":1,\"home\":0},\"freeThrowCount\":{\"away\":4,\"home\":0},\"threePointerHitRate\":{\"away\":0.0,\"home\":50.0},\"pointerHitRate\":{\"away\":33.3,\"home\":28.5},\"wonJumpBall\":{\"away\":0,\"home\":0},\"twoPointerHitRate\":{\"away\":66.6,\"home\":0.0},\"twoPointerMade\":{\"away\":2,\"home\":0},\"ballPossession\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":7,\"home\":6},\"threePointer\":{\"away\":3,\"home\":4},\"threePointerMade\":{\"away\":0,\"home\":2},\"assist\":{\"away\":0,\"home\":0}},\"1312\":{\"twoPointer\":{\"away\":0,\"home\":0},\"steal\":{\"away\":0,\"home\":0},\"twoPointerMiss\":{\"away\":0,\"home\":0},\"possession\":{\"away\":0.0,\"home\":0.0},\"reboundDefense\":{\"away\":0,\"home\":0},\"timeout\":{\"away\":0,\"home\":0},\"foul\":{\"away\":0,\"home\":0},\"rebound\":{\"away\":0,\"home\":0},\"block\":{\"away\":0,\"home\":0},\"threePointerMiss\":{\"away\":0,\"home\":0},\"freeThrowMade\":{\"away\":0,\"home\":0},\"reboundAttack\":{\"away\":0,\"home\":0},\"turnover\":{\"away\":0,\"home\":0},\"freeThrowHitRate\":{\"away\":0.0,\"home\":0.0},\"freeThrowMiss\":{\"away\":0,\"home\":0},\"freeThrowCount\":{\"away\":0,\"home\":0},\"threePointerHitRate\":{\"away\":0.0,\"home\":0.0},\"pointerHitRate\":{\"away\":0.0,\"home\":0.0},\"wonJumpBall\":{\"away\":0,\"home\":0},\"twoPointerHitRate\":{\"away\":0.0,\"home\":0.0},\"twoPointerMade\":{\"away\":0,\"home\":0},\"ballPossession\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":7,\"home\":6},\"threePointer\":{\"away\":0,\"home\":0},\"threePointerMade\":{\"away\":0,\"home\":0},\"assist\":{\"away\":0,\"home\":0}}}";
//            JSONObject periodBasketballScores = JSONObject.parseObject(jsonScore);
//            System.out.println(periodBasketballScores);
//            Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodBasketballScores);
//            BasketballScores sixScores= allPeriodScores.get(matchEventInfo.getMatchPeriodId());
//            System.out.println("----"+sixScores.getMatchScore());
//
//            sixScores.set6MinuteFieldByEventCode(matchEventInfo,allPeriodScores);
//            System.out.println("总比分："+JSON.toJSONString(allPeriodScores));
//
//
//    }


    public void periodScoresChange(MatchEventInfo data, Map<Long, BasketballScores> allPeriodScores,Integer matchLength) {
        this.getMatchScore().setHome(data.getT1());
        this.getMatchScore().setAway(data.getT2());
        //修正当前阶段比分
        BasketballScores periodScores= allPeriodScores.get(data.getMatchPeriodId());
        periodScores.getMatchScore().setHome(data.getFirstT1());
        periodScores.getMatchScore().setAway(data.getFirstT2());

    }

//    public void changeScoreByPdEventCodeAndHomeAway(String homeAway, String eventCode) {
//        if("reboundDefense".equals(eventCode)){
//            if(homeAway.equals("home")){
//                reboundDefense.setHome(reboundDefense.getHome()+1);
//                rebound.setHome(rebound.getHome()+1);
//            }else {
//                reboundDefense.setAway(reboundDefense.getAway()+1);
//                rebound.setAway(rebound.getAway()+1);
//            }
//            return;
//        }
//        if("reboundAttack".equals(eventCode)){
//            if(homeAway.equals("home")){
//                reboundAttack.setHome(reboundAttack.getHome()+1);
//                rebound.setHome(rebound.getHome()+1);
//            }else {
//                reboundAttack.setAway(reboundAttack.getAway()+1);
//                rebound.setAway(rebound.getAway()+1);
//            }
//            return;
//        }
//
//        CommonItem commonItem  = this.getCommonItemByEventCode(eventCode);
//        if(homeAway.equals("home")){
//            commonItem.setHome(commonItem.getHome()+1);
//        }else {
//            commonItem.setAway(commonItem.getAway()+1);
//        }
//
//    }

    private CommonItem getCommonItemByEventCode(String eventCode) {
        if(eventCode.equals("assist")){
            return assist;
        }
        if(eventCode.equals("turnover")){
            return turnover;
        }
        if(eventCode.equals("foul")){
            return foul;
        }
        if(eventCode.equals("timeout")){
            return timeout;
        }
        if(eventCode.equals("steal")){
            return steal;
        }
        if(eventCode.equals("block")){
            return block;
        }
        if(eventCode.equals("won_jump_ball")){
            return wonJumpBall;
        }
        if(eventCode.equals("ball_possession")){
            return ballPossession;
        }
        return null;
    }

    //1 未命中
    //2.命中
    public void changeScoreBysendBallDto(PDBasketBallSendBallDto sendBallDto) {
        if(sendBallDto.getBallEventType()!=2){
            return;
        }
        if(sendBallDto.getBallEventType()==2){
            if("home".equals( sendBallDto.getHomeAway())){
                matchScore.setHome(matchScore.getHome()+sendBallDto.getScore());
            }else {
                matchScore.setAway(matchScore.getAway()+sendBallDto.getScore());
            }
        }
    }

    /**
     * 罚球、2分、3分球 命中未命中：1 未命中，2 命中
     *
     * @param sendBallDto 入参
     */
    public void hitTimesOrNot(PDBasketBallSendBallDto sendBallDto) {
        if ("home".equals(sendBallDto.getHomeAway())) {
            if (sendBallDto.isFreeThrow()) {
                freeThrowCount.setHome(freeThrowCount.getHome() + sendBallDto.getFreeThrowNumber());
            }
            if (!sendBallDto.isFreeThrow() && sendBallDto.getScore() == 2) {
                twoPointer.setHome(twoPointer.getHome() + 1);
            }
            if (!sendBallDto.isFreeThrow() && sendBallDto.getScore() == 3) {
                threePointer.setHome(threePointer.getHome() + 1);
            }
        } else {
            if (sendBallDto.isFreeThrow()) {
                freeThrowCount.setAway(freeThrowCount.getAway() + sendBallDto.getFreeThrowNumber());
            }
            if (!sendBallDto.isFreeThrow() && sendBallDto.getScore() == 2) {
                twoPointer.setAway(twoPointer.getAway() + 1);
            }
            if (!sendBallDto.isFreeThrow() && sendBallDto.getScore() == 3) {
                threePointer.setAway(threePointer.getAway() + 1);
            }
        }
        if (sendBallDto.getBallEventType() == 2) {
            if ("home".equals(sendBallDto.getHomeAway())) {
                if (sendBallDto.isFreeThrow()) {
                    freeThrowMade.setHome(freeThrowMade.getHome() + sendBallDto.getScore());
                }
                if (!sendBallDto.isFreeThrow() && sendBallDto.getScore() == 2) {
                    twoPointerMade.setHome(twoPointerMade.getHome() + 1);
                }
                if (!sendBallDto.isFreeThrow() && sendBallDto.getScore() == 3) {
                    threePointerMade.setHome(threePointerMade.getHome() + 1);
                }
            } else {
                if (sendBallDto.isFreeThrow()) {
                    freeThrowMade.setAway(freeThrowMade.getAway() + sendBallDto.getScore());
                }
                if (!sendBallDto.isFreeThrow() && sendBallDto.getScore() == 2) {
                    twoPointerMade.setAway(twoPointerMade.getAway() + 1);
                }
                if (!sendBallDto.isFreeThrow() && sendBallDto.getScore() == 3) {
                    threePointerMade.setAway(threePointerMade.getAway() + 1);
                }
            }
        }
    }

    /**
     * 删除操作，减掉 进/总 球数量
     *
     * @param homeAway 主客队
     * @param score    分数
     */
    public void minusBallCount(String homeAway, Integer score, MatchScoresEventInfo matchScoresEventInfo, String eventCodeOld) {
        String eventCode = matchScoresEventInfo.getEventCode();
        String inputScore = matchScoresEventInfo.getAddition5();
        boolean flag = "score_miss".equals(eventCode) || "3p_miss".equals(eventCode) || "2p_miss".equals(eventCode);
        if ("home".equals(homeAway) && ("score_change".equals(eventCode) || flag) && ("score_change".equals(eventCodeOld) || "delete".equals(eventCodeOld)) && null == inputScore) {
            switch (score) {
                case 1:
                    if ("score_miss".equals(eventCode) && "score_change".equals(eventCodeOld)) {
                        freeThrowMade.setHome(Math.max(freeThrowMade.getHome() - 1, 0));
                    } else {
                        freeThrowMade.setHome(Math.max(freeThrowMade.getHome() - 1, 0));
                        freeThrowCount.setHome(Math.max(freeThrowCount.getHome() - 1, 0));
                    }
                    break;
                case 2:
                    twoPointer.setHome(Math.max(twoPointer.getHome() - 1, 0));
                    twoPointerMade.setHome(Math.max(twoPointerMade.getHome() - 1, 0));
                    break;
                case 3:
                    threePointer.setHome(Math.max(threePointer.getHome() - 1, 0));
                    threePointerMade.setHome(Math.max(threePointerMade.getHome() - 1, 0));
                    break;
                default:
                    return;
            }
        }
        if ("away".equals(homeAway) && ("score_change".equals(eventCode) || flag) && ("score_change".equals(eventCodeOld) || "delete".equals(eventCodeOld)) && null == inputScore) {
            switch (score) {
                case 1:
                    if ("score_miss".equals(eventCode) && "score_change".equals(eventCodeOld)) {
                        freeThrowMade.setAway(Math.max(freeThrowMade.getAway() - 1, 0));
                    } else {
                        freeThrowMade.setAway(Math.max(freeThrowMade.getAway() - 1, 0));
                        freeThrowCount.setAway(Math.max(freeThrowCount.getAway() - 1, 0));
                    }
                    break;
                case 2:
                    twoPointer.setAway(Math.max(twoPointer.getAway() - 1, 0));
                    twoPointerMade.setAway(Math.max(twoPointerMade.getAway() - 1, 0));
                    break;
                case 3:
                    threePointer.setAway(Math.max(threePointer.getAway() - 1, 0));
                    threePointerMade.setAway(Math.max(threePointerMade.getAway() - 1, 0));
                    break;
                default:
                    return;
            }
        }
        boolean oldFlag = "score_miss".equals(eventCodeOld) || "3p_miss".equals(eventCodeOld) || "2p_miss".equals(eventCodeOld);
        if ("home".equals(homeAway) && oldFlag && null == inputScore) {
            switch (score) {
                case 1:
                    freeThrowCount.setHome(Math.max(freeThrowCount.getHome() - 1, 0));
                    break;
                case 2:
                    twoPointer.setHome(Math.max(twoPointer.getHome() - 1, 0));
                    break;
                case 3:
                    threePointer.setHome(Math.max(threePointer.getHome() - 1, 0));
                    break;
                default:
                    return;
            }
        }
        if ("away".equals(homeAway) && oldFlag && null == inputScore) {
            switch (score) {
                case 1:
                    freeThrowCount.setHome(Math.max(freeThrowCount.getAway() - 1, 0));
                    break;
                case 2:
                    twoPointer.setHome(Math.max(twoPointer.getAway() - 1, 0));
                    break;
                case 3:
                    threePointer.setHome(Math.max(threePointer.getAway() - 1, 0));
                    break;
                default:
                    return;
            }
        }
        if ("home".equals(homeAway) && null != inputScore) {
            String[] split = inputScore.split(" - ");
            freeThrowMade.setHome(Math.max(freeThrowMade.getHome() - Integer.parseInt(split[0]), 0));
            freeThrowCount.setHome(Math.max(freeThrowCount.getHome() - Integer.parseInt(split[1]), 0));
        }
        if ("away".equals(homeAway) && null != inputScore) {
            String[] split = inputScore.split(" - ");
            freeThrowMade.setAway(Math.max(freeThrowMade.getAway() - Integer.parseInt(split[0]), 0));
            freeThrowCount.setAway(Math.max(freeThrowCount.getAway() - Integer.parseInt(split[1]), 0));
        }
    }

    /**
     * 1.根据code设置参数比分
     * */
    public boolean set6MinuteFieldByEventCode(MatchEventInfo data, Map<Long, BasketballScores> allPeriodScores) {
        BasketballScores wholeScore = allPeriodScores.get(WHOLE_MATCH);

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
                        if (field.get(this) == null) {
                            log.error("thirdMatchId:{} setFieldByEventCode:{} 找不到属性", data.getThirdMatchId(), data.getEventCode());
                            return false;
                        }
                        CommonItem commonItem = (com.panda.merge.dto.CommonItem) field.get(this);
                        CommonItem wholeItem = (com.panda.merge.dto.CommonItem) field.get(wholeScore);
                        log.info(" thirdMatchId:{} ,eventCode:{},before: home:{},away:{}", data.getThirdMatchId(), data.getEventCode(), wholeItem.getHome(), wholeItem.getAway());
                        //求差值
                        Integer addH = data.getT1() - wholeItem.getHome();
                        Integer addW = data.getT2() - wholeItem.getAway();
                        if (commonItem.getHome() + addH < 0) {
                            commonItem.setHome(0);
                        } else {
                            commonItem.setHome(commonItem.getHome() + addH);
                        }
                        if (commonItem.getAway() + addW < 0) {
                            commonItem.setAway(0);
                        } else {
                            commonItem.setAway(commonItem.getAway() + addW);
                        }
                        log.info(" thirdMatchId:{} ,eventCode:{},after: home:{},away:{}", data.getThirdMatchId(), data.getEventCode(), commonItem.getHome(), commonItem.getAway());
                        return true;
                    }
                }
            } catch (IllegalAccessException e) {
                log.error(":处理数据发生异常:", e);
            } catch (Exception e) {
                log.error(":处理数据发生异常:", e);
            }
        }
        return false;
    }

    /**
     * 更新阶段进球命中率
     *
     * @param period 阶段
     */
    public void updateScoreRate(BasketballScores period) {
        // 罚球命中率
        if (ObjectUtils.isEmpty(period.getFreeThrowHitRate())) {
            period.setFreeThrowHitRate(new CommonFItem());
        }
        if (period.getFreeThrowCount().getHome() == 0) {
            period.getFreeThrowHitRate().setHome(0);
        } else {
            period.getFreeThrowHitRate().setHome(Math.round((double) period.getFreeThrowMade().getHome() / period.getFreeThrowCount().getHome() * 100));
        }
        if (period.getFreeThrowCount().getAway() == 0) {
            period.getFreeThrowHitRate().setAway(0);
        } else {
            period.getFreeThrowHitRate().setAway(Math.round((double) period.getFreeThrowMade().getAway() / period.getFreeThrowCount().getAway() * 100));
        }
        // 2分球命中率
        if (ObjectUtils.isEmpty(period.getTwoPointerHitRate())) {
            period.setTwoPointerHitRate(new CommonFItem());
        }
        if (period.getTwoPointer().getHome() == 0) {
            period.getTwoPointerHitRate().setHome(0);
        } else {
            period.getTwoPointerHitRate().setHome(Math.round((double) period.getTwoPointerMade().getHome() / period.getTwoPointer().getHome() * 100));
        }
        if (period.getTwoPointer().getAway() == 0) {
            period.getTwoPointerHitRate().setAway(0);
        } else {
            period.getTwoPointerHitRate().setAway(Math.round((double) period.getTwoPointerMade().getAway() / period.getTwoPointer().getAway() * 100));
        }
        // 3分球命中率
        if (ObjectUtils.isEmpty(period.getThreePointerHitRate())) {
            period.setThreePointerHitRate(new CommonFItem());
        }
        if (period.getThreePointer().getHome() == 0) {
            period.getThreePointerHitRate().setHome(0);
        } else {
            period.getThreePointerHitRate().setHome(Math.round((double) period.getThreePointerMade().getHome() / period.getThreePointer().getHome() * 100));
        }
        if (period.getThreePointer().getAway() == 0) {
            period.getThreePointerHitRate().setAway(0);
        } else {
            period.getThreePointerHitRate().setAway(Math.round((double) period.getThreePointerMade().getAway() / period.getThreePointer().getAway() * 100));
        }
        if (ObjectUtils.isEmpty(this.pointerHitRate)) {
            this.pointerHitRate = new CommonFItem();
        }
        if( (twoPointer.getHome() + threePointer.getHome()) > 0 ) {
            this.pointerHitRate.setHome( (twoPointerMade.getHome() + threePointerMade.getHome())*1000 / (twoPointer.getHome() + threePointer.getHome())/10f );
        }
        if( (twoPointer.getAway() + threePointer.getAway()) > 0 ) {
            this.pointerHitRate.setAway( (twoPointerMade.getAway() + threePointerMade.getAway())*1000 / (twoPointer.getAway() + threePointer.getAway())/10f );
        }
    }
}
