package com.panda.merge.calculation.impl;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.*;
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
 * 美足比分中心
 */
@Slf4j
@Service
public class AmericanFootballCalculationServiceImpl extends AbstractCalculationServiceImpl {

//    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
//    @Autowired
//    MatchEventInfoMapper matchEventInfoMapper;
    @Autowired
    MatchScoresSpecialEventMapper matchScoresSpecialEventMapper;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;

    /**
     * 计算赛事比分
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    @Override
    public void calculationMatchScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        //校验阶段ID是否合理
        if(!SportPeriodConstant.AmericanFootballPeriod.contans(data.getMatchPeriodId())){
            return;
        }
        String scoreStr=matchScoresInfo.getScoresJson();
        if(StringUtils.isEmpty(scoreStr)){
            createScores(matchScoresInfo,data);
        }else {
            //更新比分
            updateScores(matchScoresInfo,data);
        }
    }

    /**
     * 更新比分
     * @param matchScoresInfo
     * @param data
     */
    private void updateScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, AmericanFootballScores> allPeriodScores= JsonMapUtils.parseAmericanFootballMap(periodFootballScores);
        AmericanFootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);

        if(wholeSores==null|| data.getMatchPeriodId()==null){
            log.error("updateScores wholeSores==null|| data.getMatchPeriodId()==null ThirdMatchSourceId:"+data.getThirdMatchSourceId()+"matchid:"+matchScoresInfo.getThirdMatchId());
            return;
        }
        if(data.getEventCode().equals("match_status")) {
            changePeriodByExtryPeriodEvent(data, allPeriodScores);
            data.setEventCode("field_goal");
        }
        AmericanFootballScores periodScores= allPeriodScores.get(data.getMatchPeriodId());
        if(periodScores==null){
            periodScores = new AmericanFootballScores();
            allPeriodScores.put(data.getMatchPeriodId(),periodScores);
        }
        wholeSores.updateEvent(data,allPeriodScores);
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(periodScores.getMatchScore().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getMatchScore().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    private void changePeriodByExtryPeriodEvent(MatchEventInfo data, Map<Long, AmericanFootballScores> allPeriodScores) {
        if(data.getMatchPeriodId().equals(301l)){
            data.setMatchPeriodId(13l);
        }
        if(data.getMatchPeriodId().equals(302l)){
            data.setMatchPeriodId(14l);
        }
        if(data.getMatchPeriodId().equals(303l)){
            data.setMatchPeriodId(15l);
        }
        if(data.getMatchPeriodId().equals(100L)){
            data.setMatchPeriodId(16l);
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
        Map<Long, AmericanFootballScores> allPeriodScores= JsonMapUtils.parseAmericanFootballMap(periodFootballScores);

        if(!SportPeriodConstant.BasketballPeriod.contans(data.getMatchPeriodId(),matchScoresInfo.getMatchLength())){
            return;
        }

        AmericanFootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        AmericanFootballScores oldSores= allPeriodScores.get(data.getMatchPeriodId());
        if(wholeSores==null||oldSores==null){
            log.error("canleEvent 当前取消事件还未被统计:wholeSores==null||oldSores==null EventCode:"+data.getEventCode()+"事件ID:"+data.getThirdEventId());
            throw new Exception("canleEvent 当前取消事件还未被统计:wholeSores==null||oldSores==null |EventCode："+data.getEventCode());
        }
        wholeSores.cancelCalculation(data,data,allPeriodScores);
//        wholeSores.doCalculation(allPeriodScores,data);
        //入库保存
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
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
        Map<Long, AmericanFootballScores> allPeriodScores= new HashMap<>();
        AmericanFootballScores americanFootballScores=new AmericanFootballScores();
        allPeriodScores.put(WHOLE_MATCH,americanFootballScores);
        allPeriodScores.put(data.getMatchPeriodId(),americanFootballScores);
        americanFootballScores.updateEvent(data,allPeriodScores);
        //3.更新比分模板
        allPeriodScores.put(data.getMatchPeriodId(),((JSONObject) JSONObject.toJSON(americanFootballScores)).toJavaObject(AmericanFootballScores.class));
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());

        matchScoresInfo.setT1(americanFootballScores.getMatchScore().getHome());

        matchScoresInfo.setT2(americanFootballScores.getMatchScore().getAway());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        log.info("createScores 创建美足比分成功"+data.getEventCode()+"事件ID:"+data.getThirdEventId()+",linkId:"+data.getLinkId());
    }

    /**
     * 保存赛事统计比分
     * @param matchScoresInfo
     * @param data
     */
    @Override
    public void saveMatchStatisticsScores(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data, StandardMatchInfo standardMatchInfo) {
        if(!SportPeriodConstant.AmericanFootballPeriod.contans(data.getPeriod()+0l)){
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
     * 初始化比分
     * @param matchScoresInfo
     * @param data
     */
    private void createMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        Map<Long, AmericanFootballScores> periodFootballScores= new HashMap<>();
        AmericanFootballScores tennisScores=new AmericanFootballScores();
        periodFootballScores.put(WHOLE_MATCH,tennisScores);
        //更新赛事比分表
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodFootballScores));
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 保存比分
     * @param matchScoresInfo
     * @param data
     */
    private void saveMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        //保存比分
        if(data.getMatchStatisticsInfoDetailList()==null){
            log.error("createMatchStatistics data:null");;
            return;
        }
        //1.得到阶段map 转化的
        JSONObject periodBasketballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, AmericanFootballScores> allPeriodScores= JsonMapUtils.parseAmericanFootballMap(periodBasketballScores);
        AmericanFootballScores periodScores=allPeriodScores.get(data.getPeriod().longValue());
        //改当前阶段的盘比分以及总的盘比分
        AmericanFootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(periodScores==null){
            periodScores= new AmericanFootballScores();
            allPeriodScores.put(data.getPeriod().longValue(),periodScores);
        }

        for (MatchStatisticsInfoDetailDTO dto : data.getMatchStatisticsInfoDetailList()) {
            if(dto.getCode().equals("set_score")){
                //改对应阶段的局比分
//                Long   periodId=  SportPeriodConstant.TennisPeriod.getWholePeriodsByMatchLength()[dto.getFirstNum()-1];
                Long   periodId=  SportPeriodConstant.AmericanFootballPeriod.WHOLE_PERIODS[dto.getFirstNum()-1];

                AmericanFootballScores setScores=allPeriodScores.get(data.getPeriod().longValue());
                if(setScores==null){
                    setScores= new AmericanFootballScores();
                    if(periodId==data.getPeriod().longValue()){
                        allPeriodScores.put(periodId,setScores);
                        setScores.getMatchScore().setHome(dto.getT1());
                        setScores.getMatchScore().setAway(dto.getT2());
                    }else{
                        setScores.setScores(0,0);
                        allPeriodScores.put(periodId,setScores);
                    }
                }
            }else if(dto.getCode().equals("match_score")){
                Integer addHome = dto.getT1() -wholeSores.getMatchScore().getHome();
                Integer addAway = dto.getT2() -wholeSores.getMatchScore().getAway();
                wholeSores.getMatchScore().setHome(dto.getT1());
                wholeSores.getMatchScore().setAway(dto.getT2());
                periodScores.getMatchScore().setHome(periodScores.getMatchScore().getHome()+addHome);
                periodScores.getMatchScore().setAway(periodScores.getMatchScore().getAway()+addAway);
                matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
                matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
                matchScoresInfo.setPeriodT1(periodScores.getMatchScore().getHome());
                matchScoresInfo.setPeriodT2(periodScores.getMatchScore().getAway());
            }
        }
        //2.变更入库
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);

    }

    /**
     * 组装标准赛事比分数据
     * @param json
     * @return
     */
    public Map<String, Object> buildStandardMatchScoreByMap(String json){
        if(StringUtils.isEmpty(json)){
            return new HashMap<>();
        }
        JSONObject periodAmericanFootballScores = JSONObject.parseObject(json);
        Map<Long, AmericanFootballScores> allPeriodScores= JsonMapUtils.parseAmericanFootballMap(periodAmericanFootballScores);
        //.定义要求结果
        Map<String,Object> matchScore =new HashMap<>();
        //1.半场比分计算
        CommonItem periodOne =new CommonItem();
        CommonItem periodTwo =new CommonItem();
        //1.赛制判断
        for (Map.Entry<Long, AmericanFootballScores> entry : allPeriodScores.entrySet()) {
            if(entry.getKey().equals(13L)||entry.getKey().equals(14L)){
                periodOne.setHome(periodOne.getHome()+entry.getValue().getMatchScore().getHome());
                periodOne.setAway(periodOne.getAway()+entry.getValue().getMatchScore().getAway());
                matchScore.put("periodOneScore",periodOne);
            }
            if(entry.getKey().equals(15L)||entry.getKey().equals(16L)){
                periodTwo.setHome(periodTwo.getHome()+entry.getValue().getMatchScore().getHome());
                periodTwo.setAway(periodTwo.getAway()+entry.getValue().getMatchScore().getAway());
                matchScore.put("periodTwoScore",periodTwo);
            }
        }
        AmericanFootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        matchScore.put("wholeScore",wholeSores.getMatchScore());
        return matchScore;
    }
}
