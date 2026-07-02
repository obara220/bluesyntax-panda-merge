package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.bo.StandardMatchStatusBO;
import com.panda.merge.common.enums.EventCodeEnum;
import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMatchStatusMessage;
import com.panda.merge.dto.message.StandardSportMarketSellMessage;
import com.panda.merge.model.*;
import com.panda.merge.service.MatchEventInfoService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.panda.merge.config.RedisConfig.REDIS_HOUR_TIME;
import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 标准赛事状态实时通知
 *
 * @author : bevan
 * @since 2021年1月9日18:43:06
 */
@Slf4j
@Component
public class StandardMatchStatusProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private MatchEventInfoService matchEventInfoService;


    /**
     * 标准赛事状态实时通知下游
     */
    public void sendStandardMatchStatus(String linkId, StandardMatchInfo standardMatchInfo, Long dataSourceTime) {
        StandardMatchStatusMessage standardMatchStatusMessage = new StandardMatchStatusMessage();
        BeanUtils.copyProperties(standardMatchInfo, standardMatchStatusMessage);
        standardMatchStatusMessage.setStandardMatchId(standardMatchInfo.getId());
        Request<StandardMatchStatusMessage> requestMsg = new Request<>();
        requestMsg.setData(standardMatchStatusMessage);
        requestMsg.setLinkId(linkId);
        requestMsg.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<StandardMatchStatusMessage>> requestMessageBuilder = MessageBuilder.withPayload(requestMsg).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("STANDARD_MATCH_STATUS:" + standardMatchInfo.getId(), requestMessageBuilder.build());
        log.info("linkId=【{}】标准赛事状态信息下发完成,topic=STANDARD_MATCH_STATUS,request:{}", linkId, JSON.toJSONString(requestMsg));

        //普通足蓝第一次下发滚球状态需要下发额外通知（只发送一次）
        if (ONE.equals(standardMatchInfo.getMatchType())) {
            if (MatchStatusEnum.Live.value.equals(standardMatchInfo.getMatchStatus())) {
                //只需要下发一次
                String lockKey = RedisConfig.REDIS_KEY_DATABASE + "::MATCH_PRE_ODDS_ADMIN:" + standardMatchInfo.getId();
                if (redisService.tryLockOnce(lockKey, lockKey, REDIS_HOUR_TIME * FIVES)) {
                    sendMatchBeginToOddsAdmin(linkId, standardMatchInfo.getId());
                }
            }
        }
    }

    /**
     * 标准赛事状态实时通知下游(V02-push)
     */
    public void sendStandardMatchStatusToV02(String linkId, StandardMatchInfo standardMatchInfo, Long dataSourceTime) {
        Map<String, Object> requestMap = new HashMap<>(4);
        requestMap.put("standardMatchId", standardMatchInfo.getId());
        requestMap.put("status", 1);
        MessageBuilder<String> requestMessageBuilder = MessageBuilder.withPayload(JSON.toJSONString(requestMap)).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("MATCH_INFO_V02:" + standardMatchInfo.getId(), requestMessageBuilder.build());
        log.info("linkId=【{}】标准赛事状态信息下发V02-push完成,topic=MATCH_INFO_V02,request:{}, dataSourceTime:{}", linkId, JSON.toJSONString(requestMap), dataSourceTime);
    }

    /**
     * 标准赛事开售实时通知下游调用
     */
    public void sendStandardMatchSold(String linkId, StandardMatchInfo standardMatchInfo, StandardSportMarketSell standardSportMarketSell) {
        StandardSportMarketSellMessage standardMatchInfoBO = new StandardSportMarketSellMessage();
        standardMatchInfoBO.setStandardMatchId(standardMatchInfo.getId());
        standardMatchInfoBO.setMatchManageId(standardMatchInfo.getMatchManageId());
        standardMatchInfoBO.setPreRiskManagerCode(standardSportMarketSell.getPreRiskManagerCode());
        standardMatchInfoBO.setLiveRiskManagerCode(standardSportMarketSell.getLiveRiskManagerCode());

        Request<StandardSportMarketSellMessage> requestMsg = new Request<>();
        requestMsg.setData(standardMatchInfoBO);
        requestMsg.setLinkId(linkId);
        MessageBuilder<Request<StandardSportMarketSellMessage>> requestMessageBuilder = MessageBuilder.withPayload(requestMsg).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("STANDARD_MATCH_SOLD:" + standardMatchInfo.getId(), requestMessageBuilder.build());
        log.info("linkId=【{}】标准赛事开售实时通知下游完成,topic=STANDARD_MATCH_SOLD,request:{}", linkId, JSON.toJSONString(requestMsg));
    }

    /**
     * 足蓝开赛需要下发额外通知（只发送一次）
     */
    public void sendMatchBeginToOddsAdmin(String linkId, Long standardMatchInfoId) {
        linkId = linkId + "_" + standardMatchInfoId;
        Request<Long> request = new Request<>();
        request.setLinkId(linkId);
        request.setData(standardMatchInfoId);
        MessageBuilder<Request<Long>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("MATCH_PRE_ODDS_ADMIN:" + standardMatchInfoId, builder.build());
        log.info("linkId=【{}】通知赔率服务开始自动构建滚球赔率,topic=MATCH_PRE_ODDS_ADMIN,消息体:{}", linkId, standardMatchInfoId);
    }

    /**
     * 标准赛事状态实时通知下游(比分网)
     */
    public void sendStandardMatchStatusPls(String linkId, StandardMatchInfo standardMatchInfo, Long dataSourceTime, StandardSportTournament standardSportTournament) {
        StandardMatchStatusBO standardMatchStatusBO = new StandardMatchStatusBO();
        BeanUtils.copyProperties(standardMatchInfo, standardMatchStatusBO);
        standardMatchStatusBO.setStandardMatchId(standardMatchInfo.getId());
        if (standardSportTournament != null) {
            standardMatchStatusBO.setPlsStandardTournamentId(standardSportTournament.getPlsStandardTournamentId());
        }

        Request<StandardMatchStatusBO> requestMsg = new Request<>();
        requestMsg.setData(standardMatchStatusBO);
        requestMsg.setLinkId(linkId);
        requestMsg.setDataSourceTime(dataSourceTime);
        MessageBuilder<Request<StandardMatchStatusBO>> requestMessageBuilder = MessageBuilder.withPayload(requestMsg).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send(STANDARD_MATCH_INFO_STATUS_PLS + ":" + standardMatchInfo.getId(), requestMessageBuilder.build());
        log.info("linkId=【{}】标准赛事状态信息下发完成,topic=STANDARD_MATCH_INFO_STATUS_PLS,request:{}", linkId, JSON.toJSONString(requestMsg));
    }


    /**
     * 4248 【赛程】赛事中断场景优化: 状态源赛事中断&取消映射至赛事事件中断或取消
     */
    public void putMatchEventInfo(String linkId, StandardMatchInfo standardMatchInfo, String businessEvent, Long matchPeriodId) {
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standardMatchInfo.getId(), businessEvent);
        if (thirdMatchInfo != null) {
            MatchEventInfo matchEventInfo = getMatchEventInfo(linkId, standardMatchInfo, businessEvent);
            if (matchEventInfo != null) {
                linkId = linkId + "_4248";
                matchEventInfo.setEventCode(EventCodeEnum.MATCH_STATUS.code);
                matchEventInfo.setLinkId(linkId);
                matchEventInfo.setMatchPeriodId(matchPeriodId);
                matchEventInfo.setExtraInfo(matchEventInfo.getMatchPeriodId() + "");
                //重新计算秒数
                long second = (System.currentTimeMillis() - matchEventInfo.getEventTime()) / 1000;
                matchEventInfo.setSecondsFromStart(matchEventInfo.getSecondsFromStart() + second);
                matchEventInfo.setEventTime(System.currentTimeMillis());
                matchEventInfo.setThirdEventId(matchEventInfo.getThirdEventId() + "_" + matchPeriodId);
                putMatchEventInfoMq(linkId, matchEventInfo, businessEvent);
            } else {
                log.info("linkId=【{}】putMatchEventInfo,businessEvent={},赛事阶段事件信息为空,不处理！", linkId, businessEvent);
            }
        } else {
            log.info("linkId=【{}】putMatchEventInfo,businessEvent={},赛事信息为空,不处理！", linkId, businessEvent);
        }
    }

    /**
     * 4248 【赛程】赛事中断场景优化: 状态源赛事中断&取消映射至赛事事件中断或取消
     * 103497 【生产】【产品】【操盘风控】足球-常规时间状态源-异常下发结束优化
     */
    public void putMatchEventInfoMq(String linkId, MatchEventInfo matchEventInfo, String businessEvent) {
        if (matchEventInfo != null) {
            //补发到事件通道
            MatchEventInfoDTO matchEventInfoDTO = JSON.parseObject(JSON.toJSONString(matchEventInfo), MatchEventInfoDTO.class);
            Request<MatchEventInfoDTO> request = new Request(matchEventInfoDTO, linkId);
            log.info("linkId=【{}】putMatchEventInfo，状态转换事件信息处理开始,request={}", request.getLinkId(), JSON.toJSONString(request));
            MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, linkId);
            rocketMqTemplate.send(THIRD_MATCH_EVENT_INFO_API + ":" + matchEventInfo.getThirdMatchSourceId(), builder.build());
            log.info("linkId=【{}】putMatchEventInfo，状态转换事件信息处理结束", request.getLinkId());
        } else {
            log.info("linkId=【{}】putMatchEventInfo,businessEvent={},赛事阶段事件信息为空,不处理！", linkId, businessEvent);
        }
    }

    public MatchEventInfo getMatchEventInfo(String linkId, StandardMatchInfo standardMatchInfo, String businessEvent) {
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standardMatchInfo.getId(), businessEvent);
        if (thirdMatchInfo != null) {
            String standardEventLastKey = String.format(ConstantSystem.getStandardEventLastKey(), standardMatchInfo.getId());
            Object obj = redisService.get(standardEventLastKey);
            MatchEventInfo matchEventInfo;
            if (obj == null) {
                matchEventInfo = matchEventInfoService.getMatchEventInfo(thirdMatchInfo.getId(), businessEvent, EventCodeEnum.MATCH_STATUS.code);
            } else {
                matchEventInfo = (MatchEventInfo) obj;
            }
            return matchEventInfo;
        } else {
            log.info("linkId=【{}】getMatchEventInfo,businessEvent={},赛事信息为空,不处理！", linkId, businessEvent);
        }
        return null;
    }

}
