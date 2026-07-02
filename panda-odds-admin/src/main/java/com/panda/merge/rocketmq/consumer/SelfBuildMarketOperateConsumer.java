package com.panda.merge.rocketmq.consumer;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.StandardOutrightMatchDTO;
import com.panda.merge.rocketmq.processor.SelfBuildMarketOperateProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.BUILD_OUTRIGHT_MARKET;

/**
 * 自建盘口的创建或编辑
 * @author    Kepa
 * @since    2021/7/14 10:10
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = BUILD_OUTRIGHT_MARKET,
        consumerGroup = "odds-group-"+BUILD_OUTRIGHT_MARKET,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("oddsAdminApplication")
public class SelfBuildMarketOperateConsumer implements RocketMQListener<Request<StandardOutrightMatchDTO>> {

    @Autowired
    private SelfBuildMarketOperateProcessor selfBuildMarketOperateProcessor;

    @Override
    public void onMessage(Request<StandardOutrightMatchDTO> message) {
        selfBuildMarketOperateProcessor.processBuildOutRightMarket(message);
    }
}
