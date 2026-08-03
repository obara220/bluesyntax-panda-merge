package com.panda.merge.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMatchSwitchStatusMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.RocketMQDelegate;
import com.panda.merge.service.ConfigMarketAutoDiffTradeService;
import com.panda.merge.service.ConfigMarketHeadGapService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author : Bevan
 * @project Name : panda-merge
 * @package Name : com.panda.merge.rocketmq.producer
 * @description : TODO
 * @date: 2020-12-02 15:08
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Component
public class StandardMatchSwitchStatusProducer extends BaseProcessor {

    @Autowired
    private RocketMQDelegate mqDelegate;

    @Autowired
    private RocketMQTemplate mqTemplate;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private ConfigMarketAutoDiffTradeService diffTradeService;

    @Autowired
    private ConfigMarketHeadGapService headGapService;

    /**
     * 收到滚球赔率 下发赛前切换状态
     *
     * @param linkId
     * @param standardMatchInfoId 标准赛事ID
     * @param status              比赛类型
     * @param dataSourceCode      数据源
     * @param sportId             运动ID
     * @param
     */
    public void standardMatchSwitchStatus(String linkId, Long standardMatchInfoId, Integer status, String dataSourceCode, Long sportId, boolean isTrue, Integer advance) {
        //更新标准赛事 oddsLive
        StandardMatchInfo upStandardMatchInfo = new StandardMatchInfo();
        upStandardMatchInfo.setId(standardMatchInfoId);
        upStandardMatchInfo.setOddsLive(status);
        upStandardMatchInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        if (isTrue) {
            upStandardMatchInfo.setMatchStatus(MatchStatusEnum.Not_Started.value);
        }
        standardMatchInfoService.updateByPrimaryKeySelective(upStandardMatchInfo);
        //清除水差 盘口差
        delDiffByMatchInfoId(linkId, standardMatchInfoId, sportId);
        try{
            Request<StandardMatchSwitchStatusMessage> requestMsg = convert(linkId, standardMatchInfoId, status, dataSourceCode, "0",advance);
            MessageBuilder<Request<StandardMatchSwitchStatusMessage>> builder = MessageBuilder.withPayload(requestMsg).setHeader(MessageConst.PROPERTY_KEYS, linkId);
            mqDelegate.send("STANDARD_MATCH_SWITCH_STATUS:" + standardMatchInfoId, builder.build(), standardMatchInfoId);
            log.info(" ::{}::开始组装标准赛事赛前切换状态并下发,topic:STANDARD_MATCH_SWITCH_STATUS,request:{}", linkId, JSON.toJSONString(requestMsg));
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(standardMatchInfoId, DataSourceCodeEnum.F01.getCode());
            if (null != thirdMatchInfo) {
                Request<StandardMatchSwitchStatusMessage> requestF01Msg = convert(linkId, standardMatchInfoId, status, dataSourceCode, thirdMatchInfo.getThirdMatchSourceId(),advance);
                MessageBuilder<Request<StandardMatchSwitchStatusMessage>> builderF01 = MessageBuilder.withPayload(requestF01Msg).setHeader(MessageConst.PROPERTY_KEYS, linkId);
                mqTemplate.send("STANDARD_ODDS_LIVE_F01_STATUS:" + standardMatchInfoId, builderF01.build());
                log.info(" ::{}::开始组装标准赛事F01赛前切换状态并下发,topic:STANDARD_ODDS_LIVE_F01_STATUS,request:{}", linkId, JSON.toJSONString(requestMsg));
            }
        }catch (Exception e){
            log.error(" ::{}::开始组装标准赛事赛前切换状态并下发,topic:STANDARD_MATCH_SWITCH_STATUS,request:{}", linkId, e);
        }
    }

    private static Request<StandardMatchSwitchStatusMessage> convert(String linkId, Long standardMatchInfoId, Integer status, String dataSourceCode,
                                                                     String thirdMatchId,Integer advance) {
        StandardMatchSwitchStatusMessage statusMessage = new StandardMatchSwitchStatusMessage();
        statusMessage.setStandardMatchId(standardMatchInfoId);
        statusMessage.setOddsLive(status);
        statusMessage.setDataSourceCode(dataSourceCode);
        statusMessage.setThirdMatchId(thirdMatchId);
        statusMessage.setAdvance(advance);
        Request<StandardMatchSwitchStatusMessage> requestMsg = new Request<>();
        requestMsg.setData(statusMessage);
        requestMsg.setLinkId(linkId);
        requestMsg.setDataSourceTime(TimeUtils.millsSecondsEast8ZoneGmt());
        return requestMsg;
    }
}
