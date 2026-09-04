package com.panda.merge.job.common;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.config.A99ParamConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.ThirdSportMarketMessage;
import com.panda.merge.enums.RequestTypeEnum;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdSportMarketOdds;
import com.panda.merge.rocketmq.common.A99CalculationMarketProcessor;
import com.panda.merge.rocketmq.processor.A99ThirdAllBatchMarketProcessor;
import com.panda.merge.rocketmq.processor.A99ThirdMatchMarketProcessor;
import com.panda.merge.rocketmq.producer.A99ThirdSportMarketMergeProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.panda.merge.common.enums.Constant.REDIS_KEY.*;

@Slf4j
@Component
public class A99MarketOddsCommon {

    @Autowired
    public RedisService redisService;

    @Autowired
    private A99ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    @Lazy
    public A99ThirdSportMarketMergeProducer thirdSportMarketMergeProducer;
    @Autowired
    private A99CalculationMarketProcessor calculationMarketProcessor;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private A99ParamConfig a99ParamConfig;
    /**
     * @param matchIds 赛事id
     * @param matchType 0:滚球; 1:早盘
     * @param flag 1:15秒计算一次滚球; 2:30秒计算一次早盘; 3:每3秒计算一次
     */
    @Async("A99JobThreadPool")
    public void calculateMarketOdds(List<Long> matchIds, int matchType, int flag){
      /*  StopWatch stopWatch = new StopWatch();*/
     /*   try {*/
        log.info("准备计算A99赔率,赛事id:{}, 赛事类型:{}", matchIds, matchType);
       /* stopWatch.start("match");*/
//        List<Long> referenceIds = Convert.convert(List.class, standardCategoryIds.split(","));
        //标准赛事
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoService.getItems(matchIds);
        //标准赛事ID,标准赛事信息
        Map<Long, StandardMatchInfo> standardMatchInfoMap = standardMatchInfos.stream().collect(Collectors.toMap(StandardMatchInfo::getId, Function.identity(), (v1, v2) -> v1));
//        stopWatch.stop();
//        stopWatch.start("sell");
        //赛事开售信息
        List<StandardSportMarketSell> standardSportMarketSells = standardSportMarketSellService.getItems(matchIds);
        Map<Long, StandardSportMarketSell> standardSellMap = standardSportMarketSells.stream().collect(Collectors.toMap(StandardSportMarketSell::getMatchInfoId, Function.identity(), (v1, v2) -> v1));
//        stopWatch.stop();
//        stopWatch.start("maintain");
        //根据赛事id获取开启了A99的玩法集id
        Map<String, String> maintainDataSourceMap = redisService.hGetAll(AO_MAINTAIN_DATA_SOURCE);
//        stopWatch.stop();
        String matchIdRedisKey = matchType == 0 ? Constant.REDIS_KEY.RONGHE_A99_LIVE_MATCH_IDS : Constant.REDIS_KEY.RONGHE_A99_PRE_MATCH_IDS;
        String redisKeyPrefix = matchType == 0 ? Constant.REDIS_KEY.RONGHE_A99_THIRD_MARKET_ODDS_LIVE : Constant.REDIS_KEY.RONGHE_A99_THIRD_MARKET_ODDS_PRE;

        Map<Long, Map<String, Object>> groupByMarket = null;
        Map<String, Object> allMarketCache = null;
        //存放玩法集类型,[g_goal, g_corner, g_booking...],把同一场赛事的所有玩法集类型合并到一起，一次下发给A01
        List<String> requestTypeList = null;
//        stopWatch.start("A99MatchIdcache");
        Map<String, Object> cacheMatchMap = redisService.hGetAll(matchIdRedisKey);
//        stopWatch.stop();
        for (Long matchId : matchIds) {
//            stopWatch.start("for_Match");
            //校验赛事的状态，如果是早盘的赛事，已经开赛就不再下发早盘赔率，如果是滚球的赛事，已经完赛就不再下发赔率
            StandardMatchInfo standardMatchInfo = standardMatchInfoMap.get(matchId);
            if (ObjectUtil.isNull(standardMatchInfo)) {
                log.info("赛事id:{}, 赛事信息为空, 跳过处理", matchId);
                continue;
            }
            int oddsLive = isOddsLive(matchId);
            log.info("赛事id:{}, 滚球赔率标识:{}", matchId, oddsLive);
            if((standardMatchInfo.getMatchStatus() != 0 || oddsLive != 1) && matchType == 1){
                //如果赛事状态不是未开赛，不下发早盘赔率
                log.info("赛事id:{},早盘赛事状态为{},oddsLive为{},不计算A99赔率", matchId, standardMatchInfo.getMatchStatus(), oddsLive);
                continue;
            } else if((standardMatchInfo.getMatchStatus() != 1 || oddsLive != 0) && matchType == 0) {
                //如果赛事状态不是滚球，不下发滚球赔率 事id:40311470,滚球赛事状态为0,oddsLive为1,不计算A99赔率
                log.info("赛事id:{},滚球赛事状态为{},oddsLive为{},不计算A99赔率", matchId, standardMatchInfo.getMatchStatus(), oddsLive);
                continue;
            } else if(standardMatchInfo.getMatchOver() == 1) {
                //已经完赛不下发赔率
                log.info("赛事id:{},赛事状态为已完赛,不计算A99赔率", matchId);
                continue;
            }
            StandardSportMarketSell standardSportMarketSell = standardSellMap.get(matchId);
            if (ObjectUtil.isNull(standardSportMarketSell)) {
                log.info("赛事id:{}, 赛事开售信息为空, 跳过处理", matchId);
                continue;
            }
            if (matchType == 1 && !standardSportMarketSell.getPreMatchSellStatus().equalsIgnoreCase(Constant.STANDARD_MATCH_SELL.SELL_STATUS.SOLD)) {
                //如果是早盘赛事，且早盘开售状态不是开售，不下发赔率
                log.info("赛事id:{},早盘开售状态为{},不计算A99赔率", matchId, standardSportMarketSell.getPreMatchSellStatus());
                continue;
            } else if (matchType == 0 && !standardSportMarketSell.getLiveMatchSellStatus().equalsIgnoreCase(Constant.STANDARD_MATCH_SELL.SELL_STATUS.SOLD)) {
                //如果是滚球赛事，且滚球开售状态不是开售，不下发赔率
                log.info("赛事id:{},滚球开售状态为{},不计算A99赔率", matchId, standardSportMarketSell.getLiveMatchSellStatus());
                continue;
            }
            log.info("赛事id:{}, 赛事状态校验通过:{}", matchId);
            //存放玩法集类型,[g_goal, g_corner, g_booking...],把同一场赛事的所有玩法集类型合并到一起，一次下发给A01
            requestTypeList = new LinkedList<>();

            //数据源权重
            String weightRedisKey = Constant.REDIS_KEY.RONGHE_A99_DATA_SOURCE_WEIGHT + ":" + matchId + ":" + matchType;
            Map<Object, Object> weightMap = redisService.hGetAll(weightRedisKey);

            //玩法警戒值
            Map<String, Object> cautionMap = redisService.hGetAll(RONGHE_A99_DATA_SOURCE_CAUTION_VALUE + ":" + matchId + ":" + matchType);

            //赔率变化差值
            String oddsChangeDiffKeyPrefix = matchType == 0 ? RONGHE_A99_ODDS_CHANGE_DIFFERENCE_LIVE : RONGHE_A99_ODDS_CHANGE_DIFFERENCE_PRE + matchId;
            Map<String, Object> oddsChangeDiffMap = redisService.hGetAll(oddsChangeDiffKeyPrefix);

            List<String> validMarketIds = new ArrayList<>(24);
            List<String> marketKeys = new ArrayList<>(24);
            a99ParamConfig.getStandardMarketIds().forEach(marketId -> {
                //根据玩法id获取玩法集id
                String categorySetId = getCategorySetId(a99ParamConfig.getCategoryMap(), marketId);
                log.info("赛事id:{}, 根据玩法id:{}获取到玩法集:{}", matchId, marketId, categorySetId);
                //判断这场赛事A99是否开启了这个玩法集
                if (cacheMatchMap.containsKey(matchId.toString())) {
                    Object cacheObj = cacheMatchMap.get(matchId.toString());
                    log.info("赛事id:{}, 当前已开启的玩法集:{}", matchId, marketId, cacheObj);
                    if (ObjectUtil.isNotEmpty(cacheObj)) {
                        String categorySetIds = (String)cacheObj;
                        if (categorySetIds.contains(categorySetId)) {
                            validMarketIds.add(marketId);
                            marketKeys.add(redisKeyPrefix + matchId + ":" + marketId);
                            log.info("赛事id:{}, 当前已开启的玩法集包含:{}", matchId, categorySetId);
                        }
                    }
                }
            });


            //获取该赛事下所有校验通过的玩法对应的玩法缓存
            allMarketCache = redisService.syncOddsMultiGetAll(marketKeys);
            log.info("赛事id:{},获取到赛事下所有玩法缓存数量:{}", matchId, allMarketCache.size());
            if (!allMarketCache.isEmpty()) {
                //根据玩法id分组: Map<玩法id, Map<盘口id, ThirdSportMarketMessage>>
                groupByMarket = allMarketCache.entrySet().stream()
                        .collect(Collectors.groupingBy(
                                entry -> ((ThirdSportMarketMessage) entry.getValue()).getMarketCategoryId(),
                                Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue
                                )
                        ));
                log.info("赛事id:{},根据玩法id分组数量:{}", matchId, groupByMarket.size());
                for (String marketId : validMarketIds) {
                    String categorySetId = getCategorySetId(a99ParamConfig.getCategoryMap(), marketId);
                    if (groupByMarket.containsKey(Long.valueOf(marketId))) {
                        List<ThirdSportMarketMessage> matchMarketList = new ArrayList<>();
//                        Map<String, Object> thirdMarketMap = redisService.hGetAll(redisKeyPrefix + matchId + ":" + marketId);
                        Map<String, Object> thirdMarketMap = groupByMarket.get(Long.valueOf(marketId));
                        log.info("赛事id:{},玩法id:{},三方玩法数量:{}", matchId, marketId, thirdMarketMap.size());
                        Set<String> keys = thirdMarketMap.keySet();
//                    Set<String> keys = redisService.keys(redisKeyPrefix + matchId + ":" + marketId);
                        //按盘口id分组
                        Map<String, List<String>> map = keys.stream().collect(Collectors.groupingBy(key -> {
                            String[] arr = key.split(":");
                            return arr.length == 2 ? arr[1] : "";
                        }));
                        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                            log.info("准备计算A99赔率,赛事id:{},玩法id:{},盘口id:{}", matchId, marketId, entry.getKey());
                            List<String> redisItems = entry.getValue();
                            StringBuffer stringBuffer = new StringBuffer();
                            stringBuffer.append("当前缓存的数据源赔率有")
                                    .append(redisItems.size())
                                    .append("家:");
                            List<ThirdSportMarketMessage> validThirdSportMarketList = new ArrayList<>(10);
                            for (String item : redisItems) {
                                ThirdSportMarketMessage thirdSportMarketMessage = (ThirdSportMarketMessage)thirdMarketMap.get(item);
                                //校验当前数据源是否处理维护中，如果数据源正在维护，不计算A99赔率, 返回true则说明当前数据源正在维护
                                boolean dataSourceIsMaintain = checkDataSourceIsMaintain(thirdSportMarketMessage.getDataSourceCode(), maintainDataSourceMap);
                                boolean expiredFlag;
                                if (matchType == 0) {
                                    //校验赔率是否过期，滚球超过1分钟视为无效
                                    expiredFlag = validThirdMarket(thirdSportMarketMessage, 60*2);
                                } else {
                                    expiredFlag = validThirdMarket(thirdSportMarketMessage, 10*60*60);
                                }
                                if (dataSourceIsMaintain) {
                                    stringBuffer.append("数据源:")
                                            .append(thirdSportMarketMessage.getDataSourceCode())
                                            .append("当前正在维护中,过滤当前数据源赔率")
                                            .append(";");
                                } else if (!expiredFlag) {
                                    stringBuffer.append("数据源:")
                                            .append(thirdSportMarketMessage.getDataSourceCode())
                                            .append("赔率下发时间:")
                                            .append(thirdSportMarketMessage.getModifyTime())
                                            .append(",赔率状态:")
                                            .append(thirdSportMarketMessage.getStatus())
                                            .append(",赔率已过期, 过滤当前数据源赔率")
                                            .append(";");
                                }
                                if(expiredFlag && !dataSourceIsMaintain) {
                                    stringBuffer.append("数据源:")
                                            .append(thirdSportMarketMessage.getDataSourceCode())
                                            .append("赔率校验通过;");
                                    validThirdSportMarketList.add(thirdSportMarketMessage);
                                }
                            }
                            log.info("赛事id:{}, 玩法id:{}, 盘口id:{}, 三方赔率校验:{}", matchId, marketId, entry.getKey(), stringBuffer.toString());
                            if (CollectionUtil.isNotEmpty(validThirdSportMarketList)) {
                                //如果数据源>=5家，过滤掉最大和最小的
                                List<ThirdSportMarketMessage> filterMarketMessages = filterMaxAndMin(validThirdSportMarketList, stringBuffer, categorySetId, weightMap);
                                if (CollectionUtil.isNotEmpty(filterMarketMessages)) {
                                    List<ThirdSportMarketMessage> a99MarketList = calculateOdds(filterMarketMessages, flag, matchType, stringBuffer, weightMap, cautionMap);
                                    if (CollectionUtil.isNotEmpty(a99MarketList)) {
                                        matchMarketList.addAll(a99MarketList);
                                    }
                                }
                            }
                        }
                        if (CollectionUtil.isNotEmpty(matchMarketList)) {
                            String linkId = "A99_" + IdUtil.simpleUUID();
                            calculationMarketProcessor.n0nDataSourceOddsHandle(linkId, standardMatchInfo, matchMarketList, 1L);

                            //校验是否超过赔率差值，只要有一个盘口超过赔率差值，则所有盘口都需要重推
                            boolean isGreatThanDiffValue = false;
                            for (ThirdSportMarketMessage market : matchMarketList) {
                                isGreatThanDiffValue = checkIsGrateThanDiffValue(market, categorySetId, getOddsValueFromMarketMessage(market, "Under"),
                                        getOddsValueFromMarketMessage(market, "Over"), flag, matchType, oddsChangeDiffMap);
                                if (isGreatThanDiffValue) {
                                    break;
                                }
                            }
                            if (isGreatThanDiffValue) {
                                matchMarketList.forEach(market -> {
                                    //投注项排序，按orderOdds升序排序
                                    market.getThirdSportMarketOddsList().sort(Comparator.comparing(ThirdSportMarketOdds::getOrderOdds));
                                });
                                //发送A99赔率给风控
                                thirdSportMarketMergeProducer.sendThirdSportMarketMessageToMQ(linkId, standardMatchInfo, matchMarketList, matchMarketList.get(0).getModifyTime());
                                //保存已下发玩法的玩法集id
                                List<String> types = getRequestTypeListByMarketList(matchMarketList);
                                if (CollectionUtil.isNotEmpty(types)) {
                                    requestTypeList.addAll(types);
                                }
                            }
                        }
                    }
                }

                if (CollectionUtil.isNotEmpty(requestTypeList)) {
                    List<String> distinctList = requestTypeList.stream()
                            .distinct()
                            .collect(Collectors.toList());
                    String linkId = "A99_" + IdUtil.simpleUUID();
                    thirdSportMarketMergeProducer.sendA99OddsToA01(linkId, matchId, distinctList);
                }
            }
//            stopWatch.stop();
        };

        if (!groupByMarket.isEmpty()) {
            groupByMarket.clear();
        }
        if (!allMarketCache.isEmpty()) {
            allMarketCache.clear();
        }
        if (!requestTypeList.isEmpty()) {
            requestTypeList.clear();
        }
        if (!standardMatchInfos.isEmpty()) {
            standardMatchInfos.clear();
        }
        if (!standardMatchInfoMap.isEmpty()) {
            standardMatchInfoMap.clear();
        }
        if (!standardSportMarketSells.isEmpty()) {
            standardSportMarketSells.clear();
        }
        if (!standardSellMap.isEmpty()) {
            standardSellMap.clear();
        }
        if (!maintainDataSourceMap.isEmpty()) {
            maintainDataSourceMap.clear();
        }
