package com.panda.merge.utils;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.panda.aocollect.model.MatchScoresHistory;
import com.panda.aocollect.model.MatchScoresHistoryBasketball;
import com.panda.merge.calculation.impl.*;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.*;
import com.panda.merge.dto.scores.CricketOverPushDTO;
import com.panda.merge.dto.scores.MatchScoresBetterDto;
import com.panda.merge.dto.scores.QueryMatchScoresParamDTO;
import com.panda.merge.dto.scores.StandardScoreCenter;
import com.panda.merge.model.*;
import com.panda.merge.mq.message.CommonStandardScoresDto;
import com.panda.merge.mq.message.CommonThirdScoresDto;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.StandardSportMarketSellRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;


/**
 * MQ消息体组装
 */
@Slf4j
@Component
public class MessageBuilderUtils {
    @Autowired
    FootballCalculationServiceImpl footballCalculationService;
    @Autowired
    BasketballCalculationServiceImpl basketballCalculationService;
    @Autowired
    AmericanFootballCalculationServiceImpl americanFootballCalculationService;
    @Autowired
    SnookerCalculationServiceImpl snookerCalculationServiceImpl;
    @Autowired
    TennisCalculationServiceImpl tennisCalculationServiceImpl;
    @Autowired
    TableTennisCalculationServiceImpl tableTennisCalculationServiceImpl;
    @Autowired
    StandardSportMarketSellRepository standardSportMarketSellRepository;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 构建标准比分 STANDARD_MATCH_SCORES
     * @param thirdMatchInfo
     * @param matchScoresInfo
     * @param data
     * @return
     */
    public  CommonStandardScoresDto buildCommonScoresDto(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        CommonStandardScoresDto commonScoresDto =new CommonStandardScoresDto();
        commonScoresDto.setLinkedId(data.getLinkId());
        commonScoresDto.setStandardMatchId(thirdMatchInfo.getReferenceId());
        commonScoresDto.setDataSourceCode(data.getDataSourceCode());
        commonScoresDto.setEventSourceType(data.getSourceType());
        commonScoresDto.setSportId(data.getSportId());
        commonScoresDto.setPeriodId(data.getMatchPeriodId());
//        commonScoresDto.setScoreTime(data.getEventTime());
        commonScoresDto.setScoreTime(System.currentTimeMillis());
        commonScoresDto.setSecondFromStart(data.getSecondsFromStart());
        commonScoresDto.setEventCode(data.getEventCode());
        commonScoresDto.setHomeAway(data.getHomeAway());
        commonScoresDto.setAddition5(data.getAddition5());
        if ("pd_basketball_delete".equals(data.getAddition2()) || "pd_basketball_update".equals(data.getAddition2())) {
            Long secondFromStart = data.getAddition1() == null ? data.getSecondsFromStart() : Long.parseLong(data.getAddition1());
            commonScoresDto.setSecondFromStart(secondFromStart);
        }
        /**网球需要局数*/
        commonScoresDto.setSecondNum(data.getSecondNum());
        commonScoresDto.setFirstNum(data.getFirstNum());
        if(matchScoresInfo.getSportId().equals(1L)){
            commonScoresDto.setAllScores(footballCalculationService.buildMatchScoreByMap(matchScoresInfo.getScoresJson()));
            commonScoresDto.setMinuteScores(JsonMapUtils.transfer15MinsJsonMap(matchScoresInfo.getScoresJson()));
            extraExpectationScores(thirdMatchInfo, matchScoresInfo);
        }
        if(matchScoresInfo.getSportId().equals(2L)){
            commonScoresDto.setAllScores(basketballCalculationService.buildStandardMatchScoreByMap(matchScoresInfo.getScoresJson()));
        }
        if(SportTypeEnum.AMERICAN_FOOTBALL.getValue().equals(matchScoresInfo.getSportId())){
            commonScoresDto.setAllScores(americanFootballCalculationService.buildStandardMatchScoreByMap(matchScoresInfo.getScoresJson()));
        }
        if(SportTypeEnum.SNOOKER.getValue().equals(matchScoresInfo.getSportId())){
            matchScoresInfo.setScoresJson(snookerCalculationServiceImpl.buildStandardMatchScoreByMap(matchScoresInfo.getScoresJson(),data.getLinkId()));
        }
        if(StringUtils.isNotEmpty(matchScoresInfo.getScoresJsonExtra()) && !SportTypeEnum.CRICKET_BALL.getValue().equals(matchScoresInfo.getSportId())){
            JSONObject extrayScore=JSONObject.parseObject(matchScoresInfo.getScoresJsonExtra());
            commonScoresDto.setExtraScores(extrayScore);
        }
        if(SportTypeEnum.TABLE_TENNIS.getValue().equals(matchScoresInfo.getSportId())){
            JSONObject jsonExtra = tableTennisCalculationServiceImpl.buildStandardMatchScoreHisByMap(data.getStandardMatchId(),matchScoresInfo.getScoresJson(),matchScoresInfo.getScoresJsonExtra(),data.getMatchPeriodId(),data.getLinkId());
            commonScoresDto.setExtraScores(jsonExtra);
            matchScoresInfo.setScoresJsonExtra(JSONUtil.toJsonStr(jsonExtra));
            matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        }
        if(SportTypeEnum.CRICKET_BALL.getValue().equals(matchScoresInfo.getSportId())){
//            pushDeliveryOver(data);
            log.info("{} 板球轮数：{}，{}，",data.getLinkId(),data.getAddition1(),matchScoresInfo.getScoresJsonExtra());
            commonScoresDto.setOver(matchScoresInfo.getScoresJsonExtra());

        }

        commonScoresDto.setScores(JsonMapUtils.transferSimpleJsonMap(matchScoresInfo.getScoresJson()));
        return commonScoresDto;
    }

