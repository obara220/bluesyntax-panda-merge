package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.panda.merge.dto.ReplayMatchDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.rocketmq.processor.ReplayMatchProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.REPLAY_MATCH_SEND_BEGIN;

/**
 * 重播事件,赛事状态(开赛)（需求：3890 【mini2】新增模拟投注环境）
 * @author :  darwinxi
 * @since 2025年3月22日
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = REPLAY_MATCH_SEND_BEGIN,
        consumerGroup = CONSUME_REALTIME_GROUP + REPLAY_MATCH_SEND_BEGIN,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
public class ReplayMatchConsumer implements RocketMQListener<String> {

    @Resource
    private ReplayMatchProcessor replayMatchProcessor;

    @Override
    public void onMessage(String body) {
        Request<ReplayMatchDTO> request = JSON.parseObject(body, new TypeReference<Request<ReplayMatchDTO>>() {});
        replayMatchProcessor.processReplayMatch(request);
    }
}
