package com.panda.merge.calculation.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.common.enums.OperateLogTypeEnum;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.*;
import com.panda.merge.dto.scores.*;
import com.panda.merge.dto.sourceSwitch.BasketballSwitch;
import com.panda.merge.mapper.StandardMatchScoresMapper;
import com.panda.merge.model.*;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.StandardMatchInfoRepository;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.utils.SettleNumToScoreCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;
import static java.util.stream.Collectors.toList;

/**
 * 篮球 比分计算并入库
 *
 * @author idol
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2022-2-26 17:06:27
 * @see com.panda.merge.calculation.impl
 */

@Slf4j
@Service
public class BasketballCalculationServiceImpl extends AbstractCalculationServiceImpl {

    @Autowired
    StandardMatchInfoRepository standardMatchInfoRepository;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    StandardMatchScoresMapper standardMatchScoresMapper;

    @Override
    public void calculationMatchScores( MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        log.info("linkId::{}::processLivedataScores basketball  scores start...",data.getLinkId());

        //是否是赛事比赛阶段
        //1.根据event_code 计算 当前事件
        String scoreStr=matchScoresInfo.getScoresJson();
        //保存进球事件，为删除事件做准备-下发删除事件时，附加字段为被删除事件的三方事件ID
        if("score_change".equals(data.getEventCode())|| "match_status".equals(data.getEventCode())){
            redisService.set("MATCHEVENT:DELETE:SCORE_CHANGE:"+data.getThirdEventId(),data.getExtraInfo());
        }
        //没数据的情况
        if(StringUtils.isEmpty(scoreStr)){
            createScores(matchScoresInfo,data);
            return;
        }else {
            if(data.getEventCode().equals("period_score_change")){
                //有数据则更新数据
                periodScoresChange(matchScoresInfo,data);
                return;
            }
            //6分钟比分计算
            update6MinsScores(matchScoresInfo,data);
            log.info("linkId::{}::processLivedataScores basketball update6MinsScores scores start...",data.getLinkId());

            //1.判断该阶段数据是否存在，不存在则提供数据
            updateScores(matchScoresInfo,data);
            log.info("linkId::{}::processLivedataScores basketball updateScores scores start...",data.getLinkId());

        }
        //缓存上一个进球事件的事件发生时间
        if("score_change".equals(data.getEventCode())|| "match_status".equals(data.getEventCode())){
            redisService.set("MATCHEVENT:SCORE_CHANGE:"+data.getThirdMatchId(),data.getEventTime(),3600);
        }
    }

    /**
     * 阶段比分更新
     * @param matchScoresInfo
     * @param data
     */
    private void periodScoresChange(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        JSONObject periodBasketballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodBasketballScores);
        BasketballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        ////比分修正事件阶段修复
        if(wholeSores==null|| data.getMatchPeriodId()==null){
            log.error("::{}::比分不存在,或者阶段为空",data.getLinkId());
            return;
        }
        BasketballScores periodScores= allPeriodScores.get(data.getMatchPeriodId());
        //阶段不存在返回
        if(periodScores==null) {
            log.error("::{}::阶段比分不存在",data.getLinkId());
            return;
        }
        StandardMatchInfo standardMatchInfo =standardMatchInfoRepository.selectStandardMatchPrimaryKey(data.getStandardMatchId());
        wholeSores.periodScoresChange(data,allPeriodScores,standardMatchInfo.getMatchLength());
        for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
            entry.getValue().doCalculation();
        }
        //总阶段新增事件值
        matchScoresInfo.setPeriodT1(periodScores.getMatchScore().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getMatchScore().getAway());
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    private void update6MinsScores(MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        log.info("update6MinsScores 6分钟比分计算:"+data.getEventCode()+"，linkId:"+data.getLinkId());
//        StandardMatchInfo standardMatchInfo =standardMatchInfoRepository.selectStandardMatchPrimaryKey(data.getStandardMatchId());
        if(null == matchScoresInfo.getMatchLength() || 7!=matchScoresInfo.getMatchLength()){
            return;
        }
        try{
            if(data.getMatchPeriodId()==999L){
                log.info("linkId::{}::updateScores basketball updateScores 999过滤比分计算 ...{},{}",data.getLinkId(),data.getEventCode(),data.getMatchPeriodId());
                return;
            }
            JSONObject periodBasketballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodBasketballScores);
            if(data.getEventCode().equals("match_status")){
                //阶段301/234转换为13 14 15 16
                changePeriodByExtryPeriodEvent(data,allPeriodScores);
                data.setEventCode("score_change");
            }
            Long sixPeriod = SportPeriodConstant.BasketballPeriod.get6MinPeriod(data.getMatchPeriodId(),data.getSecondsFromStart());
            if(sixPeriod==null){
                log.info("{}:阶段或者时长错误,无法获取6分钟比分区间.{},{}",data.getLinkId(),data.getMatchPeriodId(),data.getSecondsFromStart());
                return;
            }
            //非进球事件，不计算6分钟比分，直接返回原比分
            if(!"score_change".equals(data.getEventCode())){
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                matchScoresInfo.setModifyTime(System.currentTimeMillis());
                matchScoresInfo.setEventTime(data.getEventTime());
                return;
            }
            if(data.getT1()==null || data.getT2()==null){
//                log.info("update6MinsScores 6分钟比分计算错误，比分为空:{}",data.getLinkId());
                return;
            }
            BasketballScores sixScores= allPeriodScores.get(sixPeriod);
            if(sixScores==null){
                sixScores = new BasketballScores(sixPeriod);
                sixScores.setMatchScore(new CommonItem());
                allPeriodScores.put(sixPeriod, sixScores);
            }
            sixScores.set6MinuteFieldByEventCode(data,allPeriodScores);
            matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
            matchScoresInfo.setModifyTime(System.currentTimeMillis());
            matchScoresInfo.setEventTime(data.getEventTime());
        }catch(Exception e){
            log.error("::{}::update6MinsScores 6分钟比分计算异常",data.getLinkId(),e);
        }
    }


    /**
     * 比分数据json转map
     * @param sjon
     * @return
     */
    public   Map<String, CommonItem> buildMatchScoreByMap(String sjon){
        if(StringUtils.isEmpty(sjon)){
            return new HashMap<>();
        }
        JSONObject periodBasketballScores = JSONObject.parseObject(sjon);
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodBasketballScores);
        //.定义要求结果
        Map<String,CommonItem> matchScore =new HashMap<>();
        //1.半场比分计算
        CommonItem period =new CommonItem();
        //1.赛制判断
        for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
            if(entry.getKey().equals(1l)||entry.getKey().equals(13L)||entry.getKey().equals(14L)){
                period.setHome(period.getHome()+entry.getValue().getMatchScore().getHome());
                period.setAway(period.getAway()+entry.getValue().getMatchScore().getAway());
            }
        }
        BasketballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        matchScore.put("periodScore",period);
        matchScore.put("wholeScore",wholeSores.getMatchScore());
        return matchScore;
    }

    /**
     * 比分数据json转map
     * @param json
     * @return
     */
    public   Map<String, Object> buildStandardMatchScoreByMap(String json){
        if(StringUtils.isEmpty(json)){
            return new HashMap<>();
        }
        JSONObject periodBasketballScores = JSONObject.parseObject(json);
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodBasketballScores);
        //.定义要求结果
        Map<String,Object> matchScore =new HashMap<>();
        //1.半场比分计算
        CommonItem periodOne =new CommonItem();
        CommonItem periodTwo =new CommonItem();
//        1.赛制判断
//        for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
//            if(entry.getKey().equals(1L)||entry.getKey().equals(13L)||entry.getKey().equals(14L)){
//                periodOne.setHome(periodOne.getHome()+entry.getValue().getMatchScore().getHome());
//                periodOne.setAway(periodOne.getAway()+entry.getValue().getMatchScore().getAway());
//
//            }
//            if(entry.getKey().equals(2L)||entry.getKey().equals(15L)||entry.getKey().equals(16L)){
//                periodTwo.setHome(periodTwo.getHome()+entry.getValue().getMatchScore().getHome());
//                periodTwo.setAway(periodTwo.getAway()+entry.getValue().getMatchScore().getAway());
//            }
//        }
        Map<Long, BasketballScores> htScoresMap =  allPeriodScores.entrySet().stream().filter(m -> m.getKey()==13L || m.getKey()==14L).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Long, BasketballScores> ftScoresMap =  allPeriodScores.entrySet().stream().filter(m -> m.getKey()==15L || m.getKey()==16L).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Long, BasketballScores> scoresMap1 =  allPeriodScores.entrySet().stream().filter(m -> m.getKey()==1L).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Long, BasketballScores> scoresMap2 =  allPeriodScores.entrySet().stream().filter(m -> m.getKey()==2L).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if(!htScoresMap.isEmpty() || !ftScoresMap.isEmpty()){
            for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
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
        }else{
            if(!scoresMap1.isEmpty()){
                periodOne.setHome(periodOne.getHome()+scoresMap1.get(1L).getMatchScore().getHome());
                periodOne.setAway(periodOne.getAway()+scoresMap1.get(1L).getMatchScore().getAway());
                matchScore.put("periodOneScore",periodOne);
            }
            if(!scoresMap2.isEmpty()){
                periodTwo.setHome(periodTwo.getHome()+scoresMap2.get(2L).getMatchScore().getHome());
                periodTwo.setAway(periodTwo.getAway()+scoresMap2.get(2L).getMatchScore().getAway());
                matchScore.put("periodTwoScore",periodTwo);
            }
        }
        BasketballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        matchScore.put("wholeScore",wholeSores.getMatchScore());
        return matchScore;
    }

    /**
     * 下发给结算/三方的 取三方事件下发的半场比分数据
     * @param json
     * @return
     */
    public   Map<String, Object> buildThirdMatchScoreByMap(String json){
        if(StringUtils.isEmpty(json)){
            return new HashMap<>();
        }
        JSONObject periodBasketballScores = JSONObject.parseObject(json);
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodBasketballScores);
        //.定义要求结果
        Map<String,Object> matchScore =new HashMap<>();
        //1.半场比分计算
        CommonItem periodOne =new CommonItem();
        CommonItem periodTwo =new CommonItem();
        Map<Long, BasketballScores> scoresMap1 =  allPeriodScores.entrySet().stream().filter(m -> m.getKey()==1L).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<Long, BasketballScores> scoresMap2 =  allPeriodScores.entrySet().stream().filter(m -> m.getKey()==2L).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if(!scoresMap1.isEmpty()){
            periodOne.setHome(periodOne.getHome()+scoresMap1.get(1L).getMatchScore().getHome());
            periodOne.setAway(periodOne.getAway()+scoresMap1.get(1L).getMatchScore().getAway());
            matchScore.put("periodOneScore",periodOne);
        }
        if(!scoresMap2.isEmpty()){
            periodTwo.setHome(periodTwo.getHome()+scoresMap2.get(2L).getMatchScore().getHome());
            periodTwo.setAway(periodTwo.getAway()+scoresMap2.get(2L).getMatchScore().getAway());
            matchScore.put("periodTwoScore",periodTwo);
        }
        BasketballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        matchScore.put("wholeScore",wholeSores.getMatchScore());
        return matchScore;
    }


    /**
     * 初始化比分
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    private void createScores( MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        if(!SportPeriodConstant.BasketballPeriod.contans(data.getMatchPeriodId(),matchScoresInfo.getMatchLength())){
            return;
        }
        Map<Long, BasketballScores> periodBasketballScores= new HashMap<>();
        //事件阶段转换
        if("score_correction".equals(data.getEventCode()) ||
                "score_change".equals(data.getEventCode()) ||
                "match_status".equals(data.getEventCode())){
            changePeriodByExtryPeriodEvent(data,periodBasketballScores);
            data.setEventCode("score_change");
        }
        BasketballScores basketballScores=new BasketballScores(data.getMatchPeriodId());
        BasketballScores wholeSores= new BasketballScores(WHOLE_MATCH);

        periodBasketballScores.put(WHOLE_MATCH,wholeSores);
        periodBasketballScores.put(data.getMatchPeriodId(),basketballScores);

        basketballScores.updateEvent(data,periodBasketballScores);
        basketballScores.doCalculation();
//        //总阶段新增事件值
//        wholeSores.doCalculation();
//        wholeSores.updateEvent(data,periodBasketballScores);

        //3.更新比分模板
        periodBasketballScores.put(data.getMatchPeriodId(),((JSONObject) JSONObject.toJSON(basketballScores)).toJavaObject(BasketballScores.class));
        matchScoresInfo.setT1(basketballScores.getMatchScore().getHome());
        matchScoresInfo.setT2(basketballScores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(basketballScores.getMatchScore().getHome());
        matchScoresInfo.setPeriodT2(basketballScores.getMatchScore().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodBasketballScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        log.info("{} createScores 成功"+data.getEventCode()+"事件ID:"+data.getThirdEventId(),data.getLinkId());
    }

    /**
     * 更新比分
     * @param matchScoresInfo
     * @param data
     * @throws Exception
     */
    private void updateScores( MatchScoresInfo matchScoresInfo, MatchEventInfo data) throws Exception {
        log.info("linkId::{}::updateScores basketball updateScores ...",data.getLinkId());
        JSONObject periodBasketballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodBasketballScores);
        BasketballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        if(data.getMatchPeriodId()==999L){
            log.info("linkId::{}::updateScores basketball updateScores 999过滤比分计算 ...{},{}",data.getLinkId(),data.getEventCode(),data.getMatchPeriodId());
            return;
        }
        ////比分修正事件阶段修复
        if(data.getEventCode().equals("score_correction")||data.getEventCode().equals("score_change") || "match_status".equals(data.getEventCode())){
            changePeriodByExtryPeriodEvent(data,allPeriodScores);
            data.setEventCode("score_change");
        }
        log.info("linkId::{}::updateScores basketball updateScores1 ...{},{}",data.getLinkId(),data.getEventCode(),data.getMatchPeriodId());
