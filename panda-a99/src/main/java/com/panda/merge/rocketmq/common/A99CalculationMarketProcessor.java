package com.panda.merge.rocketmq.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.BigDecimalUtils;
import com.panda.merge.common.utils.MergeFunctionUtils;
import com.panda.merge.component.AutoDiffCountMarketMalay;
import com.panda.merge.component.InitializeComponent;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.dto.message.ThirdSportMarketMessage;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.A99ThirdSportMarketMergeProducer;
import com.panda.merge.service.*;
import com.panda.merge.util.CategoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.component.AutoDiffCountMarketMalay.subDoubleTwo;

/**
 * 需求 ：2269 马来抽水
 * 对足球MY计算，其他赛种忽略
 */

@Slf4j
@Component
public class A99CalculationMarketProcessor extends BaseProcessor {
    @Autowired
    private MarketCategorySellService marketCategorySellService;
    @Autowired
    private ConfigMarketCategoryMarginService configMarketCategoryMarginService;
    @Autowired
    private ConfigMarketAutoDiffTradeService configMarketAutoDiffTradeService;
    @Autowired
    private ConfigPlaceNumAutoDiffTradeService configPlaceNumAutoDiffTradeService;
    @Autowired
    private ConfigCategoryAutoDiffTradeService configCategoryAutoDiffTradeService;
    @Autowired
    private InitializeComponent initializeComponent;
    @Autowired
    private AutoDiffCountMarketMalay autoDiffCountMarketMalay;
    @Autowired
    public A99ThirdSportMarketMergeProducer thirdSportMarketMergeProducer;
    @Autowired
    private StandardSportPlayerService standardSportPlayerService;

