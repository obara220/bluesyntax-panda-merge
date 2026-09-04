package com.panda.merge.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.advertise.utils.RedisUtils;
import com.panda.merge.calculation.impl.FootballCalculationServiceImpl;
import com.panda.merge.calculation.impl.SnookerCalculationServiceImpl;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.TimeStatusEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.*;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.SaleUpdateLiveBusinessEventMessage;
import com.panda.merge.dto.scores.MatchScoresBetterDto;
import com.panda.merge.dto.sourceSwitch.FootballSwitch;
import com.panda.merge.mapper.MatchScoresSearchMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.MatchTimeInfoRepository;
import com.panda.merge.repository.ScoresRedisHelp;
import com.panda.merge.repository.StandardSportMarketSellRepository;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.panda.merge.common.enums.Constant.PD_FOOTBALL_EVENT_MONITOR;
import static com.panda.merge.common.enums.Constant.REDIS_KEY.RONGHE_PD_FOOTBALL_PUBLIC_EVENT;
import static com.panda.merge.config.RedisConfig.REDIS_HOUR_TIME;
import static com.panda.merge.constant.ConstantSystem.LIVE_BUSINESS_EVENT_UPDATE_MESSAGE;
import static com.panda.merge.constant.ConstantSystem.SOLD_MESSAGE;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * 切换数据源后补发比分（风控后台触发）
 * @author       Aison
 * @createDate  2020年10月23日10:00:10
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = LIVE_BUSINESS_EVENT_UPDATE_MESSAGE,
        consumerGroup = "scores-group-"+ LIVE_BUSINESS_EVENT_UPDATE_MESSAGE,
        consumeThreadMax = 2,
        consumeTimeout = 10000L
)
@DependsOn("scoresAdminApplication")
public class SoldMessageScoresConsumer implements RocketMQListener<Request<SaleUpdateLiveBusinessEventMessage>> {

    @Autowired
    ScoresProducer scoresProducer;
    @Autowired
    MatchScoresSearchMapper matchScoresSearchMapper;
    @Autowired
    FootballCalculationServiceImpl footballCalculationService;
    //    @Autowired
//    MatchScoresInfoMapper matchScoresInfoMapper;
//    @Autowired
//    MatchTimeInfoMapper matchTimeInfoMapper;
//    @Autowired
//    ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    MatchTimeInfoRepository matchTimeInfoRepository;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    StandardSportMarketSellRepository standardSportMarketSellRepository;
    @Autowired
    RedisService redisService;
    @Autowired
    ScoresRedisHelp scoresRedisHelp;
    @Autowired
    ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    SnookerCalculationServiceImpl snookerCalculationService;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
    @Override
    public void onMessage(Request<SaleUpdateLiveBusinessEventMessage> request) {
        log.info("SoldMessageScoresConsumer MQ消费数据开始...{}", datacenterMergeSwitch);
        if (datacenterMergeSwitch && commonProducer.getDatacenterMatchIds(request.getData().getMatchId().toString())) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(request, "datacenter-LIVE_BUSINESS_EVENT_UPDATE_MESSAGE",request.getLinkId());
            return;
        }
        log.info("【SoldMessageScoresConsumer:" + SOLD_MESSAGE + "】【::" + request.getLinkId() + "::】开售处理后补发比分开始");
        //1.查询标准比分
        List<Long> standardIdList = new ArrayList<>();

        if (request.getData() == null || request.getData().getMatchId() == null) {
            return;
        }
        try {
            if(request.getData().getBusinessEventCodeOld().equals(request.getData().getBusinessEventCode())){
                log.info("{}【SoldMessageScoresConsumer:" + SOLD_MESSAGE + "】【::" + request.getData().getMatchId() + "::】新旧数据源一致,不处理和下发比分...",request.getLinkId());
                return;
            }
            //监听到之后需要复制比分
            // 判断切换前后是否包含 PD PD2 比分
            // 如果有则来回切换的是否复制
            if (request.getData().getBusinessEventCode().equals("PD") || request.getData().getBusinessEventCode().equals("PD2") ||
                    request.getData().getBusinessEventCodeOld().equals("PD") || request.getData().getBusinessEventCodeOld().equals("PD2")) {
                StandardMatchInfo matchInfo = standardMatchInfoService.getItem(request.getData().getMatchId());
                if(matchInfo!=null && matchInfo.getMatchPeriodId()==0){
                    log.info("{}【SoldMessageScoresConsumer:" + SOLD_MESSAGE + "】【::" + request.getData().getMatchId() + "::】数据复制暂停，未开赛...",request.getLinkId());
                }
                copyScore(request);
                log.info("{}【SoldMessageScoresConsumer:" + SOLD_MESSAGE + "】【::" + request.getData().getMatchId() + "::】数据复制完成:{}-->{}",
                        request.getLinkId(), request.getData().getBusinessEventCodeOld(), request.getData().getBusinessEventCode());
            }

            // pd切换到其他事件源
            SaleUpdateLiveBusinessEventMessage data = request.getData();
            // 修改前商业事件源
            String businessEventCodeOld = data.getBusinessEventCodeOld();
            // 修改后商业事件源
            String businessEventCode = data.getBusinessEventCode();
            // pd1切换到其他事件源
            boolean pdChangeToOther = businessEventCodeOld.equals(DataSourceCodeEnum.PD.code) && !businessEventCode.equals(DataSourceCodeEnum.PD.code);
            if (pdChangeToOther) {
//                ThirdMatchInfoExample example = new ThirdMatchInfoExample();
//                example.createCriteria().andReferenceIdEqualTo(data.getMatchId()).andDataSourceCodeEqualTo(DataSourceCodeEnum.PD.code).andSportIdEqualTo(data.getSportId());
//                List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoMapper.selectByExample(example);
                ThirdMatchInfo thirdMatchInfoPd = thirdMatchInfoService.getItem(data.getMatchId(), DataSourceCodeEnum.PD.code);
                if (!ObjectUtils.isEmpty(thirdMatchInfoPd)) {
                    Object monitorObj = redisService.get(PD_FOOTBALL_EVENT_MONITOR);
                    if (!ObjectUtils.isEmpty(monitorObj)) {
                        List<FootballEventMonitor> monitorList = JSON.parseObject(monitorObj.toString(), new TypeReference<List<FootballEventMonitor>>() {
                        });
                        AtomicInteger sum = new AtomicInteger();
                        monitorList.forEach(monitor -> monitor.getThirdMatchInfo().forEach(eventInfoDTO -> {
                            if (thirdMatchInfoPd.getId().equals(Long.valueOf(eventInfoDTO.getAddition8()))) {
                                // 时间走表时，把addition4置为0
                                eventInfoDTO.setAddition4(String.valueOf(TimeStatusEnum.PAUSE.getDesc()));
                                sum.incrementAndGet();
                            }
                        }));
                        if (sum.get() > 0) {
                            redisService.set(PD_FOOTBALL_EVENT_MONITOR, JSONObject.toJSON(monitorList).toString(), REDIS_HOUR_TIME * 6);
                        }
                    }
                }
            }
            // pd2切换到其他事件源
            boolean pd2ChangeToOther = businessEventCodeOld.equals(DataSourceCodeEnum.PD2.code) && !businessEventCode.equals(DataSourceCodeEnum.PD2.code);
            if (pd2ChangeToOther) {
//                ThirdMatchInfoExample example = new ThirdMatchInfoExample();
//                example.createCriteria().andReferenceIdEqualTo(data.getMatchId()).andDataSourceCodeEqualTo(DataSourceCodeEnum.PD2.code).andSportIdEqualTo(data.getSportId());
//                List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoMapper.selectByExample(example);
                ThirdMatchInfo thirdMatchInfoPd2 = thirdMatchInfoService.getItem(data.getMatchId(), DataSourceCodeEnum.PD2.code);
                if (!ObjectUtils.isEmpty(thirdMatchInfoPd2)) {
                    Object monitorObj = redisService.get(PD_FOOTBALL_EVENT_MONITOR);
                    if (!ObjectUtils.isEmpty(monitorObj)) {
                        List<FootballEventMonitor> monitorList = JSON.parseObject(monitorObj.toString(), new TypeReference<List<FootballEventMonitor>>() {
                        });
                        AtomicInteger sum = new AtomicInteger();
                        monitorList.forEach(monitor -> monitor.getThirdMatchInfo().forEach(eventInfoDTO -> {
                            if (thirdMatchInfoPd2.getId().equals(Long.valueOf(eventInfoDTO.getAddition8()))) {
                                // 时间走表时，把addition4置为0
                                eventInfoDTO.setAddition4(String.valueOf(TimeStatusEnum.PAUSE.getDesc()));
                                sum.incrementAndGet();
                            }
                        }));
                        if (sum.get() > 0) {
                            redisService.set(PD_FOOTBALL_EVENT_MONITOR, JSONObject.toJSON(monitorList).toString(), REDIS_HOUR_TIME * 6);
                        }
                    }
                }
            }

            //仅限于足球
            standardIdList.add(request.getData().getMatchId());

            log.info("【SoldMessageScoresConsumer:" + SOLD_MESSAGE + "】【::" + request.getLinkId() + "::】开售处理后补发比分:{}", JSONObject.toJSONString(request.getLinkId()));
            MatchScoresBetterDto scores = new MatchScoresBetterDto();
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(request.getData().getMatchId(), request.getData().getBusinessEventCode());
            if (thirdMatchInfo == null) {
                log.info("【SoldMessageScoresConsumer:" + SOLD_MESSAGE + "】【::" + request.getLinkId() + "::】开售处理后补发比分:无三方赛事");
                return;
            }
            MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.LIVE_DATA.getCode());
            if (matchScoresInfo == null || matchScoresInfo.getScoresJson() == null) {
                log.info("【SoldMessageScoresConsumer:" + SOLD_MESSAGE + "】【::" + request.getLinkId() + "::】开售处理后补发比分:无比分：{}---》 {}",
                        thirdMatchInfo.getId(),matchScoresInfo);
                return;
            }
            scores.setScoresJson(matchScoresInfo.getScoresJson());
            scores.setThirdMatchId(thirdMatchInfo.getId());
            scores.setSportId(request.getData().getSportId());
            scores.setMatchId(request.getData().getMatchId().toString());
            scores.setDataSourceCode(request.getData().getBusinessEventCode());
            scores.setT1(matchScoresInfo.getT1());
            scores.setT2(matchScoresInfo.getT2());
            scores.setPeriodT1(matchScoresInfo.getPeriodT1());
            scores.setPeriodT2(matchScoresInfo.getPeriodT2());
            scores.setDataSourceType(matchScoresInfo.getDataSourceType());
            scores.setPeriodNow(matchScoresInfo.getPeriod());
            scores.setMatchLength(matchScoresInfo.getMatchLength());
            scores.setMatchStatus(thirdMatchInfo.getMatchStatus());
            scores.setHomeAwayOpposite(thirdMatchInfo.getHomeAwayOpposite());
            MatchTimeInfo time = matchTimeInfoRepository.selectByPrimaryKey(matchScoresInfo.getId());
            if (time != null) {
                scores.setIsTimeGo(time.getTimeGo());
                scores.setSecondsMatchStart(time.getSecondFromStart());
                scores.setEventTime(time.getEventTime());
                scores.setRemainingTime(time.getRemainingTime());
                scores.setCurrentSet(time.getCurrentSet());
                scores.setCurrentRound(time.getCurrentRound());
            } else {
                scores.setSecondsMatchStart(0L);
            }
            //非支持比分中心的球种,直接下发
            if (!DataSourceConstant.STANDARC_SCORE_SPORTIDS.contains(matchScoresInfo.getSportId())){
                scoresProducer.sendToMQ(scores, request.getLinkId());
            }
            standardSportMarketSellRepository.cleanStandardMatchSell(standardIdList.get(0));

