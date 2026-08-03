package com.panda.merge.component;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.common.OddsWrapper;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.constant.SaleMatchSellStausEnum;
import com.panda.merge.dto.StandardMatchInfoDetail;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.ThirdSportMarketMessage;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketBatchProcessor;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.rocketmq.producer.CategoryCodeProducer;
import com.panda.merge.service.ConfigMatchStatusService;
import com.panda.merge.service.MarketCategorySellService;
import com.panda.merge.service.ThirdMarketCategoryService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.panda.merge.component.ThirdMarketSaveProcessor.checkA01ExtendedTimeStatus;

/**
 * 开出去的玩法不是三方数据源直接入库
 */
@Component
@Slf4j
public class ThirdMarketBatchSaveProcessor {

    @Resource
    private ThirdMarketCategoryService thirdMarketCategoryService;
    @Resource
    private MarketCategorySellService marketCategorySellService;
    @Resource
    private RedisService redisService;

    @Autowired
    private ConfigMatchStatusService configMatchStatusService;

    @Lazy
    @Resource
    private ThirdMatchMarketBatchProcessor thirdMatchMarketBatchProcessor;
    @Resource
    private ThirdMarketBallHeadProcessor thirdMarketBallHeadProcessor;

    @Lazy
    @Resource
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    @NacosValue(value = "${category.waitCloseTime.Switch:true}", autoRefreshed = true)
    private boolean waitCloseTimeSwitch;

    @Autowired
    public CommonAsyncService commonAsyncService;

    @Autowired
    public CategoryCodeProducer categoryCodeProducer;
    @Getter
    @NacosValue(value = "${market.validate.enabled.football:true}", autoRefreshed = true)
    private boolean footballValidateEnabled;

    private static final long WATERMARK_STALE_MS = 180_000L;