    /**
     * 赔率盘口处理
     *
     * @param linkId
     * @param standardMatchInfo
     * @param standardCategoryId
     * @param standardMarketDataMessages
     */
    public void calculationMarketProcessor(String linkId, StandardMatchInfo standardMatchInfo, Long standardCategoryId, List<StandardMarketDataMessage> standardMarketDataMessages) {
        if (CollectionUtils.isEmpty(standardMarketDataMessages)) {
            return;
        }
        Integer marketType = isOddsLive(standardMatchInfo.getId());
        //查询玩法开售表
        MarketCategorySell marketCategorySell = marketCategorySellService.getItem(linkId, standardMatchInfo.getId(), marketType, standardCategoryId);
        for (StandardMarketDataMessage standardMarketDataMessage : standardMarketDataMessages) {
            Long relationMarketId = convertRelationMarketId(linkId, standardMarketDataMessage);
            if (standardMarketDataMessage.getMarketOddsList().size() < 2) {
                standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                standardMarketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                log.info("::{}::新抽水计算,盘口id:{},投注项个数错误,关盘处理.", linkId, relationMarketId);
                continue;
            }
            //剔除非开封的盘口，不需要计算和排序
            if (!standardMarketDataMessage.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.ACTIVE) && !standardMarketDataMessage.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.SUSPENDED)) {
                continue;
            }
            ConfigMarketCategoryMargin configMarketCategoryMarginOne = configMarketCategoryMarginService.getItemTwo(linkId, standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getChildMarketCategoryId(), standardMarketDataMessage.getPlaceNum() == null ? 1 : standardMarketDataMessage.getPlaceNum());
            Double spread = 0.1D;
            if (configMarketCategoryMarginOne == null) {
                configMarketCategoryMarginOne = configMarketCategoryMarginService.getItemTwo(linkId, standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getMarketCategoryId(), 1);
            }
            if (null != configMarketCategoryMarginOne) {
                spread = configMarketCategoryMarginOne.getMargin();
            }
            ConfigCategoryAutoDiffTrade configCategoryAutoDiffTrade = null;
            ConfigPlacenumAutoDiffTrade configPlacenumAutoDiffTrade = null;
            boolean ok = calculationMarket(linkId, spread, standardMarketDataMessage, configCategoryAutoDiffTrade, configPlacenumAutoDiffTrade, standardMatchInfo, relationMarketId);
            if (!ok) {
                log.error("::{}::新抽水计算,盘口id:{},抽水或水差计算失败.", linkId, relationMarketId);
                standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                standardMarketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                continue;
            }
            //最终的马来赔转欧赔
            for (StandardMarketOddsDataMessage marketOdds : standardMarketDataMessage.getMarketOddsList()) {
                Integer paOddsValue = BigDecimal.valueOf(initializeComponent.getConvertMalayToEurope(marketOdds.getMalayOddsValue())).multiply(new BigDecimal(Double.toString(100000))).intValue();
                log.info("::{}::新抽水计算,盘口id:{},设置最终马来赔转欧赔, 投注项:{}, 欧赔:{}, 原始赔率:{}, 马来赔:{}", linkId, relationMarketId, marketOdds.getId(), paOddsValue, marketOdds.getOriginalOddsValue(), marketOdds.getMalayOddsValue());
                marketOdds.setPaOddsValue(paOddsValue);
            }
            //特殊抽水计算
             autoDiffCountMarketMalay.standardMarketPumping(linkId, standardCategoryId, standardMatchInfo.getId(), marketCategorySell, standardMarketDataMessage);
        }
    }

    /**
     * 抽水赔率计算
     *
     * @param linkId
     * @param spread
     * @param standardMarketDataMessage
     * @param stringBuffer
     * @return
     */
    private Boolean arithmeticSpread(String linkId, Double spread, StandardMarketDataMessage standardMarketDataMessage, StringBuffer stringBuffer, Long relationMarketId) {
        double odds_min_maly = 0.01;
        double odds_1st_maly = 0D; //第1个投注项 - 马来抽水后赔率（马来赔率）#改了马来抽水逻辑，已经抽水了
        double odds_2nd_maly = 0D; //第2个投注项 - 马来抽水后赔率（马来赔率）#改了马来抽水逻辑，已经抽水了。后续加水差转成欧洲赔率都是原有逻辑 -
        double odds_1st_margin = 0D;//第1个投注项的抽水spread
        double odds_2nd_margin = 0D;//第2个投注项的抽水spread
        List<StandardMarketOddsDataMessage> marketOddsList = standardMarketDataMessage.getMarketOddsList();
        StandardMarketOddsDataMessage odds_1st_ori_Entities = marketOddsList.get(0);
        odds_1st_ori_Entities.setMargin(spread);
        StandardMarketOddsDataMessage odds_2nd_ori_Entities = marketOddsList.get(1);
        odds_2nd_ori_Entities.setMargin(spread);
        stringBuffer.append(linkId + ",新抽水计算,抽水计算开始,盘口ID：" + relationMarketId + ",抽水：" + spread + ",第1个投注项ID:" + odds_1st_ori_Entities.getRelationMarketOddsId() + ",第1个投注项赔率:" + odds_1st_ori_Entities.getOriginalOddsValue() + ",第2个投注项ID:" + odds_2nd_ori_Entities.getRelationMarketOddsId() + ",第2个投注项赔率:" + odds_2nd_ori_Entities.getOriginalOddsValue());
        Double odds_1st_ori = subDoubleTwo(BigDecimal.valueOf(odds_1st_ori_Entities.getOriginalOddsValue()).divide(new BigDecimal(Double.toString(100000))).doubleValue());
        Double odds_2nd_ori = subDoubleTwo(BigDecimal.valueOf(odds_2nd_ori_Entities.getOriginalOddsValue()).divide(new BigDecimal(Double.toString(100000))).doubleValue());
        if (null == initializeComponent.getEuropeConvertMalayMap().get(odds_1st_ori) && null == initializeComponent.getEuropeConvertMalayMap().get(odds_2nd_ori)) {
            stringBuffer.append("原始赔率没有对应马来赔.");
            return false;
        }
        //计算
        double sp = 2 / (2 - BigDecimalUtils.divide(spread, 2)); //1/(1 - spread/2 + 1)  + 1/(1 - spread/2 + 1)   固定抽水（spread=0.3，50%，50%对应1 - spread/2,和1.85）
        if (odds_1st_ori <= 2) { //判断第1个投注项 - 原始赔率是否比2小
            odds_1st_maly = odds_1st_ori / sp - 1; //如果第1个投注项赔率比2小，计算第1个投注项赔率的马来抽水后赔率，去尾法保留两位小数（如果计算出来是0 .1875，取0 .18）
            odds_1st_maly = BigDecimalUtils.scaleCrop(Math.max(odds_min_maly, odds_1st_maly), 2);// 和最小马来赔率比较，取大的1个
            odds_1st_margin = odds_1st_ori - 1 - odds_1st_maly;
            odds_2nd_margin = spread - odds_1st_margin;
            if ((odds_1st_maly + spread) < 1) {  //：第2个投注项马来赔率，由第1个投注项马来赔率和spread计算而来
                odds_2nd_maly = -(BigDecimalUtils.add(odds_1st_maly, spread));
            } else {
                odds_2nd_maly = BigDecimalUtils.subtract(2, BigDecimalUtils.add(odds_1st_maly, spread));
            }
            stringBuffer.append(",第一个投注项赔率小于等于2，计算出maly：" + odds_1st_maly + "===" + odds_2nd_maly);
        } else { //#如果第1个投注项原始赔率大于2，先计算第2个投注项的马来赔率，计算逻辑和上面类似
            odds_2nd_maly = odds_2nd_ori / sp - 1;//#去尾法保留两位小数（如果计算出来是0 .1875，取0 .18）
            odds_2nd_maly = BigDecimalUtils.scaleCrop(Math.max(odds_min_maly, odds_2nd_maly), 2);
            odds_2nd_margin = odds_2nd_ori - 1 - odds_2nd_maly;
            odds_1st_margin = spread - odds_2nd_margin;
            if ((odds_2nd_maly + spread) < 1) {
                odds_1st_maly = -(BigDecimalUtils.add(odds_2nd_maly, spread));
            } else {
                odds_1st_maly = BigDecimalUtils.subtract(2, BigDecimalUtils.add(odds_2nd_maly, spread));
            }
            stringBuffer.append(",第一个投注项赔率大于2，计算出maly：" + odds_1st_maly + "===" + odds_2nd_maly);
        }
        stringBuffer.append(",抽水计算结束.");
        odds_1st_ori_Entities.setMalayOddsValue(BigDecimalUtils.scaleCrop(odds_1st_maly, 2));
        odds_2nd_ori_Entities.setMalayOddsValue(BigDecimalUtils.scaleCrop(odds_2nd_maly, 2));
        return true;
    }

    /**
     * 抽水赔率、水差赔率计算
     */
    private boolean calculationMarket(String linkId, double spread, StandardMarketDataMessage standardMarketDataMessage, ConfigCategoryAutoDiffTrade configCategoryAutoDiffTrade, ConfigPlacenumAutoDiffTrade configPlacenumAutoDiffTrade, StandardMatchInfo standardMatchInfo, Long relationMarketId) {
        StringBuffer stringBuffer = new StringBuffer();
        Double diffValue = 0D;
        //收集足球、篮球附加字段玩法
        List<Long> add1List = new ArrayList<>();
        if (StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId()) || StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId())) {
            add1List.addAll(MarginCategoryConfig.FootBall_MY_CATEGORY);
            add1List.addAll(MarginCategoryConfig.BASKETBALL_MY_CATEGORY);
        }
        Double marketValue = 0D;
        if (StringUtils.isNotBlank(standardMarketDataMessage.getAddition1()) && add1List.contains(standardMarketDataMessage.getMarketCategoryId())) {
            marketValue = Double.parseDouble(standardMarketDataMessage.getAddition1());
        }
        //抽水赔率计算
        Boolean arithmeticSpreadTrue = arithmeticSpread(linkId, spread, standardMarketDataMessage, stringBuffer, relationMarketId);
        if (!arithmeticSpreadTrue) {
            log.info(stringBuffer.toString() + ",赔率异常结束.");
            return false;
        }
        //水差赔率计算
        for (StandardMarketOddsDataMessage standardSportMarketOdds : standardMarketDataMessage.getMarketOddsList()) {
            //计算下盘
            if (standardSportMarketOdds.getOddsType().equals("Under") || standardSportMarketOdds.getOddsType().equals("Even") || standardSportMarketOdds.getOddsType().equals("No") || (MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()) && standardSportMarketOdds.getOddsType().equals("X")) || (marketValue > 0 && standardSportMarketOdds.getOddsType().equals("1") && !MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId())) || (marketValue <= 0 && standardSportMarketOdds.getOddsType().equals("2") && !MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()))) {
                //标记下盘投注项标记，特殊抽水计算需要
                standardSportMarketOdds.setOddsTypeTag(Boolean.TRUE);
                //抽水转换赔率为马来赔
                stringBuffer.append(",新抽水计算,开始计算水差赔率：下盘统一投注项id：" + standardSportMarketOdds.getRelationMarketOddsId() + ",下盘投注项类型：" + standardSportMarketOdds.getOddsType() + ",下盘投注项马来赔：" + standardSportMarketOdds.getMalayOddsValue());
                //获取水差配置

                //获取水差----------
                //只有足球有盘口水差
//                ConfigMarketAutoDiffTrade marketAutoDiffTrade = null;
//                if (standardMatchInfo.getSportId() == 1) {
//                    marketAutoDiffTrade = configMarketAutoDiffTradeService.getItem(linkId, standardMatchInfo.getId(),relationMarketId, standardSportMarketOdds.getOddsType());
//                }
                Double marketDiffTrade = 0.0;
                Double placenumDiffTrade = 0.0;
                Double categoryDiffTrade = 0.0;
                //盘口水差
//                if (marketAutoDiffTrade != null) {
//                    marketDiffTrade = marketAutoDiffTrade.getDiffValue();
//                    stringBuffer.append(",操盘后台设置的盘口水差值：" + marketDiffTrade);
//                }
                //玩法水差，当有玩法水差时，坑位水差跟着玩法水差的投注项走（理论上，有坑位水差一定有玩法水差，默认水差值0）
                String categoryOddsType = null;
                if (configCategoryAutoDiffTrade != null) {
                    categoryOddsType = configCategoryAutoDiffTrade.getOddsType();
                    if (categoryOddsType.equalsIgnoreCase(standardSportMarketOdds.getOddsType())) {
                        categoryDiffTrade = configCategoryAutoDiffTrade.getDiffValue();
                    } else {
                        categoryDiffTrade = -configCategoryAutoDiffTrade.getDiffValue();
                    }
                    stringBuffer.append(",操盘后台设置的玩法水差值：" + categoryDiffTrade);
                }
                //坑位水差
                if (null != configPlacenumAutoDiffTrade) {
                    if (null != categoryOddsType) {
                        configPlacenumAutoDiffTrade.setOddsType(categoryOddsType);
                    }
                    if (configPlacenumAutoDiffTrade.getOddsType().equalsIgnoreCase(standardSportMarketOdds.getOddsType())) {
                        placenumDiffTrade = configPlacenumAutoDiffTrade.getDiffValue();
                    } else {
                        placenumDiffTrade = -configPlacenumAutoDiffTrade.getDiffValue();
                    }
                    stringBuffer.append(",操盘后台设置的坑位水差值：" + placenumDiffTrade);
                }
                diffValue = marketDiffTrade + placenumDiffTrade + categoryDiffTrade;
                //水差计算------
                if (diffValue != 0D) {
                    stringBuffer.append(",下盘口水差：" + diffValue);
                    boolean isOk = autoDiffCountMarketMalay.arithmeticMALAY(linkId, standardMarketDataMessage, diffValue, 0D, standardSportMarketOdds, false);
                    //如果计算失败
                    if (!isOk) {
                        stringBuffer.append(",下盘口赔率不正常，水差计算失败.");
                        log.error(stringBuffer.toString());
                        standardMarketDataMessage.setRemark("盘口赔率不正常，水差计算失败，盘口关盘");
                        return false;
                    }
                    stringBuffer.append(",抽水计算后的下盘马来赔率：" + standardSportMarketOdds.getMalayOddsValue());
                    //设置水差
                    standardSportMarketOdds.setMarketDiffValue(diffValue);
                } else {
                    stringBuffer.append(",下盘投注项：" + standardSportMarketOdds.getOddsType() + ",盘口水差不存在。");
                }
            }
        }
        //计算上盘
        for (StandardMarketOddsDataMessage standardSportMarketOdds : standardMarketDataMessage.getMarketOddsList()) {
            if (standardSportMarketOdds.getOddsType().equals("Under") || standardSportMarketOdds.getOddsType().equals("Even") || standardSportMarketOdds.getOddsType().equals("No") || (MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()) && standardSportMarketOdds.getOddsType().equals("X")) || (marketValue > 0 && standardSportMarketOdds.getOddsType().equals("1") && !MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId())) || (marketValue <= 0 && standardSportMarketOdds.getOddsType().equals("2") && !MarginCategoryConfig.SPECIAL_CATEGORY.contains(standardMarketDataMessage.getMarketCategoryId()))) {
            } else {
                //抽水转换赔率为马来赔
                stringBuffer.append(",上盘统一投注项id：" + standardSportMarketOdds.getRelationMarketOddsId() + ",上盘投注项类型：" + standardSportMarketOdds.getOddsType() + ",上盘投注项马来赔：" + standardSportMarketOdds.getMalayOddsValue());
                if (diffValue != 0D) {
                    boolean isOk = autoDiffCountMarketMalay.arithmeticMALAY(linkId, standardMarketDataMessage, diffValue, 0D, standardSportMarketOdds, true);
                    if (!isOk) {
                        stringBuffer.append(",上盘口赔率不正常，水差计算失败.");
                        log.error(stringBuffer.toString());
                        standardMarketDataMessage.setRemark("盘口赔率不正常，水差计算失败，盘口关盘");
                        return false;
                    }
                    stringBuffer.append(",抽水计算后的上盘马来赔率：" + standardSportMarketOdds.getMalayOddsValue());
                }
            }
        }
        stringBuffer.append(",水差赔率计算成功.");
        log.info(stringBuffer.toString());
        return true;

    }

    public static void main(String[] args) {
        double spread = 0.1;
        double odds_min_maly = 0.01;
        double odds_1st_ori = 2.14d;//第1个投注项 - 原始赔率（欧洲赔率） 1.95
        double odds_2nd_ori = 1.74D; // 第2个投注项 - 原始赔率（欧洲赔率） 2.04
        double odds_1st_maly = 0D; //第1个投注项 - 马来抽水后赔率（马来赔率）#改了马来抽水逻辑，已经抽水了
        double odds_2nd_maly = 0D; //第2个投注项 - 马来抽水后赔率（马来赔率）#改了马来抽水逻辑，已经抽水了。后续加水差转成欧洲赔率都是原有逻辑 -
        double odds_1st_margin = 0D;//第1个投注项的抽水spread
        double odds_2nd_margin = 0D;//第2个投注项的抽水spread
        //计算过程
        double sp = 2 / (2 - BigDecimalUtils.divide(spread, 2)); //1/(1 - spread/2 + 1)  + 1/(1 - spread/2 + 1)   固定抽水（spread=0.3，50%，50%对应1 - spread/2,和1.85）
        if (odds_1st_ori <= 2) { //判断第1个投注项 - 原始赔率是否比2小
            odds_1st_maly = odds_1st_ori / sp - 1; //如果第1个投注项赔率比2小，计算第1个投注项赔率的马来抽水后赔率，去尾法保留两位小数（如果计算出来是0 .1875，取0 .18）
            odds_1st_maly = BigDecimalUtils.scaleCrop(Math.max(odds_min_maly, odds_1st_maly), 2);// 和最小马来赔率比较，取大的1个
            odds_1st_margin = odds_1st_ori - 1 - odds_1st_maly;
            odds_2nd_margin = spread - odds_1st_margin;
            if ((odds_1st_maly + spread) < 1) {  //：第2个投注项马来赔率，由第1个投注项马来赔率和spread计算而来
                odds_2nd_maly = -(BigDecimalUtils.add(odds_1st_maly, spread));
            } else {
                odds_2nd_maly = BigDecimalUtils.subtract(2, BigDecimalUtils.add(odds_1st_maly, spread));
            }
        } else { //#如果第1个投注项原始赔率大于2，先计算第2个投注项的马来赔率，计算逻辑和上面类似
            odds_2nd_maly = odds_2nd_ori / sp - 1;//#去尾法保留两位小数（如果计算出来是0 .1875，取0 .18）
            odds_2nd_maly = BigDecimalUtils.scaleCrop(Math.max(odds_min_maly, odds_2nd_maly), 2);
            odds_2nd_margin = odds_2nd_ori - 1 - odds_2nd_maly;
            odds_1st_margin = spread - odds_2nd_margin;
            if ((odds_2nd_maly + spread) < 1) {
                odds_1st_maly = -(BigDecimalUtils.add(odds_2nd_maly, spread));
            } else {
                odds_1st_maly = BigDecimalUtils.subtract(2, BigDecimalUtils.add(odds_2nd_maly, spread));
            }
        }
        System.err.println("odds_1st_ori：" + odds_1st_ori + "=====" + odds_1st_maly);
        System.err.println("odds_2nd_ori：" + odds_2nd_ori + "=====" + odds_2nd_maly);
    }

    /**
     * TX统一盘口ID 为 sendData 其他数据源为  relationMarketId
     * 统一转为 relationMarketId
     *
     * @param linkId
     * @param standardMarketDataMessage
     * @return
     */
    public Long convertRelationMarketId(String linkId, StandardMarketDataMessage standardMarketDataMessage) {
        Long relationMarketId = 0L;
        try {
            if (standardMarketDataMessage.getDataSourceCode().equals(DataSourceCodeEnum.TX.code) && StringUtils.isNotBlank(standardMarketDataMessage.getSendData())) {
                relationMarketId = Long.valueOf(standardMarketDataMessage.getSendData());
            } else {
                relationMarketId = standardMarketDataMessage.getRelationMarketId();
            }

        } catch (Exception e) {
            relationMarketId = standardMarketDataMessage.getRelationMarketId();
            log.info("::{}::三方盘口数据源ID:{},TX统一盘口ID转换失败,", linkId, standardMarketDataMessage.getThirdMarketSourceId(), e);
        }
        return relationMarketId;
    }


    public void n0nDataSourceOddsHandle(String linkId, StandardMatchInfo standardMatchInfo, List<ThirdSportMarketMessage> thirdSportMarketMessagesSort, Long sportId) {
        try {
//            List<ThirdSportMarketMessage> thirdSportMarketMessages = thirdSportMarketMessagesSort.stream().filter(e -> (MarginCategoryConfig.FootBall_3446_3447_CATEGORY.contains(e.getMarketCategoryId()) || MarginCategoryConfig.BasketBall_3446_3447_CATEGORY.contains(e.getMarketCategoryId()))).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(thirdSportMarketMessagesSort)) {
                return;
            }
//            String dataSourceCodeDB = dataSourceCode.split("-")[0].toUpperCase();
//              if (!DataSourceCodeEnum.N01.getCode().equals(dataSourceCodeDB) && !DataSourceCodeEnum.N02.getCode().equals(dataSourceCodeDB)) {
//                return;
//            }
            log.info("::{}::n0nDataSourceOddsHandle,赔率计算开始", linkId);
            log.info("::{}::n0nDataSourceOddsHandle,开始排序", linkId);
            thirdSportMarketMergeProducer.setPlaceNum(thirdSportMarketMessagesSort);
            log.info("::{}::n0nDataSourceOddsHandle,排序完成", linkId);
            //三方盘口消息体先转换成标准盘口消息体
            List<StandardMarketDataMessage> standardMarketDataMessages = thirdSportMarketMessagesSort.stream().map(e -> {
                StandardMarketDataMessage v = thirdMarketConvertStandard(e);
                if (null != v) {
                    v.setChildMarketCategoryId((CategoryUtils.getChildCategoryId(linkId, v.getMarketCategoryId(), v.getAddition1(), v.getAddition2(), v.getAddition3(), v.getAddition4(), v.getAddition5(), String.valueOf(v.getStandardMatchInfoId()))));
                }
                return v;
            }).collect(Collectors.toList());


            Set<Long> marketCategoryIdSet = standardMarketDataMessages.stream().map(StandardMarketDataMessage::getMarketCategoryId).collect(Collectors.toSet());
            //MY玩法
            Set<Long> marketCategoryIdMALAY = new HashSet<>();
            //两项盘EU玩法
            List<Long> marketCategoryIdEu = new ArrayList<>();
            //取操盘两项盘玩法id集
            marketCategoryDistinguish(marketCategoryIdSet, marketCategoryIdMALAY, marketCategoryIdEu, sportId);
            //取操盘两项盘玩法id集,并根据玩法分组
            Map<Long, List<StandardMarketDataMessage>> standardMarketMapMALAY = standardMarketDataMessages.stream().filter(e -> marketCategoryIdMALAY.contains(e.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));

            //篮球两项盘/足球三项盘口(两项盘)margin计算玩法 根据玩法分组
            Set<Long> finalMarketCategoryIdEUROPE = marketCategoryIdSet.stream().filter(marketCategoryIdEu::contains).collect(Collectors.toSet());
            Map<Long, List<StandardMarketDataMessage>> standardMarketMapEUROPE = standardMarketDataMessages.stream().filter(e -> finalMarketCategoryIdEUROPE.contains(e.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));

            //取剩余玩法id集，并根据玩法分组
            Set<Long> marketCategoryIdOTHER = marketCategoryIdSet.stream().filter(e -> !marketCategoryIdMALAY.contains(e) && !finalMarketCategoryIdEUROPE.contains(e)).collect(Collectors.toSet());
            Map<Long, List<StandardMarketDataMessage>> standardMarketMapOTHER = standardMarketDataMessages.stream().filter(e -> marketCategoryIdOTHER.contains(e.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));
            List<StandardMarketDataMessage> standardMarketMessageList = new ArrayList<>();
            //--------------操盘两项盘spread计算------------------
            if (!CollectionUtils.isEmpty(standardMarketMapMALAY)) {
                standardMarketMapMalay(linkId, standardMatchInfo, standardMarketMessageList, standardMarketMapMALAY);
            }
            //--------------三项盘/两项盘margin计算,其他球类使用数据商抽水赔率------------------
            if (!CollectionUtils.isEmpty(standardMarketMapEUROPE)) {
                standardMarketMapEurope(linkId, standardMatchInfo, standardMarketMessageList, standardMarketMapEUROPE);
            }
            //------------其余玩法处理--------------------
            if (!CollectionUtils.isEmpty(standardMarketMapOTHER)) {
                //循环遍历盘口信息，设置低赔和赔率差
                setOddsMetricAndLowOddsForMTS(linkId, standardMarketMessageList, standardMarketMapOTHER, standardMatchInfo);
            }
            //设置三方盘口信息数据
            thirdSportMarketMessagesSort.forEach(e -> {
                standardMarketConvertThird(standardMarketMessageList, e);
            });
        } catch (Exception e) {
            log.info("::{}::n0nDataSourceOddsHandle,赔率计算,异常:{}", linkId, e.getMessage(), e);
        }
