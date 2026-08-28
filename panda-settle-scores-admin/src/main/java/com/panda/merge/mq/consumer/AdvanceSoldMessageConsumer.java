package com.panda.merge.mq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.mysql.cj.util.StringUtils;
import com.panda.merge.api.ISettleCenterApi;
import com.panda.merge.constant.MatchLengthConstant;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.advertise.MatchSettleSwitcherDto;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 开售处理后补发事件
 * @author       Aison
 * @createDate  2020年10月23日10:00:10
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MATCH_ADVANCE_SALE,
        consumerGroup = "settle-group-"+ MATCH_ADVANCE_SALE,
        consumeThreadMax = 2,
        consumeTimeout = 10000L
)
@DependsOn("settleScoresAdminApplication")
public class AdvanceSoldMessageConsumer implements RocketMQListener<Request<StandardSportMarketSell>> {

    @Autowired
    ISettleCenterApi settleCenterApi;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    MatchSettleInfoMapper matchSettleInfoMapper;
    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;

    //是否使用数据中心接口
    @NacosValue(value = "${datacenter.settle.switch}", autoRefreshed = true)
    private Boolean datacenterSettleSwitch;

    @NacosValue(value = "${datacenter.settle.id}", autoRefreshed = true)
    private String datacenterSettleId;

    @Autowired
    CommonProducer commonProducer;

    @Override
    public void onMessage(Request<StandardSportMarketSell> request) {
        log.info("linkId: {} 数据中心MATCH_ADVANCE_SALE分流 start Id:{} data:{} ", request.getLinkId(), datacenterSettleId, request);
        if(datacenterSettleSwitch|| commonProducer.getDatacenterMatchIds(request.getData().getMatchInfoId().toString())){
            log.info("Link::{}::MATCH_ADVANCE_SALE数据中心分流Id::{}::",request.getLinkId(),request.getData().getMatchInfoId());
            commonProducer.asyncSend(request, "datacenter-MATCH_ADVANCE_SALE");
            return;
        }
        if(request==null||request.getData()==null||request.getData().getMatchInfoId()==null){
            return;
        }
        log.info("【AdvanceSoldMessageConsumer:"+
                         MATCH_ADVANCE_SALE +"】【::"+request.getLinkId()+"::】预开售处理后预生成开始");
        //只接足球
        if(!request.getData().getSportId().equals(1l) && !request.getData().getSportId().equals(2l)){
            log.info("linkId: {} 事件不是足/蓝球", request.getLinkId());
            return;
        }
        MatchSettleInfo matchSettleInfo =matchSettleInfoRepository.getModelMatchSettleInfo(request.getData().getMatchInfoId());
        if(matchSettleInfo!=null){
            log.info("linkId: {} matchSettleInfo已经有数据了 matchSettleInfo:{}", request.getLinkId(), matchSettleInfo);
            return;
        }
        //电竞赛事等赛制过滤
        StandardMatchInfo standardMatchInfo =standardMatchInfoService.getItem(request.getData().getMatchInfoId());
        if(standardMatchInfo==null){
            log.info("linkId: {} standardMatchInfo数据为null standardMatchInfo:{}", request.getLinkId(), standardMatchInfo);
            return;
        }
        if(!MatchLengthConstant.FOOT_BALL_FULL_SPORT_MATCH_LENGTH.contains(standardMatchInfo.getMatchLength())){
            log.info("linkId: {} 赛制不符合 standardMatchInfo length:{}", request.getLinkId(), standardMatchInfo.getMatchLength());
            return;
        }
        try {
            log.info("linkId: {} 开始组装切换结算2.0", request.getLinkId());
            MatchSettleSwitcherDto matchSettleSwitcherDto =new MatchSettleSwitcherDto();
            matchSettleSwitcherDto.setSettleType(2);
            matchSettleSwitcherDto.setLinkId(request.getLinkId());
            matchSettleSwitcherDto.setSportId(request.getData().getSportId());
            matchSettleSwitcherDto.setMatchId(request.getData().getMatchInfoId());
            matchSettleSwitcherDto.setOperatorName("Auto");
            settleCenterApi.settleSwitcher(matchSettleSwitcherDto);
        }catch (Exception e){
            log.error("【AdvanceSoldMessageConsumer:"+ MATCH_ADVANCE_SALE +"】【::"+request.getLinkId()+"::】预开售处理后预生成异常:", e);
        }
        //2.下发
        log.info("【AdvanceSoldMessageConsumer:"+
                         MATCH_ADVANCE_SALE +"】【::"+request.getLinkId()+"::】预开售处理后预生成结束");
    }
}
