package com.panda.merge.task;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.StandardMatchInfoDetail;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.rocketmq.producer.StandardMarketOddsProducer;
import com.panda.merge.service.ConfigMatchStatusService;
import com.panda.merge.service.ThirdSportMarketOddsNewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.*;


@Slf4j
@Component
public class CloseConvertMarketTask extends BaseTask {
    String HOST_ADDRESS_100 = "";
    @Autowired
    RedisService redisService;
    @Lazy
    @Autowired
    StandardMarketOddsProducer standardMarketOddsProducer;
    @Lazy
    @Autowired
    ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Autowired
    ConfigMatchStatusService configMatchStatusService;
    @Autowired
    ThirdSportMarketOddsNewService thirdSportMarketOddsService;

    @PostConstruct
    public void initAddress() {
        if (StringUtils.isEmpty(HOST_ADDRESS_100)) {
            InetAddress address = getLocalHostExactAddress();
            HOST_ADDRESS_100 = address.getHostAddress();
            redisService.set(Constant.REDIS_KEY.RONGHE_LOCK100, HOST_ADDRESS_100, RedisConfig.REDIS_YEAR_TIME);
        }
    }


    @Scheduled(initialDelay = 1000, fixedRate = 5000)
    @Async("CloseConvertMarketThreadPool")
    public void monitorConfigMatchStatus() {

        String linkId = "100sauto_" + UUIdUtils.getId();
        String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK100;
        if (StringUtils.isEmpty(HOST_ADDRESS_100)) {
            InetAddress address = null;
            try {
                address = getLocalHostExactAddress();
            } catch (Exception e) {
                e.printStackTrace();
            }
            HOST_ADDRESS_100 = address.getHostAddress();
        }
        Object oldAddress = redisService.get(redisLocKey);
        if (oldAddress != null && !StringUtils.equals((String) oldAddress, HOST_ADDRESS_100)) {
            return;
        }
        log.info("::{}::接拒关封 100s盘口无更新自动下发关盘 ", linkId);
        String configKey = Constant.REDIS_KEY.RONGHE_CONFIG_MATCH_STATUS_DATA;
        Set<Long> config = (Set<Long>) redisService.get(configKey);
        if (CollectionUtils.isEmpty(config)) {
            log.info("::{}::接拒关封 100s盘口无更新自动下发关盘,赛事配置信息不存在 ", linkId);
            return;
        }
        //当前时间减去100s
        long currTime = System.currentTimeMillis() - 100000;
        Long updateTime;
        String[] categoryMarket;
        for (Long matchId : config) {
            //数据源 对应 玩法集合
            Map<String, List<Long>> sourceCategoryIdsMap = new HashMap<>();
            String key = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_ODDS_UPDATETIME + matchId;
            Map<String, Long> market = redisService.hGetAll(key);
            //历史关盘数据
            String redisDeaOddsKey = Constant.REDIS_KEY.RONGHE_MATCH_MARKET_DEA_ODDS + matchId;
            if (CollectionUtils.isEmpty(market)) {
                continue;
            }
            Set<Long> closMarket = new HashSet<>();
            for (String marketId : market.keySet()) {
                updateTime = market.get(marketId);
                categoryMarket = marketId.split("_");
                if (updateTime.longValue() > currTime) {
                    log.info("::{}::接拒关封 100s盘口无更新自动下发关盘,赛事ID:{},盘口:{}_{},更新时间大于当前时间:{}_{} ",
                            linkId, marketId, categoryMarket[1], categoryMarket[2], updateTime.longValue(), currTime);
                    continue;
                }
                Set<String> oldDeaSet = (Set<String>) redisService.hGet(redisDeaOddsKey, categoryMarket[0]);
                if (oldDeaSet != null && oldDeaSet.contains(categoryMarket[1] + "_" + categoryMarket[2])) {
                    log.info("::{}::接拒关封 100s盘口无更新自动下发关盘,赛事ID:{},关盘缓存已经存在,盘口ID:{}_{} ", linkId, marketId, categoryMarket[1], categoryMarket[2]);
                    continue;
                }
                closMarket.add(Long.valueOf(categoryMarket[1]));

                Long categoryId = Long.valueOf(categoryMarket[0]);
                String dataSoureCode = categoryMarket[2];
                if (sourceCategoryIdsMap.containsKey(dataSoureCode)) {
                    sourceCategoryIdsMap.get(dataSoureCode).add(categoryId);
                } else {
                    List<Long> a = new ArrayList<>();
                    a.add(categoryId);
                    sourceCategoryIdsMap.put(dataSoureCode, a);
                }
            }
            if (!CollectionUtils.isEmpty(closMarket)) {
                log.info("::{}::接拒关封 100s盘口无更新自动下发关盘 赛事ID:{},盘口集合:{},数据源玩法集合:{}",
                        linkId, matchId, closMarket, JSONObject.toJSONString(sourceCategoryIdsMap));
                processOddsByAll(linkId, matchId, closMarket, sourceCategoryIdsMap);

            }
        }
    }

