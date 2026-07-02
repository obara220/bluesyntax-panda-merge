package com.panda.merge.mq.consumer;

import cn.hutool.core.lang.TypeReference;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.config.mq.ConsumerConfigDetail;
import com.panda.merge.config.mq.MqConsumerConfig;
import com.panda.merge.dto.Request;
import com.panda.merge.mapper.StandardMatchScoresMapper;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.model.StandardMatchScores;
import com.panda.merge.mq.producer.CommonProducer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 比分修改，比分中心异步下发消费
 */

@Slf4j
@Component
//@RocketMQMessageListener(
//        topic = "STANDARD_MATCH_SCORES_UPDATE",
//        consumerGroup = "scores-group-STANDARD_MATCH_SCORES_UPDATE",
//        consumeThreadMax = 10,
//        consumeTimeout = 10L
//)
@DependsOn("scoresAdminApplication")
public class StandMatchScoresUpdateConsumer extends AbstractMultipleMessageMQConsumer<Request<StandardMatchScores>>{

    private static final String TOPIC ="STANDARD_MATCH_SCORES_UPDATE";
    private static final String CONSUMER_GROUP="scores-group-STANDARD_MATCH_SCORES_UPDATE";

    // 一次处理100条消息
    public static final int MESSAGE_LIST_SIZE=100;

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
    @Autowired
    private StandardMatchScoresMapper standardMatchScoresMapper;
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
    @Override
    MqConsumerConfig buildConfig() {
        ConsumerConfigDetail consumerConfigDetail = ConsumerConfigDetail.builder()
                .threadNumber(consumerThreadNumber).pullBatchSize(64).messageSize(MESSAGE_LIST_SIZE).build();
        return new MqConsumerConfig(TOPIC, CONSUMER_GROUP
                , new TypeReference<Request<StandardMatchScores>>() {},consumerConfigDetail);
    }


    @Override
    public void processMessageList(List<Request<StandardMatchScores>> list) {
        log.info("StandMatchScoresUpdateConsumer MQ消费数据开始...{}",datacenterMergeSwitch);
        if (datacenterMergeSwitch) {
            //MQ消息转发给数据中心
            commonProducer.asyncSendList(list, "datacenter-STANDARD_MATCH_SCORES_UPDATE",System.currentTimeMillis()+"");
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if(scoresTodbSwitch!=1){
            return;
        }
//        // 需要排序后再批量插入，避免死锁
        List<StandardMatchScores> matchScoresInfoList = list.stream().map(Request::getData)
                .sorted(Comparator.comparingLong(StandardMatchScores::getMatchId))
                .collect(Collectors.toList());
//        standardMatchScoresMapper.batchUpdateByPrimaryKey(matchScoresInfoList);
//        log.info("StandMatchScoresUpdateConsumer消费到数据 保存：{}条，耗时：",list.size(),stopWatch.getTotalTimeMillis());
        if(matchScoresInfoList.isEmpty()){
            return;
        }
        // 根据ID分组，并找到每个组中modifyTime最大的对象
        Map<Long, StandardMatchScores> maxMatchScoresInfoByGroup = matchScoresInfoList.stream()
                .collect(Collectors.groupingBy(
                        StandardMatchScores::getMatchId,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(StandardMatchScores::getUpdateTime)),
                                optional -> optional.orElse(null)
                        )
                ));
        List<StandardMatchScores> updateList = new ArrayList<>();
        List<StandardMatchScores> saveList = new ArrayList<>();

        maxMatchScoresInfoByGroup.forEach((id, matchScoresInfo) -> {
            if (matchScoresInfo != null) {
                saveList.add(matchScoresInfo);
//                if(matchScoresInfo.getId()==null){
//                    saveList.add(matchScoresInfo);
//                }else{
//                    updateList.add(matchScoresInfo);
//                }
            }
        });
        if(!saveList.isEmpty()){
            standardMatchScoresMapper.batchUpdateByPrimaryKey(saveList);
            log.info("StandMatchScoresUpdateConsumer消费到数据 批量保存：{}条 {}",saveList.size(),stopWatch.getTotalTimeMillis());
        }
//        if(!saveList.isEmpty()){
//            standardMatchScoresMapper.batchInsert(saveList);
//            log.info("StandMatchScoresUpdateConsumer消费到数据 批量保存：{}条",saveList.size(),stopWatch.getTotalTimeMillis());
//        }
//        if(!updateList.isEmpty()){
//            standardMatchScoresMapper.updateByPrimaryKey(updateList);
//            log.info("StandMatchScoresUpdateConsumer消费到数据 批量更新：{}条",updateList.size(),stopWatch.getTotalTimeMillis());
//        }
        log.info("StandMatchScoresUpdateConsumer消费到数据 批量入库：{}条，耗时：{}",matchScoresInfoList.size(),stopWatch.getTotalTimeMillis());

    }
}
