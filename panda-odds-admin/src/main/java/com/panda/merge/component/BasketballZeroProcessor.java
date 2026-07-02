package com.panda.merge.component;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.ConfigMarketCategoryHead;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarket;
import com.panda.merge.model.StandardSportMarketOdds;
import com.panda.merge.rocketmq.producer.DataMerchantBaffleProducer;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 需求：2249【篮球/综合操盘】篮球0盘替代，因和独赢盘相同
 * 1.篮球所有让分玩法的，0平的取消，别的盘口取代
 * 玩法：39 全场让分，19 上半场让分，46 第1节让分，52 第2节让分，58 第3节让分，64 第4节让分，143 下半场让分
 */
@Component
@Slf4j
public class BasketballZeroProcessor extends BaseProcessor {

    @Autowired
    private ConfigMarketHeadGapService configMarketHeadGapService;
    @Autowired
    private StandardSportMarketNewService standardSportMarketService;
    @Autowired
    private StandardSportMarketOddsNewService standardSportMarketOddsService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private DataMerchantBaffleProducer dataMerchantBaffleProducer;

    /**
     * 绝对值主球头 ，如上一次盘口1.5 ，计算后盘口-1.5封一次对应的独赢
     */
    public static List<String> HANDICAP_MARKET_DISPOSE = Arrays.asList("0.5", "1", "1.5");

