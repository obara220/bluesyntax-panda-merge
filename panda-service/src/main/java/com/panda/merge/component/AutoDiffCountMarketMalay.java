package com.panda.merge.component;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.model.ConfigMarketAutoDiffTrade;
import com.panda.merge.model.MarketCategorySell;
import com.panda.merge.service.ConfigMarketAutoDiffTradeService;
import com.panda.merge.service.PandaOddsConvertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 马来盘水差计算：如大小、单双、让球等玩法
 * @author :  Jimmy
 * @Project Name :  panda_data_realtime_marketodds
 * @Package Name :  com.panda.sport.data.realtime.service.autodiff.count
 * @Description :  TODO
 * @Date: 2020-01-22 17:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Slf4j
@Component
public class AutoDiffCountMarketMalay extends AutoDiffCountSuper {
    @Autowired
    private PandaOddsConvertService pandaOddsConvertService;

    @Autowired
    private ConfigMarketAutoDiffTradeService configMarketAutoDiffTradeService;

    @Autowired
    private InitializeComponent initializeComponent;

    /**
     * 二项盘水差和spread计算 （减spread,加水差）
     * @param standardMarketDataMessage
     * @param diffValue
     * @param standardSportMarketOdds
     * @param isSpread
     */
    public boolean arithmeticMALAY(String linkId,StandardMarketDataMessage standardMarketDataMessage, Double diffValue, Double spread, StandardMarketOddsDataMessage standardSportMarketOdds, boolean isSpread) {
        //马来赔率
        Double malayOddsValue = standardSportMarketOdds.getMalayOddsValue();
        //马来赔率减去spread或者加上水差
        BigDecimal malayAndDiff;
        if(isSpread){
            diffValue =  - diffValue;
        }
        malayAndDiff =  new BigDecimal(Double.toString(malayOddsValue)).add(new BigDecimal(Double.toString(diffValue)));
        if(malayOddsValue > 0 && malayOddsValue <= 1){
            //原马来赔+自动水差>=1
            if (malayAndDiff.doubleValue() > 1){
                //-[2-（计算后的值）]
                Double oddsValue = subDoubleTwo(new BigDecimal(2).subtract(malayAndDiff).doubleValue())*(-1);
                standardSportMarketOdds.setMalayOddsValue(oddsValue);
            }else if (malayAndDiff.doubleValue() <= 0.01){
                //原马来赔+自动水差<= 0.01)：新马来赔固定取0.01
                standardSportMarketOdds.setMalayOddsValue(0.01);
            }else{
                Double malaysia = malayAndDiff.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                standardSportMarketOdds.setMalayOddsValue(malaysia);
            }
        }else if(malayOddsValue > -1 && malayOddsValue < 0){
            //原马来赔+自动水差<=-1
            if (malayAndDiff.doubleValue() <= -1){
                //2 +（计算后的值）
                Double oddsValue = subDoubleTwo(new BigDecimal(2).add(malayAndDiff).doubleValue());
                standardSportMarketOdds.setMalayOddsValue(oddsValue);
            }else if (malayAndDiff.doubleValue() >= -0.01){
                //原马来赔+自动水差>= -0.01)：新马来赔固定取-0.01
                standardSportMarketOdds.setMalayOddsValue(-0.01);
            }else{
                //抽水计算触发
                if(isSpread){
                    //计算前的值为<0 && 计算后的值+spread>=0
                    Double oddsSpeadValue = subDoubleTwo(malayAndDiff.add(new BigDecimal(Double.toString(spread))).doubleValue());
                    if (oddsSpeadValue >= 0){
                        standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        standardMarketDataMessage.setRemark("计算前的值为<0 && 计算后的值+spread>=0,触发封盘");
                        log.info("::{}::标准赛事id:{},标准盘口id:{},统一盘口id:{},三方盘口源id:{},马来+抽水数据不合理，触发封盘。马来赔率:{},参与计算的spread:{},配置spread:{},计算后等于:{},大于等于0",
                                linkId, standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getId(), standardMarketDataMessage.getRelationMarketId(),
                                standardMarketDataMessage.getThirdMarketSourceId(), malayOddsValue, diffValue, spread, oddsSpeadValue);
                        Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                        standardSportMarketOdds.setMalayOddsValue(malaysia);
                        return true;
                    }
                }else {
                    //水差计算触发
                    //计算前的值为<0 && 计算后的值>0,触发封盘，前端提醒[设置水差(x)将触发封盘]
                    if (malayAndDiff.doubleValue() > 0){
                        standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        standardMarketDataMessage.setRemark("计算前的值为<0 && 计算后的值>0,触发封盘");
                        //standardMarketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        log.info("::{}::标准赛事id:{},标准盘口id:{},统一盘口id:{},三方盘口源id:{},马来+水差数据不合理，触发封盘。马来赔率:{},配置的水差:{},计算后的值:{},大于0",
                                linkId, standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getId(), standardMarketDataMessage.getRelationMarketId(),
                                standardMarketDataMessage.getThirdMarketSourceId(),malayOddsValue,diffValue,malayAndDiff);
                        Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                        standardSportMarketOdds.setMalayOddsValue(malaysia);
                        return true;
                    }
                    //计算前的值为<0 && 计算后的值+spread>=0
                    Double oddsSpeadValue = subDoubleTwo(malayAndDiff.add(new BigDecimal(Double.toString(spread))).doubleValue());
                    if (oddsSpeadValue >= 0){
                        standardMarketDataMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        standardMarketDataMessage.setRemark("计算前的值为<0 && 计算后的值+spread>=0,触发封盘");
                        //standardMarketDataMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        log.info("::{}::标准赛事id:{},标准盘口id:{},统一盘口id:{},三方盘口源id:{},马来+水差数据不合理，触发封盘。马来赔率:{},配置的水差:{},配置spread:{},计算后等于:{},大于等于0",
                                linkId, standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getId(), standardMarketDataMessage.getRelationMarketId(),
                                standardMarketDataMessage.getThirdMarketSourceId(), malayOddsValue, diffValue, spread, oddsSpeadValue);
                        Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                        standardSportMarketOdds.setMalayOddsValue(malaysia);
                        return true;
                    }
                }
                Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                standardSportMarketOdds.setMalayOddsValue(malaysia);
            }
        }else{
            log.error("::{}::标准赛事id:{},标准盘口id:{},统一盘口id:{},三方盘口源id:{},马来+水差数据不合理，不计算水差。{}+{}",
                    linkId, standardMarketDataMessage.getStandardMatchInfoId(), standardMarketDataMessage.getId(), standardMarketDataMessage.getRelationMarketId(),
                    standardMarketDataMessage.getThirdMarketSourceId(), malayOddsValue, diffValue);
            return false;
        }
        return true;
    }

    private ConfigMarketAutoDiffTrade getConfigMarketAutoDiffTrade(List<ConfigMarketAutoDiffTrade> configMarketAutoDiffTradeList) {
        ConfigMarketAutoDiffTrade marketAutoDiffTrade = null;
        for (ConfigMarketAutoDiffTrade configMarketAutoDiffTrade : configMarketAutoDiffTradeList) {
            if (configMarketAutoDiffTrade.getDiffValue() > 0 || configMarketAutoDiffTrade.getDiffValue() < 0) {
                marketAutoDiffTrade = configMarketAutoDiffTrade;
                break;
            }
        }
        return marketAutoDiffTrade;
    }

    public static double subDoubleTwo(double d){
        DecimalFormat dFormat = new DecimalFormat();
        dFormat.setMaximumFractionDigits(2);
        dFormat.setGroupingSize(0);
        dFormat.setRoundingMode(RoundingMode.FLOOR);
        return Double.parseDouble(dFormat.format(d));
    }

    /**
     * 特殊抽水
     * 1.判断玩法是否开启特殊抽水
     * 2.区间匹配赔率：赔率最小作为下盘，相等取下盘投注项赔率，最终下盘PA赔率 + 区间赔率 为最终上盘PA赔率
     *
     * @param linkId
     * @param standardCategoryId
     * @param standardMatchId
     * @param marketCategorySell
     * @param standardMarketDataMessage
     */
    public void standardMarketPumping(String linkId, Long standardCategoryId, Long standardMatchId, MarketCategorySell marketCategorySell, StandardMarketDataMessage standardMarketDataMessage) {
        //统一盘口ID
        Long relationMarketId = standardMarketDataMessage.getRelationMarketId();
        // step1
        if (marketCategorySell != null && marketCategorySell.getIsSpecialPumping() != null && marketCategorySell.getIsSpecialPumping() == 1) {
            String specialOddsInterval = marketCategorySell.getSpecialOddsInterval();
            List<StandardMarketOddsDataMessage> marketOddsList = standardMarketDataMessage.getMarketOddsList();
            // step2 根据PA赔率排序 小 - 大
            marketOddsList = marketOddsList.stream().sorted(Comparator.comparing(StandardMarketOddsDataMessage::getPaOddsValue)).collect(Collectors.toList());
            log.info("::{}::特殊抽水根据PA赔率倒序排序,赛事ID:{},玩法ID:{},盘口ID:{},统一盘口ID:{},三方盘口ID:{},特殊抽水赔率区间:{},marketOddsList:{}",
                    linkId, standardMatchId, standardCategoryId, standardMarketDataMessage.getRelationMarketId(), relationMarketId,
                    standardMarketDataMessage.getThirdMarketSourceId(), specialOddsInterval, JSONObject.toJSON(marketOddsList));
            //下盘
            StandardMarketOddsDataMessage downMarketOddsDataMessage = marketOddsList.get(0);
            //上盘
            StandardMarketOddsDataMessage upMarketOddsDataMessage = marketOddsList.get(1);
            //step2 相等取下盘投注项标识赔率，匹配赔率抽水区间，计算上盘
            if (downMarketOddsDataMessage.getPaOddsValue().equals(upMarketOddsDataMessage.getPaOddsValue())) {
                Map<Boolean, StandardMarketOddsDataMessage> marketOddsMap = marketOddsList.stream().collect(Collectors.toMap(StandardMarketOddsDataMessage::getOddsTypeTag, a -> a, (k1, k2) -> k1));
                StandardMarketOddsDataMessage downOddsDataMessage = marketOddsMap.get(true);
                StandardMarketOddsDataMessage upOddsDataMessage = marketOddsMap.get(false);
                //计算上盘
                oddsScopeCalculate(linkId, specialOddsInterval, downOddsDataMessage, upOddsDataMessage, relationMarketId);
            } else {
                //step2 赔率最小作为下盘，匹配赔率抽水区间，计算上盘
                oddsScopeCalculate(linkId, specialOddsInterval, downMarketOddsDataMessage, upMarketOddsDataMessage, relationMarketId);
            }
        } else {
            log.info("::{}::特殊抽水,未启用特殊抽水,赛事ID:{},玩法ID:{}", linkId, standardMatchId, standardCategoryId);
        }
    }

    /**
     * 根据下赔率，计算上赔率
     *
     * @param linkId
     * @param specialOddsInterval       赔率区间配置 格式 {"1.01-1.05":0.07,"1.06-1.19":0.08}
     * @param downMarketOddsDataMessage 下赔
     * @param upMarketOddsDataMessage   上赔
     */
    private void oddsScopeCalculate(String linkId, String specialOddsInterval, StandardMarketOddsDataMessage downMarketOddsDataMessage, StandardMarketOddsDataMessage upMarketOddsDataMessage, Long relationMarketId) {
        JSONObject specialOddsIntervalObj = JSONObject.parseObject(specialOddsInterval);
        for (String oddsScope : specialOddsIntervalObj.keySet()) {
            String[] odds = oddsScope.split("-");
            Integer minOdds = (int) (Double.parseDouble(odds[0]) * 100000);
            Integer maxOdds = (int) (Double.parseDouble(odds[1]) * 100000);
            Double diff = Double.parseDouble(specialOddsIntervalObj.get(oddsScope).toString());
            Integer downPaOddsValue = downMarketOddsDataMessage.getPaOddsValue();
            if (downPaOddsValue >= minOdds && downPaOddsValue <= maxOdds) {
                //根据下盘马来赔计算
                Double downMalayOddsValue = downMarketOddsDataMessage.getMalayOddsValue();
                //下盘赔率加上spread是否>=1
                Double oddsSpeadValue = subDoubleTwo(new BigDecimal(Double.toString(downMalayOddsValue)).add(new BigDecimal(Double.toString(diff))).doubleValue());
                if (oddsSpeadValue >= 1) {
                    //上盘赔率= 2-（下盘赔率+spread）
                    Double oddsValue = subDoubleTwo(new BigDecimal(2).subtract(new BigDecimal(Double.toString(oddsSpeadValue))).doubleValue());
                    log.info("::{}::特殊抽水,盘口ID:{},下盘马来赔加特殊抽水>=1,下马来赔:{},原始上马来赔:{},特殊水差:{},计算后上盘马来赔:{}",
                            linkId, relationMarketId, downMalayOddsValue, upMarketOddsDataMessage.getMalayOddsValue(), diff, oddsValue);
                    upMarketOddsDataMessage.setMalayOddsValue(oddsValue);
                } else {
                    //上盘赔率= -（下盘赔率+spread）
                    Double oddsValue = oddsSpeadValue * (-1);
                    log.info("::{}::特殊抽水,盘口ID:{},下盘马来赔加特殊抽水<1,下马来赔:{},原始上马来赔:{},特殊水差:{},计算后上盘马来赔:{}",
                            linkId, relationMarketId, downMalayOddsValue, upMarketOddsDataMessage.getMalayOddsValue(), diff, oddsValue);
                    upMarketOddsDataMessage.setMalayOddsValue(oddsValue);
                }
                //设置最终的高配paOddsValue
                //将马来赔转欧赔后，计算赔率差绝对值
                Integer upPaOddsValue = BigDecimal.valueOf(initializeComponent.getConvertMalayToEurope(upMarketOddsDataMessage.getMalayOddsValue())).multiply(new BigDecimal(Double.toString(100000))).intValue();
                Integer paOddsValue = upMarketOddsDataMessage.getPaOddsValue();
                upMarketOddsDataMessage.setPaOddsValue(upPaOddsValue);
                log.info("::{}::特殊抽水最终赔率,盘口ID:{},下赔:{},原始上赔:{},计算后新上赔:{},赔率区间:{}:{}",
                        linkId, relationMarketId, downPaOddsValue, paOddsValue, upPaOddsValue, oddsScope, diff);
            }
        }
    }

    public static double arithmeticMALAYTest(Double malayOddsValue, Double diffValue, Double spread,  boolean isSpread) {
        StringBuffer sb = new StringBuffer("开始测试：");
        sb.append(isSpread?"margin":"水  差");
        sb.append("【");
        sb.append("malayOddsValue:"+malayOddsValue+",");
        sb.append("diffValue:"+diffValue+",");
        sb.append("spread:"+spread+",");
        sb.append("isSpread:"+isSpread+",");

        //马来赔率 malayOddsValue
        //马来赔率减去spread或者加上水差
        BigDecimal malayAndDiff;
        if(isSpread){
            diffValue =  - diffValue;
        }
        sb.append("diffValue:"+diffValue+",");
        malayAndDiff =  new BigDecimal(Double.toString(malayOddsValue)).add(new BigDecimal(Double.toString(diffValue)));
        sb.append("malayAndDiff:"+malayAndDiff+",");
        if(malayOddsValue > 0 && malayOddsValue <= 1){
            //原马来赔+自动水差>=1
            if (malayAndDiff.doubleValue() > 1){
                //-[2-（计算后的值）]
                Double oddsValue = subDoubleTwo(new BigDecimal(2).subtract(malayAndDiff).doubleValue())*(-1);
                sb.append("oddsValue1:"+oddsValue+",");
                sb.append("】 ");
                System.out.println(sb.toString());
                return oddsValue;
            }else if (malayAndDiff.doubleValue() <= 0.01){
                //原马来赔+自动水差<= 0.01)：新马来赔固定取0.01
                sb.append("oddsValue2:"+0.01+",");
                sb.append("】 ");
                System.out.println(sb.toString());
                return 0.01;
            }else{
                Double malaysia = malayAndDiff.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                sb.append("oddsValue3:"+malaysia+",");
                sb.append("】 ");
                System.out.println(sb.toString());
                return malaysia;
            }
        }else if(malayOddsValue > -1 && malayOddsValue < 0){
            //原马来赔+自动水差<=-1
            if (malayAndDiff.doubleValue() <= -1){
                //2 +（计算后的值）
                Double oddsValue = subDoubleTwo(new BigDecimal(2).add(malayAndDiff).doubleValue());
                sb.append("oddsValue4:"+oddsValue+",");
                sb.append("】 ");
                System.out.println(sb.toString());
                return oddsValue;
            }else if (malayAndDiff.doubleValue() >= -0.01){
                //原马来赔+自动水差>= -0.01)：新马来赔固定取-0.01
                sb.append("oddsValue5:"+-0.01+",");
                sb.append("】 ");
                System.out.println(sb.toString());
                return -0.01;
            }else{
                //抽水计算触发
                if(isSpread){
                    //计算前的值为<0 && 计算后的值+spread>=0
                    Double oddsSpeadValue = subDoubleTwo(malayAndDiff.add(new BigDecimal(Double.toString(spread))).doubleValue());
                    if (oddsSpeadValue >= 0){
                        Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                        sb.append("oddsValue6:"+malaysia+",");
                        sb.append("】 ");
                        System.out.println(sb.toString());
                        return malaysia;
                    }
                }else {
                    //水差计算触发
                    //计算前的值为<0 && 计算后的值>0,触发封盘，前端提醒[设置水差(x)将触发封盘]
                    if (malayAndDiff.doubleValue() > 0){
                        Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                        sb.append("oddsValue7:"+malaysia+",");
                        sb.append("】 ");
                        System.out.println(sb.toString());
                        return malaysia;
                    }
                    //计算前的值为<0 && 计算后的值+spread>=0
                    Double oddsSpeadValue = subDoubleTwo(malayAndDiff.add(new BigDecimal(Double.toString(spread))).doubleValue());
                    if (oddsSpeadValue >= 0){
                        Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                        sb.append("oddsValue8:"+malaysia+",");
                        sb.append("】 ");
                        System.out.println(sb.toString());
                        return malaysia;
                    }
                }
                Double malaysia = subDoubleTwo(malayAndDiff.doubleValue());
                sb.append("oddsValue9:"+malaysia+"");
                sb.append("】 ");
                System.out.println(sb.toString());
                return malaysia;
            }
        }else{
            sb.append("oddsValue10:异常");
            sb.append("】");
            System.out.println(sb.toString());
            return -999;
        }

    }

    public static void main(String[] args) {
        double malayOddsValue = -0.21;
        double margin = 0.28;
        double diffValue = -0.02;
        double malay = arithmeticMALAYTest(malayOddsValue, margin/2, margin,  true);
        double malay2 = arithmeticMALAYTest(malay, diffValue, margin,  false);
        System.out.println(malay2);

        /*for (int i=0;i<100;i++)
        {
            double m = 0.01+i/100.0;
            double malay = arithmeticMALAYTest(m, 0.02, 0.04,  true);
            arithmeticMALAYTest(malay, 0.04, 0.04,  false);
        }*/

        //arithmeticMALAYTest(Double malayOddsValue, Double diffValue, Double spread,  false)
        //subDoubleTwo(1.917);


//    	List<StandardMarketOddsDataMessage> standardSportMarketOddsList = Lists.newArrayList();
//    	StandardMarketOddsDataMessage odds1 = new StandardMarketOddsDataMessage();
//    	odds1.setOddsType("1");
//    	odds1.setOddsValue(192000);
//    	odds1.setOriginalOddsValue(197836);
//    	odds1.setPaOddsValue(196000);
//    	odds1.setMalayOddsValue(-0.95d);
//    	standardSportMarketOddsList.add(odds1);
//    	StandardMarketOddsDataMessage odds2 = new StandardMarketOddsDataMessage();
//    	odds2.setOddsType("2");
//    	odds2.setOddsValue(196000);
//    	odds2.setOriginalOddsValue(202211);
//    	odds2.setPaOddsValue(192000);
//    	odds2.setMalayOddsValue(0.69d);
//    	standardSportMarketOddsList.add(odds2);
//
//    	AutoDiffCountMarketMalay melay = new AutoDiffCountMarketMalay();
//    	//计算Margin值
//        Double margin = melay.countMarginValue(standardSportMarketOddsList);
//
//        ConfigMarketAutoDiffTrade marketAutoDiffTrade = new ConfigMarketAutoDiffTrade();
//        marketAutoDiffTrade.setDiffValue(-0.1);
//        marketAutoDiffTrade.setOddsType("1");
//
//        //针对马来赔添加水差
//        melay.countMalayOddsValueByDiff(standardSportMarketOddsList, marketAutoDiffTrade, margin);
//
//        //转换计划后的马来赔率为欧赔
//        for (StandardMarketOddsDataMessage standardSportMarketOdds : standardSportMarketOddsList) {
////        	System.out.println(standardSportMarketOdds.getMalayOddsValue());
//        	if(standardSportMarketOdds.getMalayOddsValue() <= -1) {
//        		standardSportMarketOdds.setMalayOddsValue(1d);
//        	}else if(standardSportMarketOdds.getMalayOddsValue() > 1) {
//        		Double newValue = new BigDecimal(-1).divide(new BigDecimal(standardSportMarketOdds.getMalayOddsValue()), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
//        		standardSportMarketOdds.setMalayOddsValue(newValue);
//        	}
//        	System.out.println(standardSportMarketOdds.getMalayOddsValue());
////            standardSportMarketOdds.setPaOddsValue(melay.convertMalayToEurope(standardSportMarketOdds.getMalayOddsValue()));
//        }
//        BigDecimal europeOddsValue = new BigDecimal(0);
//        europeOddsValue = BigDecimal.valueOf(new Double("2.01")).multiply(new BigDecimal(100000));
//
//        System.out.println(europeOddsValue.intValue());
//        System.out.println(2.01d * 100000);
    }
}