    /**
     * 1.兼容冠军盘口
     * 2.三方玩法转换标准玩法
     * 3.校验开出的标准玩法，是不是数据商盘口
     * 是：返回到新集合
     * 否：返回到新集合并设置标识
     *
     * @param validRequest
     * @param thirdMatchInfoBasedIdMap
     * @param standardMatchInfoBasedIdMap
     * @param thirdSportMarketMessagesMap
     * @param thirdSportMarketOddsUpdateMap
     * @param standardSportMarketSellMap
     * @param marketCategorySellMap
     * @return
     */
    public List<OddsWrapper<ThirdMarketDTO>> marketBatchSaveProcessor(List<OddsWrapper<ThirdMatchMarketDTO>> validRequest, Map<Long, ThirdMatchInfo> thirdMatchInfoBasedIdMap,
                                                                      Map<Long, StandardMatchInfoDetail> standardMatchInfoBasedIdMap, Map<String, List<ThirdSportMarketMessage>> thirdSportMarketMessagesMap,
                                                                      Map<String, List<ThirdSportMarketOdds>> thirdSportMarketOddsUpdateMap,
                                                                      Map<Long, StandardSportMarketSell> standardSportMarketSellMap,
                                                                      Map<String, MarketCategorySell> marketCategorySellMap) {
        if(CollectionUtils.isEmpty(validRequest)) {
            return Collections.emptyList();
        }
        Long uuid = validRequest.get(0).getUuid();
        //最终返回盘口
        List<OddsWrapper<ThirdMarketDTO>> newList = Collections.synchronizedList(new ArrayList());
        try {
            Map<String, List<OddsWrapper<ThirdMarketDTO>>> thirdMarketMapDTO = validRequest.stream().flatMap(t-> t.getData().getMarketList()
                    .stream()
                    .map(market -> {
                        OddsWrapper<ThirdMarketDTO> oddsWrapper = new OddsWrapper();
                        oddsWrapper.setLinkId(t.getLinkId());
                        oddsWrapper.setDataSourceCode(t.getDataSourceCode());
                        oddsWrapper.setDataSourceTime(t.getDataSourceTime());
                        oddsWrapper.setMatchBeginTime(t.getMatchBeginTime());
                        oddsWrapper.setIsOutRight(t.getIsOutRight());
                        oddsWrapper.setThirdMatchId(t.getThirdMatchId());
                        oddsWrapper.setStandardSourceId(t.getStandardSourceId());
                        oddsWrapper.setThirdMatchSourceId(t.getThirdMatchSourceId());
                        oddsWrapper.setMarketType(t.getMarketType());
                        oddsWrapper.setSportId(t.getSportId());
                        oddsWrapper.setData(market);
                        oddsWrapper.setUuid(t.getUuid());
                        return oddsWrapper;
                    })).collect(Collectors.groupingBy(t->t.getDataSourceCode()+"-"+t.getData().getThirdMarketCategorySourceId()));
            for (Map.Entry<String, List<OddsWrapper<ThirdMarketDTO>>> entry : thirdMarketMapDTO.entrySet()) {
                //47319：数据商盘口状态是封 数据商盘口投注项全是未激活 ，改为关
                checkMarketStateAndChange(entry.getValue());
            }

            StopWatch sw = new StopWatch(UUID.randomUUID().toString());
            sw.start("三方盘口获取标准玩法耗时");
            // 获取标准玩法们
            log.info("::{}:: 获取标准玩法们开始", uuid);
            List<ThirdMarketCategory> thirdMarketCategories = thirdMarketCategoryService.getItems(thirdMarketMapDTO.keySet().stream().collect(Collectors.toList()));
            Map<String, ThirdMarketCategory> thirdMarketCategoryMap = thirdMarketCategories.stream().collect(Collectors.toMap(t->
                    t.getDataSourceCode()+"-"+t.getThirdSourceId(), Function.identity(), (v1, v2)->v1));

            // 基于标准玩法，过滤数据
            List<OddsWrapper<ThirdMarketDTO>> filteredValidData = thirdMarketMapDTO.entrySet().stream().filter(entry->{
                String key = entry.getKey();
                if (!thirdMarketCategoryMap.containsKey(key) || thirdMarketCategoryMap.get(key).getReferenceId() == null
                        || thirdMarketCategoryMap.get(key).getReferenceId() == 0L){
                    log.info("::{}::未找到三方玩法或者三方玩法未绑定标准玩法,三方玩法id:{}", uuid, key);
                    return false;
                } else {
                    entry.getValue().forEach(t->{
                        t.setMarketCategoryId(thirdMarketCategoryMap.get(key).getReferenceId());
                        t.setThirdMarketCategoryId(thirdMarketCategoryMap.get(key).getId());
                        ThirdMarketDTO data = t.getData();
                        data.setMarketCategoryId(t.getMarketCategoryId());
                    });
                    return true;
                }
            }).flatMap(t->t.getValue().stream()).collect(Collectors.toList());

            // 找出角球大小玩法的盘口，判断是否要修改盘口状态为数据商关盘
            filteredValidData.stream()
                    .filter(e -> MarginCategoryConfig.TOTAL_CORNERS_CATEGORY.contains(e.getMarketCategoryId()))
                    .forEach(m -> {
                        String[] additionItems = StringUtils.split(m.getData().getAddition1(), ".");
                        if(Objects.nonNull(additionItems) && additionItems.length == 2) {
                            // 例如0.25处理后additionItems[0]为0, additionItems[1]为25
                            boolean isNotSupport = MarginCategoryConfig.TOTAL_CORNERS_CATEGORY_NOT_SUPPORT_ADDITION1.contains(additionItems[1]);
                            if (isNotSupport) {
                                // 出现1/4球头的盘口 盘口状态改为数据商关盘
                                m.getData().setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                                log.info("::{}::ThirdMarketBatchSaveProcessor,1/4球头的盘口，盘口状态改为数据商关盘，当前盘口球头:{}",
                                        m.getLinkId(), m.getData().getAddition1());
                            }
                        } else {
                            log.info("::{}::ThirdMarketBatchSaveProcessor,角球大小玩法1/4球头校验，盘口值:{}解析异常不进行过滤",
                                    m.getLinkId(), m.getData().getAddition1());
                        }
                    });
            sw.stop();
            // 过滤相同盘口，并保留最近时间的盘口
            sw.start("三方盘口过滤相同盘口，并保留最近时间的盘口耗时");
            log.info("::{}:: 过滤相同盘口开始", uuid);
            Map<String, OddsWrapper<ThirdMarketDTO>> tempThirdMarketMapDTOMap = filteredValidData.stream().collect(
                    Collectors.toMap(thirdMarketDTO -> genKeyBasedDatasourceTime(thirdMarketDTO, thirdMatchInfoBasedIdMap), Function.identity(),
                            BinaryOperator.maxBy(Comparator.comparing(t-> t.getData().getModifyTime()))));
            filteredValidData = tempThirdMarketMapDTOMap.values().stream().collect(Collectors.toList());
            sw.stop();
            // 获取所有开售缓存
            sw.start("三方盘口开售缓存耗时");
            Pair<Map<String, Object>, Map<String, MarketCategorySell>> allMarketCategorySell = obtainAllMarketCategorySell(filteredValidData);
            // 根据开售信息进行过滤
            filteredValidData = filteredValidData.stream().filter(inner->{
                if(inner.getStandardSourceId() != null){
                    String dataSourceCode = inner.getDataSourceCode();
                    String linkId = inner.getLinkId();
                    Long marketCategoryId = inner.getMarketCategoryId();
                    // 冠军赛事没有玩法开售，赛事开售即为玩法开售
                    if (inner.getIsOutRight()) {
                        Long standardMatchId = inner.getStandardSourceId();
                        StandardSportMarketSell standardSportMarketSell =
                                standardSportMarketSellMap.get(standardMatchId);
                        if (Objects.nonNull(standardSportMarketSell) && standardSportMarketSell.preMatchSold()) {
                            log.info("::{}::ThirdMarketSaveProcessor,冠军赛事一致不处理走原逻辑加锁,玩法ID:{},开售数据源:{}", linkId,
                                     marketCategoryId, dataSourceCode);
                            newList.add(inner);
                            return false;
                        }
                        return true;
                    }

                    String marketCategoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + inner.getStandardSourceId() + "_" + inner.getMarketType()+"-"+marketCategoryId;
                    String sellKey = inner.getStandardSourceId() + "-" + inner.getMarketType() +"-"+marketCategoryId;
                    MarketCategorySell marketCategorySell = marketCategorySellMap.get(sellKey);
                    if (marketCategorySell == null && allMarketCategorySell.getLeft().containsKey(marketCategoryRedisKey)) {
                        marketCategorySell = new MarketCategorySell();
                        marketCategorySell.setSellStatus(SaleMatchSellStausEnum.Sold.name());
                        marketCategorySell.setDataSourceCode((String) allMarketCategorySell.getLeft().get(marketCategoryRedisKey));
                        marketCategorySell.setMarketCategoryId(marketCategoryId);
                        marketCategorySell.setMarketType(String.valueOf(inner.getMarketType()));
                        marketCategorySell.setMatchId(inner.getStandardSourceId());
                    }
                    if (marketCategorySell == null) {
                        marketCategorySell = allMarketCategorySell.getRight().get(sellKey);
                    }
                    //玩法开售表数据源与数据商赔率数据源对比，一致不处理走原逻辑加锁，不一致直接入库
                    if (null != marketCategorySell &&
                            SaleMatchSellStausEnum.Sold.name().equalsIgnoreCase(marketCategorySell.getSellStatus())) {
                        marketCategorySellMap.putIfAbsent(sellKey, marketCategorySell);
                        if (StringUtils.equals(marketCategorySell.getDataSourceCode(), dataSourceCode)) {
                            log.info("::{}::ThirdMarketSaveProcessor,一致不处理走原逻辑加锁,玩法ID:{},开售数据源:{}",
                                     linkId,
                                     marketCategoryId,
                                     dataSourceCode);
                            newList.add(inner);
                            return false;
                        }
                    }
                }
                return true;
            }).collect(Collectors.toList());
            sw.stop();
            sw.start("三方盘口足球处理的数据耗时");
            // 需要进行足球处理的数据，如TX让球比分处理等
            log.info("::{}:: 对足球数据处理开始", uuid);
            Map<String, Object> resultForSwitchStatus = new ConcurrentHashMap<>();
            Map<String, Object> resultForStandardScores = new ConcurrentHashMap<>();
            Map<String, Object> resultForscoreCenter = new ConcurrentHashMap<>();
            doFootballProcess(filteredValidData,resultForSwitchStatus,resultForStandardScores,resultForscoreCenter, thirdMatchInfoBasedIdMap);
            sw.stop();
            sw.start("三方盘口开始处理盘口信息耗时");
            List<OddsWrapper<ThirdMarketDTO>> storeData = Collections.synchronizedList(new ArrayList());
            // 开始处理盘口信息
            thirdMarketMapDTO = filteredValidData.stream().collect(Collectors.groupingBy(t->t.getData().getThirdMarketCategorySourceId()));
            log.info("::{}:: 开始处理盘口信息size:{}", uuid, thirdMarketMapDTO.size());
            for (Map.Entry<String, List<OddsWrapper<ThirdMarketDTO>>> entry : thirdMarketMapDTO.entrySet()) {
                doProcess(uuid, entry, thirdMatchInfoBasedIdMap, resultForSwitchStatus, resultForStandardScores, resultForscoreCenter, storeData, standardMatchInfoBasedIdMap, Boolean.FALSE, Boolean.TRUE);
            }
            sw.stop();
            sw.start("三方盘口三方赔率入库开始耗时");
            //三方赔率入库
            log.info("::{}:: 三方赔率入库开始", uuid);
            List<OddsWrapper<ThirdSportMarketMessage>> marketMessages = thirdMatchMarketBatchProcessor.processThirdSportMarket(storeData, thirdMatchInfoBasedIdMap, thirdSportMarketOddsUpdateMap);
            sw.stop();
            sw.start("三方盘口后续数据处理耗时");
            //百家赔
            for(OddsWrapper<ThirdSportMarketMessage> item : marketMessages){
                if(thirdSportMarketMessagesMap.containsKey(item.getLinkId())){
                    List<ThirdSportMarketMessage> existItems = thirdSportMarketMessagesMap.get(item.getLinkId());
                    existItems.add(item.getData());
                    thirdSportMarketMessagesMap.put(item.getLinkId(), existItems);
                } else {
                    List<ThirdSportMarketMessage> newItems = Collections.synchronizedList(new ArrayList());
                    newItems.add(item.getData());
                    thirdSportMarketMessagesMap.put(item.getLinkId(), newItems);
                }
            }
            newList.addAll(storeData);
            //数据源球头排序 下发AO
            List<OddsWrapper<ThirdMarketDTO>> AOData = newList.stream().filter(t->t.getMarketType()!=2).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(AOData)){
                thirdMarketBallHeadProcessor.thirdMarketBallHead(AOData, thirdMatchInfoBasedIdMap,standardMatchInfoBasedIdMap);
                thirdMarketBallHeadProcessor.thirdBasketballMarketBallHead(AOData, thirdMatchInfoBasedIdMap,standardMatchInfoBasedIdMap);
                thirdMarketBallHeadProcessor.thirdTableTennisMarketBallHead(AOData, thirdMatchInfoBasedIdMap,standardMatchInfoBasedIdMap);
                List<Long> matchIds = AOData.stream().filter(e->e.getDataSourceCode().equalsIgnoreCase("AO")).map(e->e.getStandardSourceId()).distinct().collect(Collectors.toList());
                Set<Long> aoCategoryIds = AOData.stream().filter(e->e.getDataSourceCode().equalsIgnoreCase("AO")).map(e->e.getMarketCategoryId()).collect(Collectors.toSet());
                for (Long matchId : matchIds){
                    String key = Constant.REDIS_KEY.THIRD_MARKET_HEAD_CLOSE + matchId;
                    Map<String,Object> categoryMap = redisService.hGetAll(key);
                    Set<Long> categoryIds = categoryMap.keySet().stream().map(e->Long.valueOf(e)).collect(Collectors.toSet());
                    if (!categoryIds.isEmpty() && footballValidateEnabled){
                        Set<Long> ids = categoryIds.stream().filter(e->aoCategoryIds.contains(e)).collect(Collectors.toSet());
                        if (!ids.isEmpty()){
                            for (Long categoryId : ids){
                                Set<Long> tempIds = new HashSet<>();
                                tempIds.add(categoryId);
                                Long time = TimeUtils.millsSecondsEast8ZoneGmt();
                                Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap =
                                        thirdMatchMarketProcessor.getStringStandardMarketDataMessageMap(tempIds,uuid+"_A01_"+categoryId, standardMatchInfoBasedIdMap.get(matchId),
                                                standardSportMarketSellMap.get(matchId));
                                if (MapUtils.isNotEmpty(stringStandardMarketDataMessageMap)){
                                    time = stringStandardMarketDataMessageMap.values().stream().collect(Collectors.toList()).get(0).getModifyTime();
                                }
                                thirdMatchMarketProcessor.processOddsByAll(uuid+"_A01_"+categoryId,0,null, standardMatchInfoBasedIdMap.get(matchId), ids, stringStandardMarketDataMessageMap,time, standardSportMarketSellMap.get(matchId), new HashMap<>());
                            }
                        }
                    }
                }
            }
            sw.stop();
            log.info("::{}::三方盘口和投注项注入总耗时{}ms," + sw.prettyPrint(), uuid, sw.getTotalTimeMillis());
        }catch (Exception e){
            e.printStackTrace();
            log.error("::"+uuid+"::marketBatchSaveProcessor出现异常" ,  e);
        }
        return newList;
    }

    /**
     * 47319：数据商盘口状态是封 数据商盘口投注项全是未激活 ，改为关
     * @param marketList
     */
    public void checkMarketStateAndChange(List<OddsWrapper<ThirdMarketDTO>> marketList) {
        Set<Long> sportIds = new HashSet<>(Arrays.asList(1L, 2L));
        List<String> dataSourceCodes = Arrays.asList(DataSourceCodeEnum.OD.getCode(), DataSourceCodeEnum.BE.getCode(), DataSourceCodeEnum.F01.getCode());
        marketList = marketList
                .stream()
                .filter(t -> !dataSourceCodes.contains(t.getDataSourceCode()) && sportIds.contains(t.getSportId()))
                .collect(Collectors.toList());
//        Map<Long, Integer> matchIsLiveMap = getMatchIsLiveMap(marketList);

        for (OddsWrapper<ThirdMarketDTO> thirdMarketDTO : marketList) {
            try {
//                if (waitCloseTimeSwitch) {
//                    if (matchIsLiveMap.get(thirdMarketDTO.getStandardSourceId()) == 0) {
//                        // 滚球不处理
//                        continue;
//                    }
//                }
                //如果数据商盘口为关
                if (thirdMarketDTO.getData().getMarketOddsList()!=null
                        && thirdMarketDTO.getData().getMarketOddsList().stream().anyMatch(odds -> odds.getActive() == 1)) {
                    continue;
                }
                thirdMarketDTO.getData().setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
                log.info("::{}::checkMarketStateAndChange检查数据源盘口是否需要关盘，三方盘口::{}",thirdMarketDTO.getLinkId(),thirdMarketDTO.getData().getThirdMarketSourceId());
            } catch (Exception e) {
                log.error("::{}::checkMarketStateAndChange检查数据源盘口是否需要关盘异常，error::{}",thirdMarketDTO.getLinkId(),e);
            }
        }
    }

    private Map<Long, Integer> getMatchIsLiveMap(List<OddsWrapper<ThirdMarketDTO>> marketList) {
        // 先去重
        Set<Long> matchIdSet = marketList.stream().map(OddsWrapper::getStandardSourceId).collect(Collectors.toSet());
        List<Long> matchIdList = new ArrayList<>(matchIdSet);

        List<String> standardMarketSwitchStatusKeyList = new ArrayList<>();
        matchIdList.forEach(e -> standardMarketSwitchStatusKeyList.add(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + e));
        List<Object> standardMarketSwitchResultList = redisService.mGet(standardMarketSwitchStatusKeyList);
        Map<Long, Integer> matchIsLiveMap = new HashMap<>();
        for (int i = 0; i < matchIdList.size(); i++) {
            Object object = standardMarketSwitchResultList.get(i);
            Integer isLive = Objects.isNull(object) ? 1 : 0;
            matchIsLiveMap.put(matchIdList.get(i), isLive);
        }
        return matchIsLiveMap;
    }

    private String genKeyBasedDatasourceTime(OddsWrapper<ThirdMarketDTO> thirdMarketDTO, Map<Long, ThirdMatchInfo> thirdMatchInfoBasedIdMap){
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoBasedIdMap.get(thirdMarketDTO.getThirdMatchId());
        String dataSourceTimeKey;
        if (DataSourceCodeEnum.TX.code.equals(thirdMarketDTO.getDataSourceCode())) {
            dataSourceTimeKey = Constant.REDIS_KEY.RONGHE_THRID_MARKET_DATASOURCE_TIME + thirdMatchInfo.getDataSourceCode() + "_" + thirdMatchInfo.getSportId() + "_" + thirdMatchInfo.getThirdMatchSourceId() + "_" + thirdMarketDTO.getData().getThirdMarketCategorySourceId() + "_" + thirdMarketDTO.getData().getOfferLineId();
        }else if(DataSourceCodeEnum.LS.code.equals(thirdMarketDTO.getDataSourceCode())){
            dataSourceTimeKey = Constant.REDIS_KEY.RONGHE_THRID_MARKET_DATASOURCE_TIME + thirdMatchInfo.getDataSourceCode() + "_" + thirdMatchInfo.getSportId() + "_" + thirdMarketDTO.getData().getThirdMarketSourceId()+"_"+thirdMarketDTO.getData().getInternalDataSourceCode();
        } else {
            dataSourceTimeKey = Constant.REDIS_KEY.RONGHE_THRID_MARKET_DATASOURCE_TIME + thirdMatchInfo.getDataSourceCode() + "_" + thirdMatchInfo.getSportId() + "_" + thirdMarketDTO.getData().getThirdMarketSourceId();
        }
        return DigestUtil.md5Hex(dataSourceTimeKey);
    }

    private Map<String, Object> mGetForMap(List<String> keys) {
        Map<String, Object> res = new ConcurrentHashMap<>();
        if(CollectionUtils.isEmpty(keys)){
            return res;
        }
        List<Object> values = redisService.mGet(keys);
        for(int i = 0; i < keys.size(); i++) {
            if(null == values.get(i)){
                continue;
            }
            res.put(keys.get(i), values.get(i));
        }
        return res;
    }

    //循环处理三方盘口入库
    public void doProcess(Long uuid, Map.Entry<String, List<OddsWrapper<ThirdMarketDTO>>> entry, Map<Long, ThirdMatchInfo> thirdMatchInfoBasedIdMap,
                          Map<String, Object> resultForSwitchStatus, Map<String, Object> resultForStandardScores,
                          Map<String, Object> resultForscoreCenter, List<OddsWrapper<ThirdMarketDTO>> storeData, Map<Long, StandardMatchInfoDetail> standardMatchInfoBasedIdMap, Boolean lockFlag, Boolean autoOpen) {
        // 开始处理盘口信息
        // 更新最新盘口的时间
        List<String> keysBasedDatasourceTime= entry.getValue().stream().map(thirdMarketDTO -> genKeyBasedDatasourceTime(thirdMarketDTO, thirdMatchInfoBasedIdMap)).collect(Collectors.toList());
        List<Object> oldTimes = redisService.mGet(keysBasedDatasourceTime);
        Map<String, Long> oldTimesMap = new ConcurrentHashMap<>();
        for(int i=0; i< keysBasedDatasourceTime.size(); i++) {
            if(null == oldTimes.get(i)){
                continue;
            }
            oldTimesMap.put(keysBasedDatasourceTime.get(i), (Long) oldTimes.get(i));
        }
        Map<String, Long> autoOpenDataSourceCodeMatchMap = redisService.hGetAll(Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE_MATCH);
        //清理赛事
        if (MapUtils.isNotEmpty(autoOpenDataSourceCodeMatchMap)) {
            Set<String> dels = Collections.synchronizedSet(new HashSet());
            Iterator<String> keyIterator = autoOpenDataSourceCodeMatchMap.keySet().iterator();
            while (keyIterator.hasNext()) {
                String matchId = keyIterator.next();
                if (System.currentTimeMillis() >= autoOpenDataSourceCodeMatchMap.get(matchId)) {
                    dels.add(matchId);
                    keyIterator.remove();
                }
            }
            if (!CollectionUtils.isEmpty(dels)) {
                redisService.hDel(Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE_MATCH, dels.toArray());
            }
        }
        //赛事ID,玩法:数据源 集合
        Map<Long, Set<String>> autoOpenCategoryMap = new ConcurrentHashMap<>();
        for (OddsWrapper<ThirdMarketDTO> thirdMarketDTO : entry.getValue()) {
            ThirdMatchInfo thirdMatchInfo = thirdMatchInfoBasedIdMap.get(thirdMarketDTO.getThirdMatchId());
            // 之前已经被赋予玩法id值了,所以不需要再次进行
            Long marketCategoryId = thirdMarketDTO.getMarketCategoryId();
            if(lockFlag) {
                thirdMarketDTO.getData().setSportId(thirdMatchInfo.getSportId());
            } else {
                //赋值标准玩法ID
                thirdMarketDTO.getData().setMarketCategoryId(marketCategoryId);
                //不走加锁逻辑直接入库
                thirdMarketDTO.getData().setLock(Boolean.FALSE);
            }

            //auto open 校验开盘盘口
            if (StandardSportTypeEnum.FootBall.code.equals(thirdMatchInfo.getSportId())
                    && MarginCategoryConfig.FootBall_MAIN_CATEGORY.contains(marketCategoryId)
                    && thirdMarketDTO.getData().getMarketType() == 0
                    && (!DataSourceCodeEnum.TX.code.equals(thirdMarketDTO.getData().getDataSourceCode())
                    && !DataSourceCodeEnum.LS.code.equals(thirdMarketDTO.getData().getDataSourceCode())
                    && !DataSourceCodeEnum.L02.code.equals(thirdMarketDTO.getData().getDataSourceCode()))
                    && (MapUtil.isNotEmpty(autoOpenDataSourceCodeMatchMap) && autoOpenDataSourceCodeMatchMap.containsKey(thirdMarketDTO.getStandardSourceId()+""))) {
                if (thirdMarketDTO.getData().getStatus().equals(Constant.SPORT_MARKET.STATUS.ACTIVE)) {
                    String finalCode = StringUtils.isEmpty(thirdMarketDTO.getData().getInternalDataSourceCode()) ? thirdMarketDTO.getData().getDataSourceCode() : thirdMarketDTO.getData().getInternalDataSourceCode();
                    if (autoOpenCategoryMap.containsKey(thirdMarketDTO.getStandardSourceId())) {
                        Set<String> existItems = autoOpenCategoryMap.get(thirdMarketDTO.getStandardSourceId());
                        existItems.add(marketCategoryId + ":" + finalCode);
                        autoOpenCategoryMap.put(thirdMarketDTO.getStandardSourceId(), existItems);
                    } else {
                        Set<String> newItems = Collections.synchronizedSet(new HashSet());
                        newItems.add(marketCategoryId + ":" + finalCode);
                        autoOpenCategoryMap.put(thirdMarketDTO.getStandardSourceId(), newItems);
                    }
                }
            }
            log.info("::{}::autoOpenCategoryMap:{},matchId:{}",thirdMarketDTO.getLinkId(),autoOpenCategoryMap,thirdMarketDTO.getStandardSourceId());
            //两项盘数据源赔率合法性验证
            if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(thirdMarketDTO.getData().getStatus())
                    && !CollectionUtils.isEmpty(thirdMarketDTO.getData().getMarketOddsList())
                    && thirdMarketDTO.getData().getMarketOddsList().size() == 2) {
                if (thirdMarketDTO.getData().getMarketOddsList().get(0).getOriginalOddsValue() < 1.01 * 100000
                        || thirdMarketDTO.getData().getMarketOddsList().get(1).getOriginalOddsValue() < 1.01 * 100000) {

                    //如果是A01赔率 判断是否开启延长开售才封盘 开启则不封盘/不开启则正常处理 注:(玩法id 2 4 18 19)
                    Object a01ExtendedTimeObjects  = null;
                    if (thirdMatchInfo.getReferenceId() != null) {
                        a01ExtendedTimeObjects = redisService.get(Constant.REDIS_KEY.A01_EXTENDED_TIME_STATUS_KEY + thirdMatchInfo.getReferenceId());
                    }
                    if(!thirdMarketDTO.getDataSourceCode().equals(DataSourceCodeEnum.AO.code)||!checkA01ExtendedTimeStatus(thirdMarketDTO.getData(),a01ExtendedTimeObjects)){
                        thirdMarketDTO.getData().setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        log.info("::{}::ThirdMarketSaveProcessor,两项盘(三方盘口源id):{},如果存在一个投注项原始赔率小于1.01,合法性封盘", thirdMarketDTO.getLinkId(), thirdMarketDTO.getData().getThirdMarketSourceId());
                    }
                }
            }
            String dataSourceTimeKeyMd5 = genKeyBasedDatasourceTime(thirdMarketDTO, thirdMatchInfoBasedIdMap);
            Long oldTime = oldTimesMap.get(dataSourceTimeKeyMd5);
            if (!redisService.setIfGreater(thirdMarketDTO.getLinkId(),thirdMarketDTO.getData().getThirdMarketSourceId(),dataSourceTimeKeyMd5,thirdMarketDTO.getData().getModifyTime(),RedisConfig.REDIS_MY_TIME)){
                long now = System.currentTimeMillis();
                Long modifyTime = thirdMarketDTO.getData().getModifyTime();
                boolean brokenWatermark = oldTime != null
                        && (now - oldTime > WATERMARK_STALE_MS || oldTime > now + WATERMARK_STALE_MS);
                if (brokenWatermark) {
                    redisService.set(dataSourceTimeKeyMd5, modifyTime, RedisConfig.REDIS_MY_TIME);
                    log.info("::{}::ThirdMarketSaveProcessor,水位线陈旧/污染兜底放行,三方源盘口id:{},旧时间戳:{},新时间戳:{},当前时间:{}",
                            thirdMarketDTO.getLinkId(), thirdMarketDTO.getData().getThirdMarketSourceId(), oldTime, modifyTime, now);
                } else {
                    log.info("::{}::ThirdMarketSaveProcessor,盘口时间戳小于当前盘口时间戳,三方源盘口id:{},旧时间戳:{}", thirdMarketDTO.getLinkId(), thirdMarketDTO.getData().getThirdMarketSourceId(), oldTime);
                    continue;
                }
            }
            /*if (oldTime != null && oldTime > thirdMarketDTO.getData().getModifyTime()) {
                if(lockFlag){
                    //Long beginTime = standardMatchInfoBasedIdMap.get(thirdMarketDTO.getStandardSourceId()).getBeginTime();
                    //configMatchStatusService.processTXTimestamps(thirdMarketDTO.getLinkId(), thirdMarketDTO.getData(), thirdMarketDTO.getStandardSourceId(), thirdMarketDTO.getDataSourceCode(), beginTime);
                }
                log.info("::{}::ThirdMarketSaveProcessor,盘口时间戳小于当前盘口时间戳,三方源盘口id:{},旧时间戳:{}", thirdMarketDTO.getLinkId(), thirdMarketDTO.getData().getThirdMarketSourceId(), oldTime);
                continue;
            }
            log.info("::{}::ThirdMarketSaveProcessor,盘口时间戳校验,三方盘口ID:{},新时间戳:{},旧时间戳:{},当前时间:{}", thirdMarketDTO.getLinkId(), thirdMarketDTO.getData().getThirdMarketSourceId(), thirdMarketDTO.getData().getModifyTime(), oldTime, System.currentTimeMillis());
            redisService.set(dataSourceTimeKeyMd5, thirdMarketDTO.getData().getModifyTime(), RedisConfig.REDIS_MY_TIME);*/
            if (null != thirdMarketDTO.getStandardSourceId()) {
                //滚球期间下发赛前盘口不处理
                if (thirdMarketDTO.getMarketType() == 1 && !Objects.isNull(resultForSwitchStatus.get(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + thirdMarketDTO.getStandardSourceId()))) {
                    //如果这个时候来了BC的早盘关盘，需要去关滚球盘
                    if (DataSourceCodeEnum.BC.code.equalsIgnoreCase(thirdMarketDTO.getDataSourceCode()) && thirdMarketDTO.getData().getStatus().equals(Constant.SPORT_MARKET.STATUS.DEACTIVATED)) {
                        thirdMarketDTO.setMarketType(0);
                    } else {
                        log.info("::{}::ThirdMarketSaveProcessor,标准赛事已经进入即将开赛阶段，不处理任何早盘数据，直接返回", thirdMarketDTO.getLinkId());
                        continue;
                    }
                }
                if (StandardSportTypeEnum.FootBall.code.equals(thirdMatchInfo.getSportId())
                        && !MarginCategoryConfig.IGNORE_SCORE_DATASOURCE_CODE.contains(thirdMarketDTO.getDataSourceCode())) {
                    //TX让球比分处理
                    thirdMatchMarketBatchProcessor.txHandicapDispose(thirdMarketDTO, resultForStandardScores);
                    //数据源角球基准分计算
                    thirdMatchMarketBatchProcessor.cornerScore(thirdMarketDTO, resultForStandardScores);
                    //15分钟进球/角球基准分计算
                    thirdMatchMarketBatchProcessor.fifteenMinutesScore(thirdMarketDTO, resultForscoreCenter);
                }
            }
            storeData.add(thirdMarketDTO);
        }
        if (MapUtils.isNotEmpty(autoOpenCategoryMap)) {
            //赛事级别
            for (Long key : autoOpenCategoryMap.keySet()) {
                Map<String, String> cacheCategoryDataSourceCodeMap = redisService.hGetAll(Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE + key);
                if (MapUtils.isEmpty(cacheCategoryDataSourceCodeMap)) {
                    redisService.hDel(Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE_MATCH, key + "");
                    continue;
                }
                //玩法，数据源
                Map<String, String> categoryDataSourceCodeMap = autoOpenCategoryMap.get(key).stream().map(c -> c.split(":")).collect(Collectors.toMap(keyValue ->keyValue[0], keyValue -> keyValue[1]));
                log.info("::{}::categoryDataSourceCodeMap:{}，cacheCategoryDataSourceCodeMap：{},key:{}",uuid,categoryDataSourceCodeMap,cacheCategoryDataSourceCodeMap,Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE + key);
                //通知风控切换
                Map<String, String> categoryDataSourceCodeNew = new ConcurrentHashMap<>();
                Set<String> dels = Collections.synchronizedSet(new HashSet());
                for (String cacheCategoryId : cacheCategoryDataSourceCodeMap.keySet()) {
                    String dataSourceCodeCache = cacheCategoryDataSourceCodeMap.get(cacheCategoryId);
                    String dataSourceCode = categoryDataSourceCodeMap.get(cacheCategoryId);
                    if (null != dataSourceCode) {
                        if (StringUtils.equals(dataSourceCodeCache, dataSourceCode)) {
                            categoryDataSourceCodeNew.put(cacheCategoryId, dataSourceCodeCache);
                            dels.add(cacheCategoryId);
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(dels)) {
                    redisService.hDel(Constant.REDIS_KEY.AUTO_OPEN_DATA_SOURCE_CODE + key, dels.toArray());
                    //通知风控
                    categoryCodeProducer.sendAutoOpenDataSourceCodeNewToMq(uuid + "", key, categoryDataSourceCodeNew);
                }
            }
        }
    }

    public void doFootballProcess(List<OddsWrapper<ThirdMarketDTO>> thirdMarketMapDTO, Map<String, Object> resultForSwitchStatus,
                                  Map<String, Object> resultForStandardScores, Map<String, Object> resultForscoreCenter, Map<Long,ThirdMatchInfo> thirdMatchInfoBasedIdMap) {
        // 需要进行足球处理的数据，如TX让球比分处理等
        Supplier<Stream<OddsWrapper<ThirdMarketDTO>>> marketDTOStream = () -> thirdMarketMapDTO.stream().filter(t->t.getStandardSourceId() != null);
        List<String> StandardIdsForSwitchStatus = marketDTOStream.get().map(t->Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS+t.getStandardSourceId()).collect(Collectors.toList());

        Stream<OddsWrapper<ThirdMarketDTO>> footballStream = marketDTOStream.get().filter(t->StandardSportTypeEnum.FootBall.code.equals(thirdMatchInfoBasedIdMap.get(t.getThirdMatchId()).getSportId())
                && !MarginCategoryConfig.IGNORE_SCORE_DATASOURCE_CODE.contains(t.getDataSourceCode()));
        List<String> standardScoresForFootball = new ArrayList<>();
        List<String> scoreCenterScoresForFootball = new ArrayList<>();
        footballStream.forEach(t->{
            standardScoresForFootball.add(DigestUtil.md5Hex(Constant.REDIS_KEY.STANDARD_MATCH_SCORES + t.getStandardSourceId()));
            scoreCenterScoresForFootball.add(DigestUtil.md5Hex(Constant.REDIS_KEY.SCORE_CENTER_SCORES + t.getStandardSourceId()));
        });
        resultForSwitchStatus.putAll(mGetForMap(StandardIdsForSwitchStatus));
        resultForStandardScores.putAll(mGetForMap(standardScoresForFootball));
        resultForscoreCenter.putAll(mGetForMap(scoreCenterScoresForFootball));
    }

    public Pair<Map<String, Object>, Map<String, MarketCategorySell>> obtainAllMarketCategorySell(List<OddsWrapper<ThirdMarketDTO>> thirdMarketMapDTO) {
        // 获取所有开售缓存
        Set<String> categoryRedisKeys = thirdMarketMapDTO.stream().map(inner->{
            if(inner.getStandardSourceId() != null){
                return Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + inner.getStandardSourceId() + "_" + inner.getMarketType();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());

        StopWatch sw = new StopWatch(UUID.randomUUID().toString());
        sw.start("marketBatchSaveProcessor获取所有开售缓存");
        Map<String, Object> oldStringHashMap = redisService.syncObtainMultiGetAll(new ArrayList<>(categoryRedisKeys));

        // 获取玩法开售
        Set<String> marketSellKeys = thirdMarketMapDTO.stream().map(inner->{
            if(inner.getStandardSourceId() != null){
                String key = inner.getStandardSourceId() + "-" + inner.getMarketCategoryId() + "-" + inner.getMarketType();
                String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + inner.getStandardSourceId() + "_" + inner.getMarketType() + "-" + inner.getMarketCategoryId();
                if(!oldStringHashMap.containsKey(categoryRedisKey)){
                    return key;
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
        List<MarketCategorySell> marketCategorySells = marketCategorySellService.getItems(marketSellKeys.stream().collect(Collectors.toList()));
        Map<String, MarketCategorySell> marketCategorySellMap = marketCategorySells.stream().collect(Collectors.toMap(
                t->t.getMatchId()+"-"+t.getMarketType()+"-"+t.getMarketCategoryId(), Function.identity(), (v1, v2)->v1));
        return Pair.of(oldStringHashMap, marketCategorySellMap);
    }
}
