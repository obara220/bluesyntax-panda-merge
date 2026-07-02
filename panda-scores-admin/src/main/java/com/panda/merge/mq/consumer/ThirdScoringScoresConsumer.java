package com.panda.merge.mq.consumer;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.SellStatusEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.RedisService;
import com.panda.merge.config.mq.ConsumerConfigDetail;
import com.panda.merge.config.mq.MqConsumerConfig;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.StandardMatchSwitchStatusMessage;
import com.panda.merge.model.*;
import com.panda.merge.mq.message.CommonStandardScoresDto;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.utils.JsonMapUtils;
import com.panda.merge.utils.MessageBuilderUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.Map;

import static com.panda.merge.constant.ConstantSystem.MATCH_STATISTICS_INFO_API_SCORES;
import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * 比分中心触发初始化比分下发
 * @author fymen
 * @since 2025-0525
 */
@Slf4j
@Component
@DependsOn("scoresAdminApplication")
public class ThirdScoringScoresConsumer  extends AbstractSingleMessageMQConsumer<Request<StandardMatchSwitchStatusMessage>>{

    @Autowired
    private MessageBuilderUtils messageBuilderUtils;
    @Autowired
    private ScoresProducer scoresProducer;
    @Autowired
    private MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    /**
     * 消费者需要配置多少线程，范围0-1000
     */
    public static final String NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY ="mq.uof-score.consumer.thread";

    @NacosValue(value = "${"+ NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY +":20}",autoRefreshed = true)
    @Getter
    @Setter
    private Integer consumerThreadNumber;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
    private static final String TOPIC="STANDARD_MATCH_SWITCH_STATUS";
    private static final String CONSUMER_GROUP="scores-group-STANDARD_MATCH_SWITCH_STATUS";
    MqConsumerConfig buildConfig() {
        ConsumerConfigDetail consumerConfigDetail = ConsumerConfigDetail.builder()
                .threadNumber(consumerThreadNumber).pullBatchSize(64).build();
        return new MqConsumerConfig(TOPIC, CONSUMER_GROUP
                , new TypeReference<Request<StandardMatchSwitchStatusMessage>>() {},consumerConfigDetail);
    }
    @Override
    public void processMessage(Request<StandardMatchSwitchStatusMessage> request) {
        log.info("ThirdScoringScoresConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch && commonProducer.getDatacenterMatchIds(request.getData().getStandardMatchId().toString())) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(request, "datacenter-STANDARD_MATCH_SWITCH_STATUS",request.getLinkId());
            return;
        }
        StopWatch extWatch = new StopWatch();
        extWatch.start();
        String link = request.getLinkId();
        if(request==null || request.getData()==null){
            log.info("::THIRD_SCORING_CENTER_API::比分中心:参数不全:{}",link);
            return;
        }
        StandardMatchSwitchStatusMessage switchStatus = request.getData();
        if(switchStatus==null || switchStatus.getOddsLive()!=1){
            return;
        }
        Long matchId = switchStatus.getStandardMatchId();
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(matchId);
        if(standardSportMarketSell==null){
            log.info("THIRD_SCORING_CENTER_API补发比分异常:开售信息不存在:{}",link);
            return;
        }
//        if (SellStatusEnum.UNSOLD.getValue().equals(standardSportMarketSell.getLiveMatchSellStatus())
//                && SellStatusEnum.UNSOLD.getValue().equals(standardSportMarketSell.getPreMatchSellStatus())) {
//            log.info("::{}::THIRD_SCORING_CENTER_API补发比分异常-赛事未开售,标准赛事id:{}", link, matchId);
//            return;
//        }
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(matchId,standardSportMarketSell.getBusinessEvent());
        if(thirdMatchInfo==null){
            log.info("THIRD_SCORING_CENTER_API补发比分异常:三方赛事不存在:{}",link);
            return;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(matchId);
        if(standardMatchInfo==null){
            log.info("::::THIRD_SCORING_CENTER_API比分中心-无标准赛事:{}", link);
            return;
        }
        MatchScoresInfo matchScoresInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(),SourceTypeEnum.LIVE_DATA.getCode());
        if(matchScoresInfo==null){
            matchScoresInfo =new  MatchScoresInfo();
            matchScoresInfo.setId(IdWorker.getId());
            matchScoresInfo.setDataSourceCode(standardSportMarketSell.getBusinessEvent());
            matchScoresInfo.setDataSourceType(SourceTypeEnum.UOF.getCode().toString());
            matchScoresInfo.setEventTime(System.currentTimeMillis());
            matchScoresInfo.setMatchLength(standardMatchInfo.getMatchLength());
            matchScoresInfo.setPeriod(standardMatchInfo.getMatchPeriodId());
            matchScoresInfo.setRemainingTime(0L);
            matchScoresInfo.setSecondsMatchStart(standardMatchInfo.getSecondsMatchStart()==null?0:Long.valueOf(standardMatchInfo.getSecondsMatchStart()));
            matchScoresInfo.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
            matchScoresInfo.setThirdMatchId(thirdMatchInfo.getId());
            matchScoresInfo.setSportId(standardMatchInfo.getSportId());
            matchScoresInfo.setCreateTime(System.currentTimeMillis());
            matchScoresInfo.setModifyTime(matchScoresInfo.getCreateTime());
        }
        //初始化比分
        if(StrUtil.isEmpty(matchScoresInfo.getScoresJson())){
            matchScoresInfo.setScoresJson(initSportScores(standardMatchInfo.getSportId()));
            matchScoreInfoRepository.updateScoresInfo(matchScoresInfo);
        }else{
            log.info("::THIRD_SCORING_CENTER_API::比分中心:SendInitMatchScoresConsumer-原数据源存在比分下发:{}",link);
            return;
        }
        log.info("::THIRD_SCORING_CENTER_API::比分中心:SendInitMatchScoresConsumer-初始化比分完成，逻辑处理开始:{}",link);
        // 数据组装
        CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildCommonScoresDto(thirdMatchInfo, matchScoresInfo);
        log.info("::{}::比分中心:STANDARD_MATCH_SWITCH_STATUS 比分下发，逻辑处理开始，request={}", link, JSON.toJSONString(commonScoresDto));
        commonScoresDto.setLinkedId(link);
        //阶段转换
        changeMatchPeriod(commonScoresDto);
        scoresProducer.sendStandardMatchScores(commonScoresDto);
        extWatch.stop();
        log.info("STANDARD_MATCH_SWITCH_STATUS 补发初始化比分完成,耗时:{}",extWatch.getTotalTimeMillis());

    }

