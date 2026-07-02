package com.panda.merge.rocketmq.consumer.asyncdb;

import cn.hutool.core.lang.TypeReference;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.job.RocketmqConsumerJob;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.rocketmq.AbstractMultipleMessageMQConsumer;
import com.panda.merge.rocketmq.ConsumerConfigDetail;
import com.panda.merge.rocketmq.MqConsumerConfig;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import com.panda.merge.service.MatchEventInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;


/**
 * 三方赛事事件信息异步批量入库
 *
 * @author Tell
 * @since 2025年2月17日13:09:23
 */
@Slf4j
@Component
@DependsOn("nonrealtimeAdminApplication")
public class MatchEventInfo2DbsConsumer extends AbstractMultipleMessageMQConsumer<Request<List<MatchEventInfo>>> {

    private static final String CONSUMER_GROUP = CONSUME_NONREALTIME_GROUP + DATA_MATCHS_EVENT_INFO_DB;

    @Value("${odds.admin.pull.batch.size:500}")
    private Integer PULL_BATCH_SIZE;

    @Value("${odds.admin.consume.message.batch.max.size:100}")
    private Integer CONSUME_MESSAGE_BATCH_MAX_SIZE;

    @Value("${odds.admin.partition.nums.per.time:10}")
    private Integer PARTITION_NUMS_PER_TIME;

    @Value("${odds.admin.pull.interval:-1}")
    private Long PULL_INTERVAL;

    /**
     * panda数据库状态是否异常（false:否，true:是）
     * */
    @NacosValue(value = "${panda.db.error.realtime:false}", autoRefreshed = true)
    private Boolean pandaDbIsError;

    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;

    @NacosValue(value = "${consumer.switch.realtime_nonrealtime_event:true}", autoRefreshed = true)
    private boolean realtimeEventSwitch;

    @Resource
    private DataCenterProducer<List<MatchEventInfo>> dataCenterProducer;

    @Autowired
    private MatchEventInfoService matchEventInfoService;

    @Autowired
    private RocketmqConsumerJob rocketmqConsumerJob;

    @Override
    public MqConsumerConfig buildConfig() {
        ConsumerConfigDetail consumerConfigDetail = ConsumerConfigDetail.builder().messageSize(CONSUME_MESSAGE_BATCH_MAX_SIZE).pullBatchSize(PULL_BATCH_SIZE).pullInterval(PULL_INTERVAL).build();
        return new MqConsumerConfig(DATA_MATCHS_EVENT_INFO_DB, CONSUMER_GROUP, new TypeReference<Request<List<MatchEventInfo>>>() {
        }, consumerConfigDetail);
    }

    @Override
    public void processMessageList(List<Request<List<MatchEventInfo>>> requestList) {
        if (!realtimeSwitch && !realtimeEventSwitch) {
            dataCenterProducer.send(requestList,DATA_MATCHS_EVENT_INFO_DB);
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("【DATA_MATCHS_EVENT_INFO_DB】批量事件消费开始,消费条数={}", requestList.size());
        try {
            //按数据源分组批量入库
            Map<String, List<Request<List<MatchEventInfo>>>> dataSourceCode2List = requestList.stream().collect(Collectors.groupingBy(obj -> obj.getDataSourceCode()));
            //按数据源分批处理入库
            for (String dataSourceCode : dataSourceCode2List.keySet()) {
//                List<Request<List<MatchEventInfo>>> requests = dataSourceCode2List.get(dataSourceCode);
                List<MatchEventInfo> matchEventInfoList = dataSourceCode2List.get(dataSourceCode).stream()
                        .map(Request::getData)
                        .filter(data -> data != null)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
                log.info("【DATA_MATCHS_EVENT_INFO_DB】【{}】 处理开始,处理条数={},pandaDbIsError={}", dataSourceCode, matchEventInfoList.size(),pandaDbIsError);
                try {
                    if(!pandaDbIsError){
                        //V02事件特殊处理
                        if (DataSourceCodeEnum.TS.code.equalsIgnoreCase(dataSourceCode)) {
                            //集锦
                            List<MatchEventInfo> resList_0 = matchEventInfoList.stream().filter(obj -> ZERO.equals(obj.getSourceType())).collect(Collectors.toList());
                            matchEventInfoService.upOrSaveBatch(resList_0,"DATA_MATCHS_EVENT_INFO_DB");
                            //非集锦
                            List<MatchEventInfo> resList_1 = matchEventInfoList.stream().filter(obj -> ONE.equals(obj.getSourceType())).collect(Collectors.toList());
                            matchEventInfoService.saveBatch(resList_1,"DATA_MATCHS_EVENT_INFO_DB");
                        }else{
                            matchEventInfoService.saveBatch(matchEventInfoList,"DATA_MATCHS_EVENT_INFO_DB");
                        }
                    }
                } catch (Exception e) {
                    log.error("【DATA_MATCHS_EVENT_INFO_DB】【" + dataSourceCode + "】 处理异常,Exception:", e);
                }
                log.info("【DATA_MATCHS_EVENT_INFO_DB】【{}】 处理结束", dataSourceCode);
            }
        } catch (Exception e) {
            log.error("【DATA_MATCHS_EVENT_INFO_DB】批量事件消费处理异常,Exception:", e);
        } finally {
            stopWatch.stop();
            log.info("【DATA_MATCHS_EVENT_INFO_DB】批量事件消费结束,共耗时={}",stopWatch.getTotalTimeMillis());
        }
    }
}
