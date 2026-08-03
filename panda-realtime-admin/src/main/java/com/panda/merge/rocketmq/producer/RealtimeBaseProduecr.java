package com.panda.merge.rocketmq.producer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.spare.SpareBaseProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 发送消息到MQ
 *
 * @author tell
 * @since 2025年02月09日10:48:37
 **/
@Slf4j
@Component
public class RealtimeBaseProduecr<T> {

    /** 主MQ生产者*/
    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /** 备MQ生产者*/
    @Autowired
    private SpareBaseProducer spareBaseProducer;

    /**
     * panda需要直接往备用投递的MQ： DATA_MATCHS_EVENT_INFO_DB
     * */
    @NacosValue(value = "${panda.spare.topic:12}", autoRefreshed = true)
    private String pandaSpareTopic;

    /**
     * 主MQ同步推送（一般使用同步推送）
     */
    public void send(T data,String linkId,String topic,String tag,String dataSourceCode) {
        log.info("linkId=【"+linkId+"】【"+topic+"】数据同步推送MQ开始,tag={}",tag);
        Request<T> request = new Request<>(data,linkId,topic,tag,dataSourceCode);
        try {
            if(pandaSpareTopic.contains(topic)){
                spareBaseProducer.syncSend(request);
            }else{
                MessageBuilder<Request<T>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
                rocketMqTemplate.send(request.getDataType() + ":" + request.getTag(), builder.build());
            }
        } catch (Exception e) {
            log.info("linkId=【"+linkId+"】【"+topic+"】数据同步推送MQ异常,Exception:", e);
        }
        log.info("linkId=【"+linkId+"】【"+topic+"】数据同步推送MQ结束");
    }


    /**
     * 主MQ异步推送（含延迟下发）
     * @param delayTimeLevel 设置延迟级别（1:1s,2:5s,3:10s）
     */
    public void syncSend(T data,String linkId,String topic,String tag,String dataSourceCode,int delayTimeLevel) {
        log.info("linkId=【"+linkId+"】【"+topic+"】数据异步推送MQ开始,tag={},delayTimeLevel={}",tag,delayTimeLevel);
        Request<T> request = new Request<>(data,linkId,topic,tag,dataSourceCode);
        try {
            MessageBuilder<Request<T>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.syncSend(request.getDataType() + ":" + request.getTag(), builder.build(), SECOND_1 * THREE,delayTimeLevel);
        } catch (Exception e) {
            log.info("linkId=【"+linkId+"】【"+topic+"】数据异步推送MQ异常,Exception:", e);
        }
        log.info("linkId=【"+linkId+"】【"+topic+"】数据异步推送MQ结束");
    }

    /** 主备MQ涉及topic*/
    @NacosValue(value = "${panda.admin.or.spare.topic:MATCH_EVENT_INFO_TO_RISK,MATCH_EVENT_INFO,MATCH_EVENT_INFO_SK,STANDARD_MATCH_STATISTICS,STANDARD_MATCH_STATISTICS_NO_LIVE}", autoRefreshed = true)
    private String pandaAdminOrSpareTopic;

    /**
     * 发送到主或者备MQ数据
     * @param delayTimeLevel 设置延迟级别（1:1s,2:5s,3:10s）
     * */
    public void sendAdminOrSpare(Request<List<T>> request,ThirdMatchInfo thirdMatchInfo,int delayTimeLevel){
        try {
            //判断当前topic是否需要区分主备MQ
            List<String> topicList = Arrays.asList(pandaAdminOrSpareTopic.split(","));
            boolean topicFlag = topicList.contains(request.getDataType());
            boolean spareMqFlag = getSpareMqFlag(thirdMatchInfo.getReferenceId() + "");
            log.info("linkId=【"+request.getLinkId()+"】【"+request.getDataType()+"】发送到主或者备MQ数据开始,tag={},delayTimeLevel={},topicFlag={},spareMqFlag={}",request.getTag(),delayTimeLevel,topicFlag,spareMqFlag);
            if(spareMqFlag && topicFlag){
                spareBaseProducer.syncSend(request,delayTimeLevel);
            }else{
                MessageBuilder<Request<List<T>>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
                if(delayTimeLevel > 0){
                    //异步延迟推送
                    rocketMqTemplate.syncSend(request.getDataType() + ":" + request.getTag(), builder.build(), SECOND_1 * THREE,delayTimeLevel);
                }else{
                    //同步推送
                    rocketMqTemplate.send(request.getDataType() + ":" + request.getTag(), builder.build());
                }
            }
        } catch (Exception e) {
            log.info("linkId=【"+request.getLinkId()+"】【"+request.getDataType()+"】发送到主或者备MQ数据异常,Exception:", e);
        }
        log.info("linkId=【"+request.getLinkId()+"】【"+request.getDataType()+"】发送到主或者备MQ数据结束");
    }


    /** mq主备配置 1:主 2:备*/
    @NacosValue(value = "${panda.data.mq.gateway.event:1}", autoRefreshed = true)
    private int pandaDataMqGatewayevent = 1;
    /** 下发到备用mq集群的赛事id,(逗号隔开)*/
    @NacosValue(value = "${panda.data.mq.gateway.matchId:1,}", autoRefreshed = true)
    private String pandaDataMqGatewayMatchId;

    /**
     * 判断是否需要发送消息到备用MQ
     * @param standardMatchId 标准比赛ID
     * @return true表示需要发送到备用MQ
     * 1: pandaDataMqGatewayevent !=2 || 标准赛事ID为空，无需切换
     * 2: pandaDataMqGatewayevent = 2
     *    pandaDataMqGatewayMatchId 为空表示全部切换，
     *    pandaDataMqGatewayMatchId 不为空并且包含 standardMatchId 则切换
     *    pandaDataMqGatewayMatchId 不为空并且不包含 standardMatchId 则不切换
     */
    public boolean getSpareMqFlag(String standardMatchId) {
        // 快速失败：不满足基本条件直接返回
        if (pandaDataMqGatewayevent != 2 || StringUtils.isBlank(standardMatchId)) {
            return false;
        }

        // 处理备用MQ配置
        if (StringUtils.isBlank(pandaDataMqGatewayMatchId)) {
            return true;
        }

        // 转换为set集合
        Set<String> spareMatchIds = new HashSet<>(
                Arrays.asList(pandaDataMqGatewayMatchId.split(","))
        );
        return spareMatchIds.contains(standardMatchId);
    }


}
