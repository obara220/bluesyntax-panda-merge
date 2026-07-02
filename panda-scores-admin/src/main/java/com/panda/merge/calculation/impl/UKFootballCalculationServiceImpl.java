package com.panda.merge.calculation.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.dto.UKFootballScores;
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
 * 橄榄球比分计算
 */
@Slf4j
@Service
public class UKFootballCalculationServiceImpl extends AbstractCalculationServiceImpl {

//    @Autowired
//    MatchScoresInfoDetailCommonMapper matchScoresInfoDetailCommonMapper;
//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
////    @Autowired
////    MatchEventInfoMapper matchEventInfoMapper;
//    @Autowired
//    MatchScoresSpecialEventMapper matchScoresSpecialEventMapper;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    /**
     * 比分计算
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    @Override
    public void calculationMatchScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {

        String scoreStr=matchScoresInfo.getScoresJson();
        if(StringUtils.isEmpty(scoreStr)){
            createScores(matchScoresInfo,data);
        }else {
            updateScores(matchScoresInfo,data);
        }
    }

    /**
     * 更新比分
     * @param matchScoresInfo
     * @param data
     */
    private void updateScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        if(!SportPeriodConstant.UKFootballPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long,UKFootballScores> allPeriodScores= JsonMapUtils.parseUKFootballMap(periodFootballScores);
        UKFootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);

        if(wholeSores==null|| data.getMatchPeriodId()==null){
            log.error("updateScores wholeSores==null|| data.getMatchPeriodId()==null ThirdMatchSourceId:"+data.getThirdMatchSourceId()+"matchid:"+matchScoresInfo.getThirdMatchId());
            return;
        }
        UKFootballScores periodScores= allPeriodScores.get(data.getMatchPeriodId());
        if(periodScores==null){
            periodScores =new UKFootballScores();
            allPeriodScores.put(data.getMatchPeriodId(),periodScores);
        }

        periodScores.updateScores(data,allPeriodScores);

        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(periodScores.getMatchScore().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getMatchScore().getAway());
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
        if(!SportPeriodConstant.UKFootballPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        JSONObject allscores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long,UKFootballScores> allPeriodScores= JsonMapUtils.parseUKFootballMap(allscores);
        UKFootballScores oldSores= allPeriodScores.get(data.getMatchPeriodId());

        oldSores.updateScores(data,allPeriodScores);
        UKFootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(oldSores.getMatchScore().getHome());
        matchScoresInfo.setPeriodT2(oldSores.getMatchScore().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 初始化比分
     * @param matchScoresInfo
     * @param data
     */
    private void createScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        Map<Long, UKFootballScores> allPeriodScores= new HashMap<>();
        UKFootballScores americanFootballScores=new UKFootballScores();
        allPeriodScores.put(WHOLE_MATCH,americanFootballScores);
        allPeriodScores.put(data.getMatchPeriodId(),americanFootballScores);

        //3.更新比分模板
        allPeriodScores.put(data.getMatchPeriodId(),((JSONObject) JSONObject.toJSON(americanFootballScores)).toJavaObject(UKFootballScores.class));
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());

        matchScoresInfo.setT1(americanFootballScores.getMatchScore().getHome());

        matchScoresInfo.setT2(americanFootballScores.getMatchScore().getAway());

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
        if(!SportPeriodConstant.AmericanFootballPeriod.contans(data.getPeriod().longValue())){
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

    private void createMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {

    }

    private void saveMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {


    }
}
