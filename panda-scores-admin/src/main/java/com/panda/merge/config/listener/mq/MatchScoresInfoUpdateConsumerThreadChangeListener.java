package com.panda.merge.config.listener.mq;

import com.panda.merge.config.listener.base.NacosChangeEvent;
import com.panda.merge.config.listener.base.NacosListener;
import com.panda.merge.mq.consumer.MatchScoresInfoConsumer;
import com.panda.merge.mq.consumer.StandMatchScoresUpdateConsumer;
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
public class MatchScoresInfoUpdateConsumerThreadChangeListener implements NacosListener {

    @Autowired
    private MatchScoresInfoConsumer matchScoresInfoConsumer;

    @Override
    public String nacosKeyToListen() {
        return StandMatchScoresUpdateConsumer.NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY;
    }

    @Override
    public void onChange(NacosChangeEvent nacosChangeEvent) {
        DefaultMQPushConsumer consumer = matchScoresInfoConsumer.getConsumer();
        consumer.updateCorePoolSize(Integer.parseInt(nacosChangeEvent.getValueAfter()));
        log.info("调整MatchScoresInfoConsumer核心线程数成功,before:{},after:{}"
                , nacosChangeEvent.getValueBefore(), nacosChangeEvent.getValueAfter());
    }
}
