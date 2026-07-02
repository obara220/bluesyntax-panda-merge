package com.panda.merge.rocketmq.consumer;

import com.panda.merge.common.RateLimiterHandler;
import com.panda.merge.dto.MatchStatisticsInfoDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.processor.MatchStatisticsInfoProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.MATCH_STATISTICS_INFO_API;

/**
 * 第三方赛事统计信息接入
 * @author   Aison
 * @since    2020年10月22日11:01:53
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MATCH_STATISTICS_INFO_API,
        consumerGroup = CONSUME_REALTIME_GROUP + MATCH_STATISTICS_INFO_API,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
public class MatchStatisticsInfoConsumer implements RocketMQListener<Request<MatchStatisticsInfoDTO>> {

    @Autowired
    MatchStatisticsInfoProcessor matchStatisticsInfoProcessor;

    @Resource
    private RateLimiterHandler rateLimiterHandler;
    @Override
    public void onMessage(Request<MatchStatisticsInfoDTO> request) {
        // 3929 【融合】数据商异常下发告警&数据下发限频
        if (!rateLimiterHandler.filter(request.getData().getThirdMatchSourceId(),request.getData().getDataSourceCode())) {
            log.info("【{}】onMessage，该三方赛事统计被限流，数据不下发！源赛事ID={}", request.getLinkId(),request.getData().getThirdMatchSourceId());
            return ;
        }
        matchStatisticsInfoProcessor.putMatchStatisticsInfo(request);
    }
}