//        }finally {
//            if (stopWatch.isRunning()) {
//                stopWatch.stop();
//            }
//            log.info("event:::process cost: {}ms. pretty: {}", stopWatch.getTotalTimeMillis(), stopWatch);
//      }
    }

    /**
     * 获取玩法下的投注项的大小/主客盘的paOddsValue,用于比较是否超过赔率差值
     * @param thirdSportMarketMessage
     * @param oddsType
     * @return
     */
    private BigDecimal getOddsValueFromMarketMessage(ThirdSportMarketMessage thirdSportMarketMessage, String oddsType){
        Integer paOddsValue = thirdSportMarketMessage.getThirdSportMarketOddsList().stream()
                .filter(odds -> oddsType.equals("Under") ?
                        odds.getOddsType().equals("Under") || odds.getOddsType().equals("1") :
                        odds.getOddsType().equals("Over") || odds.getOddsType().equals("2"))
                .map(ThirdSportMarketOdds::getPaOddsValue)
                .findFirst().get();
        return new BigDecimal(paOddsValue);
    }

    public List<String> getRequestTypeListByMarketList(List<ThirdSportMarketMessage> marketList){
        // 取出所有的标准玩法
        Set<Integer> categoryList = marketList.stream()
                .map(x -> x.getMarketCategoryId().intValue())
                .collect(Collectors.toSet());
        // 取出标准玩法对应的玩法集id
        List<String> categorySetList = a99ParamConfig.getCategoryMap().entrySet().stream()
                .filter(entry -> {
                    List<Integer> mapValue = entry.getValue();
                    return mapValue.stream().anyMatch(categoryList::contains);
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        // 根据玩法集id获取玩法集类型
        List<String> requestTypeList = categorySetList.stream()
                .map(RequestTypeEnum::getRequestTypeEnumByPlaySetId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return requestTypeList;
    }

    /**
     * 校验缓存的三方赔率是否有效
     * @param thirdSportMarketMessage
     * @param seconds   失效时间 早盘:10小时， 滚球:1分钟
     * @return 有效:true，无效:false
     */
    private boolean validThirdMarket(ThirdSportMarketMessage thirdSportMarketMessage, int seconds){
        if (thirdSportMarketMessage.getStatus() != 0) {
            return false;
        }
        //将毫秒级时间戳转为Instant对象
        Instant modifyTime = Instant.ofEpochMilli(thirdSportMarketMessage.getModifyTime());
        Instant systemTime = Instant.ofEpochMilli(System.currentTimeMillis());
        //计算两个Instant之间的时间差
        Duration duration = Duration.between(modifyTime, systemTime);
        //获取时间差的绝对值，比较是否超过1分钟
        long diffInMills = Math.abs(duration.toMillis());
        return !(diffInMills > seconds * 1000L);
    }

    /**
     * 如果>=5家时，去除最大最小的赔率
     * @param thirdSportMarketMessageList
     * @return
     */
    public List<ThirdSportMarketMessage> filterMaxAndMin(List<ThirdSportMarketMessage> thirdSportMarketMessageList, StringBuffer stringBuffer, String playSetId, Map<Object, Object> weightMap){
        //取出所有数据源的权重
        ThirdSportMarketMessage thirdSportMarketMessage = thirdSportMarketMessageList.get(0);
//        String weightRedisKey = Constant.REDIS_KEY.RONGHE_A99_DATA_SOURCE_WEIGHT + ":" + thirdSportMarketMessage.getReferenceId() + ":" + thirdSportMarketMessage.getMarketType();
//        Map<Object, Object> weightMap = redisService.hGetAll(weightRedisKey);

        List<ThirdSportMarketMessage> resultList = new ArrayList<>(thirdSportMarketMessageList.size());
        //根据盘口id分组
        Map<Long, List<ThirdSportMarketMessage>> relationGroupMap = thirdSportMarketMessageList.stream()
                .collect(Collectors.groupingBy(ThirdSportMarketMessage::getRelationMarketId));

        for (Map.Entry<Long, List<ThirdSportMarketMessage>> entry : relationGroupMap.entrySet()) {
            if (entry.getValue().size() < 5) {
                resultList.addAll(entry.getValue());
            } else {
                log.info("玩法id:{},盘口id:{},数据源大于5家，过滤最大最小的赔率前数量:{},赔率{}", thirdSportMarketMessageList.get(0).getMarketCategoryId(), entry.getKey(), entry.getValue().size(), entry.getValue());
                List<Integer> oddsValues = entry.getValue().stream()
                    .flatMap(market -> market.getThirdSportMarketOddsList().stream())
                    //如果是大小类型的玩法，取投注项为小的赔率作为参考值；如果是让球类型的玩法，取投注项为主队作为参考值
                    .filter(odds -> "Under".equalsIgnoreCase(odds.getOddsType()) || "1".equals(odds.getOddsType()))
                    .map(ThirdSportMarketOdds::getOddsValue)
                    .collect(Collectors.toList());
                Optional<Integer> minOptional = oddsValues.stream().min(Long::compare);
                Optional<Integer> maxOptional = oddsValues.stream().max(Long::compare);

                if (!minOptional.isPresent() || !maxOptional.isPresent()) {
                    return new ArrayList<>(thirdSportMarketMessageList);
                }
                Integer min = minOptional.get();
                Integer max = maxOptional.get();

                //找出最大赔率且权重最小的数据源
//                entry.getValue().stream()
//                        .filter(x -> x.getOddsValue() == max)
//                        .collect(Collectors.toList());

                Set<ThirdSportMarketMessage> toRemove = new HashSet<>();
                stringBuffer.append("赔率源超过5家,去除最大赔率,最大赔率为:")
                        .append(max)
                        .append(";");
                List<ThirdSportMarketMessage> maxMarketList = entry.getValue().stream()
                        .filter(market -> market.getThirdSportMarketOddsList().stream()
                                //打印因赔率源超过5家，过滤掉的最大最小的赔率
                                .peek(odds -> {
                                    if (("Under".equalsIgnoreCase(odds.getOddsType()) || "1".equals(odds.getOddsType())) &&
                                            odds.getOddsValue().equals(max)) {
                                        stringBuffer.append("数据源:")
                                                .append(market.getDataSourceCode())
                                                .append("赔率为:")
                                                .append(odds.getOddsValue())
                                                .append(";");
                                    }
                                })
                                .anyMatch(odds -> ("Under".equalsIgnoreCase(odds.getOddsType()) || "1".equals(odds.getOddsType())) && odds.getOddsValue().equals(max)))
                        .collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(maxMarketList)) {
                    ThirdSportMarketMessage maxToRemove = null;
                    if (maxMarketList.size() > 1) {
                        maxToRemove = maxMarketList.stream()
                                .peek(x -> {
                                    stringBuffer.append("数据源【")
                                            .append(x.getDataSourceCode())
                                            .append("】权重为:")
                                            .append(weightMap.getOrDefault(playSetId + ":" + x.getDataSourceCode(), 0))
                                            .append(";");
                                })
                                .min(Comparator.comparingInt(market -> (int) weightMap.getOrDefault(playSetId + ":" + market.getDataSourceCode(), 0)))
                                .orElse(null);
                    } else {
                        maxToRemove = maxMarketList.get(0);
                        stringBuffer.append("当前只有数据源【")
                                .append(maxToRemove.getDataSourceCode())
                                .append("】为最大赔率;");
                    }
                    if (maxToRemove != null) {
                        toRemove.add(maxToRemove);
                        stringBuffer.append("过滤掉最大赔率, 数据源:")
                                .append(maxToRemove.getDataSourceCode())
                                .append(";");
                    }
                }

                stringBuffer.append("赔率源超过5家,去除最小赔率,最小赔率为:")
                        .append(min)
                        .append(";");
                List<ThirdSportMarketMessage> minMarketList = entry.getValue().stream()
                        .filter(market -> market.getThirdSportMarketOddsList().stream()
                                //打印因赔率源超过5家，过滤掉的最大最小的赔率
                                .peek(odds -> {
                                    if (("Under".equalsIgnoreCase(odds.getOddsType()) || "1".equals(odds.getOddsType())) &&
                                            odds.getOddsValue().equals(min)) {
                                        stringBuffer.append("数据源:")
                                                .append(market.getDataSourceCode())
                                                .append("赔率为:")
                                                .append(odds.getOddsValue())
                                                .append(";");
                                    }
                                })
                                .anyMatch(odds -> ("Under".equalsIgnoreCase(odds.getOddsType()) || "1".equals(odds.getOddsType())) && odds.getOddsValue().equals(min)))
                        .collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(minMarketList)) {
                    ThirdSportMarketMessage minToRemove = null;
                    if (minMarketList.size() > 1) {
                        minToRemove = minMarketList.stream()
                                .peek(x -> {
                                    stringBuffer.append("数据源【")
                                            .append(x.getDataSourceCode())
                                            .append("】权重为:")
                                            .append(weightMap.getOrDefault(playSetId + ":" + x.getDataSourceCode(), 0))
                                            .append(";");
                                })
                                .min(Comparator.comparingInt(market -> (int) weightMap.getOrDefault(playSetId + ":" + market.getDataSourceCode(), 0)))
                                .orElse(null);
                    } else {
                        minToRemove = minMarketList.get(0);
                        stringBuffer.append("当前只有数据源【")
                                .append(minToRemove.getDataSourceCode())
                                .append("】为最小赔率;");
                    }
                    if (minToRemove != null) {
                        toRemove.add(minToRemove);
                        stringBuffer.append("过滤掉最小赔率, 数据源:")
                                .append(minToRemove.getDataSourceCode())
                                .append(";");
                    }
                }
                return entry.getValue().stream().filter(x -> !toRemove.contains(x)).collect(Collectors.toList());

//                List<ThirdSportMarketMessage> filterList = entry.getValue().stream()
//                        .filter(market -> {
//                            Optional<Integer> currentValue = market.getThirdSportMarketOddsList().stream()
//                                    //打印因赔率源超过5家，过滤掉的最大最小的赔率
//                                    .peek(odds -> {
//                                        if (("Under".equalsIgnoreCase(odds.getOddsType()) || "1".equals(odds.getOddsType())) &&
//                                                (odds.getOddsValue() == min || odds.getOddsValue() == max)) {
//                                            stringBuffer.append("赔率源超过5家,去除最大最小赔率,最大赔率为:")
//                                                    .append(max)
//                                                    .append(",最小赔率为:")
//                                                    .append(min)
//                                                    .append(",当前数据源:")
//                                                    .append(market.getDataSourceCode())
//                                                    .append("赔率为:")
//                                                    .append(odds.getOddsValue())
//                                                    .append(",已被过滤;");
//                                            log.info("赔率源超过5家,去除最大最小赔率,最大赔率为:{},最小赔率为:{}, 去除数据源:{},当前赔率:{}", max, min, market.getDataSourceCode(), odds.getOddsValue());
//                                        }
//                                    })
//                                    .filter(odds -> "Under".equalsIgnoreCase(odds.getOddsType()) || "1".equals(odds.getOddsType()))
//                                    .map(ThirdSportMarketOdds::getOddsValue)
//                                    .findFirst();
//                            return currentValue.map(value -> value != min && value != max).orElse(true);
//                        }).collect(Collectors.toList());
//                if (CollectionUtil.isEmpty(filterList)) {
//                    //兜底处理，防止所有数据源赔率全部一致时都被过滤掉
//                    log.info("玩法id:{},盘口id:{},数据源大于5家，过滤最大最小的赔率后剩余数量为0,返回未过滤前数据", thirdSportMarketMessageList.get(0).getMarketCategoryId(), entry.getKey());
//                    filterList = entry.getValue();
//                    stringBuffer.append("因过滤最大最小赔率后剩余数量为0,取消过滤最大最小赔率");
//                }
//                resultList.addAll(filterList);
            }
        }
        return resultList;
    }

    /**
     * 计算A99赔率
     * @param thirdSportMarketMessageList
     */
    private List<ThirdSportMarketMessage> calculateOdds(List<ThirdSportMarketMessage> thirdSportMarketMessageList, int flag, int matchType, StringBuffer buffer, Map<Object, Object> weightMap, Map<String, Object> cautionMap) {
        List<ThirdSportMarketMessage> resultList = new ArrayList<>(thirdSportMarketMessageList.size());
        ThirdSportMarketMessage thirdSportMarketMessage = thirdSportMarketMessageList.get(0);
        String playSetId = getPlaySetId(thirdSportMarketMessage.getMarketCategoryId().toString());
        if (playSetId == null) {
            log.info("玩法id:{}未查询到对应的玩法集，取消计算A99赔率", thirdSportMarketMessage.getMarketCategoryId());
            return resultList;
        }
        //获取玩法集警戒值
//        Object cautionObj = redisService.hGet(RONGHE_A99_DATA_SOURCE_CAUTION_VALUE + ":" + thirdSportMarketMessage.getReferenceId() + ":" + matchType, playSetId);
        Object cautionObj = cautionMap.get(playSetId);
        int cautionVal = Objects.isNull(cautionObj) ? 1 : (int)cautionObj;

        //根据盘口id分组
        Map<Long, List<ThirdSportMarketMessage>> relationGroupMap = thirdSportMarketMessageList.stream()
                .collect(Collectors.groupingBy(ThirdSportMarketMessage::getRelationMarketId));
        for (Map.Entry<Long, List<ThirdSportMarketMessage>> entry : relationGroupMap.entrySet()) {
            int numberOfProviders = 0;
            List<ThirdSportMarketMessage> groupList = entry.getValue();
            ThirdSportMarketMessage a99SportMarketMessage = groupList.get(0);
            //赔率(各个数据源的原始赔率*权重比，相加后的总和)
            BigDecimal oddsSum = new BigDecimal(0);
            //权重(各个数据源的权重比总和)
            BigDecimal weightSum = new BigDecimal(0);
            for (ThirdSportMarketMessage message : groupList) {
                for (ThirdSportMarketOdds odds : message.getThirdSportMarketOddsList()) {
                    //从投注项类型为小盘/主队的投注项开始计算
                    if("Under".equalsIgnoreCase(odds.getOddsType()) || "1".equals(odds.getOddsType())) {
                        //取出对应数据源的权重
                        Object obj = weightMap.get(playSetId + ":" + message.getDataSourceCode());
                        if(obj != null) {
                            numberOfProviders++;
                            int weight = (int) obj;
                            BigDecimal weightBigdecimal = NumberUtil.div(new BigDecimal(weight), new BigDecimal(100));
                            weightSum = NumberUtil.add(weightSum, weightBigdecimal);
                            BigDecimal oddsValueBigDecimal = NumberUtil.div(new BigDecimal(odds.getOriginalOddsValue()), new BigDecimal(100000));
                            oddsSum = NumberUtil.add(oddsSum, NumberUtil.mul(oddsValueBigDecimal, weightBigdecimal));
                            buffer.append("数据源:")
                                    .append(message.getDataSourceCode())
                                    .append(", 权重:")
                                    .append(weight)
                                    .append("%, 主队/小盘赔率基值:")
                                    .append(odds.getOriginalOddsValue())
                                    .append(";");
                        }
                    }
                }
            }
            buffer.append("求和后赔率系数为:")
                    .append(oddsSum)
                    .append("权重总和为:")
                    .append(weightSum)
                    .append(";");
            if (oddsSum.compareTo(BigDecimal.ZERO) == 0 || weightSum.compareTo(BigDecimal.ZERO) == 0) {
                //根据权重计算出来的赔率或权重占比为0，说明当前下发赔率的数据源都没有设置数据源权重，无需计算赔率
                log.info("赛事id:{}, 玩法id:{}, 盘口id:{}, 权重计算异常:{}", thirdSportMarketMessage.getReferenceId(),
                        thirdSportMarketMessage.getMarketCategoryId(), thirdSportMarketMessage.getRelationMarketId(), weightMap);
                continue;
            }
            //计算出小盘/主队的原始赔率，保留2位小数
            BigDecimal underOddsValue = NumberUtil.div(oddsSum, weightSum, 5);
            //取出小盘的倒数
            BigDecimal underBd = NumberUtil.div(BigDecimal.ONE, underOddsValue);
            //反推大盘的倒数
            BigDecimal overBd = NumberUtil.sub(1, underBd);
            if (underBd.compareTo(BigDecimal.ONE) > 0 || overBd.compareTo(BigDecimal.ONE) > 0) {
                //取出来的倒数大于1， 不再计算
                log.info("赛事id:{}, 玩法id:{}, 盘口id:{}, 倒数计算异常", thirdSportMarketMessage.getReferenceId(),
                        thirdSportMarketMessage.getMarketCategoryId(), thirdSportMarketMessage.getRelationMarketId());
                continue;
            }
            //最后用1除以大盘的倒数得出大盘的原始赔率
            BigDecimal overOddsValue = NumberUtil.div(BigDecimal.ONE, overBd, 5);
            buffer.append("计算出的主队/小盘的赔率为:")
                    .append(underOddsValue)
                    .append(", 主队/小盘的倒数为:")
                    .append(underBd)
                    .append(", 反推客队/大盘的倒数为:")
                    .append(overBd)
                    .append(", 最后计算出客队/大盘的赔率为:")
                    .append(overOddsValue)
                    .append(";");
//            //判断是否超过赔率差值
//            boolean boo = checkIsGrateThanDiffValue(thirdSportMarketMessage, playSetId, underOddsValue, overOddsValue, flag, matchType);
//            if (!boo) {
//                log.info("赛事id:{}, 玩法id:{}, 盘口id:{}, A99赔率未超过差值, 无需下发", thirdSportMarketMessage.getReferenceId(),
//                        thirdSportMarketMessage.getMarketCategoryId(), thirdSportMarketMessage.getRelationMarketId());
//                continue;
//            }
            //重新赋值
            log.info("赛事id:{}, 玩法id:{}, 盘口值:{}, 盘口id:{}, A99赔率计算完成, 主队/小赔率为:{}, 客队/大赔率为:{}, A99计算参数:{}", thirdSportMarketMessage.getReferenceId(),
                    thirdSportMarketMessage.getMarketCategoryId(), thirdSportMarketMessage.getAddition1(), thirdSportMarketMessage.getRelationMarketId(), underOddsValue, overOddsValue, buffer.toString());
            replaceToA99(a99SportMarketMessage, underOddsValue, overOddsValue);
            a99SportMarketMessage.setNumberOfProviders(numberOfProviders);
            a99SportMarketMessage.setCautionVal(cautionVal);
            a99SportMarketMessage.setA99Remark(buffer.toString());
            resultList.add(a99SportMarketMessage);
        }
        return resultList;
    }

    /**
     *  判断当前赔率是否超过赔率差值
     * @param thirdSportMarketMessage
     * @param playSetId 玩法集id
     * @param underOddsValue 小盘投注项的值
     * @param overOddsValue 大盘投注项的值
     * @param flag 标识(1:15秒计算一次滚球; 2:30秒计算一次早盘; 3:每3秒计算一次)
     * @param matchType 0:滚球 1:早盘
     * @return 超过则返回true，未超过返回false
     */
    private boolean checkIsGrateThanDiffValue(ThirdSportMarketMessage thirdSportMarketMessage, String playSetId, BigDecimal underOddsValue, BigDecimal overOddsValue, int flag, int matchType, Map<String, Object> oddsChangeDiffMap){
        /**
         * 当flag为1(15秒计算一次滚球赔率)或flag为2(30秒计算一次早盘赔率)时，强制下发一次A99赔率，直接缓存当前赔率
         * 当flag为3(3秒钟计算一次赔率，赔率超过赔率变化差值才下发赔率)时，从缓存里面取出缓存的赔率，比较是否超过了赔率变化差值，如果超过，则下发赔率并缓存当前赔率
         */
        boolean cacheOddsFlag = false;
        String underKey = RONGHE_A99_ODDS_UNDER_ODDS_VALUE + thirdSportMarketMessage.getReferenceId() + ":" + thirdSportMarketMessage.getRelationMarketId();
        String overKey = RONGHE_A99_ODDS_OVER_ODDS_VALUE + thirdSportMarketMessage.getReferenceId() + ":" + thirdSportMarketMessage.getRelationMarketId();
        if (flag ==1 || flag == 2) {
            cacheOddsFlag = true;
        } else {
            Object underObj = redisService.get(underKey);
            Object overObj = redisService.get(overKey);
            if (null == underObj || null == overObj) {
                //如果缓存里面没有赔率信息，说明这个盘口还没有下发过，缓存当前赔率并直接返回true(超过赔率差值，本次需要下发赔率)
                redisService.set(underKey, underOddsValue, 10*60*60);
                redisService.set(overKey, overOddsValue, 10*60*60);
                return true;
            }

            BigDecimal cacheOverOdds = (BigDecimal)overObj;
            BigDecimal cacheUnderOdds = (BigDecimal)underObj;

            //获取赔率差值
//            String redisKeyPrefix = matchType == 0 ? RONGHE_A99_ODDS_CHANGE_DIFFERENCE_LIVE : RONGHE_A99_ODDS_CHANGE_DIFFERENCE_PRE;
//            redisKeyPrefix += thirdSportMarketMessage.getReferenceId();
//            Object cacheDiff = redisService.hGet(redisKeyPrefix, playSetId);
            Object cacheDiff = oddsChangeDiffMap.get(playSetId);

            //如果没有设置赔率差值，默认给0.04
            Double diffValue = 0.04;
            if (cacheDiff != null) {
                diffValue = (Double) cacheDiff;
            }

            //判断当前下发的赔率和缓存中的赔率的差值，绝对值是否超过赔率差值
            if (isDifferenceThanDiffValue(underOddsValue, cacheUnderOdds, diffValue) || isDifferenceThanDiffValue(overOddsValue, cacheOverOdds, diffValue)) {
                log.info("当前赔率已超过赔率差值，需推送赔率,赛事id:{},玩法id:{},盘口id:{},小盘投注项值:{}-{}, 大盘投注项值:{}-{}, 赔率差值:{}", thirdSportMarketMessage.getReferenceId(),
                        thirdSportMarketMessage.getRelationMarketId(),thirdSportMarketMessage.getMarketCategoryId(),underOddsValue, cacheUnderOdds, overOddsValue, cacheOverOdds, diffValue);
                cacheOddsFlag = true;
            } else {
                log.info("当前赔率未超过赔率差值，无需推送赔率,赛事id:{},玩法id:{},盘口id:{},小盘投注项值:{}-{}, 大盘投注项值:{}-{}, 赔率差值:{}", thirdSportMarketMessage.getReferenceId(),
                        thirdSportMarketMessage.getRelationMarketId(),thirdSportMarketMessage.getMarketCategoryId(),underOddsValue, cacheUnderOdds, overOddsValue, cacheOverOdds, diffValue);
            }
        }
        if (cacheOddsFlag) {
            redisService.set(underKey, underOddsValue, 10*60*60);
            redisService.set(overKey, overOddsValue, 10*60*60);
        }
        return cacheOddsFlag;
    }

    private void replaceToA99(ThirdSportMarketMessage thirdSportMarketMessage, BigDecimal underOddsValue, BigDecimal overOddsValue){
        thirdSportMarketMessage.setDataSourceCode("A99");
        thirdSportMarketMessage.setModifyTime(System.currentTimeMillis());
        thirdSportMarketMessage.getThirdSportMarketOddsList().forEach(x -> {
            x.setDataSourceCode("A99");
            x.setModifyTime(System.currentTimeMillis());
            if (underOddsValue != null && overOddsValue != null) {
                if ("Under".equalsIgnoreCase(x.getOddsType()) || "1".equals(x.getOddsType())) {
                    x.setOriginalOddsValue(NumberUtil.mul(underOddsValue, new BigDecimal(100000)).intValue());
                } else {
                    x.setOriginalOddsValue(NumberUtil.mul(overOddsValue, new BigDecimal(100000)).intValue());
                }
            }
        });
    }

    /**
     * 获取玩法集id
     * @param marketCategoryId
     * @return
     */
    private String getPlaySetId(String marketCategoryId){
        Map<String, List<Integer>> categoryMap = a99ParamConfig.getCategoryMap();
        for (Map.Entry<String, List<Integer>> entry : categoryMap.entrySet()) {
            for (Integer marketId : entry.getValue()) {
                if(String.valueOf(marketId).equals(marketCategoryId)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * 根据key值分别查询开启了A99赔率的早盘/滚球的赛事id
     * @param redisKey
     * @return
     */
    public Set<Long> getA99MatchIds(String redisKey) {
        Map<String, Object> map = redisService.hGetAll(redisKey);
        Set<String> matchSet = map.keySet();
        Set<Long> set = matchSet.stream()
                .map(Long::valueOf)
                .collect(Collectors.toSet());
        return set;
    }

    /**
     * 根据玩法id查询对应的玩法集id
     * @param categoryMap
     * @param marketId
     */
    public String getCategorySetId(Map<String, List<Integer>> categoryMap, String marketId){
        for (Map.Entry<String, List<Integer>> entry : categoryMap.entrySet()) {
            String key = entry.getKey();
            List<Integer> value = entry.getValue();
            if (value.contains(Integer.valueOf(marketId))) {
                return key;
            }
        }
        return null;
    }

    /**
     * 比较当前下发的赔率和缓存的赔率是否超过赔率差值
     * @param num1 缓存赔率
     * @param num2 当前赔率
     * @param diffValue 赔率差值
     */
    public boolean isDifferenceThanDiffValue(BigDecimal num1, BigDecimal num2, Double diffValue) {
        if (num1 == null || num2 == null) {
            //如果存在空值，说明缓存里面没有，返回false
            return false;
        }
        //取两个数的差值
        BigDecimal difference = num1.subtract(num2);
        //取绝对值，不看正负
        BigDecimal absDiff = difference.abs();
        //绝对值如果大于等于赔率差值，返回true
        return absDiff.compareTo(BigDecimal.valueOf(diffValue)) > -1;
    }

    /**
     * 获取赔率
     * @param thirdSportMarketMessage
     */
    public Map<String, BigDecimal> getOddsValue(ThirdSportMarketMessage thirdSportMarketMessage){
        Map<String, BigDecimal> map = new HashMap<>();
        for (ThirdSportMarketOdds odds : thirdSportMarketMessage.getThirdSportMarketOddsList()) {
            //从投注项类型为小盘/主队的投注项开始计算
            if("Under".equalsIgnoreCase(odds.getOddsType()) || "1".equals(odds.getOddsType())) {
                map.put("underOddsValue", new BigDecimal(odds.getOddsValue()));
            } else {
                map.put("overOddsValue", new BigDecimal(odds.getOddsValue()));
            }
        }
        return map;
    }

    /**
     *
     * @param standardMatchInfoId
     * @return
     */
    public int isOddsLive(Long standardMatchInfoId) {
        Object marketTypeObj = redisService.get(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchInfoId);
        return Objects.isNull(marketTypeObj) ? 1 : 0;
    }

    public boolean checkDataSourceIsMaintain(String dataSourceCode, Map<String, String> maintainDataSourceMap) {
        List<String> maintainDataSources = new ArrayList<>();
        if (CollectionUtil.isEmpty(maintainDataSourceMap)) {
            return false;
        }
        for (Map.Entry<String, String> entity : maintainDataSourceMap.entrySet()) {
            String key = entity.getKey();
            String value = entity.getValue();
            String[] values = value.split("#");
            Integer enableSwitch = Integer.parseInt(values[0]);//是否启用(0:禁用，1:启用)
            if (enableSwitch == 0) {
                continue;
            }
            Long beginTime = Long.parseLong(values[1]);// 维护开始时间
            Long endTime = Long.parseLong(values[2]);// 维护结束时间
            long nowTime = System.currentTimeMillis();
            boolean b = nowTime >= beginTime && nowTime <= endTime;
            if (b) {
                maintainDataSources.add(key);
            }
        }
        log.info("::数据源{}当前正在维护中", maintainDataSources);
        return maintainDataSources.contains(dataSourceCode);
    }

    public static void main(String[] args) {
//        Map<String, List<Integer>> categoryMap = new HashMap<>();
//        categoryMap.put("10001", Arrays.asList(2, 4));
//        categoryMap.put("10002", Arrays.asList(113,114));
//
//        String json = "{\"addition1\":\"-0.25\",\"addition2\":\"-0.25\",\"dataSourceCode\":\"N03\",\"marketCategoryId\":4,\"marketType\":1,\"modifyTime\":1767062567299,\"numberOfWinners\":1,\"oddsName\":\"Handicap\",\"referenceId\":39884568,\"relationMarketId\":1,\"status\":0,\"thirdMarketSourceId\":\"3:10248744:4:-0.25:1::\",\"thirdMarketSourceStatus\":0,\"thirdSportMarketOddsList\":[{\"active\":1,\"addition1\":\"1028832803\",\"dataSourceCode\":\"N03\",\"id\":142521156051783612,\"marketId\":143304303872332103,\"modifyTime\":1767062567299,\"oddsType\":\"1\",\"oddsValue\":191000,\"orderOdds\":1,\"originalOddsValue\":195877,\"thirdOddsFieldSourceId\":\"3:10248744:4:-0.25:1:1\"},{\"active\":1,\"addition1\":\"646756219\",\"dataSourceCode\":\"N03\",\"id\":149857379607909904,\"marketId\":143304303872332103,\"modifyTime\":1767062567299,\"oddsType\":\"2\",\"oddsValue\":199000,\"orderOdds\":2,\"originalOddsValue\":204300,\"thirdOddsFieldSourceId\":\"3:10248744:4:-0.25:1:2\"}]}";
//        String json2 = "{\"addition1\":\"-0.25\",\"addition2\":\"-0.25\",\"dataSourceCode\":\"N02\",\"marketCategoryId\":2,\"marketType\":1,\"modifyTime\":1767062567299,\"numberOfWinners\":1,\"oddsName\":\"Handicap\",\"referenceId\":39884568,\"relationMarketId\":1,\"status\":0,\"thirdMarketSourceId\":\"3:10248744:4:-0.25:1::\",\"thirdMarketSourceStatus\":0,\"thirdSportMarketOddsList\":[{\"active\":1,\"addition1\":\"1028832803\",\"dataSourceCode\":\"N02\",\"id\":142521156051783612,\"marketId\":143304303872332103,\"modifyTime\":1767062567299,\"oddsType\":\"1\",\"oddsValue\":192000,\"orderOdds\":1,\"originalOddsValue\":195877,\"thirdOddsFieldSourceId\":\"3:10248744:4:-0.25:1:1\"},{\"active\":1,\"addition1\":\"646756219\",\"dataSourceCode\":\"N02\",\"id\":149857379607909904,\"marketId\":143304303872332103,\"modifyTime\":1767062567299,\"oddsType\":\"2\",\"oddsValue\":198000,\"orderOdds\":2,\"originalOddsValue\":204300,\"thirdOddsFieldSourceId\":\"3:10248744:4:-0.25:1:2\"}]}";
//        String json3 = "{\"addition1\":\"-0.25\",\"addition2\":\"-0.25\",\"dataSourceCode\":\"N01\",\"marketCategoryId\":113,\"marketType\":1,\"modifyTime\":1767062567299,\"numberOfWinners\":1,\"oddsName\":\"Handicap\",\"referenceId\":39884568,\"relationMarketId\":1,\"status\":0,\"thirdMarketSourceId\":\"3:10248744:4:-0.25:1::\",\"thirdMarketSourceStatus\":0,\"thirdSportMarketOddsList\":[{\"active\":1,\"addition1\":\"1028832803\",\"dataSourceCode\":\"N01\",\"id\":142521156051783612,\"marketId\":143304303872332103,\"modifyTime\":1767062567299,\"oddsType\":\"1\",\"oddsValue\":198000,\"orderOdds\":1,\"originalOddsValue\":195877,\"thirdOddsFieldSourceId\":\"3:10248744:4:-0.25:1:1\"},{\"active\":1,\"addition1\":\"646756219\",\"dataSourceCode\":\"N01\",\"id\":149857379607909904,\"marketId\":143304303872332103,\"modifyTime\":1767062567299,\"oddsType\":\"2\",\"oddsValue\":199000,\"orderOdds\":2,\"originalOddsValue\":204300,\"thirdOddsFieldSourceId\":\"3:10248744:4:-0.25:1:2\"}]}";
//        String json11 = "{\"addition1\":\"-0.25\",\"addition2\":\"-0.25\",\"dataSourceCode\":\"BG\",\"marketCategoryId\":4,\"marketType\":1,\"modifyTime\":1767062567299,\"numberOfWinners\":1,\"oddsName\":\"Handicap\",\"referenceId\":39884568,\"relationMarketId\":1,\"status\":0,\"thirdMarketSourceId\":\"3:10248744:4:-0.25:1::\",\"thirdMarketSourceStatus\":0,\"thirdSportMarketOddsList\":[{\"active\":1,\"addition1\":\"1028832803\",\"dataSourceCode\":\"BG\",\"id\":142521156051783612,\"marketId\":143304303872332103,\"modifyTime\":1767062567299,\"oddsType\":\"1\",\"oddsValue\":191000,\"orderOdds\":1,\"originalOddsValue\":195877,\"thirdOddsFieldSourceId\":\"3:10248744:4:-0.25:1:1\"},{\"active\":1,\"addition1\":\"646756219\",\"dataSourceCode\":\"BG\",\"id\":149857379607909904,\"marketId\":143304303872332103,\"modifyTime\":1767062567299,\"oddsType\":\"2\",\"oddsValue\":177000,\"orderOdds\":2,\"originalOddsValue\":204300,\"thirdOddsFieldSourceId\":\"3:10248744:4:-0.25:1:2\"}]}";
//        String json12 = "{\"addition1\":\"-0.25\",\"addition2\":\"-0.25\",\"dataSourceCode\":\"SR\",\"marketCategoryId\":121,\"marketType\":1,\"modifyTime\":1767062567299,\"numberOfWinners\":1,\"oddsName\":\"Handicap\",\"referenceId\":39884568,\"relationMarketId\":1,\"status\":0,\"thirdMarketSourceId\":\"3:10248744:4:-0.25:1::\",\"thirdMarketSourceStatus\":0,\"thirdSportMarketOddsList\":[{\"active\":1,\"addition1\":\"1028832803\",\"dataSourceCode\":\"SR\",\"id\":142521156051783612,\"marketId\":143304303872332103,\"modifyTime\":1767062567299,\"oddsType\":\"1\",\"oddsValue\":198000,\"orderOdds\":1,\"originalOddsValue\":195877,\"thirdOddsFieldSourceId\":\"3:10248744:4:-0.25:1:1\"},{\"active\":1,\"addition1\":\"646756219\",\"dataSourceCode\":\"SR\",\"id\":149857379607909904,\"marketId\":143304303872332103,\"modifyTime\":1767062567299,\"oddsType\":\"2\",\"oddsValue\":178000,\"orderOdds\":2,\"originalOddsValue\":204300,\"thirdOddsFieldSourceId\":\"3:10248744:4:-0.25:1:2\"}]}";
//        String json13 = "{\"addition1\":\"-0.25\",\"addition2\":\"-0.25\",\"dataSourceCode\":\"L01-Bet365\",\"marketCategoryId\":4,\"marketType\":1,\"modifyTime\":1767062567299,\"numberOfWinners\":1,\"oddsName\":\"Handicap\",\"referenceId\":39884568,\"relationMarketId\":1,\"status\":0,\"thirdMarketSourceId\":\"3:10248744:4:-0.25:1::\",\"thirdMarketSourceStatus\":0,\"thirdSportMarketOddsList\":[{\"active\":1,\"addition1\":\"1028832803\",\"dataSourceCode\":\"L01\",\"id\":142521156051783612,\"marketId\":143304303872332103,\"modifyTime\":1767062567299,\"oddsType\":\"1\",\"oddsValue\":195000,\"orderOdds\":1,\"originalOddsValue\":195877,\"thirdOddsFieldSourceId\":\"3:10248744:4:-0.25:1:1\"},{\"active\":1,\"addition1\":\"646756219\",\"dataSourceCode\":\"L01\",\"id\":149857379607909904,\"marketId\":143304303872332103,\"modifyTime\":1767062567299,\"oddsType\":\"2\",\"oddsValue\":177000,\"orderOdds\":2,\"originalOddsValue\":204300,\"thirdOddsFieldSourceId\":\"3:10248744:4:-0.25:1:2\"}]}";
//        ThirdSportMarketMessage thirdSportMarketMessage = JSONObject.parseObject(json, ThirdSportMarketMessage.class);
//        ThirdSportMarketMessage thirdSportMarketMessage2 = JSONObject.parseObject(json2, ThirdSportMarketMessage.class);
//        ThirdSportMarketMessage thirdSportMarketMessage3 = JSONObject.parseObject(json3, ThirdSportMarketMessage.class);
//        ThirdSportMarketMessage thirdSportMarketMessage11 = JSONObject.parseObject(json11, ThirdSportMarketMessage.class);
//        ThirdSportMarketMessage thirdSportMarketMessage12 = JSONObject.parseObject(json12, ThirdSportMarketMessage.class);
//        ThirdSportMarketMessage thirdSportMarketMessage13 = JSONObject.parseObject(json13, ThirdSportMarketMessage.class);
//        List<ThirdSportMarketMessage> list = Arrays.asList(thirdSportMarketMessage, thirdSportMarketMessage2, thirdSportMarketMessage3,
//                thirdSportMarketMessage11, thirdSportMarketMessage12, thirdSportMarketMessage13);

//        List<ThirdSportMarketMessage> list = Arrays.asList(thirdSportMarketMessage, thirdSportMarketMessage2, /*thirdSportMarketMessage3,*/
//                thirdSportMarketMessage11, thirdSportMarketMessage12, thirdSportMarketMessage13);
//
//        StringBuffer buffer = new StringBuffer();
//        List<ThirdSportMarketMessage> thirdSportMarketMessages = filterMaxAndMin(list, buffer, "10001");
//        log.info(buffer.toString());
//        Set<Integer> categoryList = list.stream()
//                .map(x -> x.getMarketCategoryId().intValue())
//                .collect(Collectors.toSet());
//        List<String> playSetList = categoryMap.entrySet().stream()
//                .filter(entry -> {
//                    List<Integer> mapValue = entry.getValue();
//                    return mapValue.stream().anyMatch(categoryList::contains);
//                })
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//        List<String> requestTypeList = playSetList.stream()
//                .map(RequestTypeEnum::getRequestTypeEnumByPlaySetId)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//        System.out.println(requestTypeList);

//        categorySet.stream()
//                .map(RequestTypeEnum::getPlaySetId)

//        List<ThirdSportMarketMessage> filterList = calculateOdds(list);
//        System.out.println(filterList);
    }

}
