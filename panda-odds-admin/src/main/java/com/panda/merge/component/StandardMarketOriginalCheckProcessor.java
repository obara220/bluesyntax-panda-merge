package com.panda.merge.component;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.BigDecimalUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.dto.message.ThirdSportMarketMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdSportMarketOdds;
import com.panda.merge.rocketmq.producer.StandardMatchPreStatusMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 需求 39924
 * AO赔率 检查 开出去的坑位1赔率校验
 */
@Component
@Slf4j
public class StandardMarketOriginalCheckProcessor extends BaseProcessor {

    @Autowired
    private RedisService redisService;
    @Autowired
    private StandardMatchPreStatusMessageProducer standardMatchPreStatusMessageProducer;

    public void standardMarketOriginalProcessor(String linkId, StandardMatchInfo standardMatchInfo) {
        if (!StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())) {
            return;
        }
        //获取最新下发的盘口缓存
        String redisOddsKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS + standardMatchInfo.getId());
        Object obj = redisService.hGetAll(redisOddsKey);
        if (ObjectUtil.isEmpty(obj)) {
            return;
        }
        List<StandardMarketMessage> standardMarketMessageList = new ArrayList<>();
        Map<Long, List<StandardMarketMessage>> standardMarketDataMessageMapNew = (Map<Long, List<StandardMarketMessage>>) obj;
        standardMarketDataMessageMapNew.entrySet().stream().forEach(k -> {
            //只处理需要的玩法，获取坑位1开盘盘口
            if (MarginCategoryConfig.CHECK_MAIN_CATEGORY.contains(Long.valueOf(String.valueOf(k.getKey())))) {
                List<StandardMarketMessage> standardMarketMessages = k.getValue().stream().filter(marketMessage ->
                        marketMessage.getPlaceNum() == 1 && marketMessage.getThirdMarketSourceStatus().equals(Constant.SPORT_MARKET.STATUS.ACTIVE)).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(standardMarketMessages)) {
                    standardMarketMessageList.addAll(standardMarketMessages);
                }
            }
        });
        if (CollectionUtils.isEmpty(standardMarketMessageList)) {
            return;
        }
        log.info("::{}::主玩法赔率异常关闭提前结算,标准盘口信息:{}", linkId, JSONObject.toJSONString(standardMarketMessageList));
        Double value_max = 0D;
        //获取AO盘口缓存
        String aoKey = Constant.REDIS_KEY.RONGHE_AO_THIRD_MARKET_ODDS + standardMatchInfo.getId();
        for (StandardMarketMessage standardMarketMessage : standardMarketMessageList) {
            Long id = standardMarketMessage.getId();
            Object o = redisService.hGet(aoKey, id.toString());
            if (ObjectUtil.isNull(o)) {
                continue;
            }
            ThirdSportMarketMessage thirdSportMarketMessage = (ThirdSportMarketMessage) o;
            value_max = Math.max(value_max, checkProcessor(linkId, standardMarketMessage, thirdSportMarketMessage));
        }
        log.info("::{}::主玩法赔率异常关闭提前结算,最终值：{}，", linkId, value_max);
        if (value_max == 0D) {
            return;
        }
        value_max = value_max > 0.05 ? 0.05 : value_max;
        String value_maxKey = Constant.REDIS_KEY.RONGHE_MATCH_PRE_VALUE + standardMatchInfo.getId();
        Object value_max_obj = redisService.get(value_maxKey);
        Double value_max_cache = 0D;
        if (ObjectUtil.isNotNull(value_max_obj)) {
            value_max_cache = (double) value_max_obj;
        }
        //和缓存对比，相同值只下发一次
        if (value_max_cache.equals(value_max)) {
            return;
        }
        redisService.set(value_maxKey, value_max, marketCacheTime(standardMatchInfo.getBeginTime()));
        // 融合校验值大于1%，下发风控告知前端Apply模块黄色
        //融合校验值大于3%，下发风控告知前端Apply模块橙色
        //融合校验值大于5%，下发风控告知前端Apply模块红色 并 下发赛事级别提前结算关盘
        standardMatchPreStatusMessageProducer.sendStandardMatchPreStatus(linkId, standardMatchInfo, value_max);
    }

    /**
     * 标准PA赔率 和 AO原始 投注项对比 找出最大值
     *
     * @param standardMarketMessage   标准盘口
     * @param thirdSportMarketMessage AO三方盘口
     */
    private static Double checkProcessor(String linkId, StandardMarketMessage standardMarketMessage, ThirdSportMarketMessage thirdSportMarketMessage) {
        double value = 0D;
        List<StandardMarketOddsMessage> marketOddsList = standardMarketMessage.getMarketOddsList();
        List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketMessage.getThirdSportMarketOddsList();
        if (CollectionUtils.isEmpty(marketOddsList) || CollectionUtils.isEmpty(thirdSportMarketOddsList)) {
            log.info("::{}::主玩法赔率异常关闭提前结算,盘口ID:{},标准投注项赔率或者AO投注项赔率不存在,返回0", linkId, standardMarketMessage.getId());
            return value;
        }
        log.info("::{}::主玩法赔率异常关闭提前结算，盘口ID:{},标准盘口数据:{},AO盘口数据:{}",
                linkId, standardMarketMessage.getId(), JSONObject.toJSONString(standardMarketMessage), JSONObject.toJSONString(thirdSportMarketMessage));
        Map<String, StandardMarketOddsMessage> standardMarketOddsMap = marketOddsList.stream().collect(Collectors.toMap(StandardMarketOddsMessage::getOddsType, i -> i, (oldValue, newValue) -> newValue));
        Map<String, ThirdSportMarketOdds> thirdSportMarketOddsMap = thirdSportMarketOddsList.stream().collect(Collectors.toMap(ThirdSportMarketOdds::getOddsType, i -> i, (oldValue, newValue) -> newValue));
        for (Map.Entry<String, StandardMarketOddsMessage> entry : standardMarketOddsMap.entrySet()) {
            String oddsType = entry.getKey();
            StandardMarketOddsMessage standardMarketOddsMessage = entry.getValue();
            ThirdSportMarketOdds thirdSportMarketOdds = thirdSportMarketOddsMap.get(oddsType);
            if (null == thirdSportMarketOdds) {
                log.info("::{}::主玩法赔率异常关闭提前结算,盘口ID:{},投注项:{},标准投注项没有找到AO投注项赔率", linkId, standardMarketMessage.getId(), oddsType);
                continue;
            }
            if (standardMarketOddsMessage.getPaOddsValue() == 0
                    || thirdSportMarketOdds.getOriginalOddsValue() == 0
                    || standardMarketOddsMessage.getPaOddsValue().equals(thirdSportMarketOdds.getOriginalOddsValue())) {
                log.info("::{}::主玩法赔率异常关闭提前结算,盘口ID:{},投注项:{},标准赔率或者AO赔率不正常", linkId, standardMarketMessage.getId(), oddsType);
                continue;
            }
            //(PA_Euro_Odds - AO_Original_Euro_Odds) / PA_Euro_Odds
            double nowValue = BigDecimalUtils.divide(standardMarketOddsMessage.getPaOddsValue() - thirdSportMarketOdds.getOriginalOddsValue(), standardMarketOddsMessage.getPaOddsValue(), 2);
            value = Math.max(value, nowValue);
            log.info("::{}::主玩法赔率异常关闭提前结算,盘口ID:{},投注项:{},nowValue:{},MAX:{}", linkId, standardMarketMessage.getId(), oddsType, nowValue, value);
        }
        return value;
    }


    public static void main(String[] args) {
        String str = "[{\"addition1\":\"1.75\",\"addition5\":\"\",\"categorySuspended\":0,\"childMarketCategoryId\":18,\"dataSourceCode\":\"AO\",\"endEdStatus\":0,\"id\":142828576140224816,\"marketCategoryId\":18,\"marketOddsList\":[{\"active\":1,\"addition1\":\"\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"addition5\":\"\",\"clearProbability\":0,\"dataSourceCode\":\"AO\",\"i18nNames\":[{\"languageType\":\"en\",\"text\":\"over\"}],\"id\":144465438442021459,\"malayOddsValue\":0.83,\"marketId\":142828576140224816,\"modifyTime\":1681099468337,\"name\":\"over\",\"nameCode\":1645266757263601665,\"oddsFieldsTemplateId\":96,\"oddsType\":\"Over\",\"oddsValue\":183000,\"orderOdds\":1,\"originalOddsValue\":188500,\"paOddsValue\":183000,\"settlementResult\":\"\",\"settlementResultText\":\"\",\"status\":0,\"thirdOddsFieldSourceId\":\"313345692867575810_20005_1.75_4\",\"thirdTemplateSourceId\":\"AO:20005:4\"},{\"active\":1,\"addition1\":\"\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"addition5\":\"\",\"clearProbability\":0,\"dataSourceCode\":\"AO\",\"i18nNames\":[{\"languageType\":\"en\",\"text\":\"under\"}],\"id\":140981339125053135,\"malayOddsValue\":-0.92,\"marketId\":142828576140224816,\"modifyTime\":1681099468337,\"name\":\"under\",\"nameCode\":1645266757305544706,\"oddsFieldsTemplateId\":95,\"oddsType\":\"Under\",\"oddsValue\":208000,\"orderOdds\":2,\"originalOddsValue\":212900,\"paOddsValue\":208000,\"settlementResult\":\"\",\"settlementResultText\":\"\",\"status\":0,\"thirdOddsFieldSourceId\":\"313345692867575810_20005_1.75_5\",\"thirdTemplateSourceId\":\"AO:20005:5\"}],\"marketSource\":0,\"marketType\":1,\"modifyTime\":1681099452785,\"nameCode\":1645266757204881409,\"numberOfWinners\":1,\"oddsMetric\":24400,\"oddsName\":\"1st half - Over/Under\",\"orderNo\":0,\"orderType\":\"\",\"paStatus\":0,\"paStatusReason\":\"{\\\"zs\\\":\\\"操盘盘口位置状态为：1，盘口状态发生变化\\\",\\\"en\\\":\\\"Market position status : 1, market status changed\\\"}\",\"placeNum\":1,\"placeNumId\":\"3440103_18_18_1\",\"placeNumStatus\":1,\"scopeId\":\"1\",\"showMarketResult\":0,\"status\":1,\"thirdMarketSourceId\":\"313345692867575810_20005_1.75\",\"thirdMarketSourceStatus\":0,\"tradeType\":0},{\"addition1\":\"0\",\"addition2\":\"0\",\"addition3\":\"0\",\"addition4\":\"0\",\"addition5\":\"\",\"categorySuspended\":0,\"childMarketCategoryId\":19,\"dataSourceCode\":\"AO\",\"endEdStatus\":0,\"id\":140941576001560756,\"marketCategoryId\":19,\"marketOddsList\":[{\"active\":1,\"addition1\":\"100592\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"addition5\":\"\",\"clearProbability\":0,\"dataSourceCode\":\"AO\",\"i18nNames\":[{\"languageType\":\"en\",\"text\":\"{competitor1}\"}],\"id\":148651764234853565,\"malayOddsValue\":0.94,\"marketId\":140941576001560756,\"modifyTime\":1681099468369,\"name\":\"{competitor1}\",\"nameCode\":1645266754684104705,\"oddsFieldsTemplateId\":146,\"oddsType\":\"1\",\"oddsValue\":194000,\"orderOdds\":1,\"originalOddsValue\":199900,\"paOddsValue\":194000,\"settlementResult\":\"\",\"settlementResultText\":\"\",\"status\":0,\"thirdOddsFieldSourceId\":\"313345692867575810_20004_0_6\",\"thirdTemplateSourceId\":\"AO:20004:6\"},{\"active\":1,\"addition1\":\"135976\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"addition5\":\"\",\"clearProbability\":0,\"dataSourceCode\":\"AO\",\"i18nNames\":[{\"languageType\":\"en\",\"text\":\"{competitor2}\"}],\"id\":144261514065247835,\"malayOddsValue\":0.96,\"marketId\":140941576001560756,\"modifyTime\":1681099468369,\"name\":\"{competitor2}\",\"nameCode\":1645266754738630658,\"oddsFieldsTemplateId\":147,\"oddsType\":\"2\",\"oddsValue\":196000,\"orderOdds\":2,\"originalOddsValue\":200000,\"paOddsValue\":196000,\"settlementResult\":\"\",\"settlementResultText\":\"\",\"status\":0,\"thirdOddsFieldSourceId\":\"313345692867575810_20004_0_7\",\"thirdTemplateSourceId\":\"AO:20004:7\"}],\"marketSource\":0,\"marketType\":1,\"modifyTime\":1681099452785,\"nameCode\":1645266754625384450,\"numberOfWinners\":1,\"oddsMetric\":100,\"oddsName\":\"1st half - Asian Handicap\",\"orderNo\":0,\"orderType\":\"\",\"paStatus\":0,\"paStatusReason\":\"{\\\"zs\\\":\\\"操盘盘口位置状态为：1，盘口状态发生变化\\\",\\\"en\\\":\\\"Market position status : 1, market status changed\\\"}\",\"placeNum\":1,\"placeNumId\":\"3440103_19_19_1\",\"placeNumStatus\":1,\"scopeId\":\"1\",\"showMarketResult\":0,\"status\":1,\"thirdMarketSourceId\":\"313345692867575810_20004_0\",\"thirdMarketSourceStatus\":0,\"tradeType\":0},{\"addition1\":\"0\",\"addition2\":\"0\",\"addition3\":\"0\",\"addition4\":\"0\",\"addition5\":\"\",\"categorySuspended\":0,\"childMarketCategoryId\":4,\"dataSourceCode\":\"AO\",\"endEdStatus\":0,\"id\":143321444681059215,\"marketCategoryId\":4,\"marketOddsList\":[{\"active\":1,\"addition1\":\"100592\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"addition5\":\"\",\"clearProbability\":0,\"dataSourceCode\":\"AO\",\"i18nNames\":[{\"languageType\":\"en\",\"text\":\"{competitor1}\"}],\"id\":141941111141131115,\"malayOddsValue\":0.64,\"marketId\":143321444681059215,\"modifyTime\":1681101431253,\"name\":\"{competitor1}\",\"nameCode\":1645266725852459010,\"oddsFieldsTemplateId\":3,\"oddsType\":\"1\",\"oddsValue\":194000,\"orderOdds\":1,\"originalOddsValue\":199900,\"paOddsValue\":164000,\"settlementResult\":\"\",\"settlementResultText\":\"\",\"status\":0,\"thirdOddsFieldSourceId\":\"313345692867575810_10004_0_6\",\"thirdTemplateSourceId\":\"AO:10004:6\"},{\"active\":1,\"addition1\":\"135976\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"addition5\":\"\",\"clearProbability\":0,\"dataSourceCode\":\"AO\",\"i18nNames\":[{\"languageType\":\"en\",\"text\":\"{competitor2}\"}],\"id\":147470500143502044,\"malayOddsValue\":-0.74,\"marketDiffValue\":0.3,\"marketId\":143321444681059215,\"modifyTime\":1681101431253,\"name\":\"{competitor2}\",\"nameCode\":1645266725911179265,\"oddsFieldsTemplateId\":4,\"oddsType\":\"2\",\"oddsValue\":196000,\"orderOdds\":2,\"originalOddsValue\":200000,\"paOddsValue\":235000,\"settlementResult\":\"\",\"settlementResultText\":\"\",\"status\":0,\"thirdOddsFieldSourceId\":\"313345692867575810_10004_0_7\",\"thirdTemplateSourceId\":\"AO:10004:7\"}],\"marketSource\":0,\"marketType\":1,\"modifyTime\":1681099452785,\"nameCode\":1645266725630160898,\"numberOfWinners\":1,\"oddsMetric\":100,\"oddsName\":\"FT - Asian Handicap\",\"orderNo\":0,\"orderType\":\"\",\"paStatus\":0,\"placeNum\":1,\"placeNumId\":\"3440103_4_4_1\",\"placeNumStatus\":0,\"scopeId\":\"3\",\"showMarketResult\":0,\"status\":0,\"thirdMarketSourceId\":\"313345692867575810_10004_0\",\"thirdMarketSourceStatus\":0,\"tradeType\":0},{\"addition1\":\"3.75\",\"addition5\":\"\",\"categorySuspended\":0,\"childMarketCategoryId\":2,\"dataSourceCode\":\"AO\",\"endEdStatus\":0,\"id\":143197591431138529,\"marketCategoryId\":2,\"marketOddsList\":[{\"active\":1,\"addition1\":\"\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"addition5\":\"\",\"clearProbability\":0,\"dataSourceCode\":\"AO\",\"i18nNames\":[{\"languageType\":\"en\",\"text\":\"over\"}],\"id\":148403676514071052,\"malayOddsValue\":0.87,\"marketId\":143197591431138529,\"modifyTime\":1681099467999,\"name\":\"over\",\"nameCode\":1645266728171909121,\"oddsFieldsTemplateId\":2,\"oddsType\":\"Over\",\"oddsValue\":187000,\"orderOdds\":1,\"originalOddsValue\":192400,\"paOddsValue\":187000,\"settlementResult\":\"\",\"settlementResultText\":\"\",\"status\":0,\"thirdOddsFieldSourceId\":\"313345692867575810_10005_3.75_4\",\"thirdTemplateSourceId\":\"AO:10005:4\"},{\"active\":1,\"addition1\":\"\",\"addition2\":\"\",\"addition3\":\"\",\"addition4\":\"\",\"addition5\":\"\",\"clearProbability\":0,\"dataSourceCode\":\"AO\",\"i18nNames\":[{\"languageType\":\"en\",\"text\":\"under\"}],\"id\":149255138026045331,\"malayOddsValue\":-0.97,\"marketId\":143197591431138529,\"modifyTime\":1681099467999,\"name\":\"under\",\"nameCode\":1645266728213852161,\"oddsFieldsTemplateId\":1,\"oddsType\":\"Under\",\"oddsValue\":203000,\"orderOdds\":2,\"originalOddsValue\":208100,\"paOddsValue\":203000,\"settlementResult\":\"\",\"settlementResultText\":\"\",\"status\":0,\"thirdOddsFieldSourceId\":\"313345692867575810_10005_3.75_5\",\"thirdTemplateSourceId\":\"AO:10005:5\"}],\"marketSource\":0,\"marketType\":1,\"modifyTime\":1681099452785,\"nameCode\":1645266728108994561,\"numberOfWinners\":1,\"oddsMetric\":15700,\"oddsName\":\"FT - Over/Under\",\"orderNo\":0,\"orderType\":\"\",\"paStatus\":0,\"paStatusReason\":\"{\\\"zs\\\":\\\"操盘盘口位置状态为：1，盘口状态发生变化\\\",\\\"en\\\":\\\"Market position status : 1, market status changed\\\"}\",\"placeNum\":1,\"placeNumId\":\"3440103_2_2_1\",\"placeNumStatus\":1,\"scopeId\":\"3\",\"showMarketResult\":0,\"status\":1,\"thirdMarketSourceId\":\"313345692867575810_10005_3.75\",\"thirdMarketSourceStatus\":0,\"tradeType\":0}]";
        List<StandardMarketMessage> standardMarketMessageList = JSONObject.parseArray(str, StandardMarketMessage.class);
        Map<Long, ThirdSportMarketMessage> map = new HashMap();
        map.put(142828576140224816L, JSONObject.parseObject("{\"addition1\":\"1.75\",\"dataSourceCode\":\"AO\",\"marketCategoryId\":18,\"marketType\":1,\"modifyTime\":1681099452785,\"numberOfWinners\":1,\"oddsName\":\"1st half - Over/Under\",\"offerLineId\":1,\"placeNum\":1,\"referenceId\":3440103,\"relationMarketId\":142828576140224816,\"status\":0,\"thirdMarketSourceId\":\"313345692867575810_20005_1.75\",\"thirdMarketSourceStatus\":0,\"thirdSportMarketOddsList\":[{\"active\":1,\"dataSourceCode\":\"AO\",\"id\":144465438442021459,\"marketId\":142828576140224816,\"modifyTime\":1681099452788,\"oddsType\":\"Over\",\"oddsValue\":183000,\"orderOdds\":1,\"originalOddsValue\":188500,\"thirdOddsFieldSourceId\":\"313345692867575810_20005_1.75_4\"},{\"active\":1,\"dataSourceCode\":\"AO\",\"id\":140981339125053135,\"marketId\":142828576140224816,\"modifyTime\":1681099452788,\"oddsType\":\"Under\",\"oddsValue\":208000,\"orderOdds\":2,\"originalOddsValue\":212900,\"thirdOddsFieldSourceId\":\"313345692867575810_20005_1.75_5\"}]}", ThirdSportMarketMessage.class));
        map.put(140941576001560756L, JSONObject.parseObject("{\"addition1\":\"0\",\"addition2\":\"0\",\"addition3\":\"0\",\"addition4\":\"0\",\"dataSourceCode\":\"AO\",\"marketCategoryId\":19,\"marketType\":1,\"modifyTime\":1681099452785,\"numberOfWinners\":1,\"oddsName\":\"1st half - Asian Handicap\",\"offerLineId\":1,\"placeNum\":1,\"referenceId\":3440103,\"relationMarketId\":140941576001560756,\"status\":0,\"thirdMarketSourceId\":\"313345692867575810_20004_0\",\"thirdMarketSourceStatus\":0,\"thirdSportMarketOddsList\":[{\"active\":1,\"addition1\":\"100592\",\"dataSourceCode\":\"AO\",\"id\":148651764234853565,\"marketId\":140941576001560756,\"modifyTime\":1681099452788,\"oddsType\":\"1\",\"oddsValue\":194000,\"orderOdds\":1,\"originalOddsValue\":199900,\"thirdOddsFieldSourceId\":\"313345692867575810_20004_0_6\"},{\"active\":1,\"addition1\":\"135976\",\"dataSourceCode\":\"AO\",\"id\":144261514065247835,\"marketId\":140941576001560756,\"modifyTime\":1681099452788,\"oddsType\":\"2\",\"oddsValue\":196000,\"orderOdds\":2,\"originalOddsValue\":200000,\"thirdOddsFieldSourceId\":\"313345692867575810_20004_0_7\"}]}", ThirdSportMarketMessage.class));
        map.put(143321444681059215L, JSONObject.parseObject("{\"addition1\":\"0\",\"addition2\":\"0\",\"addition3\":\"0\",\"addition4\":\"0\",\"dataSourceCode\":\"AO\",\"marketCategoryId\":4,\"marketType\":1,\"modifyTime\":1681099452785,\"numberOfWinners\":1,\"oddsName\":\"FT - Asian Handicap\",\"offerLineId\":1,\"placeNum\":1,\"referenceId\":3440103,\"relationMarketId\":143321444681059215,\"status\":0,\"thirdMarketSourceId\":\"313345692867575810_10004_0\",\"thirdMarketSourceStatus\":0,\"thirdSportMarketOddsList\":[{\"active\":1,\"addition1\":\"100592\",\"dataSourceCode\":\"AO\",\"id\":141941111141131115,\"marketId\":143321444681059215,\"modifyTime\":1681099452785,\"oddsType\":\"1\",\"oddsValue\":194000,\"orderOdds\":1,\"originalOddsValue\":199900,\"thirdOddsFieldSourceId\":\"313345692867575810_10004_0_6\"},{\"active\":1,\"addition1\":\"135976\",\"dataSourceCode\":\"AO\",\"id\":147470500143502044,\"marketId\":143321444681059215,\"modifyTime\":1681099452785,\"oddsType\":\"2\",\"oddsValue\":196000,\"orderOdds\":2,\"originalOddsValue\":200000,\"thirdOddsFieldSourceId\":\"313345692867575810_10004_0_7\"}]}", ThirdSportMarketMessage.class));
        map.put(143197591431138529L, JSONObject.parseObject("{\"addition1\":\"3.75\",\"dataSourceCode\":\"AO\",\"marketCategoryId\":2,\"marketType\":1,\"modifyTime\":1681099452785,\"numberOfWinners\":1,\"oddsName\":\"FT - Over/Under\",\"offerLineId\":1,\"placeNum\":1,\"referenceId\":3440103,\"relationMarketId\":143197591431138529,\"status\":0,\"thirdMarketSourceId\":\"313345692867575810_10005_3.75\",\"thirdMarketSourceStatus\":0,\"thirdSportMarketOddsList\":[{\"active\":1,\"dataSourceCode\":\"AO\",\"id\":148403676514071052,\"marketId\":143197591431138529,\"modifyTime\":1681099452785,\"oddsType\":\"Over\",\"oddsValue\":187000,\"orderOdds\":1,\"originalOddsValue\":192400,\"thirdOddsFieldSourceId\":\"313345692867575810_10005_3.75_4\"},{\"active\":1,\"dataSourceCode\":\"AO\",\"id\":149255138026045331,\"marketId\":143197591431138529,\"modifyTime\":1681099452785,\"oddsType\":\"Under\",\"oddsValue\":203000,\"orderOdds\":2,\"originalOddsValue\":208100,\"thirdOddsFieldSourceId\":\"313345692867575810_10005_3.75_5\"}]}", ThirdSportMarketMessage.class));
        Double value_max = 0D;
        for (StandardMarketMessage standardMarketMessage : standardMarketMessageList) {
            Long id = standardMarketMessage.getId();
            ThirdSportMarketMessage thirdSportMarketMessage = map.get(id);
            value_max = Math.max(value_max, checkProcessor("linkId", standardMarketMessage, thirdSportMarketMessage));
        }
        log.info("::{}::主玩法赔率异常关闭提前结算,最终值：{}，", "linkId", value_max);
        Double value_max_cache = 0D;
        //和缓存对比，相同值只下发一次
        if (value_max_cache.equals(value_max)) {
            return;
        }
        System.out.println();
    }
}