//        thirdSportMarketMessagesSort.forEach(market -> {
//            market.getThirdSportMarketOddsList().forEach(odds -> {
//                log.info("::{}, 玩法id:{}, 盘口值:{}, 盘口id:{}, 玩法抽水赔率设置完成, 投注项:{}, :抽水前:{} - 抽水后{}", linkId, market.getMarketCategoryId(),
//                        market.getAddition1(), market.getRelationMarketId(), odds.getOddsType(), odds.getOriginalOddsValue(), odds.getOddsValue());
//            });
//        });
        log.info("::{}::n0nDataSourceOddsHandle,赔率计算完成", linkId);
    }

    private void standardMarketConvertThird(List<StandardMarketDataMessage> standardMarketDataMessages, ThirdSportMarketMessage thirdSportMarketMessage) {
        standardMarketDataMessages.stream().filter(e -> e.getRelationMarketId().equals(thirdSportMarketMessage.getRelationMarketId())).findFirst().ifPresent(e -> {
            thirdSportMarketMessage.setThirdMarketSourceStatus(e.getThirdMarketSourceStatus());
            thirdSportMarketMessage.setStatus(e.getStatus());
            if (!CollectionUtils.isEmpty(e.getMarketOddsList()) && !thirdSportMarketMessage.getThirdSportMarketOddsList().isEmpty()) {
                standardOddsConvertThird(e.getMarketOddsList(), thirdSportMarketMessage.getThirdSportMarketOddsList());
            }
        });
    }

    private void standardOddsConvertThird(List<StandardMarketOddsDataMessage> standardOddsDataMessages, List<ThirdSportMarketOdds> thirdSportMarketOddsList) {
        for (StandardMarketOddsDataMessage standardOddsDataMessage : standardOddsDataMessages) {
            thirdSportMarketOddsList.stream().filter(e -> e.getId().equals(standardOddsDataMessage.getRelationMarketOddsId())).findFirst().ifPresent(e -> {
                e.setActive(standardOddsDataMessage.getActive());
                e.setPaOddsValue(standardOddsDataMessage.getPaOddsValue());
                Integer oddsValue = standardOddsDataMessage.getOddsValue();
                log.info("盘口id:{}, 投注项id:{}, 准备设置抽水赔率:{}", e.getMarketId(), e.getId(), oddsValue);
                e.setOddsValue(oddsValue);
            });
        }
    }

    /**
     * 玩法分类计算 MY/EU
     *
     * @param marketCategoryIdSet
     * @param marketCategoryIdMALAY
     * @param marketCategoryIdEu
     * @param sportId
     */
    private static void marketCategoryDistinguish(Set<Long> marketCategoryIdSet, Set<Long> marketCategoryIdMALAY, List<Long> marketCategoryIdEu, Long sportId) {
        switch (sportId.intValue()) {
            case 1: //足球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.FootBall_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.FootBall_EU_CATEGORY);
                break;
            case 2: //篮球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.BASKETBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.BASKETBALL_EU_CATEGORY);
                break;
            case 3: //棒球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.BASEBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.BASEBALL_EU_CATEGORY);
                break;
            case 4: //冰球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.ICEBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.ICEBALL_EU_CATEGORY);
                break;
            case 5: //网球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.TENNIS_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.TENNIS_EU_CATEGORY);
                break;
            case 6: //美式足球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.AMERICAN_FOOTBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.AMERICAN_FOOTBALL_EU_CATEGORY);
                break;
            case 7: //斯诺克
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.SNOOKER_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.SNOOKER_EU_CATEGORY);
                break;
            case 9: //排球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.VOLLEYBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.VOLLEYBALL_EU_CATEGORY);
                break;
            case 8://乒乓球
            case 10://羽毛球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.TABLETENNIS_AND_BADMINTON_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.TABLETENNIS_AND_BADMINTON_EU_CATEGORY);
                break;
            case 11: //手球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.HANDBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.HANDBALL_EU_CATEGORY);
                break;
            case 12: //拳击
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.BOXING_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.BOXING_EU_CATEGORY);
                break;
            case 13: //沙滩排球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.BEACH_VOLLEYBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.BEACH_VOLLEYBALL_EU_CATEGORY);
                break;
            case 14: //橄榄球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.UK_FOOTBALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.UK_FOOTBALL_EU_CATEGORY);
                break;
            case 15: //曲棍球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.HOCKEY_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.HOCKEY_EU_CATEGORY);
                break;
            case 16: //水球
                marketCategoryIdMALAY.addAll(marketCategoryIdSet.stream().filter(e -> MarginCategoryConfig.WATER_BALL_MY_CATEGORY.contains(e)).collect(Collectors.toSet()));
                marketCategoryIdEu.addAll(MarginCategoryConfig.WATER_BALL_EU_CATEGORY);
                break;
            default:
                break;
        }
    }

    private void standardMarketMapMalay(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketMessageList, Map<Long, List<StandardMarketDataMessage>> standardMarketMapMALAY) {
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : standardMarketMapMALAY.entrySet()) {
            //获取key对应的盘口对象集合
            List<StandardMarketDataMessage> standardMarketDataMessages = entry.getValue();
            //---------处理有效盘口------------
            List<StandardMarketDataMessage> standardMarketsValid = standardMarketDataMessages.stream().filter(e -> !CollectionUtils.isEmpty(e.getMarketOddsList())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(standardMarketsValid)) {
                //开盘中的盘口计算和封装
                if (MarginCategoryConfig.TRADER_SUPPORT_SPORT.contains(standardMatchInfo.getSportId())) {
                    //新算法计算
                    calculationMarketProcessor(linkId, standardMatchInfo, entry.getKey(), standardMarketsValid);
                }
            }
            standardMarketDataMessages.forEach(standardMarketDataMessage -> standardMarketDataMessage.setCategoryType("MY"));
            standardMarketMessageList.addAll(standardMarketDataMessages);
        }
    }

    public StandardMarketDataMessage thirdMarketConvertStandard(ThirdSportMarketMessage thirdSportMarketMessage) {
        StandardMarketDataMessage standardMarketDataMessage = new StandardMarketDataMessage();
        BeanUtils.copyProperties(thirdSportMarketMessage, standardMarketDataMessage);
        standardMarketDataMessage.setRelationMarketId(thirdSportMarketMessage.getRelationMarketId());
        standardMarketDataMessage.setThirdMarketSourceStatus(thirdSportMarketMessage.getThirdMarketSourceStatus());
        standardMarketDataMessage.setStatus(thirdSportMarketMessage.getStatus());
        standardMarketDataMessage.setMarketType(thirdSportMarketMessage.getMarketType());
        standardMarketDataMessage.setPlaceNum(thirdSportMarketMessage.getPlaceNum());
        standardMarketDataMessage.setRelationMarketId(thirdSportMarketMessage.getRelationMarketId());
        MergeFunctionUtils.setNumberOfWinners( standardMarketDataMessage, thirdSportMarketMessage.getNumberOfWinners());
        standardMarketDataMessage.setAddition1(thirdSportMarketMessage.getAddition1());
        standardMarketDataMessage.setAddition2(thirdSportMarketMessage.getAddition2());
        standardMarketDataMessage.setAddition3(thirdSportMarketMessage.getAddition3());
        standardMarketDataMessage.setAddition4(thirdSportMarketMessage.getAddition4());
        standardMarketDataMessage.setAddition5(thirdSportMarketMessage.getAddition5());
        standardMarketDataMessage.setMarketCategoryId(thirdSportMarketMessage.getMarketCategoryId());
        standardMarketDataMessage.setOldThirdMarketSourceStatus(thirdSportMarketMessage.getThirdMarketSourceStatus());
        standardMarketDataMessage.setDataSourceCode(thirdSportMarketMessage.getDataSourceCode());
        standardMarketDataMessage.setThirdMarketSourceId(thirdSportMarketMessage.getThirdMarketSourceId());
        standardMarketDataMessage.setStandardMatchInfoId(thirdSportMarketMessage.getReferenceId());
        if (!CollectionUtils.isEmpty(thirdSportMarketMessage.getThirdSportMarketOddsList())) {
            standardMarketDataMessage.setMarketOddsList(thirdSportMarketMessage.getThirdSportMarketOddsList().stream().map(e -> {
                StandardMarketOddsDataMessage standardMarketOddsDataMessage = new StandardMarketOddsDataMessage();
                BeanUtils.copyProperties(e, standardMarketOddsDataMessage);
                standardMarketOddsDataMessage.setRelationMarketOddsId(e.getId());
                //N01/N02数据源默认使用公平赔率
                standardMarketOddsDataMessage.setPaOddsValue(e.getOriginalOddsValue());
                standardMarketOddsDataMessage.setOddsValue(e.getOddsValue());
                standardMarketOddsDataMessage.setOriginalOddsValue(e.getOriginalOddsValue());
                standardMarketOddsDataMessage.setThirdOddsFieldSourceId(e.getThirdOddsFieldSourceId());
                standardMarketOddsDataMessage.setOddsFieldsTemplateId(e.getOddsFieldsTemplateId());
                return standardMarketOddsDataMessage;
            }).collect(Collectors.toList()));
        }
        return standardMarketDataMessage;
    }

    private void standardMarketMapEurope(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketDataMessage> standardMarketMessageList, Map<Long, List<StandardMarketDataMessage>> standardMarketMapEUROPE) {
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : standardMarketMapEUROPE.entrySet()) {
            //获取玩法id
            Long marketCategoryId = entry.getKey();
            //获取key对应的盘口对象
            List<StandardMarketDataMessage> standardMarketDataMessagesList = entry.getValue();
            for (StandardMarketDataMessage standardMarketDataMessage : standardMarketDataMessagesList) {
                //操盘球种EU计算
                if (MarginCategoryConfig.SOPRT_TYPE.contains(standardMatchInfo.getSportId())) {
                    if (!CollectionUtils.isEmpty(standardMarketDataMessage.getMarketOddsList())) {
                        marginCalculateTransfer(linkId, standardMatchInfo.getId(), standardMarketDataMessage, marketCategoryId, standardMatchInfo.getSportId());
                    }
                }
            }
            standardMarketMessageList.addAll(standardMarketDataMessagesList);
        }
    }

    /**
     * 判断投注项数量
     * 足球margin计算存在两项盘
     */
    private void marginCalculateTransfer(String linkId, Long standardMatchId, StandardMarketDataMessage standardMarketDataMessage, Long marketCategoryId, Long sportId) {
        //计算有效盘口
        if (standardMarketDataMessage.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
            return;
        }
        if (standardMarketDataMessage.getMarketOddsList().size() == 3) {
            processStandardMarketMarginEUROPE(linkId, standardMatchId, standardMarketDataMessage, marketCategoryId);
        } else if (standardMarketDataMessage.getMarketOddsList().size() == 2) {
            processStandardMarketTwoEUROPE(linkId, standardMatchId, standardMarketDataMessage, marketCategoryId, sportId);
        } else {
            log.info("::{}::marginCalculateTransfer投注项数量错误,标准赛事ID:{},玩法:{},standardMarketDataMessage:{}", linkId, standardMatchId, marketCategoryId, JSON.toJSON(standardMarketDataMessage));
        }
    }

    /*** 计算方式：
     * (1/((1/抽水赔 截取八位小数) + 概率差 )) + 水差
     *
     * @param linkId
     * @param standardMatchInfoId
     * @param standardMarketDataMessage
     * @param marketCategoryId
     */
    private void processStandardMarketMarginEUROPE(String linkId, Long standardMatchInfoId, StandardMarketDataMessage standardMarketDataMessage, Long marketCategoryId) {
        //转换统一盘口ID
        Long relationMarketId = convertRelationMarketId(linkId, standardMarketDataMessage);
        if (standardMarketDataMessage.getPlaceNum() == null) {
            standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
            log.info("::{}::三项盘独赢坑位为null盘口关盘处理,标准赛事ID:{},玩法ID{},统一盘口ID:{},坑位:{}", linkId, standardMatchInfoId, marketCategoryId, relationMarketId, standardMarketDataMessage.getPlaceNum());
            return;
        }
        Map<String, ConfigMarketMarginGap> marginGapMap = new HashMap<>();
        Double initMargin = 110D;
        //查询独赢配置
        List<ConfigMarketMarginGap> itemList = configMarketMarginGapService.getItemList(standardMatchInfoId, marketCategoryId, standardMarketDataMessage.getChildMarketCategoryId(), standardMarketDataMessage.getPlaceNum());
        if (CollectionUtils.isEmpty(itemList)) {
            itemList = configMarketMarginGapService.getItemList(standardMatchInfoId, marketCategoryId, marketCategoryId, standardMarketDataMessage.getPlaceNum());
        }
        if (!CollectionUtils.isEmpty(itemList)) {
            marginGapMap = itemList.stream().collect(Collectors.toMap(ConfigMarketMarginGap::getOddsType, a -> a, (k1, k2) -> k1));
        }
        try {
            for (StandardMarketOddsDataMessage marketOdds : standardMarketDataMessage.getMarketOddsList()) {
                String oddsType = marketOdds.getOddsType();
                ConfigMarketMarginGap configMarketMarginGap = new ConfigMarketMarginGap();
                //配置设置不存在默认值
                if (CollectionUtils.isEmpty(marginGapMap)) {
                    configMarketMarginGap.setMargin(initMargin);
                    //默认不联动
                    configMarketMarginGap.setLinkageMode(0);
                } else {
                    if (marginGapMap.get(oddsType) != null) {
                        configMarketMarginGap = marginGapMap.get(oddsType);
                    }
                }
                //概率差- PGap
                Double probability = BigDecimalUtils.divide(BigDecimalUtils.changeZero(configMarketMarginGap.getProbability()), 100);
                //A模式联动；不联动则概率差不平摊到其他选项 ：0(否),1(是)
                Integer linkageMode = BigDecimalUtils.changeZero(configMarketMarginGap.getLinkageMode());
                //水差- OddsGap
                Double diffValue = BigDecimalUtils.changeZero(configMarketMarginGap.getDiffValue());
                //描点 ：0(否),1(是)
                Integer anchor = configMarketMarginGap.getAnchor();
                //抽水赔率
                Integer oddsValue = marketOdds.getOddsValue();
                //原始赔率为0 ,水差就是最终赔率
                if (oddsValue == 0) {
                    marketOdds.setMarketDiffValue(BigDecimalUtils.multiply(diffValue, 100));
                    marketOdds.setProbability(BigDecimalUtils.multiply(probability, 100));
                    marketOdds.setProbabilityOdds(0);
                    marketOdds.setMargin(configMarketMarginGap.getMargin());
                    standardMarketDataMessage.setLinkageMode(linkageMode);
                    marketOdds.setPaOddsValue(processOddsValueDecimals(linkId, BigDecimalUtils.multiply(diffValue, 100000).intValue()));
                    marketOdds.setAnchor(anchor);
                    standardMarketDataMessage.setRemark("投注项原始存在为:0");
                    log.info("::{}::三项盘独赢计算:{},标准盘口:{},统一盘口id:{},原始赔率为:0，不再计算,封盘口和投注项:{}", linkId, standardMatchInfoId, standardMarketDataMessage.getId(), standardMarketDataMessage.getRelationMarketId(), marketOdds.getOddsType());
                    continue;
                }
                //Step1:原始赔率转为小数点，原始概率： P = 1/抽水赔率
                double changOriginalOdds = BigDecimalUtils.divide(oddsValue, 100000D, 2);
                double p = BigDecimalUtils.divide(1, changOriginalOdds, 8);
                //Step2:计算概率差赔率probabilityOdds, 公式: 1/(P+M+PGap)
                double probabilityOdds = BigDecimalUtils.add(p, probability);
                probabilityOdds = BigDecimalUtils.divide(1, probabilityOdds, 2);
                //最终PA赔率 所有投注项概率赔率加上水差：paOdds = odds + oddsGap
                Double paOddsValue = BigDecimalUtils.add(probabilityOdds, diffValue);
                marketOdds.setPaOddsValue(BigDecimalUtils.multiply(paOddsValue, 100000).intValue());
                marketOdds.setAnchor(anchor);
                //水差*100 返回给前端
                marketOdds.setMarketDiffValue(BigDecimalUtils.multiply(diffValue, 100));
                marketOdds.setProbability(BigDecimalUtils.multiply(probability, 100));
                marketOdds.setMargin(configMarketMarginGap.getMargin());
                standardMarketDataMessage.setLinkageMode(linkageMode);
                marketOdds.setProbabilityOdds(BigDecimalUtils.multiply(probabilityOdds, 100000).intValue());
                marketOdds.setMarginProbabilityOdds(BigDecimalUtils.multiply(changOriginalOdds, 100000).intValue());
                //最终赔率小数点处理
                marketOdds.setPaOddsValue(processOddsValueDecimals(linkId, BigDecimalUtils.multiply(paOddsValue, 100000).intValue()));
                log.info("::{}::三项盘独赢计算,标准赛事id:{},标准盘口ID:{},转换统一盘口ID:{},统一盘口id:{},三方盘口源id:{},投注项类型:{},瞄点(0否/1是):{},原始赔率:{},P原始概率:{},抽水原始概率:{},概率差:{},概率差赔率:{},水差:{},最终PA赔率:{},联动0(否)/1(是):{},配置信息:{}", linkId, standardMatchInfoId, standardMarketDataMessage.getId(), relationMarketId, standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getThirdMarketSourceId(), oddsType, anchor, changOriginalOdds, p, changOriginalOdds, probability, probabilityOdds, diffValue, paOddsValue, linkageMode, JSONObject.toJSON(configMarketMarginGap));
            }
        } catch (Exception e) {
            //出现异常盘口封盘
            standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
            log.info("::{}::三项独赢盘计算出现异常,盘口ID:{},玩法ID:{},三方盘口ID:{},e:{}", linkId, relationMarketId, marketCategoryId, standardMarketDataMessage.getThirdMarketSourceId(), e);
        }
    }

    /**
     * 两项盘 margin计算
     * **操盘球种计算方式 如:
     * 下盘： 原始赔率  水差     margin均分
     * 4.66
     * 1/( (1/4.66 + 0.02 ) + (1.1-1)/2 )
     * 上盘：
     * 1.27
     * 1/( (1/1.27 - 0.02 ) + (1.1-1)/2 )
     * <p>
     * 计算出小数点都是截取
     * **综合球种计算方式 如:
     * 下盘： 原始赔率  水差
     * 4.66
     * 1/( (1/4.66 + 0.02 ))
     * 上盘：
     * 1.27
     * 1/( (1/1.27 - 0.02 ))
     *
     * @param linkId
     * @param matchId
     * @param standardMarketDataMessage
     * @param marketCategoryId
     */
    private void processStandardMarketTwoEUROPE(String linkId, Long matchId, StandardMarketDataMessage standardMarketDataMessage, Long marketCategoryId, Long sportId) {
        //只计算有效盘口
        if (standardMarketDataMessage.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
            return;
        }
        //TRUE:操盘球种（数据商原始赔率） ，FALSE:综合球种（数据商抽水赔率）
        Boolean isTrue = MarginCategoryConfig.TRADER_SUPPORT_SPORT.contains(sportId) ? Boolean.TRUE : Boolean.FALSE;
        Long relationMarketId = convertRelationMarketId(linkId, standardMarketDataMessage);
        StringBuffer sb = new StringBuffer("标准赛事ID:" + matchId + "统一盘口ID:" + relationMarketId + "玩法:" + marketCategoryId);
        //TX坑位查询水差配置处理
        Integer placeNum = standardMarketDataMessage.getPlaceNum();
        if (DataSourceCodeEnum.TX.code.equals(standardMarketDataMessage.getDataSourceCode()) && standardMarketDataMessage.getTxPlaceNum() != null) {
            placeNum = standardMarketDataMessage.getTxPlaceNum();
            log.info("::{}::新margin计算,三方盘口ID:{},标准盘口ID:{},TX坑位变更前:{},后:{}", linkId, standardMarketDataMessage.getThirdMarketSourceId(), standardMarketDataMessage.getRelationMarketId(), standardMarketDataMessage.getTxPlaceNum(), placeNum);
        }
        Long childMarketCategoryId = standardMarketDataMessage.getChildMarketCategoryId();
        //子玩法margin不存在，查询总玩法
        ConfigMarketCategoryMargin configMargin = configMarketCategoryMarginService.getItemTwo(linkId, matchId, marketCategoryId, childMarketCategoryId, placeNum == null ? 1 : placeNum);
        if (configMargin == null) {
            sb.append("，子玩法margin,查询总玩法margin");
            configMargin = configMarketCategoryMarginService.getItemTwo(linkId, matchId, marketCategoryId, marketCategoryId, 1);
        }
        Double marginOdds = 110D;
        if (configMargin != null && configMargin.getMargin() >= 1) {
            marginOdds = configMargin.getMargin();
            sb.append("，玩法margin：" + marginOdds);
        }
        double diffValue = 0D;
        //足球/综合球种 盘口水差
        if (sportId == 1L || MarginCategoryConfig.COMPLEX_SPORTIDS.contains(sportId)) {
            String oddsType = MarginCategoryConfig.COMPLEX_SPORT_CATEGORY_ODDS_TYPE_NO.contains(marketCategoryId) ? "No" : "2";
            ConfigMarketAutoDiffTrade marketAutoDiffTrade = configMarketAutoDiffTradeService.getItem(linkId, matchId, relationMarketId, oddsType);
            if (marketAutoDiffTrade != null) {
                diffValue = marketAutoDiffTrade.getDiffValue();
            }
            sb.append("，足球/综合球种,盘口水差 " + diffValue + ",查询下盘投注项：" + oddsType);
        } else {
            //其他球总坑位水差
            //水差子玩法不存在
            ConfigPlacenumAutoDiffTrade configPlacenumAutoDiffTrade = configPlaceNumAutoDiffTradeService.getItem(linkId, matchId, marketCategoryId, childMarketCategoryId, placeNum);
            if (configPlacenumAutoDiffTrade != null) {
                diffValue = configPlacenumAutoDiffTrade.getDiffValue();
            }
            sb.append("，水差值 " + diffValue + ",查询水差坑位：" + placeNum);
        }
        sb.append("，盘口信息：" + JSONObject.toJSONString(standardMarketDataMessage));
        for (StandardMarketOddsDataMessage marketOdds : standardMarketDataMessage.getMarketOddsList()) {
            Boolean place = Boolean.TRUE;
            if (StringUtils.equals(marketOdds.getOddsType(), "2") || StringUtils.equals(marketOdds.getOddsType(), "No")) {
                place = Boolean.FALSE;
            }
            //设置margin
            marketOdds.setMargin(marginOdds);
            marketOdds.setMarketDiffValue(diffValue);
            // 原始赔率为0 日志补全不计算
            if (null == marketOdds.getOriginalOddsValue() || marketOdds.getOriginalOddsValue() == 0) {
                marketOdds.setPaOddsValue(0);
                sb.append("，原始赔率为0不计算----------");
                continue;
            }
            //--------------------margin计算--------------------//
            //原始赔率转换为小数点后两位
            double changOriginalOdds = BigDecimalUtils.divide((isTrue ? marketOdds.getOriginalOddsValue() : marketOdds.getOddsValue()), 100000, 2);
            //原始概率 = 1 / 原始赔率
            double originalProbability = BigDecimalUtils.divide(1, changOriginalOdds, 4);
            //水差赔率概率 = 原始概率 + -  水差
            double diffOdds;
            if (place) {
                diffOdds = BigDecimalUtils.subtract(originalProbability, diffValue);
            } else {
                diffOdds = BigDecimalUtils.add(originalProbability, diffValue);
            }
            sb.append(" --- 投注项：" + marketOdds.getOddsType() + "，原始赔率：" + changOriginalOdds + "，原始概率：" + originalProbability + "，水差赔率概率：" + diffOdds);
            //水差赔率概率校验：小于等于：0   大于等于：1 盘口封盘
            if (diffOdds <= 0 || diffOdds >= 1) {
                sb.append("，水差赔率概率校验：小于等于：0 、 大于等于：1 盘口封盘 ----------");
                continue;
            }
            //操盘球种才有margin均分
            if (isTrue) {
                //margin均分
                double marginAverage = BigDecimalUtils.divide(BigDecimalUtils.subtract(marginOdds, 100), 200);
                //PA赔率概率
                double paOddsProbability = BigDecimalUtils.add(diffOdds, marginAverage);
                //最终PA赔率概率
                double paOdds = BigDecimalUtils.divide(1, paOddsProbability);
                marketOdds.setPaOddsValue(BigDecimalUtils.multiply(paOdds, 100000).intValue());
                sb.append("，margin均分：" + marginAverage + "，PA赔率概率：" + paOddsProbability + "，最终PA赔率：" + paOdds);
            } else {
                //最终PA赔率概率
                double paOdds = BigDecimalUtils.divide(1, diffOdds);
                marketOdds.setPaOddsValue(BigDecimalUtils.multiply(paOdds, 100000).intValue());
                sb.append("，最终PA赔率：" + paOdds);
            }
        }
    }

    /**
     * 给MTS的盘口设置排序，抽水赔
     *
     * @param standardMarketMapMTS
     */
    public void setOddsMetricAndLowOddsForMTS(String linkId, List<StandardMarketDataMessage> standardMarketMessageList, Map<Long, List<StandardMarketDataMessage>> standardMarketMapMTS, StandardMatchInfo standardMatchInfo) {
        //循环遍历盘口信息,设置低赔和赔率差
        for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : standardMarketMapMTS.entrySet()) {
            //获取key对应的盘口对象集合
            List<StandardMarketDataMessage> standardMarketDataMessages = entry.getValue();
            //取盘口中有投注项的有效数据
            List<StandardMarketDataMessage> standardMarketsValid = standardMarketDataMessages.stream().filter(e -> !CollectionUtils.isEmpty(e.getMarketOddsList())).collect(Collectors.toList());
            //------------处理有效盘口的排序-----------
            if (!CollectionUtils.isEmpty(standardMarketsValid)) {
                //第一步：计算赔率差和低赔
                for (StandardMarketDataMessage standardMarketDataMessage : standardMarketsValid) {
                    //计算有效盘口
                    if (standardMarketDataMessage.getThirdMarketSourceStatus() >= Constant.SPORT_MARKET.STATUS.DEACTIVATED) {
                        continue;
                    }
                    //852需求 查询独赢配置获取多项盘概率差,只对足球处理
                    Map<String, ConfigMarketMarginGap> marginGapMap = new HashMap<>();
                    Long marketCategoryId = standardMarketDataMessage.getMarketCategoryId();
                    if ((standardMatchInfo.getSportId() == 1 || standardMatchInfo.getSportId() == 2 || MarginCategoryConfig.COMPLEX_SPORTIDS.contains(standardMatchInfo.getSportId())) && MarginCategoryConfig.THREE_CATEGORY.contains(marketCategoryId)) {
                        log.info("::{}::查询独赢配置获取多项盘概率差,赛事ID:{},统一盘口ID:{},玩法ID:{},坑位ID:{}", linkId, standardMatchInfo.getId(), standardMarketDataMessage.getRelationMarketId(), marketCategoryId, standardMarketDataMessage.getPlaceNum());
                        if (standardMarketDataMessage.getPlaceNum() != null) {
                            List<ConfigMarketMarginGap> itemList = configMarketMarginGapService.getItemList(standardMatchInfo.getId(), marketCategoryId, standardMarketDataMessage.getChildMarketCategoryId(), standardMarketDataMessage.getPlaceNum());
                            if (!CollectionUtils.isEmpty(itemList)) {
                                marginGapMap = itemList.stream().collect(Collectors.toMap(ConfigMarketMarginGap::getOddsType, a -> a, (k1, k2) -> k1));
                            }
                        }
                    }
                    //获取盘口投注项
                    List<StandardMarketOddsDataMessage> marketOddsList = standardMarketDataMessage.getMarketOddsList();
                    Integer minOddsValue = 0;
                    Integer maxOddsValue = 0;
                    //循环遍历盘口投注项
                    for (StandardMarketOddsDataMessage standardMarketOddsDataMessage : marketOddsList) {
                        //设置pa赔率：数据源抽水赔率
                        standardMarketOddsDataMessage.setPaOddsValue(standardMarketOddsDataMessage.getOddsValue());
                        if (null == standardMarketOddsDataMessage.getPaOddsValue()) {
                            standardMarketOddsDataMessage.setPaOddsValue(0);
                        }
                        if (standardMarketOddsDataMessage.getPaOddsValue() > maxOddsValue) {
                            maxOddsValue = standardMarketOddsDataMessage.getPaOddsValue();
                        }
                        if (standardMarketOddsDataMessage.getPaOddsValue() < minOddsValue || minOddsValue == 0) {
                            minOddsValue = standardMarketOddsDataMessage.getPaOddsValue();
                        }
                        //852需求 数据源抽水赔率转概率赔率 + 概率差
                        String oddsType = standardMarketOddsDataMessage.getOddsType();
                        //球员玩法上游传的是中文，传递给下游是namecode,独赢配置存的是namecode
                        if (MarginCategoryConfig.PLAYER_CATEGORY_ODDS.contains(standardMarketDataMessage.getMarketCategoryId())) {
                            if (!MarginCategoryConfig.PLAYER_CATEGORY_ODDS_TYPE.contains(standardMarketOddsDataMessage.getOddsType())) {
                                StandardSportPlayer standardSportPlayer = standardSportPlayerService.getItem(standardMatchInfo.getSportId(), standardMarketOddsDataMessage.getAddition1());
                                if (null != standardSportPlayer) {
                                    oddsType = standardSportPlayer.getNameCode().toString();
                                }
                            }
                            log.info("::{}::数据商抽水赔率加概率球员类玩法投注类型转换,赛事ID:{},玩法ID:{},oddsType:{}", linkId, standardMatchInfo.getId(), marketCategoryId, oddsType);
                        }
                        if (!CollectionUtils.isEmpty(marginGapMap) && marginGapMap.get(oddsType) != null) {
                            Integer paOddsValue = standardMarketOddsDataMessage.getPaOddsValue();
                            if (paOddsValue != 0) {
                                ConfigMarketMarginGap configMarketMarginGaps = marginGapMap.get(oddsType);
                                //最终PA赔率 原始概率赔率加上水差
                                Double probability = BigDecimalUtils.divide(BigDecimalUtils.changeZero(configMarketMarginGaps.getProbability()), 100);
                                if (probability != 0) {
                                    //原始概率保留4位小数 : 1/原始赔率
                                    double probabilityOdds = BigDecimalUtils.divide(1, BigDecimalUtils.divide(paOddsValue, 100000), 4);
                                    Double finalPaOddsValue = BigDecimalUtils.divide(1, BigDecimalUtils.add(probabilityOdds, probability));
                                    log.info("::{}::数据商抽水赔率加概率差,赛事ID:{},玩法ID:{},计算前赔率:{},计算后赔率:{},margin配置信息:{}", linkId, standardMatchInfo.getId(), marketCategoryId, standardMarketOddsDataMessage.getPaOddsValue(), finalPaOddsValue, JSON.toJSON(configMarketMarginGaps));
                                    standardMarketOddsDataMessage.setPaOddsValue(BigDecimalUtils.multiply(finalPaOddsValue, 100000).intValue());
                                    standardMarketOddsDataMessage.setProbability(BigDecimalUtils.multiply(probability, 100));
                                }
                            }
                        }
                        //欧赔转下马来
                        Double malayOdds = initializeComponent.getEuropeConvertMalayMap().get(standardMarketOddsDataMessage.getPaOddsValue());
                        standardMarketOddsDataMessage.setMalayOddsValue(malayOdds == null ? 0D : malayOdds);
                    }
                    //设置低赔
                    standardMarketDataMessage.setPaOddsValue(minOddsValue);
                }
            }
            standardMarketMessageList.addAll(standardMarketDataMessages);
        }
    }

}
