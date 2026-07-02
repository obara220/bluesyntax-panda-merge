package com.panda.merge.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.producer.ThirdSportMarketMergeProducer;

import lombok.extern.slf4j.Slf4j;

/**
 * TX 数据源，盘口位置处理类
 */
@Component
@Slf4j
public class TXMarketDataService {

    @Autowired
    public ThirdSportMarketMergeProducer thirdSportMarketMergeProducer;
    @Autowired
    public RedisService redisService;

    /**
     * 步骤1
     * 1.玩法分组、坑位分组
     * 2.获取每个坑位上修改时间最新的盘口，不是最新的盘口改为数据商关盘，修改坑位为999
     * 步骤2
     * 1.根据统一盘口ID 分组 （sendData）
     * 2.数据源状态存在 开关盘口数据 取开盘的盘口 ，两个以上的开盘取修改时间最新的
     * 3.数据源状态存在 关关的盘口数据 ，取修改时间最新的盘口数据
     *
     * @param linkId
     * @param standardMarketDataMessagesAUTO
     */
    public void txMarketMerge(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketDataMessagesAUTO) {
        log.info("::{}::TX盘口数据融合处理进入:{}", linkId, standardMarketDataMessagesAUTO.size());
        List<StandardMarketDataMessage> resultMarketMerge = new ArrayList<>();
        List<StandardMarketDataMessage> marketMerge = new ArrayList<>();
        List<StandardMarketDataMessage> collect = new ArrayList<>();
        //Step1:同坑位处理 玩法分组 ，再坑位分组
        Map<Long, List<StandardMarketDataMessage>> marketCategoryGrop = standardMarketDataMessagesAUTO.stream().filter(e -> e.getDataSourceCode().equals(DataSourceCodeEnum.TX.code)).collect(Collectors.groupingBy(StandardMarketDataMessage::getChildMarketCategoryId));
        if (CollectionUtils.isEmpty(marketCategoryGrop)) {
            log.info("::{}::非TX盘口数据融合处理条数为：{}", linkId, standardMarketDataMessagesAUTO.size());
            return;
        }
        Set<Long> changeCategoryIdS = new HashSet<>();
        //数据太多，不打印投注项
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        filter.getExcludes().add("marketOddsList");
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : marketCategoryGrop.entrySet()) {
            List<StandardMarketDataMessage> marketDataMessages = entry.getValue();
            collect.addAll(marketDataMessages);
            changeCategoryIdS.add(entry.getKey());
            //相同坑位分组
            Map<Integer, List<StandardMarketDataMessage>> placeNumGrop = marketDataMessages.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getPlaceNum));
            for (Map.Entry<Integer, List<StandardMarketDataMessage>> placeEntry : placeNumGrop.entrySet()) {
                List<StandardMarketDataMessage> placeMarketDataMessages = placeEntry.getValue();
                //TX旧数据赋值
                for (StandardMarketDataMessage market : placeMarketDataMessages) {
                    if (StringUtils.isBlank(market.getSendData()) || "''".equals(market.getSendData())) {
                        market.setSendData(market.getRelationMarketId().toString());
                        if (!CollectionUtils.isEmpty(market.getMarketOddsList())) {
                            for (StandardMarketOddsDataMessage marketOdds : market.getMarketOddsList()) {
                                marketOdds.setRemark(marketOdds.getRelationMarketOddsId().toString());
                            }
                        }
                    }
                }
                //Step1:相同坑位根据盘口修改时间升序，第一个盘口不做状态处理其他盘口改为关盘
                List<StandardMarketDataMessage> resultPlaceMarketDataMessages = placeMarketDataMessages.stream().sorted(Comparator.comparing(StandardMarketDataMessage::getModifyTime).reversed()).collect(Collectors.toList());
                log.info("::{}::标准玩法:{},TX相同坑位:{},相同坑位根据盘口修改时间升序:{}", linkId, entry.getKey(), placeEntry.getKey(), JSONObject.toJSONString(resultPlaceMarketDataMessages, filter));
                int num = 0;
                for (StandardMarketDataMessage placeMarket : resultPlaceMarketDataMessages) {
                    if (num == 0) {
                        if (placeMarket.getStatus().equals(Constant.SPORT_MARKET.STATUS.LOSE)) {
                            placeMarket.setPlaceNum(999);
                        }
                        resultMarketMerge.add(placeMarket);
                        log.info("::{}::三方盘口ID:{},TX盘口相同坑位最新盘口", linkId, placeMarket.getThirdMarketSourceId());
                    }
                    num++;
                }
            }
        }
        //Step1:根据同一盘口ID分组
        Map<String, List<StandardMarketDataMessage>> relationMarketGrop = resultMarketMerge.stream()
                .filter(e -> StringUtils.isNotBlank(e.getSendData())).peek(m -> m.setRelationMarketId(Long.valueOf(m.getSendData()))).collect(Collectors.groupingBy(StandardMarketDataMessage::getSendData));
        log.info("::{}::TX盘口相同坑位根据时间戳处理结果:{}", linkId, JSONObject.toJSONString(relationMarketGrop, filter));
        if (!CollectionUtils.isEmpty(relationMarketGrop)) {
            for (Map.Entry<String, List<StandardMarketDataMessage>> entry : relationMarketGrop.entrySet()) {
                String relationMarketId = entry.getKey();
                List<StandardMarketDataMessage> relationMarket = entry.getValue();
                //Step2:数据源状态存在 开关盘口数据 取开盘的盘口 ，如果有多条开盘数据 取最新的修改时间对应的盘口数据
                List<StandardMarketDataMessage> openMarket = relationMarket.stream().filter(e -> Constant.SPORT_MARKET.STATUS.ACTIVE.equals(e.getThirdMarketSourceStatus())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(openMarket)) {
                    //说明有两个以上的开 取修改时间最新的
                    if (openMarket.size() > 1) {
                        relationMarket = relationMarket.stream().sorted(Comparator.comparing(StandardMarketDataMessage::getModifyTime).reversed()).collect(Collectors.toList());
                        log.info("::{}::统一盘口ID:{},TX盘口数据取开盘或者关盘的最新修改时间数据:{}", linkId, relationMarketId, JSON.toJSONString(relationMarket.get(0)));
                        marketMerge.add(relationMarket.get(0));
                    } else {
                        //只有一个开
                        log.info("::{}::统一盘口ID:{},TX盘口数据取开盘数据:{}", linkId, relationMarketId, JSON.toJSONString(openMarket.get(0)));
                        marketMerge.add(openMarket.get(0));
                    }
                    continue;
                }
                //Step3:数据源状态存在关关的盘口数据 ，取修改时间最新的盘口数据
                relationMarket = relationMarket.stream().sorted(Comparator.comparing(StandardMarketDataMessage::getModifyTime).reversed()).collect(Collectors.toList());
                log.info("::{}::统一盘口ID:{},TX盘口数据取开盘或者关盘的最新修改时间数据:{}", linkId, relationMarketId, JSON.toJSONString(relationMarket.get(0)));
                marketMerge.add(relationMarket.get(0));
            }
            if (!CollectionUtils.isEmpty(collect)) {
                log.info("::{}::总数:{},TX处理盘口数:{},处理后盘口数:{},", linkId, standardMarketDataMessagesAUTO.size(), collect.size(), marketMerge.size());
                //清除TX 非融合前的盘口
                standardMarketDataMessagesAUTO.removeAll(collect);
                //添加融合后的盘口
                standardMarketDataMessagesAUTO.addAll(marketMerge);
                //只取有改变的多玩法数据源 TX盘口数据 根据统一盘口ID去重 相同统一盘口ID取开盘的
                Map<Long, List<StandardMarketDataMessage>> changeMarketDataMessage = standardMarketDataMessagesAUTO.stream()
                        .filter(e -> changeCategoryIdS.contains(e.getChildMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getRelationMarketId));
                for (Map.Entry<Long, List<StandardMarketDataMessage>> placeEntry : changeMarketDataMessage.entrySet()) {
                    List<StandardMarketDataMessage> marketDataMessages = placeEntry.getValue();
                    standardMarketDataMessagesAUTO.removeAll(marketDataMessages);
                    List<StandardMarketDataMessage> openMarketData = marketDataMessages.stream().filter(e -> e.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.ACTIVE)).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(openMarketData)) {
                        openMarketData = marketDataMessages.stream().sorted(Comparator.comparing(StandardMarketDataMessage::getModifyTime).reversed()).collect(Collectors.toList());
                    }
                    standardMarketDataMessagesAUTO.add(openMarketData.get(0));
                    StandardMarketDataMessage standardMarketDataMessage = openMarketData.get(0);
                    setBall(linkId, standardMatchInfo, standardMarketDataMessage, standardMarketDataMessage.getAddition5());
                }
                //TX坑位最后排序
                Map<Long, List<StandardMarketDataMessage>> marketCategoryGropfinal = standardMarketDataMessagesAUTO.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getChildMarketCategoryId));
                for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : marketCategoryGropfinal.entrySet()) {
                    List<StandardMarketDataMessage> marketDataMessages = entry.getValue();
                    txMarketSort(linkId, entry.getKey(), standardMatchInfo, marketDataMessages);
                }
            }
        }
    }

    /**
     * TX A模式 ，A+模式 设置缓存
     * Map<玩法ID，球头>
     *
     * @param standardMatchInfo
     * @param standardMarketDataMessage
     */
    private void setBall(String linkId, StandardMatchInfo standardMatchInfo, StandardMarketDataMessage standardMarketDataMessage, String addition1) {
        if (StringUtils.isEmpty(addition1)) {
            return;
        }
        //TX篮球设置球头值缓存 Map<玩法ID，球头>
        if (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId())
                && DataSourceCodeEnum.TX.code.equals(standardMarketDataMessage.getDataSourceCode())
                && standardMarketDataMessage.getPlaceNum() == 1) {
            Long marketCategoryId = standardMarketDataMessage.getMarketCategoryId();
            log.info("::{}::TX缓存最新球头,赛事ID:{},玩法:{},球头值:{},", linkId, standardMatchInfo.getId(), marketCategoryId, addition1);
            String setBall = Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_BALL + standardMatchInfo.getId();
            redisService.hSet(setBall, String.valueOf(marketCategoryId), addition1, RedisConfig.REDIS_HOUR_TIME);
        }
    }

    /**
     * TX 排序
     * 坑位水差设置在坑位：1
     * 坑位：1关 坑位：2开  坑位：3开
     * 坑位：2 - > 1 用坑位1水差  坑位：3 - > 2
     */
    public void txMarketSort(String linkId, Long marketCategoryId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketDataMessages) {
        if (!MarginCategoryConfig.TX_SORT_SPORT.contains(standardMatchInfo.getSportId())) {
            return;
        }
        int a = 1;
        //其他数据源开盘条数，只会在切换数据源才处理
        List<StandardMarketDataMessage> standardMarketDataMessageOther = standardMarketDataMessages.stream().filter(e ->
                !DataSourceCodeEnum.TX.code.equals(e.getDataSourceCode()) && e.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(standardMarketDataMessageOther)) {
            a = standardMarketDataMessageOther.size() + 1;
        }
        //只处理TX 开封盘口
        List<StandardMarketDataMessage> txStandardMarketDataMessage = standardMarketDataMessages.stream().filter(e ->
                DataSourceCodeEnum.TX.code.equals(e.getDataSourceCode()) && e.getThirdMarketSourceStatus() < Constant.SPORT_MARKET.STATUS.DEACTIVATED).collect(Collectors.toList());


        int b = txMarketSortDispose(linkId, marketCategoryId, standardMatchInfo, a, txStandardMarketDataMessage, "处理开封盘口");

        //处理TX 非开封盘口
        List<StandardMarketDataMessage> txStandardMarketDataMessageError = standardMarketDataMessages.stream().filter(e ->
                DataSourceCodeEnum.TX.code.equals(e.getDataSourceCode()) && e.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(txStandardMarketDataMessageError)) {
            return;
        }
        txMarketSortDispose(linkId, marketCategoryId, standardMatchInfo, b, txStandardMarketDataMessageError, "处理非开封盘口");
    }

    public int txMarketSortDispose(String linkId, Long marketCategoryId, StandardMatchInfo standardMatchInfo, int a,
                                   List<StandardMarketDataMessage> standardMarketDataMessage, String remark) {
        if (CollectionUtils.isEmpty(standardMarketDataMessage)) {
            return a;
        }
        //TX位置分组,
        Map<Integer, List<StandardMarketDataMessage>> groupPlace = standardMarketDataMessage.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getPlaceNum));
        //Map<TX坑位, 排序后坑位>
        Map<Integer, Integer> sortPlaceMap = new HashMap<Integer, Integer>();
        Set<Integer> placeNums = groupPlace.keySet();
        for (int placeNum : placeNums) {
            sortPlaceMap.put(placeNum, a);
            a++;
        }
        log.info("::{}::{},TX位置排序,赛事ID:{},玩法:{},排序信息:{},处理条数:{}",
                linkId, remark, standardMatchInfo.getId(), marketCategoryId, JSONObject.toJSONString(sortPlaceMap), standardMarketDataMessage.size());
        standardMarketDataMessage.forEach(t -> {
            t.setTxPlaceNum(sortPlaceMap.get(t.getPlaceNum()));
        });
        return a;
    }

}