            //15分钟要存储缓存切换
            if (request.getData().getSportId().equals(1L)) {
                //复制控球率
                buildPossession(scores, request.getData());
                footballCalculationService.save15MinToCacheByStandardId(request.getData().getMatchId());
            }
            if((data.getBusinessEventCode().equals("PD")&&data.getBusinessEventCodeOld().equals("PD2"))||(data.getBusinessEventCode().equals("PD2")&&data.getBusinessEventCodeOld().equals("PD"))){
                log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getData().getMatchId()+"::】PD、PD2之间不复制标准比分.");
                return;
            }
            log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getData().getMatchId()+"::】切换事件源完成,复制标准比分并下发.");
            copyStandardScores(scores, request.getLinkId());
        } catch (Exception e) {
            log.error("【SoldMessageScoresConsumer:" + SOLD_MESSAGE + "】【::" + request.getLinkId() + "::】开售处理后补发比分异常:{}", e);
        }
        //2.下发
        log.info("【SoldMessageScoresConsumer:" + SOLD_MESSAGE + "】【::" + request.getLinkId() + "::】开售处理后补发比分结束");
    }

    /**
     * 复制标准比分
     *
     * @param scores
     */
    private void copyStandardScores(MatchScoresBetterDto scores, String linkId) {
        if(!SportTypeEnum.FOOTBALL.getValue().equals(scores.getSportId())){
            scoresProducer.sendToMQ(scores, linkId);
            log.info("非足球切换事件源,比分直接下发,linkId={}",linkId);
            return;
        }
        if(!scores.getSportId().equals(1L) && !scores.getSportId().equals(2L)){
            return;
        }
        StandardMatchScores standardMatchScores = scoresRedisHelp.getCatchStandScoreByMatchId(Long.valueOf(scores.getMatchId()));
        if (standardMatchScores == null) {
            standardMatchScores = new StandardMatchScores();
        }
        standardMatchScores.setDataSourceCode(scores.getDataSourceCode());
//        standardMatchScores.setScoreJson();
        standardMatchScores.setUpdateTime(System.currentTimeMillis());
        if (SportTypeEnum.FOOTBALL.getValue().equals(scores.getSportId())) {
            Map<Long, FootballScores> footballScores = copyFootballScores(scores, standardMatchScores,linkId);
            standardMatchScores.setScoreJson(JSONObject.toJSONString(footballScores));
            scores.setScoresJson(JSONObject.toJSONString(footballScores));
        } /*else if (SportTypeEnum.BASKETBALL.getValue().equals(scores.getSportId())) {
            copyBasketballScores(scores, standardMatchScores);
        } else if (SportTypeEnum.TENNIS.getValue().equals(scores.getSportId())) {
            copyTennisScores(scores, standardMatchScores);
        } else if (SportTypeEnum.TABLE_TENNIS.getValue().equals(scores.getSportId())) {
            copyTableTennisScores(scores, standardMatchScores);
        } else if (SportTypeEnum.BADMINTON.getValue().equals(scores.getSportId())) {
            copyBadmintonScores(scores, standardMatchScores);
        } else if (SportTypeEnum.VOLLEYBALL.getValue().equals(scores.getSportId())) {
            copyVolleyScores(scores, standardMatchScores);
        } else {
            log.info("【SoldMessageScoresConsumer:" + SOLD_MESSAGE + "】【::" + request.getLinkId() + "::】开售处理后补发比分:无该类型赛事");
        }*/
        //支持比分中心的球种,复制比分后下发
        scoresProducer.sendToMQ(scores, linkId);
        scoresRedisHelp.saveCatchStandScore(standardMatchScores);
        log.info("【SoldMessageScoresConsumer:" + SOLD_MESSAGE + "】【::" + linkId + "::】开售处理后补发比分,复制标准比分完成");
    }


    /**
     * 三方的进攻危险进攻射正射偏控球率数据同步
     * @param standScores
     * @param soresSource
     */
    public void setOther(FootballScores standScores,FootballScores soresSource){
        standScores.setAttack(soresSource.getAttack());
        standScores.setDangerousAttack(soresSource.getDangerousAttack());
        standScores.setBallPossessionPercentage(soresSource.getBallPossessionPercentage());
        standScores.setShotOn(soresSource.getShotOn());
        standScores.setShotOff(soresSource.getShotOff());
        standScores.setShot(soresSource.getShot());

    }
    private Map<Long, FootballScores> copyFootballScores(MatchScoresBetterDto scores, StandardMatchScores standardMatchScores,String linkId) {
        FootballSwitch footballSwitch = new FootballSwitch();
        if (StringUtils.isNotEmpty(standardMatchScores.getDataSourceAccoSwitch())) {
            footballSwitch = JSONObject.parseObject(standardMatchScores.getDataSourceAccoSwitch(), FootballSwitch.class);
        }
        Map<Long, FootballScores> standardScores = new HashMap<>();
        //标准比分为空，直接复制三方比分
        if (!StringUtils.isEmpty(standardMatchScores.getScoreJson())) {
            standardScores = JSON.parseObject(standardMatchScores.getScoreJson(), new TypeReference<Map<Long, FootballScores>>() {
            });
        } else {
            standardScores = JSON.parseObject(scores.getScoresJson(), new TypeReference<Map<Long, FootballScores>>() {
            });
        }
        Map<Long, FootballScores> allPeriodScores = JSON.parseObject(scores.getScoresJson(), new TypeReference<Map<Long, FootballScores>>() {
        });
        FootballScores thirdWholeScores= allPeriodScores.get(WHOLE_MATCH);
        if(thirdWholeScores==null){
            thirdWholeScores = new FootballScores(WHOLE_MATCH);
        }

        Boolean hasOt = false;
        Integer otHomeGoal = 0, otAwayGoal = 0;
        Integer otHomeCorner = 0, otAwayCorner = 0;
        Integer otHomeYellowCard = 0, otAwayYellowCard = 0;
        Integer otHomeRedCard = 0, otAwayRedCard = 0;
        Integer otHomeAttack = 0, otAwayAttack = 0;
        Integer otHomeDangerousAttack = 0, otAwayDangerousAttack = 0;
        Integer otHomePossession = 0, otAwayPossession = 0;
        Integer otShotOnHome = 0, otShotOnAway = 0;
        Integer otShotOffHome = 0, otShotOffAway = 0;
        Integer otShotHome = 0, otShotAway = 0;

        //拼阶段100的比分-常规赛不含加时
        Integer homeGoal = 0, awayGoal = 0;
        Integer homeCorner = 0, awayCorner = 0;
        Integer homeYellowCard = 0, awayYellowCard = 0;
        Integer homeRedCard = 0, awayRedCard = 0;
        Integer homeAttack = 0, awayAttack = 0;
        Integer homeDangerousAttack = 0, awayDangerousAttack = 0;
        Integer homePossession = 0, awayPossession = 0;
        Integer shotOnHome = 0, shotOnAway = 0;
        Integer shotOffHome = 0, shotOffAway = 0;
        Integer shotHome = 0, shotAway = 0;
        int rate = 0;
        int otrate = 0;
        //检索历史比分，根据开关同步历史比分
        for (Map.Entry<Long, FootballScores> entry : allPeriodScores.entrySet()) {
            //获取标准比分当前阶段的比分
            FootballScores standScores = standardScores.get(entry.getKey());
            if (standScores == null) {
                standScores = new FootballScores(entry.getKey());
            }
            if (Objects.equals(entry.getKey(), SportPeriodConstant.FootballPeriod.period_60899)) {
                FootballScores footballMinScores1 = standardScores.get(SportPeriodConstant.FootballPeriod.period_60899);
                log.info("{},获取标准比分60899:{}",linkId,footballMinScores1);
                if (footballMinScores1 == null) {
                    footballMinScores1 = new FootballScores(SportPeriodConstant.FootballPeriod.period_60899);
                    standardScores.put(SportPeriodConstant.FootballPeriod.period_60899, footballMinScores1);
                }
                log.info("{},获取开关 getGoal60899:{}",linkId,footballSwitch.getGoal60899());
                //0-15分钟进球
                if(footballSwitch.getGoal60899()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_60899)!=null){
                        log.info("{},获取比分 60899L:{}",linkId,allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_60899).getGoal());
                        footballMinScores1.setGoal(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_60899).getGoal());
                    }
                }
                //0-15分钟角球
                if(footballSwitch.getCorner60899()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_60899)!=null){
                        footballMinScores1.setCorner(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_60899).getCorner());
                    }
                }
                //0-15分钟黄牌
                if(footballSwitch.getYellowCard60899()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_60899)!=null){
                        footballMinScores1.setYellowCard(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_60899).getYellowCard());
                    }
                }
                //0-15分钟红牌
                if(footballSwitch.getYellowCard60899()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_60899)!=null){
                        footballMinScores1.setRedCard(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_60899).getRedCard());
                    }
                }
                standardScores.put(SportPeriodConstant.FootballPeriod.period_60899, footballMinScores1);
                log.info("{},切换事件源后同步比分60899L:{}",linkId,footballMinScores1.getGoal());
            }else if(entry.getKey() == SportPeriodConstant.FootballPeriod.period_61799) {
                FootballScores footballMinScores2 = standardScores.get(SportPeriodConstant.FootballPeriod.period_61799);
                log.info("{},获取标准比分61799:{}",linkId,footballMinScores2);
                if (footballMinScores2 == null) {
                    footballMinScores2 = new FootballScores(SportPeriodConstant.FootballPeriod.period_61799);
                    standardScores.put(SportPeriodConstant.FootballPeriod.period_61799, footballMinScores2);
                }
                log.info("{},获取开关 getGoal61799:{}",linkId,footballSwitch.getGoal61799());
                //15-30分钟进球
                if(footballSwitch.getGoal61799()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_61799)!=null){
                        log.info("{},获取比分 61799L:{}",linkId,allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_61799).getGoal());
                        footballMinScores2.setGoal(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_61799).getGoal());
                    }
                }
                //15-30分钟角球
                if(footballSwitch.getCorner61799()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_61799)!=null){
                        footballMinScores2.setCorner(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_61799).getCorner());
                    }
                }
                //15-30分钟黄牌
                if(footballSwitch.getYellowCard61799()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_61799)!=null){
                        footballMinScores2.setYellowCard(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_61799).getYellowCard());
                    }
                }
                //15-30分钟红牌
                if(footballSwitch.getYellowCard61799()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_61799)!=null){
                        footballMinScores2.setRedCard(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_61799).getRedCard());
                    }
                }
                standardScores.put(SportPeriodConstant.FootballPeriod.period_61799, footballMinScores2);
                log.info("{},切换事件源后同步比分61799L:{}",linkId,footballMinScores2.getGoal());
            }else if (entry.getKey() == SportPeriodConstant.FootballPeriod.period_62699){
                FootballScores footballMinScores3 = standardScores.get(SportPeriodConstant.FootballPeriod.period_62699);
                log.info("{},获取标准比分62699:{}",linkId,footballMinScores3);
                if (footballMinScores3 == null) {
                    footballMinScores3 = new FootballScores(SportPeriodConstant.FootballPeriod.period_62699);
                    standardScores.put(SportPeriodConstant.FootballPeriod.period_62699, footballMinScores3);
                }
                log.info("{},获取开关 获取标准比分62699:{}",linkId,footballSwitch.getGoal62699());
                //30-45分钟进球
                if(footballSwitch.getGoal62699()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_62699)!=null){
                        log.info("{},获取比分 62699L:{}",linkId,allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_62699).getGoal());
                        footballMinScores3.setGoal(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_62699).getGoal());
                    }
                }
                //30-45分钟角球
                if(footballSwitch.getCorner62699()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_62699)!=null){
                        footballMinScores3.setCorner(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_62699).getCorner());
                    }
                }
                //30-45分钟黄牌
                if(footballSwitch.getYellowCard62699()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_62699)!=null){
                        footballMinScores3.setYellowCard(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_62699).getYellowCard());
                    }
                }
                //30-45分钟红牌
                if(footballSwitch.getYellowCard62699()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_62699)!=null){
                        footballMinScores3.setRedCard(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_62699).getRedCard());
                    }
                }
                standardScores.put(SportPeriodConstant.FootballPeriod.period_62699, footballMinScores3);
                log.info("{},切换事件源后同步比分62699L:{}",linkId,footballMinScores3.getGoal());
            }else if (entry.getKey() == SportPeriodConstant.FootballPeriod.period_6) {
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                if (footballSwitch.getGoalHf() == 1) {
                    standScores.setGoal(thirdScores.getGoal());
                }
                if (footballSwitch.getCornerHf() == 1) {
                    standScores.setCorner(thirdScores.getCorner());
                }
                if (footballSwitch.getYellowHf() == 1) {
                    standScores.setYellowCard(thirdScores.getYellowCard());
                }
                if (footballSwitch.getRedHf() == 1) {
                    standScores.setRedCard(thirdScores.getRedCard());
                }
                //三方的进攻危险进攻射正射偏控球率数据同步
                this.setOther(standScores, thirdScores);
                standardScores.put(6L, standScores);
            }else if (entry.getKey() == SportPeriodConstant.FootballPeriod.period_73599) {
                FootballScores footballMinScores1 = standardScores.get(SportPeriodConstant.FootballPeriod.period_73599);
                if (footballMinScores1 == null) {
                    footballMinScores1 = new FootballScores(SportPeriodConstant.FootballPeriod.period_73599);
                    standardScores.put(SportPeriodConstant.FootballPeriod.period_73599, footballMinScores1);
                }
                //45-60钟进球
                if(footballSwitch.getGoal73599()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_73599)!=null){
                        footballMinScores1.setGoal(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_73599).getGoal());
                    }
                }
                //45-60钟角球
                if(footballSwitch.getCorner73599()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_73599)!=null){
                        footballMinScores1.setCorner(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_73599).getCorner());
                    }
                }
                //45-60钟黄牌
                if(footballSwitch.getYellowCard73599()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_73599)!=null){
                        footballMinScores1.setYellowCard(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_73599).getYellowCard());
                    }
                }
                //45-60钟红牌
                if(footballSwitch.getRedCard73599()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_73599)!=null){
                        footballMinScores1.setRedCard(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_73599).getRedCard());
                    }
                }
                standardScores.put(SportPeriodConstant.FootballPeriod.period_73599, footballMinScores1);
            } else if (entry.getKey() == SportPeriodConstant.FootballPeriod.period_74499) {
                FootballScores footballMinScores2 = standardScores.get(SportPeriodConstant.FootballPeriod.period_74499);
                if (footballMinScores2 == null) {
                    footballMinScores2 = new FootballScores(SportPeriodConstant.FootballPeriod.period_74499);
                    standardScores.put(SportPeriodConstant.FootballPeriod.period_74499, footballMinScores2);
                }
                //60-75分钟进球
                if(footballSwitch.getGoal74499()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_74499)!=null){
                        footballMinScores2.setGoal(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_74499).getGoal());
                    }
                }
                //60-75分钟角球
                if(footballSwitch.getCorner74499()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_74499)!=null){
                        footballMinScores2.setCorner(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_74499).getCorner());
                    }
                }
                //60-75分钟黄牌
                if(footballSwitch.getYellowCard74499()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_74499)!=null){
                        footballMinScores2.setYellowCard(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_74499).getYellowCard());
                    }
                }
                //60-75分钟红牌
                if(footballSwitch.getRedCard74499()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_74499)!=null){
                        footballMinScores2.setRedCard(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_74499).getRedCard());
                    }
                }
                standardScores.put(SportPeriodConstant.FootballPeriod.period_74499, footballMinScores2);
            } else if (entry.getKey() == SportPeriodConstant.FootballPeriod.period_75399) {
                FootballScores footballMinScores3 = standardScores.get(SportPeriodConstant.FootballPeriod.period_75399);
                if (footballMinScores3 == null) {
                    footballMinScores3 = new FootballScores(SportPeriodConstant.FootballPeriod.period_75399);
                    standardScores.put(SportPeriodConstant.FootballPeriod.period_75399, footballMinScores3);
                }

                //75-90分钟进球
                if(footballSwitch.getGoal75399()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_75399)!=null){
                        footballMinScores3.setGoal(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_75399).getGoal());
                    }
                }
                //75-90分钟角球
                if(footballSwitch.getCorner75399()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_75399)!=null){
                        footballMinScores3.setCorner(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_75399).getCorner());
                    }
                }
                //75-90分钟黄牌
                if(footballSwitch.getYellowCard75399()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_75399)!=null){
                        footballMinScores3.setYellowCard(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_75399).getYellowCard());
                    }
                }
                //75-90分钟红牌
                if(footballSwitch.getRedCard75399()==1){
                    if(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_75399)!=null){
                        footballMinScores3.setRedCard(allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_75399).getRedCard());
                    }
                }
                standardScores.put(SportPeriodConstant.FootballPeriod.period_75399, footballMinScores3);
            }else if (entry.getKey() == SportPeriodConstant.FootballPeriod.period_7) {
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                if (footballSwitch.getGoalFt() == 1) {
                    standScores.setGoal(thirdScores.getGoal());
                }
                if (footballSwitch.getCornerFt() == 1) {
                    standScores.setCorner(thirdScores.getCorner());
                }
                if (footballSwitch.getYellowFt() == 1) {
                    standScores.setYellowCard(thirdScores.getYellowCard());
                }
                if (footballSwitch.getRedFt() == 1) {
                    standScores.setRedCard(thirdScores.getRedCard());
                }
                //三方的进攻危险进攻射正射偏控球率数据同步
                this.setOther(standScores, thirdScores);
                standardScores.put(SportPeriodConstant.FootballPeriod.period_7, standScores);
            }
            if (entry.getKey() == SportPeriodConstant.FootballPeriod.period_41) {
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                if (footballSwitch.getGoalOt() == 1) {
                    standScores.setGoal(thirdScores.getGoal());
                }
                if (footballSwitch.getCornerOt() == 1) {
                    standScores.setCorner(thirdScores.getCorner());
                }
                if (footballSwitch.getYellowOt() == 1) {
                    standScores.setYellowCard(thirdScores.getYellowCard());
                }
                if (footballSwitch.getRedOt() == 1) {
                    standScores.setRedCard(thirdScores.getRedCard());
                }
                //三方的进攻危险进攻射正射偏控球率数据同步
                this.setOther(standScores, thirdScores);
                standardScores.put(SportPeriodConstant.FootballPeriod.period_41, standScores);
            }
            if (entry.getKey() == SportPeriodConstant.FootballPeriod.period_42) {
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                if (footballSwitch.getGoalOt() == 1) {
                    standScores.setGoal(thirdScores.getGoal());
                }
                if (footballSwitch.getCornerOt() == 1) {
                    standScores.setCorner(thirdScores.getCorner());
                }
                if (footballSwitch.getYellowOt() == 1) {
                    standScores.setYellowCard(thirdScores.getYellowCard());
                }
                if (footballSwitch.getRedOt() == 1) {
                    standScores.setRedCard(thirdScores.getRedCard());
                }
                //三方的进攻危险进攻射正射偏控球率数据同步
                this.setOther(standScores, thirdScores);
                standardScores.put(SportPeriodConstant.FootballPeriod.period_42, standScores);
            }
            if (entry.getKey() == SportPeriodConstant.FootballPeriod.period_41 || entry.getKey() == SportPeriodConstant.FootballPeriod.period_42) {
                hasOt = true;
                FootballScores ot1 = allPeriodScores.get(SportPeriodConstant.FootballPeriod.period_41);
                otHomeGoal += ot1.getGoal().getHome();
                otAwayGoal += ot1.getGoal().getAway();
                otHomeCorner += ot1.getCorner().getHome();
                otAwayCorner += ot1.getCorner().getAway();
                otHomeYellowCard += ot1.getYellowCard().getHome();
                otAwayYellowCard += ot1.getYellowCard().getAway();
                otHomeRedCard += ot1.getRedCard().getHome();
                otAwayRedCard += ot1.getRedCard().getAway();
                otHomeAttack += ot1.getAttack().getHome();
                otAwayAttack += ot1.getAttack().getAway();
                otHomeDangerousAttack += ot1.getDangerousAttack().getHome();
                otAwayDangerousAttack += ot1.getDangerousAttack().getAway();
                otHomePossession += ot1.getBallPossessionPercentage().getHome();
                otAwayPossession += ot1.getBallPossessionPercentage().getAway();
                otrate += 1;
                otShotOnHome += ot1.getShotOn().getHome();
                otShotOnAway += ot1.getShotOn().getAway();
                otShotOffHome += ot1.getShotOff().getHome();
                otShotOffAway += ot1.getShotOff().getAway();
                otShotHome += ot1.getShot().getHome();
                otShotAway += ot1.getShot().getAway();
            }
            if (entry.getKey() == SportPeriodConstant.FootballPeriod.period_6 || entry.getKey() == SportPeriodConstant.FootballPeriod.period_7) {
                FootballScores ftScore = entry.getValue();
                if (ftScore != null) {
                    homeGoal += ftScore.getGoal().getHome();
                    awayGoal += ftScore.getGoal().getAway();
                    homeCorner += ftScore.getCorner().getHome();
                    awayCorner += ftScore.getCorner().getAway();
                    homeYellowCard += ftScore.getYellowCard().getHome();
                    awayYellowCard += ftScore.getYellowCard().getAway();
                    homeRedCard += ftScore.getRedCard().getHome();
                    awayRedCard += ftScore.getRedCard().getAway();
                    homeAttack += ftScore.getAttack().getHome();
                    awayAttack += ftScore.getAttack().getAway();
                    homeDangerousAttack += ftScore.getDangerousAttack().getHome();
                    awayDangerousAttack += ftScore.getDangerousAttack().getAway();
                    homePossession += ftScore.getBallPossessionPercentage().getHome();
                    awayPossession += ftScore.getBallPossessionPercentage().getAway();
                    rate += 1;
                    shotOnHome += ftScore.getShotOn().getHome();
                    shotOnAway += ftScore.getShotOn().getAway();
                    shotOffHome += ftScore.getShotOff().getHome();
                    shotOffAway += ftScore.getShotOff().getAway();
                    shotHome += ftScore.getShot().getHome();
                    shotAway += ftScore.getShot().getAway();
                }
            }
            if (entry.getKey() == SportPeriodConstant.FootballPeriod.period_50) {
                //获取三方比分当前阶段的比分
                FootballScores thirdScores = entry.getValue();
                if(thirdScores==null){
                    thirdScores = new FootballScores(entry.getKey());
                }
                log.info("{}，同步点球大战比分1：{}", footballSwitch.getPenalty(), thirdScores);
                if (footballSwitch.getPenalty() == 1) {
                    standScores.setGoal(thirdScores.getGoal());
                }
                log.info("{}，同步点球大战比分2：{}", footballSwitch.getPenalty(), standScores);
                standardScores.put(SportPeriodConstant.FootballPeriod.period_50, standScores);
            }
        }
        //存在加时赛
        if(hasOt){
            FootballScores standScoresOts = standardScores.get(SportPeriodConstant.FootballPeriod.period_110);
            if (standScoresOts == null) {
                standScoresOts = new FootballScores(SportPeriodConstant.FootballPeriod.period_110);
                standardScores.put(SportPeriodConstant.FootballPeriod.period_110, standScoresOts);
            }
            if (footballSwitch.getGoalOt() == 1) {
                standScoresOts.setGoal(new CommonItem(otHomeGoal, otAwayGoal));
            }
            if (footballSwitch.getCornerOt() == 1) {
                standScoresOts.setCorner(new CommonItem(otHomeCorner, otAwayCorner));
            }
            if (footballSwitch.getYellowOt() == 1) {
                standScoresOts.setYellowCard(new CommonItem(otHomeYellowCard, otAwayYellowCard));
            }
            if (footballSwitch.getRedOt() == 1) {
                standScoresOts.setRedCard(new CommonItem(otHomeRedCard, otAwayRedCard));
            }
            standScoresOts.countFaCard();
            standScoresOts.setAttack(new CommonItem(otHomeAttack, otAwayAttack));
            standScoresOts.setDangerousAttack(new CommonItem(otHomeDangerousAttack, otAwayDangerousAttack));
            if (otrate != 0) {
                standScoresOts.setBallPossessionPercentage(new CommonItem(otHomePossession / otrate, otAwayPossession / otrate));
            } else {
                standScoresOts.setBallPossessionPercentage(new CommonItem(otHomePossession, otAwayPossession));
            }
            standScoresOts.setShotOn(new CommonItem(otShotOnHome, otShotOnAway));
            standScoresOts.setShotOff(new CommonItem(otShotOffHome, otShotOffAway));
            standScoresOts.setShot(new CommonItem(otShotHome, otShotAway));
            //阶段41|| 42
            standardScores.put(SportPeriodConstant.FootballPeriod.period_110, standScoresOts);
        }


        FootballScores standScoresEnd = standardScores.get(SportPeriodConstant.FootballPeriod.period_100);
        if (standScoresEnd == null) {
            standScoresEnd = new FootballScores(SportPeriodConstant.FootballPeriod.period_100);
            standardScores.put(SportPeriodConstant.FootballPeriod.period_100, standScoresEnd);
        }
        standScoresEnd.setGoal(new CommonItem(homeGoal, awayGoal));
        standScoresEnd.setCorner(new CommonItem(homeCorner, awayCorner));
        standScoresEnd.setYellowCard(new CommonItem(homeYellowCard, awayYellowCard));
        standScoresEnd.setRedCard(new CommonItem(homeRedCard, awayRedCard));
        standScoresEnd.setAttack(new CommonItem(homeAttack, awayAttack));
        standScoresEnd.setDangerousAttack(new CommonItem(homeDangerousAttack, awayDangerousAttack));
        if (rate != 0) {
            standScoresEnd.setBallPossessionPercentage(new CommonItem(homePossession / rate, awayPossession / rate));
        } else {
            standScoresEnd.setBallPossessionPercentage(new CommonItem(homePossession, awayPossession));
        }
        standScoresEnd.setShotOn(new CommonItem(shotOnHome, shotOnAway));
        standScoresEnd.setShotOff(new CommonItem(shotOffHome, shotOffAway));
        standScoresEnd.setShot(new CommonItem(shotHome, shotAway));
        standardScores.put(SportPeriodConstant.FootballPeriod.period_100, standScoresEnd);