//        if("match_status".equals(data.getEventCode())){
////            changePeriodByExtryPeriodEvent(data,allPeriodScores);
//            data.setEventCode("score_change");
              //95082 【生产】【产品】篮球允许小节休息阶段修改对应阶段比分  注释原有三分限制逻辑
//            List<Long> BASKETBALL_PERIOD = new ArrayList<>(Arrays.asList(13L,14L,15L,16L));
//            if(BASKETBALL_PERIOD.contains(data.getMatchPeriodId())){
//                BasketballScores currentPeriodScores = allPeriodScores.get(data.getMatchPeriodId());
//                if(currentPeriodScores==null){
//                    currentPeriodScores = new BasketballScores(data.getMatchPeriodId());
//                }
//                int oldScore = currentPeriodScores.getMatchScore().getHome() + currentPeriodScores.getMatchScore().getAway();
//                //最新阶段的当前节的节比分
//                if(data.getFirstT1()==null || data.getFirstT2() == null){
//                    log.info(data.getLinkId()+" 数据异常，data.getFirstT1()="+data.getFirstT1()+",data.getFirstT2()="+data.getFirstT2()+"，不处理比分计算");
//                    return;
//                }
//                int newScore = data.getFirstT1() + data.getFirstT2();
//                if(oldScore!=0 && newScore!=0){
//                    if(oldScore-newScore>3 || newScore-oldScore>3){
//                        log.info("{}-{},新阶段比分与原阶段比分相差大于3分，当前比分不处理,N0123除外：{}",oldScore,newScore,data.getLinkId());
//                        if(!DataSourceCodeEnum.N01.getCode().equals(data.getDataSourceCode())
//                            && !DataSourceCodeEnum.N02.getCode().equals(data.getDataSourceCode())
//                            && !DataSourceCodeEnum.N03.getCode().equals(data.getDataSourceCode())){
////                            throw new Exception("新阶段比分与原阶段比分相差大于3分，当前比分不处理："+data.getLinkId()+"---"+oldScore+"--"+newScore);
//                            return;
//                        }
//                    }
//                }
//            }
//        }
        if(!SportPeriodConstant.BasketballPeriod.contans(data.getMatchPeriodId(),matchScoresInfo.getMatchLength())){
            log.info("linkId::{}::updateScores basketball updateScores 赛制校验不通过...{}",data.getLinkId(),matchScoresInfo.getMatchLength());
            return;
        }
        if(wholeSores==null|| data.getMatchPeriodId()==null){
            log.info("linkId:"+data.getLinkId()+"，updateScores wholeSores==null|| data.getMatchPeriodId()==null ThirdMatchSourceId:"+data.getThirdMatchSourceId()+"matchid:"+matchScoresInfo.getThirdMatchId());
            return;
        }
        log.info("linkId::{}::updateScores basketball updateScores2 ...{},{}",data.getLinkId(),data.getEventCode(),data.getMatchPeriodId());

        BasketballScores periodScores= allPeriodScores.get(data.getMatchPeriodId());

        //新建该阶段值
        if(periodScores==null) {
            periodScores = new BasketballScores(data.getMatchPeriodId());
            allPeriodScores.put(data.getMatchPeriodId(), periodScores);
        }
        //上一个事件的事件发生时间
        if(redisService.get("MATCHEVENT:SCORE_CHANGE:"+data.getThirdMatchId())!=null){
            log.info("获取篮球上一个事件的事件时间：{}",redisService.get("MATCHEVENT:SCORE_CHANGE:"+data.getThirdMatchId())+"");
            Long scoreChangeTime = (Long) redisService.get("MATCHEVENT:SCORE_CHANGE:"+data.getThirdMatchId());
            data.setAddition3(scoreChangeTime+"");
        }
        wholeSores.updateEvent(data,allPeriodScores);
        //总阶段新增事件值
        periodScores.doCalculation();
        wholeSores.doCalculation();

        //当前阶段新增事件值 或者设置当前事件值
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(periodScores.getMatchScore().getHome());
        matchScoresInfo.setPeriodT2(periodScores.getMatchScore().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 获取最新阶段比分
     * @param allPeriodScores
     * @param period
     * @param newScore
     * @return
     */
    private Integer getNewScores(Map<Long, BasketballScores> allPeriodScores,Long period,Integer newScore){
        if(allPeriodScores.get(period)!=null){
            int home = allPeriodScores.get(period).getMatchScore().getHome();
            int away = allPeriodScores.get(period).getMatchScore().getAway();
            newScore = newScore - (home+ away);
        }
        return newScore;
    }
    /**
     * 调整,适配阶段ID
     * @param data
     * @param allPeriodScores
     */
    private void changePeriodByExtryPeriodEvent(MatchEventInfo data, Map<Long, BasketballScores> allPeriodScores) {
        if(data.getMatchPeriodId().equals(301L)){
            data.setMatchPeriodId(13L);
        }
        if(data.getMatchPeriodId().equals(302L)){
            data.setMatchPeriodId(14L);
        }
        if(data.getMatchPeriodId().equals(303L)){
            data.setMatchPeriodId(15L);
        }
        if(data.getMatchPeriodId().equals(31L)){
            data.setMatchPeriodId(1L);
        }
        if(data.getMatchPeriodId().equals(100L)){
            if(allPeriodScores.get(1L)!=null){
                data.setMatchPeriodId(2L);
            }else {
                data.setMatchPeriodId(16L);
            }
        }
        if(data.getMatchPeriodId().equals(999L)){
            if(allPeriodScores.get(40L)!=null){
                data.setMatchPeriodId(40L);
            }else {
                data.setMatchPeriodId(16L);
            }
        }
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

        String key = data.getThirdMatchId()+"score_change";
        if(redisService.hasKey(key)){
            MatchEventInfo event = (MatchEventInfo)redisService.get(key);
            if(Objects.equals(event.getT1(), data.getT1()) && Objects.equals(event.getT2(), data.getT2())){
                log.info("取消事件已存在，不需处理：当前link:{},已处理LINK:{}",data.getLinkId(),event.getLinkId());
                return;
            }
        }

        JSONObject periodBasketballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodBasketballScores);
        if(data.getEventCode().equals("match_status")||data.getEventCode().equals("score_change")){
            data.setEventCode("score_change");
            changePeriodByExtryPeriodEvent(data,allPeriodScores);
        }
        if(!SportPeriodConstant.BasketballPeriod.contans(data.getMatchPeriodId(),matchScoresInfo.getMatchLength())){
            return;
        }

        BasketballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        BasketballScores oldSores= allPeriodScores.get(data.getMatchPeriodId());
        if(wholeSores==null||oldSores==null){
            log.error("canleEvent 当前取消事件还未被统计:wholeSores==null||oldSores==null EventCode:"+data.getEventCode()+"事件ID:"+data.getThirdEventId());
            throw new Exception("canleEvent 当前取消事件还未被统计:wholeSores==null||oldSores==null |EventCode："+data.getEventCode());
        }

        if(data.getExtraInfo()!=null){
            if(data.getExtraInfo().length()>1){
                if(redisService.get("MATCHEVENT:DELETE:SCORE_CHANGE:"+data.getExtraInfo())!=null){
                    String extraInfo =(String) redisService.get("MATCHEVENT:DELETE:SCORE_CHANGE:"+data.getExtraInfo());
                    log.info("获取删除事件 EventCode:"+data.getEventCode()+"事件ID:"+data.getThirdEventId()+"extraInfo=",extraInfo);
                    data.setExtraInfo(extraInfo);
                }
            }
        }
        wholeSores.cancelEvent(data,data,allPeriodScores);
        wholeSores.doCalculation();
        oldSores.doCalculation();
        //入库保存
        matchScoresInfo.setT1(wholeSores.getMatchScore().getHome());
        matchScoresInfo.setT2(wholeSores.getMatchScore().getAway());
        matchScoresInfo.setPeriodT1(oldSores.getMatchScore().getHome());
        matchScoresInfo.setPeriodT2(oldSores.getMatchScore().getAway());
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        if("score_change".equals(data.getEventCode())){
            log.info("score_change事件修正{}",data.getLinkId());
            redisService.set(data.getThirdMatchId()+"score_change",data,180);
        }
    }

    /**
     * 保存赛事统计比分
     * @param matchScoresInfo
     * @param data
     */
    @Override
    public void saveMatchStatisticsScores(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data, StandardMatchInfo standardMatchInfo) {
        if(matchScoresInfo.getMatchLength()==3){
            save3X3MatchStatistics(matchScoresInfo,data);
            return;
        }
        //1.1 查询 matchScoresInfo 的 json 是否存在 不存在则新建
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            createMatchStatistics(matchScoresInfo,data);
        }else {
            //2.如果存在则覆盖值
            saveMatchStatistics(matchScoresInfo,data);
        }
    }

    /**
     * 保存3x3玩法比分
     * @param matchScoresInfo
     * @param data
     */
    private void save3X3MatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        for (MatchStatisticsInfoDetailDTO detailDTO : data.getMatchStatisticsInfoDetailList()) {

            if(detailDTO.getCode().equals("match_score")){
                matchScoresInfo.setT1(detailDTO.getT1());
                matchScoresInfo.setT2(detailDTO.getT2());
            }
        }
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    /**
     * 保存赛事统计信息
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
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodBasketballScores);
        //1. 根据 data periodId 和 赛制 判断当前的阶段是哪个赛制 ， 2. 根据赛制获取 map列表 3. 根据 map 阶段 使用 set_score 赋值全局阶段比分
        Long []  periodArr = null;
        if(matchScoresInfo.getMatchLength()!=null&&matchScoresInfo.getMatchLength()==17){
            periodArr = new Long[]{1L, 2L,40L};
        }else {
            periodArr =    new Long[]{13L,14L, 15L,16L,40L};
        }

        for (MatchStatisticsInfoDetailDTO detailDTO : data.getMatchStatisticsInfoDetailList()) {
            if(detailDTO.getCode().equals("set_score")){
                if(periodArr.length<detailDTO.getFirstNum()){
                    continue;
                }
                Long periodId = periodArr[detailDTO.getFirstNum()-1];
                //篮球UOF 如果没有加时赛或者加时赛结束不处理加时赛比分
                if(periodId.equals(40L)){
                    if(data.getPeriod()==40||data.getPeriod()==110){
                    }else {
                        continue;
                    }
                }
                //根据 阶段 赋值当前阶段 set_core
                BasketballScores periodScore=allPeriodScores.get(periodId);
                if(periodScore==null){
                    periodScore=new BasketballScores(data.getPeriod()+0l);
                    allPeriodScores.put(periodId,periodScore);
                }
                periodScore.getMatchScore().setHome(detailDTO.getT1());
                periodScore.getMatchScore().setAway(detailDTO.getT2());
                continue;
            }
            if(detailDTO.getCode().equals("match_score")){
                BasketballScores wholeScore=allPeriodScores.get(WHOLE_MATCH);
                wholeScore.getMatchScore().setHome(detailDTO.getT1());
                wholeScore.getMatchScore().setAway(detailDTO.getT2());
            }
        }
        for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
            entry.getValue().doCalculation();
        }
        //2.变更入库
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
        matchScoresInfo.setModifyTime(System.currentTimeMillis());
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);

    }

    /**
     * 初始化比分统计
     * @param matchScoresInfo
     * @param data
     */
    private void createMatchStatistics(MatchScoresInfo matchScoresInfo, MatchStatisticsInfoDTO data) {
        BasketballScores basketballScores=new BasketballScores(data.getPeriod().longValue());
        BasketballScores wholeScores=new BasketballScores(WHOLE_MATCH);
        Map<Long, BasketballScores> periodBasketballScores= new HashMap<>();
        periodBasketballScores.put(data.getPeriod().longValue(),basketballScores);
        periodBasketballScores.put(WHOLE_MATCH,wholeScores);

        //保存比分
        if(data.getMatchStatisticsInfoDetailList()==null){
            log.error("createMatchStatistics data:null");
            return;
        }
        for (MatchStatisticsInfoDetailDTO matchStatisticsInfoDetailDTO : data.getMatchStatisticsInfoDetailList()) {
            if(matchStatisticsInfoDetailDTO.getCode().equals("set_score")){
                continue;
            }
            basketballScores.saveStatisticsInfo(matchStatisticsInfoDetailDTO.getCode(),matchStatisticsInfoDetailDTO.getT1(),matchStatisticsInfoDetailDTO.getT2());
            wholeScores.saveStatisticsInfo(matchStatisticsInfoDetailDTO.getCode(),matchStatisticsInfoDetailDTO.getT1(),matchStatisticsInfoDetailDTO.getT2());
        }
        for (Map.Entry<Long, BasketballScores> entry : periodBasketballScores.entrySet()) {
            entry.getValue().doCalculation();
        }
        //更新赛事比分表
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(periodBasketballScores));
//        matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
    }

    public Map<String, CommonItem> build3X3MatchScoreByMap(MatchScoresInfo scoresJson) {

        //.定义要�
        Map<String,CommonItem> matchScore =new HashMap<>();
        CommonItem wholeSores = new CommonItem();
        wholeSores.setHome(scoresJson.getT1());
        wholeSores.setAway(scoresJson.getT2());
        matchScore.put("wholeScore",wholeSores);
        return matchScore;
    }

