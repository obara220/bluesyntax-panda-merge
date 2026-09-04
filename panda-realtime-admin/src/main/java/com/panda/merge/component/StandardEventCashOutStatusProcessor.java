package com.panda.merge.component;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.StandardMatchMarketPreResultMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.producer.StandardMatchPreResultProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 需求：2323
 */
@Component
@Slf4j
public class StandardEventCashOutStatusProcessor {

    @Autowired
    private RedisService redisService;
    @Autowired
    private StandardMatchPreResultProducer standardMatchPreResultProducer;
    /**
     * 提前结算开关，false关，true开
     */
    @NacosValue(value = "${market.pre.switch}", autoRefreshed = true)
    private boolean marketPreSwitch;
    public static List<String> EVENT_CODE = Arrays.asList("possible_video_assistant_referee", "video_assistant_referee", "possible_var", "var_reason", "var_reviewing");

    public void cashOutStatusUnavailable(StandardMatchInfo standardMatchInfo, String newLinkId, String eventCode) {
        if (!StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())) {
            return;
        }
        if (!marketPreSwitch) {
            log.info("::{}::提前结算NACOS关,提前结算特殊事件不处理", newLinkId);
            return;
        }
        String eventKey = Constant.REDIS_KEY.STANDARD_EVENT_VR_CODE + standardMatchInfo.getId();
        //默认缓存1天
        redisService.set(eventKey, eventCode);
        if (!EVENT_CODE.contains(eventCode)) {
            return;
        }
        log.info("::{}::开始处理特殊事件关闭提前计算cashOutStatus,赛事id:{},事件:{},缓存key:{},缓存事件：{}",
                newLinkId, standardMatchInfo.getId(), eventCode, eventKey, redisService.get(eventKey));
        List<StandardMatchMarketPreResultMessage> sendStandardPreResultMessageList = new ArrayList<>();
        //提前结算概率标准盘口缓存 Map<标准盘口ID，标准提前结算盘口>
        String standardPreMarketKey = Constant.REDIS_KEY.STANDARD_MARKET_PRE_RESULT + standardMatchInfo.getId();
        Map<String, StandardMatchMarketPreResultMessage> standardMatchMarketPreResultMessageMap = redisService.hGetAll(standardPreMarketKey);
        if (!CollectionUtils.isEmpty(standardMatchMarketPreResultMessageMap)) {
            standardMatchMarketPreResultMessageMap.forEach((key, marketPreResultMessage) -> {
                marketPreResultMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                marketPreResultMessage.setSpread(0D);
                sendStandardPreResultMessageList.add(marketPreResultMessage);
            });
        }
        if (!CollectionUtils.isEmpty(sendStandardPreResultMessageList)) {
            standardMatchPreResultProducer.sendStandardMatchPreResult(newLinkId, standardMatchInfo, standardMatchInfo.getSportId(), sendStandardPreResultMessageList, sendStandardPreResultMessageList.get(0).getMatchPreStatus(), System.currentTimeMillis());
        }
        log.info("::{}::开始处理特殊事件关闭提前计算cashOutStatus,赛事id:{},事件:{},处理完成", newLinkId, standardMatchInfo.getId(), eventCode);
    }
}