//        FootballScores wholeStands = standardScores.get(WHOLE_MATCH);
        for(Map.Entry<Long, FootballScores> entry : standardScores.entrySet()){
            if(entry.getKey()==6 || entry.getKey()==7 || entry.getKey()==110 ){
                //累计-1比分
                calcWholeScore(thirdWholeScores,standardScores.get(entry.getKey()));
            }
            //计算每个阶段的罚牌比分
            entry.getValue().countFaCard();
        }
        FootballScores wholeStands = standardScores.get(WHOLE_MATCH);
        wholeStands.setGoal(new CommonItem(homeGoal+otHomeGoal, awayGoal+otAwayGoal));
        wholeStands.setCorner(new CommonItem(homeCorner+otHomeCorner, awayCorner+otAwayCorner));
        wholeStands.setYellowCard(new CommonItem(homeYellowCard+otHomeYellowCard, awayYellowCard+otAwayYellowCard));
        wholeStands.setRedCard(new CommonItem(homeRedCard+otHomeRedCard, awayRedCard+otAwayRedCard));
        return standardScores;
    }
    private void calcWholeScore(FootballScores wholeSores,FootballScores standScores) {
        wholeSores.setGoal(new CommonItem(wholeSores.getGoal().getHome()+standScores.getGoal().getHome(),
                wholeSores.getGoal().getAway()+standScores.getGoal().getAway()));
        wholeSores.setCorner(new CommonItem(wholeSores.getCorner().getHome()+standScores.getCorner().getHome(),
                wholeSores.getCorner().getAway()+standScores.getCorner().getAway()));
        wholeSores.setYellowCard(new CommonItem(wholeSores.getYellowCard().getHome()+standScores.getYellowCard().getHome(),
                wholeSores.getYellowCard().getAway()+standScores.getYellowCard().getAway()));
        wholeSores.setRedCard(new CommonItem(wholeSores.getRedCard().getHome()+standScores.getRedCard().getHome(),
                wholeSores.getRedCard().getAway()+standScores.getRedCard().getAway()));
        wholeSores.countFaCard();
    }

    /**
     * 查询切换前的控球率和控球时长，复制到新的事件源比分
     * @param scores
     * @param data
     */
    private void buildPossession(MatchScoresBetterDto scores, SaleUpdateLiveBusinessEventMessage data) {
        log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+data.getMatchId()+"::】切换事件源复制比分开始");
        Long time = System.currentTimeMillis();
        //旧事件源三方赛事
