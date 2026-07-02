package com.panda.merge.calculation.impl;


import com.alibaba.fastjson.JSONObject;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.*;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.panda.merge.constant.EffectScoresCode.RED_CARD;
import static com.panda.merge.constant.EffectScoresCode.YELLOW_RED_CARD;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * 手球比分中心计算
 */
@Slf4j
@Service
public class HandballCalculationServiceImpl extends AbstractCalculationServiceImpl {

//    @Autowired
//    MatchScoresInfoDetailCommonMapper matchScoresInfoDetailCommonMapper;
//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
//    @Autowired
//    MatchEventInfoMapper matchEventInfoMapper;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    /**
     * 计算赛事比分
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    @Override
    public void calculationMatchScores( MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        //是否是赛事比赛阶段
        //1.根据event_code 计算 当前事件
        String scoreStr=matchScoresInfo.getScoresJson();
        //没数据的情况
        if(StringUtils.isEmpty(scoreStr)){
            createScores(matchScoresInfo,data);
        }else {
            //1.全阶段比分计算 判断该阶段数据是否存在，不存在则提供数据
            updateScores(matchScoresInfo,data);

        }
    }


    /**
     * 更新比分
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    private void updateScores( MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        if(!SportPeriodConstant.HandBallPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, HandballScores> allPeriodScores= JsonMapUtils.parseHandballMap(periodFootballScores);
        HandballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);

        // 2黄一红特别处理
        if(data.getEventCode().equals(YELLOW_RED_CARD)){
            data.setEventCode(RED_CARD);
        }
        if(wholeSores==null|| data.getMatchPeriodId()==null){
            log.error("updateScores wholeSores==null|| data.getMatchPeriodId()==null ThirdMatchSourceId:"+data.getThirdMatchSourceId()+"matchid:"+matchScoresInfo.getThirdMatchId());
            return;
        }
        HandballScores periodScores= allPeriodScores.get(data.getMatchPeriodId());
        //新建该阶段值
        if(periodScores==null) {
            periodScores = new HandballScores(data.getMatchPeriodId());
        }
        allPeriodScores.put(data.getMatchPeriodId(), periodScores);
        if(data.getMatchPeriodId().equals(50l)){
            if(data.getEventCode().equals("goal")){
                periodScores.getGoal().setHome(data.getT1());
                periodScores.getGoal().setAway(data.getT2());
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
//                matchScoresInfo.setModifyTime(System.currentTimeMillis());
//                matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
            }
            return;
        }
        periodScores.updateScores(data,wholeSores);
        matchScoresInfo.setT1(wholeSores.getGoal().getHome());
        matchScoresInfo.setT2(wholeSores.getGoal().getAway());
        matchScoresInfo.setPeriodT1(periodScores.getGoal().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getGoal().getAway());
        //当前阶段新增事件值 或者设置当前事件值

        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 比分json转map 方便数据组装
     * @param sjon
     * @return
     */
    public   Map<String,CommonItem> buildMatchScoreByMap(String sjon){
        if(StringUtils.isEmpty(sjon)){
            return new HashMap<>();
        }
        JSONObject periodFootballScores = JSONObject.parseObject(sjon);
        Map<Long, HandballScores> allPeriodScores= JsonMapUtils.parseHandballMap(periodFootballScores);
        //.定义要求结果
        Map<String,CommonItem> matchScore =new HashMap<>();
        //1.求全场数据
        CommonItem whole =new CommonItem();
        //2.求当前半场数据
        CommonItem period =new CommonItem();
        //3.求加时赛数据
        CommonItem overtime =new CommonItem();
        //4.求点球大战数据
        CommonItem penalty =new CommonItem();
        //5.组装返回string
        Long periodKey= 0l;
        for (Map.Entry<Long, HandballScores> entry : allPeriodScores.entrySet()) {
            if(entry.getKey().equals(6l)||entry.getKey().equals(7l)){
                whole.setHome(whole.getHome()+entry.getValue().getGoal().getHome());
                whole.setAway(whole.getAway()+entry.getValue().getGoal().getAway());
                if(periodKey<entry.getKey()){
                    periodKey=entry.getKey();
                    period.setHome(entry.getValue().getGoal().getHome());
                    period.setAway(entry.getValue().getGoal().getAway());
                }
            }
            if(entry.getKey().equals(41L)||entry.getKey().equals(42L)){
                overtime.setHome(overtime.getHome()+entry.getValue().getGoal().getHome());
                overtime.setAway(overtime.getAway()+entry.getValue().getGoal().getAway());
                matchScore.put("overtimeScore",overtime);
            }
        }
        matchScore.put("wholeScore",whole);
        matchScore.put("periodScore",period);
        HandballScores wholeSores= allPeriodScores.get(WHOLE_MATCH.longValue());
        if(wholeSores!=null&&allPeriodScores.get(50L)!=null){
            matchScore.put("penaltyShootout",allPeriodScores.get(50L).getGoal());
        }
        return matchScore;
    }

