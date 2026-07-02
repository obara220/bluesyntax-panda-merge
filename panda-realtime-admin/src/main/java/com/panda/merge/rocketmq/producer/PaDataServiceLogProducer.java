package com.panda.merge.rocketmq.producer;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.dto.PaDataServiceLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.PA_DATA_SERVICE_LOG;

/**
 * 统计PA数据服务日志
 * @author   tell
 * @since    2021年3月12日14:56:58
 */
@Slf4j
@Component
public class PaDataServiceLogProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @NacosValue(value = "${panda.data.service.log:false}", autoRefreshed = true)
    private boolean pandaDataServiceLog;

    /**
     * 统计PA数据服务日志
     * @param paDataServiceLogDTO    标准赛事信息
     */
    public void sendPaDataServiceLog(PaDataServiceLogDTO paDataServiceLogDTO) {
        try{
            if(pandaDataServiceLog){
                MessageBuilder<PaDataServiceLogDTO> builder = MessageBuilder.withPayload(paDataServiceLogDTO)
                        .setHeader(MessageConst.PROPERTY_KEYS, paDataServiceLogDTO.getLinkId());
                //通知预售开售赛事完赛
                rocketMqTemplate.send(PA_DATA_SERVICE_LOG+":"+paDataServiceLogDTO.getServiceType()+"-"+paDataServiceLogDTO.getApiCode(), builder.build());
                log.info("linkId=【{}】sendPaDataServiceLog 实时服务统计PA数据服务日志消息结束,topic=PA_DATA_SERVICE_LOG", paDataServiceLogDTO.getLinkId());
            }
        }catch (Exception e){
            log.info("::"+paDataServiceLogDTO.getLinkId()+"::sendPaDataServiceLog 实时服务统计PA数据服务日志消息异常,topic=PA_DATA_SERVICE_LOG,Exception:",e);
        }
    }

}