//        ThirdMatchInfoExample thirdMatchInfoExample =new ThirdMatchInfoExample();
//        thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(data.getMatchId()).andDataSourceCodeEqualTo(data.getBusinessEventCodeOld());
//        List<ThirdMatchInfo> thirdMatchInfos =thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(data.getMatchId(),data.getBusinessEventCodeOld());
        if(thirdMatchInfo!=null){
            //查询旧事件源比分
            MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(),1);
            if(matchScoresInfo==null) {
                log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+data.getMatchId()+"::】切换事件源复制比分:旧事件源无比分对象");
                return;
            }
            JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
            Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
            //旧 全场(-1)比分
            FootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
            //旧 控球率 控球时长
            CommonItem possession= wholeSores.getBallPossessionPercentage();
            CommonItem possessionTime= wholeSores.getPossessionTime();
            if(possession!=null&&possessionTime!=null) {
                log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+data.getMatchId()+"::】切换事件源复制比分:无控球率");
                return;
            }
            if(StringUtils.isEmpty(scores.getScoresJson())){
                log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+data.getMatchId()+"::】切换事件源复制比分:无比分");
                return;
            }
            //复制控球率、控球时长到DTO
            JSONObject newFootballScoreJson = JSONObject.parseObject(scores.getScoresJson());
            Map<Long, FootballScores> newScores= JsonMapUtils.parseFootballMap(newFootballScoreJson);
            FootballScores newWholeSores= newScores.get(WHOLE_MATCH);
            newWholeSores.setBallPossessionPercentage(possession);
            newWholeSores.setPossessionTime(possessionTime);
            newScores.put(-1L,newWholeSores);
            scores.setScoresJson(JSONObject.toJSONString(newFootballScoreJson));
            //复制旧事件源的控球率、控球时长到新事件源的比分并入库
            log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+data.getMatchId()+"::】切换事件源复制比分:控球率对象复制完成");
