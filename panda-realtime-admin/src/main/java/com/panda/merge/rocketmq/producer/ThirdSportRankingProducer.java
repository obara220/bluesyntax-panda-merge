package com.panda.merge.rocketmq.producer;

import cn.hutool.json.JSONUtil;
import com.panda.merge.dto.Request;
import com.panda.merge.model.ThirdSportPlayerRanking;
import com.panda.merge.model.ThirdSportTeamRanking;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import static com.panda.merge.constant.ConstantSystem.*;

@Slf4j
@Component
public class ThirdSportRankingProducer {

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 推送球队排行榜单给比分网后台
     * @param linkId
     * @param item
     */
    public void pushThirdSportTeamRankingPLS(String linkId, String dataSourceCode, ThirdSportTeamRanking item) {
        Request<ThirdSportTeamRanking> request = new Request<>();
        request.setData(item);
        request.setLinkId(linkId);
        MessageBuilder<String> builder = MessageBuilder.withPayload(JSONUtil.toJsonStr(request)).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.syncSend(THIRD_SPORT_TEAM_RANKING_PLS +":"+ item.getThirdTeamSourceId(), builder.build(), SECOND_1 * THREE,ONE);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_SPORT_TEAM_RANKING_PLS+"】【{} : {}】推送球队排行榜单给比分网后台完成 id:{},thirdTeamSourceId:{}", dataSourceCode,linkId,item.getId(),item.getThirdTeamSourceId());
    }

    /**
     * 推送球员排行榜单给比分网后台
     * @param linkId
     * @param item
     */
    public void pushThirdSportPlayerRankingPLS(String linkId, String dataSourceCode, ThirdSportPlayerRanking item) {
        Request<ThirdSportPlayerRanking> request = new Request<>();
        request.setData(item);
        request.setLinkId(linkId);
        MessageBuilder<String> builder = MessageBuilder.withPayload(JSONUtil.toJsonStr(request)).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.syncSend(THIRD_SPORT_PLAYER_RANKING_PLS +":"+ item.getThirdPlayerSourceId(), builder.build(), SECOND_1 * THREE,ONE);
        log.info("【"+ PROJECT_ID_NOREALTIME +" ："+ THIRD_SPORT_PLAYER_RANKING_PLS+"】【{} : {}】推送球员排行榜单给比分网后台完成 id:{},thirdPlayerSourceId:{}", dataSourceCode,linkId,item.getId(),item.getThirdPlayerSourceId());
    }
}
