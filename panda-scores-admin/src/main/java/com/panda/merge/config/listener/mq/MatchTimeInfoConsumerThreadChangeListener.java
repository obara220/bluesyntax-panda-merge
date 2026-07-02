package com.panda.merge.config.listener.mq;

import com.panda.merge.config.listener.base.NacosChangeEvent;
import com.panda.merge.config.listener.base.NacosListener;
import com.panda.merge.mq.consumer.MatchTimeInfoConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Hunta
 * @since 9/13/2023
 */


@Component
@Slf4j
public class MatchTimeInfoConsumerThreadChangeListener implements NacosListener {

    @Autowired
    private MatchTimeInfoConsumer matchTimeInfoConsumer;

    @Override
    public String nacosKeyToListen() {
        return MatchTimeInfoConsumer.NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY;
    }

    @Override
    public void onChange(NacosChangeEvent nacosChangeEvent) {
//        DefaultMQPushConsumer consumer = matchTimeInfoConsumer.getConsumer();
//        consumer.updateCorePoolSize(Integer.parseInt(nacosChangeEvent.getValueAfter()));
        log.info("调整MatchTimeInfoConsumer核心线程数成功,before:{},after:{}"
                , nacosChangeEvent.getValueBefore(), nacosChangeEvent.getValueAfter());
    }
}
