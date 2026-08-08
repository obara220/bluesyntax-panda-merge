package com.panda.merge.mq.consumer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.SourceTypeEnum;
import com.panda.merge.dto.FootballPenaltyScores;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.dto.Request;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.mq.producer.ScoresProducer;
import com.panda.merge.service.IScoresService;
import com.panda.merge.utils.JsonMapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

/**
 * 同步结算事件到比分中心（结算2.0手动操作下发比分）
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "MATCH_SETTLE_EVENT",
        consumerGroup = "scores-group2-MATCH_SETTLE_EVENT-NEW",
        consumeThreadMax = 20,
        consumeTimeout = 10000L
)
@DependsOn("scoresAdminApplication")
public class MatchEndSettleEventConsumer implements RocketMQListener<Request<MatchSettleEvent>> {

    @Autowired
    MatchScoresInfoMapper matchScoresInfoMapper;

    @Autowired
    ThirdMatchInfoMapper thirdMatchInfoMapper;

    @Autowired
    ScoresProducer scoresProducer;

    @Autowired
    IScoresService scoresService;

    @Autowired
    RedisService redisService;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
    //结算同步比分中心开关key
    private static final String SETTLE_SYNC_SCORES_KEY = "settle_sync_scores";

    @Override
    public void onMessage(Request<MatchSettleEvent> request) {
        log.info("MatchEndSettleEventConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch && commonProducer.getDatacenterMatchIds(request.getData().getStandardMatchId().toString())) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(request, "datacenter-MATCH_SETTLE_EVENT",request.getLinkId());
            return;
        }
        String linkId = request.getLinkId();
        Long start = System.currentTimeMillis();
        log.info("{} MatchSettleEventConsumer事件比分中心处理开始：{}", linkId, start);
        if (request == null || request.getData() == null) {
            return;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(request.getData().getStandardMatchId());
        if(ObjectUtils.isEmpty(standardMatchInfo)||standardMatchInfo.getMatchStatus()==null||standardMatchInfo.getMatchStatus()!=3){
            return;
        }
        //1、过滤不符合的消息
        if (!check(request.getData())) {
            return;
        }

        //2、处理同步比分中心的逻辑
        String key = "MATCH_SETTLE_SYNC_SCORES:" + request.getData().getStandardMatchId();
        try {
            if (redisService.tryLock(key, linkId, 3, 2)) {
                if(request.getData().getSportId().equals(1L)){
                    handleData(request);
                }
            }
        }catch (Exception e){
            log.error("error data:{},msg:{}",request.getData(),e.getMessage());

        }finally {
            redisService.unLock(key, linkId);
        }


        log.info("{} MatchSettleEventConsumer事件比分中心处理结束耗时：{}", linkId, System.currentTimeMillis()-start);
    }

    /**
     * 检查mq消息是否合法
     * @param data
     * @return
     */
    private boolean check(MatchSettleEvent data){
        //非已结算不对接
        if (data.getStatus()==null || !data.getStatus().equals(3)) {
            return false;
        }
        //非进球比分事件不对接
        if(data.getEventType()==null ||  !data.getEventType().equals(1)){
            return false;
        }
        return true;
    }

    /**
     * 处理同步比分中心的逻辑
     * @param request
     */
    private void handleData(Request<MatchSettleEvent> request) {
        MatchSettleEvent matchSettleEvent = request.getData();
        //获取三方赛事id
        ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
        thirdMatchInfoExample.createCriteria().andReferenceIdEqualTo(matchSettleEvent.getStandardMatchId());
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
        if(CollectionUtil.isEmpty(thirdMatchInfos)){
            log.error("1--------{}",matchSettleEvent);
            return;
        }

        //比分中心下多个数据商
        for(ThirdMatchInfo thirdMatchInfo : thirdMatchInfos) {
            MatchScoresInfoExample scoresInfoExample = new MatchScoresInfoExample();
            scoresInfoExample.createCriteria().andThirdMatchIdEqualTo(thirdMatchInfo.getId())
                    .andDataSourceTypeEqualTo(SourceTypeEnum.LIVE_DATA.getCode().toString());
            List<MatchScoresInfo> matchScoresInfos = matchScoresInfoMapper.selectByExample(scoresInfoExample);
            if (CollectionUtil.isEmpty(matchScoresInfos)) {
               continue;
            }
            for (MatchScoresInfo matchScoresInfo : matchScoresInfos) {
                if(StringUtils.isBlank(matchScoresInfo.getScoresJson())){
                    continue;
                }
                JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
                Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);
                FootballScores wholeSores = allPeriodScores.get(WHOLE_MATCH);
                FootballScores periodScores = allPeriodScores.get(matchSettleEvent.getPeriodId());

                //点球大战结算阶段较特殊，period为120
                if(periodScores == null && !matchSettleEvent.getSettleNum().equals("1028")){
                    continue;
                }

                //点球事件
                if (matchSettleEvent.getPeriodId().equals(50l) && matchSettleEvent.getSettleNum().equals("1030")) {
                    //计算点球信息
                    String scoresJsonExtra = handlePenaltyScores(matchSettleEvent, matchScoresInfo, allPeriodScores);
                    if(StringUtils.isNotEmpty(scoresJsonExtra)) {
                        matchScoresInfo.setScoresJsonExtra(scoresJsonExtra);
                    }
                }else if(matchSettleEvent.getSettleNum().equals("1028")) {
                    //点球大战结束
                    periodScores = allPeriodScores.get(50l);
                    if(periodScores == null){
                        continue;
                    }
                    periodScores.getGoal().setHome(matchSettleEvent.getT1());
                    periodScores.getGoal().setAway(matchSettleEvent.getT2());
                    matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
                }else if ("none".equals(matchSettleEvent.getHomeAway()) && "fa_card".equals(matchSettleEvent.getEventCode())){
                    //最后一张罚牌

                    //下半场和加时赛下半场获取的都是总罚牌数(包含上半场)，所以需要计算差值获取当前阶段罚牌数
                    /*Integer xFirstT1 = matchSettleEvent.getFirstT1();
                    Integer xFirstT2 = matchSettleEvent.getFirstT2();
                    Integer xSecondT1 = matchSettleEvent.getSecondT1();
                    Integer xSecondT2 = matchSettleEvent.getSecondT2();
                    Integer xT1 = matchSettleEvent.getT1();
                    Integer xT2 = matchSettleEvent.getT2();
                    if (matchSettleEvent.getPeriodId().equals(7l)) {
                        FootballScores footballScores_6L = allPeriodScores.get(6L);
                        if(footballScores_6L != null){
                            xFirstT1 = matchSettleEvent.getFirstT1() - footballScores_6L.getYellowCard().getHome();
                            xFirstT2 = matchSettleEvent.getFirstT2() - footballScores_6L.getYellowCard().getAway();
                            xSecondT1 = matchSettleEvent.getSecondT1() - footballScores_6L.getRedCard().getHome();
                            xSecondT2 = matchSettleEvent.getSecondT2() - footballScores_6L.getRedCard().getAway();
                            xT1 = matchSettleEvent.getT1() - footballScores_6L.getFaCard().getHome();
                            xT2 = matchSettleEvent.getT2() - footballScores_6L.getFaCard().getAway();
                        }
                    }else if(matchSettleEvent.getPeriodId().equals(42l)){
                        FootballScores footballScores_41L = allPeriodScores.get(41L);
                        if(footballScores_41L != null){
                            xFirstT1 = matchSettleEvent.getFirstT1() - footballScores_41L.getYellowCard().getHome();
                            xFirstT2 = matchSettleEvent.getFirstT2() - footballScores_41L.getYellowCard().getAway();
                            xSecondT1 = matchSettleEvent.getSecondT1() - footballScores_41L.getRedCard().getHome();
                            xSecondT2 = matchSettleEvent.getSecondT2() - footballScores_41L.getRedCard().getAway();
                            xT1 = matchSettleEvent.getT1() - footballScores_41L.getFaCard().getHome();
                            xT2 = matchSettleEvent.getT2() - footballScores_41L.getFaCard().getAway();
                        }
                    }
                    periodScores.getYellowCard().setHome(xFirstT1);
                    periodScores.getYellowCard().setAway(xFirstT2);
                    periodScores.getRedCard().setHome(xSecondT1);
                    periodScores.getRedCard().setAway(xSecondT2);
                    periodScores.getFaCard().setHome(xT1);
                    periodScores.getFaCard().setAway(xT2);

                    //上下半场和加时赛上下半场需要同步全场比分
                    if (ArrayUtil.contains(new long[]{6L, 7L, 41L, 42L}, matchSettleEvent.getPeriodId())) {
                        Integer yellowCardHome = 0;
                        Integer yellowCardAway = 0;
                        Integer redCardHome = 0;
                        Integer redCardAway = 0;
                        Integer faCardHome = 0;
                        Integer faCardAway = 0;

                        FootballScores footballScores_6L = allPeriodScores.get(6L);
                        FootballScores footballScores_7L = allPeriodScores.get(7L);
                        FootballScores footballScores_41L = allPeriodScores.get(41L);
                        FootballScores footballScores_42L = allPeriodScores.get(42L);
                        if (footballScores_6L != null) {
                            yellowCardHome += footballScores_6L.getYellowCard().getHome();
                            yellowCardAway += footballScores_6L.getYellowCard().getAway();
                            redCardHome += footballScores_6L.getRedCard().getHome();
                            redCardAway += footballScores_6L.getRedCard().getAway();
                            faCardHome += footballScores_6L.getFaCard().getHome();
                            faCardAway += footballScores_6L.getFaCard().getAway();
                        }
                        if (footballScores_7L != null) {
                            yellowCardHome += footballScores_7L.getYellowCard().getHome();
                            yellowCardAway += footballScores_7L.getYellowCard().getAway();
                            redCardHome += footballScores_7L.getRedCard().getHome();
                            redCardAway += footballScores_7L.getRedCard().getAway();
                            faCardHome += footballScores_7L.getFaCard().getHome();
                            faCardAway += footballScores_7L.getFaCard().getAway();
                        }
                        if (footballScores_41L != null) {
                            yellowCardHome += footballScores_41L.getYellowCard().getHome();
                            yellowCardAway += footballScores_41L.getYellowCard().getAway();
                            redCardHome += footballScores_41L.getRedCard().getHome();
                            redCardAway += footballScores_41L.getRedCard().getAway();
                            faCardHome += footballScores_41L.getFaCard().getHome();
                            faCardAway += footballScores_41L.getFaCard().getAway();
                        }
                        if (footballScores_42L != null) {
                            yellowCardHome += footballScores_42L.getYellowCard().getHome();
                            yellowCardAway += footballScores_42L.getYellowCard().getAway();
                            redCardHome += footballScores_42L.getRedCard().getHome();
                            redCardAway += footballScores_42L.getRedCard().getAway();
                            faCardHome += footballScores_42L.getFaCard().getHome();
                            faCardAway += footballScores_42L.getFaCard().getAway();
                        }

                        wholeSores.getYellowCard().setHome(yellowCardHome);
                        wholeSores.getYellowCard().setAway(yellowCardAway);
                        wholeSores.getRedCard().setHome(redCardHome);
                        wholeSores.getRedCard().setAway(redCardAway);
                        wholeSores.getFaCard().setHome(faCardHome);
                        wholeSores.getFaCard().setAway(faCardAway);
                    }
                    matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));*/
                    setScores(matchSettleEvent,allPeriodScores,wholeSores,periodScores,matchScoresInfo);
                }
                matchScoresInfo.setModifyTime(System.currentTimeMillis());
                log.info("组装对象matchScoresInfo：{}",matchScoresInfo);
                matchScoresInfoMapper.updateByPrimaryKey(matchScoresInfo);

                //mq通知下游
                scoresProducer.sendToMQ(thirdMatchInfo, matchScoresInfo, request.getLinkId());
            }
        }
    }

    /**
     * 计算点球信息
     * @param matchSettleEvent
     * @param matchScoresInfo
     * @param allPeriodScores
     */
    private String handlePenaltyScores(MatchSettleEvent matchSettleEvent, MatchScoresInfo matchScoresInfo, Map<Long, FootballScores> allPeriodScores) {
//        FootballScores periodScores= allPeriodScores.get(50l);
//        //新建该阶段值
//        if(periodScores==null) {
//            periodScores = new FootballScores(50l);
//        }
//        if("goal".equals(matchSettleEvent.getEventCode())){
//            periodScores.getGoal().setHome(matchSettleEvent.getT1());
//            periodScores.getGoal().setAway(matchSettleEvent.getT2());
//        }
        //scoresJsonExtra 点球信息
        String extraScores =matchScoresInfo.getScoresJsonExtra();
        if(StringUtils.isEmpty(extraScores)){
            return null;
        }
        //是否要构建点球大战比分呢
        FootballPenaltyScores footballPenaltyScores = JSONObject.toJavaObject((JSONObject.parseObject(extraScores)) , FootballPenaltyScores.class);
        //计算点球信息
        footballPenaltyScores.calutionPenaltyScores(matchSettleEvent);
        return JSONObject.toJSONString(footballPenaltyScores);
    }

    private void setScores(MatchSettleEvent matchSettleEvent,Map<Long, FootballScores> allPeriodScores,FootballScores wholeSores,FootballScores periodScores,MatchScoresInfo matchScoresInfo){

        Integer xFirstT1 = matchSettleEvent.getFirstT1();
        Integer xFirstT2 = matchSettleEvent.getFirstT2();
        Integer xSecondT1 = matchSettleEvent.getSecondT1();
        Integer xSecondT2 = matchSettleEvent.getSecondT2();
        Integer xT1 = matchSettleEvent.getT1();
        Integer xT2 = matchSettleEvent.getT2();
        if (matchSettleEvent.getPeriodId().equals(7l)) {
            FootballScores footballScores_6L = allPeriodScores.get(6L);
            if(footballScores_6L != null){
                xFirstT1 = matchSettleEvent.getFirstT1() - footballScores_6L.getYellowCard().getHome();
                xFirstT2 = matchSettleEvent.getFirstT2() - footballScores_6L.getYellowCard().getAway();
                xSecondT1 = matchSettleEvent.getSecondT1() - footballScores_6L.getRedCard().getHome();
                xSecondT2 = matchSettleEvent.getSecondT2() - footballScores_6L.getRedCard().getAway();
                xT1 = matchSettleEvent.getT1() - footballScores_6L.getFaCard().getHome();
                xT2 = matchSettleEvent.getT2() - footballScores_6L.getFaCard().getAway();
            }
        }else if(matchSettleEvent.getPeriodId().equals(42l)){
            FootballScores footballScores_41L = allPeriodScores.get(41L);
            if(footballScores_41L != null){
                xFirstT1 = matchSettleEvent.getFirstT1() - footballScores_41L.getYellowCard().getHome();
                xFirstT2 = matchSettleEvent.getFirstT2() - footballScores_41L.getYellowCard().getAway();
                xSecondT1 = matchSettleEvent.getSecondT1() - footballScores_41L.getRedCard().getHome();
                xSecondT2 = matchSettleEvent.getSecondT2() - footballScores_41L.getRedCard().getAway();
                xT1 = matchSettleEvent.getT1() - footballScores_41L.getFaCard().getHome();
                xT2 = matchSettleEvent.getT2() - footballScores_41L.getFaCard().getAway();
            }
        }
        periodScores.getYellowCard().setHome(xFirstT1);
        periodScores.getYellowCard().setAway(xFirstT2);
        periodScores.getRedCard().setHome(xSecondT1);
        periodScores.getRedCard().setAway(xSecondT2);
        periodScores.getFaCard().setHome(xT1);
        periodScores.getFaCard().setAway(xT2);

        //上下半场和加时赛上下半场需要同步全场比分
        if (ArrayUtil.contains(new long[]{6L, 7L, 41L, 42L}, matchSettleEvent.getPeriodId())) {
            Integer yellowCardHome = 0;
            Integer yellowCardAway = 0;
            Integer redCardHome = 0;
            Integer redCardAway = 0;
            Integer faCardHome = 0;
            Integer faCardAway = 0;

            FootballScores footballScores_6L = allPeriodScores.get(6L);
            FootballScores footballScores_7L = allPeriodScores.get(7L);
            FootballScores footballScores_41L = allPeriodScores.get(41L);
            FootballScores footballScores_42L = allPeriodScores.get(42L);
            if (footballScores_6L != null) {
                yellowCardHome += footballScores_6L.getYellowCard().getHome();
                yellowCardAway += footballScores_6L.getYellowCard().getAway();
                redCardHome += footballScores_6L.getRedCard().getHome();
                redCardAway += footballScores_6L.getRedCard().getAway();
                faCardHome += footballScores_6L.getFaCard().getHome();
                faCardAway += footballScores_6L.getFaCard().getAway();
            }
            if (footballScores_7L != null) {
                yellowCardHome += footballScores_7L.getYellowCard().getHome();
                yellowCardAway += footballScores_7L.getYellowCard().getAway();
                redCardHome += footballScores_7L.getRedCard().getHome();
                redCardAway += footballScores_7L.getRedCard().getAway();
                faCardHome += footballScores_7L.getFaCard().getHome();
                faCardAway += footballScores_7L.getFaCard().getAway();
            }
            if (footballScores_41L != null) {
                yellowCardHome += footballScores_41L.getYellowCard().getHome();
                yellowCardAway += footballScores_41L.getYellowCard().getAway();
                redCardHome += footballScores_41L.getRedCard().getHome();
                redCardAway += footballScores_41L.getRedCard().getAway();
                faCardHome += footballScores_41L.getFaCard().getHome();
                faCardAway += footballScores_41L.getFaCard().getAway();
            }
            if (footballScores_42L != null) {
                yellowCardHome += footballScores_42L.getYellowCard().getHome();
                yellowCardAway += footballScores_42L.getYellowCard().getAway();
                redCardHome += footballScores_42L.getRedCard().getHome();
                redCardAway += footballScores_42L.getRedCard().getAway();
                faCardHome += footballScores_42L.getFaCard().getHome();
                faCardAway += footballScores_42L.getFaCard().getAway();
            }

            wholeSores.getYellowCard().setHome(yellowCardHome);
            wholeSores.getYellowCard().setAway(yellowCardAway);
            wholeSores.getRedCard().setHome(redCardHome);
            wholeSores.getRedCard().setAway(redCardAway);
            wholeSores.getFaCard().setHome(faCardHome);
            wholeSores.getFaCard().setAway(faCardAway);
        }
        matchScoresInfo.setScoresJson(JSONObject.toJSONString(allPeriodScores));
    }
}
