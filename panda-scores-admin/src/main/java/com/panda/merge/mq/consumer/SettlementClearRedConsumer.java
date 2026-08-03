package com.panda.merge.mq.consumer;

import cn.hutool.core.lang.TypeReference;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.OperateLogTypeEnum;
import com.panda.merge.config.mq.ConsumerConfigDetail;
import com.panda.merge.config.mq.MqConsumerConfig;
import com.panda.merge.dto.BetCancelLogVo;
import com.panda.merge.dto.Request;
import com.panda.merge.mapper.MatchScoresCenterLogMapper;
import com.panda.merge.model.MatchScoresCenterLog;
import com.panda.merge.mq.producer.CommonProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * 催结算清楚红色告警标记日志
 */
@Slf4j
@Component
@DependsOn("scoresAdminApplication")
public class SettlementClearRedConsumer extends AbstractSingleMessageMQConsumer<Request<List<BetCancelLogVo>>>{

    private static final String TOPIC ="match_settle_announcement_log";
    private static final String CONSUMER_GROUP="scores-group-MATCH_SETTLE_ANNOUNCEMENT_LOG";
    @NacosValue(value = "${datacenter.scores.switch:false}", autoRefreshed = true)
    private Boolean datacenterMergeSwitch;
    @Autowired
    CommonProducer commonProducer;
    @Autowired
    private MatchScoresCenterLogMapper matchScoresCenterLogMapper;

    @Override
    MqConsumerConfig buildConfig() {
        ConsumerConfigDetail consumerConfigDetail = ConsumerConfigDetail.builder()
                .threadNumber(20).pullBatchSize(64).build();
        return new MqConsumerConfig(TOPIC, CONSUMER_GROUP
                , new TypeReference<Request<List<BetCancelLogVo>>>() {},consumerConfigDetail);
    }

    @Override
    public void processMessage(Request<List<BetCancelLogVo>> request) {
        String linkId = request.getLinkId();
        log.info("linkId::{}::SettlementClearRedConsumer start switch:{} data: {}",linkId, datacenterMergeSwitch, request.getData());
        if (datacenterMergeSwitch) {
            //MQ消息转发给数据中心
            commonProducer.asyncSend(request, "datacenter-match_settle_announcement_log",request.getLinkId());
            return;
        }
        try{
            List<MatchScoresCenterLog> scoresCenterLogs = new ArrayList<>();
            for (BetCancelLogVo item :request.getData()) {
                MatchScoresCenterLog matchScoresCenterLog = new MatchScoresCenterLog();
                matchScoresCenterLog.setMatchManageId(item.getObjectId());
                matchScoresCenterLog.setOperateId(item.getObjectId());
                String objectName = item.getObjectName();
                if ((!StringUtils.isEmpty(objectName)) && (!"-".equals(objectName))) {
                    JSONObject jsonObject = JSONObject.parseObject(objectName);
                    String zs = jsonObject.getString("zs");
                    String en = jsonObject.getString("en");
                    objectName = zs + "&&" + en;
                }
                matchScoresCenterLog.setOperateName(objectName);
                matchScoresCenterLog.setOperateParaName(OperateLogTypeEnum.DISMISS_SETTLEMENT_ALERT_INDICATOR.getCode() + "");
                matchScoresCenterLog.setOperateType(OperateLogTypeEnum.DISMISS_SETTLEMENT_ALERT_INDICATOR.getCode() + "");
                String afterVal = item.getAfterVal();
                if ((!StringUtils.isEmpty(afterVal)) && (!"-".equals(afterVal))) {
                    JSONObject jsonObject = JSONObject.parseObject(afterVal);
                    String zs = jsonObject.getString("zs");
                    String en = jsonObject.getString("en");
                    afterVal = zs + "&&" + en;
                }
                String beforeVal = item.getBeforeVal();
                if ((!StringUtils.isEmpty(beforeVal)) && (!"-".equals(beforeVal))) {
                    JSONObject jsonObject = JSONObject.parseObject(beforeVal);
                    String zs = jsonObject.getString("zs");
                    String en = jsonObject.getString("en");
                    beforeVal = zs + "&&" + en;
                }
                matchScoresCenterLog.setOperateRearText(afterVal);
                matchScoresCenterLog.setOperateForwText(beforeVal);
                matchScoresCenterLog.setMatchManageId(item.getObjectId());
                matchScoresCenterLog.setOperateMatchId(item.getExtObjectId());
                String extObjectName = item.getExtObjectName();
                if ((!StringUtils.isEmpty(extObjectName)) && (!"-".equals(extObjectName))) {
                    JSONObject jsonObject = JSONObject.parseObject(extObjectName);
                    String zs = jsonObject.getString("zs");
                    String en = jsonObject.getString("en");
                    extObjectName = zs + "&&" + en;
                }
                matchScoresCenterLog.setOperateMatchName(extObjectName);
                String pageName = item.getOperatePageName();
                if ((!StringUtils.isEmpty(pageName)) && (!"-".equals(pageName))) {
                    JSONObject jsonObject = JSONObject.parseObject(pageName);
                    String zs = jsonObject.getString("zs");
                    String en = jsonObject.getString("en");
                    pageName = zs + "&&" + en;
                }
                matchScoresCenterLog.setOperateModule(pageName);
                matchScoresCenterLog.setOperateUserName(item.getUserName());
                matchScoresCenterLog.setIpAddress(item.getIp());

                long curTime = System.currentTimeMillis();
                matchScoresCenterLog.setCreateTime(curTime);
                matchScoresCenterLog.setModifyTime(curTime);
                //加入一键取消类型
                if (item.getOperatePageCode().equals(OperateLogTypeEnum.SCORES_CANCEL_WITH_ONE_CLICK.getCode())){
                    matchScoresCenterLog.setOperateParaName(OperateLogTypeEnum.SCORES_CANCEL_WITH_ONE_CLICK.getName() );
                    matchScoresCenterLog.setOperateType(OperateLogTypeEnum.SCORES_CANCEL_WITH_ONE_CLICK.getName() );
                    matchScoresCenterLog.setOperateRearText("取消");
                    matchScoresCenterLog.setOperateForwText("-");
                }
                scoresCenterLogs.add(matchScoresCenterLog);
            }
            matchScoresCenterLogMapper.batchInsert(scoresCenterLogs);

        }catch(Exception e){
            log.error("linkId::{}::SettlementClearRedConsumer error:",linkId,e);
        }
        log.info("linkId::{}::SettlementClearRedConsumer end!", linkId);
    }
}
