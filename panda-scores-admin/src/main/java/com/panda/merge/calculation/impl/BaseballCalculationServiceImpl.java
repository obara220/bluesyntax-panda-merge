package com.panda.merge.calculation.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.BaseballScores;
import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.dto.MatchStatisticsInfoDetailDTO;
import com.panda.merge.mapper.MatchScoresSpecialEventMapper;
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
 * 棒球比分计算并入库
 */
@Slf4j
@Service
public class BaseballCalculationServiceImpl extends AbstractCalculationServiceImpl {

//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
    //    @Autowired
//    MatchEventInfoMapper matchEventInfoMapper;
    @Autowired
    MatchScoresSpecialEventMapper matchScoresSpecialEventMapper;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    /**
     *棒球比分处理
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
     * 修改比分
     * @param matchScoresInfo
     * @param data
     */
    private void updateScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BaseballScores> allPeriodScores= JsonMapUtils.parseBaseballMap(periodFootballScores);
        BaseballScores wholeSores= allPeriodScores.get(WHOLE_MATCH.longValue());
        //阶段变更要清楚垒位
        if(data.getEventCode().equals("match_status")){
            wholeSores.cleanBase();
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
//            matchScoresInfo.setModifyTime(System.currentTimeMillis());
//            matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        }
        if(!SportPeriodConstant.BaseballPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        if(wholeSores==null|| data.getMatchPeriodId()==null){
            log.error("updateScores wholeSores==null|| data.getMatchPeriodId()==null ThirdMatchSourceId:"+data.getThirdMatchSourceId()+"matchid:"+matchScoresInfo.getThirdMatchId());
            return;
        }
        BaseballScores periodScores= allPeriodScores.get(data.getMatchPeriodId().longValue());
        if(periodScores == null){
            periodScores = new BaseballScores();
            allPeriodScores.put(data.getMatchPeriodId(),periodScores);
            log.info("{}::棒球新建阶段比分:{},{}",data.getLinkId(),data.getMatchPeriodId(),periodScores);
        }
        if(data.getEventCode().equals("baseball_stats_correction")){
            periodScores.correctionEvent(data,allPeriodScores);
        }else {
            wholeSores.updateEvent(data,allPeriodScores);
        }

        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(wholeSores.getSetScore().getHome());
        matchScoresInfo.setPeriodT2(wholeSores.getSetScore().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 取消事件下发
     * @param matchScoresInfo
     * @param data
     * @param isAgain
     * @throws Exception
     */
    @Override
    public void cancelEvent(MatchScoresInfo matchScoresInfo, MatchEventInfo data , boolean isAgain,Boolean isReissue) throws Exception {
        if(!data.getEventCode().equals("run_scored")){
            return;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BaseballScores> allPeriodScores= JsonMapUtils.parseBaseballMap(periodFootballScores);
        BaseballScores oldSores= allPeriodScores.get(data.getMatchPeriodId());
        BaseballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        wholeSores.cancelCalculation( data,oldSores);

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
        if(!SportPeriodConstant.BaseballPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        Map<Long, BaseballScores> allPeriodScores= new HashMap<>();
        BaseballScores badmintonScores=new BaseballScores();

        allPeriodScores.put(WHOLE_MATCH,badmintonScores);
        allPeriodScores.put(data.getMatchPeriodId(),badmintonScores);
        badmintonScores.updateEvent(data,allPeriodScores);
        //3.更新比分模板
        allPeriodScores.put(data.getMatchPeriodId(),((JSONObject) JSONObject.toJSON(badmintonScores)).toJavaObject(BaseballScores.class));
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        log.info("createScores 创建棒球比分成功"+data.getEventCode()+"事件ID:"+data.getThirdEventId()+",linkId:"+data.getLinkId());
    }

    /**
     * 保存赛事统计比分
     * @param matchScoresInfo
     * @param data
     */
    @Override
    public void saveMatchStatisticsScores(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data, StandardMatchInfo standardMatchInfo) {
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            createMatchStatistics(matchScoresInfo,data);
        }else {
            //2.如果存在则覆盖值
            saveMatchStatistics(matchScoresInfo,data);
        }
    }

    /**
     * 初始化比分统计
     * @param matchScoresInfo
     * @param data
     */
    private void createMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        Map<Long, BaseballScores> periodFootballScores= new HashMap<>();
        BaseballScores baseballScores=new BaseballScores();
        periodFootballScores.put(WHOLE_MATCH,baseballScores);
        //更新赛事比分表
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 保存赛事统计比分
     * @param matchScoresInfo
     * @param data
     */
    private void saveMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        //保存比分
        if(data.getMatchStatisticsInfoDetailList()==null){
            log.error("createMatchStatistics data:null");
            return;
        }
        //109122 【日常】【生产】棒球统计比分 比赛没有进入加时赛时 比分中心下发加时比分0:0
        if(DataSourceCodeEnum.SR.code.equals(data.getDataSourceCode()) && data.getPeriod()==999L){
            log.info("::保存赛事统计比分,棒球统计比分暂不处理999的比分变更:{}",data);
            return;
        }
        //1.得到阶段map 转化的
        JSONObject periodBasketballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BaseballScores> allPeriodScores= JsonMapUtils.parseBaseballMap(periodBasketballScores);
        BaseballScores periodScores=allPeriodScores.get(data.getPeriod().longValue());
        //改当前阶段的盘比分以及总的盘比分
        BaseballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(periodScores==null){
            periodScores= new BaseballScores();
            allPeriodScores.put(data.getPeriod().longValue(),periodScores);
        }
        //适用的阶段
        Long[] periodArr =    new Long[]{ 402L,  404L,  406L,  408L, 410L, 412L,
                414L,  416L,  418L,  42010L, 42011L, 42012L, 42013L,
                42014L, 42015L, 42016L,  42017L,42018L, 42019L,
                42020L};
        for (MatchStatisticsInfoDetailDTO dto : data.getMatchStatisticsInfoDetailList()) {

            if(dto.getCode().equals("match_score")){
                Integer addHome = dto.getT1() -wholeSores.getMatchScore().getHome();
                Integer addAway = dto.getT2() -wholeSores.getMatchScore().getAway();
                wholeSores.getMatchScore().setHome(dto.getT1());
                wholeSores.getMatchScore().setAway(dto.getT2());
                periodScores.getMatchScore().setHome(periodScores.getMatchScore().getHome()+addHome);
                periodScores.getMatchScore().setAway(periodScores.getMatchScore().getAway()+addAway);
                matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
                matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
                //测试
                periodScores.getSetScore().setHome(periodScores.getSetScore().getHome()+addHome);
                periodScores.getSetScore().setAway(periodScores.getSetScore().getAway()+addAway);
                wholeSores.getSetScore().setHome( dto.getT1());
                wholeSores.getSetScore().setAway( dto.getT2());
            }
        }
        //UOF 棒球 上下半场拆解
        for (MatchStatisticsInfoDetailDTO dto : data.getMatchStatisticsInfoDetailList()) {
            if(dto.getCode().equals("set_score")){
                if(dto.getFirstNum()<=periodArr.length-1){
                    Long periodId = periodArr[dto.getFirstNum()-1];
                    BaseballScores periodScore=allPeriodScores.get(periodId);
                    if(periodScore==null){
                        periodScore=new BaseballScores();
                        allPeriodScores.put(periodId,periodScore);
                    }
                    periodScore.getSetScore().setHome( dto.getT1());
                    periodScore.getSetScore().setAway( dto.getT2());
                }
            }
            if(dto.getCode().equals("extra_time_score")){
                if(dto.getFirstNum()<=periodArr.length-1){
                    Long periodId = 42010L;
                    BaseballScores periodScore=allPeriodScores.get(periodId);
                    if(periodScore==null){
                        periodScore=new BaseballScores();
                        allPeriodScores.put(periodId,periodScore);
                    }
                    periodScore.getSetScore().setHome( dto.getT1());
                    periodScore.getSetScore().setAway( dto.getT2());
                }
            }
        }
        //2.变更入库
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);

    }

}
