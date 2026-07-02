package com.panda.merge.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchStatusDTO;
import com.panda.merge.dto.message.SaleUpdateLiveBusinessEventMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.processor.LiveBusinessEventUpdateProcessor;
import com.panda.merge.rocketmq.processor.ThirdMatchStatusProcessor;
import com.panda.merge.rocketmq.producer.DataCenterProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 切换数据源后后补发赛事状态给业务
 * @author      Aison
 * @since       2020年10月23日09:57:07
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = LIVE_BUSINESS_STATUS_UPDATE_MESSAGE,
        consumerGroup = CONSUME_REALTIME_GROUP + LIVE_BUSINESS_STATUS_UPDATE_MESSAGE,
        consumeThreadMax = 128,
        consumeTimeout = 10000L
)
@DependsOn("realtimeAdminApplication")
public class LiveBusinessStatusUpdateConsumer implements RocketMQListener<Request<SaleUpdateLiveBusinessEventMessage>> {

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private ThirdMatchStatusProcessor thirdMatchStatusProcessor;
    @NacosValue(value = "${consumer.switch.realtime_nonrealtime:true}", autoRefreshed = true)
    private boolean realtimeSwitch;
    @Resource
    private DataCenterProducer<SaleUpdateLiveBusinessEventMessage> dataCenterProducer;

    @Override
    public void onMessage(Request<SaleUpdateLiveBusinessEventMessage> request) {
        if (!realtimeSwitch) {
            dataCenterProducer.send(request,LIVE_BUSINESS_STATUS_UPDATE_MESSAGE);
            return;
        }
        log.info("LiveBusinessStatusUpdateConsumer 切换赛事状态源通知消费开始,request={}", JSON.toJSONString(request));
        SaleUpdateLiveBusinessEventMessage data = request.getData();
        try {
            //商业状态源编码
            String dataSourceCode = data.getBusinessEventCode();
            //查询三方赛事信息
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItemNoCache(data.getMatchId(), dataSourceCode);
            if (null == thirdMatchInfo) {
                log.info("linkId=【{}】LiveBusinessStatusUpdateConsumer 补发赛事状态下发,查询三方赛事为空,三方赛事id={}，商业事件源编码={}",request.getLinkId(), data.getMatchId(),dataSourceCode);
                return;
            }
            //获取标准赛事信息
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(data.getMatchId());
            if (null == standardMatchInfo) {
                log.info("linkId=【{}】LiveBusinessStatusUpdateConsumer 补发赛事状态下发,查询标准赛事为空,标准赛事id={}，商业事件源编码={}",request.getLinkId(), data.getMatchId(),dataSourceCode);
                return;
            }
            //刷新开售缓存并返回最新开售信息
            StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.refreshCache(data.getMatchId());
            if (null == standardSportMarketSell) {
                log.info("linkId=【{}】LiveBusinessStatusUpdateConsumer 补发赛事状态下发,未找到预开售信息,标准赛事id={}", request.getLinkId(),data.getMatchId());
                return;
            }
            log.info("linkId=【{}】LiveBusinessStatusUpdateConsumer 补发赛事状态下发,开售信息={}", request.getLinkId(),JSON.toJSONString(standardSportMarketSell));
            //当前滚球赛事状态服务商(用于判断标准赛事信息是否更新)
            String matchStatusSourceCode = StringUtils.isNotBlank(standardSportMarketSell.getMatchStatusSourceCode()) ? standardSportMarketSell.getMatchStatusSourceCode() : standardMatchInfo.getDataSourceCode();
            if (!dataSourceCode.equalsIgnoreCase(matchStatusSourceCode)) {
                log.info("linkId=【{}】LiveBusinessStatusUpdateConsumer 当前数据服务商{}和赛事状态服务商{}不匹配！", request.getLinkId(), dataSourceCode,matchStatusSourceCode);
            }else{
                ThirdMatchStatusDTO thirdMatchStatusDTO = new ThirdMatchStatusDTO();
                thirdMatchStatusDTO.setMatchStatus(thirdMatchInfo.getMatchStatus());
                thirdMatchStatusDTO.setThirdMatchSourceId(thirdMatchInfo.getThirdMatchSourceId());
                thirdMatchStatusDTO.setDataSourceCode(thirdMatchInfo.getDataSourceCode());
                log.info("linkId=【{}】LiveBusinessStatusUpdateConsumer 当前标准赛事信息={}", request.getLinkId(),JSON.toJSONString(standardMatchInfo));
                //更新标准赛事（标准赛事依赖的数据源会同步更新标准赛事，其他数据源则只更新本身的数据）
                StandardMatchInfo upStandardMatchInfo = thirdMatchStatusProcessor.updateStandardMatchInfo(request.getLinkId(),standardMatchInfo, thirdMatchInfo, thirdMatchStatusDTO, standardSportMarketSell);
                //向下游推送赛事状态数据
                thirdMatchStatusProcessor.pushMatchStatusInfo(request.getLinkId(), upStandardMatchInfo, standardSportMarketSell, thirdMatchStatusDTO.getDataSourceCode(), request.getDataSourceTime());
            }
        }catch (Exception e){
            log.error("linkId=【"+request.getLinkId()+"】LiveBusinessStatusUpdateConsumer 补发赛事状态下发异常,标准赛事id="+data.getMatchId()+"，Exception:", e);
        }
    }
}
