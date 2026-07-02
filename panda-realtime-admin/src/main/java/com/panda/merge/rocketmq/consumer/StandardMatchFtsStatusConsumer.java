package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.annotation.ConsumerSwitch;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.TeamTypeEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMatchStatusMessage;
import com.panda.merge.model.FtsMatchRelation;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.processor.ThirdMatchStatusProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import com.panda.merge.service.FtsMatchRelationService;
import com.panda.merge.service.StandardMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.CONSUME_REALTIME_GROUP;
import static com.panda.merge.constant.ConstantSystem.STANDARD_MATCH_STATUS_FTS;

/**
 * 范特西赛事状态（赛事状态通道处投递 或者 赛程服务生成FTS赛事处投递）
 *
 * @author aldrich
 * 2023/12/19 14:50
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = STANDARD_MATCH_STATUS_FTS,
        consumerGroup = CONSUME_REALTIME_GROUP + STANDARD_MATCH_STATUS_FTS,
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("realtimeAdminApplication")
public class StandardMatchFtsStatusConsumer implements RocketMQListener<Request<StandardMatchStatusMessage>> {

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private ThirdMatchStatusProcessor thirdMatchStatusProcessor;

    @Autowired
    private FtsMatchRelationService ftsMatchRelationService;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<StandardMatchStatusMessage> dataCenterProducer;

    @Override
    public void onMessage(Request<StandardMatchStatusMessage> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,STANDARD_MATCH_STATUS_FTS);
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("linkId=【{}】范特西赛事状态处理开始,request={}", request.getLinkId(),JSON.toJSONString(request));
        try {
            StandardMatchStatusMessage standardMatchStatusMessage = request.getData();
            //赛事状态通道处投递,特殊处理
            if(StringUtils.equals(standardMatchStatusMessage.getDataSourceCode(),"handleFtsMatchStatusInfo")){
                //需求2550 FTS范特西赛事，通过标准赛事查询关联的范特西赛事
                List<FtsMatchRelation> ftsMatchRelations = ftsMatchRelationService.getFtsMatchRelation(standardMatchStatusMessage.getStandardMatchId());
                if(!CollectionUtils.isEmpty(ftsMatchRelations)){
                    log.info("linkId=【{}】范特西赛事状态处理,关联的赛事id={},赛种ID={},关联的赛事条数={},", request.getLinkId(), standardMatchStatusMessage.getStandardMatchId(), standardMatchStatusMessage.getSportId(),ftsMatchRelations.size());
                    for(FtsMatchRelation ftsMatchRelation : ftsMatchRelations){
                        if(null != ftsMatchRelation){
                            StandardMatchInfo ftsStandardMatchInfo = standardMatchInfoService.getItem(ftsMatchRelation.getNewMatchId());
                            if(null != ftsStandardMatchInfo){
                                String ftsHomeAway = standardMatchStatusMessage.getStandardMatchId().equals(ftsMatchRelation.getNewHomeMatchId()) ? TeamTypeEnum.HOME.code : TeamTypeEnum.AWAY.code;
                                //范特西赛事处理(标准)
                                thirdMatchStatusProcessor.handleFtsMatchStatusInfo(request.getLinkId() + "_" + DataSourceCodeEnum.FTS.code, ftsStandardMatchInfo, standardMatchStatusMessage.getMatchStatus(), request.getDataSourceTime(),ftsHomeAway);
                            }
                        }
                    }
                }
            }else{
                //赛程服务生成FTS赛事处投递
                StandardMatchInfo ftsStandardMatchInfo = standardMatchInfoService.getItem(standardMatchStatusMessage.getStandardMatchId());
                if (null == ftsStandardMatchInfo) {
                    log.info("linkId=【{}】范特西赛事状态处理,范特西赛事信息为空！ 赛事ID：{}", request.getLinkId(), standardMatchStatusMessage.getStandardMatchId());
                    return;
                }
                thirdMatchStatusProcessor.handleFtsMatchStatusInfo(request.getLinkId() + "_" + DataSourceCodeEnum.FTS.code, ftsStandardMatchInfo, standardMatchStatusMessage.getMatchStatus(), request.getDataSourceTime(),"all");
            }
        } catch (Exception e) {
            log.error("linkId=【" + request.getLinkId() + "】范特西赛事状态处理异常,Exception:", e);
        }finally {
            stopWatch.stop();
            log.info("linkId=【{}】范特西赛事状态处理结束,共耗时={}", request.getLinkId(),stopWatch.getTotalTimeMillis());
        }
    }
}
