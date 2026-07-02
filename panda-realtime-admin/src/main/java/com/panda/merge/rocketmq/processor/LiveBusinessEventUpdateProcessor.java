package com.panda.merge.rocketmq.processor;

import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.SaleUpdateLiveBusinessEventMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

import static com.panda.merge.constant.ConstantSystem.LIVE_BUSINESS_EVENT_UPDATE_MESSAGE;
import static com.panda.merge.constant.ConstantSystem.PROJECT_ID_REALTIME;

/**
 * 切换数据源后后补发事件给业务
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/9/11 <br>
 * @see com.panda.merge.rocketmq.processor <br>
 */
@Slf4j
@Component
@Validated
public class LiveBusinessEventUpdateProcessor {

    @Autowired
    private SoldMessageToEventProcessor soldMessageToEventProcessor;

    public void reissueEventInfo(@Valid Request<SaleUpdateLiveBusinessEventMessage> request){
        log.info("【"+ PROJECT_ID_REALTIME+" ："+ LIVE_BUSINESS_EVENT_UPDATE_MESSAGE+"】【::"+request.getLinkId()+"::】切换数据源后补发事件给业务开始");
        int num = soldMessageToEventProcessor.reissueEventInfo(request.getLinkId(), request.getData().getMatchId(),true);
        log.info("【"+ PROJECT_ID_REALTIME+" ："+ LIVE_BUSINESS_EVENT_UPDATE_MESSAGE+"】【::"+request.getLinkId()+"::】切换数据源后补发事件给业务结束,补发条数:{}",num);
    }
}
