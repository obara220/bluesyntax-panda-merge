package com.panda.merge.rocketmq.consumer;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.THIRD_MATCH_MARKET_API;

/**
 * 消费数据源盘口赔率信息
 * @author   Aison
 * @since    2020年11月18日16:50:51
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = THIRD_MATCH_MARKET_API,
        consumerGroup = "odds-group-test-"+THIRD_MATCH_MARKET_API,
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("testAdminApplication")
public class ThirdMatchMarketConsumer implements RocketMQListener<Request<ThirdMatchMarketDTO>>  {

    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Override
    public void onMessage(Request<ThirdMatchMarketDTO> request) {
        //thirdMatchMarketProcessor.accessMatchMarketData(request);
    }
}