    public void zeroProcessor(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketDataMessageList) {
        if (!standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Basketball.getCode())) {
            return;
        }
        Map<Long, List<StandardMarketDataMessage>> standardMarketDataMessageMap = standardMarketDataMessageList.stream().filter(s -> MarginCategoryConfig.BASKETBALL_HEAD_CATEGORY.contains(s.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));
        if (MapUtils.isEmpty(standardMarketDataMessageMap)) {
            return;
        }
        Set<Long> riskCategorySet = new HashSet<>();
        Long standardMatchId = standardMatchInfo.getId();
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : standardMarketDataMessageMap.entrySet()) {
            Long marketCategoryId = entry.getKey();
            List<StandardMarketDataMessage> standardMarketDataMessages = entry.getValue();
            String key = Constant.REDIS_KEY.RONGHE_HEAD_HANDICAP_ADD1 + standardMatchInfo.getId();
            //A01特殊处理球头缓存
            standardMarketDataMessageA01Processor(linkId, key, standardMarketDataMessages);
            for (StandardMarketDataMessage standardMarketDataMessage : standardMarketDataMessages) {
                //根据赛事、玩法id查询盘口差配置数据
                ConfigMarketCategoryHead configMarketCategoryHead = configMarketHeadGapService.getItemCache(linkId, standardMatchId, standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getChildMarketCategoryId());
                //定义盘口差
                Double configMarketHeadGap = 0D;
                if (null != configMarketCategoryHead && standardMarketDataMessage.getMarketType().equals(configMarketCategoryHead.getMarketType()) && configMarketCategoryHead.getMarketHeadGap() != 0) {
                    configMarketHeadGap = configMarketCategoryHead.getMarketHeadGap();
                }
                if (configMarketHeadGap == 0D) {
                    continue;
                }
                if (null == configMarketCategoryHead.getMarketHeadGapInitial()) {
                    configMarketCategoryHead.setMarketHeadGapInitial(1D);
                    log.info("::{}::篮球平盘逻辑,赛事ID:{},玩法ID:{},盘口差最初值不存在默认1", linkId, standardMatchId, marketCategoryId);
                }
                if (MarginCategoryConfig.BASKETBALL_ADDTION2_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId())) {
                    //add2不存在，赋值原始add1，add2加入盘口差计算
                    if (StringUtils.isBlank(standardMarketDataMessage.getAddition2())) {
                        standardMarketDataMessage.setAddition2(standardMarketDataMessage.getAddition1());
                    }
                    Double marketHead2 = new BigDecimal(Double.parseDouble(standardMarketDataMessage.getAddition2())).add(new BigDecimal(Double.toString(configMarketHeadGap))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    standardMarketDataMessage.setAddition2(String.valueOf(marketHead2).replace(".0", ""));
                }
                //数据源原始球头
                standardMarketDataMessage.setAddition5(standardMarketDataMessage.getAddition1());
                BigDecimal marketHeadInitial = new BigDecimal(configMarketCategoryHead.getMarketHeadGapInitial());
                int tailNum = marketHeadInitial.remainder(BigDecimal.ONE).movePointRight(marketHeadInitial.scale()).abs().intValue();
                log.info("::{}::篮球平盘逻辑,赛事ID:{},玩法ID:{},盘口差最初值:{},盘口差:{},盘口数据:{}", linkId, standardMatchId, marketCategoryId, marketHeadInitial, configMarketHeadGap, JSONObject.toJSONString(standardMarketDataMessage));
                if (marketCategoryId == 39L) {
                    //开平盘
                    if (tailNum == 5) {
                        specialHeadHandicapZero(linkId, standardMarketDataMessage, configMarketHeadGap);
                    } else {
                        specialHeadHandicapNotZero(linkId, standardMarketDataMessage, configMarketHeadGap);
                    }
                } else {
                    //开平盘
                    if (tailNum == 5) {
                        specialHeadHalfTimeZero(linkId, standardMarketDataMessage, configMarketHeadGap);
                    } else {
                        specialHeadHalfTimeNotZero(linkId, standardMarketDataMessage, configMarketHeadGap);
                    }
                }
                if (standardMarketDataMessage.getPlaceNum() == 1
                        && standardMarketDataMessage.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.ACTIVE)) {
                    headChange(linkId, key, standardMarketDataMessage, riskCategorySet);
                }
                //设置盘口差
                standardMarketDataMessage.setMarketHeadGap(configMarketHeadGap);
                //通过附加字段，找到新的统一盘口id，统一投注项id
                StandardSportMarket standardSportMarket = new StandardSportMarket();
                BeanUtils.copyProperties(standardMarketDataMessage, standardSportMarket);
                //T01单独处理盘口ID
                if (standardMarketDataMessage.getDataSourceCode().equals(DataSourceCodeEnum.TX.getCode())) {
                    standardMarketDataMessage.setSendData(standardSportMarketService.createRelationMarketId(linkId, standardSportMarket).toString());
                } else {
                    standardMarketDataMessage.setRelationMarketId(standardSportMarketService.getRelationMarketId(linkId, standardSportMarket));
                }
                //关盘的盘口存在没有投注项的可能
                if (!CollectionUtils.isEmpty(standardMarketDataMessage.getMarketOddsList())) {
                    standardMarketDataMessage.getMarketOddsList().forEach(standardMarketOddsDataMessage -> {
                        StandardSportMarketOdds standardSportMarketOdds = new StandardSportMarketOdds();
                        standardMarketOddsDataMessage.setRelationMarketId(standardMarketDataMessage.getRelationMarketId());
                        BeanUtils.copyProperties(standardMarketOddsDataMessage, standardSportMarketOdds);
                        standardMarketOddsDataMessage.setRelationMarketOddsId(standardSportMarketOddsService.getRelationMarketOddsId(standardSportMarketOdds, standardMarketDataMessage.getMarketCategoryId()));
                        //T01单独处理盘口投注项ID
                        if (standardMarketDataMessage.getDataSourceCode().equals(DataSourceCodeEnum.TX.getCode())) {
                            standardMarketOddsDataMessage.setRemark(standardSportMarketOddsService.adjustmentTxCreateRelationMarketOddsId(standardSportMarketOdds, standardMarketDataMessage));
                        }
                    });
                }
                //只记录主盘口变动的盘口差,判断数据源开
                if (standardMarketDataMessage.getPlaceNum() == 1
                        && standardMarketDataMessage.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.ACTIVE)) {
                    redisService.hSet(key, standardMarketDataMessage.getThirdMarketSourceId(), standardMarketDataMessage.getAddition1(), standardMatchInfo.getBeginTime());
                }
            }
        }

        if (!CollectionUtils.isEmpty(riskCategorySet)) {
            log.info("::{}::篮球平盘逻辑,球头改变下发独赢封盘最终玩法集合,赛事ID:{},封盘玩法集合:{},", linkId, standardMatchInfo.getId(), riskCategorySet);
            dataMerchantBaffleProducer.sendCategoryListToRiskMQ(linkId + "_riskCategorySet_1", standardMatchInfo.getId(), standardMatchInfo.getSportId(), riskCategorySet, 11);

        }
    }

    /**
     * 全场让球 开平盘 小于等于0.5往后减盘口差2
     *
     * @param standardMarketDataMessage
     * @param configMarketHeadGap
     */
    private void specialHeadHandicapZero(String linkId, StandardMarketDataMessage standardMarketDataMessage, Double configMarketHeadGap) {
        Double marketHeadNew = 0D;
        //计算出球头
        Double marketHead = new BigDecimal(Double.parseDouble(standardMarketDataMessage.getAddition1())).add(new BigDecimal(Double.toString(configMarketHeadGap))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        //球头小于0.5 , 再减1.5盘口差
        if (Double.parseDouble(standardMarketDataMessage.getAddition1()) < 0) {
            if (Math.abs(Double.parseDouble(standardMarketDataMessage.getAddition1())) <= Math.abs(configMarketHeadGap)) {
                if (marketHead >= 0D) {
                    if (configMarketHeadGap < 0) {
                        marketHeadNew = new BigDecimal(marketHead).subtract(new BigDecimal(Double.toString(1.5))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    } else {
                        marketHeadNew = new BigDecimal(marketHead).add(new BigDecimal(Double.toString(1.5))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    }
                }
            } else if (marketHead == -0.5D) {
                marketHeadNew = 1D;
            }
        } else {
            if (marketHead <= 0D) {
                if (configMarketHeadGap < 0) {
                    marketHeadNew = new BigDecimal(marketHead).subtract(new BigDecimal(Double.toString(1.5))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                } else {
                    marketHeadNew = new BigDecimal(marketHead).add(new BigDecimal(Double.toString(1.5))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
            }
            if (marketHead == 0.5D) {
                marketHeadNew = -1D;
            }
        }
        marketHeadNew = marketHeadNew != 0D ? marketHeadNew : marketHead;
        log.info("::{}::篮球平盘逻辑,全场让球不开平盘,玩法ID:{},盘口id:{},三方盘口id:{},盘口差:{},第一次计算出球头:{},第二次计算出球头:{}", linkId, standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getThirdMarketSourceId(), configMarketHeadGap, marketHead, marketHeadNew);
        standardMarketDataMessage.setMarketOddsValue(Math.abs(marketHeadNew));
        standardMarketDataMessage.setAddition1(String.valueOf(marketHeadNew).replace(".0", ""));
    }

    /**
     * 全场让球 不开平盘 小于等于0.5往后减盘口差 1.5
     *
     * @param standardMarketDataMessage
     * @param configMarketHeadGap
     */
    private void specialHeadHandicapNotZero(String linkId, StandardMarketDataMessage standardMarketDataMessage, Double configMarketHeadGap) {
        Double marketHeadNew = 0D;
        //计算出球头
        Double marketHead = new BigDecimal(Double.parseDouble(standardMarketDataMessage.getAddition1())).add(new BigDecimal(Double.toString(configMarketHeadGap))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        //球头小于0.5 , 再减2盘口差
        if (Double.parseDouble(standardMarketDataMessage.getAddition1()) < 0) {
            if (Math.abs(Double.parseDouble(standardMarketDataMessage.getAddition1())) <= Math.abs(configMarketHeadGap)) {
                if (marketHead >= 0.5D) {
                    if (configMarketHeadGap < 0) {
                        marketHeadNew = new BigDecimal(marketHead).subtract(new BigDecimal(Double.toString(2))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    } else {
                        marketHeadNew = new BigDecimal(marketHead).add(new BigDecimal(Double.toString(2))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    }
                }
            } else if (marketHead == -0.5D) {
                marketHeadNew = 1.5D;
            }
        } else {
            if (marketHead <= 0.5D) {
                if (configMarketHeadGap < 0) {
                    marketHeadNew = new BigDecimal(marketHead).subtract(new BigDecimal(Double.toString(2))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                } else {
                    marketHeadNew = new BigDecimal(marketHead).add(new BigDecimal(Double.toString(2))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
            } else if (marketHead == 0.5D) {
                marketHeadNew = -1.5D;
            }
        }
        marketHeadNew = marketHeadNew != 0D ? marketHeadNew : marketHead;

        log.info("::{}::篮球平盘逻辑,全场让球不开平盘,玩法ID:{},盘口id:{},三方盘口id:{},盘口差:{},第一次计算出球头:{},第二次计算出球头:{}", linkId, standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getThirdMarketSourceId(), configMarketHeadGap, marketHead, marketHeadNew);
        standardMarketDataMessage.setMarketOddsValue(Math.abs(marketHeadNew));
        standardMarketDataMessage.setAddition1(String.valueOf(marketHeadNew).replace(".0", ""));
    }

    /**
     * 半场&单节玩法 开平盘
     *
     * @param standardMarketDataMessage
     * @param configMarketHeadGap
     */
    private static void specialHeadHalfTimeZero(String linkId, StandardMarketDataMessage standardMarketDataMessage, Double configMarketHeadGap) {
        Double marketHeadNew = 0D;
        //计算出球头
        Double marketHead = new BigDecimal(Double.parseDouble(standardMarketDataMessage.getAddition1())).add(new BigDecimal(Double.toString(configMarketHeadGap))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        //球头小于0.5 , 再减0.5盘口差

        if (Double.parseDouble(standardMarketDataMessage.getAddition1()) < 0) {
            if (Math.abs(Double.parseDouble(standardMarketDataMessage.getAddition1())) <= Math.abs(configMarketHeadGap)) {
                if (marketHead >= 0D) {
                    if (configMarketHeadGap < 0) {
                        marketHeadNew = new BigDecimal(marketHead).subtract(new BigDecimal(Double.toString(0.5))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    } else {
                        marketHeadNew = new BigDecimal(marketHead).add(new BigDecimal(Double.toString(0.5))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                    }
                }
            }
        } else {
            if (marketHead <= 0D) {
                if (configMarketHeadGap < 0) {
                    marketHeadNew = new BigDecimal(marketHead).subtract(new BigDecimal(Double.toString(0.5))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                } else {
                    marketHeadNew = new BigDecimal(marketHead).add(new BigDecimal(Double.toString(0.5))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
            }
        }
        marketHeadNew = marketHeadNew != 0D ? marketHeadNew : marketHead;
        log.info("::{}::篮球平盘逻辑,半场&单节玩法开平盘,玩法ID:{},盘口id:{},盘口差:{},第一次计算出球头:{},第二次计算出球头:{}", linkId, standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getRelationMarketId(), configMarketHeadGap, marketHead, marketHeadNew);

        standardMarketDataMessage.setMarketOddsValue(Math.abs(marketHeadNew));
        standardMarketDataMessage.setAddition1(String.valueOf(marketHeadNew).replace(".0", ""));

    }

    /**
     * 半场&单节玩法 不开平盘
     *
     * @param standardMarketDataMessage
     * @param configMarketHeadGap
     */
    private static void specialHeadHalfTimeNotZero(String linkId, StandardMarketDataMessage standardMarketDataMessage, Double configMarketHeadGap) {
        //计算出球头
        Double marketHeadNew = new BigDecimal(Double.parseDouble(standardMarketDataMessage.getAddition1())).add(new BigDecimal(Double.toString(configMarketHeadGap))).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        standardMarketDataMessage.setMarketOddsValue(Math.abs(marketHeadNew));
        standardMarketDataMessage.setAddition1(String.valueOf(marketHeadNew).replace(".0", ""));
    }

    /**
     * 球头改变下发独赢封盘
     *
     * @param linkId
     * @param standardMarketDataMessage
     * @param
     */
    private void headChange(String linkId, String key, StandardMarketDataMessage standardMarketDataMessage, Set<Long> riskCategorySet) {
        Object o = redisService.hGet(key, standardMarketDataMessage.getThirdMarketSourceId());
        if (o == null) {
            return;
        }
        Double previousAdd1 = Math.abs(Double.parseDouble(o.toString()));
        Double newAdd1 = Math.abs(Double.parseDouble(standardMarketDataMessage.getAddition1()));
        if (previousAdd1.equals(newAdd1) && HANDICAP_MARKET_DISPOSE.contains(String.valueOf(previousAdd1).replace(".0", ""))) {
            log.info("::{}::篮球平盘逻辑,球头改变下发独赢封盘,玩法ID:{},三方盘口id:{},盘口id:{},缓存球头:{},最新球头:{}", linkId, standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getThirdMarketSourceId(), standardMarketDataMessage.getRelationMarketId(), o.toString(), standardMarketDataMessage.getAddition1());
            riskCategorySet.add(MarginCategoryConfig.HANDICAP_WINNER_MAP.get(standardMarketDataMessage.getMarketCategoryId()));
        }
    }

    /**
     * A01删除坑位不是最新的缓存 ，记录让分跳动后的盘口值
     * @param linkId
     * @param redisKey
     * @param standardMarketDataMessages
     */
    private void standardMarketDataMessageA01Processor(String linkId, String redisKey, List<StandardMarketDataMessage> standardMarketDataMessages) {
        Map<Long, List<StandardMarketDataMessage>> marketCategoryGrop = standardMarketDataMessages.stream().filter(e ->
                e.getDataSourceCode().equals(DataSourceCodeEnum.AO.code) && MarginCategoryConfig.BASKETBALL_HEAD_CATEGORY.contains(e.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getChildMarketCategoryId));
        if (CollectionUtils.isEmpty(marketCategoryGrop)) {
            return;
        }
        List<String> value = new ArrayList<>();
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : marketCategoryGrop.entrySet()) {
            List<StandardMarketDataMessage> marketDataMessages = entry.getValue();
            //坑位分组
            Map<Integer, List<StandardMarketDataMessage>> placeNumGrop = marketDataMessages.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getPlaceNum));
            for (Map.Entry<Integer, List<StandardMarketDataMessage>> placeEntry : placeNumGrop.entrySet()) {
                List<StandardMarketDataMessage> placeMarketDataMessages = placeEntry.getValue();
                //Step1:相同坑位根据盘口修改时间升序，第一个盘口不做状态其他删除缓存
                List<StandardMarketDataMessage> resultPlaceMarketDataMessages = placeMarketDataMessages.stream().sorted(Comparator.comparing(StandardMarketDataMessage::getModifyTime).reversed()).collect(Collectors.toList());
                int num = 0;
                for (StandardMarketDataMessage placeMarket : resultPlaceMarketDataMessages) {
                    if (num != 0) {
                        value.add(placeMarket.getThirdMarketSourceId());
                    }
                    num++;
                }
            }
        }
        if(CollectionUtils.isEmpty(value)) {
            return;
        }
        redisService.hDel(redisKey, value.toArray());
        log.info("::{}::删除记录让分跳动后的盘口值：{}", linkId, value);
    }

    public static void main(String[] args) {
        BasketballZeroProcessor basketballZeroProcessor = new BasketballZeroProcessor();
        BigDecimal marketHeadB = new BigDecimal(Double.toString(1D));
        int tailNum = marketHeadB.remainder(BigDecimal.ONE).movePointRight(marketHeadB.scale()).abs().intValue();
        Double configMarketHeadGap = -4D;
        StandardMarketDataMessage standardMarketDataMessage = new StandardMarketDataMessage();
        standardMarketDataMessage.setAddition1("4.5");
//        if (tailNum == 5) {
//            basketballZeroProcessor.specialHeadHandicapZero("", standardMarketDataMessage, configMarketHeadGap);
//            System.err.println("平盘：" + standardMarketDataMessage.getAddition1());
//        } else {
//            basketballZeroProcessor.specialHeadHandicapNotZero("", standardMarketDataMessage, configMarketHeadGap);
//            System.err.println("不平盘：" + standardMarketDataMessage.getAddition1());
//        }
//        basketballZeroProcessor.specialHeadHalfTimeZero("linkId", standardMarketDataMessage, configMarketHeadGap);
//        System.err.println("不平盘：" + standardMarketDataMessage.getAddition1());

        basketballZeroProcessor.specialHeadHalfTimeNotZero("linkId", standardMarketDataMessage, configMarketHeadGap);
        System.err.println("平盘：" + standardMarketDataMessage.getAddition1());

    }
}