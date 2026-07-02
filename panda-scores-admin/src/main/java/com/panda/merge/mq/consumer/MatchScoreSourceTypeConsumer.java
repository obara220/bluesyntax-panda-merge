package com.panda.merge.mq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.dto.Request;
import com.panda.merge.mapper.MatchScoresSourceTypeMapper;
import com.panda.merge.model.MatchScoresSourceType;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.repository.MatchScoresSourceTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

/**
 * 比分修改SourceType，比分中心异步下发消费
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "MATCH_SCORES_SOURCE_TYPE_UPDATE",
        consumerGroup = "scores-group-MATCH_SCORES_SOURCE_TYPE_UPDATE",
        consumeThreadMax = 256,
        consumeTimeout = 10000L
)
@DependsOn("scoresAdminApplication")
public class MatchScoreSourceTypeConsumer implements RocketMQListener<Request<MatchScoresSourceType>> {

    @Autowired
    MatchScoresSourceTypeMapper matchScoresSourceTypeMapper;
    @Autowired
    RedisService redisService;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
    @Override
    public void onMessage(Request<MatchScoresSourceType> matchScoresInfoRequest) {

        log.info("MatchScoreSourceTypeConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(matchScoresInfoRequest, "datacenter-MATCH_SCORES_SOURCE_TYPE_UPDATE",System.currentTimeMillis()+"");
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if(matchScoresInfoRequest.getData()!=null){
            try {
                matchScoresSourceTypeMapper.updateByPrimaryKey(matchScoresInfoRequest.getData());
                log.info("linkId::{}::LiveDataScoresConsumer,比分切换关联表更新&插入成功livedata,thirdMatchId={}", matchScoresInfoRequest.getLinkId(), matchScoresInfoRequest.getData().getThirdMatchId());
            } catch (Exception e) {
                log.error("linkId::{}::LiveDataScoresConsumer,比分切换关联表更新&插入失败,thirdMatchId={},数据库插入异常信息---{}", matchScoresInfoRequest.getLinkId(), matchScoresInfoRequest.getData().getThirdMatchId(), e.getMessage(), e);
            }
        }
        stopWatch.stop();
        log.info("matchScoresSourceType消费到数据  保存比分耗时：{}", stopWatch.getTotalTimeMillis());
//        log.info("{} MATCH_SCORES_SOURCE_TYPE_UPDATE 比分入库成功", matchScoresInfoRequest.getLinkId());
//        //1.先更新缓存
//        String key = RepositoryConstant.MATCH_SCORES_SOURCE_TYPE + matchScoresInfoRequest.getData().getThirdMatchId();
//        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(matchScoresInfoRequest.getData());
//        redisService.set(key, jsonObject.toJSONString(), RepositoryConstant.REDIS_THREE_TIME);
    }

}
