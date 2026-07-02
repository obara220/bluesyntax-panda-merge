package com.panda.merge.mq.consumer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.constant.SportTypeEnum;
import com.panda.merge.model.*;
import com.panda.merge.mq.message.CommonStandardScoresDto;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.repository.MatchScoreInfoRepository;
import com.panda.merge.repository.ScoresRedisHelp;
import com.panda.merge.repository.StandardMatchInfoRepository;
import com.panda.merge.service.*;
import com.panda.merge.utils.MessageBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;


/**
 * PLS通知比分中心下发比分
 * fymeng 2021/12/23
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "NOTIFY_SCORE_CENTER_SEND_SCORE_PLS",
        consumerGroup = "scores_group_NOTIFY_SCORE_CENTER_SEND_SCORE_PLS",
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("scoresAdminApplication")
public class PlsMatchConsumer implements RocketMQListener<String> {

    @Autowired
    ScoresRedisHelp scoresRedisHelp;
    @Autowired
    MatchScoreInfoRepository matchScoreInfoRepository;
    @Autowired
    StandardMatchInfoService StandardMatchInfoService;
    @Autowired
    StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    ThirdMatchInfoService  thirdMatchInfoService;
    @Autowired
    ScoresProducer scoresProducer;
    @Autowired
    MessageBuilderUtils messageBuilderUtils;
    @Autowired
    StandardSportTournamentService standardSportTournamentServiceImpl;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
    @Override
    public void onMessage(String s) {
        log.info("PlsMatchConsumer MQ消费数据开始...{}",datacenterMergeSwitch);

        StopWatch extWatch = new StopWatch();
        extWatch.start();
        if(StrUtil.isEmpty(s)){
            return;
        }
        JSONObject jsonObj = new JSONObject(s);

        if(null == jsonObj.get("data")) {
            return;
        }
       try{
           log.info("::{}::pls通知比分下发:s：{}",s);
           JSONObject data = jsonObj.getJSONObject("data");
           String linkId = jsonObj.getStr("linkId");
           Long standatdMatchId = 0L;
           if(null != data.get("standardMatchId")){
               standatdMatchId = data.getLong("standardMatchId");
           }
           if (datacenterMergeSwitch) {
               //MQ消息转发给数据中心
               commonProducer.asyncSend(s, "datacenter-NOTIFY_SCORE_CENTER_SEND_SCORE_PLS",standatdMatchId+"");
               return;
           }
           StandardSportMarketSell sell = standardSportMarketSellService.getItem(standatdMatchId);
           if(sell==null){
               log.info("::{}::pls通知比分下发:开售信息不存在,赛事ID:{},{}",linkId,standatdMatchId,s);
               return;
           }

           StandardMatchInfo matchInfo = StandardMatchInfoService.getItem(standatdMatchId);
           if(matchInfo==null){
               log.info("::{}::pls通知比分下发:无标准赛事,赛事ID:{},{}",linkId,standatdMatchId);
               return;
           }
           StandardMatchScores scores = scoresRedisHelp.getCatchStandScoreByMatchId(standatdMatchId);
           //无标准比分时，查询三方比分
           if(null==scores || StrUtil.isEmpty(scores.getScoreJson())){
               log.info("::{}::pls通知比分下发:无标准比分,赛事ID:{},{}",linkId,standatdMatchId,scores);
               ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standatdMatchId,sell.getBusinessEvent());
               if(thirdMatchInfo==null){
                   log.info("::{}::pls通知比分下发:无三方赛事,赛事ID:{},{}",linkId,standatdMatchId);
                   return;
               }
               MatchScoresInfo matchScoreInfo = matchScoreInfoRepository.selectByExample(thirdMatchInfo.getId(),SourceTypeEnum.LIVE_DATA.getCode());
               if(matchScoreInfo==null){
                   log.info("::{}::pls通知比分下发:无三方赛事，无三方比分,三方赛事ID:{},{}",linkId,thirdMatchInfo.getId(),thirdMatchInfo);
                   return;
               }
               log.info("::{}::pls通知比分下发:同步三方比分至标准比分下发：{}",linkId,standatdMatchId);

               //同步比分下发到PLS
               scores.setScoreJson(matchScoreInfo.getScoresJson());
           }
           MatchEventInfo eventData = buildDataEvent(matchInfo);
           //1.发送 标准比分
           CommonStandardScoresDto commonScoresDto = messageBuilderUtils.buildStandardMatchScoreCommonScoresDto(scores, eventData,null);
           StandardSportTournament tournment =  standardSportTournamentServiceImpl.getItem(matchInfo.getStandardTournamentId());
           if(tournment!=null){
               commonScoresDto.setPlsStandardTournamentId(tournment.getPlsStandardTournamentId());
           }
           commonScoresDto.setMatchStatus(matchInfo.getMatchStatus());
           commonScoresDto.setLinkedId(matchInfo.getId()+"");
           commonScoresDto.setPlsStandardMatchId(matchInfo.getPlsStandardMatchId());
           scoresProducer.sendPlsScores(commonScoresDto);
           extWatch.stop();
           log.info("pls通知比分下发：{},用时:{}",standatdMatchId,extWatch.getTotalTimeMillis());
       }catch (Exception e){
           log.error("队列匹配赛事数据异常：{}",e.getMessage(),e);
       }
    }

    private MatchEventInfo buildDataEvent(StandardMatchInfo matchInfo) {
        MatchEventInfo eventData = new MatchEventInfo();
        eventData.setLinkId(matchInfo.getId()+"");
        eventData.setStandardMatchId(matchInfo.getId());
        eventData.setSportId(SportTypeEnum.FOOTBALL.getValue());
        eventData.setSourceType(SourceTypeEnum.LIVE_DATA.getCode());
        eventData.setMatchPeriodId(matchInfo.getMatchPeriodId());
        eventData.setSecondsFromStart(new Long(matchInfo.getSecondsMatchStart()));
        return eventData;
    }

}
