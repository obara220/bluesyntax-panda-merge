package com.panda.merge.calculation.impl;


import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.dto.MatchStatisticsInfoDetailDTO;
import com.panda.merge.dto.SnookerScores;
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
 * 斯诺克比分计算并入库
 */
@Slf4j
@Service
public class SnookerCalculationServiceImpl extends AbstractCalculationServiceImpl {

    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;

    /**
     * 比分处理
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    @Override
    public void calculationMatchScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {

        String scoreStr = matchScoresInfo.getScoresJson();
        if (StringUtils.isEmpty(scoreStr)) {
            createScores(matchScoresInfo, data);
        } else {
            updateScores(matchScoresInfo, data);
        }
        updateHighestSingleShot(matchScoresInfo,data);
    }

    /**
     * 更新最高单发球
     * @param matchScoresInfo
     * @param data
     */
    private void updateHighestSingleShot(MatchScoresInfo matchScoresInfo, MatchEventInfo data){
        String highShotStr= data.getAddition2();
        if(StringUtils.isEmpty(highShotStr)){
            return;
        }
        Integer high = null;
        try {
            high = Integer.parseInt(highShotStr);
        }catch (Exception e){
            return;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, SnookerScores> allPeriodScores= JsonMapUtils.parseSnookerMap(periodFootballScores);
        SnookerScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        SnookerScores periodScores= allPeriodScores.get(data.getFirstNum()+0l);
        if(periodScores==null){
            periodScores =new SnookerScores();
            allPeriodScores.put(data.getFirstNum()+0l,periodScores);
        }
        wholeSores.updateHighestSingleShot(high,data.getHomeAway());
        periodScores.updateHighestSingleShot(high,data.getHomeAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 更新比分
     * @param matchScoresInfo
     * @param data
     */
    private void updateScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        if(!isEffectEvent(data)){
            return;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, SnookerScores> allPeriodScores= JsonMapUtils.parseSnookerMap(periodFootballScores);
        SnookerScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(wholeSores==null|| data.getFirstNum()==null){
            log.error("updateScores wholeSores==null|| data.getFirstNum()==null ThirdMatchSourceId:"+data.getThirdMatchSourceId()+"matchid:"+matchScoresInfo.getThirdMatchId());
            return;
        }
        SnookerScores periodScores= allPeriodScores.get(data.getFirstNum()+0l);
        if(periodScores==null){
            periodScores =new SnookerScores();
            allPeriodScores.put(data.getFirstNum()+0l,periodScores);
        }
        periodScores.doCalculation(data,wholeSores);
//        wholeSores.doCalculation(data);
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(periodScores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getSetScore().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
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
        Map<Long, SnookerScores> allPeriodScores= JsonMapUtils.parseSnookerMap(periodFootballScores);
        SnookerScores oldSores= allPeriodScores.get(data.getFirstNum()+0l);
        oldSores.cancelCalculation( data,data,allPeriodScores);
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 校验事件编码
     * @param data
     * @return
     */
    private boolean isEffectEvent(MatchEventInfo data){
//        if(data.getEventCode().equals("snooker_score_change")||data.getEventCode().equals("ball_possession")){
//            return true;
//        }
//        if(data.getEventCode().equals("snooker_foul")){
//            return true;
//        }
        return true;
    }

    /**
     * 初始化比分
     * @param matchScoresInfo
     * @param data
     */
    private void createScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        Map<Long, SnookerScores> scoresHashMap= new HashMap<>();
        SnookerScores badmintonScores=new SnookerScores();
        scoresHashMap.put(WHOLE_MATCH,badmintonScores);
        scoresHashMap.put(data.getFirstNum()+0l,badmintonScores);

//        badmintonScores.doCalculation(data,badmintonScores);
        //3.更新比分模板
        scoresHashMap.put(data.getFirstNum()+0l,((JSONObject) JSONObject.toJSON(badmintonScores)).toJavaObject(SnookerScores.class));

        matchScoresInfo.setScoresJson(JSONObject.toJSONString(scoresHashMap));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        log.info("createScores 成功"+data.getEventCode()+"事件ID:"+data.getThirdEventId());
    }

    /**
     * 保存比分统计
     * @param matchScoresInfo
     * @param data
     */
    @Override
    public void saveMatchStatisticsScores(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data, StandardMatchInfo standardMatchInfo) {
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
     * 更新比分统计
     * @param matchScoresInfo
     * @param data
     */
    private void createMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        Map<Long, SnookerScores> periodFootballScores= new HashMap<>();
        SnookerScores tennisScores=new SnookerScores();
        periodFootballScores.put(WHOLE_MATCH,tennisScores);
        //更新赛事比分表
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 保存比分统计
     * @param matchScoresInfo
     * @param data
     */
    private void saveMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        //保存比分
        if(data.getMatchStatisticsInfoDetailList()==null){
            log.error("createMatchStatistics data:null");
            return;
        }
        //1.得到阶段map 转化的
        JSONObject periodBasketballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, SnookerScores> allPeriodScores= JsonMapUtils.parseSnookerMap(periodBasketballScores);
        //改当前阶段的盘比分以及总的盘比分
        SnookerScores wholeSores= allPeriodScores.get(WHOLE_MATCH.longValue());
        Long maxPeriodId =0l;
        for (MatchStatisticsInfoDetailDTO dto : data.getMatchStatisticsInfoDetailList()) {
            if(dto.getCode().equals("set_score")){
                if(dto.getFirstNum()==null){
                    continue;
                }
                //改对应阶段的局比分
                if(maxPeriodId<dto.getFirstNum().longValue()){
                    maxPeriodId = dto.getFirstNum().longValue();
                }
                SnookerScores setScores=allPeriodScores.get(dto.getFirstNum().longValue());
                if(setScores==null){
                    setScores= new SnookerScores();
                    allPeriodScores.put(dto.getFirstNum().longValue(),setScores);
                }
                setScores.getSetScore().setHome(dto.getT1());
                setScores.getSetScore().setAway(dto.getT2());

            }else if(dto.getCode().equals("match_score")){
                wholeSores.getMatchScore().setHome(dto.getT1());
                wholeSores.getMatchScore().setAway(dto.getT2());
                matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
                matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());

            }
        }
        SnookerScores setScores=allPeriodScores.get(maxPeriodId);
        if(setScores!=null){
            wholeSores.getSetScore().setHome(setScores.getSetScore().getHome());
            wholeSores.getSetScore().setAway(setScores.getSetScore().getAway());
            matchScoresInfo.setPeriodT1(setScores.getSetScore().getHome());
            matchScoresInfo.setPeriodT2(setScores.getSetScore().getAway());
        }
        //2.变更入库
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);

    }

}
