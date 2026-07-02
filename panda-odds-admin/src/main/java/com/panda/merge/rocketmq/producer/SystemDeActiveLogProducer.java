package com.panda.merge.rocketmq.producer;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.dto.message.StandardMarketMessage;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.OperationLogMessage;
import com.panda.merge.model.StandardMatchInfo;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Objects;

@Slf4j
@Component
public class SystemDeActiveLogProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    public void doSendLogToRisk(String linkId, StandardMatchInfo standardMatchInfo, StandardMarketMessage standardMarketMessage) {
        if (standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code)){
            return;
        }
        OperationLogMessage operationLogMessage = buildOperationLogMessage(standardMatchInfo, standardMarketMessage);
        if (Objects.isNull(operationLogMessage)) {
            log.error("{}buildOperationLogMessage构造自动关盘日志为空，不进行发送", linkId);
            return;
        }
        Request<OperationLogMessage> sendMessageRequest = new Request<>();
        sendMessageRequest.setData(operationLogMessage);
        sendMessageRequest.setLinkId(linkId);
        MessageBuilder<Request<OperationLogMessage>> builder =
            MessageBuilder.withPayload(sendMessageRequest)
                    .setHeader(MessageConst.PROPERTY_KEYS, sendMessageRequest.getLinkId());
        log.info("::{}::开始下发系统关盘日志到风控,topic:OPERATION_LOG_TO_RISK", sendMessageRequest.getLinkId());
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

    private OperationLogMessage buildOperationLogMessage(StandardMatchInfo standardMatchInfo, StandardMarketMessage standardMarketMessage) {
        try {
            Long marketCategoryId = standardMarketMessage.getMarketCategoryId();
            Long marketId = standardMarketMessage.getId();
            Integer status = standardMarketMessage.getThirdMarketSourceStatus();
            Integer marketType = standardMarketMessage.getMarketType();
            OperationLogMessage operationLogMessage = new OperationLogMessage();
            operationLogMessage.setMatchId(standardMatchInfo.getId());
            operationLogMessage.setObjectId(marketCategoryId.toString());
            operationLogMessage.setObjectName(marketCategoryId.toString());
            operationLogMessage.setExtObjectId(marketId.toString());
            operationLogMessage.setExtObjectName(standardMatchInfo.getHomeAwayInfo());
            operationLogMessage.setBehavior("开关封锁");
            operationLogMessage.setAfterVal("关");
            operationLogMessage.setUserName("auto");
            operationLogMessage.setIp("-");
            operationLogMessage.setOperateTime(new Date());
            if (marketType == 1) {
                operationLogMessage.setOperatePageName("早盘操盘");
                operationLogMessage.setOperatePageCode(14);
            } else if (marketType == 0) {
                operationLogMessage.setOperatePageName("滚球操盘");
                operationLogMessage.setOperatePageCode(17);
            }
            operationLogMessage.setBeforeVal(getStatusDesc(status));
            operationLogMessage.setPlayId(marketCategoryId);
            operationLogMessage.setSportId(standardMatchInfo.getSportId().intValue());
            return operationLogMessage;
        } catch (Exception e) {
            log.error("buildOperationLogMessage构造自动关盘日志异常", e);
            return null;
        }
    }

    private String getStatusDesc(Integer status) {
        if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.ACTIVE.equals(status)) {
            return "开";
        }
        if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.SUSPENDED.equals(status)) {
            return "封";
        }
        if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED.equals(status)) {
            return "关";
        }
        return "-";
    }
}
