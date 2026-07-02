package com.panda.merge.mq.consumer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.mysql.cj.util.StringUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.dto.message.StandardMatchMarketMessage;
import com.panda.merge.mapper.MatchSettleSpMarketMapper;
import com.panda.merge.mapper.MatchSettleSpOddsMapper;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.ThirdMatchInfoService;
import com.panda.merge.service.impl.SPStandardMarketServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;


/**
 * 特殊玩法盘口 初始化结算信息MQ
 * @author    KB
 * @since     2024年03月01日15:05:03
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = "SCORE_SETTLE_SP_MARKET",
        consumerGroup = "settle-group-"+"SCORE_SETTLE_SP_MARKET",
        consumeThreadMax = 16,
        consumeTimeout = 10000L
)
@DependsOn("settleScoresAdminApplication")
public class SPStandardMarketConsumer implements RocketMQListener<Request<StandardMatchMarketMessage>> {


    @Autowired
    MatchSettleSpMarketMapper matchSettleSpMarketMapper;
    @Autowired
    MatchSettleSpOddsMapper matchSettleSpOddsMapper;
    @Autowired
    SPStandardMarketServiceImpl spStandardMarketService;
    @Autowired
    StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    StandardMatchInfoService standardMatchInfoService;
    @Autowired
    ThirdMatchInfoService thirdMatchInfoService;
    //是否使用数据中心接口
    @NacosValue(value = "${datacenter.settle.switch}", autoRefreshed = true)
    private Boolean datacenterSettleSwitch;
    @Autowired
    CommonProducer commonProducer;
    @NacosValue(value = "${datacenter.settle.id}", autoRefreshed = true)
    private String datacenterSettleId;

    @Override
    public void onMessage(Request<StandardMatchMarketMessage> request) {
        log.info("数据中心SCORE_SETTLE_SP_MARKET分流Id:"+datacenterSettleId);
        if(datacenterSettleSwitch|| commonProducer.getDatacenterMatchIds(request.getData().getStandardMatchInfoId().toString())){
            log.info("Link::{}::SCORE_SETTLE_SP_MARKET数据中心分流Id::{}::",request.getLinkId(),request.getData().getStandardMatchInfoId());
            commonProducer.asyncSend(request, "datacenter-SCORE_SETTLE_SP_MARKET");
            return;
        }
        String linkId = request.getLinkId();
        log.info("::{}::特殊玩法盘口接收处理BEGIN", linkId);

        try {
           //1.查询对应的盘口 结算数据 看是否存在
            this.saveSettleSpMarket(request);
            //1.1 不存在 插入
            //1.2 存在更新

            log.info("特殊玩法盘口接收处理END linkId ::{}::",linkId);
        }catch (Exception e){
            log.error("特殊玩法盘口接收处理END linkId ::"+linkId+"::error:",e);
        }
    }

    private void saveSettleSpOdds(Long matchId,StandardMarketMessage marketMessage) {
        StandardMatchInfo standardMatchInfo =standardMatchInfoService.getItem(matchId);
        for (StandardMarketOddsMessage standardMarketOddsDTO :marketMessage.getMarketOddsList()) {
            //1.查询对应的投注项 结算数据 看是否存在
            MatchSettleSpOdds matchSettleSpOddsOld = matchSettleSpOddsMapper.selectByPrimaryKey(standardMarketOddsDTO.getId());
            MatchSettleSpOdds matchSettleSpOddsNew = spStandardMarketService.initSPOdds(matchId,standardMarketOddsDTO);
            //1.1 不存在 插入
            if(matchSettleSpOddsOld==null){
                matchSettleSpOddsNew.setCreateTime(System.currentTimeMillis());
                matchSettleSpOddsNew.setSportId(standardMatchInfo.getSportId());
                matchSettleSpOddsMapper.insert(matchSettleSpOddsNew);
            }
//            else {
//                //1.2 存在更新
//                matchSettleSpOddsNew.setModifyTime(System.currentTimeMillis());
//                matchSettleSpOddsNew.setSportId(standardMatchInfo.getSportId());
//                matchSettleSpOddsMapper.updateByPrimaryKey(matchSettleSpOddsNew);
//            }

        }
    }

    private void saveSettleSpMarket(Request<StandardMatchMarketMessage> request) {
        StandardMatchInfo standardMatchInfo =standardMatchInfoService.getItem(request.getData().getStandardMatchInfoId());
        for (StandardMarketMessage standardMarketMessage : request.getData().getMarketList()) {
            //1.查询对应的盘口 结算数据 看是否存在
            MatchSettleSpMarket matchSettleSpMarketOld = matchSettleSpMarketMapper.selectByPrimaryKey(standardMarketMessage.getId());
            MatchSettleSpMarket matchSettleSpMarketNew = spStandardMarketService.initSPMarket(request.getData().getStandardMatchInfoId(),standardMarketMessage);
            //1.1 不存在 插入
            if(matchSettleSpMarketOld==null){
                matchSettleSpMarketNew.setCreateTime(System.currentTimeMillis());
                matchSettleSpMarketNew.setSportId(standardMatchInfo.getSportId());
                matchSettleSpMarketMapper.insert(matchSettleSpMarketNew);
            }
//            else {
//                //1.2 存在更新
//                matchSettleSpMarketNew.setModifyTime(matchSettleSpMarketOld.getCreateTime());
//                matchSettleSpMarketNew.setSportId(standardMatchInfo.getSportId());
//                matchSettleSpMarketMapper.updateByPrimaryKey(matchSettleSpMarketNew);
//            }
            // 2.查看对应的盘口的投注项 结算数据 看是否存在
            //2.1 不存在插入
            //2.2 存在更新
            this.saveSettleSpOdds(request.getData().getStandardMatchInfoId(),standardMarketMessage);
        }

    }


}
