package com.panda.merge.calculation.impl;


import com.alibaba.fastjson.JSONObject;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.BeachVolleyballScores;
import com.panda.merge.dto.MatchStatisticsInfoDTO;
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

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * 沙滩排球 比分计算入库处理
 */
@Slf4j
@Service
public class BeachVolleyballCalculationServiceImpl extends AbstractCalculationServiceImpl {

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
        if(!SportPeriodConstant.BeachVolleyballPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BeachVolleyballScores> allPeriodScores= JsonMapUtils.parseBeachVolleyBallMap(periodFootballScores);
        BeachVolleyballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);

        if(wholeSores==null|| data.getMatchPeriodId()==null){
            log.error("updateScores wholeSores==null|| data.getMatchPeriodId()==null ThirdMatchSourceId:"+data.getThirdMatchSourceId()+"matchid:"+matchScoresInfo.getThirdMatchId());
            return;
        }
        BeachVolleyballScores periodScores= allPeriodScores.get(data.getMatchPeriodId());

        //新建该阶段值
        if(periodScores==null) {
            periodScores = new BeachVolleyballScores(data.getMatchPeriodId());
        }
        allPeriodScores.put(data.getMatchPeriodId(), periodScores);

        periodScores.updateScores(data,wholeSores);
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(periodScores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getSetScore().getAway());
        //当前阶段新增事件值 或者设置当前事件值

        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }


    /**
     * 创建比分
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    private void createScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {

        //破发事件计算
        if(!SportPeriodConstant.BeachVolleyballPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        Map<Long, BeachVolleyballScores> periodFootballScores= new HashMap<>();
        BeachVolleyballScores footballScores=new BeachVolleyballScores(data.getMatchPeriodId());
        periodFootballScores.put(WHOLE_MATCH,footballScores);
        periodFootballScores.put(data.getMatchPeriodId(),footballScores);

        matchScoresInfo.setT1(footballScores.getMatchScore().getHome());
        matchScoresInfo.setT2(footballScores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(footballScores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(footballScores.getSetScore().getAway());
        //3.更新比分模板
        periodFootballScores.put(data.getMatchPeriodId(),((JSONObject) JSONObject.toJSON(footballScores)).toJavaObject(BeachVolleyballScores.class));

        matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        log.info("createScores 创建沙排比分成功"+data.getEventCode()+"事件ID:"+data.getThirdEventId()+",linkId:"+data.getLinkId());
        //4.返回成功
//        footballScores.setFieldByEventCode(data.getEventCode(),data.)  计算方式通过配置实现
    }


    /**
     * 事件取消
     * @param matchScoresInfo
     * @param data
     * @param isAgain
     * @throws Exception
     */
    @Override
    public void cancelEvent(MatchScoresInfo matchScoresInfo, MatchEventInfo data , boolean isAgain,Boolean isReissue) throws Exception {
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BeachVolleyballScores> allPeriodScores= JsonMapUtils.parseBeachVolleyBallMap(periodFootballScores);
        BeachVolleyballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        BeachVolleyballScores oldSores= allPeriodScores.get(data.getMatchPeriodId());
        if(wholeSores==null||oldSores==null){
            log.error("canleEvent 当前取消事件还未被统计:wholeSores==null||oldSores==null EventCode:"+data.getEventCode()+"事件ID:"+data.getThirdEventId());
            throw new Exception("canleEvent 当前取消事件还未被统计:wholeSores==null||oldSores==null |EventCode："+data.getEventCode());
        }
        //1.先取消全局
        oldSores.updateScores(data,wholeSores);

        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(oldSores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(oldSores.getSetScore().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }


    /**
     * 保存赛事比分统计
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
    /**
     * 保存赛事比分统计
     * @param matchScoresInfo
     * @param data
     */
    private void saveMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {


    }
    /**
     * 保存赛事比分统计
     * @param matchScoresInfo
     * @param data
     */
    private void createMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {

    }


}