    private void changeMatchPeriod(CommonStandardScoresDto commonScoresDto) {
        if(commonScoresDto.getSportId()!=2){
            return;
        }
        if(301L == commonScoresDto.getPeriodId()){
            commonScoresDto.setPeriodId(13L);
        }else if(302L == commonScoresDto.getPeriodId()){
            commonScoresDto.setPeriodId(14L);
        }if(303L == commonScoresDto.getPeriodId()){
            commonScoresDto.setPeriodId(15L);
        }if(304L == commonScoresDto.getPeriodId()){
            commonScoresDto.setPeriodId(16L);
        }if(31L == commonScoresDto.getPeriodId()){
            commonScoresDto.setPeriodId(1L);
        }
    }

    /**
     * 初始化各球种的比分
     * @param sportId
     * @return
     */
    private String initSportScores(Long sportId) {
        String scoresJson = "";
        if(SportTypeEnum.FOOTBALL.getValue().equals(sportId)){
            Map<Long, FootballScores> periodScores= new HashMap<>();
            FootballScores footballScores=new FootballScores(WHOLE_MATCH);
            periodScores.put(WHOLE_MATCH,footballScores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.BASKETBALL.getValue().equals(sportId)){
            Map<Long, BasketballScores> periodScores= new HashMap<>();
            BasketballScores scores=new BasketballScores(WHOLE_MATCH);
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.TENNIS.getValue().equals(sportId)){
            Map<Long, TennisScores> periodScores= new HashMap<>();
            TennisScores scores=new TennisScores(WHOLE_MATCH);
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.BASEBALL.getValue().equals(sportId)){
            Map<Long, BaseballScores> periodScores= new HashMap<>();
            BaseballScores scores=new BaseballScores();
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.ICE_HOCKEY.getValue().equals(sportId)){
            Map<Long, IceHockeyScores> periodScores= new HashMap<>();
            IceHockeyScores scores=new IceHockeyScores(WHOLE_MATCH);
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.AMERICAN_FOOTBALL.getValue().equals(sportId)){
            Map<Long, AmericanFootballScores> periodScores= new HashMap<>();
            AmericanFootballScores scores=new AmericanFootballScores();
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.SNOOKER.getValue().equals(sportId)){
            Map<Long, SnookerScores> periodScores= new HashMap<>();
            SnookerScores scores=new SnookerScores();
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.TABLE_TENNIS.getValue().equals(sportId)){
            Map<Long, TableTennisScores> periodScores= new HashMap<>();
            TableTennisScores scores=new TableTennisScores();
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.VOLLEYBALL.getValue().equals(sportId)){
            Map<Long, VolleyballScores> periodScores= new HashMap<>();
            VolleyballScores scores=new VolleyballScores();
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.BADMINTON.getValue().equals(sportId)){
            Map<Long, BadmintonScores> periodScores= new HashMap<>();
            BadmintonScores scores=new BadmintonScores();
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.HANDBALL.getValue().equals(sportId)){
            Map<Long, HandballScores> periodScores= new HashMap<>();
            HandballScores scores=new HandballScores(WHOLE_MATCH);
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.BEACH_VOLLEYBALL.getValue().equals(sportId)){
            Map<Long, BeachVolleyballScores> periodScores= new HashMap<>();
            BeachVolleyballScores scores=new BeachVolleyballScores(WHOLE_MATCH);
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.UK_FOOTBALL.getValue().equals(sportId)){
            Map<Long, UKFootballScores> periodScores= new HashMap<>();
            UKFootballScores scores=new UKFootballScores();
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.HOCKEY.getValue().equals(sportId)){
            Map<Long, HockeyScores> periodScores= new HashMap<>();
            HockeyScores scores=new HockeyScores();
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.WATER_BALL.getValue().equals(sportId)){
            Map<Long, WaterballScores> periodScores= new HashMap<>();
            WaterballScores scores=new WaterballScores();
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }else if(SportTypeEnum.CRICKET_BALL.getValue().equals(sportId)){
            Map<Long, CricketBallScores> periodScores= new HashMap<>();
            CricketBallScores scores=new CricketBallScores();
            periodScores.put(WHOLE_MATCH,scores);
            scoresJson = com.alibaba.fastjson.JSONObject.toJSONString(periodScores);
        }
        return scoresJson;
    }
}
