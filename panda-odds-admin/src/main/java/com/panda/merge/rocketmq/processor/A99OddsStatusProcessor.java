package com.panda.merge.rocketmq.processor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.A99MatchSwitchDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.odds.A99OddsStatusDTO;
import com.panda.merge.model.MarketCategorySell;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.service.ConfigTradeTypeService;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.StandardSportMarketSellService;
import com.panda.merge.util.CategoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
@Validated
public class A99OddsStatusProcessor extends BaseProcessor {
    @Autowired
    private RedisService redisService;

    @Lazy
    @Resource
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
    public void execute(@Valid Request<A99MatchSwitchDTO> request) {
        log.info("::{}::收到赛事A99玩法状态,request:{}", request.getLinkId(), JSONUtil.toJsonStr(request));
        A99MatchSwitchDTO a99OddsStatusDTO = request.getData();
        Long matchId = a99OddsStatusDTO.getMatchId();
        //查询标准赛事是否存在
        StandardMatchInfo standardMatchInfo =
                standardMatchInfoService.getItem(matchId);
        if (standardMatchInfo == null) {
            log.info("::{}::收到赛事A99玩法状态,标准赛事未找到，标准赛事id:{}", request.getLinkId(),matchId);
            return ;
        }
        //刷新开售缓存并返回最新开售信息
        StandardSportMarketSell standardSportMarketSell =
                standardSportMarketSellService.refreshCache(matchId);
        //赛事未开售
        if (standardSportMarketSell == null) {
            log.info("::{}::收到赛事A99玩法状态 ,赛事未开售，标准赛事id：{}", request.getLinkId(), matchId);
            return ;
        }
        if (a99OddsStatusDTO.getStatus() == 0){
            log.info("::{}::收到赛事A99玩法状态,状态为关闭A99", request.getLinkId());
            closeCategoryIdsA99(request.getLinkId(),a99OddsStatusDTO,standardMatchInfo,standardSportMarketSell,request);
        }
        if (a99OddsStatusDTO.getStatus() == 1){
            log.info("::{}::收到赛事A99玩法状态,状态为开启A99", request.getLinkId());
            openCategoryIdsA99(request.getLinkId(),a99OddsStatusDTO,standardMatchInfo,standardSportMarketSell,request);
        }

    }

