package com.panda.merge.service.impl;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.enums.WarningTypeEnum;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.WarningEventDTO;
import com.panda.merge.dto.WarningMessageDTO;
import com.panda.merge.service.WarningService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WarningServiceImpl implements WarningService {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    @Override
    public void warn(WarningEventDTO event) {

        try {
            WarningMessageDTO message = buildMessage(event);

            if (!shouldNotify(message)) {
                return;
            }

            send(message, event.getLinkId());
        } catch (Exception e) {
            log.error("WarningService error, event={}", JSON.toJSONString(event), e);
        }
    }

    private void send(WarningMessageDTO warnMessage, String linkId) {
        Request<List<String>> request = new Request<>(warnMessage.getContents(), linkId, ConstantSystem.TOPIC_PA_WARN, null, warnMessage.getCode());
        //会判断超过五分钟不发预警 去掉
        request.setDataSourceTime(null);
        try {
            Message<String> message = MessageBuilder.withPayload(JSON.toJSONString(request))
                    .setHeader(MessageConst.PROPERTY_KEYS, request.getLinkId())
                    .build();
            rocketMqTemplate.send(ConstantSystem.TOPIC_PA_WARN, message);
        } catch (Exception e) {
            log.error("发送预警消息异常, linkId={}, code={}", linkId, warnMessage.getCode(), e);
        }

        log.info("发送预警消息结束, linkId={}, code={}", linkId, warnMessage.getCode());
    }

    /**
     * 是否需要发送 (频控 / 降级 / 开关)
     * @param message
     * @return
     */
    private boolean shouldNotify(WarningMessageDTO message) {
        if (CollectionUtils.isEmpty(message.getContents())) {
            return false;
        }
        return true;
    }

    /**
     * 构建预警消息
     * @param event
     * @return
     */
    private WarningMessageDTO buildMessage(WarningEventDTO event) {

        WarningTypeEnum warningType = event.getWarningType();
        List<Map<String, Object>> contexts = event.getContexts();

        WarningMessageDTO warningMessage = new WarningMessageDTO();
        warningMessage.setCode(warningType.getCode());
        warningMessage.setContexts(event.getContexts());
        warningMessage.setContents(new ArrayList<>());
        for (Map<String, Object> context : contexts) {
            switch (warningType) {
                case CROSS_PERIOD_SCORE_CHANGED:
                    StringBuilder sb = new StringBuilder();
                    sb.append("数据源: ");
                    sb.append(context.get("dataSourceCode"));
                    sb.append("\n");

                    sb.append("赛事对阵: ");
                    sb.append(context.get("homeAwayInfo"));
                    sb.append("\n");

                    sb.append("赛事ID: ");
                    sb.append(context.get("matchManageId"));
                    sb.append("\n");


                    sb.append("下发阶段: ");
                    //第二节
                    sb.append(context.get("curMatchPeriod"));
                    sb.append("\n");

                    sb.append("修正/删除阶段: ");
                    //第一节
                    sb.append(context.get("originalMatchPeriod"));
                    sb.append("\n");

                    sb.append("原因: ");
                    //赛事跨阶段下发修正/删除比分
                    sb.append(context.get("reason"));
                    sb.append("\n");

                    warningMessage.getContents().add(sb.toString());
                    break;
            }

        }
        return warningMessage;
    }
}
