package com.panda.merge.rocketmq.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;

import lombok.extern.slf4j.Slf4j;

/**
 * <Description> 处理开赛后的赔率及时下发<br>
 *
 * @author damian<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2022/6/4 <br>
 * @see com.panda.merge.rocketmq.processor <br>
 */
@Component
@Slf4j
@Validated
public class MatchOddsIssuedProcessor extends BaseProcessor {

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    public void processor(@Valid Request<Long> request) {
        StopWatch stopWatch = new StopWatch("STANDARD_MATCH_ODDS_ISSUED_" + UUIdUtils.getId());
        stopWatch.start();
        String linkId = request.getLinkId();
        log.info("::{}::开赛后的赔率及时下发开始", linkId);
        Long standaMatchInfoId = request.getData();
        //查找标准赛事
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standaMatchInfoId);
        if (null == standardMatchInfo) {
            log.info("::{}::标准赛事为空,标准赛事id={}", linkId, standaMatchInfoId);
            return;
        }
        //查找开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standaMatchInfoId);
        if (null == standardSportMarketSell) {
            log.info("::{}::未找到预开售信息,标准赛事id={}", linkId, standaMatchInfoId);
            return;
        }
        //获取缓存中的盘口数据
        Map<String, StandardMarketDataMessage> map = thirdMatchMarketProcessor.getStringStandardMarketDataMessageMap(new HashSet<>(),linkId, standardMatchInfo, standardSportMarketSell);
        if (CollectionUtils.isEmpty(map)) {
            log.info("::{}::当前赛事没有找到盘口,标准赛事id={}", linkId, standaMatchInfoId);
            return;
        }

        Set<Long> marketCategoryIdSet = new HashSet<Long>();
        StandardMarketDataMessage s;
        for (String key : map.keySet()) {
            s = map.get(key);
            if (!marketCategoryIdSet.contains(s.getMarketCategoryId())) {
                marketCategoryIdSet.add(s.getMarketCategoryId());
            }
        }
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(), request.getOperaterId(),standardMatchInfo, marketCategoryIdSet, map,
                TimeUtils.millsSecondsEast8ZoneGmt(), standardSportMarketSell, new HashMap<>());
        stopWatch.stop();
    }
}
