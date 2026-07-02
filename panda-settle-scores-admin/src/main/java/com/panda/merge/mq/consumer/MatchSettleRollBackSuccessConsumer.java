
package com.panda.merge.mq.consumer;


import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.settle.MatchSettleRollBackSuccessDto;
import com.panda.merge.model.MatchSettleRollBackInfo;
import com.panda.merge.mq.producer.CommonProducer;
import com.panda.merge.service.IMatchSettleLogService;
import com.panda.merge.service.IWsPushService;
import com.panda.merge.v2.repository.MatchSettleRollBackInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RocketMQMessageListener(topic = "STANDARD_UPGRADE_SETTLE_ROLLBACK_REPLY", consumerGroup = "settle-group-STANDARD_UPGRADE_SETTLE_ROLLBACK_REPLY_scores-admin",
        consumeThreadMax = 128,
        consumeTimeout = 10000L)
@DependsOn("settleScoresAdminApplication")
public class MatchSettleRollBackSuccessConsumer implements RocketMQListener<MatchSettleRollBackSuccessDto> {
    @Autowired
    IMatchSettleLogService matchSettleLogService;
    @Autowired
    IWsPushService wsPushService;

    @Autowired
    private RedisService redisService;
    @Autowired
    MatchSettleRollBackInfoRepository matchSettleRollBackInfoRepository;
    //是否使用数据中心接口
    @NacosValue(value = "${datacenter.settle.switch}", autoRefreshed = true)
    private Boolean datacenterSettleSwitch;

    @Autowired
    CommonProducer commonProducer;
    /**
     * 赛事回滚成功回调
     */

    @Override
    public void onMessage(MatchSettleRollBackSuccessDto data) {
        if(datacenterSettleSwitch){
//            commonProducer.asyncSend(data, "datacenter-STANDARD_UPGRADE_SETTLE_ROLLBACK_REPLY");
            return;
        }
        log.info("MatchSettleRollBackSuccessConsumer data:{}",data);
//        MatchSettleRollBackSuccessDto data = mq.getData();
        String key = "MatchSettleRollBack"+data.getMatchId();
        if (data.getRollBackBetTotal().equals(0)  && data.getBetTotal().equals(0) ) {
            log.info("::{}::回滚回调订单总数和回滚订单数都是0 不予处理, 数据信息：{}",data.getLinkId(),data.toString());
            return;
        }


        if(data !=null){

            if(redisService.tryLock(key,data.toString(),5,5)) {

                try {
                log.info("回滚成功数据信息："+data.toString());
                MatchSettleRollBackInfo info = matchSettleRollBackInfoRepository.getModelMatchSettleRollBackInfo(data.getEvenRollBackId());
                if(info != null){
                    info.setRollBackStatus(0);
                    if(info.getRollBackOrderCount() == null){//回滚订单数
                        info.setRollBackOrderCount(data.getRollBackBetTotal()+0l);
                    } else {
                        info.setRollBackOrderCount(data.getRollBackBetTotal()+info.getRollBackOrderCount());
                    }
                    if(info.getOrderCount() == null){//总订单数
                        info.setOrderCount(data.getBetTotal()+0l);
                    } else {
                        info.setOrderCount(data.getBetTotal()+info.getOrderCount());
                    }
                    info.setRollBackSuccessTime(System.currentTimeMillis());
                    info.setModifyTime(System.currentTimeMillis());
                    matchSettleRollBackInfoRepository.updateMatchSettleRollBackInfoToRedis(info,false);
                    log.info("::{}::回滚回调更新成功, 数据信息：{}",data.getLinkId(),info.toString());
                    //推送WS
                    wsPushService.pushMatchSettleRollBackStatus(info);

                    //增加日志
                    matchSettleLogService.upLog(data.getEvenRollBackId(),data.getMatchId(), info.getRollBackOrderCount()+"/"+info.getOrderCount());
                }


                }catch (Exception e){

                    log.error("回滚回调异常 linkedId:"+data.getLinkId()+":",e);
                }finally {
                    redisService.unLock(key,data.toString());
                }
            }

        }

    }
}