//            thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(data.getMatchId()).andDataSourceCodeEqualTo(data.getBusinessEventCode());
//            List<ThirdMatchInfo> newScore =thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
            ThirdMatchInfo newThirdMatchInfo = thirdMatchInfoService.getItem(data.getMatchId(),data.getBusinessEventCode());
            if(newThirdMatchInfo!=null){
                //查询新事件源比分
                MatchScoresInfo newScoreInfo = matchScoreInfoRepository.selectByExample(newThirdMatchInfo.getId(),1);
                if (newScoreInfo==null){
                    log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+data.getMatchId()+"::】切换事件源复制比分:新事件源无比分");
                    return;
                }
                ////复制旧事件源的控球率、控球时长到新事件源的比分并入库
                JSONObject newScoreJson = JSONObject.parseObject(newScoreInfo.getScoresJson());
                Map<Long, FootballScores> newFootballScore= JsonMapUtils.parseFootballMap(newScoreJson);
                FootballScores newWholes= newScores.get(WHOLE_MATCH);
                newWholes.setBallPossessionPercentage(possession);
                newWholes.setPossessionTime(possessionTime);
                newFootballScore.put(WHOLE_MATCH,newWholes);
                newScoreInfo.setScoresJson(JSONObject.toJSONString(newFootballScoreJson));
                newScoreInfo.setModifyTime(System.currentTimeMillis());
                matchScoreInfoRepository.updateScoresInfo(newScoreInfo);
                log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+data.getMatchId()+"::】切换事件源复制比分:更新完成：{}",System.currentTimeMillis()-time);
                //复制旧事件源的控球率、控球时长到新事件源三方赛事的redis中
                String homeEventKey = "POSSESSION:HOME:EVENT:MATCHID:"+scores.getThirdMatchId();
                String homeEventKeyOld = "POSSESSION:HOME:EVENT:MATCHID:"+thirdMatchInfo.getId();
                String awayEventKey = "POSSESSION:AWAY:EVENT:MATCHID:"+scores.getThirdMatchId();
                String awayEventKeyOld = "POSSESSION:AWAY:EVENT:MATCHID:"+thirdMatchInfo.getId();
                if(redisService.hasKey(homeEventKeyOld)){
                    redisService.set(homeEventKey,redisService.get(homeEventKeyOld),7200);
                }
                if(redisService.hasKey(awayEventKeyOld)){
                    redisService.set(awayEventKey,redisService.get(awayEventKeyOld),7200);
                }

                String publicEventKey  = RONGHE_PD_FOOTBALL_PUBLIC_EVENT + scores.getThirdMatchId();
                if(!redisService.hasKey(publicEventKey)){
                    PublicEvent publicEvent = new PublicEvent();
                    MatchEventInfo lastEvent = getLastEvent(homeEventKey, awayEventKey);
                    publicEvent.setPreviousEvent(lastEvent.getHomeAway());
                    redisService.set(publicEventKey,publicEvent,7200);
                }else{
                    PublicEvent publicEvent = (PublicEvent) redisService.get(publicEventKey);
                    MatchEventInfo lastEvent = getLastEvent(homeEventKey, awayEventKey);
                    publicEvent.setPreviousEvent(lastEvent.getHomeAway());
                    redisService.set(publicEventKey,publicEvent,7200);
                }

            }
        }
    }

    private MatchEventInfo getLastEvent(String homeEventKey, String awayEventKey) {
        Long homeTime = 0L;
        Long awayTime = 0L;
        Object hasHomeEvent = redisService.get(homeEventKey);
        Object hasAwayEvent = redisService.get(awayEventKey);
        if(null!=hasHomeEvent){
            MatchEventInfo dh = (MatchEventInfo) hasHomeEvent;
            homeTime = dh.getCreateTime();
        }
        if(null!=hasAwayEvent){
            MatchEventInfo da = (MatchEventInfo) hasAwayEvent;
            awayTime = da.getCreateTime();
        }
        MatchEventInfo lastEvent = new MatchEventInfo();
        if (homeTime >= awayTime) {
            if(hasHomeEvent!=null){
                lastEvent =  (MatchEventInfo) hasHomeEvent;
            }
        } else {
            if(hasAwayEvent!=null){
                lastEvent =  (MatchEventInfo) hasAwayEvent;
            }
        }
        return lastEvent;
    }


    private void copyScore(Request<SaleUpdateLiveBusinessEventMessage> request) {
        log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getData().getMatchId()+"::】切换事件源复制比分开始");
        SaleUpdateLiveBusinessEventMessage data= request.getData();
        String linkId = request.getLinkId();
        //0.先只做足球 91886 接入篮球
        if(!data.getSportId().equals(1L) && !data.getSportId().equals(2L)&& !data.getSportId().equals(7L) && !data.getSportId().equals(9L)){
            log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getData().getMatchId()+"::】赛种不支持复制");
            return;
        }
        //1. PD PD2之间不来回复制
        if((data.getBusinessEventCode().equals("PD")&&data.getBusinessEventCodeOld().equals("PD2"))||(data.getBusinessEventCode().equals("PD2")&&data.getBusinessEventCodeOld().equals("PD"))){
            log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getData().getMatchId()+"::】PD、PD2之间不相互复制");
            return;
        }
        if(data.getBusinessEventCodeOld().equals("PD")||data.getBusinessEventCodeOld().equals("PD2")){
            log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getData().getMatchId()+"::】PD、PD2切商业事件源不复制");
            return;
        }
        //2.复制比分
        MatchScoresInfo matchScoresInfoOld =  this.getMatchScoreByCodeAndStandardMatchId(data.getBusinessEventCodeOld(),data.getMatchId(),linkId);
        if(matchScoresInfoOld==null){
            log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getData().getMatchId()+"::】复制比分旧事件源无比分数据:{}",request.getLinkId(),data.getBusinessEventCodeOld());
            return;
        }
        MatchScoresInfo matchScoresInfoNew =  this.getMatchScoreByCodeAndStandardMatchId(data.getBusinessEventCode(),data.getMatchId(),linkId);
        if(matchScoresInfoNew==null){
            log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getData().getMatchId()+"::】复制比分新事件源无比分数据:{}",request.getLinkId(),data.getBusinessEventCode());
            return ;
        }
        log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getData().getMatchId()+"::】复制比分完成:{}-->{}",request.getLinkId(),data.getBusinessEventCodeOld(),data.getBusinessEventCode());

        MatchTimeInfo matchTimeInfoOld =  this.getMatchTimeByCodeAndStandardMatchId(data.getBusinessEventCodeOld(),data.getMatchId(),matchScoresInfoOld,linkId);
        if(matchTimeInfoOld==null){
            log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getData().getMatchId()+"::】复制时间旧事件源无时间数据:{}",request.getLinkId(),data.getBusinessEventCodeOld());
            return;
        }
        MatchTimeInfo matchTimeInfoNew  =  this.getMatchTimeByCodeAndStandardMatchId(data.getBusinessEventCode(),data.getMatchId(),matchScoresInfoNew,linkId);
        if(matchTimeInfoNew==null){
            log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getData().getMatchId()+"::】复制时间新事件源无时间数据:{}",request.getLinkId(),data.getBusinessEventCode());
