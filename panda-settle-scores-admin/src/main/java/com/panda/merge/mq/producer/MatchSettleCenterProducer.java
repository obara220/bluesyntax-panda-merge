package com.panda.merge.mq.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.util.StringUtils;
import com.panda.merge.common.enums.BasketBallSettleNumEnum;
import com.panda.merge.common.enums.MatchPeriodEnum;
import com.panda.merge.common.enums.OperateLogTypeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.constant.MatchSettleCheckConstant;
import com.panda.merge.dto.MangoRequest;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.SettleQueryDTO;
import com.panda.merge.dto.advertise.MatchFreezeDto;
import com.panda.merge.dto.advertise.ScoresPeriodFreezeDto;
import com.panda.merge.dto.advertise.ScoresPeriodOrderFreezeDto;
import com.panda.merge.dto.message.MatchFreezeMessage;
import com.panda.merge.dto.message.MatchSettleInfoMessage;
import com.panda.merge.dto.message.OperationLogMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class MatchSettleCenterProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;


    //结算切换MQ下发
    public void pushMatchSettleType(MatchSettleInfoMessage matchSettleInfoMessage, String linkId) {
        Request<MatchSettleInfoMessage> reqMessage = new Request<>();
        reqMessage.setLinkId(linkId);
        reqMessage.setData(matchSettleInfoMessage);
        MessageBuilder<Request<MatchSettleInfoMessage>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId)
                .setHeader(MessageConst.PROPERTY_TAGS, matchSettleInfoMessage.getMatchId());
        rocketMqTemplate.send("MATCH_SETTLE_TYPE:" + linkId, builder.build());
        log.info("::{}::开始结算切换消息下发完毕,topic:MATCH_SETTLE_TYPE,request={}", linkId, JSON.toJSONString(reqMessage));
    }

    //赛事冻结MQ下发
    public void MatchFreeze(MatchFreezeDto matchFreezeDto,String type) {
        Request<MatchFreezeDto> reqMessage = new Request<>();
        String linkId = matchFreezeDto.getLinkId();
        reqMessage.setLinkId(linkId);
        reqMessage.setData(matchFreezeDto);
        MessageBuilder<Request<MatchFreezeDto>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, matchFreezeDto.getLinkId())
                .setHeader(MessageConst.PROPERTY_TAGS, type);
        rocketMqTemplate.send("MATCH_FREEZE:" + type, builder.build());
        log.info("::{}::下发冻结消息下发完毕,topic:MATCH_FREEZE,request={}", linkId, JSON.toJSONString(reqMessage));
    }

    //赛事冻结MQ下发
    public void MatchFreeze(MatchFreezeMessage matchFreezeDto, String type) {
        Request<MatchFreezeMessage> reqMessage = new Request<>();
        String linkId = matchFreezeDto.getLinkId();
        reqMessage.setLinkId(linkId);
        reqMessage.setData(matchFreezeDto);
        MessageBuilder<Request<MatchFreezeMessage>> builder = MessageBuilder.withPayload(reqMessage)
                .setHeader(MessageConst.PROPERTY_KEYS, matchFreezeDto.getMatchId())
                .setHeader(MessageConst.PROPERTY_TAGS, type);
        rocketMqTemplate.send("MATCH_FREEZE:" + type, builder.build());
        log.info("::{}::下发冻结消息下发完毕,topic:MATCH_FREEZE,request={}", linkId, JSON.toJSONString(reqMessage));
    }

    //删除事件芒果预警MQ下发
    public void manGoEarlyWarning(String linkId,String data, String type) {
        MangoRequest<String> reqMessage = new MangoRequest<>();
        //reqMessage.setLinkId(linkId);
        reqMessage.setData(data);
        reqMessage.setDataSourceCode("1917");
        String data1 = JSONObject.toJSONString(reqMessage).replace("\\n", "\n");
        MessageBuilder<String> builder = MessageBuilder.withPayload(data1)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("MATCH_DELETE_MANGO_EARLY_WARNING:" + type, builder.build());
        log.info("::{}::下发删除事件芒果预警消息下发完毕,topic:MATCH_DELETE_MANGO_EARLY_WARNING,request={}", linkId, data1);
    }

    public void secondSettleWarning(String linkId,String data, String type) {
        MangoRequest<List<String>> reqMessage = new MangoRequest<>();
        reqMessage.setData(Arrays.asList(data));
        reqMessage.setDataSourceCode("4053");
        String data1 = JSONObject.toJSONString(reqMessage).replace("\\n", "\n");
        MessageBuilder<String> builder = MessageBuilder.withPayload(data1)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("PA_COMMON_WARN_INFO:" + type, builder.build());
        log.info("::{}::二次结算告警下发完毕,topic:secondSettleWarning,request={}", linkId, data1);
    }

    //人员错误结算芒果预警MQ下发
    public void personErrorSettleManGoEarlyWarning(String linkId,String data, String type) {
        MangoRequest<String> reqMessage = new MangoRequest<>();
        //reqMessage.setLinkId(linkId);
        reqMessage.setData(data);
        reqMessage.setDataSourceCode("1917_2");
        String data1 = JSONObject.toJSONString(reqMessage).replace("\\n", "\n");
        MessageBuilder<String> builder = MessageBuilder.withPayload(data1)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("PERSON_ERROR_SETTLE_MANGO_EARLY_WARNING:" + type, builder.build());
        log.info("::{}::下发人员错误结算芒果预警消息下发完毕,topic:PERSON_ERROR_SETTLE_MANGO_EARLY_WARNING,request={}", linkId, data1);
    }

    public void doSendLogToRisk(StandardMatchInfo standardMatchInfo,MatchSettleInfo matchSettleInfo, MatchFreezeMessage matchFreezeMessage,MatchFreezeDto matchFreezeDto) {
        OperationLogMessage operationLogMessage = buildOperationLogMessage(standardMatchInfo,matchSettleInfo, matchFreezeMessage,matchFreezeDto);
        String linkId = IdWorker.getId() + "_NoType";
        if (Objects.isNull(operationLogMessage)) {
            log.error("{}buildOperationLogMessage构造结算2.0冻结日志为空，不进行发送", linkId);
            return;
        }
        Request<OperationLogMessage> sendMessageRequest = new Request<>();
        sendMessageRequest.setData(operationLogMessage);
        sendMessageRequest.setLinkId(linkId);
        MessageBuilder<Request<OperationLogMessage>> builder =
                MessageBuilder.withPayload(sendMessageRequest)
                        .setHeader(MessageConst.PROPERTY_KEYS, sendMessageRequest.getLinkId());
        log.info("::{}::开始下发结算2.0冻结日志到风控:{},topic:OPERATION_LOG_TO_RISK", matchSettleInfo.getStandardMatchId(), sendMessageRequest.getLinkId());
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

    private OperationLogMessage buildOperationLogMessage(StandardMatchInfo standardMatchInfo,MatchSettleInfo matchSettleInfo, MatchFreezeMessage matchFreezeMessage,MatchFreezeDto matchFreezeDto) {
        try {
            OperationLogMessage operationLogMessage = new OperationLogMessage();
            operationLogMessage.setMatchId(matchFreezeDto.getMatchId());
            if ("0".equals(matchFreezeDto.getSettleNum()) || StringUtils.isNullOrEmpty(matchFreezeDto.getSettleNum())){
                operationLogMessage.setObjectId(standardMatchInfo.getMatchManageId());
            }else {
                operationLogMessage.setObjectId(matchFreezeMessage.getEventId());
            }

            operationLogMessage.setObjectName(standardMatchInfo.getHomeAwayInfo());
            operationLogMessage.setExtObjectId("-");
            operationLogMessage.setExtObjectName("-");
            operationLogMessage.setBehavior("冻结");
            if (matchSettleInfo.getFreezeStatus() == 1) {
                operationLogMessage.setBeforeVal("取消冻结");
                operationLogMessage.setAfterVal("冻结");
//
//                if (matchFreezeDto.getMins() != null && matchFreezeDto.getMins() > 0) {
//                    String prar = OperateLogTypeEnum.SCORES_SETTLE_10037.getName().replace("s/", matchFreezeDto.getMins().toString());
//                    operationLogMessage.setBehavior(prar);
//                    operationLogMessage.setBeforeVal("-");
//                    operationLogMessage.setAfterVal(OperateLogTypeEnum.type_1.getName());
//                } else if (matchFreezeDto.getFreezeTime() != null && matchFreezeDto.getFreezeTime() != 0) {
//                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
//                    operationLogMessage.setBehavior(OperateLogTypeEnum.SCORES_PD_100139.getName());
//                    operationLogMessage.setBeforeVal(sdf.format(TimeUtils.millsSecondsEast8ZoneGmt()));
//                    operationLogMessage.setAfterVal(sdf.format(matchFreezeDto.getFreezeTime()));
//                } else {
//                    operationLogMessage.setBehavior((matchSettleInfo.getFreezeStatus() == 0 ?
//                            OperateLogTypeEnum.type_2.getName() : OperateLogTypeEnum.type_5.getName()));
//                    operationLogMessage.setBeforeVal("-");
//                    operationLogMessage.setAfterVal(OperateLogTypeEnum.type_1.getName());
//                }
            } else {
                operationLogMessage.setBeforeVal("冻结");
                operationLogMessage.setAfterVal("取消冻结");

//                operationLogMessage.setBehavior(OperateLogTypeEnum.type_2.getName());
//                operationLogMessage.setBeforeVal(OperateLogTypeEnum.type_1.getName());
//                operationLogMessage.setAfterVal(OperateLogTypeEnum.type_2.getName());
//                if (matchFreezeDto.getSettleNum() != null && !matchFreezeDto.getSettleNum().equals("0")) {
//                    operationLogMessage.setObjectName(matchFreezeDto.getSettleNum());
//                }
            }
            if (matchFreezeDto.getSportId()==2){
                if (matchFreezeDto.getFreezeSettleStatus() == 0) {
                    operationLogMessage.setBeforeVal(OperateLogTypeEnum.type_1.getName());
                    operationLogMessage.setAfterVal(OperateLogTypeEnum.type_2.getName());
                }
                if (matchFreezeDto.getFreezeSettleStatus() == 1) {
                    operationLogMessage.setBeforeVal(OperateLogTypeEnum.type_2.getName());
                    operationLogMessage.setAfterVal(OperateLogTypeEnum.type_1.getName());
                }
            }


            operationLogMessage.setUserName(matchFreezeDto.getOperatorName());
            operationLogMessage.setUserId("-");
            operationLogMessage.setIp(matchFreezeDto.getIpAddress());
            operationLogMessage.setOperateTime(new Date());
            if (standardMatchInfo.getSportId().intValue()==1){
                operationLogMessage.setOperatePageCode(209);
            }
            if (standardMatchInfo.getSportId().intValue()==2){
                operationLogMessage.setOperatePageCode(210);
            }

            operationLogMessage.setSportId(standardMatchInfo.getSportId().intValue());
            operationLogMessage.setParameterName("-");
            //防止为空逻辑
            operationLogMessage.setBeforeVal(operationLogMessage.getBeforeVal()==null?"-":operationLogMessage.getBeforeVal());
            operationLogMessage.setAfterVal(operationLogMessage.getAfterVal()==null?"-":operationLogMessage.getAfterVal());
            operationLogMessage.setBehavior(operationLogMessage.getBehavior()==null?"-":operationLogMessage.getBehavior());
            return operationLogMessage;
        } catch (Exception e) {
            log.error("buildOperationLogMessage构造结算风控冻结日志异常", e);
            return null;
        }
    }



    public void doSendLogToRiskByType(StandardMatchInfo standardMatchInfo,SettleQueryDTO settleQueryDTO,String forwText) {
        OperationLogMessage operationLogMessage = buildOperationLogMessageByType(standardMatchInfo,settleQueryDTO,forwText);
        String linkId = IdWorker.getId() + "_ByType";
        if (Objects.isNull(operationLogMessage)) {
            log.error("{}buildOperationLogMessageByType构造结算2.0冻结日志为空，不进行发送", linkId);
            return;
        }
        Request<OperationLogMessage> sendMessageRequest = new Request<>();
        sendMessageRequest.setData(operationLogMessage);
        sendMessageRequest.setLinkId(linkId);
        MessageBuilder<Request<OperationLogMessage>> builder =
                MessageBuilder.withPayload(sendMessageRequest)
                        .setHeader(MessageConst.PROPERTY_KEYS, sendMessageRequest.getLinkId());
        log.info("::{}::开始下发结算2.0冻结日志到风控:{},topic:OPERATION_LOG_TO_RISK", settleQueryDTO.getMatchId(), sendMessageRequest.getLinkId());
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

    private OperationLogMessage buildOperationLogMessageByType(StandardMatchInfo standardMatchInfo,SettleQueryDTO settleQueryDTO,String forwText) {
        OperationLogMessage operationLogMessage = new OperationLogMessage();
        try {
            if (forwText!=null){
                forwText = OperateLogTypeEnum.getEnumByZs(forwText);
            }


            //操作对象转换
            String operateName = "-";
            if (settleQueryDTO.getSportId().intValue() == 2) {
                //操作参数名称
                if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1 || settleQueryDTO.getExInfo() == 2)) {
                    List<String> basketBallSettleNums = MatchSettleCheckConstant.getBasketBallSettleNumEnumList(settleQueryDTO);
                    if (!basketBallSettleNums.isEmpty()) {
                        if (!settleQueryDTO.getSettleNum().equals("100") && !settleQueryDTO.getSettleNum().equals("200") && !settleQueryDTO.getSettleNum().equals("300") && !settleQueryDTO.getSettleNum().equals("400") && !settleQueryDTO.getSettleNum().equals("end")) {
                            operationLogMessage.setParameterName(basketBallSettleNums.get(0));
                        }
                        if (settleQueryDTO.getPlayCategoryNum() != null && settleQueryDTO.getPlayCategoryNum() == 1 && (standardMatchInfo.getMatchLength() == 17 || standardMatchInfo.getMatchLength() == 73)) {
                            operationLogMessage.setParameterName("-");
                        }
                        String periodName = MatchSettleCheckConstant.getPeriodBySettleNum(basketBallSettleNums.get(0), standardMatchInfo.getMatchLength());
                        if (!org.apache.commons.lang3.StringUtils.isAnyEmpty(periodName)) {
                            if (periodName.equals(BasketBallSettleNumEnum.BK_SN.getCode())) {
                                return null;
                            }
                            operateName = periodName;
                        }
                    }
                }
            } else {
                //操作参数名称
                if (settleQueryDTO.getPlayCategory() != null) {
                    switch (settleQueryDTO.getPlayCategory()) {
                        case 1:
                            operateName = "进球类";
                            break;
                        case 2:
                            operateName = "角球类";
                            break;
                        case 3:
                            operateName = "罚牌类";
                            break;
                    }
                }
                operationLogMessage.setObjectName(operateName);
            }
            String realText = "-";
            String operateType = "-";
            switch (settleQueryDTO.getExInfo()) {
                case 0: //取消冻结
                    operateType = OperateLogTypeEnum.type_2.getName();
                    realText = OperateLogTypeEnum.type_2.getName(); //操作后
                    break;
                case 1:
                    //玩法按分钟冻结
                    if (settleQueryDTO.getMins() != null && settleQueryDTO.getMins() != 0) {
                        forwText = "-";
                        realText = OperateLogTypeEnum.type_1.getName();
                        operateType = OperateLogTypeEnum.SCORES_SETTLE_10032.getName();
                    } else {
                        realText = OperateLogTypeEnum.type_1.getName();
                        operateType = OperateLogTypeEnum.SCORES_SETTLE_10032.getName();
                    }
                    break;
                case 2:  //玩法级程序重跑
                    realText = "-";
                    operateType = OperateLogTypeEnum.ROLLBACK_EXECUTE.getName();
                    break;
                default:
                    break;
            }
            operationLogMessage.setMatchId(settleQueryDTO.getMatchId());
            operationLogMessage.setBeforeVal(forwText);
            operationLogMessage.setAfterVal(realText);
            //操作对象id
            operationLogMessage.setParameterName("-");
            operationLogMessage.setBehavior("冻结");
            operationLogMessage.setExtObjectId("-");
            operationLogMessage.setObjectId(standardMatchInfo.getMatchManageId());
            operationLogMessage.setExtObjectName("-");
            operationLogMessage.setUserName(settleQueryDTO.getOperatorName());
            operationLogMessage.setUserId("-");
            operationLogMessage.setIp(settleQueryDTO.getIpAddress());
            operationLogMessage.setOperateTime(new Date());
            if (standardMatchInfo.getSportId().intValue()==1){
                operationLogMessage.setOperatePageCode(209);
            }
            if (standardMatchInfo.getSportId().intValue()==2){
                operationLogMessage.setOperatePageCode(210);
            }
            operationLogMessage.setSportId(standardMatchInfo.getSportId().intValue());

            //防止为空逻辑
            operationLogMessage.setBeforeVal(operationLogMessage.getBeforeVal()==null?"-":operationLogMessage.getBeforeVal());
            operationLogMessage.setAfterVal(operationLogMessage.getAfterVal()==null?"-":operationLogMessage.getAfterVal());
            operationLogMessage.setBehavior(operationLogMessage.getBehavior()==null?"-":operationLogMessage.getBehavior());

        } catch (Exception e) {
            log.error("buildOperationLogMessageByType,标准赛事ID:"+settleQueryDTO.getMatchId()+" , error:", e);

        }
        return operationLogMessage;
    }

    public void doSendLogToRiskByTypeBasketball(StandardMatchInfo standardMatchInfo,SettleQueryDTO settleQueryDTO,String forwText) {
        OperationLogMessage operationLogMessage = buildOperationLogMessageByTypeBasketball(standardMatchInfo,settleQueryDTO,forwText);
        String linkId = IdWorker.getId() + "_Basketball";
        if (Objects.isNull(operationLogMessage)) {
            log.error("{}buildOperationLogMessageByTypeBasketball构造结算2.0冻结日志为空，不进行发送", linkId);
            return;
        }
        Request<OperationLogMessage> sendMessageRequest = new Request<>();
        sendMessageRequest.setData(operationLogMessage);
        sendMessageRequest.setLinkId(linkId);
        MessageBuilder<Request<OperationLogMessage>> builder =
                MessageBuilder.withPayload(sendMessageRequest)
                        .setHeader(MessageConst.PROPERTY_KEYS, sendMessageRequest.getLinkId());
        log.info("::{}::开始下发结算2.0冻结日志到风控:{},topic:OPERATION_LOG_TO_RISK", settleQueryDTO.getMatchId(), sendMessageRequest.getLinkId());
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

    private OperationLogMessage buildOperationLogMessageByTypeBasketball(StandardMatchInfo standardMatchInfo,SettleQueryDTO settleQueryDTO,String forwText) {
        OperationLogMessage operationLogMessage = new OperationLogMessage();
        try {
            operationLogMessage.setMatchId(settleQueryDTO.getMatchId());


            //操作对象转换
            String operateName = "-";
            if (settleQueryDTO.getSportId().intValue() == 2) {


                //操作参数名称
                if (settleQueryDTO.getLevel().equals(2) && (settleQueryDTO.getExInfo() == 0 || settleQueryDTO.getExInfo() == 1 || settleQueryDTO.getExInfo() == 2)) {
                    List<String> basketBallSettleNums = MatchSettleCheckConstant.getBasketBallSettleNumEnumList(settleQueryDTO);
                    if (!basketBallSettleNums.isEmpty()) {
                        if (!settleQueryDTO.getSettleNum().equals("100") && !settleQueryDTO.getSettleNum().equals("200") && !settleQueryDTO.getSettleNum().equals("300") && !settleQueryDTO.getSettleNum().equals("400") && !settleQueryDTO.getSettleNum().equals("end")) {
                            operateName = basketBallSettleNums.get(0);
                            operationLogMessage.setParameterName(BasketBallSettleNumEnum.getEnum(operateName).getValue());
                        }
                        if (settleQueryDTO.getPlayCategoryNum() != null && settleQueryDTO.getPlayCategoryNum() == 1 && (standardMatchInfo.getMatchLength() == 17 || standardMatchInfo.getMatchLength() == 73)) {
                            operationLogMessage.setParameterName("-");
                        }
                        if (operateName.equals("-")){
                            String periodName = MatchSettleCheckConstant.getPeriodBySettleNum(basketBallSettleNums.get(0), standardMatchInfo.getMatchLength());
                            if (!org.apache.commons.lang3.StringUtils.isAnyEmpty(periodName)) {
                                if (periodName.equals(BasketBallSettleNumEnum.BK_SN.getCode())) {
                                    return null;
                                }
                                operateName = BasketBallSettleNumEnum.getEnum(periodName).getValue();
                                operationLogMessage.setParameterName(operateName);
                            }
                        }
                    }
                }
            } else {
                //操作参数名称
                if (settleQueryDTO.getPlayCategory() != null) {
                    switch (settleQueryDTO.getPlayCategory()) {
                        case 1:
                            operateName = "进球类";
                            break;
                        case 2:
                            operateName = "角球类";
                            break;
                        case 3:
                            operateName = "罚牌类";
                            break;
                    }
                }
            }
            String realText = "-";
            String operateType = "-";
            switch (settleQueryDTO.getExInfo()) {
                case 0: //取消冻结
                    forwText = OperateLogTypeEnum.type_1.getName();
                    operateType = OperateLogTypeEnum.type_2.getName();
                    realText = OperateLogTypeEnum.type_2.getName(); //操作后
                    break;
                case 1:
                    //玩法按分钟冻结
                    if (settleQueryDTO.getMins() != null && settleQueryDTO.getMins() != 0) {
                        forwText = "-";
                        realText = OperateLogTypeEnum.type_1.getName();
                        operateType = OperateLogTypeEnum.SCORES_SETTLE_10037.getName();
                    } else {
                        forwText = "-";
                        realText = OperateLogTypeEnum.type_1.getName().toString();
                        operateType = OperateLogTypeEnum.SCORES_SETTLE_10032.getName();
                    }
                    break;
                case 2:  //玩法级程序重跑
                    realText = "-";
                    operateType = OperateLogTypeEnum.ROLLBACK_EXECUTE.getName();
                    break;
                default:
                    break;
            }

            operationLogMessage.setBeforeVal(forwText);
            operationLogMessage.setAfterVal(realText);
            //操作对象id
            operationLogMessage.setObjectName("篮球比分");
            operationLogMessage.setBehavior("冻结");
            operationLogMessage.setObjectId(standardMatchInfo.getMatchManageId());
            operationLogMessage.setExtObjectId("-");
            operationLogMessage.setExtObjectName("-");
            operationLogMessage.setUserName(settleQueryDTO.getOperatorName());
            operationLogMessage.setUserId("-");
            operationLogMessage.setIp(settleQueryDTO.getIpAddress());
            operationLogMessage.setOperateTime(new Date());
            if (standardMatchInfo.getSportId().intValue()==1){
                operationLogMessage.setOperatePageCode(209);
            }
            if (standardMatchInfo.getSportId().intValue()==2){
                operationLogMessage.setOperatePageCode(210);
            }

            operationLogMessage.setSportId(standardMatchInfo.getSportId().intValue());
            //防止为空逻辑
            operationLogMessage.setBeforeVal(operationLogMessage.getBeforeVal()==null?"-":operationLogMessage.getBeforeVal());
            operationLogMessage.setAfterVal(operationLogMessage.getAfterVal()==null?"-":operationLogMessage.getAfterVal());
            operationLogMessage.setBehavior(operationLogMessage.getBehavior()==null?"-":operationLogMessage.getBehavior());

        } catch (Exception e) {
            log.error("buildOperationLogMessageByTypeBasketball,标准赛事ID:"+settleQueryDTO.getMatchId()+" , error:", e);

        }
        return operationLogMessage;
    }
    public void operationLogScoresPeriodFreeze(StandardMatchInfo standardMatchInfo,MatchSettleScore matchSettleScore, String forwText, ScoresPeriodFreezeDto scoresPeriodFreezeDto) {
        OperationLogMessage operationLogMessage = buildOperationLogMessageScoresPeriodFreeze(standardMatchInfo,matchSettleScore,forwText,scoresPeriodFreezeDto);
        String linkId = IdWorker.getId() + "_Scores";
        if (Objects.isNull(operationLogMessage)) {
            log.error("{}operationLogScoresPeriodFreeze构造结算2.0冻结日志为空，不进行发送", linkId);
            return;
        }
        Request<OperationLogMessage> sendMessageRequest = new Request<>();
        sendMessageRequest.setData(operationLogMessage);
        sendMessageRequest.setLinkId(linkId);
        MessageBuilder<Request<OperationLogMessage>> builder =
                MessageBuilder.withPayload(sendMessageRequest)
                        .setHeader(MessageConst.PROPERTY_KEYS, sendMessageRequest.getLinkId());
        log.info("::{}::开始下发结算2.0冻结日志到风控:{},topic:OPERATION_LOG_TO_RISK", scoresPeriodFreezeDto.getMatchId(), sendMessageRequest.getLinkId());
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

    private OperationLogMessage buildOperationLogMessageScoresPeriodFreeze(StandardMatchInfo standardMatchInfo,MatchSettleScore matchSettleScore, String forwText, ScoresPeriodFreezeDto scoresPeriodFreezeDto) {

        String ipAddress = scoresPeriodFreezeDto.getIpAddress();
        OperationLogMessage operationLogMessage = new OperationLogMessage();
        try {
            String eventCode = matchSettleScore.getEventCode();
            operationLogMessage.setMatchId(scoresPeriodFreezeDto.getMatchId());
            operationLogMessage.setExtObjectName("-");
            operationLogMessage.setIp(ipAddress);
            operationLogMessage.setBehavior("冻结");
            if (scoresPeriodFreezeDto.getFreezeStatus() == 1){
                operationLogMessage.setBeforeVal("-");
                operationLogMessage.setAfterVal(OperateLogTypeEnum.type_1.getName());
            }
            if (scoresPeriodFreezeDto.getFreezeStatus() == 0){
                operationLogMessage.setBeforeVal(OperateLogTypeEnum.type_1.getName());
                operationLogMessage.setAfterVal(OperateLogTypeEnum.type_2.getName());
            }
//            if (scoresPeriodFreezeDto.getMins() != null && scoresPeriodFreezeDto.getMins() > 0) {
//                String prar = OperateLogTypeEnum.SCORES_SETTLE_10037.getName().replace("s/", scoresPeriodFreezeDto.getMins().toString());
//
//                operationLogMessage.setBeforeVal("-");
//                operationLogMessage.setAfterVal(OperateLogTypeEnum.type_1.getName());
//
//            } else if (scoresPeriodFreezeDto.getFreezeTime() != null && scoresPeriodFreezeDto.getFreezeTime() != 0) {
//                SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
//                operationLogMessage.setBehavior(OperateLogTypeEnum.SCORES_PD_100139.getName());
//                operationLogMessage.setBeforeVal(sdf.format(TimeUtils.millsSecondsEast8ZoneGmt()));
//                operationLogMessage.setAfterVal(sdf.format(scoresPeriodFreezeDto.getFreezeTime()));
//
//            } else {
//                if (!org.apache.commons.lang3.StringUtils.isAnyEmpty(scoresPeriodFreezeDto.getSettleNum()) && scoresPeriodFreezeDto.getFreezeStatus() == 1) {
//                    operationLogMessage.setBeforeVal("-");
//                    operationLogMessage.setBehavior(OperateLogTypeEnum.type_1.getName());
//                } else {
//                    operationLogMessage.setBeforeVal(scoresPeriodFreezeDto.getFreezeStatus() == 0 ? OperateLogTypeEnum.type_1.getName(): OperateLogTypeEnum.type_2.getName());
//                    operationLogMessage.setBehavior(scoresPeriodFreezeDto.getFreezeStatus() == 0 ? OperateLogTypeEnum.type_2.getName() : OperateLogTypeEnum.type_5.getName());
//                }
//            }

            operationLogMessage.setExtObjectId("-");
            operationLogMessage.setUserName(scoresPeriodFreezeDto.getOperatorName());
            operationLogMessage.setUserId("-");
            operationLogMessage.setOperateTime(new Date());
            if (standardMatchInfo.getSportId().intValue()==1){
                operationLogMessage.setOperatePageCode(209);
            }
            if (standardMatchInfo.getSportId().intValue()==2){
                operationLogMessage.setOperatePageCode(210);
            }
            operationLogMessage.setSportId(standardMatchInfo.getSportId().intValue());

            //操作对象id
            operationLogMessage.setObjectId(standardMatchInfo.getMatchManageId());
            //操作参数名称
            if (eventCode!= null) {
                switch (eventCode) {
                    case "goal":
                        operationLogMessage.setObjectName("进球类");
                        break;
                    case "corner":
                        operationLogMessage.setObjectName("角球类");
                        break;
                    case "fa_card":
                        operationLogMessage.setObjectName("罚牌类");
                        break;
                    case "score_change":
                        operationLogMessage.setObjectName("-");
                        break;
                }
            }
//            operationLogMessage.setObjectName("-");
            //开球进入
            if (org.apache.commons.lang3.StringUtils.isEmpty(eventCode) && "kick_off".equals(matchSettleScore.getEventCode())){
                operationLogMessage.setObjectName("进球类");
            }
            //操作参数名称
            String settleNum = matchSettleScore.getSettleNum();
            if (standardMatchInfo.getSportId().intValue() == 2) {
                //篮球结算事件
                if (BasketBallSettleNumEnum.getEnum(settleNum) != null) {
                    operationLogMessage.setParameterName(BasketBallSettleNumEnum.getEnum(settleNum).getValue());
                }
            } else {
                if (MatchPeriodEnum.getEnum(settleNum) != null) {
                    operationLogMessage.setParameterName(MatchPeriodEnum.getEnum(settleNum).getName());
                }
            }
            //防止为空逻辑
            operationLogMessage.setBeforeVal(operationLogMessage.getBeforeVal()==null?"-":operationLogMessage.getBeforeVal());
            operationLogMessage.setAfterVal(operationLogMessage.getAfterVal()==null?"-":operationLogMessage.getAfterVal());
            operationLogMessage.setBehavior(operationLogMessage.getBehavior()==null?"-":operationLogMessage.getBehavior());
        } catch (Exception e) {
            log.error("buildOperationLogMessageScoresPeriodFreeze,标准赛事ID:"+scoresPeriodFreezeDto.getMatchId()+" , error:", e);
        }
        return operationLogMessage;
    }


    public void operationLogScoresPeriodOrderFreeze(StandardMatchInfo standardMatchInfo, MatchSettleEvent matchSettleEvent, String forwText, ScoresPeriodOrderFreezeDto freezeDto) {
        OperationLogMessage operationLogMessage = buildOperationLogScoresPeriodOrderFreeze(standardMatchInfo,matchSettleEvent,forwText,freezeDto);
        String linkId = IdWorker.getId() + "_ScoresOrder";
        if (Objects.isNull(operationLogMessage)) {
            log.error("{}buildOperationLogScoresPeriodOrderFreeze构造结算2.0冻结日志为空，不进行发送", linkId);
            return;
        }
        Request<OperationLogMessage> sendMessageRequest = new Request<>();
        sendMessageRequest.setData(operationLogMessage);
        sendMessageRequest.setLinkId(linkId);
        MessageBuilder<Request<OperationLogMessage>> builder =
                MessageBuilder.withPayload(sendMessageRequest)
                        .setHeader(MessageConst.PROPERTY_KEYS, sendMessageRequest.getLinkId());
        log.info("::{}::开始下发结算2.0冻结日志到风控:{},topic:OPERATION_LOG_TO_RISK", freezeDto.getMatchId(), sendMessageRequest.getLinkId());
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

    private OperationLogMessage buildOperationLogScoresPeriodOrderFreeze(StandardMatchInfo standardMatchInfo, MatchSettleEvent matchSettleEvent, String forwText, ScoresPeriodOrderFreezeDto freezeDto) {
        String operatorName = freezeDto.getOperatorName();
        String ipAddress = freezeDto.getIpAddress();
        OperationLogMessage operationLogMessage = new OperationLogMessage();
        String eventCode = matchSettleEvent.getEventCode();
        try {
            if (standardMatchInfo.getSportId().intValue()==1){
                operationLogMessage.setOperatePageCode(209);
            }
            if (standardMatchInfo.getSportId().intValue()==2){
                operationLogMessage.setOperatePageCode(210);
            }
            operationLogMessage.setMatchId(freezeDto.getMatchId());
            operationLogMessage.setObjectId(standardMatchInfo.getMatchManageId());

            operationLogMessage.setExtObjectName("-");
            operationLogMessage.setBehavior("冻结");
            if (freezeDto.getFreezeStatus() == 1){

                operationLogMessage.setBeforeVal("-");
                operationLogMessage.setAfterVal(OperateLogTypeEnum.type_1.getName());
            }
            if (freezeDto.getFreezeStatus() == 0){
                operationLogMessage.setBeforeVal(OperateLogTypeEnum.type_1.getName());
                operationLogMessage.setAfterVal(OperateLogTypeEnum.type_2.getName());
            }else {
                operationLogMessage.setBehavior("冻结");

            }
            if (eventCode!= null) {
                switch (eventCode) {
                    case "goal":
                        operationLogMessage.setObjectName("进球类");
                        break;
                    case "corner":
                        operationLogMessage.setObjectName("角球类");
                        break;
                    case "fa_card":
                        operationLogMessage.setObjectName("罚牌类");
                        break;
                }
            }

//
//
//            operationLogMessage.setBeforeVal(forwText);
//            operationLogMessage.setAfterVal(matchSettleEvent.getSettleFreeze() == 0 ? OperateLogTypeEnum.type_2.getName() : OperateLogTypeEnum.type_1.getName());
            operationLogMessage.setOperateTime(new Date());
            operationLogMessage.setIp(ipAddress);

            operationLogMessage.setExtObjectId("-");

            operationLogMessage.setUserName(operatorName);
            operationLogMessage.setUserId("-");
//
//            //全部冻结
//            if (freezeDto.getMins() != null && freezeDto.getMins() > 0) {
//                String prar = OperateLogTypeEnum.SCORES_SETTLE_10037.getName().replace("s/", freezeDto.getMins().toString());
//                operationLogMessage.setBehavior(prar);
//                operationLogMessage.setBeforeVal("-");
//                operationLogMessage.setAfterVal(OperateLogTypeEnum.type_1.getName());
//            } else if (freezeDto.getFreezeTime() != null && freezeDto.getFreezeTime() != 0) {
//                SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
//                operationLogMessage.setBehavior(OperateLogTypeEnum.SCORES_PD_100139.getName());
//                operationLogMessage.setBeforeVal(sdf.format(TimeUtils.millsSecondsEast8ZoneGmt()));
//                operationLogMessage.setAfterVal(sdf.format(freezeDto.getFreezeTime()));
//            } else {
//                if (freezeDto.getFreezeStatus() == 1){
//                    operationLogMessage.setBehavior(OperateLogTypeEnum.type_1.getName());
//                    operationLogMessage.setBeforeVal("-");
//                    operationLogMessage.setAfterVal(freezeDto.getFreezeStatus() == 0 ? OperateLogTypeEnum.type_2.getName() : OperateLogTypeEnum.type_1.getName());
//                }
//                if (freezeDto.getFreezeStatus() == 0){
//                    operationLogMessage.setBehavior(OperateLogTypeEnum.type_2.getName());
//                    operationLogMessage.setBeforeVal(OperateLogTypeEnum.type_1.getName());
//                    operationLogMessage.setAfterVal(freezeDto.getFreezeStatus() == 0 ? OperateLogTypeEnum.type_2.getName() : OperateLogTypeEnum.type_1.getName());
//                }else {
//                    operationLogMessage.setBehavior(freezeDto.getFreezeStatus() == 1 ?
//                            OperateLogTypeEnum.type_1.getName() : OperateLogTypeEnum.type_5.getName());
//
//                }
//
//            }

            Integer eventOrder = matchSettleEvent.getEventOrder();
            //操作参数名称
            String settleNum = matchSettleEvent.getSettleNum();
            if (freezeDto.getSportId() == 2) {
                //篮球结算事件
                if (BasketBallSettleNumEnum.getEnum(settleNum) != null) {
                    operationLogMessage.setParameterName(BasketBallSettleNumEnum.getEnum(settleNum).getValue());
                }
            } else {
                if (MatchPeriodEnum.getEnum(settleNum) != null) {
                    if (eventOrder!=null){
                        operationLogMessage.setParameterName(MatchPeriodEnum.getEnum(settleNum).getName()+"-"+eventOrder);
                    }else {
                        operationLogMessage.setParameterName(MatchPeriodEnum.getEnum(settleNum).getName());
                    }
                }
            }

            //防止为空逻辑
            operationLogMessage.setBeforeVal(operationLogMessage.getBeforeVal()==null?"-":operationLogMessage.getBeforeVal());
            operationLogMessage.setAfterVal(operationLogMessage.getAfterVal()==null?"-":operationLogMessage.getAfterVal());
            operationLogMessage.setBehavior(operationLogMessage.getBehavior()==null?"-":operationLogMessage.getBehavior());
        } catch (Exception e) {
            log.error("buildOperationLogScoresPeriodOrderFreeze :标准赛事ID:"+matchSettleEvent.getStandardMatchId()+", error: 比分阶段冻结", e);

        }
        return operationLogMessage;
    }



}
