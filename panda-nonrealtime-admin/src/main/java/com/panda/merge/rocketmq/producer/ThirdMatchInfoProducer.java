package com.panda.merge.rocketmq.producer;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.CommUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchInfoDetail;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.ThirdMatchInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 推送三方赛事给下游 <br>
 * @author   tell<br>
 * @since    2020年9月5日19:07:32<br>
 */
@Slf4j
@Component
public class ThirdMatchInfoProducer extends BaseProcessor {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    /**
     * 推送三方赛事给下游
     * @param linkId
     * @param item
     */
    public void pushQueueMatch(String linkId, DataSource dataSource, ThirdMatchInfo item) {
        //查询标准赛事开售信息 ，对应的三方开赛时间写入赛前转滚球缓存内，到开赛时间下发滚球标识
        refreshStandardMatchBeginTimeByThirdMatchInfo(linkId, item);
        MessageBuilder<String> builder = MessageBuilder.withPayload(CommUtils.getJsonById(item.getId())).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.syncSend(QUEUE_MATCH +":"+ item.getThirdMatchSourceId(), builder.build(), SECOND_1 * THREE,ONE);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【{} : {}】推送三方赛事给下游完成,id:{},thirdMatchSourceId:{}", dataSource.getCode(),linkId,item.getId(),item.getThirdMatchSourceId());
    }


    /**
     * 通知赛程服务指定赛事自动开售
     * @param linkId
     * @param item
     */
    public void pushReplayMatch(String linkId, DataSource dataSource, ThirdMatchInfo item) {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("id",item.getId());
        jsonObj.put("linkId",linkId);
        MessageBuilder<JSONObject> builder = MessageBuilder.withPayload(jsonObj).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.syncSend(THIRD_REPLAY_MATCH_INFO_API +":"+ item.getId(),builder.build(), SECOND_1 * THREE,ONE);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_REPLAY_MATCH_INFO_API+"】【{} : {}】推送三方重播赛事给下游完成,id:{}", dataSource.getCode(),linkId,item.getId());
    }

    /**
     * 推送三方赛事给比分网后台
     * @param linkId
     * @param item
     */
    public void pushThirdMatchPLS(String linkId, DataSource dataSource, ThirdMatchInfoDetail item) {
        Request<ThirdMatchInfoDetail> request = new Request<>();
        request.setData(item);
        request.setLinkId(linkId);
        MessageBuilder<String> builder = MessageBuilder.withPayload(JSONUtil.toJsonStr(request)).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.syncSend(THIRD_MATCH_INFO_PLS +":"+ item.getThirdMatchSourceId(), builder.build(), SECOND_1 * THREE,ONE);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_MATCH_INFO_API+"】【{} : {}】推送三方赛事给比分网后台完成【topic : "+ THIRD_MATCH_INFO_PLS +"】 ,id:{},thirdMatchSourceId:{}", dataSource.getCode(),linkId,item.getId(),item.getThirdMatchSourceId());
    }

    /**
     * 数据商变更赛事来源时有实时更新
     * @param linkId
     * @param item
     */
    public void pushDataSourceMatchSource(String linkId,  ThirdMatchInfo item) {
        Request<ThirdMatchInfo> request = new Request<>();
        request.setData(item);
        request.setLinkId(linkId);
        MessageBuilder<String> builder = MessageBuilder.withPayload(JSONUtil.toJsonStr(request)).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.syncSend(DATASOURCE_MATCH_SOURCE_CHANGE_TO_RISK +":"+ item.getThirdMatchSourceId(), builder.build(), SECOND_1 * THREE,ONE);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ DATASOURCE_MATCH_SOURCE_CHANGE_TO_RISK+"】【{}:推送数据商变更赛事来源时有实时更新完成【topic : "+ DATASOURCE_MATCH_SOURCE_CHANGE_TO_RISK +"】 ,id:{},thirdMatchSourceId:{}", linkId,item.getId(),item.getThirdMatchSourceId());
    }

}
