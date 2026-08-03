package com.panda.merge.rocketmq.consumer;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.MatchEventInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.EnableSecondRocketMQCluster;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.panda.merge.constant.ConstantSystem.CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO_API;
import static com.panda.merge.constant.ConstantSystem.DATACENTER;

/**
 * PD 赛事阶段回退 55493
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO_API,
        consumerGroup = "odds-group-" + CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO_API,
        consumeThreadMax = 20,
        consumeTimeout = 10000L)
@DependsOn("oddsAdminApplication")
@EnableSecondRocketMQCluster
public class MatchPeriodBackConsumer implements RocketMQListener<Request<MatchEventInfoDTO>> {

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    RedisService redisService;
    /**
     * 数据中心赔率状态开关 1开 0关
     */
    @NacosValue(value = "${datacenter.odds.status:1}", autoRefreshed = true)
    private Integer datacenterOddsStatus;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void onMessage(Request<MatchEventInfoDTO> request) {
        //数据中心赔率状态开关 1开 0关
        if (datacenterOddsStatus == 1) {
            // 转发消息到数据中心
            log.info("收到 ::{}:: Topic的消息：{}", CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO_API, request.getData());
            String toTopic = CHANGE_PERIOD_THIRD_MATCH_EVENT_INFO_API + DATACENTER;
            String destination = !StringUtils.isEmpty(request.getTag()) ? toTopic + ":" + request.getTag() : toTopic;
            // 发送到 数据中心Topic
            MessageBuilder<Request<MatchEventInfoDTO>> builder = MessageBuilder.withPayload(request)
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId());
            rocketMqTemplate.send(destination, builder.build());
            log.info("::{}::消息已转发到数据中心 Topic:{},request:{}", request.getLinkId(), toTopic, JSON.toJSONString(request));
            return;
        }
        String linkId = request.getLinkId();
        MatchEventInfoDTO matchEventInfoDTO = request.getData();
        String dataSourceCode = matchEventInfoDTO.getDataSourceCode();
        String thirdMatchSourceId = matchEventInfoDTO.getThirdMatchSourceId();
        //查询三方赛事，找到标准赛事id
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSourceCode, thirdMatchSourceId);
        if (null == thirdMatchInfo || 0 == thirdMatchInfo.getReferenceId() || null == thirdMatchInfo.getReferenceId()) {
            log.info("::{}::MatchPeriodBackConsumer,PD三方赛事或标准赛事不存在!", linkId);
            return;
        }
        //删除自动关盘缓存
        String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_AUTO_CLOSE + thirdMatchInfo.getReferenceId());
        redisService.del(redisKey);
        log.info("::{}::MatchPeriodBackConsumer,删除自动关盘缓存成功，标准赛事ID：{}", linkId, thirdMatchInfo.getReferenceId());
    }
}
