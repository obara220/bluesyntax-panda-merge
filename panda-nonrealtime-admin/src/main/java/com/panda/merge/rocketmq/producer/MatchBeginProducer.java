package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.dto.CloseCategoryDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardRelationNewStandard;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardRelationNewStandardService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.panda.merge.constant.ConstantSystem.*;

@Slf4j
@Component
public class MatchBeginProducer {
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    private StandardRelationNewStandardService standardRelationNewStandardService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    public void sendMatchBeginToOddsAdmin(String linkId, Long standardMatchInfoId) {
        linkId = linkId + "_" + standardMatchInfoId;
        sendMatchRreOddsAdmin(linkId, standardMatchInfoId);
        sendMatchRreOddsByAO(linkId, standardMatchInfoId);
        //测试联赛
        StandardRelationNewStandard standardRelationNewStandard = standardRelationNewStandardService.getItem(standardMatchInfoId);
        if (standardRelationNewStandard == null) {
            return;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardRelationNewStandard.getNewStandardId());
        if (standardMatchInfo == null) {
            return;
        }
        log.info("::{}::测试联赛，自动构建滚球赔率,消息体:{}", linkId, standardMatchInfo.getId());
        sendMatchRreOddsAdmin(linkId, standardMatchInfo.getId());
    }

    public void sendMatchRreOddsAdmin(String linkId, Long standardMatchInfoId) {
        Request<Long> request = new Request<>();
        request.setLinkId(linkId);
        request.setData(standardMatchInfoId);
        MessageBuilder<Request<Long>> builder = MessageBuilder.withPayload(request)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.syncSend("MATCH_PRE_ODDS_ADMIN:" + standardMatchInfoId, builder.build(), SECOND_1 * THREE,ONE);
        log.info("::{}::通知赔率服务开始自动构建滚球赔率,topic=MATCH_PRE_ODDS_ADMIN,消息体:{}", linkId, standardMatchInfoId);

    }


    public void sendMatchRreOddsByAO(String linkId, Long standardMatchInfoId) {
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standardMatchInfoId, DataSourceCodeEnum.AO.getCode());
        if(null == thirdMatchInfo){
            return;
        }
        JSONObject object = new JSONObject();
        object.put("aoMatchId",thirdMatchInfo.getThirdMatchSourceId());
        object.put("sportId",thirdMatchInfo.getSportId());
        object.put("status",1);
        Request<JSONObject> request = new Request<>();
        request.setLinkId(linkId);
        request.setData(object);
        MessageBuilder<Request<JSONObject>> builder = MessageBuilder.withPayload(request)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.syncSend("MATCH_PRE_ODDS_ADMIN_AO:" + standardMatchInfoId, builder.build(), SECOND_1 * THREE,ONE);
        log.info("::{}::通知赔率AO1开赛标识,topic=MATCH_PRE_ODDS_ADMIN_AO,消息体:{}", linkId, standardMatchInfoId);

    }

    public void sendCloseCategory2OddsAdmin(String linkId, Long standardMatchInfoId, List<String> categoryList) {
        Request<CloseCategoryDTO> request = new Request<>();
        CloseCategoryDTO closeCategoryDTO = new CloseCategoryDTO();
        closeCategoryDTO.setCategoryList(categoryList);
        closeCategoryDTO.setMatchId(standardMatchInfoId);
        request.setLinkId(linkId);
        request.setData(closeCategoryDTO);
        MessageBuilder<Request<CloseCategoryDTO>> builder = MessageBuilder.withPayload(request)
                .setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.send("CLOSE_CATEGORY_ODDS_ADMIN:" + standardMatchInfoId, builder.build());
        log.info("::{}::通知赔率服务下发玩法关盘,topic=CLOSE_CATEGORY_ODDS_ADMIN, 消息体:{}", linkId, JSON.toJSONString(request));
    }
}