    /**
     * 推送板球发球轮数
     * @param data
     */
    private void pushDeliveryOver(MatchEventInfo data) {
        if(!"delivery".equals(data.getEventCode()) || ObjectUtils.isEmpty(data.getAddition1())){
            return;
        }
        log.info("{} deliveryOver板球推送:赛事ID：{}，轮数：{}", data.getLinkId(),data.getStandardMatchId(), data.getAddition1());
        Request<String> reqMessage = new Request<>();
        reqMessage.setLinkId(data.getStandardMatchId().toString());
        CricketOverPushDTO cricketOverPushDTO = new CricketOverPushDTO();
        cricketOverPushDTO.setStandardMatchId(data.getStandardMatchId());
        cricketOverPushDTO.setOver(data.getAddition1());
        log.info("linkId::{}::pushDeliveryOver,livedata 推送轮数到ws服务开始:{}", data.getLinkId(), cricketOverPushDTO);
        reqMessage.setData(JSONObject.toJSONString(cricketOverPushDTO, SerializerFeature.DisableCircularReferenceDetect));
        MessageBuilder<Request<String>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, reqMessage.getLinkId());
        rocketMqTemplate.send("CRICKET_OVER_MATCH_PUSH" +":" +reqMessage.getLinkId(), builder.build());
        log.info("linkId::{}::pushDeliveryOver,livedata 推送轮数到ws服务结束", data.getLinkId());

    }

    /**
     * 组装预期失球、预期进球数据
     * @param thirdMatchInfo 三方赛事
     * @param matchScoresInfo 比分
     */
    private void extraExpectationScores(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo) {
        if(thirdMatchInfo==null){
            return;
        }
        Map<Long, FootballScores> scoresMap = JSONObject.parseObject(matchScoresInfo.getScoresJson(), new TypeReference<Map<Long, FootballScores>>() {});
        FootballScores wholeScores = scoresMap.get(WHOLE_MATCH);
        wholeScores.setExpectationLoss(new CommonItemBigDecimal(thirdMatchInfo.getHomeExpectationLoss(), thirdMatchInfo.getAwayExpectationLoss()));
        wholeScores.setExpectationXg(new CommonItemBigDecimal(thirdMatchInfo.getHomeExpectationXg(), thirdMatchInfo.getAwayExpectationXg()));
        log.info("组装预期进失球：{}",scoresMap);
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(scoresMap));

    }


    /**
     * 构建标准比分 STANDARD_MATCH_SCORES
     * @param standardMatchScores
     * @param data
     * @return
     */
    public  CommonStandardScoresDto buildStandardMatchScoreCommonScoresDto(StandardMatchScores standardMatchScores, MatchEventInfo data,MatchScoresInfo thirdMatchScoresInfos) {
        String  extraScoreJson = null;
        if(thirdMatchScoresInfos!=null){
            extraScoreJson=  thirdMatchScoresInfos.getScoresJsonExtra();
        }
        CommonStandardScoresDto commonScoresDto =new CommonStandardScoresDto();
        commonScoresDto.setStandardMatchId(standardMatchScores.getMatchId());
        commonScoresDto.setLinkedId(data.getLinkId());
        commonScoresDto.setDataSourceCode(data.getDataSourceCode());
        commonScoresDto.setEventSourceType(data.getSourceType());
        commonScoresDto.setSportId(data.getSportId());
        commonScoresDto.setPeriodId(data.getMatchPeriodId());
        commonScoresDto.setScoreTime(System.currentTimeMillis());
        commonScoresDto.setSecondFromStart(data.getSecondsFromStart());
        commonScoresDto.setDataSourceCode(standardMatchScores.getDataSourceCode());
        /**网球需要局数*/
        commonScoresDto.setSecondNum(data.getSecondNum());
        commonScoresDto.setFirstNum(data.getFirstNum());
        commonScoresDto.setEventCode(data.getEventCode());
        commonScoresDto.setHomeAway(data.getHomeAway());
        commonScoresDto.setAddition5(data.getAddition5());
//        if(SportTypeEnum.AMERICAN_FOOTBALL.getValue().equals(standardMatchScores.getSportId())){
//            commonScoresDto.setAllScores(americanFootballCalculationService.buildStandardMatchScoreByMap(standardMatchScores.getScoreJson()));
//        }
        if(standardMatchScores.getSportId().equals(1L)){
            commonScoresDto.setAllScores(footballCalculationService.buildMatchScoreByMap(standardMatchScores.getScoreJson()));
            commonScoresDto.setMinuteScores(JsonMapUtils.transfer15MinsJsonMap(standardMatchScores.getScoreJson()));
//            extraExpectationScores(null, standardMatchScores);
        }
        if(standardMatchScores.getSportId().equals(2L)){
            commonScoresDto.setAllScores(basketballCalculationService.buildStandardMatchScoreByMap(standardMatchScores.getScoreJson()));
        }
        if(SportTypeEnum.TENNIS.getValue().equals(standardMatchScores.getSportId())){
            tennisCalculationServiceImpl.buildStandardMatchScoreByMap(standardMatchScores,data.getMatchPeriodId(),data.getEventCode());
        }
        //局内比分后处理
        if(StringUtils.isNotEmpty(extraScoreJson)){
            JSONObject extrayScore=JSONObject.parseObject(extraScoreJson);
            commonScoresDto.setExtraScores(extrayScore);
        }
        //乒乓球extraInfoScore保存历史比分
        if(SportTypeEnum.TABLE_TENNIS.getValue().equals(standardMatchScores.getSportId())){
            JSONObject jsonExtra = tableTennisCalculationServiceImpl.buildStandardMatchScoreHisByMap
                    (data.getStandardMatchId(),standardMatchScores.getScoreJson(),extraScoreJson,data.getMatchPeriodId(),data.getLinkId());
//            Map<Long, TableTennisScores> allPeriodScores = tableTennisCalculationServiceImpl.calcStandardSetScore(standardMatchScores.getScoreJson(),data.getLinkId());
            if(jsonExtra!=null){
                log.info("buildStandardMatchScoreHisByMap 乒乓球历史比分:{},{}",data.getLinkId(),jsonExtra);
                commonScoresDto.setExtraScores(jsonExtra);
                thirdMatchScoresInfos.setScoresJsonExtra(JSONUtil.toJsonStr(jsonExtra));
            }
//            if(allPeriodScores!=null){
//                log.info("buildStandardMatchScoreHisByMap 乒乓球setScore总比分:{},{}",data.getLinkId(),allPeriodScores);
//                standardMatchScores.setScoreJson(JSONObject.toJSONString(allPeriodScores));
//                thirdMatchScoresInfos.setScoresJson(JSONObject.toJSONString(allPeriodScores));
//            }
            matchScoreInfoRepository.updateScoresInfo(thirdMatchScoresInfos);
        }
        commonScoresDto.setScores(JsonMapUtils.transferSimpleJsonMap(standardMatchScores.getScoreJson()));
        return commonScoresDto;
    }


    /**
     * 构建三分赛事比分 THIRD_MATCH_SCORES
     * @param thirdMatchInfo
     * @param matchScoresInfo
     * @param data
     * @return
     */
    public  CommonThirdScoresDto buildThirdScoresDto(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo, MatchEventInfo data) {
        CommonThirdScoresDto commonScoresDto =new CommonThirdScoresDto();
        commonScoresDto.setLinkedId(data.getLinkId());
        commonScoresDto.setThirdMatchId(matchScoresInfo.getThirdMatchId());
        commonScoresDto.setDataSourceCode(data.getDataSourceCode());
        commonScoresDto.setEventSourceType(data.getSourceType());
        commonScoresDto.setEventId(data.getId());
        commonScoresDto.setSportId(data.getSportId());
        commonScoresDto.setPeriodId(data.getMatchPeriodId());
//        commonScoresDto.setScoreTime(data.getEventTime());
        commonScoresDto.setScoreTime(System.currentTimeMillis());
        commonScoresDto.setStandardMatchId(thirdMatchInfo.getReferenceId());
        commonScoresDto.setUserName(matchScoresInfo.getScoresJsonType());
        //新增时间
        commonScoresDto.setSecondFromStart(data.getSecondsFromStart());
        if ("pd_basketball_delete".equals(data.getAddition2()) || "pd_basketball_update".equals(data.getAddition2())) {
            Long secondFromStart = data.getAddition1() == null ? data.getSecondsFromStart() : Long.parseLong(data.getAddition1());
            commonScoresDto.setSecondFromStart(secondFromStart);
        }
        if(StringUtils.isNotEmpty(matchScoresInfo.getScoresJsonExtra())  && !SportTypeEnum.CRICKET_BALL.getValue().equals(matchScoresInfo.getSportId())){
            JSONObject extrayScore=JSONObject.parseObject(matchScoresInfo.getScoresJsonExtra());
            commonScoresDto.setExtraScores(extrayScore);
        }
        if(matchScoresInfo.getSportId().equals(1L)){
            commonScoresDto.setAllScores(footballCalculationService.buildMatchScoreByMap(matchScoresInfo.getScoresJson()));
            commonScoresDto.setMinuteScores(JsonMapUtils.transfer15MinsJsonMap(matchScoresInfo.getScoresJson()));
//            log.info("15minuteScore {}:事件三方赛事:{} ,标准赛事ID：{} 的15分钟比分为:{}",data.getLinkId(),thirdMatchInfo.getId(),thirdMatchInfo.getReferenceId(),commonScoresDto.getMinuteScores());
        }
        if(matchScoresInfo.getSportId().equals(2L)){
            commonScoresDto.setAllScores(basketballCalculationService.buildThirdMatchScoreByMap(matchScoresInfo.getScoresJson()));
//            extraBasketballSixScores(commonScoresDto,matchScoresInfo);
        }
        if(SportTypeEnum.AMERICAN_FOOTBALL.getValue().equals(matchScoresInfo.getSportId())){
            commonScoresDto.setAllScores(americanFootballCalculationService.buildStandardMatchScoreByMap(matchScoresInfo.getScoresJson()));
        }
        commonScoresDto.setScores(JsonMapUtils.transferSimpleJsonMap(matchScoresInfo.getScoresJson()));
        commonScoresDto.setEventId(data.getId());
        return commonScoresDto;
    }

    /**
     * 针对篮球6分钟的比分临时处理方案：三方比分不下发6分钟区间比分
     */
    private void extraBasketballSixScores(CommonThirdScoresDto commonScoresDto,MatchScoresInfo matchScoresInfo) {
        log.info("处理篮球6分钟逻辑开始：{}",commonScoresDto.getLinkedId());
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        List<Long> SIX_SCORES_NUM = new ArrayList<>(Arrays.asList(1312L,1306L,1412L,1406L,1512L,1506L,1612L,1606L));
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellRepository.selectThirdMatchInfoPrimaryKey(commonScoresDto.getStandardMatchId());
        if(standardSportMarketSell != null && standardSportMarketSell.getBusinessEvent() != null){
            if(!standardSportMarketSell.getBusinessEvent().equals(matchScoresInfo.getDataSourceCode())){
                List<Long> removeKey = new ArrayList<>();
                for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
                    Long key = entry.getKey();
                    if(SIX_SCORES_NUM.contains(key)){
                        removeKey.add(key);
                        log.info("::{}::删除6分钟区间比分：{}",commonScoresDto.getLinkedId(),key);
                    }
                }
                if(!removeKey.isEmpty()){
                    allPeriodScores.keySet().removeIf(removeKey::contains);
                }
                matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                log.info("处理篮球6分钟逻辑结束：{},移除key:{},比分：{}",commonScoresDto.getLinkedId(),removeKey,JSONObject.toJSONString(allPeriodScores));
            }
        }

    }


    /**
     * 构建标准赛事比分 STANDARD_MATCH_SCORES
     * @param thirdMatchInfo
     * @param matchScoresInfo
     * @return
     */
    public CommonStandardScoresDto buildCommonScoresDto(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo) {
        CommonStandardScoresDto commonScoresDto =new CommonStandardScoresDto();
        commonScoresDto.setStandardMatchId(thirdMatchInfo.getReferenceId());
        commonScoresDto.setDataSourceCode(matchScoresInfo.getDataSourceCode());
        commonScoresDto.setEventSourceType( Integer.parseInt(matchScoresInfo.getDataSourceType()));
        commonScoresDto.setSportId(matchScoresInfo.getSportId());
        commonScoresDto.setPeriodId(matchScoresInfo.getPeriod());
        commonScoresDto.setScoreTime(System.currentTimeMillis());
        commonScoresDto.setSecondFromStart(matchScoresInfo.getSecondsMatchStart());
        commonScoresDto.setWhetherStop(thirdMatchInfo.getWhetherStop());
        if(matchScoresInfo.getSportId().equals(2L)){
            if(matchScoresInfo.getMatchLength()==3){
                BasketballScores basketballScores =new BasketballScores();
                basketballScores.setMatchScore(new CommonItem());
                basketballScores.getMatchScore().setHome(matchScoresInfo.getT1());
                basketballScores.getMatchScore().setAway(matchScoresInfo.getT2());
                Map<Long, BasketballScores> basketballScoresMap= new HashMap<>();
                basketballScoresMap.put(WHOLE_MATCH,basketballScores);
                commonScoresDto.setScores(JsonMapUtils.transferSimpleJsonMap(JSONObject.toJSONString(basketballScoresMap)));
                return commonScoresDto;
            }
            commonScoresDto.setAllScores(basketballCalculationService.buildStandardMatchScoreByMap(matchScoresInfo.getScoresJson()));
        }
        if(SportTypeEnum.AMERICAN_FOOTBALL.getValue().equals(matchScoresInfo.getSportId())){
            commonScoresDto.setAllScores(americanFootballCalculationService.buildStandardMatchScoreByMap(matchScoresInfo.getScoresJson()));
        }
        commonScoresDto.setScores(JsonMapUtils.transferSimpleJsonMap(matchScoresInfo.getScoresJson()));
        if(StringUtils.isNotEmpty(matchScoresInfo.getScoresJsonExtra())){
            JSONObject extrayScore=JSONObject.parseObject(matchScoresInfo.getScoresJsonExtra());
            commonScoresDto.setExtraScores(extrayScore);
        }
        if(matchScoresInfo.getSportId().equals(1L)){
            commonScoresDto.setAllScores(footballCalculationService.buildMatchScoreByMap(matchScoresInfo.getScoresJson()));
            commonScoresDto.setMinuteScores(JsonMapUtils.transfer15MinsJsonMap(matchScoresInfo.getScoresJson()));
            extraExpectationScores(thirdMatchInfo, matchScoresInfo);
//           11 log.info("15minuteScore {}:事件三方赛事:{} ,标准赛事ID：{} 的15分钟比分为:{}",data.getLinkId(),thirdMatchInfo.getId(),thirdMatchInfo.getReferenceId(),commonScoresDto.getMinuteScores());
        }
//        if(SportTypeEnum.TABLE_TENNIS.getValue().equals(matchScoresInfo.getSportId())){
//            JSONObject jsonExtra = tableTennisCalculationServiceImpl.buildStandardMatchScoreHisByMap(thirdMatchInfo.getReferenceId(),matchScoresInfo.getScoresJson(),matchScoresInfo.getScoresJsonExtra(),matchScoresInfo.getPeriod(),"");
//            commonScoresDto.setExtraScores(jsonExtra);
//            matchScoresInfo.setScoresJsonExtra(jsonExtra.toJSONString());
//            matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
//        }
        return commonScoresDto;
    }

    /**
     * 构建三分赛事比分 THIRD_MATCH_SCORES
     * @param thirdMatchInfo
     * @param matchScoresInfo
     * @return
     */
    public CommonThirdScoresDto buildThirdScoresDto(ThirdMatchInfo thirdMatchInfo, MatchScoresInfo matchScoresInfo) {
        CommonThirdScoresDto commonScoresDto =new CommonThirdScoresDto();
        commonScoresDto.setThirdMatchId(thirdMatchInfo.getId());
        commonScoresDto.setDataSourceCode(matchScoresInfo.getDataSourceCode());
        commonScoresDto.setEventSourceType(Integer.parseInt(matchScoresInfo.getDataSourceType()));
        commonScoresDto.setSportId(matchScoresInfo.getSportId());
        commonScoresDto.setPeriodId(matchScoresInfo.getPeriod());
        commonScoresDto.setScoreTime(System.currentTimeMillis());
        commonScoresDto.setStandardMatchId(thirdMatchInfo.getReferenceId());
        commonScoresDto.setWhetherStop(thirdMatchInfo.getWhetherStop());
        commonScoresDto.setScores(JsonMapUtils.transferSimpleJsonMap(matchScoresInfo.getScoresJson()));
        /**
         * PD PD 2 报球员暂时使用这个字段，比分类型
         * */
        commonScoresDto.setUserName(matchScoresInfo.getScoresJsonType());
        commonScoresDto.setSecondFromStart(matchScoresInfo.getSecondsMatchStart());
        if(matchScoresInfo.getSportId().equals(1L)){
            commonScoresDto.setAllScores(footballCalculationService.buildMatchScoreByMap(matchScoresInfo.getScoresJson()));
        }
        if(StringUtils.isNotEmpty(matchScoresInfo.getScoresJsonExtra())){
            JSONObject extrayScore=JSONObject.parseObject(matchScoresInfo.getScoresJsonExtra());
            commonScoresDto.setExtraScores(extrayScore);
        }
        if(matchScoresInfo.getSportId().equals(2L)){
            if(matchScoresInfo.getMatchLength()==3){
                BasketballScores basketballScores =new BasketballScores();
                basketballScores.setMatchScore(new CommonItem());
                basketballScores.getMatchScore().setHome(matchScoresInfo.getT1());
                basketballScores.getMatchScore().setAway(matchScoresInfo.getT2());
                Map<Long, BasketballScores> basketballScoresMap= new HashMap<>();
                basketballScoresMap.put(WHOLE_MATCH,basketballScores);
                commonScoresDto.setScores(JsonMapUtils.transferSimpleJsonMap(JSONObject.toJSONString(basketballScoresMap)));
                return commonScoresDto;
            }
            commonScoresDto.setAllScores(basketballCalculationService.buildStandardMatchScoreByMap(matchScoresInfo.getScoresJson()));
//            extraBasketballSixScores(commonScoresDto,matchScoresInfo);
        }
        if(SportTypeEnum.AMERICAN_FOOTBALL.getValue().equals(matchScoresInfo.getSportId())){
            commonScoresDto.setAllScores(americanFootballCalculationService.buildStandardMatchScoreByMap(matchScoresInfo.getScoresJson()));
        }
        return commonScoresDto;
    }


    /**/
    public CommonThirdScoresDto buildStandardScoresToThirdScoresDto(ThirdMatchInfo thirdMatchInfo, StandardMatchScores scores,StandardMatchInfo matchInfo,String userName) {
        CommonThirdScoresDto commonScoresDto =new CommonThirdScoresDto();
        commonScoresDto.setThirdMatchId(thirdMatchInfo.getId());
        commonScoresDto.setDataSourceCode("BFZX");
        commonScoresDto.setEventSourceType(1);
        commonScoresDto.setSportId(matchInfo.getSportId());
        commonScoresDto.setPeriodId(matchInfo.getMatchPeriodId()!=null?new Long(matchInfo.getMatchPeriodId()) : 999L);
        commonScoresDto.setScoreTime(System.currentTimeMillis());
        commonScoresDto.setStandardMatchId(matchInfo.getId());
        commonScoresDto.setWhetherStop(thirdMatchInfo.getWhetherStop());
        String settleScores = scores.getScoreJson();
        if(matchInfo.getSportId()==1L){
            settleScores = remove5minScores(scores.getScoreJson());
        }
        commonScoresDto.setScores(JsonMapUtils.transferSimpleJsonMap(settleScores));
        commonScoresDto.setScoreTime(System.currentTimeMillis());
        //标准比分无法获取事件ID，写死赛事ID发给结算服务
        commonScoresDto.setEventId(new Long(matchInfo.getId()+"99999"));
        commonScoresDto.setUserName(userName);
        commonScoresDto.setSecondFromStart(5400L);
        if(matchInfo.getSportId().equals(1L)){
            commonScoresDto.setAllScores(footballCalculationService.buildMatchScoreByMap(scores.getScoreJson()));
        }else if(SportTypeEnum.BASKETBALL.getValue().equals(matchInfo.getSportId())){
            commonScoresDto.setAllScores(basketballCalculationService.buildStandardMatchScoreByMap(scores.getScoreJson()));
        }
        return commonScoresDto;
    }

    /**
     * 删除5分钟区间比分 不下发结算
     * @param scoreJson
     * @return
     */
    private String remove5minScores(String scoreJson) {
        Map<Long, FootballScores> allPeriodScores = new HashMap<>();
        if(StrUtil.isNotEmpty(scoreJson)) {
            JSONObject periodFootballScores = JSONObject.parseObject(scoreJson);
            allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
        }
        if(allPeriodScores.isEmpty()){
            return null;
        }
        Iterator<Map.Entry<Long, FootballScores>> it =  allPeriodScores.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<Long, FootballScores> enter = it.next();
            if(enter.getKey()>999 && enter.getKey() < 60899){
                it.remove();
            }
        }
        return JSONUtil.toJsonStr(allPeriodScores);
    }


    /**
     * 构建开售切换事件源下发标准比分 STANDARD_MATCH_SCORES
     * @param matchScoresBetterDto
     * @param linkId
     * @return
     */
    public CommonStandardScoresDto buildCommonScoresDto(MatchScoresBetterDto matchScoresBetterDto,String linkId) {
        CommonStandardScoresDto commonScoresDto =new CommonStandardScoresDto();
        commonScoresDto.setLinkedId(linkId);
        commonScoresDto.setStandardMatchId( Long.parseLong(matchScoresBetterDto.getMatchId()));
        commonScoresDto.setDataSourceCode(matchScoresBetterDto.getDataSourceCode());
        commonScoresDto.setEventSourceType(Integer.parseInt(matchScoresBetterDto.getDataSourceType()));
        commonScoresDto.setSportId(matchScoresBetterDto.getSportId());
        commonScoresDto.setPeriodId(matchScoresBetterDto.getPeriodNow());
        commonScoresDto.setScoreTime(System.currentTimeMillis());
        if(matchScoresBetterDto.getSportId().equals(1L)){
            commonScoresDto.setAllScores(footballCalculationService.buildMatchScoreByMap(matchScoresBetterDto.getScoresJson()));
            commonScoresDto.setMinuteScores(JsonMapUtils.transfer15MinsJsonMap(matchScoresBetterDto.getScoresJson()));
            log.info("15minuteScore {}:标准赛事ID：{} 的15分钟比分为:{}",linkId,matchScoresBetterDto.getMatchId(),commonScoresDto.getMinuteScores());
        }
        if(matchScoresBetterDto.getSportId().equals(2L)){
            commonScoresDto.setAllScores(basketballCalculationService.buildStandardMatchScoreByMap(matchScoresBetterDto.getScoresJson()));
        }
        commonScoresDto.setScores(JsonMapUtils.transferSimpleJsonMap(matchScoresBetterDto.getScoresJson()));
        return commonScoresDto;
    }

    /**
     * 构建足球AP事件比分 MATCH_SCORES_HISTORY
     * @param matchScoresInfo
     * @param standardMatchId
     * @return
     */
    public MatchScoresHistory buildAoMatchScoresHistory(MatchScoresInfo matchScoresInfo,Long standardMatchId) {
        MatchScoresHistory matchScoresHistory=new MatchScoresHistory();
        if(!checkMatchLength(matchScoresInfo)){
            return null;
        }
        matchScoresHistory.setCreateTime(System.currentTimeMillis());
        matchScoresHistory.setMatchId(standardMatchId);
        matchScoresHistory.setDataSourceCode(matchScoresInfo.getDataSourceCode());
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        if (ObjectUtils.isEmpty(periodFootballScores)) {
            return matchScoresHistory;
        }
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        //全场
        FootballScores wholeScore=allPeriodScores.get(WHOLE_MATCH);
        FootballScores hScore=allPeriodScores.get(6L);
        FootballScores h2Score=allPeriodScores.get(7L);
        if(wholeScore==null||hScore==null||h2Score==null){
            return null;
        }
        FootballScores matchScores=AddScores(hScore,h2Score);

        matchScoresHistory.setAwayCorner(matchScores.getCorner().getAway());
        matchScoresHistory.setAwayRedCard(matchScores.getRedCard().getAway());
        matchScoresHistory.setAwayTeamGoal(matchScores.getGoal().getAway());
        matchScoresHistory.setAwayYellowCard(matchScores.getYellowCard().getAway());

        matchScoresHistory.setHomeCorner(matchScores.getCorner().getHome());
        matchScoresHistory.setHomeRedCard(matchScores.getRedCard().getHome());
        matchScoresHistory.setHomeTeamGoal(matchScores.getGoal().getHome());
        matchScoresHistory.setHomeYellowCard(matchScores.getYellowCard().getHome());

        matchScoresHistory.setCornerScore(matchScores.getCorner().doCountScoreStr());
        matchScoresHistory.setYellowCardScore(matchScores.getYellowCard().doCountScoreStr());
        matchScoresHistory.setRedCardScore(matchScores.getRedCard().doCountScoreStr());
        matchScoresHistory.setRegularScore(matchScores.getGoal().doCountScoreStr());

        matchScoresHistory.setHalftimeScore(hScore.getGoal().doCountScoreStr());

        //加时赛
        FootballScores ahScore=allPeriodScores.get(41L);
        FootballScores ah2Score=allPeriodScores.get(42L);
        if(ahScore==null||ah2Score==null){
            return matchScoresHistory;
        }
        FootballScores aMatchScores=AddScores(ahScore,ah2Score);

        matchScoresHistory.setOvertimeAwayCorner(aMatchScores.getCorner().getAway());
        matchScoresHistory.setOvertimeAwayRedCard(aMatchScores.getRedCard().getAway());
        matchScoresHistory.setOvertimeAwayTeamGoal(aMatchScores.getGoal().getAway());
        matchScoresHistory.setOvertimeAwayYellowCard(aMatchScores.getYellowCard().getAway());

        matchScoresHistory.setOvertimeHomeCorner(aMatchScores.getCorner().getHome());
        matchScoresHistory.setOvertimeHomeRedCard(aMatchScores.getRedCard().getHome());
        matchScoresHistory.setOvertimeHomeTeamGoal(aMatchScores.getGoal().getHome());
        matchScoresHistory.setOvertimeHomeYellowCard(aMatchScores.getYellowCard().getHome());

        matchScoresHistory.setOvertimeCornerScore(aMatchScores.getCorner().doCountScoreStr());
        matchScoresHistory.setOvertimeYellowCardScore(aMatchScores.getYellowCard().doCountScoreStr());
        matchScoresHistory.setOvertimeRedCardScore(aMatchScores.getRedCard().doCountScoreStr());
        matchScoresHistory.setOvertimeScore(aMatchScores.getGoal().doCountScoreStr());
        matchScoresHistory.setOvertimeHalftimeScore(aMatchScores.getGoal().doCountScoreStr());
        //点球
        FootballScores pScore=allPeriodScores.get(50L);
        if(pScore==null){
            return matchScoresHistory;
        }
        matchScoresHistory.setPenaltyScore(pScore.getGoal().doCountScoreStr());
        return matchScoresHistory;
    }

    private FootballScores AddScores(FootballScores hScore, FootballScores h2Score) {
        FootballScores footballScores =new FootballScores(0L);

        footballScores.getGoal().setHome(hScore.getGoal().getHome()+h2Score.getGoal().getHome());
        footballScores.getGoal().setAway(hScore.getGoal().getAway()+h2Score.getGoal().getAway());

        footballScores.getCorner().setHome(hScore.getCorner().getHome()+h2Score.getCorner().getHome());
        footballScores.getCorner().setAway(hScore.getCorner().getAway()+h2Score.getCorner().getAway());

        footballScores.getRedCard().setHome(hScore.getRedCard().getHome()+h2Score.getRedCard().getHome());
        footballScores.getRedCard().setAway(hScore.getRedCard().getAway()+h2Score.getRedCard().getAway());

        footballScores.getYellowCard().setHome(hScore.getYellowCard().getHome()+h2Score.getYellowCard().getHome());
        footballScores.getYellowCard().setAway(hScore.getYellowCard().getAway()+h2Score.getYellowCard().getAway());

        footballScores.getFaCard().setHome(hScore.getFaCard().getHome()+h2Score.getFaCard().getHome());
        footballScores.getFaCard().setAway(hScore.getFaCard().getAway()+h2Score.getFaCard().getAway());
        return footballScores;
    }


    /**
     * 构建篮球AO事件比分
     * @param matchScoresInfo
     * @param standardMatchId
     * @return
     */
    public MatchScoresHistoryBasketball buildBasketballAoMatchScoresHistory(MatchScoresInfo matchScoresInfo, Long standardMatchId) {
        MatchScoresHistoryBasketball basketballMatchAoScores = new MatchScoresHistoryBasketball();
        if(!checkMatchLength(matchScoresInfo)){
            log.info("初盘比分下发错误，篮球比赛长度不对，matchId:{},matchScoresInfo:{}",standardMatchId,matchScoresInfo.getMatchLength());
            return null;
        }
        basketballMatchAoScores.setCreateTime(System.currentTimeMillis());
        basketballMatchAoScores.setMatchId(standardMatchId);
        basketballMatchAoScores.setDataSourceCode(matchScoresInfo.getDataSourceCode());
        //获取/转换 阶段比分
        JSONObject periodBasketballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodBasketballScores);
        BasketballScores wholeScore=allPeriodScores.get(WHOLE_MATCH);
        if(wholeScore==null ){
            log.info("初盘比分下发错误，wholeScore比分为空，matchId:{}",standardMatchId);
            return null;
        }
        //全场
        basketballMatchAoScores.setWholeScores(wholeScore.getMatchScore().doCountScoreStr());
        //节比分
        BasketballScores firstScore = allPeriodScores.get(13L);
        BasketballScores secondScore = allPeriodScores.get(14L);
        BasketballScores thirdScore = allPeriodScores.get(15L);
        BasketballScores fourScore = allPeriodScores.get(16L);
        if(firstScore!=null){
            basketballMatchAoScores.setOneScores(firstScore.getMatchScore().doCountScoreStr());
        }
        if(secondScore!=null) {
            basketballMatchAoScores.setTwoScores(secondScore.getMatchScore().doCountScoreStr());
        }
        if(thirdScore!=null) {
            basketballMatchAoScores.setThreeScores(thirdScore.getMatchScore().doCountScoreStr());
        }
        if(fourScore!=null) {
            basketballMatchAoScores.setFourScores(fourScore.getMatchScore().doCountScoreStr());
        }
        //上半场、下半场
        if(firstScore!=null&&secondScore!=null){
            basketballMatchAoScores.setHalftimeScore(addBsketballScores(firstScore,secondScore).getMatchScore().doCountScoreStr());
        }
        if(thirdScore!=null&&fourScore!=null) {
            basketballMatchAoScores.setSecondHalfScores(addBsketballScores(thirdScore, fourScore).getMatchScore().doCountScoreStr());
        }
        //罚球/两分球/三分球 进球率
        getCountAndRate(basketballMatchAoScores,wholeScore);
        //篮板
        basketballMatchAoScores.setTotalRebound(wholeScore.getRebound().getHome() + wholeScore.getRebound().getAway());
        basketballMatchAoScores.setOffRebound(null);
        basketballMatchAoScores.setDefRebound(null);
        //失误 抢断 盖帽 犯规
        basketballMatchAoScores.setTripUp(null);
        basketballMatchAoScores.setTackle(null);
        basketballMatchAoScores.setBlockShot(null);
        basketballMatchAoScores.setFoul(wholeScore.getFoul().getHome() + wholeScore.getFoul().getAway());
        return basketballMatchAoScores;
    }

    /**
     * 检查赛制
     * 篮球只允许0,7,17三个赛制
     * 足球只允许0,1,9,10,11,46
     * @param matchScoresInfo
     * @return
     */
    private static boolean checkMatchLength(MatchScoresInfo matchScoresInfo) {
        List<Integer> matchLengthsBasketball = new ArrayList<>(Arrays.asList(0,7,17));
        List<Integer> matchLengthsFootball = new ArrayList<>(Arrays.asList(0,1,9,10,11,46));
        if(SportTypeEnum.FOOTBALL.getValue().equals(matchScoresInfo.getSportId())){
            if(matchLengthsFootball.contains(matchScoresInfo.getMatchLength())){
                return true;
            }
        }else  if(SportTypeEnum.BASKETBALL.getValue().equals(matchScoresInfo.getSportId())){
            if(matchLengthsBasketball.contains(matchScoresInfo.getMatchLength())){
                return true;
            }
        }
        return false;
    }

    /**
     * 组装获取命中率进球率
     * @param matchScoresHistory
     * @param wholeScore 比分总数据
     */
    private void getCountAndRate(MatchScoresHistoryBasketball matchScoresHistory, BasketballScores wholeScore) {
        //罚球  出手次数 命中次数 命中率  罚球命中率 = 罚球命中次数/罚球出手次数
        Integer penaltyShotCount = wholeScore.getFreeThrowCount().getHome() + wholeScore.getFreeThrowCount().getAway();
        Integer freeThrowMade = wholeScore.getFreeThrowMade().getHome() + wholeScore.getFreeThrowMade().getAway();
        Float penaltyHitRate = wholeScore.getFreeThrowHitRate().getHome() + wholeScore.getFreeThrowHitRate().getAway() ;
        matchScoresHistory.setFreeThrowHitRate((penaltyHitRate/2)+"");
        matchScoresHistory.setFreeThrowFga(penaltyShotCount);
        matchScoresHistory.setFreeThrowHits(freeThrowMade);

        //两分球 命中率 出手次数 命中次数
        Float twoPointHitRate = wholeScore.getTwoPointerHitRate().getHome() + wholeScore.getTwoPointerHitRate().getAway() ;
        Integer twoPointShotCount = wholeScore.getTwoPointer().getHome() + wholeScore.getTwoPointer().getAway();
        Integer twoPointerMade = wholeScore.getTwoPointerMade().getHome() + wholeScore.getTwoPointerMade().getAway();
        matchScoresHistory.setTwoPointHitRate((twoPointHitRate.intValue()/2 +""));
        matchScoresHistory.setTwoPointFga(twoPointShotCount);
        matchScoresHistory.setTwoPointHits(twoPointerMade);

        //三分球 命中率 出手次数 命中次数
        Float threePointHitRate = (wholeScore.getThreePointerHitRate().getHome() + wholeScore.getThreePointerHitRate().getAway()) ;
        Integer threePointShotCount = wholeScore.getThreePointer().getHome() + wholeScore.getThreePointer().getAway();
        Integer threePointerMade = wholeScore.getThreePointerMade().getHome() + wholeScore.getThreePointerMade().getAway();
        matchScoresHistory.setThreePointHitRate((threePointHitRate.intValue()/ 2)+ "");
        matchScoresHistory.setThreePointFga(threePointShotCount);
        matchScoresHistory.setThreePointHits(threePointerMade);

        //总命中 命中率 出手次数 命中次数
        matchScoresHistory.setTotalHitRate((penaltyShotCount + twoPointShotCount + threePointShotCount)+"");
        matchScoresHistory.setTotalFga(freeThrowMade + twoPointerMade + threePointerMade);
        matchScoresHistory.setTotalHits(calcRate(freeThrowMade + twoPointerMade + threePointerMade , penaltyShotCount + twoPointShotCount + threePointShotCount));
    }


    /**
     * 组装篮球比分
     * ps：这里13+14阶段、15+15阶段的比分 分别为上半场比分和下半场比分
     * @param firstScore 上一节
     * @param secondScore 下一节
     * @return
     */
    private static BasketballScores addBsketballScores(BasketballScores firstScore, BasketballScores secondScore) {
        BasketballScores basketballScores =new BasketballScores(0L);
        basketballScores.getMatchScore().setHome(firstScore.getMatchScore().getHome()+secondScore.getMatchScore().getHome());
        basketballScores.getMatchScore().setAway(firstScore.getMatchScore().getAway()+secondScore.getMatchScore().getAway());
        return basketballScores;
    }

    /**
     * 计算命中率= 命中次数/出手次数
     * @param hitCount
     * @param shotCount
     * @return
     */
    private Integer calcRate(Integer hitCount,Integer shotCount){
       try{
           if(hitCount==null || shotCount==null){
               return 0;
           }
           if(hitCount==0 || shotCount==0){
               return 0;
           }
           Double doubleValue =  new BigDecimal((float)hitCount/shotCount).setScale(2, BigDecimal.ROUND_CEILING).doubleValue() ;
           Integer rate = (int) (doubleValue * 100);
           return rate;
       }catch (Exception e){
           return 0;
       }
    }

    public CommonStandardScoresDto buildRcsQueryMatchScoresDto(MatchScoresBetterDto s, Request<QueryMatchScoresParamDTO> request) {
        CommonStandardScoresDto commonScoresDto =new CommonStandardScoresDto();
        if(s==null){
            return commonScoresDto;
        }
        commonScoresDto.setLinkedId(request.getLinkId());
        commonScoresDto.setStandardMatchId(Long.parseLong(s.getMatchId()));
        commonScoresDto.setSportId(s.getSportId());
        commonScoresDto.setPeriodId(s.getPeriodNow());
        commonScoresDto.setDataSourceCode(s.getDataSourceCode());
        commonScoresDto.setEventSourceType(Integer.valueOf(s.getDataSourceType()));
        commonScoresDto.setScoreTime(s.getNowSystemTime());
        commonScoresDto.setWhetherStop(s.getIsTimeGo());
        commonScoresDto.setScores(JsonMapUtils.transferSimpleJsonMap(s.getScoresJson()));
        commonScoresDto.setFirstNum(s.getCurrentSet());
        commonScoresDto.setSecondNum(s.getCurrentRound());
        commonScoresDto.setScoreTime(System.currentTimeMillis());
        commonScoresDto.setMatchStatus(s.getMatchStatus());
        commonScoresDto.setSecondFromStart(s.getSecondsMatchStart());

        //局内比分后处理、点球大战
        if(StringUtils.isNotEmpty(s.getScoresJsonExtra())){
            JSONObject extrayScore=JSONObject.parseObject(s.getScoresJsonExtra());
            commonScoresDto.setExtraScores(extrayScore);
        }
        if(s.getSportId().equals(1L)){
            commonScoresDto.setAllScores(footballCalculationService.buildMatchScoreByMap(s.getScoresJson()));
            commonScoresDto.setMinuteScores(JsonMapUtils.transfer15MinsJsonMap(s.getScoresJson()));
        }
        return commonScoresDto;
    }


