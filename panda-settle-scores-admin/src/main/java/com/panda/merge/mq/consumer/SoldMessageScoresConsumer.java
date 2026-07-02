package com.panda.merge.mq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.constant.MatchSettleCheckConstant;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.SaleUpdateLiveBusinessEventMessage;
import com.panda.merge.mapper.MatchScoresSearchMapper;
import com.panda.merge.mapper.MatchSettleCheckInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.service.IBasketballInSettleService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.v2.service.IMatchSettleInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.LIVE_BUSINESS_EVENT_UPDATE_MESSAGE;
import static com.panda.merge.constant.ConstantSystem.SOLD_MESSAGE;

/**
 * 开售处理后补发事件
 * @author       Aison
 * @createDate  2020年10月23日10:00:10
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = LIVE_BUSINESS_EVENT_UPDATE_MESSAGE,
        consumerGroup = "settle-group-"+ LIVE_BUSINESS_EVENT_UPDATE_MESSAGE,
        consumeThreadMax = 2,
        consumeTimeout = 10000L
)
@DependsOn("settleScoresAdminApplication")
public class SoldMessageScoresConsumer implements RocketMQListener<Request<SaleUpdateLiveBusinessEventMessage>> {

    @Autowired
    MatchScoresSearchMapper matchScoresSearchMapper;
    @Autowired
    MatchSettleCheckInfoMapper matchSettleCheckInfoMapper;
    @Autowired
    IMatchSettleInfoService matchSettleInfoService;
    @Autowired
    IBasketballInSettleService basketballInSettleService;
    @Autowired
    StandardSportMarketSellService standardSportMarketSellService;
    //是否使用数据中心接口
    @NacosValue(value = "${datacenter.settle.switch}", autoRefreshed = true)
    private Boolean datacenterSettleSwitch;
    @NacosValue(value = "${datacenter.settle.id}", autoRefreshed = true)
    private String datacenterSettleId;
    @Autowired
    CommonProducer commonProducer;

    @Override
    public void onMessage(Request<SaleUpdateLiveBusinessEventMessage> request) {
        log.info("数据中心LIVE_BUSINESS_EVENT_UPDATE_MESSAGE分流Id:"+datacenterSettleId);
        if(datacenterSettleSwitch||commonProducer.getDatacenterMatchIds(request.getData().getMatchId().toString())){
            log.info("Link::{}::LIVE_BUSINESS_EVENT_UPDATE_MESSAGE数据中心分流Id::{}::",request.getLinkId(),request.getData().getMatchId());
            commonProducer.asyncSend(request, "datacenter-LIVE_BUSINESS_EVENT_UPDATE_MESSAGE");
            return;
        }
        log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getLinkId()+"::】开售处理后补发结算事件开始");
        //1.查询标准比分
        List<Long> standardIdList=new ArrayList<>();

        if(request==null||request.getData()==null||request.getData().getMatchId()==null){
            return;
        }
        try {
            //篮球即时结算要关闭 切换后 事件源不一致
            if(StringUtils.isNotEmpty(request.getData().getBusinessEventCode())&& StringUtils.isNotEmpty(request.getData().getBusinessEventCodeOld())
                    &&(!request.getData().getBusinessEventCode().equals(request.getData().getBusinessEventCodeOld()))) {
                if(request.getData().getSportId().equals(2L)){
                    basketballInSettleService.closeInAutoSettleBySoldMsgChange(request.getData().getMatchId(),request.getData().getBusinessEventCode());
                }
            }
            List<Long> standardMatchIds = new ArrayList<>();
            standardMatchIds.add(request.getData().getMatchId());

            List<StandardSportMarketSell> eventSells = standardSportMarketSellService.getItems(standardMatchIds);
            if(!matchSettleInfoService.matchIsAutoSettle(request.getData().getMatchId())) {
                if (eventSells.size() != 0) {
                    String dataSourceCode = eventSells.get(0).getBusinessEvent();
                    MatchSettleCheckInfoExample checkInfoExample = new MatchSettleCheckInfoExample();
                    checkInfoExample.createCriteria().andStandardMatchIdEqualTo(request.getData().getMatchId())
                            .andCheckDataTypeEqualTo(MatchSettleCheckConstant.CheckDataType.DATA_SOURCE)
                            .andDataSourceCodeNotEqualTo(dataSourceCode);
                    matchSettleCheckInfoMapper.deleteByExample(checkInfoExample);
                }
            }
            //2.挨个检查事件是否需要自动生成
        }catch (Exception e){
            log.error("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getLinkId()+"::】开售处理后补发结算事件异常:",e);
        }
        //2.下发
        log.info("【SoldMessageScoresConsumer:"+ SOLD_MESSAGE+"】【::"+request.getLinkId()+"::】开售处理后补发结算事件结束");
    }
}
