package com.panda.merge.odds.service;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * DiffService
 *
 * @description:
 * @date: 5/3/2025
 **/
@Service
@Slf4j
public class DiffService {

    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;

    @Autowired
    private ConfigMarketAutoDiffTradeService configMarketAutoDiffTradeService;

    @Autowired
    private ConfigCategoryAutoDiffTradeService configCategoryAutoDiffTradeService;

    @Autowired
    private ConfigPlaceNumAutoDiffTradeService configPlaceNumAutoDiffTradeService;

    @Autowired
    private ConfigMarketMarginGapService configMarketMarginGapService;

    @Autowired
    private ConfigMarketHeadGapService headGapService;

    @Autowired
    private RocketMQTemplate rocketMqTemplate;

    /**
     * 清除盘口水差、玩法水差、坑位水差，篮球的话还有盘口差
     * 清理独赢配置 清概率差，水差
     * @param linkId
     * @param standardMatchId
     * @param categoryList
     * @param sportId
     */
    public void delDiffByMatchIdAndCategoryList(String linkId, Long standardMatchId, List<Long> categoryList, Integer sportId) {
        if (CollectionUtils.isEmpty(categoryList)) {
            return;
        }
        if (StandardSportTypeEnum.FootBall.code.equals(sportId.longValue())) {
            ThirdMatchInfo aoMatchInfo = thirdMatchInfoService.getItem(standardMatchId, DataSourceCodeEnum.AO.code);
            if (null != aoMatchInfo) {
                sendClearAoDiffConfig(linkId, standardMatchId, aoMatchInfo.getThirdMatchSourceId(), categoryList);
            }
        }
        log.info("::{}::处理清除水差delDiffByMatchIdAndCategoryList,开始处理", linkId);
        CompletableFuture c1 = CompletableFuture.runAsync(() -> {
            configMarketAutoDiffTradeService.delDiffByMatchIdAndCategoryList(linkId, standardMatchId, categoryList);
        });
        CompletableFuture c2 = CompletableFuture.runAsync(() -> {
            headGapService.delCacheByCategoryIdList(linkId, standardMatchId, categoryList);
        });
        CompletableFuture c3 = CompletableFuture.runAsync(() -> {
            configCategoryAutoDiffTradeService.delDiffByMatchIdAndCategoryList(linkId, standardMatchId, categoryList);
        });
        CompletableFuture c4 = CompletableFuture.runAsync(() -> {
            configPlaceNumAutoDiffTradeService.delDiffByMatchIdAndCategoryList(linkId, standardMatchId, categoryList);
        });
        CompletableFuture c6 = CompletableFuture.runAsync(() -> {
            List<Long> thanThreeCategoryIds = getMoreCategoryId(categoryList, true);
            if (!CollectionUtils.isEmpty(thanThreeCategoryIds)) {
                configMarketMarginGapService.upProbabilityByMatchIdAndCategoryIdList(linkId, standardMatchId, thanThreeCategoryIds);
            }
        });
        CompletableFuture c7 = CompletableFuture.runAsync(() -> {
            List<Long> otherCategoryIds = getMoreCategoryId(categoryList, false);
            if (!CollectionUtils.isEmpty(otherCategoryIds)) {
                configMarketMarginGapService.updateByMatchIdAndCategoryList(linkId, standardMatchId, otherCategoryIds);
            }
        });
        CompletableFuture.allOf(c1, c2, c3, c4, c6, c7);
        log.info("::{}::处理清除水差delDiffByMatchIdAndCategoryList,处理完成", linkId);
    }


    /**
     * 赛事切换 玩法切换 清除AO配置
     *
     * @param linkId
     * @param standardMatchId
     * @param thirdMatchSourceId
     * @param categoryList
     */
    public void sendClearAoDiffConfig(String linkId, Long standardMatchId, String thirdMatchSourceId, List<Long> categoryList) {
        List<Long> categorys = new ArrayList<>();
        if (!CollectionUtils.isEmpty(categoryList)) {
            categorys = categoryList.stream()
                                    .collect(Collectors.toMap(e -> e, e -> 1, Integer::sum))
                                    .entrySet()
                                    .stream()
                                    .filter(e -> e.getValue() > 1)
                                    .map(Map.Entry::getKey)
                                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(categorys)) {
                return;
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("linkId", linkId);
        obj.put("standardMatchId", standardMatchId);
        obj.put("aoMatchId", thirdMatchSourceId);
        obj.put("categorys", categorys);
        MessageBuilder<JSONObject>
                builder = MessageBuilder.withPayload(obj).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.asyncSend("AO_DIFF_CONFIG_CLEAR:" + standardMatchId, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,AO_DIFF_CONFIG_CLEAR，send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "AO_MATCH_DIFF_CONFIG_CLEAR", throwable);
            }
        });
    }

    private List<Long> getMoreCategoryId(List<Long> categoryList, boolean isTrue) {
        //大于三项盘玩法 清除概率差 ,其他玩法 清除清概率差，水差
        List<Long> thanThreeCategoryIds = Collections.synchronizedList(new ArrayList());
        List<Long> otherCategoryIds = Collections.synchronizedList(new ArrayList());
        categoryList.forEach(categoryId -> {
            if (MarginCategoryConfig.THREE_CATEGORY.contains(categoryId)) {
                thanThreeCategoryIds.add(categoryId);
            } else {
                otherCategoryIds.add(categoryId);
            }
        });
        return isTrue ? thanThreeCategoryIds : otherCategoryIds;
    }


}
