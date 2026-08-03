package com.panda.merge.component;

import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.BigDecimalUtils;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.panda.merge.component.AutoDiffCountMarketMalay.subDoubleTwo;

/**
 * 需求 ：2269 马来抽水
 * 对足球MY计算，其他赛种忽略
 */

@Slf4j
@Component
public class MyCalculationMarketProcessor extends BaseProcessor {
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
            //子玩法不存在配置查询标准玩法margin
            if (configMarketCategoryMarginOne == null) {
                configMarketCategoryMarginOne = configMarketCategoryMarginService.getItemTwo(linkId, standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getMarketCategoryId(), 1);
            }
            if (null != configMarketCategoryMarginOne) {
                spread = configMarketCategoryMarginOne.getMargin();
            }
            ConfigCategoryAutoDiffTrade configCategoryAutoDiffTrade = null;
            ConfigPlacenumAutoDiffTrade configPlacenumAutoDiffTrade = null;
            //margin 和 水差计算
            //只有篮球/网球/乒乓球 才有坑位水差跟玩法水差
            if (MarginCategoryConfig.SPORT_HEAD.contains(standardMatchInfo.getSportId())) {
                configCategoryAutoDiffTrade = configCategoryAutoDiffTradeService.getItem(linkId, standardMatchInfo.getId(), standardCategoryId, standardMarketDataMessage.getChildMarketCategoryId());
                configPlacenumAutoDiffTrade = configPlaceNumAutoDiffTradeService.getItem(linkId, standardMatchInfo.getId(), standardCategoryId, standardMarketDataMessage.getChildMarketCategoryId(), standardMarketDataMessage.getPlaceNum());
            }
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
                marketOdds.setPaOddsValue(paOddsValue);
            }
            //特殊抽水计算
            autoDiffCountMarketMalay.standardMarketPumping(linkId, standardCategoryId, standardMatchInfo.getId(), marketCategorySell, standardMarketDataMessage);
        }
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
                ConfigMarketAutoDiffTrade marketAutoDiffTrade = null;
                if (standardMatchInfo.getSportId() == 1) {
                    marketAutoDiffTrade = configMarketAutoDiffTradeService.getItem(linkId, standardMatchInfo.getId(),relationMarketId, standardSportMarketOdds.getOddsType());
                }
                Double marketDiffTrade = 0.0;
                Double placenumDiffTrade = 0.0;
                Double categoryDiffTrade = 0.0;
                //盘口水差
                if (marketAutoDiffTrade != null) {
                    marketDiffTrade = marketAutoDiffTrade.getDiffValue();
                    stringBuffer.append(",操盘后台设置的盘口水差值：" + marketDiffTrade);
                }
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

}
