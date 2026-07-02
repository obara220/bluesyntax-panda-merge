package com.panda.merge.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.TraderUpdateDto;
import com.panda.merge.mapper.MatchSettleInfoMapper;
import com.panda.merge.model.MatchSettleInfo;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.v2.repository.MatchSettleInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@Component
@RocketMQMessageListener(topic = "RCS_TRADE_NUM", consumeThreadMax = 10,consumerGroup = "settle-group-RCS_TRADE_NUM",
        consumeTimeout = 10000L)
@DependsOn("settleScoresAdminApplication")
public class TraderUpdateBySettleConsumer implements RocketMQListener<Request<TraderUpdateDto>> {
    //是否使用数据中心接口
    @NacosValue(value = "${datacenter.settle.switch}", autoRefreshed = true)
    private Boolean datacenterSettleSwitch;
    @NacosValue(value = "${datacenter.settle.id}", autoRefreshed = true)
    private String datacenterSettleId;
    @Autowired
    private MatchSettleInfoMapper matchSettleInfoMapper;

    @Autowired
    MatchSettleInfoRepository matchSettleInfoRepository;
    @Autowired
    CommonProducer commonProducer;

    /**
     * 接收操盘指派后 更新结算信息中的操盘手
     * @param request
     */


    @Override
    @Deprecated
    public void onMessage(Request<TraderUpdateDto> request) {
        log.info("数据中心RCS_TRADE_NUM分流Id:"+datacenterSettleId);
        if(datacenterSettleSwitch||commonProducer.getDatacenterMatchIds(request.getData().getMatchId())){
            log.info("Link::{}::RCS_TRADE_NUM数据中心分流Id::{}::",request.getLinkId(),request.getData().getMatchId());
            commonProducer.asyncSend(request, "datacenter-RCS_TRADE_NUM");
            return;
        }
        log.info("::{}::操盘指派更新结算信息开始", JSON.toJSON(request));
        String matchId = request.getData().getMatchId();
        MatchSettleInfo matchSettleInfo = matchSettleInfoRepository.getModelMatchSettleInfo(Long.valueOf(matchId));
        if (matchSettleInfo ==  null) {
            return;
        }

        if (!StringUtils.isEmpty(matchSettleInfo.getAllLiveTrader())) {
            //结算所有操盘手
            ArrayList<String> list = JSON.parseObject(matchSettleInfo.getAllLiveTrader(), ArrayList.class);
            //mq传入的操盘手列表
            List<HashMap<String, String>> trader = request.getData().getTrader();
            ArrayList<String> requestTrader = new ArrayList<>();

            //结算表操盘手和传入的操盘手比对  结算表没有则新增
            for (int i = 0; i < trader.size(); i++) {
                HashMap<String, String> map = trader.get(i);
                String traderCode = map.get("traderCode");
                requestTrader.add(traderCode);
                if (!list.contains(traderCode)) {
                    list.add(traderCode);
                }
            }
            //结算表操盘手和传入的操盘手比对  剔除MQ没有存在的操盘手
            for (int i = 0; i < list.size(); i++) {
                String traderCode = list.get(i);
                if (!requestTrader.contains(traderCode)) {
                    list.remove(i);
                }
            }
            MatchSettleInfo info = new MatchSettleInfo();
            BeanUtils.copyProperties(matchSettleInfo,info);
            info.setAllLiveTrader(JSON.toJSONString(list));
            info.setModifyTime(System.currentTimeMillis());
            info.setId(matchSettleInfo.getId());
            matchSettleInfoRepository.updateMatchSettleInfoToRedis(info,false);
            matchSettleInfo.setAllLiveTrader(JSON.toJSONString(list));
            matchSettleInfo.setModifyTime(System.currentTimeMillis());

        }
    }
}