//            matchTimeInfoNew = new MatchTimeInfo();
            return;
        }
        log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getData().getMatchId()+"::】复制时间完成:{}-->{}",request.getLinkId(),data.getBusinessEventCodeOld(),data.getBusinessEventCode());

        this.copyMatchScoreAndTime(matchScoresInfoOld,matchScoresInfoNew,matchTimeInfoOld,matchTimeInfoNew,linkId);

//        //3.推送报球版WS 刷新比分
//        redisUtils.pushFootBallScore(matchScoresInfoNew.getThirdMatchId());
    }

    private MatchScoresInfo getMatchScoreByCodeAndStandardMatchId(String businessEventCodeOld, Long matchId,String linkId) {
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(matchId,businessEventCodeOld);
        if(thirdMatchInfo==null){
            log.info("linkId:{},sourceCode:{} 事件源切换复制比分：无三方赛事，三方赛事ID：{}",linkId,businessEventCodeOld,thirdMatchInfo.getId());
            return null;
        }
        MatchScoresInfo matchScoresInfo= matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(), SourceTypeEnum.LIVE_DATA.getCode()) ;
        if(matchScoresInfo==null){
            log.info("linkId:{},sourceCode:{} 事件源切换复制比分：无三方赛事，无比分：{}",linkId,businessEventCodeOld,thirdMatchInfo.getId());
            return null;
        }
        log.info("linkId:{},sourceCode:{} 事件源切换复制比分：thirdMatchId:{},比分：{}",linkId,businessEventCodeOld,thirdMatchInfo.getId(),matchScoresInfo);
        return matchScoresInfo;
    }

    private MatchTimeInfo getMatchTimeByCodeAndStandardMatchId(String businessEventCodeOld, Long matchId, MatchScoresInfo matchScoresInfo,String linkId) {
        MatchTimeInfo matchTimeInfo= new MatchTimeInfo();
        if(matchScoresInfo!=null){
            matchTimeInfo =matchTimeInfoRepository.selectByPrimaryKey(matchScoresInfo.getId());
        }
        log.info("{} matchId：{}businessEventCode:{},事件源切换复制比分,获取时间：{} ",linkId,matchId,businessEventCodeOld,matchTimeInfo);
        return matchTimeInfo;
    }

    private void copyMatchScoreAndTime(MatchScoresInfo oldScore, MatchScoresInfo matchScoresInfo, MatchTimeInfo oldTimeInfo, MatchTimeInfo matchTimeInfo,String linkId) {
        //1.复制比分
        log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+linkId+"::】测试日志复制比分开始1，oldScore：{}",linkId,oldScore);
        log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+linkId+"::】测试日志复制比分开始2，matchScoresInfo：{}",linkId,matchScoresInfo);
        log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+linkId+"::】测试日志复制比分开始3，oldTimeInfo：{}",linkId,oldTimeInfo);
        log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+linkId+"::】测试日志复制比分开始4，matchTimeInfo：{}",linkId,matchTimeInfo);
        try {
         Long newScoreId =matchScoresInfo.getId();
         Long newTimeId =matchTimeInfo.getId();
         Long thirdMatchId=matchScoresInfo.getThirdMatchId();
         String thirdMatchSourceId=matchScoresInfo.getThirdMatchSourceId();
         String dataSourceCode =  matchScoresInfo.getDataSourceCode();
         log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+linkId+"::】测试日志复制比分开始5:{}-->{}--->{}--->{}--->{}",
                 linkId,newScoreId,newTimeId,thirdMatchId,thirdMatchSourceId,dataSourceCode);
         //2.复制时间
        String destScoresJson = matchScoresInfo.getScoresJson();
        BeanUtils.copyProperties(oldScore,matchScoresInfo);
        if(StringUtils.isNotEmpty(destScoresJson)) {
            String srcScoresJson = matchScoresInfo.getScoresJson();
            JSONObject destJson = JSONObject.parseObject(destScoresJson);
            JSONObject srcJson = StringUtils.isNotEmpty(srcScoresJson)
                    ? JSONObject.parseObject(srcScoresJson)
                    : new JSONObject();
            for (String periodKey : destJson.keySet()) {
                if(!srcJson.containsKey(periodKey)) {
                    srcJson.put(periodKey, destJson.get(periodKey));
                }
            }
            matchScoresInfo.setScoresJson(srcJson.toJSONString());
            log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+linkId+"::】复制比分合并阶段，新事件源保留的独有阶段：{}",
                    linkId, srcJson.keySet());
        }
         log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+linkId+"::】测试日志复制比分开始6:matchScoresInfo-->{}",
                 linkId,matchScoresInfo);
         BeanUtils.copyProperties(oldTimeInfo,matchTimeInfo);
         log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+linkId+"::】测试日志复制比分开始7:matchTimeInfo-->{}",
                 linkId,matchTimeInfo);
         // oldTimeInfo 阶段period大于0滚球状态。就标记为中场切换PD,就存redis. key 过期时间36小时
         // 结算那边。判断redis, PD 存在 return 返回，不参与结算
         matchScoresInfo.setId(newScoreId);
         matchScoresInfo.setThirdMatchId(thirdMatchId);
         matchScoresInfo.setDataSourceCode(dataSourceCode);
         matchScoresInfo.setThirdMatchSourceId(thirdMatchSourceId);
         matchTimeInfo.setId(newTimeId);
         matchTimeInfo.setThirdMatchId(thirdMatchId);
         if(null==matchTimeInfo.getMatchLength()){
             matchTimeInfo.setMatchLength(0);
         }
         if(null==matchScoresInfo.getMatchLength()){
             matchScoresInfo.setMatchLength(0);
         }
         log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+linkId+"::】测试日志复制比分开始8:matchScoresInfo-->{}",
                 linkId,matchScoresInfo);
         log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+linkId+"::】测试日志复制比分开始9:matchTimeInfo-->{}",
                 linkId,matchTimeInfo);