    /**
     * 创建比分对象
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    private void createScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        if(data.getEventCode().equals(YELLOW_RED_CARD)){
            data.setEventCode(RED_CARD);
        }
        //破发事件计算
        if(!SportPeriodConstant.HandBallPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        Map<Long, HandballScores> periodFootballScores= new HashMap<>();
        HandballScores footballScores=new HandballScores(data.getMatchPeriodId());
        periodFootballScores.put(WHOLE_MATCH,footballScores);
        periodFootballScores.put(data.getMatchPeriodId(),footballScores);

        matchScoresInfo.setT1(footballScores.getGoal().getHome());
        matchScoresInfo.setT2(footballScores.getGoal().getAway());
        matchScoresInfo.setPeriodT1(footballScores.getGoal().getHome());
        matchScoresInfo.setPeriodT2(footballScores.getGoal().getAway());
        //3.更新比分模板
        periodFootballScores.put(data.getMatchPeriodId(),((JSONObject) JSONObject.toJSON(footballScores)).toJavaObject(HandballScores.class));

        matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        log.info("createScores 成功"+data.getEventCode()+"事件ID:"+data.getThirdEventId());
        //4.返回成功
//        footballScores.setFieldByEventCode(data.getEventCode(),data.)  计算方式通过配置实现
    }


    /**
     * 事件取消下发
     * @param matchScoresInfo
     * @param data
     * @param isAgain
     * @throws Exception
     */
    @Override
    public void cancelEvent(MatchScoresInfo matchScoresInfo, MatchEventInfo data , boolean isAgain,Boolean isReissue) throws Exception {
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, HandballScores> allPeriodScores= JsonMapUtils.parseHandballMap(periodFootballScores);
        HandballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        HandballScores oldSores= allPeriodScores.get(data.getMatchPeriodId());
        if(wholeSores==null||oldSores==null){
            log.error("canleEvent 当前取消事件还未被统计:wholeSores==null||oldSores==null EventCode:"+data.getEventCode()+"事件ID:"+data.getThirdEventId());
            throw new Exception("canleEvent 当前取消事件还未被统计:wholeSores==null||oldSores==null |EventCode："+data.getEventCode());
        }
        if(data.getMatchPeriodId().equals(50l)){
            if(data.getEventCode().equals("goal")){
                oldSores.getGoal().setHome(data.getT1());
                oldSores.getGoal().setAway(data.getT2());
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
//                matchScoresInfo.setModifyTime(System.currentTimeMillis());
//                matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
            }
            return;
        }
        //1.先取消全局
        oldSores.updateScores(data,wholeSores);

        matchScoresInfo.setT1(wholeSores.getGoal().getHome());
        matchScoresInfo.setT2(wholeSores.getGoal().getAway());
        matchScoresInfo.setPeriodT1(oldSores.getGoal().getHome());
        matchScoresInfo.setPeriodT2(oldSores.getGoal().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }


    /**
     * 保存赛事统计比分
     * @param matchScoresInfo
     * @param data
     */
    @Override
    public void saveMatchStatisticsScores(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data, StandardMatchInfo standardMatchInfo) {
        if(!SportPeriodConstant.FootballPeriod.contans(data.getPeriod().longValue())){
            return;
        }
        //1.查询数据库的阶段值是否存在
        //1.1 查询 matchScoresInfo 的 json 是否存在 不存在则新建
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            createMatchStatistics(matchScoresInfo,data);
        }else {
            //2.如果存在则覆盖值
            saveMatchStatistics(matchScoresInfo,data);
        }
    }

    private void saveMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {


    }

    private void createMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {

    }


}
