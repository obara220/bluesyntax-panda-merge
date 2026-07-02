package com.panda.merge.mq.consumer;

import cn.hutool.core.lang.TypeReference;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.config.RedisService;
import com.panda.merge.config.mq.ConsumerConfigDetail;
import com.panda.merge.config.mq.MqConsumerConfig;
import com.panda.merge.constant.RepositoryConstant;
import com.panda.merge.dto.Request;
import com.panda.merge.mapper.MatchScoresInfoMapper;
import com.panda.merge.mapper.MatchTimeInfoMapper;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.MatchTimeInfo;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.utils.MessageGZIP;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.RepositoryConstant.MATCH_TIME_INFO;


/**
 * 赛事时间，PD报球板+比分中心下发
 */
@Slf4j
@Component
//@RocketMQMessageListener(
//        topic = "MATCH_TIME_INFO_UPDATE",
//        consumerGroup = "scores-group-MATCH_TIME_INFO_UPDATE",
//        consumeThreadMax = 20,
//        consumeTimeout = 10000L
//)
@DependsOn("scoresAdminApplication")
public class MatchTimeInfoConsumer extends AbstractMultipleMessageMQConsumer<Request<MatchTimeInfo>>{

    @Autowired
    MatchTimeInfoMapper matchTimeInfoMapper;
    @Autowired
    RedisService redisService;
    private static final String TOPIC ="MATCH_TIME_INFO_UPDATE";
    private static final String CONSUMER_GROUP="scores-group-MATCH_TIME_INFO_UPDATE";

    // 一次处理20条消息
    public static final int MESSAGE_LIST_SIZE=20;
    /**
     * 消费者需要配置多少线程，范围0-1000
     */
    public static final String NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY ="mq.uof-score.consumer.thread";
    @NacosValue(value = "${"+ NACOS_MQ_CONSUMER_THREAD_NUMBER_KEY +":20}",autoRefreshed = true)
    @Getter
    @Setter
    private Integer consumerThreadNumber;
      /**
     * 比分服务数据是否入库开关，默认1开  0关
     */
    public static final String SCORES_TODB_SWITCH ="scores.toDB.switch";
    @NacosValue(value = "${"+ SCORES_TODB_SWITCH +":1}",autoRefreshed = true)
    @Getter
    @Setter
    private Integer scoresTodbSwitch;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
//    @Override
//    public void onMessage(Request<MatchTimeInfo> matchTimeInfoRequest) {
//        if(matchTimeInfoRequest.getData()!=null){
////            matchTimeInfoMapper.updateByPrimaryKey(matchTimeInfoRequest.getData());
//        }
//        String key = MATCH_TIME_INFO +matchTimeInfoRequest.getData().getId();
//        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(matchTimeInfoRequest.getData());
//        redisService.set(key, MessageGZIP.compressToByte(jsonObject.toJSONString()), RepositoryConstant.REDIS_THREE_TIME);
//        log.info("{} MATCH_TIME_INFO_UPDATE 时间入库成功", matchTimeInfoRequest.getLinkId());
//    }

    @Override
    MqConsumerConfig buildConfig() {
        ConsumerConfigDetail consumerConfigDetail = ConsumerConfigDetail.builder()
                .threadNumber(consumerThreadNumber).pullBatchSize(64).messageSize(MESSAGE_LIST_SIZE).build();
        return new MqConsumerConfig(TOPIC, CONSUMER_GROUP
                , new TypeReference<Request<MatchTimeInfo>>() {},consumerConfigDetail);
    }

    @Override
    public void processMessageList(List<Request<MatchTimeInfo>> list) {
        log.info("MatchTimeInfoConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch) {
            //MQ消息转发给数据中心
            commonProducer.asyncSendList(list, "datacenter-MATCH_TIME_INFO_UPDATE",System.currentTimeMillis()+"");
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if(scoresTodbSwitch!=1){
            return;
        }
        List<MatchTimeInfo> matchTimeInfoList = list.stream().map(Request::getData)
                .sorted(Comparator.comparingLong(MatchTimeInfo::getId))
                .collect(Collectors.toList());

        // 根据ID分组，并找到每个组中modifyTime最大的对象
        Map<Long, MatchTimeInfo> maxMatchTimeInfoByGroup = matchTimeInfoList.stream()
                .collect(Collectors.groupingBy(
                        MatchTimeInfo::getId,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(MatchTimeInfo::getModifyTime)),
                                optional -> optional.orElse(null)
                        )
                ));
        List<MatchTimeInfo> saveList = new ArrayList<>();
        maxMatchTimeInfoByGroup.forEach((id, matchTimeInfo) -> {
            if (matchTimeInfo != null) {
                saveList.add(matchTimeInfo);
            }
        });
        if(!saveList.isEmpty()){
            matchTimeInfoMapper.batchUpdateByPrimaryKey(saveList);
        }
        log.info("MatchTimeInfo消费到数据 保存：{}条，耗时：",saveList.size(),stopWatch.getTotalTimeMillis());
    }
}
