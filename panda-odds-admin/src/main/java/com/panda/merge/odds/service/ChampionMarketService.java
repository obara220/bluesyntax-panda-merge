package com.panda.merge.odds.service;

import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.SellStatusEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.mapper.StandardOutrightMarketMapper;
import com.panda.merge.mapper.StandardOutrightMatchInfoMapper;
import com.panda.merge.model.*;
import com.panda.merge.service.StandardSportMarketNewService;
import com.panda.merge.service.StandardSportMarketOddsNewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ChampionMarketService {

    @Autowired
    public RedisService redisService;

    @Autowired
    private StandardOutrightMatchInfoMapper standardOutrightMatchInfoMapper;

    @Autowired
    private StandardOutrightMarketMapper standardOutrightMarketMapper;

    @Autowired
    private StandardSportMarketNewService standardSportMarketNewService;

    @Autowired
    private StandardSportMarketOddsNewService standardSportMarketOddsNewService;


    public  Map<String, StandardMarketDataMessage> championMarketCacheExecute(String linkId, Long standardMatchId, String dataSourceCode) {
        Map<String, StandardMarketDataMessage> newMarketDataMessageMap = new HashMap<>();

        log.info("::{}::championMarketCacheExecute入参; standardMatchId:{}, dataSourceCode:{}", linkId, standardMatchId, dataSourceCode);
        StandardOutrightMatchInfo standardMatchInfo = standardOutrightMatchInfoMapper.selectByPrimaryKey(standardMatchId);
        if ( Objects.isNull(standardMatchInfo) ) {
            log.info("::{}::championMarketCacheExecute查询冠军赛为空!", linkId);
            return null;
        }

        StandardOutrightMarketExample standardOutrightMarketExample = new StandardOutrightMarketExample();
        standardOutrightMarketExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId).andMarketSellStatusEqualTo(SellStatusEnum.SOLD.value);
        List<StandardOutrightMarket> outrightMarkets = standardOutrightMarketMapper.selectByExample(standardOutrightMarketExample);
        if ( CollectionUtils.isEmpty(outrightMarkets) ) {
            log.info("::{}::championMarketCacheExecute查询开售冠军盘口为空!", linkId);
            return null;
        }

        List<Long> relationMarketIds = outrightMarkets.stream().map(StandardOutrightMarket::getId).collect(Collectors.toList());

        List<StandardSportMarket> standardSportMarketList = standardSportMarketNewService.getItemsByRelationMarketIds(relationMarketIds);
        if ( CollectionUtils.isEmpty(standardSportMarketList) ) {
            log.info("::{}::championMarketCacheExecute查询标准盘口为空!", linkId);
            return null;
        }

        String marketKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchId + "_" + dataSourceCode;
        for ( StandardSportMarket standardSportMarket : standardSportMarketList) {
            List<StandardSportMarketOdds> standardSportMarketOddsList = standardSportMarketOddsNewService.getItemList(standardSportMarket.getId());
            if ( CollectionUtils.isEmpty(standardSportMarketOddsList) ) {
                log.info("::{}::championMarketCacheExecute查询赔率为空, marketId:{}", linkId, standardSportMarket.getId());
                continue;
            }
            // 查询冠军盘口
            StandardMarketDataMessage standardMarketDataMessage = convertToStandardMarketDataMessage(standardSportMarketOddsList, standardSportMarket,TimeUtils.millsSecondsEast8ZoneGmt()-10*1000);
            redisService.hSet(marketKey, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage, marketCacheTime(standardMatchInfo.getStandrdOutrightMatchEndTime()));
            newMarketDataMessageMap.put(standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage);
        }
        log.info("::{}::championMarketCacheExecute执行完毕,marketKey:{}", linkId, marketKey);
        return newMarketDataMessageMap;
    }


    /**
     * 盘口缓存时间
     * （比赛时间 - 系统时间） + 2天时间
     *
     * @param beginTime 比赛时间
     */
    public Long marketCacheTime(Long beginTime) {
        if (beginTime == null || beginTime == 0) {
            return RedisConfig.REDIS_DEFAULT_TIME.longValue();
        }
        //获取剩余开赛时间 =  开赛时间-当前时间
        Long cacheTime = (beginTime - Calendar.getInstance().getTimeInMillis());
        if (cacheTime <= 0) {
            return RedisConfig.REDIS_DEFAULT_TIME.longValue();
        }
        //redis过期时间为秒 = 剩余开赛时间 + 2天时间 ，为redis过期时间
        return (cacheTime / 1000) + (2L * RedisConfig.REDIS_DEFAULT_TIME);
    }

    /**
     * 封装投递给下游的标准赔率数据结构
     *
     * @param standardSportMarketOddsList
     * @param standardSportMarket
     * @return
     */
    public StandardMarketDataMessage convertToStandardMarketDataMessage(List<StandardSportMarketOdds> standardSportMarketOddsList,
                                                                        StandardSportMarket standardSportMarket, Long dataSourceTime) {
        StandardMarketDataMessage standardMarketMessage = new StandardMarketDataMessage();
        BeanUtils.copyProperties(standardSportMarket, standardMarketMessage);
        //收集足球、篮球附加字段玩法
        List<Long> add1List = new ArrayList<>();
        add1List.addAll(MarginCategoryConfig.FootBall_MY_CATEGORY);
        add1List.addAll(MarginCategoryConfig.BASKETBALL_MY_CATEGORY);
        //盘口值的绝对值  addition1
        if (StringUtils.isNotBlank(standardMarketMessage.getAddition1()) &&
                add1List.contains(standardMarketMessage.getMarketCategoryId())) {
            standardMarketMessage.setMarketOddsValue(Math.abs(Double.parseDouble(standardMarketMessage.getAddition1())));
        } else {
            standardMarketMessage.setMarketOddsValue(0D);
        }

        if (!CollectionUtils.isEmpty(standardSportMarketOddsList)) {
            List<StandardMarketOddsDataMessage> standardMarketOddsMessageList = new ArrayList<>();
            for (StandardSportMarketOdds standardSportMarketOdds : standardSportMarketOddsList) {
                StandardMarketOddsDataMessage standardMarketOddsMessage = new StandardMarketOddsDataMessage();
                BeanUtils.copyProperties(standardSportMarketOdds, standardMarketOddsMessage);
                standardMarketOddsMessageList.add(standardMarketOddsMessage);
            }
            standardMarketMessage.setMarketOddsList(standardMarketOddsMessageList);
        }
        standardMarketMessage.setModifyTime(null == standardMarketMessage.getModifyTime() ? dataSourceTime : standardMarketMessage.getModifyTime());
        return standardMarketMessage;
    }
}