//    /**
//     * 同步事件源的区间比分
//     * @param periodId
//     * @param standardScores
//     * @param allPeriodScores
//     * @param basketballSwitch
//     */
//    private void copyMinuteScores(Long periodId, Map<Long, BasketballScores> standardScores, Map<Long, BasketballScores> allPeriodScores, BasketballSwitch basketballSwitch) {
//        if(allPeriodScores!=null){
//            for (Long period : allPeriodScores.keySet()) {
//                if(period>999L){
//                    standardScores.put(period,allPeriodScores.get(period));
//                }
//            }
//        }
//    }
    @Override
    public void calcStandardMatchScores(MatchScoresInfo matchScoresInfo, StandardMatchScores score, MatchEventInfo data) throws Exception {
        log.info("calcStandardMatchScores篮球 linkId={},计算标准比分",data.getLinkId());
        String scoresJson = matchScoresInfo.getScoresJson();
        StandardMatchInfo standardMatchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(data.getStandardMatchId());
        if(standardMatchInfo==null){
            log.info("{}标准赛事数据不存在，同步标准比分异常",data.getLinkId());
            return;
        }
        Map<Long, BasketballScores> allPeriodScores = JSON.parseObject(scoresJson, new TypeReference<Map<Long, BasketballScores>>() {
        });
        BasketballScores thirdWholeSores= allPeriodScores.get(WHOLE_MATCH);
        Map<Long, BasketballScores> standardScores = new HashMap<>();
        //适配、转换阶段
        changePeriodByExtryPeriodEvent(data,allPeriodScores);
        try{
            if (!StringUtils.isEmpty(score.getScoreJson())) {
                standardScores = JSON.parseObject(score.getScoreJson(), new TypeReference<Map<Long, BasketballScores>>() {
                });
                String sourceSwitchJson = score.getDataSourceAccoSwitch();
                BasketballSwitch basketballSwitch = new BasketballSwitch();
                if (StringUtils.isNotEmpty(sourceSwitchJson)) {
                    basketballSwitch = JSONObject.parseObject(sourceSwitchJson, BasketballSwitch.class);
                }
                //每次执行当前阶段的比分同步
                setPeriodScores(standardScores, allPeriodScores, basketballSwitch,data.getMatchPeriodId(),standardMatchInfo);
            }else{
                //标准比分为空，直接复制三方比分
                standardScores = JSON.parseObject(scoresJson, new TypeReference<Map<Long, BasketballScores>>() {
                });
            }
            //计算常规赛比分 100阶段
            calcWholeScores(standardScores,thirdWholeSores,matchScoresInfo.getMatchLength(),100L);
            //计算总比分 -1阶段
            calcWholeScores(standardScores,thirdWholeSores,matchScoresInfo.getMatchLength(),-1L);
        }catch (Exception e){
            log.error("计算标准比分错误:{}",data.getLinkId(),e);
        }
        //保存
        score.setScoreJson(JSONUtil.toJsonStr(standardScores));
    }

    private void setPeriodScores(Map<Long, BasketballScores> standardScores, Map<Long, BasketballScores> allPeriodScores, BasketballSwitch basketballSwitch, Long periodId,StandardMatchInfo standardMatchInfo) {
        BasketballScores soresSource= allPeriodScores.get(periodId);
        if(soresSource==null) {
            log.info("复制篮球阶段比分,三方阶段比分为空 {}",periodId);
            return;
        }
        if(standardMatchInfo.getMatchLength()==null){
            standardMatchInfo.setMatchLength(0);
        }
//        copyMinuteScores(periodId,standardScores,allPeriodScores,basketballSwitch);
        if(standardScores.get(periodId)==null){
            standardScores.put(periodId,new BasketballScores(periodId));
        }
        log.info("复制篮球阶段比分,阶段：{}，{}",periodId,soresSource.getMatchScore().doCountScoreStr());
        //检索历史比分，根据开关同步历史比分
        for(Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()){
            //同步三方阶段比分到标准比分
            if(entry.getKey()==13L && basketballSwitch.getFirstSwitch()==1){
                standardScores.put(13L,entry.getValue());
                if(standardMatchInfo.getMatchLength()==7){
                    BasketballScores soresFirst= allPeriodScores.get(1306L);
                    BasketballScores soresSecond = allPeriodScores.get(1312L);
                    standardScores.put(1306L,soresFirst);
                    standardScores.put(1312L,soresSecond);
                }
            }else if(entry.getKey()==14L && basketballSwitch.getSecondSwitch()==1){
                standardScores.put(14L,entry.getValue());
                if(standardMatchInfo.getMatchLength()==7){
                    BasketballScores soresFirst= allPeriodScores.get(1406L);
                    BasketballScores soresSecond = allPeriodScores.get(1412L);
                    standardScores.put(1406L,soresFirst);
                    standardScores.put(1412L,soresSecond);
                }
            }else if(entry.getKey()==15L && basketballSwitch.getThirdSwitch()==1){
                standardScores.put(15L,entry.getValue());
                if(standardMatchInfo.getMatchLength()==7){
                    BasketballScores soresFirst= allPeriodScores.get(1506L);
                    BasketballScores soresSecond = allPeriodScores.get(1512L);
                    standardScores.put(1506L,soresFirst);
                    standardScores.put(1512L,soresSecond);
                }
            }else if(entry.getKey()==16L && basketballSwitch.getFourSwitch()==1){
                standardScores.put(16L,entry.getValue());
                if(standardMatchInfo.getMatchLength()==7){
                    BasketballScores soresFirst= allPeriodScores.get(1606L);
                    BasketballScores soresSecond = allPeriodScores.get(1612L);
                    standardScores.put(1606L,soresFirst);
                    standardScores.put(1612L,soresSecond);
                }
            }else if(entry.getKey()==40L && basketballSwitch.getOtSwitch()==1){
                standardScores.put(40L,entry.getValue());
            }else if(entry.getKey()==1L && basketballSwitch.getHfSwitch()==1){
                standardScores.put(1L,entry.getValue());
            }else if(entry.getKey()==2L && basketballSwitch.getFtSwitch()==1){
                standardScores.put(2L,entry.getValue());
            }else if(entry.getKey()==21L && basketballSwitch.getAllSwitch()==1){
                standardScores.put(21L,entry.getValue());
            }
        }
    }

    public StandardScoreCenterDTO queryMatchScores(Long standardMatchId) {
        StandardScoreCenterDTO dto = new StandardScoreCenterDTO();
        StandardSportMarketSell match = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(standardMatchId);
        if (match==null) {
            log.info("开售信息不存在");
            return null;
        }
        List<StandardScoreCenter> list = new ArrayList<>();

        StandardMatchInfo matchInfo = standardMatchInfoRepository.selectStandardMatchPrimaryKey(standardMatchId);

        dto.setSportId(match.getSportId());
        dto.setStandardMatchId(standardMatchId);
        dto.setMatchManageId(match.getMatchManageId());
        dto.setBusinessEvent(match.getBusinessEvent());
        dto.setRelatedDataSourceCoderList(matchInfo.getRelatedDataSourceCoderList());
        dto.setPreId(match.getId());
        dto.setMatchLength(matchInfo.getMatchLength());
        //查询标准比分
        StandardMatchScores standardMatchScores = scoresRedisHelp.getCatchStandScoreByMatchId(standardMatchId);
//        StandardMatchScores standardMatchScores = standardMatchScoresMapper.loadByMatchId(standardMatchId);

        if (standardMatchScores == null) {
            return null;
        }
        dto.setShowStatus(standardMatchScores.getShowStatus());
        dto.setSendSettleCount(standardMatchScores.getSendSettleCount()==null?0:standardMatchScores.getSendSettleCount());
        //获取标准比分
        StandardScoreCenter centerStand = new StandardScoreCenter();
        centerStand.setDataSourceCode("STAND");
        centerStand.setIndex(0);//排序保证标准比分放最前面
        centerStand.setStandardMatchId(standardMatchId);
        centerStand.setSportId(matchInfo.getSportId());
        centerStand.setSwitchStatus(standardMatchScores.getDataSourceAccoSwitch());
        //组装标准比分
        this.buildBasketballscore(centerStand, standardMatchScores.getScoreJson(),matchInfo);
//        if (centerStand.getScores() == null || centerStand.getScores().isEmpty()) {
            this.scoreIsNullExtractBaksetball(centerStand,standardMatchScores.getScoreJson(),matchInfo.getMatchLength());
//        }
        list.add(centerStand);

        List<MatchScoresInfo> listScore = new ArrayList<>();
        //获取其他数据源比分
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoService.getItems(standardMatchId);
        if (thirdMatchInfoList == null || thirdMatchInfoList.isEmpty()) {
            log.info("查询标准比分，无三方赛事,直接返回：{}",standardMatchId);
            //无三方赛事，直接返回
            dto.setScores(list);
            return dto;
        }
        for (ThirdMatchInfo thirdMatchInfo : thirdMatchInfoList) {
            if(N0123_SOURCE_CODE.contains(thirdMatchInfo.getDataSourceCode())){
                continue;
            }
            //其他数据源默认取实时事件比分 livedata
            MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.LIVE_DATA.getCode());
            if (matchScoresInfo != null) {
                listScore.add(matchScoresInfo);
            }else{
                //无事件比分则查询是否存在UOF统计比分
                matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.UOF.getCode());
                if (matchScoresInfo != null) {
                    listScore.add(matchScoresInfo);
                }
            }
        }
        if (listScore.isEmpty()) {
            log.info("查询标准比分，无三方比分,直接返回：{}",standardMatchId);
            dto.setScores(list);
            return dto;
        }
        //三方事件源比分转MAP返回前端
        Map<String, List<MatchScoresInfo>> scoreMaps =
                listScore.stream().collect(Collectors.groupingBy(MatchScoresInfo::getDataSourceCode, LinkedHashMap::new, toList()));
        int index = 1;//排序
        for (Map.Entry<String, List<MatchScoresInfo>> values : scoreMaps.entrySet()) {
            String dataSourceCode = values.getKey();
            String scoresJson = values.getValue().get(0).getScoresJson();
            StandardScoreCenter dataSourceScores = new StandardScoreCenter();
            dataSourceScores.setDataSourceCode(dataSourceCode);
            dataSourceScores.setStandardMatchId(standardMatchId);
            dataSourceScores.setSportId(matchInfo.getSportId());
            dataSourceScores.setIndex(index++);
            //是否主数据源
            if (dataSourceCode.equals(match.getBusinessEvent())) {
                dataSourceScores.setIsMain(true);
            } else {
                dataSourceScores.setIsMain(false);
            }
            //组装数据源比分
            this.buildBasketballscore(dataSourceScores, scoresJson,matchInfo);
            list.add(dataSourceScores);
        }
        if (!list.isEmpty()) {
            //排序，保证0-标准比分一直处于第一个
            list.sort(Comparator.comparing((StandardScoreCenter::getIndex)));
            super.chechScoreIsDifferent(list);
        }
        dto.setScores(list);
        return dto;
    }

    private static void scoreIsNullExtractBaksetball(StandardScoreCenter centerStand,String scoresJson,Integer matchLength) {
        List<StandardScoreDTO> listScore = new ArrayList<>();
        List<Long> scorePeroidLists = new ArrayList<>(Arrays.asList(13L,14L,15L,16L,40L,21L,307L,1L,2L,21L,-1L));
        if(matchLength==17){
            scorePeroidLists = new ArrayList<>(Arrays.asList(1L,2L,40L,-1L));
        }else if (matchLength == 73){
            scorePeroidLists = new ArrayList<>(Arrays.asList(21L,-1L));
        }
        if (centerStand.getScores() == null || centerStand.getScores().isEmpty()) {
            for (int i = 0; i < scorePeroidLists.size(); i++){
                StandardScoreDTO scores = new StandardScoreDTO();
                scores.setHome(null);
                scores.setAway(null);
                scores.setIndex(scorePeroidLists.get(i).intValue());
                scores.setPeriodId(scorePeroidLists.get(i));
                scores.setSwitchs(1);
                listScore.add(scores);
            }
            centerStand.setScores(listScore);
        }else{
            JSONObject periodScores = JSONObject.parseObject(scoresJson);
            Map<Long, BasketballScores> allPeriodScores = JsonMapUtils.parseBasketballMap(periodScores);
            List<Long> result = new ArrayList(allPeriodScores.keySet());
            List<Long> reduce1 = scorePeroidLists.stream().filter(item -> !result.contains(item)).collect(toList());
            if(!reduce1.isEmpty()){
                BasketballSwitch switchs = new BasketballSwitch();
                if(centerStand.getSwitchStatus()!=null){
                    switchs = JSONUtil.toBean(centerStand.getSwitchStatus(), BasketballSwitch.class);
                }
                for (int i = 0; i < reduce1.size(); i++){
                    StandardScoreDTO scores = new StandardScoreDTO();
                    scores.setHome(null);
                    scores.setAway(null);
                    scores.setIndex(scorePeroidLists.get(i).intValue());
                    scores.setPeriodId(scorePeroidLists.get(i));
                    scores.setSwitchs(1);
                    setScoresSwitch(scorePeroidLists.get(i), scores, switchs);
                    listScore.add(scores);
                }
            }
        }
    }
    public void buildBasketballscore(StandardScoreCenter center, String scoresJson,StandardMatchInfo matchInfo) {
        if (StringUtils.isEmpty(scoresJson)) {
            return;
        }
        List<Long> basketballScoreCenterPeriod = Arrays.asList(13L,14L,15L,16L,40L,21L);
        if(matchInfo.getMatchLength()==17){
            basketballScoreCenterPeriod = Arrays.asList(1L,2L,40L);
        }
        log.info("查询标准比分:{} ----:{}",center.getStandardMatchId(),scoresJson);
        //标准比分中心页面内容
        JSONObject periodScores = JSONObject.parseObject(scoresJson);
        Map<Long, BasketballScores> allPeriodScores = JsonMapUtils.parseBasketballMap(periodScores);
        //比分内容
        List<StandardScoreDTO> listScore = new ArrayList<>();
        //半场比分
        Integer hfHome=null, ftHome=null;
        Integer hfAway=null, ftAway=null;
        String str = center.getSwitchStatus();
        BasketballSwitch switchs = new BasketballSwitch();
        if(str!=null){
            switchs = JSONUtil.toBean(str, BasketballSwitch.class);
        }
        for (Long periodId : allPeriodScores.keySet()) {
            //查询比分时过滤阶段 5分钟 15分钟区间
            if (!basketballScoreCenterPeriod.contains(periodId)) {
                continue;
            }
            BasketballScores cc = allPeriodScores.get(periodId);
            log.info("查询标准比分{},获取阶段{},比分:{}",center.getStandardMatchId(),periodId,cc);
            StandardScoreDTO scores = new StandardScoreDTO();
            if(cc==null){
                scores.setHome(null);
                scores.setAway(null);
            }else{
                if(periodId==13L || periodId == 14L ||  periodId == 1L){
                    if(hfHome == null) hfHome = 0;
                    if(hfAway == null) hfAway = 0;
                    hfHome += cc.getMatchScore().getHome();
                    hfAway += cc.getMatchScore().getAway();
                }
                if(periodId==15L || periodId == 16L ||  periodId == 2L){
                    if(ftHome == null) ftHome = 0;
                    if(ftAway == null) ftAway = 0;
                    ftHome += cc.getMatchScore().getHome();
                    ftAway += cc.getMatchScore().getAway();
                }
                if(periodId==21){
                    if(hfHome == null) hfHome = 0;
                    if(hfAway == null) hfAway = 0;
                    hfHome += cc.getMatchScore().getHome();
                    hfAway += cc.getMatchScore().getAway();
                }
                scores.setHome(cc.getMatchScore().getHome());
                scores.setAway(cc.getMatchScore().getAway());
            }
            scores.setPeriodId(periodId);
            scores.setIndex(periodId.intValue());
            setScoresSwitch(periodId, scores, switchs);
            listScore.add(scores);
        }
        Integer otHome = 0,otAway = 0;
        if(matchInfo.getMatchLength()==null){
            matchInfo.setMatchLength(0);
        }
        if(matchInfo.getMatchLength()!=73){
            log.info("半场比分1:{},{}",hfHome,hfAway);
            //添加上半场比分
            StandardScoreDTO scores301 = new StandardScoreDTO();
            scores301.setHome(hfHome);
            scores301.setAway(hfAway);
            scores301.setPeriodId(1L);
            scores301.setSwitchs(switchs.getHfSwitch());
            listScore.add(scores301);

            log.info("半场比分2:{},{}",ftHome,ftAway);
            //添加下半场比分
            StandardScoreDTO scores302 = new StandardScoreDTO();
            scores302.setPeriodId(2L);
            scores302.setHome(ftHome);
            scores302.setAway(ftAway);
            scores302.setSwitchs(switchs.getFtSwitch());
            listScore.add(scores302);
            //添加下半场含加时
            StandardScoreDTO scores307 = new StandardScoreDTO();
            scores307.setPeriodId(307L);
            Integer home = 0,away = 0;
            if(matchInfo.getMatchLength()==17){
                if(allPeriodScores.get(2L)!=null){
                    home+=allPeriodScores.get(2L).getMatchScore().getHome();
                    away+=allPeriodScores.get(2L).getMatchScore().getAway();
                }
            }else{
                home = ftHome==null?0:ftHome;
                away = ftAway==null?0:ftAway;
            }
            if(allPeriodScores.get(40L)!=null){
                home+=allPeriodScores.get(40L).getMatchScore().getHome();
                away+=allPeriodScores.get(40L).getMatchScore().getAway();
                otHome = allPeriodScores.get(40L).getMatchScore().getHome();
                otAway = allPeriodScores.get(40L).getMatchScore().getAway();
            }
            if(home==0 && away ==0){
                scores307.setHome(null);
                scores307.setAway(null);
            }else{
                scores307.setHome(home);
                scores307.setAway(away);
            }
            listScore.add(scores307);
        }
        if(hfHome == null){
            hfHome=0;
            hfAway=0;
        }
        if(ftHome == null){
            ftHome=0;
            ftAway=0;
        }
        if(otHome==null || otAway==null){
            otHome=0;
            otAway=0;
        }
        StandardScoreDTO scores100 = new StandardScoreDTO();
        scores100.setPeriodId(100L);
        scores100.setHome(hfHome+ftHome);
        scores100.setAway(hfAway+ftAway);
        listScore.add(scores100);

        StandardScoreDTO scoresAll = new StandardScoreDTO();
        scoresAll.setPeriodId(-1L);
        scoresAll.setHome(hfHome + ftHome + otHome);
        scoresAll.setAway(hfAway+ftAway+otAway);
        listScore.add(scoresAll);
        listScore.sort(Comparator.comparing((StandardScoreDTO::getPeriodId)));
        log.info("查询标准比分3:{}",listScore);

//        if(matchInfo.getMatchLength()==7){
//            //组装6分钟比分
//            build6MinScores(listScore,allPeriodScores);
//        }
        //根据赛制获取完整比分列表
        listScore = getScoresListByMatchLength(listScore,matchInfo.getMatchLength(),switchs);
        log.info("查询标准比分4:{}",listScore);
        center.setScores(listScore);
        if(null != matchInfo.getMatchLength() && 7==matchInfo.getMatchLength()){
            center.setMinute6Scores(build6Minutes(allPeriodScores));
        }
        //只查询一次结算比分
        if("STAND".equals(center.getDataSourceCode())){
            center.setSettleScores(buildSettleScores(center.getStandardMatchId(),listScore));
        }
    }


    /**
     * 组装结算比分
     * @param standardMatchId
     * @param listScore
     * @return
     */
    private List<StandardScoreDTO> buildSettleScores(Long standardMatchId, List<StandardScoreDTO> listScore) {
        log.info("::{}::校验结算比分是否不同：{}",standardMatchId,JSON.toJSONString(listScore));

        List<StandardScoreDTO> settleResult = new ArrayList<>();
        //查询结算比分
        MatchSettleResultExample example =  new MatchSettleResultExample();
        example.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
        List<MatchSettleResult> list = matchSettleResultMapper.selectByExample(example);
        if (list.isEmpty()) {
            return null;
        }
        for (MatchSettleResult result: list){
            //保留阶段比分，过滤次序比分
            Integer index = SettleNumToScoreCodeUtils.getBasketSettleScoreIndex(result.getScoreCode());
            log.info("::{}::校验结算比分是否不同：index:{}",standardMatchId,index);
            if(index==null){
                continue;
            }
            log.info("::{}::校验结算比分是否不同：index:{},{}",index,standardMatchId,result);
            //获取页面的标准比分
//            List<StandardScoreDTO> standScList = listScore.stream().filter(s -> s.getIndex() == index).collect(Collectors.toList());
            if(!listScore.isEmpty()){
                for(StandardScoreDTO score: listScore ){
                    if(score.getIndex()==index || score.getPeriodId().intValue()==index){
                        //添加结算比分与标准比分不一致的标识
                        log.info("结算比分校验结算比分是否不同 home:T1 = {}:{},{}",score.getHome(),result.getT1(),Objects.equals(score.getHome(), result.getT1()));
                        log.info("结算比分校验结算比分是否不同 away:T2 = {}:{},{}",score.getAway(),result.getT2(),Objects.equals(score.getAway(), result.getT2()));
                        StandardScoreDTO scores = new StandardScoreDTO();
                        scores.setHome(result.getT1());
                        scores.setAway(result.getT2());
                        scores.setIndex(index);
                        scores.setPeriodId(Long.valueOf(index));
                        if(!Objects.equals(score.getHome(), result.getT1()) || !Objects.equals(score.getAway(), result.getT2())){
                            scores.setIsDifference(true);
                        }else{
                            scores.setIsDifference(false);
                        }
                        settleResult.add(scores);
                    }
                }
            }
//            settleResult.add(scores);
        }
        return settleResult;
    }

    /**
     * 组装篮球6分钟比分  ps:应前端要求，数据结构如此设计，才方便需求图数据渲染
     * @param allPeriodScores
     * @return
     */
    private static List<StandardScoresSixDetailDTO> build6Minutes(Map<Long, BasketballScores> allPeriodScores) {
        List<StandardScoresSixDetailDTO> listScore = new ArrayList<>();
        Long[] q1PeriodList = new Long[]{1312L,1412L,1512L,1612L};
        Long[] q2PeriodList = new Long[]{1306L,1406L,1506L,1606L};
        //0-6分钟
        StandardScoresSixDetailDTO scores6 = new StandardScoresSixDetailDTO();
        scores6.setPeriodId(6L);
        for(int i=0;i<q1PeriodList.length;i++){
            BasketballScores scores = allPeriodScores.get(q1PeriodList[i]);
            log.info("获取篮球6分钟比分：阶段：{}，比分：{},",q1PeriodList[i],scores);
            if(q1PeriodList[i]==1312L){
                if(scores!=null && scores.getMatchScore().getHome()!=null && scores.getMatchScore().getAway()!=null ){
                    scores6.setQ1Home(scores.getMatchScore().getHome());
                    scores6.setQ1Away(scores.getMatchScore().getAway());
                }
            }
            if(q1PeriodList[i]==1412L){
                if(scores!=null && scores.getMatchScore().getHome()!=null && scores.getMatchScore().getAway()!=null ){
                    scores6.setQ2Home(scores.getMatchScore().getHome());
                    scores6.setQ2Away(scores.getMatchScore().getAway());
                }
            }
            if(q1PeriodList[i]==1512L){
                if(scores!=null && scores.getMatchScore().getHome()!=null && scores.getMatchScore().getAway()!=null ){
                    scores6.setQ3Home(scores.getMatchScore().getHome());
                    scores6.setQ3Away(scores.getMatchScore().getAway());
                }
            }
            if(q1PeriodList[i]==1612L){
                if(scores!=null && scores.getMatchScore().getHome()!=null && scores.getMatchScore().getAway()!=null ){
                    scores6.setQ4Home(scores.getMatchScore().getHome());
                    scores6.setQ4Away(scores.getMatchScore().getAway());
                }
            }
        }
        listScore.add(scores6);
        //6-12分钟
        StandardScoresSixDetailDTO scores12 = new StandardScoresSixDetailDTO();
        scores12.setPeriodId(12L);
        for(int i=0;i<q2PeriodList.length;i++){
            BasketballScores scores = allPeriodScores.get(q2PeriodList[i]);
            log.info("获取篮球6分钟比分：阶段：{}，比分：{},",q2PeriodList[i],scores);
            if(q2PeriodList[i]==1306L){
                if(scores!=null && scores.getMatchScore().getHome()!=null && scores.getMatchScore().getAway()!=null ){
                    scores12.setQ1Home(scores.getMatchScore().getHome());
                    scores12.setQ1Away(scores.getMatchScore().getAway());
                }
            }
            if(q2PeriodList[i]==1406L){
                if(scores!=null && scores.getMatchScore().getHome()!=null && scores.getMatchScore().getAway()!=null ){
                    scores12.setQ2Home(scores.getMatchScore().getHome());
                    scores12.setQ2Away(scores.getMatchScore().getAway());
                }
            }
            if(q2PeriodList[i]==1506L){
                if(scores!=null && scores.getMatchScore().getHome()!=null && scores.getMatchScore().getAway()!=null ){
                    scores12.setQ3Home(scores.getMatchScore().getHome());
                    scores12.setQ3Away(scores.getMatchScore().getAway());
                }
            }
            if(q2PeriodList[i]==1606L){
                if(scores!=null && scores.getMatchScore().getHome()!=null && scores.getMatchScore().getAway()!=null ){
                    scores12.setQ4Home(scores.getMatchScore().getHome());
                    scores12.setQ4Away(scores.getMatchScore().getAway());
                }
            }
        }
        listScore.add(scores12);
        return listScore;
    }


    /**
     * 根据赛制,补齐对应的比分结构
     * @param listScore
     * @param matchLength
     */
    public static  List<StandardScoreDTO> getScoresListByMatchLength(List<StandardScoreDTO> listScore, Integer matchLength,BasketballSwitch switchs) {
        List<StandardScoreDTO> tempList = getScoresList(matchLength,switchs);
        Map<Long, StandardScoreDTO> target = new HashMap<>();
        if (CollectionUtils.isNotEmpty(listScore) && CollectionUtils.isNotEmpty(tempList)) {
            for (StandardScoreDTO tempUser : tempList) {
                target.put(tempUser.getPeriodId(), tempUser);
            }
            for (StandardScoreDTO tempUse2 : listScore) {
                Long userId = tempUse2.getPeriodId();
                if (target.containsKey(userId)) {
                    StandardScoreDTO temp = target.get(userId);
                    // 阶段重复，以listScore中的数据为准
                    temp.setPeriodId(tempUse2.getPeriodId());
                    temp.setHome(tempUse2.getHome());
                    temp.setAway(tempUse2.getAway());
                    temp.setSwitchs(tempUse2.getSwitchs());
//                    setScoresSwitch(tempUse2.getPeriodId(), tempUse2, switchs);
                    target.put(userId, temp);
                } else {
                    target.put(userId, tempUse2);
                }
            }
        }
        List<StandardScoreDTO> list = new ArrayList<>(target.values());
        list.sort(Comparator.comparing((StandardScoreDTO::getPeriodId)));
        return list;
    }


    public  static List<StandardScoreDTO> getScoresList(Integer matchLength,BasketballSwitch switchs) {

        List<Long> basketballScoreCenterPeriod = Arrays.asList(13L,14L,1L,15L,16L,2L,40L,/*307L,*/21L,-1L);

        List<StandardScoreDTO> tempList = new ArrayList<>();
        if(matchLength==null){
            matchLength = 0;
        }
        if(matchLength == 17){
            basketballScoreCenterPeriod = Arrays.asList(1L,2L,40L,307L,-1L);
        }else if(matchLength == 73){
            basketballScoreCenterPeriod = Arrays.asList(21L,-1L);
        }
        for (int i = 0; i < basketballScoreCenterPeriod.size(); i++) {
            StandardScoreDTO dto = new StandardScoreDTO();
            dto.setPeriodId(Long.valueOf(basketballScoreCenterPeriod.get(i)));
            setScoresSwitch(basketballScoreCenterPeriod.get(i), dto, switchs);
            tempList.add(dto);
        }
        return tempList;
    }


    private static void setScoresSwitch(Long periodId, StandardScoreDTO scores, BasketballSwitch switchs) {
        //开关
        if(periodId ==13L){
            scores.setSwitchs(switchs.getFirstSwitch());
        }else if (periodId ==14L){
            scores.setSwitchs(switchs.getSecondSwitch());
        }else if (periodId ==15L){
            scores.setSwitchs(switchs.getThirdSwitch());
        }else if (periodId ==16L){
            scores.setSwitchs(switchs.getFourSwitch());
        }else if (periodId ==40L){
            scores.setSwitchs(switchs.getOtSwitch());
        }else if (periodId ==1L){
            scores.setSwitchs(switchs.getHfSwitch());
        }else if (periodId ==2L){
            scores.setSwitchs(switchs.getFtSwitch());
        }else if (periodId ==21L){
            scores.setSwitchs(switchs.getAllSwitch());
        }
    }

    /**
     * 修改标准比分
     * @param scores
     * @return
     */
    public Response editStandScores(StandardScoreCenter scores,StandardMatchScores standardMatchScores,StandardMatchInfo standardMatchInfo){
        log.info("足球标准比分修改:{},{}",scores.getStandardMatchId(),JSON.toJSONString(scores));
        //修改前的比分 数据库查询出来
        String scoresJson = standardMatchScores.getScoreJson();
        Map<Long, BasketballScores> allPeriodScores = new HashMap<>();
        if(StringUtils.isNotBlank(scoresJson)) {
            JSONObject periodBaksetballScores = JSONObject.parseObject(scoresJson);
            allPeriodScores = JsonMapUtils.parseBasketballMap(periodBaksetballScores);
        }
        //要修改的比分,前端传
        List<StandardScoreDTO> editScores = scores.getScores();
        if(editScores.isEmpty()){
            log.info("篮球标准比分修改:{}  editScores.isEmpty()",scores.getStandardMatchId());
            return Response.failed("比分解析异常:无比分");
        }
        Integer matchLength = standardMatchInfo.getMatchLength();
        if(matchLength==null){
            matchLength=0;
        }
        //编辑校验
        Integer rtnFlag = checkEditScores(scores,standardMatchInfo.getMatchLength(),standardMatchInfo.getMatchPeriodId());
        if(rtnFlag!=0){
            return Response.failed(rtnFlag.toString());
        }
        if(7 == matchLength){
            //编辑校验
            Integer editFlag = checkEditMinScores(scores);
            if(editFlag!=0){
                log.info("篮球标准比分修改校验 standardMatchId:{},editFlag:{} ",scores.getStandardMatchId(),editFlag);
                return Response.failed(editFlag.toString());
            }
        }
        //半场比分
        int hfHome=0, ftHome=0;
        int hfAway=0, ftAway=0;
        //加时比分
        Integer otHome=null, home=0;
        Integer otAway=null, away=0;

        List<Long> period0 = new ArrayList<>(Arrays.asList(13L,14L,15L,16L,40L));
        List<Long> period20 = new ArrayList<>(Arrays.asList(1L, 2L,40L));
        for (StandardScoreDTO score : editScores) {
            if(score.getHome()==null || score.getAway()==null){
                continue;
            }
            //计算半场比分全场比分
            if(matchLength==17){
                if(!period20.contains(score.getPeriodId())){
                    continue;
                }
                if(score.getPeriodId()==1L){
                    hfHome=score.getHome();
                    hfAway=score.getAway();
                }else if (score.getPeriodId()==2L){
                    ftHome=score.getHome();
                    ftAway=score.getAway();
                }else if (score.getPeriodId()==40L){
                    if(otHome==null) otHome = 0;
                    if(otAway==null) otAway = 0;
                    otHome=score.getHome();
                    otAway=score.getAway();
                }
            }else if(matchLength==73){
                if(score.getPeriodId()==21L) {
                    home += score.getHome();
                    away += score.getAway();
                }
            }else{
                if(!period0.contains(score.getPeriodId())){
                    continue;
                }
                if(score.getPeriodId()==13L || score.getPeriodId()==14L){
                    hfHome+=score.getHome();
                    hfAway+=score.getAway();
                }else if (score.getPeriodId()==15L || score.getPeriodId()==16L){
                    ftHome+=score.getHome();
                    ftAway+=score.getAway();
                }else if (score.getPeriodId()==40L){
                    if(otHome==null) otHome = 0;
                    if(otAway==null) otAway = 0;
                    otHome+=score.getHome();
                    otAway+=score.getAway();
                }
            }
            CommonItem scoreItem = new CommonItem();
            scoreItem.setHome(score.getHome());
            scoreItem.setAway(score.getAway());
            //保留数据商源比分,替换进球比分
            if(allPeriodScores.get(score.getPeriodId())==null){
                allPeriodScores.put(score.getPeriodId(),new BasketballScores(score.getPeriodId()));
            }
            allPeriodScores.get(score.getPeriodId()).setMatchScore(scoreItem);
        }
        if(matchLength!=73 && matchLength!=17) {
//            if (allPeriodScores.get(1L) == null) {
                BasketballScores basketballScores301 = new BasketballScores();
                basketballScores301.setMatchScore(new CommonItem(hfHome, hfAway));
                allPeriodScores.put(1L, basketballScores301);
//            }
//            if (allPeriodScores.get(2L) == null) {
//                if (ftHome != null || ftAway != 0) {
                    BasketballScores basketballScores302 = new BasketballScores();
                    basketballScores302.setMatchScore(new CommonItem(ftHome, ftAway));
                    allPeriodScores.put(2L, basketballScores302);
//                }
//            }
            BasketballScores basketballScores100 = new BasketballScores();
            basketballScores100.setMatchScore(new CommonItem(hfHome+ftHome, hfAway+ftAway));
            allPeriodScores.put(100L, basketballScores100);
//            if (allPeriodScores.get(40L) == null) {
                if( otHome != null &&  otAway != null) {
                    BasketballScores basketballScores40 = new BasketballScores();
                    basketballScores40.setMatchScore(new CommonItem(otHome, otAway));
                    allPeriodScores.put(40L, basketballScores40);
                }
//            }

            if (otHome!=null && ftHome!=0) {
                BasketballScores basketballScores307 = new BasketballScores();
                basketballScores307.setMatchScore(new CommonItem(ftHome + otHome, otAway + ftAway));
                allPeriodScores.put(307L, basketballScores307);
            }
        }

        //保留数据商源比分,替换进球比分
        if(allPeriodScores.get(-1L)==null){
            allPeriodScores.put(-1L,new BasketballScores(-1L));
        }
        BasketballScores basketballScores = new BasketballScores();
        if(otHome==null) {
            otHome = 0;
        }
        if(otAway==null) {
            otAway= 0;
        }
        basketballScores.setMatchScore(new CommonItem(otHome+hfHome+ftHome+home,otAway+hfAway+ftAway+away));
        allPeriodScores.get(-1L).setMatchScore(basketballScores.getMatchScore());
        log.info("篮球修改标准比分:matchId={},allPeriodScores={}",scores.getStandardMatchId(),JSONUtil.toJsonStr(allPeriodScores));
        if(7 == matchLength){
            List<StandardScoresSixDetailDTO> minute6Scores = scores.getMinute6Scores();
            if(minute6Scores !=null && !minute6Scores.isEmpty()){
                log.info("篮球标准比分修改 组装6分钟比分 standardMatchId:{} ,scores:{}",scores.getStandardMatchId(),minute6Scores);
                for (StandardScoresSixDetailDTO score : minute6Scores) {
                    if(score.getPeriodId()==6L){
                        log.info("篮球标准比分修改 组装6分钟比分 阶段6：standardMatchId:{} ,scores:{}",scores.getStandardMatchId(),score);
                        if(score.getQ1Home()!=null && score.getQ1Away()!=null){
                            BasketballScores basketballScores6Min1312 = new BasketballScores();
                            basketballScores6Min1312.setMatchScore(new CommonItem(score.getQ1Home(),score.getQ1Away()));
                            allPeriodScores.put(1312L,basketballScores6Min1312);
                        }
                        if(score.getQ2Home()!=null && score.getQ2Away()!=null){
                            BasketballScores basketballScores6Min1412 = new BasketballScores();
                            basketballScores6Min1412.setMatchScore(new CommonItem(score.getQ2Home(),score.getQ2Away()));
                            allPeriodScores.put(1412L,basketballScores6Min1412);
                        }
                        if(score.getQ3Home()!=null && score.getQ3Away()!=null){
                            BasketballScores basketballScores6Min1512 = new BasketballScores();
                            basketballScores6Min1512.setMatchScore(new CommonItem(score.getQ3Home(),score.getQ3Away()));
                            allPeriodScores.put(1512L,basketballScores6Min1512);
                        }
                        if(score.getQ4Home()!=null && score.getQ4Away()!=null){
                            BasketballScores basketballScores6Min1612 = new BasketballScores();
                            basketballScores6Min1612.setMatchScore(new CommonItem(score.getQ4Home(),score.getQ4Away()));
                            allPeriodScores.put(1612L,basketballScores6Min1612);
                        }

                    }else if(score.getPeriodId()==12L){
                        log.info("篮球标准比分修改 组装6分钟比分 阶段12：standardMatchId:{} ,scores:{}",scores.getStandardMatchId(),score);
                        if(score.getQ1Home()!=null && score.getQ1Away()!=null){
                            BasketballScores basketballScores6Min1306 = new BasketballScores();
                            basketballScores6Min1306.setMatchScore(new CommonItem(score.getQ1Home(),score.getQ1Away()));
                            allPeriodScores.put(1306L,basketballScores6Min1306);
                        }
                        if(score.getQ2Home()!=null && score.getQ2Away()!=null){
                            BasketballScores basketballScores6Min1406 = new BasketballScores();
                            basketballScores6Min1406.setMatchScore(new CommonItem(score.getQ2Home(),score.getQ2Away()));
                            allPeriodScores.put(1406L,basketballScores6Min1406);
                        }
                        if(score.getQ3Home()!=null && score.getQ3Away()!=null){
                            BasketballScores basketballScores6Min1506 = new BasketballScores();
                            basketballScores6Min1506.setMatchScore(new CommonItem(score.getQ3Home(),score.getQ3Away()));
                            allPeriodScores.put(1506L,basketballScores6Min1506);
                        }
                        if(score.getQ4Home() !=null && score.getQ4Away()!=null){
                            BasketballScores basketballScores6Min1606 = new BasketballScores();
                            basketballScores6Min1606.setMatchScore(new CommonItem(score.getQ4Home(),score.getQ4Away()));
                            allPeriodScores.put(1606L,basketballScores6Min1606);
                        }
                    }
                }
            }
        }
        //重新设置比分数据
        standardMatchScores.setScoreJson(JSONUtil.toJsonStr(allPeriodScores));

        //数据源开关联动
        setSwitch(standardMatchScores,scoresJson,scores);

        super.updateEndSendScoresInfo(standardMatchScores,standardMatchInfo);
        //添加日志
        super.editScoreCenterSettleLog(scoresJson,standardMatchScores,scores,standardMatchInfo.getMatchLength());

        return Response.success();
    }

        private static Integer checkEditMinScores(StandardScoreCenter scores) {
            Integer flag = 0;
            List<StandardScoresSixDetailDTO> minuteScores = scores.getMinute6Scores();
            if(minuteScores==null || minuteScores.isEmpty()){
                return flag;
            }
            List<StandardScoreDTO> scoresq1 = scores.getScores().stream().filter(s -> s.getPeriodId()==13L).collect(Collectors.toList());
            List<StandardScoreDTO> scoresq2 = scores.getScores().stream().filter(s -> s.getPeriodId()==14L).collect(Collectors.toList());
            List<StandardScoreDTO> scoresq3 = scores.getScores().stream().filter(s -> s.getPeriodId()==15L).collect(Collectors.toList());
            List<StandardScoreDTO> scoresq4 = scores.getScores().stream().filter(s -> s.getPeriodId()==16L).collect(Collectors.toList());
            Boolean checkNull13 = (scoresq1.get(0).getHome()!=null && scoresq1.get(0).getAway()==null) || (scoresq1.get(0).getHome()==null && scoresq1.get(0).getAway()!=null);
            Boolean checkNull14 = (scoresq2.get(0).getHome()!=null && scoresq2.get(0).getAway()==null) || (scoresq2.get(0).getHome()==null && scoresq2.get(0).getAway()!=null);
            Boolean checkNull15 = (scoresq3.get(0).getHome()!=null && scoresq3.get(0).getAway()==null) || (scoresq3.get(0).getHome()==null && scoresq3.get(0).getAway()!=null);
            Boolean checkNull16 = (scoresq4.get(0).getHome()!=null && scoresq4.get(0).getAway()==null) || (scoresq4.get(0).getHome()==null && scoresq4.get(0).getAway()!=null);
            if(checkNull13 || checkNull14 || checkNull15 || checkNull16){
                log.info("篮球编辑6分钟比分区间：必须双方都有比分{}，{}，{}，{}",checkNull13 , checkNull14, checkNull15 , checkNull16);
                return OperateLogTypeEnum.EDIT_TIPS_MSG_09.getCode();
            }
            for (StandardScoresSixDetailDTO score : scores.getMinute6Scores()) {
                Boolean checkNull1 = (score.getQ1Home()!=null && score.getQ1Away()==null) || (score.getQ1Home()==null && score.getQ1Away()!=null);
                Boolean checkNull2 = (score.getQ2Home()!=null && score.getQ2Away()==null) || (score.getQ2Home()==null && score.getQ2Away()!=null);
                Boolean checkNull3 = (score.getQ3Home()!=null && score.getQ3Away()==null) || (score.getQ3Home()==null && score.getQ3Away()!=null);
                Boolean checkNull4 = (score.getQ4Home()!=null && score.getQ4Away()==null) || (score.getQ4Home()==null && score.getQ4Away()!=null);
                if(checkNull1 || checkNull2 || checkNull3 || checkNull4){
                    log.info("篮球编辑6分钟比分区间：必须双方都有比分{}，{}，{}，{}",checkNull1,checkNull2,checkNull3,checkNull4);
                    return OperateLogTypeEnum.EDIT_TIPS_MSG_09.getCode();
                }
                if(score.getPeriodId()==12){
                    continue;
                }
                if(!scoresq1.isEmpty()&& scoresq1.get(0).getHome()!=null && scoresq1.get(0).getAway()!=null){
                    if(score.getQ1Home()==null || score.getQ1Away()==null){
                        log.info("篮球编辑6分钟比分区间：必须双方都有比分scoresq1：{}，{}，{}，{}",
                                scoresq1.get(0).getHome(),scoresq1.get(0).getAway(),score.getQ1Home(),score.getQ1Away());
                        return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
                    }
                }
                if(!scoresq2.isEmpty()&& scoresq2.get(0).getHome()!=null && scoresq2.get(0).getAway()!=null){
                    if(score.getQ2Home()==null || score.getQ2Away()==null){
                        log.info("篮球编辑6分钟比分区间：必须双方都有比分scoresq2：{}，{}，{}，{}",
                                scoresq2.get(0).getHome(),scoresq2.get(0).getAway(),score.getQ2Home(),score.getQ2Away());
                        return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
                    }
                }
                if(!scoresq3.isEmpty()&& scoresq3.get(0).getHome()!=null && scoresq3.get(0).getAway()!=null){
                    if(score.getQ3Home()==null || score.getQ3Away()==null){
                        log.info("篮球编辑6分钟比分区间：必须双方都有比分scoresq3：{}，{}，{}，{}",
                                scoresq3.get(0).getHome(),scoresq3.get(0).getAway(),score.getQ3Home(),score.getQ3Away());
                        return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
                    }
                }
                if(!scoresq4.isEmpty()&& scoresq4.get(0).getHome()!=null && scoresq4.get(0).getAway()!=null){
                    if(score.getQ4Home()==null || score.getQ4Away()==null){
                        log.info("篮球编辑6分钟比分区间：必须双方都有比分scoresq4：{}，{}，{}，{}",
                                scoresq4.get(0).getHome(),scoresq4.get(0).getAway(),score.getQ4Home(),score.getQ4Away());
                        return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
                    }
                }
            }
            //上半场6分钟
            List<StandardScoresSixDetailDTO> hfScores = minuteScores.stream().filter(s -> s.getPeriodId() == 6L).collect(Collectors.toList());
            //下半场6分钟
            List<StandardScoresSixDetailDTO> ftScores = minuteScores.stream().filter(s -> s.getPeriodId() == 12L).collect(Collectors.toList());
            StandardScoresSixDetailDTO hf = hfScores.get(0);
            StandardScoresSixDetailDTO ft = ftScores.get(0);
            if(scoresq1.get(0).getHome()!=null && scoresq1.get(0).getAway()!=null){
                Integer q1Home =  calculation(hf.getQ1Home(),ft.getQ1Home());
                Integer q1Away =  calculation(hf.getQ1Away(),ft.getQ1Away());
                if(!Objects.equals(scoresq1.get(0).getHome(), q1Home) || !q1Away.equals(scoresq1.get(0).getAway())){
                    log.info("篮球编辑6分钟比分区间不一致：{}，{}，{}",scores.getStandardMatchId(),q1Home+":"+q1Away,scoresq1.get(0).getHome()+":"+scoresq1.get(0).getAway());
                    return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
                }
            }
            if(scoresq2.get(0).getHome()!=null && scoresq2.get(0).getAway()!=null){
                Integer q2Home =  calculation(hf.getQ2Home(),ft.getQ2Home());
                Integer q2Away =  calculation(hf.getQ2Away(),ft.getQ2Away());
                if(!q2Home.equals(scoresq2.get(0).getHome()) || !q2Away.equals(scoresq2.get(0).getAway())){
                    log.info("篮球编辑6分钟比分区间不一致：{}，{}，{}",scores.getStandardMatchId(),q2Home+":"+q2Away,scoresq2.get(0).getHome()+":"+scoresq2.get(0).getAway());
                    return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
                }
            }

            if(scoresq3.get(0).getHome()!=null && scoresq3.get(0).getAway()!=null){
                Integer q3Home =  calculation(hf.getQ3Home(),ft.getQ3Home());
                Integer q3Away =  calculation(hf.getQ3Away(),ft.getQ3Away());
                if(!q3Home.equals(scoresq3.get(0).getHome()) || !q3Away.equals(scoresq3.get(0).getAway())){
                    log.info("篮球编辑6分钟比分区间不一致：{}，{}，{}",scores.getStandardMatchId(),q3Home+":"+q3Away,scoresq3.get(0).getHome()+":"+scoresq3.get(0).getAway());
                    return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
                }
            }
            if(scoresq4.get(0).getHome()!=null && scoresq4.get(0).getAway()!=null){
                Integer q4Home =  calculation(hf.getQ4Home(),ft.getQ4Home());
                Integer q4Away =  calculation(hf.getQ4Away(),ft.getQ4Away());
                if(!q4Home.equals(scoresq4.get(0).getHome()) || !q4Away.equals(scoresq4.get(0).getAway())){
                    log.info("篮球编辑6分钟比分区间不一致：{}，{}，{}",scores.getStandardMatchId(),q4Home+":"+q4Away,scoresq4.get(0).getHome()+":"+scoresq4.get(0).getAway());
                    return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
                }
            }


//        if(!hfScores.isEmpty() && !ftScores.isEmpty()){
//            Boolean isNullq1 = (hfScores.get(0).getQ1Home()==null || hfScores.get(0).getQ1Away()==null ||
//                    ftScores.get(0).getQ1Home()==null || ftScores.get(0).getQ1Away()==null);
//            if(!isNullq1){
//                Integer q1Home =  hfScores.get(0).getQ1Home()+ftScores.get(0).getQ1Home();
//                Integer q1Away =  hfScores.get(0).getQ1Away()+ftScores.get(0).getQ1Away();
//                if(!q1Home.equals(scoresq1.get(0).getHome()) || !q1Away.equals(scoresq1.get(0).getAway())){
//                    log.info("篮球编辑6分钟比分区间不一致：{}，{}，{}",scores.getStandardMatchId(),q1Home+":"+q1Away,scoresq1.get(0).getHome()+":"+scoresq1.get(0).getAway());
//                    return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
//                }
//            }
//            Boolean isNullq2 = (hfScores.get(0).getQ2Home()==null || hfScores.get(0).getQ2Away()==null || ftScores.get(0).getQ2Home()==null || ftScores.get(0).getQ2Away()==null);
//            if(!isNullq2){
//                Integer q2Home =  hfScores.get(0).getQ2Home()+ftScores.get(0).getQ2Home();
//                Integer q2Away =  hfScores.get(0).getQ2Away()+ftScores.get(0).getQ2Away();
//                if(!q2Home.equals(scoresq2.get(0).getHome()) || !q2Away.equals(scoresq2.get(0).getAway())){
//                    log.info("篮球编辑6分钟比分区间不一致：{}，{}，{}",scores.getStandardMatchId(),q2Home+":"+q2Away,scoresq2.get(0).getHome()+":"+scoresq2.get(0).getAway());
//                    return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
//                }
//            }
//            Boolean isNullq3 = (hfScores.get(0).getQ3Home()==null || hfScores.get(0).getQ3Away()==null || ftScores.get(0).getQ3Home()==null || ftScores.get(0).getQ3Away()==null);
//            if(!isNullq3){
//                Integer q3Home =  hfScores.get(0).getQ3Home()+ftScores.get(0).getQ3Home();
//                Integer q3Away =  hfScores.get(0).getQ3Away()+ftScores.get(0).getQ3Away();
//                if(!q3Home.equals(scoresq3.get(0).getHome()) || !q3Away.equals(scoresq3.get(0).getAway())){
//                    log.info("篮球编辑6分钟比分区间不一致：{}，{}，{}",scores.getStandardMatchId(),q3Home+":"+q3Away,scoresq3.get(0).getHome()+":"+scoresq3.get(0).getAway());
//                    return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
//                }
//            }
//
//            Boolean isNullq4 = (hfScores.get(0).getQ4Home()==null || hfScores.get(0).getQ4Away()==null || ftScores.get(0).getQ4Home()==null || ftScores.get(0).getQ4Away()==null);
//            if(!isNullq4){
//                Integer q4Home =  hfScores.get(0).getQ4Home()+ftScores.get(0).getQ4Home();
//                Integer q4Away =  hfScores.get(0).getQ4Away()+ftScores.get(0).getQ4Away();
//                if(!q4Home.equals(scoresq4.get(0).getHome()) || !q4Away.equals(scoresq4.get(0).getAway())){
//                    log.info("篮球编辑6分钟比分区间不一致：{}，{}，{}",scores.getStandardMatchId(),q4Home+":"+q4Away,scoresq4.get(0).getHome()+":"+scoresq4.get(0).getAway());
//                    return OperateLogTypeEnum.EDIT_TIPS_MSG_18.getCode();
//                }
//            }
//
//        }
        return 0;
    }
    private static Integer calculation(Integer home,Integer away){
        if(home==null){
            home = 0;
        }
        if (away==null){
            away = 0;
        }
        return home + away;
    }

    /**
     * 修改数据源联动开关,同步比分
     * @param matchSwitchDTO
     * @param standardMatchScores
     * @param matchScoresInfo
     */
    @Override
    public Boolean editAccoSwitch(StandardMatchSwitchDTO matchSwitchDTO, StandardMatchScores standardMatchScores, MatchScoresInfo matchScoresInfo,StandardMatchInfo standardMatchInfo) {
        try{
            standardMatchScores.setDataSourceAccoSwitch(getStrByIndex(matchSwitchDTO,standardMatchScores));
            standardMatchScores.setScoreJson(getScoresAsSwitch(standardMatchScores,matchSwitchDTO,matchScoresInfo));
            super.updateEndSendScoresInfo(standardMatchScores,standardMatchInfo);
        }catch(Exception e){
            log.error("修改数据源联动开关异常:{}",e);
        }
        return true;

    }

    private String getScoresAsSwitch(StandardMatchScores standardMatchScores, StandardMatchSwitchDTO matchSwitchDTO, MatchScoresInfo matchScoresInfo) {
        log.info("修改开关联动同步比分 matchSwitchDTO:{}==========standardMatchScores:{} " +
                "======================matchScoresInfo:{}",matchSwitchDTO,standardMatchScores,matchScoresInfo);


        Map<Long, BasketballScores> newStandardScores = new HashMap<>();
        if(StrUtil.isNotEmpty(standardMatchScores.getScoreJson())){
            newStandardScores = JSON.parseObject(standardMatchScores.getScoreJson(), new TypeReference<Map<Long, BasketballScores>>() {
            });
        }
        Map<Long, BasketballScores> thirdMatchScores = new HashMap<>();
        BasketballScores thirdWholeSores= new BasketballScores(WHOLE_MATCH);
        if(matchScoresInfo!=null && StrUtil.isNotEmpty(matchScoresInfo.getScoresJson())){
            JSONObject periodBasketballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            thirdMatchScores= JsonMapUtils.parseBasketballMap(periodBasketballScores);
            thirdWholeSores= thirdMatchScores.get(WHOLE_MATCH);
        }
        //足球外的其他球种 index字段传阶段值
        int index = matchSwitchDTO.getIndex();
        log.info("修改开关联动同步比分:index:{}",index);
        if(matchSwitchDTO.getStatus()==1){
            Long period = new Long(index);
            BasketballScores thirdScores = thirdMatchScores.get(period);
            if(thirdScores==null){
                return standardMatchScores.getScoreJson();
            }
            BasketballScores standScores = newStandardScores.get(period);
            if(standScores==null){
                standScores = new BasketballScores(period);
            }
            standScores.setMatchScore(thirdScores.getMatchScore());
            newStandardScores.put(period,standScores);
            if(matchScoresInfo.getMatchLength()!=null && matchScoresInfo.getMatchLength()==7){
                if(period==13L){
                    newStandardScores.put(1306L,thirdMatchScores.get(1306L)==null?new BasketballScores(1306L):thirdMatchScores.get(1306L));
                    newStandardScores.put(1312L,thirdMatchScores.get(1312L)==null?new BasketballScores(1312L):thirdMatchScores.get(1312L));
                }else if (period==14L){
                    newStandardScores.put(1406L,thirdMatchScores.get(1406L)==null?new BasketballScores(1406L):thirdMatchScores.get(1406L));
                    newStandardScores.put(1412L,thirdMatchScores.get(1412L)==null?new BasketballScores(1412L):thirdMatchScores.get(1412L));
                }else if (period==15L){
                    newStandardScores.put(1506L,thirdMatchScores.get(1506L)==null?new BasketballScores(1506L):thirdMatchScores.get(1506L));
                    newStandardScores.put(1512L,thirdMatchScores.get(1512L)==null?new BasketballScores(1512L):thirdMatchScores.get(1512L));
                }else if (period==16L){
                    newStandardScores.put(1606L,thirdMatchScores.get(1606L)==null?new BasketballScores(1606L):thirdMatchScores.get(1606L));
                    newStandardScores.put(1612L,thirdMatchScores.get(1612L)==null?new BasketballScores(1612L):thirdMatchScores.get(1612L));
                }
            }
        }
        if(null == matchScoresInfo.getMatchLength()){
            matchScoresInfo.setMatchLength(0);
        }
        calcWholeScores(newStandardScores,thirdWholeSores,matchScoresInfo.getMatchLength(),100L);
        calcWholeScores(newStandardScores,thirdWholeSores,matchScoresInfo.getMatchLength(),-1L);
        log.info("修改开关联动同步比分 newStandardScores:{}",newStandardScores);
        return JSON.toJSONString(newStandardScores);
    }

    private void calcWholeScores(Map<Long, BasketballScores> newStandardScores,BasketballScores thirdWholeSores,Integer matchLength,Long period) {
        List<Long> basketballScoreCenterPeriod = Arrays.asList(13L, 14L,15L, 16L,40L,21L,1L,2L);
        List<Long> calcScoresPeriod = Arrays.asList(13L, 14L,15L, 16L,40L);
        if(matchLength==null){
            matchLength = 0;
        }
        if(period==100L){
            basketballScoreCenterPeriod = Arrays.asList(13L,14L,15L,16L,21L,1L,2L);
            calcScoresPeriod = Arrays.asList(13L,14L,15L,16L);
        }
        Integer home = 0,away=0;
        for (Long periodId : newStandardScores.keySet()) {
            //查询比分时过滤阶段 5分钟 15分钟区间
            if (!basketballScoreCenterPeriod.contains(periodId)) {
                continue;
            }
            BasketballScores cc = newStandardScores.get(periodId);
            if(matchLength==73){
                home = cc.getMatchScore().getHome();
                away = cc.getMatchScore().getAway();
            }else if(matchLength==17){
                if(periodId==1L || periodId==2L || periodId==40L){
                    home += cc.getMatchScore().getHome();
                    away += cc.getMatchScore().getAway();
                }
            }else{
                if(calcScoresPeriod.contains(periodId)){
                    home += cc.getMatchScore().getHome();
                    away += cc.getMatchScore().getAway();
                }
            }
        }
        if(period==100L){
            if(newStandardScores.get(100L)==null){
                newStandardScores.put(100L,new BasketballScores(100L));
            }else{
                newStandardScores.put(100L,thirdWholeSores);
            }
            newStandardScores.get(100L).setMatchScore(new CommonItem(home,away));
        }else{
            if(newStandardScores.get(WHOLE_MATCH)==null){
                newStandardScores.put(WHOLE_MATCH,new BasketballScores(WHOLE_MATCH));
            }else{
                newStandardScores.put(WHOLE_MATCH,thirdWholeSores);
            }
            newStandardScores.get(WHOLE_MATCH).setMatchScore(new CommonItem(home,away));
        }


    }

    /**
     *
     * @param matchSwitchDTO
     * @param standardMatchScores
     * @return
     */
    private String getStrByIndex(StandardMatchSwitchDTO matchSwitchDTO, StandardMatchScores standardMatchScores) {
        BasketballSwitch switchs = new BasketballSwitch();
        if(StrUtil.isNotEmpty(standardMatchScores.getDataSourceAccoSwitch())){
            switchs = JSON.parseObject(standardMatchScores.getDataSourceAccoSwitch(), BasketballSwitch.class);
        }
        int status = matchSwitchDTO.getStatus();
        if (matchSwitchDTO.getIndex() == 13) {
            switchs.setFirstSwitch(status);
        }else if(matchSwitchDTO.getIndex() == 14) {
            switchs.setSecondSwitch(status);
        }else if(matchSwitchDTO.getIndex() == 15) {
            switchs.setThirdSwitch(status);
        }else if(matchSwitchDTO.getIndex() == 16) {
            switchs.setFourSwitch(status);
        }else if(matchSwitchDTO.getIndex() == 40) {
            switchs.setOtSwitch(status);
        }else if(matchSwitchDTO.getIndex() == 1) {
            switchs.setHfSwitch(status);
        }else if(matchSwitchDTO.getIndex() == 2) {
            switchs.setFtSwitch(status);
        }else if(matchSwitchDTO.getIndex() == 21) {
            switchs.setAllSwitch(status);
        }
        log.info("修改联动开关:{}",switchs);
        return JSON.toJSONString(switchs);
    }

    private void setSwitch(StandardMatchScores standardMatchScores, String scoresJson, StandardScoreCenter scores) {
        //获取修改后前端传的比分
        JSONObject periodBasketballScores = JSONObject.parseObject(standardMatchScores.getScoreJson());
        Map<Long,BasketballScores> newScores = JsonMapUtils.parseBasketballMap(periodBasketballScores);
        //获取修改前数据库的比分
        if(StrUtil.isEmpty(scoresJson)){
            Map<Long, BasketballScores> wholePeriodScores= new HashMap<>();
            BasketballScores footballScores=new BasketballScores(WHOLE_MATCH);
            wholePeriodScores.put(WHOLE_MATCH,footballScores);
            //无比分时初始化比分,用于做开关自动关闭的校验
            scoresJson = JSONObject.toJSONString(wholePeriodScores);
        }
        JSONObject oldScores = JSONObject.parseObject(scoresJson);
        Map<Long,BasketballScores> allPeriodScores2 = JsonMapUtils.parseBasketballMap(oldScores);
        if(allPeriodScores2.isEmpty()){
            allPeriodScores2 = new HashMap<>();
        }
        BasketballSwitch accoSwitchs = getSwitchsConfig(standardMatchScores, newScores, allPeriodScores2,scores);
        standardMatchScores.setDataSourceAccoSwitch(JSONUtil.toJsonStr(accoSwitchs));

    }

    private BasketballSwitch getSwitchsConfig(StandardMatchScores standardMatchScores, Map<Long, BasketballScores> newScores,
                                              Map<Long, BasketballScores> oldScores, StandardScoreCenter scores) {
        BasketballSwitch accoSwitchs = new BasketballSwitch();
        if(!StrUtil.isEmpty(standardMatchScores.getDataSourceAccoSwitch())){
            accoSwitchs = JSONObject.parseObject(standardMatchScores.getDataSourceAccoSwitch(),BasketballSwitch.class);
        }
        //修改前的联动开关串
        List<Long> basketballScoreCenterPeriod = Arrays.asList(13L,14L,1L,15L,16L,2L,40L,21L);
        String matchManageId = standardMatchScores.getMatchManageId();
        StandardMatchSwitchDTO switchDTO = super.setSwitchObj(standardMatchScores,scores);
        //编辑比分自动保存数据源联动开关关的日志
        for(int i=0;i<basketballScoreCenterPeriod.size();i++){

            Long period = basketballScoreCenterPeriod.get(i);
            //对比修改前后比分,变更开关-修改后-前端传比分
            BasketballScores scoresfor = newScores.get(period);
            //对比修改前后比分,变更开关-修改前-数据库比分
            BasketballScores scoresRea = oldScores.get(period);
            if(scoresfor==null && scoresRea==null){
                continue;
            }
            if(scoresfor==null){
                scoresfor = new BasketballScores(period);
            }
            if(scoresRea==null){
                scoresRea = new BasketballScores(period);
            }
            log.info("修改比分获取开关联动1:{}",scoresfor.getMatchScore().doCountScoreStr());
            log.info("{},修改比分获取开关联动2:{}",StrUtil.equals(scoresfor.getMatchScore().doCountScoreStr(),scoresRea.getMatchScore().doCountScoreStr())
                    ,scoresRea.getMatchScore().doCountScoreStr());
            if(!StrUtil.equals(scoresfor.getMatchScore().doCountScoreStr(),scoresRea.getMatchScore().doCountScoreStr())){
                switchDTO.setIndex(period.intValue());
                if(period==13L){
                    if(accoSwitchs.getFirstSwitch()!=0){
                        scoresCenterApiImpl.editSwitchLog(switchDTO,matchManageId);
                    }
                    accoSwitchs.setFirstSwitch(0);
                }else if (period==14L){
                    if(accoSwitchs.getSecondSwitch()!=0){
                        scoresCenterApiImpl.editSwitchLog(switchDTO,matchManageId);
                    }
                    accoSwitchs.setSecondSwitch(0);
                }else if (period==15L){
                    if(accoSwitchs.getThirdSwitch()!=0){
                        scoresCenterApiImpl.editSwitchLog(switchDTO,matchManageId);
                    }
                    accoSwitchs.setThirdSwitch(0);
                }else if (period==16L){
                    if(accoSwitchs.getFourSwitch()!=0){
                        scoresCenterApiImpl.editSwitchLog(switchDTO,matchManageId);
                    }
                    accoSwitchs.setFourSwitch(0);
                }else if (period==1L){
                    if(accoSwitchs.getHfSwitch()!=0){
                        scoresCenterApiImpl.editSwitchLog(switchDTO,matchManageId);
                    }
                    accoSwitchs.setHfSwitch(0);
                }else if (period==2L){
                    if(accoSwitchs.getFtSwitch()!=0){
                        scoresCenterApiImpl.editSwitchLog(switchDTO,matchManageId);
                    }
                    accoSwitchs.setFtSwitch(0);
                }else if (period==40L){
                    if(accoSwitchs.getOtSwitch()!=0){
                        scoresCenterApiImpl.editSwitchLog(switchDTO,matchManageId);
                    }
                    accoSwitchs.setOtSwitch(0);
                }else if (period==21L){
                    if(accoSwitchs.getAllSwitch()!=0){
                        scoresCenterApiImpl.editSwitchLog(switchDTO,matchManageId);
                    }
                    accoSwitchs.setAllSwitch(0);
                }
            }
        }
        return accoSwitchs;

    }


}