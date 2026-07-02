package com.panda.merge.rocketmq.consumer;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.MatchMarketCategoryConfigurationMessage;
import com.panda.merge.rocketmq.processor.MatchCategoryConfigruationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;

/**
 * @Description :   开盘数据服务商及需要开盘玩法的配置
 * @author :  Riben
 * @since :  2020年12月9日13:43:05
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "Tournament_Template_Play",
        consumerGroup = CONSUME_REALTIME_GROUP + "TOURNAMENT_TEMPLATE_PLAY",
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
public class MatchMarketCategoryConfigurationConsumer implements RocketMQListener<Request<MatchMarketCategoryConfigurationMessage>> {

    @Autowired
    MatchCategoryConfigruationProcessor processor;


    @Override
    public void onMessage(Request<MatchMarketCategoryConfigurationMessage> matchMarketCategoryConfigurationMessageRequest) {
        MatchMarketCategoryConfigurationMessage requestData = matchMarketCategoryConfigurationMessageRequest.getData();
        if (null == requestData) {
            log.info("::{}::MatchMarketCategoryConfigurationConsumer ,传入参数信息不能为空！", matchMarketCategoryConfigurationMessageRequest.getLinkId());
            return;
        }
        Integer isDubboSell = null == requestData.getIsDubboSell() ? Constant.INTEGER_FLAG_ZERO : requestData.getIsDubboSell();
        if (isDubboSell.equals(Constant.INTEGER_FLAG_ONE)) {
            log.info("::{}::MatchMarketCategoryConfigurationConsumer ,标识为isDubboSell调用:{}", matchMarketCategoryConfigurationMessageRequest.getLinkId(), isDubboSell);
            return;
        }
        processor.handleCategoryConfigrations(matchMarketCategoryConfigurationMessageRequest);
    }
}
