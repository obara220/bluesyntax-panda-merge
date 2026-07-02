package com.panda.merge.mq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.OperationLogMessage;
import com.panda.merge.mapper.MatchScoresPdLogMapper;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.model.MatchFreezeSettlePdLogEvent;
import com.panda.merge.model.MatchScoresPdLog;
import com.panda.merge.model.MatchScoresPdLogExample;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.service.IMatchScorePdLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 结算2.0 下发的结算冻结？
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "OSMC_CONSOLE_FREEZE", consumerGroup = "scores-group-OSMC_CONSOLE_FREEZE", consumeThreadMax = 2, consumeTimeout = 10000L)
@DependsOn("scoresAdminApplication")
public class MatchFreezeSettlePdLogConsumer implements RocketMQListener<MatchFreezeSettlePdLogEvent> {

    @Autowired
    IMatchScorePdLogService iMatchScorePdLogService;

    @Autowired
    RocketMQTemplate rocketMqTemplate;

    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    MatchScoresPdLogMapper matchScoresPdLogMapper;

    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;

    @Override
    public void onMessage(MatchFreezeSettlePdLogEvent matchFreezeSettlePdLogEvent) {
        log.info("MatchFreezeSettlePdLogConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch && commonProducer.getDatacenterMatchIds(matchFreezeSettlePdLogEvent.getMatchId())) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(matchFreezeSettlePdLogEvent, "datacenter-OSMC_CONSOLE_FREEZE",matchFreezeSettlePdLogEvent.getMatchId());
            return;
        }
        /**
         * 查询是否有事件未处理
         * 有则拉出来后 加入本次事件处理机制
         * */
        Long start = System.currentTimeMillis();
        log.info("MatchScoresPdLogConsumer 打印PD报球板结算冻结事件日志开始：{}", start);
        if (matchFreezeSettlePdLogEvent == null || matchFreezeSettlePdLogEvent.getSportId() <= 0) {
            return;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoMapper.selectByPrimaryKey(Long.valueOf(matchFreezeSettlePdLogEvent.getMatchId()));
        MatchScoresPdLogExample example = new MatchScoresPdLogExample();
        example.createCriteria().andMatchManageIdEqualTo(standardMatchInfo.getMatchManageId()).andOperateRearTextLike("%冻结%");
        example.setOrderByClause("id desc limit 1");
        List<MatchScoresPdLog> matchScoresPdLogList = matchScoresPdLogMapper.selectByExample(example);
        if ("Y".equals(matchFreezeSettlePdLogEvent.getCategory())) {
            if (CollectionUtils.isEmpty(matchScoresPdLogList)) {
                matchFreezeSettlePdLogEvent.setOperateForw("-");
            } else {
                matchFreezeSettlePdLogEvent.setOperateForw("取消冻结");
            }
        }
        if ("N".equals(matchFreezeSettlePdLogEvent.getCategory())) {
            if (CollectionUtils.isEmpty(matchScoresPdLogList)) {
                matchFreezeSettlePdLogEvent.setOperateForw("-");
            } else {
                matchFreezeSettlePdLogEvent.setOperateForw("冻结");
            }
        }
        doSendLogToRisk(standardMatchInfo, matchFreezeSettlePdLogEvent, matchScoresPdLogList);
        iMatchScorePdLogService.matchFreezeSettleLog(matchFreezeSettlePdLogEvent);
    }

    public void doSendLogToRisk(StandardMatchInfo standardMatchInfo, MatchFreezeSettlePdLogEvent matchFreezeSettlePdLogEvent, List<MatchScoresPdLog> matchScoresPdLogList) {
        String linkId = String.valueOf(IdWorker.getId());
        OperationLogMessage operationLogMessage = buildOperationLogMessage(standardMatchInfo, matchFreezeSettlePdLogEvent, matchScoresPdLogList);
        if (Objects.isNull(operationLogMessage)) {
            log.error("{}buildOperationLogMessage构造冻结日志为空，不进行发送", linkId);
            return;
        }
        Request<OperationLogMessage> sendMessageRequest = new Request<>();
        sendMessageRequest.setData(operationLogMessage);
        sendMessageRequest.setLinkId(linkId);
        MessageBuilder<Request<OperationLogMessage>> builder =
                MessageBuilder.withPayload(sendMessageRequest)
                        .setHeader(MessageConst.PROPERTY_KEYS, sendMessageRequest.getLinkId());
        log.info("::{}::开始下发系统冻结日志到风控:{},topic:OPERATION_LOG_TO_RISK", standardMatchInfo, sendMessageRequest.getLinkId());
        rocketMqTemplate.asyncSend("OPERATION_LOG_TO_RISK", builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,OPERATION_LOG_TO_RISK send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "OPERATION_LOG_TO_RISK", throwable);
            }
        });
    }

    private OperationLogMessage buildOperationLogMessage(StandardMatchInfo standardMatchInfo, MatchFreezeSettlePdLogEvent dto, List<MatchScoresPdLog> matchScoresPdLogList) {
        try {
            OperationLogMessage operationLogMessage = new OperationLogMessage();
            operationLogMessage.setMatchId(standardMatchInfo.getId());
            operationLogMessage.setObjectName(standardMatchInfo.getHomeAwayInfo());
            operationLogMessage.setObjectId(standardMatchInfo.getMatchManageId());
            operationLogMessage.setExtObjectId("-");
            operationLogMessage.setExtObjectName("-");
            if ("Y".equals(dto.getCategory())) {
                operationLogMessage.setBehavior("冻结");
                operationLogMessage.setAfterVal("冻结");
                if (CollectionUtils.isEmpty(matchScoresPdLogList)) {
                    operationLogMessage.setBeforeVal("-");
                } else {
                    operationLogMessage.setBeforeVal("取消冻结");
                }
            }
            if ("N".equals(dto.getCategory())) {
                operationLogMessage.setBehavior("冻结");
                operationLogMessage.setAfterVal("取消冻结");
                if (CollectionUtils.isEmpty(matchScoresPdLogList)) {
                    operationLogMessage.setBeforeVal("-");
                } else {
                    operationLogMessage.setBeforeVal("冻结");
                }
            }
            operationLogMessage.setUserId(dto.getOperatorId());
            operationLogMessage.setUserName(dto.getOperatorName());
            operationLogMessage.setIp(dto.getIp());
            operationLogMessage.setOperateTime(new Date());
            if (standardMatchInfo.getSportId() == 1) {
                operationLogMessage.setOperatePageName("-");
                operationLogMessage.setOperatePageCode(207);
            }
            if (standardMatchInfo.getSportId() == 2) {
                operationLogMessage.setOperatePageName("-");
                operationLogMessage.setOperatePageCode(208);
            }
            operationLogMessage.setSportId(standardMatchInfo.getSportId().intValue());
            return operationLogMessage;
        } catch (Exception e) {
            log.error("buildOperationLogMessage构造冻结日志异常", e);
            return null;
        }
    }
}
