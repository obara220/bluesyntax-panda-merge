package com.panda.merge.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.CommonConstant;
import com.panda.merge.dto.ModifyMatchBusinessMessageDto;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.CONSUME_SETTLE_SCORE_GROUP;

/**
 * modify_match_business 赛事编辑管理消息消费者
 * <p>
 * 当联赛/赛事编辑管理下发消息时，从消息中的 thirdMatchInfoList 提取数据源编码，
 * 并合并 DB 中该标准赛事下的三方赛事数据源，去重后写入 Redis 缓存。
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "modify_match_business",
        consumerGroup = CONSUME_SETTLE_SCORE_GROUP + "modify_match_business",
        consumeTimeout = 10000L
)
@DependsOn("settleScoresAdminApplication")
public class MatchOperateMsgConsumer implements RocketMQListener<ModifyMatchBusinessMessageDto> {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Override
    public void onMessage(ModifyMatchBusinessMessageDto message) {
        if (message == null) {
            log.warn("MatchOperateMsgConsumer 收到空消息，忽略处理");
            return;
        }

        String linkId = message.getLinkId() != null ? message.getLinkId() : "modify_match_business";
        log.info("linkId::{}::MatchOperateMsgConsumer 收到 modify_match_business 消息, dataSize:{}",
                linkId, message.getData() != null ? message.getData().size() : 0);

        if (CollectionUtils.isEmpty(message.getData())) {
            log.warn("linkId::{}::data 为空，无法处理, message:{}", linkId, JSON.toJSONString(message));
            return;
        }

        // 一般 list 只有一条，取第一条
        ModifyMatchBusinessMessageDto.ModifyMatchBusinessDataItem first = message.getData().get(0);
        Long standardMatchId = first.getId();
        if (standardMatchId == null) {
            log.warn("linkId::{}::标准赛事ID(id)为空，无法处理, message:{}", linkId, JSON.toJSONString(message));
            return;
        }

        try {
            // 1. 从消息中的 thirdMatchInfoList 提取所有 dataSourceCode
            Set<String> dataSourceCodesFromMsg = null;
            if (CollectionUtils.isNotEmpty(first.getThirdMatchInfoList())) {
                dataSourceCodesFromMsg = first.getThirdMatchInfoList().stream()
                        .map(ModifyMatchBusinessMessageDto.ThirdMatchInfoListEntry::getDataSourceCode)
                        .filter(code -> code != null && !code.isEmpty())
                        .collect(Collectors.toSet());
            }

            // 2. 从 DB 获取该标准赛事下所有三方赛事的数据源（防止漏掉）
            List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoService.getItems(standardMatchId);
            Set<String> dataSourceCodesFromDb = null;
            if (CollectionUtils.isNotEmpty(thirdMatchInfos)) {
                dataSourceCodesFromDb = thirdMatchInfos.stream()
                        .map(ThirdMatchInfo::getDataSourceCode)
                        .filter(code -> code != null && !code.isEmpty())
                        .collect(Collectors.toSet());
            }

            // 3. 合并两处数据源并去重
            Set<String> merged = new java.util.HashSet<>();
            if (CollectionUtils.isNotEmpty(dataSourceCodesFromMsg)) {
                merged.addAll(dataSourceCodesFromMsg);
            }
            if (CollectionUtils.isNotEmpty(dataSourceCodesFromDb)) {
                merged.addAll(dataSourceCodesFromDb);
            }

            String redisKey = CommonConstant.SETTLE_MATCH_DATASOURCES + standardMatchId;
            if (merged.isEmpty()) {
                redisService.del(redisKey);
                log.info("linkId::{}::标准赛事下无有效数据源, 已删除数据源缓存, standardMatchId:{}, key:{}",
                        linkId, standardMatchId, redisKey);
                return;
            }

            // 4. 写入 Redis
            redisService.set(redisKey, merged, RedisConfig.REDIS_MONTH_TIME);
            log.info("linkId::{}::标准赛事数据源列表已写入Redis, standardMatchId:{}, key:{}, value:{}, expire:{}s",
                    linkId, standardMatchId, redisKey, JSON.toJSONString(merged), RedisConfig.REDIS_MONTH_TIME);

        } catch (Exception e) {
            log.error("linkId::{}::处理 modify_match_business 消息失败, standardMatchId:{}, message:{}",
                    linkId, standardMatchId, JSON.toJSONString(message), e);
        }
    }
}