//         matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);
//         matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfo);
         //其他数据商还要刷新缓存
         matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
         matchTimeInfoRepository.updateByPrimaryKey(matchTimeInfo);
         log.info("{}【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+linkId+"::】测试日志复制比分",linkId);
        } catch (Exception e) {
            log.error("linkId:{}, 事件源切换复制比分：复制比分异常：",linkId,e);
        }
    }

    /**
     * 斯诺克数据源切换时映射阶段 period：
     * - 切到 PD：将 “序号局(1..35)” 或 “结束阶段(301..)” 映射到 PD 的局开始阶段(8/9/.../441/442/...)
     * - 切离 PD：将 PD 的局开始/结束阶段映射为 “序号局(1..35)”（保持旧数据源存储口径）
     */
    private void mapSnookerPeriodForDataSourceSwitch(MatchScoresInfo oldScore, MatchTimeInfo oldTimeInfo, boolean toPd) {
        if (oldScore == null || oldTimeInfo == null) return;
        Long srcPeriod = oldTimeInfo.getPeriod();
        Long mapped = toPd ? mapSnookerPeriodToPd(srcPeriod) : mapSnookerPeriodFromPd(srcPeriod);
        oldTimeInfo.setPeriod(mapped);
        oldScore.setPeriod(mapped);
    }

    private static Long mapSnookerPeriodToPd(Long srcPeriod) {
        if (srcPeriod == null) return null;
        if (srcPeriod <= 0) return srcPeriod;
        // 旧数据源可能用 1..35 表示第N局
        if (srcPeriod <= 35) {
            return SportPeriodConstant.SnookerPeriod.getPeriodByIndex(srcPeriod.intValue());
        }
        // 结束阶段映射到开始阶段（301->8 等），其它阶段原样返回（445/80/100 等）
        return SportPeriodConstant.SnookerPeriod.getSnookerPeriod(srcPeriod);
    }

    private static Long mapSnookerPeriodFromPd(Long srcPeriod) {
        if (srcPeriod == null) return null;
        if (srcPeriod <= 0) return srcPeriod;
        Long begin = SportPeriodConstant.SnookerPeriod.getSnookerPeriod(srcPeriod);
        return SportPeriodConstant.SnookerPeriod.getIndexByPeriod(begin);
    }

    /**
     * 切到 PD 数据源时，补齐报球板所需 Redis 状态。
     * 这些字段会出现在 getCurrentMatchInfo 返回的 matchStatus 中（截图红框）。
     */
    private void refreshSnookerPdMatchStatusCache(Long thirdMatchId, MatchTimeInfo matchTimeInfo, MatchScoresInfo matchScoresInfo) {
        if (thirdMatchId == null) return;
        String key = SnookerConstant.RONGHE_PD_MATCH_STATUS + thirdMatchId;
        // 当前阶段：以 timeInfo.period 为准
        Long periodId = matchTimeInfo != null ? matchTimeInfo.getPeriod() : 0L;
        redisService.hSet(key, SnookerConstant.MATCH_CURRENT_PERIOD, periodId == null ? 0L : periodId, RedisConfig.REDIS_WEEK_TIME);
        // 赛制：报球板 matchStatus 里也会展示（即便 getCurrentMatchInfo 会兜底，这里尽量补齐）
        Integer matchLength = null;
        if (matchTimeInfo != null && matchTimeInfo.getMatchLength() != null && matchTimeInfo.getMatchLength() > 0) {
            matchLength = matchTimeInfo.getMatchLength();
        } else if (matchScoresInfo != null && matchScoresInfo.getMatchLength() != null && matchScoresInfo.getMatchLength() > 0) {
            matchLength = matchScoresInfo.getMatchLength();
        }
        if (matchLength != null) {
            redisService.hSet(key, "matchLength", matchLength, RedisConfig.REDIS_WEEK_TIME);
        }
        // controlType：原本由 changeMatchStatus 写入 Redis；切源复制时没有对应事件，按 period 兜底一个可预期值
        Integer controlType = null;
        if (periodId != null) {
            if (periodId.equals(80L)) {
                controlType = 2; // 暂停/中断（与 MatchScoreCommonController 的 period 兜底口径一致）
            } else if (periodId.equals(100L)) {
                controlType = 4; // 结束
            } else if (periodId > 0) {
                controlType = 1; // 进行中/开赛
            }
        }
        if (controlType != null) {
            redisService.hSet(key, SnookerConstant.CONTROL_TYPE, controlType, RedisConfig.REDIS_WEEK_TIME);
        }
        // 默认置空状态（避免旧数据源残留影响 PD 报球板）
        redisService.hSet(key, SnookerConstant.KICKOFF_FIRST_CLICK, new CommonItem(0, 0, false), RedisConfig.REDIS_WEEK_TIME);
        redisService.hSet(key, SnookerConstant.CURRENT_STRIKER, new CommonItem(0, 0, false), RedisConfig.REDIS_WEEK_TIME);
        redisService.hSet(key, SnookerConstant.MATCH_EVENT_INTERRUPTED, false, RedisConfig.REDIS_WEEK_TIME);
    }

    public String buildStandardMatchScoreByMap(String scoresJson,String linkId) {
        JSONObject periodSnookerScores = JSONObject.parseObject(scoresJson);
        Map<Long, SnookerScores> allPeriodScores= JsonMapUtils.parseSnookerMap(periodSnookerScores);
        Map<Long, SnookerScores> newAllPeriodScores= new HashMap<>();
        allPeriodScores.forEach((key,value)->{
            newAllPeriodScores.put(SportPeriodConstant.SnookerPeriod.getPeriodByIndex(Integer.parseInt(key+"")),value);
        });
        log.info("::{}::斯诺克比分编码转换：{}",linkId,newAllPeriodScores);
        return JSONObject.toJSONString(newAllPeriodScores);
    }


}