    private void openCategoryIdsA99(String linkId,A99MatchSwitchDTO a99OddsStatusDTO,StandardMatchInfo standardMatchInfo,StandardSportMarketSell standardSportMarketSell,Request<A99MatchSwitchDTO> request){
        Set<Long> marketCategoryIdSet = MarginCategoryConfig.A99_category.get(a99OddsStatusDTO.getCategorySetId()).stream().collect(Collectors.toSet());
        Map<Long, Integer> tradeTypeMap = configTradeTypeService.getItemByMatchAndCategorys(standardMatchInfo.getId().toString(), marketCategoryIdSet);
        //筛选出需要下发的玩法
        Set<Long> marketCategoryValid = new HashSet<>();
        for (Long marketCategory : marketCategoryIdSet) {
            if (tradeTypeMap.get(marketCategory) == null || Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeTypeMap.get(marketCategory))) {
                marketCategoryValid.add(marketCategory);
            } else {
                log.info("::{}::M和A+模式判断，标准赛事id={},玩法id={},M和A+模式不下发赔率", linkId,
                        standardMatchInfo.getId(), marketCategory);
            }
        }

        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = getStringStandardMarketDataMessageMap(marketCategoryValid,request.getLinkId(), standardMatchInfo,
                standardSportMarketSell);
        if (!stringStandardMarketDataMessageMap.isEmpty()){
            stringStandardMarketDataMessageMap.forEach((k,v)->{
                if (v.getMarketType() == a99OddsStatusDTO.getMatchType()){
                    v.setThirdMarketSourceStatus(2);
                    v.setStatus(2);
                    String marketKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + v.getStandardMatchInfoId() + "_" + v.getDataSourceCode() + "_" + v.getMarketCategoryId());
                    redisService.hSet(marketKey,  k, v, RedisConfig.REDIS_WEEK_TIME);

                }
            });
        }
        //--------操盘后台操作开关封锁,异步处理-----------
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId() + "_A99_status",request.getOddsSource(), request.getOperaterId(),standardMatchInfo, marketCategoryValid,
                stringStandardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
    }
    private void closeCategoryIdsA99(String linkId,A99MatchSwitchDTO a99OddsStatusDTO,StandardMatchInfo standardMatchInfo,StandardSportMarketSell standardSportMarketSell,Request<A99MatchSwitchDTO> request){
        Set<Long> marketCategoryIdSet = MarginCategoryConfig.A99_category.get(a99OddsStatusDTO.getCategorySetId()).stream().collect(Collectors.toSet());
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

        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = getStringStandardMarketDataMessageMapA99(marketCategoryValid,request.getLinkId(), standardMatchInfo,
                standardSportMarketSell);
        if (!stringStandardMarketDataMessageMap.isEmpty()){
            stringStandardMarketDataMessageMap.forEach((k,v)->{
                if (v.getMarketType() == a99OddsStatusDTO.getMatchType()){
                    v.setThirdMarketSourceStatus(2);
                    v.setStatus(2);
                    String marketKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + v.getStandardMatchInfoId() + "_" + v.getDataSourceCode() + "_" + v.getMarketCategoryId());
                    redisService.hSet(marketKey,  k, v, RedisConfig.REDIS_WEEK_TIME);
                }
            });
        }

        //--------操盘后台操作开关封锁,异步处理-----------
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId() + "_A99_status",request.getOddsSource(), request.getOperaterId(),standardMatchInfo, marketCategoryValid,
                stringStandardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
    }
    public Map<String, StandardMarketDataMessage> getStringStandardMarketDataMessageMap(Set<Long> marketCategoryIds,String linkId, StandardMatchInfo standardMatchInfo, StandardSportMarketSell standardSportMarketSell) {
        int oddsLive = isOddsLive(standardMatchInfo.getId());
        //赛前数据源
        String preProviderCode = standardSportMarketSell.getPreMatchDataProviderCode();
        //滚球数据源
        String liveProviderCode = standardSportMarketSell.getLiveMatchDataProviderCode();
        log.info("::{}:: 标准赛事id：{},赛前服务商={},滚球服务商={} ", linkId, standardMatchInfo.getId(), preProviderCode,
                liveProviderCode);
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = new HashMap<>();
        if (StringUtils.isNotBlank(preProviderCode) && 1 == oddsLive) {
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 1;
            Map<String, String> stringHashMap = redisService.hGetAll(categoryRedisKey);
            if (MapUtil.isNotEmpty(stringHashMap)) {
                for (Map.Entry<String, String> dataSourceCodeEntry : stringHashMap.entrySet()) {
                    Long marketCategoryId = Long.valueOf(dataSourceCodeEntry.getKey());
                    if (!CollectionUtils.isEmpty(marketCategoryIds) && !marketCategoryIds.contains(marketCategoryId)) {
                        continue;
                    }
                    String dataSourceCode = dataSourceCodeEntry.getValue();

                    String tempRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode + "_" + marketCategoryId);
                    Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(tempRedisKey);
                    //只取赛前盘
                    Map<String, StandardMarketDataMessage> preMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(1))
                            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    stringStandardMarketDataMessageMap.putAll(preMap);
                    log.info("::{}:: sendMatchMarketData,赛前玩法开售key={}, 赛前redisKey={},赛前盘缓存总数据：{} ",
                            linkId, categoryRedisKey, tempRedisKey, preMap.size());
                }
            }
        }
        if (StringUtils.isNotBlank(liveProviderCode) && 0 == oddsLive) {
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 0;
            Map<String, String> stringHashMap = redisService.hGetAll(categoryRedisKey);
            if (MapUtil.isNotEmpty(stringHashMap)) {
                for (Map.Entry<String, String> dataSourceCodeEntry : stringHashMap.entrySet()) {
                    Long marketCategoryId = Long.valueOf(dataSourceCodeEntry.getKey());
                    if (!CollectionUtils.isEmpty(marketCategoryIds) && !marketCategoryIds.contains(marketCategoryId)) {
                        continue;
                    }
                    String dataSourceCode = dataSourceCodeEntry.getValue();
                    String tempRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode + "_" + marketCategoryId);
                    Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(tempRedisKey);
                    //只取滚球盘
                    Map<String, StandardMarketDataMessage> liveMap = standardMarketMessageMap.entrySet().stream().filter(e ->
                            e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(0)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    stringStandardMarketDataMessageMap.putAll(liveMap);
                    log.info("::{}:: sendMatchMarketData,滚球玩法开售key={}, 滚球redisKey={},滚球盘缓存总数据：{} ",
                            linkId, categoryRedisKey, tempRedisKey, liveMap.size());
                }
            }
        }
        return stringStandardMarketDataMessageMap;
    }

    public Map<String, StandardMarketDataMessage> getStringStandardMarketDataMessageMapA99(Set<Long> marketCategoryIds,String linkId, StandardMatchInfo standardMatchInfo, StandardSportMarketSell standardSportMarketSell) {
        int oddsLive = isOddsLive(standardMatchInfo.getId());
        //赛前数据源
        String preProviderCode = standardSportMarketSell.getPreMatchDataProviderCode();
        //滚球数据源
        String liveProviderCode = standardSportMarketSell.getLiveMatchDataProviderCode();
        log.info("::{}:: 标准赛事id：{},赛前服务商={},滚球服务商={} ", linkId, standardMatchInfo.getId(), preProviderCode,
                liveProviderCode);
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = new HashMap<>();
        if (StringUtils.isNotBlank(preProviderCode) && 1 == oddsLive) {
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 1;
            Map<String, String> stringHashMap = redisService.hGetAll(categoryRedisKey);
            if (MapUtil.isNotEmpty(stringHashMap)) {
                for (Map.Entry<String, String> dataSourceCodeEntry : stringHashMap.entrySet()) {
                    Long marketCategoryId = Long.valueOf(dataSourceCodeEntry.getKey());
                    if (!CollectionUtils.isEmpty(marketCategoryIds) && !marketCategoryIds.contains(marketCategoryId)) {
                        continue;
                    }
                    String dataSourceCode = DataSourceCodeEnum.A99.code;

                    String tempRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode + "_" + marketCategoryId);
                    Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(tempRedisKey);
                    //只取赛前盘
                    Map<String, StandardMarketDataMessage> preMap = standardMarketMessageMap.entrySet().stream().filter(e -> e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(1))
                            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    stringStandardMarketDataMessageMap.putAll(preMap);
                    log.info("::{}:: sendMatchMarketData,赛前玩法开售key={}, 赛前redisKey={},赛前盘缓存总数据：{} ",
                            linkId, categoryRedisKey, tempRedisKey, preMap.size());
                }
            }
        }
        if (StringUtils.isNotBlank(liveProviderCode) && 0 == oddsLive) {
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + 0;
            Map<String, String> stringHashMap = redisService.hGetAll(categoryRedisKey);
            if (MapUtil.isNotEmpty(stringHashMap)) {
                for (Map.Entry<String, String> dataSourceCodeEntry : stringHashMap.entrySet()) {
                    Long marketCategoryId = Long.valueOf(dataSourceCodeEntry.getKey());
                    if (!CollectionUtils.isEmpty(marketCategoryIds) && !marketCategoryIds.contains(marketCategoryId)) {
                        continue;
                    }
                    String dataSourceCode = DataSourceCodeEnum.A99.code;
                    String tempRedisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + standardMatchInfo.getId() + "_" + dataSourceCode + "_" + marketCategoryId);
                    Map<String, StandardMarketDataMessage> standardMarketMessageMap = redisService.hGetAll(tempRedisKey);
                    //只取滚球盘
                    Map<String, StandardMarketDataMessage> liveMap = standardMarketMessageMap.entrySet().stream().filter(e ->
                            e.getValue().getMarketType() != null && e.getValue().getMarketType().equals(0)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
                    stringStandardMarketDataMessageMap.putAll(liveMap);
                    log.info("::{}:: sendMatchMarketData,滚球玩法开售key={}, 滚球redisKey={},滚球盘缓存总数据：{} ",
                            linkId, categoryRedisKey, tempRedisKey, liveMap.size());
                }
            }
        }
        return stringStandardMarketDataMessageMap;
    }

}
