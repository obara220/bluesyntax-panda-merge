package com.panda.merge.calculation.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.dto.MatchStatisticsInfoDetailDTO;
import com.panda.merge.dto.CricketBallScores;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.StandardMatchInfoRepository;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/** 板球比分计算并入库
 *
 * @author fymen
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2024-11-05
 * @see com.panda.merge.calculation.impl
 */

@Slf4j
@Service
public class CricketCalculationServiceImpl extends AbstractCalculationServiceImpl {
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    StandardMatchInfoRepository standardMatchInfoRepository;
    /**
     * 比分计算入库
     * @param matchScoresInfo
     * @param data
     * @throws Exception
    */
    @Override
    public void calculationMatchScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        log.info("板球比分计算：calculationMatchScores:{}",data.getLinkId());
        if(!SportPeriodConstant.CricketPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        //发球保存轮数
        if("delivery".equals(data.getEventCode())){
            matchScoresInfo.setScoresJsonExtra(data.getAddition1());
        }
        String scoreStr = matchScoresInfo.getScoresJson();
        if (StringUtils.isEmpty(scoreStr)) {
            createScores(matchScoresInfo, data);
        } else {
            updateScores(matchScoresInfo, data);
        }
    }

    /**
     * 更新比分
     * @param matchScoresInfo
     * @param data
     */
    private void updateScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, CricketBallScores> allPeriodScores= JsonMapUtils.parseCricketMap(periodFootballScores);
        CricketBallScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(wholeSores==null|| data.getMatchPeriodId()==null){
            log.error("updateScores wholeSores==null|| data.getMatchPeriodId()==null ThirdMatchSourceId:"+data.getThirdMatchSourceId()+"matchid:"+matchScoresInfo.getThirdMatchId());
            return;
        }
        changeMatchStatus(data);
        CricketBallScores periodScores= allPeriodScores.get(data.getMatchPeriodId());
        if(periodScores==null){
            periodScores =new CricketBallScores();
            periodScores.setOver(new HashMap<>());
            allPeriodScores.put(data.getMatchPeriodId(),periodScores);
        }
        periodScores.doCalculation(data,allPeriodScores);
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(periodScores.getMatchScore().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getMatchScore().getAway());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 装配事件编码和阶段
     * @param data
     */
    private void changeMatchStatus(MatchEventInfo data) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(data.getStandardMatchId());

        //下游需要301 302的比分，这边照着阶段下发
        if(data.getEventCode().equals("match_status")){
            if(data.getMatchPeriodId().equals(301L)){
//                data.setMatchPeriodId(8L);
                data.setEventCode("delivery");
            }
            if(data.getMatchPeriodId().equals(302L) || data.getMatchPeriodId().equals(100L) || data.getMatchPeriodId().equals(999L)){
//                if(standardMatchInfo.getMatchLength()==5){
//                    data.setMatchPeriodId(9L);
//                }
                data.setEventCode("delivery");
            }
        }
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
        Map<Long, CricketBallScores> allPeriodScores= JsonMapUtils.parseCricketMap(periodFootballScores);
        CricketBallScores oldSores= allPeriodScores.get(data.getMatchPeriodId());
        oldSores.cancelCalculation( data,data,allPeriodScores);
        CricketBallScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        //发球保存轮数
        if("delivery".equals(data.getEventCode())){
            matchScoresInfo.setScoresJsonExtra(data.getAddition1());
        }
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 更新比分
     * @param matchScoresInfo
     * @param data
     */
    private void createScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        Map<Long, CricketBallScores> scoresHashMap= new HashMap<>();
        CricketBallScores badmintonScores=new CricketBallScores();
        scoresHashMap.put(WHOLE_MATCH,badmintonScores);
        scoresHashMap.put(data.getMatchPeriodId(),badmintonScores);

        badmintonScores.doCalculation(data,scoresHashMap);
        //3.更新比分模板
        scoresHashMap.put(data.getMatchPeriodId(),((JSONObject) JSONObject.toJSON(badmintonScores)).toJavaObject(CricketBallScores.class));
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(scoresHashMap));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
        matchScoresInfo.setT1(badmintonScores.getMatchScore().getHome());
        matchScoresInfo.setT2(badmintonScores.getMatchScore().getAway());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        log.info("板球比分计算 createScores 成功"+data.getEventCode()+"事件ID:"+data.getThirdEventId());
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
     * 初始化统计比分
     * @param matchScoresInfo
     * @param data
     */
    private void createMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        Map<Long, CricketBallScores> periodFootballScores= new HashMap<>();
        CricketBallScores cricketBallScores=new CricketBallScores();
        periodFootballScores.put(WHOLE_MATCH,cricketBallScores);

        CricketBallScores  periodScores= new CricketBallScores();
        periodFootballScores.put(data.getPeriod().longValue(),periodScores);
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
        Map<Long, CricketBallScores> allPeriodScores= JsonMapUtils.parseCricketMap(periodBasketballScores);
        CricketBallScores periodScores=allPeriodScores.get((long) data.getPeriod());
        //改当前阶段的盘比分以及总的盘比分
        CricketBallScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(periodScores==null){
            periodScores= new CricketBallScores();
            allPeriodScores.put((long) data.getPeriod(),periodScores);
        }
        for (MatchStatisticsInfoDetailDTO dto : data.getMatchStatisticsInfoDetailList()) {
            //阶段
            if(dto.getCode().equals("match_score")){
                wholeSores.getMatchScore().setHome(dto.getT1());
                wholeSores.getMatchScore().setAway(dto.getT2());
            }else if(dto.getCode().equals("game_score")){
                if(dto.getSecondNum()!=null){
                    Long periodId=  SportPeriodConstant.CricketPeriod.getWholePeriodsByMatchLength()[dto.getSecondNum()-1];
                    CricketBallScores periodScore=allPeriodScores.get(periodId);
                    if(periodScore==null){
                        periodScore = new CricketBallScores();
                        allPeriodScores.put(periodId,periodScore);
                    }
                    periodScore.getMatchScore().setHome(dto.getT1());
                    periodScore.getMatchScore().setAway(dto.getT2());
                }
            }else if(dto.getCode().equals("over")){
                if(dto.getSecondNum()!=null) {
                    Long periodId = SportPeriodConstant.CricketPeriod.getWholePeriodsByMatchLength()[dto.getSecondNum() - 1];
                    CricketBallScores periodScore = allPeriodScores.get(periodId);
                    if (periodScore == null) {
                        return;
                    }
                    Map<Integer, CommonItem> overMap = periodScore.getOver();
                    if (overMap == null) {
                        overMap = new HashMap<>();
                        periodScore.setOver(overMap);
                    }
                    if (overMap.containsKey(dto.getSecondNum())) {
                        overMap.get(dto.getSecondNum()).setHome(dto.getT1());
                        overMap.get(dto.getSecondNum()).setAway(dto.getT2());
                    } else {
                        CommonItem com = new CommonItem();
                        com.setHome(dto.getT1());
                        com.setAway(dto.getT2());
                        overMap.put(dto.getSecondNum(), com);
                    }
                    periodScore.setOver(overMap);
                    log.info("板球统计获取轮比分：{}，{}", overMap, periodScore);
                }
            }
        }
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);

    }
}