//    public static void main(String[] args) {
//        MatchScoresInfo matchScoresInfo = new MatchScoresInfo();
//        Long standardMatchId = 3566116L;
//        String json = "{\"-1\":{\"foul\":{\"away\":0,\"home\":0},\"freeThrowCount\":{\"away\":0,\"home\":0},\"freeThrowHitRate\":{\"away\":0.0,\"home\":0.0},\"freeThrowMade\":{\"away\":0,\"home\":0},\"freeThrowMiss\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":78,\"home\":66},\"pointerHitRate\":{\"away\":0.0,\"home\":0.0},\"possession\":{\"away\":0.0,\"home\":0.0},\"rebound\":{\"away\":0,\"home\":0},\"threePointer\":{\"away\":0,\"home\":0},\"threePointerHitRate\":{\"away\":0.0,\"home\":0.0},\"threePointerMade\":{\"away\":0,\"home\":0},\"threePointerMiss\":{\"away\":0,\"home\":0},\"timeout\":{\"away\":0,\"home\":0},\"twoPointer\":{\"away\":0,\"home\":0},\"twoPointerHitRate\":{\"away\":0.0,\"home\":0.0},\"twoPointerMade\":{\"away\":0,\"home\":0},\"twoPointerMiss\":{\"away\":0,\"home\":0}},\"16\":{\"foul\":{\"away\":0,\"home\":0},\"freeThrowCount\":{\"away\":0,\"home\":0},\"freeThrowHitRate\":{\"away\":0.0,\"home\":0.0},\"freeThrowMade\":{\"away\":0,\"home\":0},\"freeThrowMiss\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":20,\"home\":21},\"pointerHitRate\":{\"away\":0.0,\"home\":0.0},\"possession\":{\"away\":0.0,\"home\":0.0},\"rebound\":{\"away\":0,\"home\":0},\"threePointer\":{\"away\":0,\"home\":0},\"threePointerHitRate\":{\"away\":0.0,\"home\":0.0},\"threePointerMade\":{\"away\":0,\"home\":0},\"threePointerMiss\":{\"away\":0,\"home\":0},\"timeout\":{\"away\":0,\"home\":0},\"twoPointer\":{\"away\":0,\"home\":0},\"twoPointerHitRate\":{\"away\":0.0,\"home\":0.0},\"twoPointerMade\":{\"away\":0,\"home\":0},\"twoPointerMiss\":{\"away\":0,\"home\":0}},\"40\":{\"foul\":{\"away\":0,\"home\":0},\"freeThrowCount\":{\"away\":0,\"home\":0},\"freeThrowHitRate\":{\"away\":0.0,\"home\":0.0},\"freeThrowMade\":{\"away\":0,\"home\":0},\"freeThrowMiss\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":2,\"home\":3},\"pointerHitRate\":{\"away\":0.0,\"home\":0.0},\"possession\":{\"away\":0.0,\"home\":0.0},\"rebound\":{\"away\":0,\"home\":0},\"threePointer\":{\"away\":0,\"home\":0},\"threePointerHitRate\":{\"away\":0.0,\"home\":0.0},\"threePointerMade\":{\"away\":0,\"home\":0},\"threePointerMiss\":{\"away\":0,\"home\":0},\"timeout\":{\"away\":0,\"home\":0},\"twoPointer\":{\"away\":0,\"home\":0},\"twoPointerHitRate\":{\"away\":0.0,\"home\":0.0},\"twoPointerMade\":{\"away\":0,\"home\":0},\"twoPointerMiss\":{\"away\":0,\"home\":0}},\"13\":{\"foul\":{\"away\":0,\"home\":0},\"freeThrowCount\":{\"away\":0,\"home\":0},\"freeThrowHitRate\":{\"away\":0.0,\"home\":0.0},\"freeThrowMade\":{\"away\":0,\"home\":0},\"freeThrowMiss\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":3,\"home\":1},\"pointerHitRate\":{\"away\":0.0,\"home\":0.0},\"possession\":{\"away\":0.0,\"home\":0.0},\"rebound\":{\"away\":0,\"home\":0},\"threePointer\":{\"away\":0,\"home\":0},\"threePointerHitRate\":{\"away\":0.0,\"home\":0.0},\"threePointerMade\":{\"away\":0,\"home\":0},\"threePointerMiss\":{\"away\":0,\"home\":0},\"timeout\":{\"away\":0,\"home\":0},\"twoPointer\":{\"away\":0,\"home\":0},\"twoPointerHitRate\":{\"away\":0.0,\"home\":0.0},\"twoPointerMade\":{\"away\":0,\"home\":0},\"twoPointerMiss\":{\"away\":0,\"home\":0}},\"14\":{\"foul\":{\"away\":0,\"home\":0},\"freeThrowCount\":{\"away\":0,\"home\":0},\"freeThrowHitRate\":{\"away\":0.0,\"home\":0.0},\"freeThrowMade\":{\"away\":0,\"home\":0},\"freeThrowMiss\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":12,\"home\":11},\"pointerHitRate\":{\"away\":0.0,\"home\":0.0},\"possession\":{\"away\":0.0,\"home\":0.0},\"rebound\":{\"away\":0,\"home\":0},\"threePointer\":{\"away\":0,\"home\":0},\"threePointerHitRate\":{\"away\":0.0,\"home\":0.0},\"threePointerMade\":{\"away\":0,\"home\":0},\"threePointerMiss\":{\"away\":0,\"home\":0},\"timeout\":{\"away\":0,\"home\":0},\"twoPointer\":{\"away\":0,\"home\":0},\"twoPointerHitRate\":{\"away\":0.0,\"home\":0.0},\"twoPointerMade\":{\"away\":0,\"home\":0},\"twoPointerMiss\":{\"away\":0,\"home\":0}},\"15\":{\"foul\":{\"away\":0,\"home\":0},\"freeThrowCount\":{\"away\":0,\"home\":0},\"freeThrowHitRate\":{\"away\":0.0,\"home\":0.0},\"freeThrowMade\":{\"away\":0,\"home\":0},\"freeThrowMiss\":{\"away\":0,\"home\":0},\"matchScore\":{\"away\":35,\"home\":24},\"pointerHitRate\":{\"away\":0.0,\"home\":0.0},\"possession\":{\"away\":0.0,\"home\":0.0},\"rebound\":{\"away\":0,\"home\":0},\"threePointer\":{\"away\":0,\"home\":0},\"threePointerHitRate\":{\"away\":0.0,\"home\":0.0},\"threePointerMade\":{\"away\":0,\"home\":0},\"threePointerMiss\":{\"away\":0,\"home\":0},\"timeout\":{\"away\":0,\"home\":0},\"twoPointer\":{\"away\":0,\"home\":0},\"twoPointerHitRate\":{\"away\":0.0,\"home\":0.0},\"twoPointerMade\":{\"away\":0,\"home\":0},\"twoPointerMiss\":{\"away\":0,\"home\":0}}}\t";
//        matchScoresInfo.setScoresJson(json);
//        matchScoresInfo.setMatchLength(7);
//        matchScoresInfo.setSportId(2L);
//        buildBasketballAoMatchScoresHistory(matchScoresInfo,standardMatchId);
//    }
}
