package com.panda.merge.component;

import cn.hutool.crypto.digest.DigestUtil;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.constant.SaleMatchSellStausEnum;
import com.panda.merge.dto.StandardMatchInfoDetail;
import com.panda.merge.dto.ThirdMarketDTO;
import com.panda.merge.dto.ThirdMatchMarketDTO;
import com.panda.merge.dto.message.ThirdSportMarketMessage;
import com.panda.merge.model.MarketCategorySell;
import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdSportMarketOdds;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.service.MarketCategorySellService;
import com.panda.merge.service.ThirdMarketCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 开出去的玩法不是三方数据源直接入库
 */
@Component
@Slf4j
public class ThirdMarketSaveProcessor {

    @Autowired
    private ThirdMarketCategoryService thirdMarketCategoryService;
    @Autowired
    private MarketCategorySellService marketCategorySellService;
    @Autowired
    private RedisService redisService;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    /**
     * 1.冠军盘口不处理
     * 2.三方玩法转换标准玩法
     * 3.校验开出的标准玩法，是不是数据商盘口
     * 是：返回到新集合
     * 否：返回到新集合并设置标识
     *
     * @param linkId
     * @param thirdMatchInfo
     * @param standardMatchInfo
     * @param thirdMatchMarketDTO
     * @param marketType
     * @param thirdSportMarketMessages
     * @return
     */
    public List<ThirdMarketDTO> marketSaveProcessor(String linkId, ThirdMatchInfo thirdMatchInfo, StandardMatchInfoDetail standardMatchInfo, ThirdMatchMarketDTO thirdMatchMarketDTO, Integer marketType, List<ThirdSportMarketMessage> thirdSportMarketMessages,Long dataSourceTime,  List<ThirdSportMarketOdds> thirdSportMarketOddsUpdate) {
        //最终返回盘口
        List<ThirdMarketDTO> newList = new ArrayList<>();
        String dataSourceCode = thirdMatchInfo.getDataSourceCode();
        String thirdMatchSourceId = thirdMatchMarketDTO.getThirdMatchSourceId();
        List<ThirdMarketDTO> thirdMarketDTOs = thirdMatchMarketDTO.getMarketList();
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());
        sw.start("ThirdMarketSaveProcessor方法耗时");
        //根据三方玩法ID分组
        Map<String, List<ThirdMarketDTO>> ThirdMarketMapDTO = thirdMarketDTOs.stream().collect(Collectors.groupingBy(ThirdMarketDTO::getThirdMarketCategorySourceId));
        //当未开售时增加校验，取开售缓存再校验一次,并且开售缓存里面的一定是开售了的
        Map<String, String> oldStringHashMap = new HashMap<>();
        if (null != standardMatchInfo) {
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + marketType;
            oldStringHashMap = redisService.hGetAll(categoryRedisKey);
        }
        for (Map.Entry<String, List<ThirdMarketDTO>> entry : ThirdMarketMapDTO.entrySet()) {
            //三方盘口ID
            String thirdCategorySourceId = entry.getKey();
            //47319：数据商盘口状态是封 数据商盘口投注项全是未激活 ，改为关
            checkMarketStateAndChange(linkId,entry.getValue(),dataSourceCode);
            //找到标准玩法ID
            ThirdMarketCategory thirdMarketCategory = thirdMarketCategoryService.getItem(dataSourceCode, thirdCategorySourceId);
            if (thirdMarketCategory == null) {
                log.info("::{}::ThirdMarketSaveProcessor,未找到三方玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                continue;
            }
            if (null == thirdMarketCategory.getReferenceId() || 0L == thirdMarketCategory.getReferenceId()) {
                log.info("::{}::ThirdMarketSaveProcessor,三方玩法未绑定标准玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                continue;
            }
            //标准玩法
            Long marketCategoryId = thirdMarketCategory.getReferenceId();
            if (null != standardMatchInfo) {
                //获取玩法开售
                MarketCategorySell marketCategorySell = null;
                if (oldStringHashMap != null && oldStringHashMap.containsKey(marketCategoryId.toString())) {
                    marketCategorySell = new MarketCategorySell();
                    marketCategorySell.setSellStatus(SaleMatchSellStausEnum.Sold.name());
                    marketCategorySell.setDataSourceCode(oldStringHashMap.get(marketCategoryId.toString()));
                }
                if (MapUtils.isEmpty(oldStringHashMap)) {
                    marketCategorySell = marketCategorySellService.getItem(linkId, standardMatchInfo.getId(), marketType, marketCategoryId);
                }
                //玩法开售表数据源与数据商赔率数据源对比，一致不处理走原逻辑加锁，不一致直接入库
                if (null != marketCategorySell && StringUtils.equals(marketCategorySell.getDataSourceCode(), dataSourceCode) &&
                        SaleMatchSellStausEnum.Sold.name().equalsIgnoreCase(marketCategorySell.getSellStatus())) {
                    log.info("::{}::ThirdMarketSaveProcessor,一致不处理走原逻辑加锁,玩法ID:{},开售数据源:{}", linkId, marketCategoryId, dataSourceCode);
                    List<ThirdMarketDTO> marketDTOS = entry.getValue();
                    marketDTOS.stream().forEach(market->{
                        //赋值标准玩法ID
                        market.setMarketCategoryId(marketCategoryId);
                    });
                    newList.addAll(marketDTOS);
                    continue;
                }
            }
            for (ThirdMarketDTO thirdMarketDTO : entry.getValue()) {
                //赋值标准玩法ID
                thirdMarketDTO.setMarketCategoryId(marketCategoryId);
                //不走加锁逻辑直接入库
                thirdMarketDTO.setLock(Boolean.FALSE);
                //两项盘数据源赔率合法性验证
                if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(thirdMarketDTO.getStatus()) && !CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList()) && thirdMarketDTO.getMarketOddsList().size() == 2) {
                    if (thirdMarketDTO.getMarketOddsList().get(0).getOriginalOddsValue() < 1.01 * 100000 || thirdMarketDTO.getMarketOddsList().get(1).getOriginalOddsValue() < 1.01 * 100000) {
                       //如果是A01赔率 判断是否开启延长开售才封盘 开启则不封盘/不开启则正常处理 注:(玩法id 2 4 18 19)
                        Object a01ExtendedTimeObjects  = null;
                        if (standardMatchInfo != null) {
                            a01ExtendedTimeObjects = redisService.get(Constant.REDIS_KEY.A01_EXTENDED_TIME_STATUS_KEY + standardMatchInfo.getMarketSell().getMatchInfoId());
                        }
                        if(!thirdMarketDTO.getDataSourceCode().equals(DataSourceCodeEnum.AO.code)||!checkA01ExtendedTimeStatus(thirdMarketDTO,a01ExtendedTimeObjects)){
                            thirdMarketDTO.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                            log.info("::{}::ThirdMarketSaveProcessor,两项盘(三方盘口源id):{},如果存在一个投注项原始赔率小于1.01,合法性封盘", linkId, thirdMarketDTO.getThirdMarketSourceId());
                        }
                     }
                }
                String dataSourceTimeKey;
                if (dataSourceCode.equals(DataSourceCodeEnum.TX.code)) {
                    dataSourceTimeKey = Constant.REDIS_KEY.RONGHE_THRID_MARKET_DATASOURCE_TIME + thirdMatchInfo.getDataSourceCode() + "_" + thirdMatchInfo.getSportId() + "_" + thirdMatchSourceId + "_" + thirdMarketDTO.getThirdMarketCategorySourceId() + "_" + thirdMarketDTO.getOfferLineId();
                    log.info("::{}::ThirdMarketSaveProcessor,TX盘口时间戳打印,三方源盘口id:{},RedisKEY:{},时间戳:{}", linkId, thirdMarketDTO.getThirdMarketSourceId(), dataSourceTimeKey, thirdMarketDTO.getModifyTime());
                } else {
                    dataSourceTimeKey = Constant.REDIS_KEY.RONGHE_THRID_MARKET_DATASOURCE_TIME + thirdMatchInfo.getDataSourceCode() + "_" + thirdMatchInfo.getSportId() + "_" + thirdMarketDTO.getThirdMarketSourceId();
                }
                String dataSourceTimeKeyMd5 = DigestUtil.md5Hex(dataSourceTimeKey);
                Long oldTime = (Long) redisService.get(dataSourceTimeKeyMd5);
                if (oldTime != null && oldTime > thirdMarketDTO.getModifyTime()) {
                    log.info("::{}::ThirdMarketSaveProcessor,盘口时间戳小于当前盘口时间戳,三方源盘口id:{},RedisKEY:{},旧时间戳:{}", linkId, thirdMarketDTO.getThirdMarketSourceId(), dataSourceTimeKey, oldTime);
                    continue;
                }
                log.info("::{}::ThirdMarketSaveProcessor,盘口时间戳校验,三方盘口ID:{},key:{},新时间戳:{},旧时间戳:{},当前时间:{}", linkId, thirdMarketDTO.getThirdMarketSourceId(), dataSourceTimeKey, thirdMarketDTO.getModifyTime(), oldTime, System.currentTimeMillis());
                redisService.set(dataSourceTimeKeyMd5, thirdMarketDTO.getModifyTime(), RedisConfig.REDIS_MY_TIME);
                if (null != standardMatchInfo) {
                    //滚球期间下发赛前盘口不处理
                    if (marketType == 1 && !Objects.isNull(redisService.get(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchInfo.getId()))) {
                        //如果这个时候来了BC的早盘关盘，需要去关滚球盘
                        if (DataSourceCodeEnum.BC.code.equalsIgnoreCase(dataSourceCode) && thirdMarketDTO.getStatus().equals(Constant.SPORT_MARKET.STATUS.DEACTIVATED)) {
                            thirdMarketDTO.setMarketType(0);
                        } else {
                            log.info("::{}::ThirdMarketSaveProcessor,标准赛事已经进入即将开赛阶段，不处理任何早盘数据，直接返回", linkId);
                            continue;
                        }
                    }
                    if (StandardSportTypeEnum.FootBall.code.equals(thirdMatchInfo.getSportId())
                            && !MarginCategoryConfig.IGNORE_SCORE_DATASOURCE_CODE.contains(dataSourceCode)) {
                        //TX让球比分处理
                        thirdMatchMarketProcessor.txHandicapDispose(linkId, marketCategoryId, standardMatchInfo, thirdMarketDTO, thirdMatchInfo, dataSourceCode);
                        //数据源角球基准分计算
                        thirdMatchMarketProcessor.cornerScore(linkId, marketCategoryId, standardMatchInfo, thirdMarketDTO, thirdMatchInfo, dataSourceCode);
                        //15分钟进球/角球基准分计算
                        thirdMatchMarketProcessor.fifteenMinutesScore(linkId, marketCategoryId, standardMatchInfo, thirdMarketDTO);
                    }
                }
                //三方赔率入库
                ThirdSportMarketMessage thirdSportMarketMessage = thirdMatchMarketProcessor.processThirdSportMarket(linkId, dataSourceCode, thirdMatchInfo, thirdMarketDTO, thirdMarketCategory,thirdSportMarketOddsUpdate);
                //百家赔
                if (thirdSportMarketMessage != null) {
                    thirdSportMarketMessages.add(thirdSportMarketMessage);
                }
                newList.add(thirdMarketDTO);
            }
        }
        sw.stop();
        log.info("::{}::ThirdMarketSaveProcessor,盘口处理耗时{}ms," + sw.prettyPrint(), linkId, sw.getTotalTimeMillis());
        return newList;
    }
    /**
     * 47319：数据商盘口状态是封 数据商盘口投注项全是未激活 ，改为关
     * @param marketList
     */
    public void checkMarketStateAndChange(String linkId,List<ThirdMarketDTO> marketList,String dataSourceCode) {
        if (dataSourceCode.equals(DataSourceCodeEnum.OD.getCode())
                || dataSourceCode.equals(DataSourceCodeEnum.BE.getCode())) {
            return;
        }
        for (ThirdMarketDTO thirdMarketDTO : marketList) {
            try {
                //如果数据商盘口为关
                if(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED == thirdMarketDTO.getStatus()){
                    continue;
                }
                //判断数据商盘口状态是否是'封'
                if (Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.SUSPENDED != thirdMarketDTO.getStatus()) {
                    //验证投注项是否全是'未激活'
                    if (thirdMarketDTO.getMarketOddsList().stream().anyMatch(odds -> odds.getActive() == 1)) {
                        continue;
                    }
                }
                thirdMarketDTO.setStatus(Constant.TRADE_MARKET_CONFIG.MARKET_STATUS.DEACTIVATED);
            } catch (Exception e) {
                log.error("::{}::checkMarketStateAndChange检查数据源盘口是否需要关盘异常，error::{}",linkId,e);
            }
        }
    }

    /**
     * 验证A01是否开启延长开售
     * @return
     */
    public static Boolean checkA01ExtendedTimeStatus(ThirdMarketDTO thirdMarketDTO,Object a01ExtendedTimeObjects){
        if(!Objects.isNull(thirdMarketDTO)){
            //Object a01ExtendedTimeObjects  = redisService.get(Constant.REDIS_KEY.A01_EXTENDED_TIME_STATUS_KEY + standardMatchInfo.getMarketSell().getMatchInfoId());
            if (!Objects.isNull(a01ExtendedTimeObjects)) {
                Integer a01ExtendedTimeStatus = (Integer) a01ExtendedTimeObjects;
                if (a01ExtendedTimeStatus == 1 && thirdMarketDTO.getDataSourceCode().equals(DataSourceCodeEnum.AO.code)
                        && MarginCategoryConfig.A01_EXTENDED_TIME_CATEGORY.contains(thirdMarketDTO.getMarketCategoryId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
