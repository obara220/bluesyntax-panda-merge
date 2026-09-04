package com.panda.merge.rocketmq.consumer;

import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import com.panda.merge.odds.model.StandardMatchCategoryRemovedDto;
import com.panda.merge.rocketmq.processor.StandardMatchCategoryRemovedProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;


/**
 * 4184【操盘】联赛模板支持批量编辑/同步赛事/中途下架
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = ConstantSystem.STANDARD_MATCH_CATEGORY_REMOVED,
        consumerGroup = "odds-group-" + ConstantSystem.STANDARD_MATCH_CATEGORY_REMOVED,
        consumeThreadMax = 50)
@DependsOn("oddsAdminApplication")
public class StandardMatchCategoryRemovedConsumer implements RocketMQListener<Request<StandardMatchCategoryRemovedDto>> {

    @Autowired
    private StandardMatchCategoryRemovedProcessor standardMatchCategoryRemovedProcessor;

    @Override
    public void onMessage(Request<StandardMatchCategoryRemovedDto> request) {
        standardMatchCategoryRemovedProcessor.processor(request);
    }
}