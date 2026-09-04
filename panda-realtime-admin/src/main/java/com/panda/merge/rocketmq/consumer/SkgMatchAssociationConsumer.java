package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import com.panda.merge.rocketmq.producer.MatchEventInfoProducer;
import com.panda.merge.service.MatchEventInfoService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 关联SK数据，需要重推全量事件
 *
 * @author tell
 * @since 2023年12月20日16:56:16
 **/
@Slf4j
@Component
@RocketMQMessageListener(topic = MATCH_ASSOCIATION_INFO_SK,
        consumerGroup = CONSUME_REALTIME_GROUP + MATCH_ASSOCIATION_INFO_SK,
        consumeThreadMax = 256, consumeTimeout = 10000L)
public class SkgMatchAssociationConsumer implements RocketMQListener<MessageExt> {

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private MatchEventInfoService matchEventInfoService;

    @Autowired
    private MatchEventInfoProducer matchEventInfoProducer;
    @Autowired
    public BaseProcessor baseProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime_event:true}", autoRefreshed = true)
    private boolean realtimeEventSwitch;
    @Resource
    private DataCenterProducer dataCenterProducer;

//    @ConsumerSwitch("realtime")
    @Override
    public void onMessage(MessageExt ext) {
        if (!realtimeSwitch && !realtimeEventSwitch) {
            dataCenterProducer.send(ext,MATCH_ASSOCIATION_INFO_SK);
            return;
        }
        String linkId = null;
        Long standardMatchId = null;
        try {
            String message = new String(ext.getBody(), "utf-8");
            JSONObject parse = JSONObject.parseObject(message);
            standardMatchId = parse.getLong("standardMatchId");
            linkId = standardMatchId + "_" + System.currentTimeMillis();
            log.info("::{}::SkgMatchAssociationConsumer 补发事件下发,未找到预开售信息,标准赛事id={} 开始", linkId, standardMatchId);
            //刷新开售缓存并返回最新开售信息
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.refreshCache(standardMatchId);
            if (null == standardSportMarketSell) {
                log.info("::{}::SkgMatchAssociationConsumer 补发事件下发,未找到预开售信息,标准赛事id={}", linkId, standardMatchId);
                return;
            }
            //商业事件源编码
            String dataSource = standardSportMarketSell.getBusinessEvent();
            log.info("::{}::SkgMatchAssociationConsumer 补发事件下发,下发未下发事件,开售信息={}", linkId, JSON.toJSONString(standardSportMarketSell));
            //查询三方赛事信息
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standardMatchId, dataSource);
            if (null == thirdMatchInfo) {
                log.info("::{}::SkgMatchAssociationConsumer 补发事件下发,查询三方赛事为空,三方赛事id={}，商业事件源编码={}", linkId, standardMatchId, dataSource);
                return;
            }
            //获取标准赛事信息
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(standardMatchId);
            if (null == standardMatchInfo) {
                log.info("::{}::SkgMatchAssociationConsumer 补发事件下发,查询标准赛事为空,标准赛事id={}，商业事件源编码={}", linkId, standardMatchId, dataSource);
                return;
            }
            //根据三方赛事ID获取库中未下发的事件列表
            List<MatchEventInfo> oldEventInfoList = matchEventInfoService.getItemByThirdMatchIdAndDataSoureCode(
                    thirdMatchInfo.getId(), thirdMatchInfo.getDataSourceCode()
            );

            if(!CollectionUtils.isEmpty(oldEventInfoList)){
                //转换后的事件
                List<MatchEventInfo> matchEventInfos = baseProcessor.matchHomeAwayExchange(oldEventInfoList,thirdMatchInfo);
                log.info("::{}::SkgMatchAssociationConsumer 组装事件并下发开始,topic:"+MATCH_EVENT_INFO_SK+"，下发条数：{}", linkId,matchEventInfos.size());
                //根据 SourceType数据来源类型.0:UOF;1:liveData,来分组事件，
                Map<Integer, List<MatchEventInfo>> sourceType2SkList = matchEventInfos.stream().collect(Collectors.groupingBy(obj -> obj.getSourceType()));
                if(sourceType2SkList.size() > 1){
                    for (Integer sourceType: sourceType2SkList.keySet()) {
                        matchEventInfoProducer.pushMatchEventList2Mq(linkId+"_"+sourceType,sourceType2SkList.get(sourceType),thirdMatchInfo,MATCH_EVENT_INFO_SK,false);
                    }
                }else{
                    matchEventInfoProducer.pushMatchEventList2Mq(linkId,matchEventInfos,thirdMatchInfo,MATCH_EVENT_INFO_SK,false);
                }
                log.info("::{}::SkgMatchAssociationConsumer 组装事件并下发完成,topic:"+MATCH_EVENT_INFO_SK+"，下发条数：{}", linkId,matchEventInfos.size());
            }
        } catch (Exception e) {
            log.error("::" + linkId + "::SkgMatchAssociationConsumer 补发赛事状态下发异常,标准赛事id=" + standardMatchId + "，Exception:", e);
        }
        log.info("::{}::SkgMatchAssociationConsumer 补发事件下发,未找到预开售信息,标准赛事id={} 结束", linkId, standardMatchId);
    }
}
