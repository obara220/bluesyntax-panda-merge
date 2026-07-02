package com.panda.merge.rocketmq.processor;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import com.panda.merge.util.CategoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
@Validated
public class A99OddsProcessor extends BaseProcessor {
    @Autowired
    private RedisService redisService;

    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;

    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private ConfigTradeTypeService configTradeTypeService;

    /**
     * 处理三方赛事内部编码
     * @param request
     */
    public void execute(@Valid Request<List<StandardMarketDataMessage>> request) {
        log.info("::{}::收到A99赔率", request.getLinkId());
        Long matchId = request.getData().get(0).getStandardMatchInfoId();
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo =
                standardMatchInfoService.getItem(matchId);
        if (standardMatchInfo == null) {
            log.info("::{}::收到A99赔率,标准赛事未找到，标准赛事id:{}", request.getLinkId(),matchId);
            return ;
        }

        //刷新开售缓存并返回最新开售信息
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.refreshCache(matchId);
        //赛事未开售
        if (standardSportMarketSell == null) {
            log.info("::{}::收到A99赔率 ,赛事未开售，标准赛事id：{}", request.getLinkId(), matchId);
            return ;
        }

        Set<Long> marketCategoryIdSet = request.getData().stream().map(e->e.getMarketCategoryId()).collect(Collectors.toSet());
        Map<Long, Integer> tradeTypeMap = configTradeTypeService.getItemByMatchAndCategorys(standardMatchInfo.getId().toString(), marketCategoryIdSet);
        //筛选出需要下发的玩法
        Set<Long> marketCategoryValid = new HashSet<>();
        for (Long marketCategory : marketCategoryIdSet) {
            if (tradeTypeMap.get(marketCategory) == null || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeTypeMap.get(marketCategory))) {
                marketCategoryValid.add(marketCategory);
            } else {
                log.info("::{}::M和A+模式判断，标准赛事id={},玩法id={},M和A+模式不下发赔率", request.getLinkId(),
                        standardMatchInfo.getId(), marketCategory);
            }
        }

        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = request.getData().stream().collect(Collectors.toMap(e->e.getRelationMarketId()+"", Function.identity()));
        // 关转封限定足球主玩法
        stringStandardMarketDataMessageMap.forEach((k,v)->{
            redisService.hDel(Constant.REDIS_KEY.THIRD_MARKET_HEAD_CLOSE + v.getStandardMatchInfoId(),v.getMarketCategoryId().toString());
            String marketKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + v.getStandardMatchInfoId() + "_" + v.getDataSourceCode() + "_" + v.getMarketCategoryId());
            //并发问题设置子玩法
            v.setChildMarketCategoryId(CategoryUtils.getChildCategoryId(request.getLinkId(), v.getMarketCategoryId(), v.getAddition1(), v.getAddition2(), v.getAddition3(), v.getAddition4(), v.getAddition5(), String.valueOf(v.getStandardMatchInfoId())));
            boolean flag = redisService.hSet(marketKey, v.getRelationMarketId().toString(), v, marketCacheTime(standardMatchInfo.getBeginTime()));

        });
        //--------操盘后台操作开关封锁,异步处理-----------
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId() + "_A99",request.getOddsSource(), request.getOperaterId(),standardMatchInfo, marketCategoryValid,
                stringStandardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());

    }

}
