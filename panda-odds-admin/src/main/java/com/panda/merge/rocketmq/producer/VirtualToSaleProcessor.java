package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.dto.message.VirtualToSaleMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import java.util.Date;



@Slf4j
@Component
public class VirtualToSaleProcessor {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    /**
     * 虚拟赛事给赛程控制开售
     *  @param thirdRequest
     */
    public void pushVirtualToSaleMessage(Request<ThirdMatchMarketDTO> thirdRequest) {
        //非虚拟赛事退出
        if(!VirtualToSaleMessage.VIRTUAL_SPORTS_TYPE.contains(thirdRequest.getData().getSportId())){
            return;
        }
        log.info("::{}::start send VirtualToSaleMessage mq",thirdRequest.getLinkId());
        Request<VirtualToSaleMessage> request = new Request<>();
        VirtualToSaleMessage virtualToSaleMessage=new VirtualToSaleMessage();
        virtualToSaleMessage.setDataSourceCode(thirdRequest.getDataSourceCode());
        virtualToSaleMessage.setThirdMatchSourceId(thirdRequest.getData().getThirdMatchSourceId());
        virtualToSaleMessage.setSportId(thirdRequest.getData().getSportId());
        virtualToSaleMessage.setModifyTime(new Date().getTime());
        virtualToSaleMessage.setLinkedId(thirdRequest.getLinkId());
        request.setDataSourceCode(thirdRequest.getData().getDataSourceCode());
        request.setLinkId(thirdRequest.getLinkId());
        MessageBuilder<Request<VirtualToSaleMessage>> builder = MessageBuilder.withPayload(request).setHeader(MessageConst.PROPERTY_KEYS, thirdRequest.getLinkId());
        //第一个参数表示topic:tag
        rocketMqTemplate.send("VIRTUAL_TO_SALE:" + request.getData().getThirdMatchSourceId(), builder.build());
        log.info("::{}::开始组装虚拟赛事开售下发,topic:STANDARD_MATCH_RESULT,三方赛事原始ID：{},request:{}",  thirdRequest.getLinkId(), request.getData().getThirdMatchSourceId(), JSON.toJSONString(request));
    }

}