    private void processOddsByAll(String linkId, Long matchId, Set<Long> closMarket, Map<String, List<Long>> sourceCategoryIdsMap) {
        linkId = linkId + "_" + matchId;
        StandardMatchInfoDetail match = thirdMatchMarketProcessor.getStandardMatchInfo(false, matchId);
        if (match == null) {
            configMatchStatusService.deleteRedisData(linkId, matchId);
            return;
        }
        StandardSportMarketSell standardSportMarketSell = thirdMatchMarketProcessor.getStandardSportMarketSell(false, matchId);
        if (standardSportMarketSell == null) {
            configMatchStatusService.deleteRedisData(linkId, matchId);
            return;
        }
        String lockValue = UUIdUtils.getId() + "_" + linkId;
        String redisLocKey = Constant.REDIS_KEY.RONGHE_LOCK + match.getId();
        log.info("::{}:: 接拒100s自动关盘,redisLocKey:{},准备获取分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);
        boolean isLock = redisService.tryLock(redisLocKey, lockValue, 1, 1);
        log.info("::{}:: 接拒100s自动关盘,redisLocKey:{},获取到分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);
        if (isLock) {
            try {
                Set<StandardMarketDataMessage> clos = new HashSet<StandardMarketDataMessage>();
                List<StandardMarketDataMessage> marketsAll = thirdMatchMarketProcessor.getStringStandardMarketDataMessageByDataSourceCode(linkId, match, sourceCategoryIdsMap);
                marketsAll.forEach(x -> {
                    Long sendDataId = StringUtils.isNotEmpty(x.getSendData()) && !"''".equals(x.getSendData()) ? Long.valueOf(x.getSendData()) : 0L;
                    if (closMarket.contains(x.getRelationMarketId()) || closMarket.contains(sendDataId)) {
                        x.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        x.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        //获取当前数据源缓存中所有的盘口
                        String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + matchId + "_" + x.getDataSourceCode() + "_" + x.getMarketCategoryId());
                        redisService.hSet(redisKey, x.getRelationMarketId().toString(), x, thirdMatchMarketProcessor.marketCacheTime(match.getBeginTime()));
                        clos.add(x);
                    }
                });
                Set<Long> marketCategoryIdSet = new HashSet<>();
                //根据当前标准玩法对应数据源过滤除最终下发盘口
                Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = new HashMap<>();
                int oddsLive = thirdMatchMarketProcessor.isOddsLive(matchId);
                String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + matchId + "_" + oddsLive;
                Map<String, String> stringHashMap = redisService.hGetAll(categoryRedisKey);
                stringHashMap.values().stream().distinct().forEach(dataSourceCode -> {
                    marketsAll.forEach(markets -> {
                        if (stringHashMap.containsKey(markets.getMarketCategoryId().toString())
                                && stringHashMap.get(markets.getMarketCategoryId().toString()).equals(dataSourceCode)) {
                            marketCategoryIdSet.add(markets.getMarketCategoryId());
                            stringStandardMarketDataMessageMap.put(markets.getRelationMarketId().toString(), markets);
                        }
                    });
                });
                //清除掉100s 盘口记时
                thirdSportMarketOddsService.deleteMatchMarketOddsOfRedis(linkId, matchId, clos, System.currentTimeMillis());
                configMatchStatusService.saveDeaMarketOfRedis(linkId, matchId, clos, match.getBeginTime());
                if (!MapUtils.isEmpty(stringStandardMarketDataMessageMap)) {
                    //盘口推送
                    thirdMatchMarketProcessor.processOddsByAll(linkId,-1,null, match, marketCategoryIdSet, stringStandardMarketDataMessageMap, System.currentTimeMillis(), standardSportMarketSell, null);
                } else {
                    log.info("::{}::赛事ID:{}, 盘口为null，不作关盘下发处理的盘口", linkId, matchId);
                }
            } finally {
                redisService.unLock(redisLocKey, lockValue);
                log.info("::{}:: 接拒100s自动关盘,redisLocKey:{},释放分布式锁,lockValue:{}", linkId, redisLocKey, lockValue);
            }
        }
    }
}