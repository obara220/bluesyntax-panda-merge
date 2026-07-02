package com.panda.merge.rocketmq.processor;


import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.RedisHelper;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.utils.ListUtils;
import com.panda.merge.common.utils.MD5Utils;
import com.panda.merge.common.utils.MergeFunctionUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.config.ThreadPoolConfig;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.constant.RelationKeyFactory;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.ThirdSportMarketMessage;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.ThirdSportMarketMergeProducer;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.ZERO;

@Slf4j
@Validated
@Component
@Async("ProcessAllThirdMarketThreadPool")
public class ThirdAllMarketProcessor extends BaseProcessor {
    @Autowired
    private ThirdOutrightMatchInfoService thirdOutrightMatchInfoService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private StandardOutrightMatchInfoService standardOutrightMatchInfoService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    ThirdMarketCategoryService thirdMarketCategoryService;
    @Autowired
    public ThirdSportMarketMergeProducer thirdSportMarketMergeProducer;
    @Autowired
    RedisService redisService;
    @Autowired
    private ThreadPoolConfig threadPoolConfig;
    @Autowired
    private ThirdSportMarketService thirdSportMarketService;
    @Autowired
    private ThirdSportMarketOddsService thirdSportMarketOddsService;
    @Autowired
    private RedisHelper redisHelper;

    @ExceptionHelper
    public void execute(@Valid Request<ThirdMatchMarketDTO> request) {
        String linkId = request.getLinkId();
        ThirdMatchMarketDTO thirdMatchMarketDTO = request.getData();
        String dataSourceCode = thirdMatchMarketDTO.getDataSourceCode();
        String thirdMatchSourceId = thirdMatchMarketDTO.getThirdMatchSourceId();
        Long modifyTime = thirdMatchMarketDTO.getModifyTime();
        Integer marketType = thirdMatchMarketDTO.getMarketList().get(0).getMarketType();
        long befTime = System.currentTimeMillis();
        log.info("::{}::百家赔:接收数据源赔率开始,三方赛事id:{}", linkId, thirdMatchSourceId);
        //判断冠军玩法
        boolean isOutRight = Arrays.asList(MarginCategoryConfig.THIRD_OUTRIGHT_CATEGORY).contains(thirdMatchMarketDTO.getMarketList().get(0).getThirdMarketCategorySourceId());
        String dataSourceCodeDB = dataSourceCode.split("-")[0].toUpperCase();
        //兼容冠军玩法，获取三方赛事信息
        log.info("::{}::百家赔:接收数据源赔率开始,三方赛事:{},数据源编码={}", linkId, thirdMatchSourceId,dataSourceCode);
        ThirdMatchInfo thirdMatchInfo = getThirdMatchInfo(isOutRight, dataSourceCodeDB, thirdMatchSourceId);
        if (thirdMatchInfo == null) {
            cacheThirdMarket(linkId, thirdMatchMarketDTO, dataSourceCode, dataSourceCodeDB);
            log.info("::{}::百家赔:三方赛事不存在,三方数据源id:{},冠军玩法:{}", linkId, thirdMatchSourceId, isOutRight);
            return;
        }
        log.info("::{}::百家赔:接收数据源赔率开始,赛事信息:{}", linkId, thirdMatchSourceId);
        //兼容冠军玩法，获取标准赛事信息
        StandardMatchInfoDetail standardMatchInfo = getStandardMatchInfo(isOutRight, thirdMatchInfo.getReferenceId());
        if (standardMatchInfo == null) {
            cacheThirdMarket(linkId, thirdMatchMarketDTO, dataSourceCode, dataSourceCodeDB);
            log.info("::{}::百家赔:标准赛事不存在,三方赛事id:{}", linkId, thirdMatchInfo.getId());
            return;
        }
        log.info("::{}::百家赔:接收数据源赔率开始,赛事开售信息信息:{}", linkId, thirdMatchSourceId);
        //兼容冠军玩法，获取赛事开售信息
        StandardSportMarketSell standardSportMarketSell = getStandardSportMarketSell(isOutRight, thirdMatchInfo.getReferenceId());
        if (standardSportMarketSell == null) {
            cacheThirdMarket(linkId, thirdMatchMarketDTO, dataSourceCode, dataSourceCodeDB);
            log.info("::{}::百家赔:赛事未开售赔率不下发,标准赛事id:{}", linkId, standardMatchInfo.getId());
            return;
        }

        //存储需要下发的三方数据商盘口集合
        List<ThirdSportMarketMessage> thirdSportMarketMessages = new ArrayList<>();
        if (CollectionUtils.isEmpty(thirdMatchMarketDTO.getMarketList())) {
            return;
        }
        log.info("::{}::百家赔:接收数据源赔率开始,开始处理盘口信息:{}", linkId, thirdMatchMarketDTO.getMarketList().size());
        Map<String, List<ThirdMarketDTO>> thirdMarketDTOMap = thirdMatchMarketDTO.getMarketList().stream().collect(Collectors.groupingBy(ThirdMarketDTO::getThirdMarketCategorySourceId));
        for (Map.Entry<String, List<ThirdMarketDTO>> entry : thirdMarketDTOMap.entrySet()) {
            String thirdCategorySourceId = entry.getKey();
            log.info("::{}::百家赔:接收数据源赔率开始,查询玩法:{}", linkId, thirdCategorySourceId);
            ThirdMarketCategory thirdMarketCategory = thirdMarketCategoryService.getItem(dataSourceCodeDB, thirdCategorySourceId);
            if (null == thirdMarketCategory) {
                log.info("::{}::百家赔：未找到三方玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                continue;
            }
            log.info("::{}::百家赔:接收数据源赔率开始,查询玩法结束:{}", linkId, thirdCategorySourceId);
            if (null == thirdMarketCategory.getReferenceId() || Long.valueOf(ZERO).equals(thirdMarketCategory.getReferenceId())) {
                log.info("::{}::百家赔：三方玩法未绑定标准玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                continue;
            }
            log.info("::{}::百家赔:接收数据源赔率开始,开始处理盘口数据:{}", linkId, thirdCategorySourceId);
            List<ThirdMarketDTO> value = entry.getValue();
            log.info("::{}::百家赔:接收数据源赔率开始,玩法id:{} ,异步处理数据开始", linkId, thirdCategorySourceId);
            for (ThirdMarketDTO thirdMarketDTO : value) {
                ThirdSportMarketMessage thirdSportMarketMessage = copyThirdMarketDTO(thirdMarketDTO);
                List<ThirdSportMarketOdds> thirdSportMarketOddsList = new ArrayList<>();
                thirdSportMarketMessage.setMarketCategoryId(thirdMarketCategory.getReferenceId());
                thirdSportMarketMessage.setDataSourceCode(dataSourceCode);
                thirdSportMarketMessage.setThirdMarketSourceStatus(thirdMarketDTO.getStatus());
                thirdSportMarketMessage.setStatus(thirdMarketDTO.getStatus());
                //thirdSportMarketMessage.setRelationMarketId(thirdSportMarketService.getRelationMarketId(linkId, standardMatchInfo.getId(), thirdSportMarketMessage.getMarketCategoryId(), thirdSportMarketMessage.getAddition1(), thirdSportMarketMessage.getAddition2(), thirdSportMarketMessage.getAddition3(), thirdSportMarketMessage.getAddition4(), thirdSportMarketMessage.getAddition5(), thirdSportMarketMessage.getMarketType(), thirdSportMarketMessage.getThirdMarketSourceId()));
                thirdSportMarketMessage.setReferenceId(standardMatchInfo.getId());
                if (!CollectionUtils.isEmpty(thirdMarketDTO.getMarketOddsList())) {
                    for (ThirdMarketOddsDTO thirdMarketOddsDTO : thirdMarketDTO.getMarketOddsList()) {
                        ThirdSportMarketOdds thirdSportMarketOdds = copyThirdMarketOddsDTO(thirdMarketOddsDTO);
                        thirdSportMarketOdds.setDataSourceCode(dataSourceCode);
                        //thirdSportMarketOdds.setMarketId(thirdSportMarketMessage.getRelationMarketId());
                        //thirdSportMarketOdds.setId(thirdSportMarketOddsService.getRelationMarketOddsId(thirdSportMarketMessage.getRelationMarketId(), thirdSportMarketOdds.getOddsType(), thirdSportMarketOdds.getThirdOddsFieldSourceId(), thirdSportMarketOdds.getAddition1(), thirdSportMarketMessage.getMarketCategoryId()));
                        thirdSportMarketOddsList.add(thirdSportMarketOdds);

                    }
                }
                thirdSportMarketMessage.setThirdSportMarketOddsList(thirdSportMarketOddsList);
                thirdSportMarketMessages.add(thirdSportMarketMessage);
            }
            log.info("::{}::百家赔:接收数据源赔率开始,玩法id:{} ,异步处理数据完成:{}", linkId, thirdCategorySourceId, value.size());
        }
        log.info("::{}::百家赔:接收数据源赔率开始,处理盘口信息完成:{}", linkId, thirdSportMarketMessages.size());
        if (!CollectionUtils.isEmpty(thirdSportMarketMessages)) {
            //设置盘口id
            getMarketId(linkId, standardMatchInfo, thirdSportMarketMessages);
            //TX百家赔 初盘
            allTxThirdFistMarket(dataSourceCodeDB, dataSourceCode, standardMatchInfo, thirdMatchMarketDTO.getMarketList());
            //LS百家赔 初盘
            allLsThirdFistMarket(dataSourceCodeDB, dataSourceCode, standardMatchInfo, thirdMatchMarketDTO.getMarketList());
            thirdSportMarketMergeProducer.sendThirdSportMarketMessageToMQ(linkId + "_" + dataSourceCode + "_third", standardMatchInfo, thirdSportMarketMessages, modifyTime);
            log.info("::{}::百家赔:接收数据源赔率开始,三方赛事id:{},耗时：{}", linkId, thirdMatchSourceId, befTime - System.currentTimeMillis());
        }
    }

    /**
     * 获取盘口id
     *
     * @param linkId
     * @param standardMatchInfo
     * @param thirdSportMarketMessages
     */
    public void getMarketId(String linkId, StandardMatchInfo standardMatchInfo, List<ThirdSportMarketMessage> thirdSportMarketMessages) {
        try {
            //map<盘口id，盘口标识>
            Map<String, String> marketMap = new HashMap<>();
            List<String> keys = new ArrayList<>();
            thirdSportMarketMessages.stream().forEach(t -> {
                try{
                    String key = RelationKeyFactory.getMarketRelationKeyByThirdInfo(linkId, standardMatchInfo.getId(), t.getMarketCategoryId(), t.getAddition1(), t.getAddition2(), t.getAddition3(), t.getAddition4(), t.getAddition5(), t.getMarketType(), t.getThirdMarketSourceId());
                    keys.add(key);
                    marketMap.put(t.getThirdMarketSourceId(), key);
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
            List<Object> objectList = redisService.mGet(keys);
            Map<String, String> result = new HashMap<>();
            List<String> requiredCallItems = new ArrayList<>();
            redisHelper.postMarketkeyProcMget(keys, objectList, result, requiredCallItems);
            if (!CollectionUtils.isEmpty(requiredCallItems)) {
                Map<String, Object> mset = new HashMap<>();
                for (String requiredCallItem : requiredCallItems) {
                    String relationMarketIdstr = MD5Utils.getLongByMD5(requiredCallItem) + "";
                    mset.put(requiredCallItem, relationMarketIdstr);
                    result.put(requiredCallItem, relationMarketIdstr);
                }
                //set
                redisService.mSet(mset);
            }
            thirdSportMarketMessages.stream().forEach(thirdSportMarketMessage -> {
                thirdSportMarketMessage.setRelationMarketId(Long.valueOf(result.get(marketMap.get(thirdSportMarketMessage.getThirdMarketSourceId()))));
            });
            //设置投注项id
            getMarketOddsId(linkId, standardMatchInfo, thirdSportMarketMessages);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getMarketOddsId(String linkId, StandardMatchInfo standardMatchInfo, List<ThirdSportMarketMessage> thirdSportMarketMessages) {
        List<String> keys = new ArrayList<>();
        //map<投注项id，投注项标识>
        Map<String, String> marketoDDSMap = new HashMap<>();
        for (ThirdSportMarketMessage thirdSportMarketMessage : thirdSportMarketMessages) {
            Long relationMarketId = thirdSportMarketMessage.getRelationMarketId();
            List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketMessage.getThirdSportMarketOddsList();
            if (!CollectionUtils.isEmpty(thirdSportMarketOddsList)) {
                for (ThirdSportMarketOdds t : thirdSportMarketOddsList) {
                    String marketOddsRelationKey = RelationKeyFactory.getMarketOddsRelationKeyByThirdOddsInfo(relationMarketId, t.getOddsType(), t.getThirdOddsFieldSourceId(), t.getAddition1(), thirdSportMarketMessage.getMarketCategoryId());
                    keys.add(marketOddsRelationKey);
                    marketoDDSMap.put(t.getThirdOddsFieldSourceId(), marketOddsRelationKey);
                }
            }
        }
        List<Object> objectList = redisService.mGet(keys);
        Map<String, String> result = new HashMap<>();
        List<String> requiredCallItems = new ArrayList<>();
        redisHelper.postMarketkeyProcMget(keys, objectList, result, requiredCallItems);
        if (!CollectionUtils.isEmpty(requiredCallItems)) {
            Map<String, Object> mset = new HashMap<>();
            for (String requiredCallItem : requiredCallItems) {
                String relationMarketOddsIdstr = MD5Utils.getLongByMD5(requiredCallItem) + "";
                mset.put(requiredCallItem, relationMarketOddsIdstr);
                result.put(requiredCallItem, relationMarketOddsIdstr);
            }
            //set
            redisService.mSet(mset);
        }
        String key = Constant.REDIS_KEY.RONGHE_AO_MARKET_ORIGINAL_ODDS + standardMatchInfo.getId();
        Map<String, Integer> oddsMap = redisService.hGetAll(key);
        if (oddsMap == null) {
            oddsMap = new HashMap<String, Integer>();
        }
        //赋值
        for (ThirdSportMarketMessage thirdSportMarketMessage : thirdSportMarketMessages) {
            String dataSourceCode = thirdSportMarketMessage.getDataSourceCode();
            List<ThirdSportMarketOdds> thirdSportMarketOddsList = thirdSportMarketMessage.getThirdSportMarketOddsList();
            if (!CollectionUtils.isEmpty(thirdSportMarketOddsList)) {
                Map<String, Integer> finalOddsMap = oddsMap;
                thirdSportMarketOddsList.stream().forEach(sportMarketOdds -> {
                    sportMarketOdds.setMarketId(thirdSportMarketMessage.getRelationMarketId());
                    sportMarketOdds.setId(Long.valueOf(result.get(marketoDDSMap.get(sportMarketOdds.getThirdOddsFieldSourceId()))));
                    //缓存 AO原始赔率
                    if (DataSourceCodeEnum.AO.code.equals(dataSourceCode) && MarginCategoryConfig.FootBall_MAIN_CATEGORY.contains(thirdSportMarketMessage.getMarketCategoryId())) {
                        finalOddsMap.put(sportMarketOdds.getId().toString(), sportMarketOdds.getOriginalOddsValue());
                    }
                });
            }
        }
        redisService.hSetAllBasedBucket(key, ConstantSystem.BUCKET_QUANTITY_EIGHT, oddsMap, marketCacheTime(standardMatchInfo.getBeginTime()));

    }

    public ThirdMatchInfo getThirdMatchInfo(boolean isOutRight, String dataSourceCode, String thirdMatchSourceId) {
        if (isOutRight) {
            ThirdOutrightMatchInfo thirdOutrightMatchInfo = thirdOutrightMatchInfoService.getItem(dataSourceCode, thirdMatchSourceId);
            if (thirdOutrightMatchInfo == null) {
                return null;
            }
            //三方赛事信息转换
            ThirdMatchInfo thirdMatchInfo = new ThirdMatchInfo();
            thirdMatchInfo.setId(thirdOutrightMatchInfo.getId());
            thirdMatchInfo.setSportId(thirdOutrightMatchInfo.getSportId());
            thirdMatchInfo.setReferenceId(thirdOutrightMatchInfo.getReferenceId());
            thirdMatchInfo.setDataSourceCode(thirdOutrightMatchInfo.getDataSourceCode());
            thirdMatchInfo.setThirdMatchSourceId(thirdOutrightMatchInfo.getThirdOutrightSourceId());
            return thirdMatchInfo;
        }
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSourceCode, thirdMatchSourceId);
        if (thirdMatchInfo == null) {
            return null;
        }
        //兼容缓存覆盖问题
        if (thirdMatchInfo.getReferenceId() == null || thirdMatchInfo.getReferenceId() == 0) {
            return thirdMatchInfoService.getItem(thirdMatchInfo.getId());
        }
        return thirdMatchInfo;
    }

    public StandardMatchInfoDetail getStandardMatchInfo(boolean isOutRight, Long standardMatchId) {
        StandardMatchInfoDetail standardMatchInfoDetail = new StandardMatchInfoDetail();
        if (isOutRight) {
            StandardOutrightMatchInfo standardOutrightMatchInfo = standardOutrightMatchInfoService.getItem(standardMatchId);
            if (null == standardOutrightMatchInfo) {
                return null;
            }
            //标准赛事信息转换
            BeanUtils.copyProperties(standardOutrightMatchInfo, standardMatchInfoDetail);
            standardMatchInfoDetail.setId(standardOutrightMatchInfo.getId());
            standardMatchInfoDetail.setSportId(standardOutrightMatchInfo.getSportId());
            standardMatchInfoDetail.setDataSourceCode(standardOutrightMatchInfo.getDataSourceCode());
            standardMatchInfoDetail.setOperateMatchStatus(standardOutrightMatchInfo.getMatchMarketStatus());
            standardMatchInfoDetail.setMatchType(1);
            standardMatchInfoDetail.setAutoSellStatus(standardOutrightMatchInfo.getAutoSellStatus());
            //冠军赛事结束时间赋值给beginTime 用于盘口缓存时间计算
            standardMatchInfoDetail.setBeginTime(standardOutrightMatchInfo.getStandrdOutrightMatchEndTime());
            return standardMatchInfoDetail;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        //standardMatchInfo==null
        if (ObjectUtils.isEmpty(standardMatchInfo)) {
            return null;
        }
        BeanUtils.copyProperties(standardMatchInfo, standardMatchInfoDetail);
        standardMatchInfoDetail.setMatchType(0);
        return standardMatchInfoDetail;
    }

    public StandardSportMarketSell getStandardSportMarketSell(boolean isOutRight, Long standardMatchId) {
        if (isOutRight) {
            StandardOutrightMatchInfo standardOutrightMatchInfo = standardOutrightMatchInfoService.getItem(standardMatchId);
            if (standardOutrightMatchInfo == null) {
                return null;
            }
            StandardSportMarketSell standardSportMarketSell = new StandardSportMarketSell();
            standardSportMarketSell.setMatchInfoId(standardOutrightMatchInfo.getId());
            standardSportMarketSell.setPreMatchDataProviderCode(standardOutrightMatchInfo.getDataSourceCode());
            standardSportMarketSell.setPreMatchSellStatus(standardOutrightMatchInfo.getSellStatus());
            return standardSportMarketSell;
        }
        return standardSportMarketSellService.getItem(standardMatchId);
    }

    public void cacheThirdMarket(String linkId, ThirdMatchMarketDTO thirdMatchMarketDTO, String dataSourceCode, String dataSourceCodeDb) {
        if (DataSourceCodeEnum.LS.code.equals(dataSourceCodeDb)) {
            cacheLsThirdMarket(linkId, thirdMatchMarketDTO, dataSourceCode);
            return;
        }
        String thirdMatchSourceId = thirdMatchMarketDTO.getThirdMatchSourceId();
        //缓存KEY
        String thirdMarketKey = Constant.REDIS_KEY.RONGHE_THIRD_STANDARD_MARKET + thirdMatchSourceId;
        //赛事id-数据源-三方盘口id-offerLineId-Dto
        Map<String, Map<Integer, ThirdMarketDTO>> mapMap = null;
        Object obj = redisService.hGet(thirdMarketKey, dataSourceCode);
        if (!Objects.isNull(obj)) {
            mapMap = (Map<String, Map<Integer, ThirdMarketDTO>>) obj;
        }

        for (ThirdMarketDTO thirdMarketDTO : thirdMatchMarketDTO.getMarketList()) {
            //三方玩法源ID
            String thirdMarketCategorySourceId = thirdMarketDTO.getThirdMarketCategorySourceId();
            //TX根据三方玩法 缓存坑位最新数据  Map<三方玩法,Map<坑位,盘口数据>>
            //TX坑位
            Map<Integer, ThirdMarketDTO> categoryPlaceMap = new HashMap<>();
            Integer offerLineId = thirdMarketDTO.getOfferLineId();
            thirdMarketDTO.setDataSourceCode(dataSourceCode);
            categoryPlaceMap.put(offerLineId, thirdMarketDTO);
            if (!Objects.isNull(mapMap)) {
                if (mapMap.get(thirdMarketCategorySourceId) != null) {
                    mapMap.get(thirdMarketCategorySourceId).put(offerLineId, thirdMarketDTO);
                } else {
                    mapMap.put(thirdMarketCategorySourceId, categoryPlaceMap);
                }
            } else {
                mapMap = new HashMap<>();
                mapMap.put(thirdMarketCategorySourceId, categoryPlaceMap);
            }
        }
        if (!Objects.isNull(mapMap)) {
            redisService.hSet(thirdMarketKey, dataSourceCode, mapMap, RedisConfig.REDIS_WEEK_TIME);
            log.error("::{}::百家赔：三方赛事不存在,缓存盘口,三方赛事数据源ID:{},dataSourceCode:{},map:{}", linkId, thirdMatchSourceId, dataSourceCode, mapMap);
        }
    }

    /**
     * 缓存LS盘口
     *
     * @param linkId
     * @param thirdMatchMarketDTO
     * @param dataSourceCode
     */
    public void cacheLsThirdMarket(String linkId, ThirdMatchMarketDTO thirdMatchMarketDTO, String dataSourceCode) {
        String thirdMatchSourceId = thirdMatchMarketDTO.getThirdMatchSourceId();
        //缓存KEY
        String thirdMarketKey = Constant.REDIS_KEY.RONGHE_LS_THIRD_STANDARD_MARKET + thirdMatchSourceId;
        //三方盘口id，Dto
        Map<String, ThirdMarketDTO> mapMap = new HashMap<>();
        Object obj = redisService.hGet(thirdMarketKey, dataSourceCode);
        if (!Objects.isNull(obj)) {
            mapMap = (Map<String, ThirdMarketDTO>) obj;
        }
        for (ThirdMarketDTO thirdMarketDTO : thirdMatchMarketDTO.getMarketList()) {
            thirdMarketDTO.setDataSourceCode(dataSourceCode);
            mapMap.put(thirdMarketDTO.getThirdMarketSourceId(), thirdMarketDTO);
        }
        redisService.hSet(thirdMarketKey, dataSourceCode, mapMap, RedisConfig.REDIS_WEEK_TIME);
    }


    /**
     * TX百家赔球头
     *
     * @param dataSourceCodeDB
     * @param standardMatchInfo
     * @param thirdMarkets
     */
    private void allTxThirdFistMarket(String dataSourceCodeDB, String dataSourceCode, StandardMatchInfo standardMatchInfo, List<ThirdMarketDTO> thirdMarkets) {
        if (DataSourceCodeEnum.TX.getCode().equals(dataSourceCodeDB)) {
            String fistMatchKey = Constant.REDIS_KEY.THIRD_FIST_MATCH;
            //百家赔三方初盘
            String fistKey = Constant.REDIS_KEY.THIRD_FIST_MARKET_HEAD + standardMatchInfo.getId();
            //需要处理的玩法分组
            Map<Long, List<ThirdMarketDTO>> marketListsMap = thirdMarkets.stream().filter(t -> MarginCategoryConfig.THIRD_FIRST_MARKET_BALL_HEAD_CATEGORY.contains(t.getMarketCategoryId()) && Constant.SPORT_MARKET.STATUS.ACTIVE.equals(t.getStatus()) && t.getOfferLineId() == 1L).collect(Collectors.groupingBy(ThirdMarketDTO::getMarketCategoryId));
            for (Map.Entry<Long, List<ThirdMarketDTO>> entry : marketListsMap.entrySet()) {
                ThirdMarketDTO thirdMarketDto = entry.getValue().get(0);
                String key = "THIRD_All_" + dataSourceCode + "_" + thirdMarketDto.getMarketCategoryId() + "_" + thirdMarketDto.getMarketType();
                Object obj = redisService.hGet(fistKey, key);
                if (Objects.isNull(obj)) {
                    StandardMarketDataMessage standardMarketDataMessage = thirdConvertStandardMarket(thirdMarketDto);
                    if (null != standardMarketDataMessage) {
                        standardMarketDataMessage.setMarketCategoryId(thirdMarketDto.getMarketCategoryId());
                        standardMarketDataMessage.setDataSourceCode(dataSourceCode);
                        redisService.hSet(fistKey, key, standardMarketDataMessage);
                    }
                    redisService.hSet(fistMatchKey, standardMatchInfo.getId().toString(), standardMatchInfo.getBeginTime());
                }
            }
        }
    }

    /**
     * LS百家赔球头
     *
     * @param dataSourceCodeDB
     * @param standardMatchInfo
     * @param marketLists
     */
    private void allLsThirdFistMarket(String dataSourceCodeDB, String dataSourceCode, StandardMatchInfo standardMatchInfo, List<ThirdMarketDTO> marketLists) {
        if (DataSourceCodeEnum.LS.getCode().equals(dataSourceCodeDB)) {
            String fistMatchKey = Constant.REDIS_KEY.THIRD_FIST_MATCH;
            //百家赔三方初盘
            String fistKey = Constant.REDIS_KEY.THIRD_FIST_MARKET_HEAD + standardMatchInfo.getId();
            //需要处理的玩法分组
            Map<Long, List<ThirdMarketDTO>> marketListsMap = marketLists.stream().filter(t -> MarginCategoryConfig.THIRD_FIRST_MARKET_BALL_HEAD_CATEGORY.contains(t.getMarketCategoryId()) && Constant.SPORT_MARKET.STATUS.ACTIVE.equals(t.getStatus())).collect(Collectors.groupingBy(ThirdMarketDTO::getMarketCategoryId));
            for (Map.Entry<Long, List<ThirdMarketDTO>> entry : marketListsMap.entrySet()) {
                List<ThirdMarketDTO> thirdMarketDtos = entry.getValue();
                //计算出投注项赔率差
                thirdMarketDtos.forEach(m -> {
                    m.setOddsMetric(m.getMarketOddsList().stream().map(ThirdMarketOddsDTO::getOriginalOddsValue).reduce(0, (a, b) -> a >= b ? a - b : b - a));
                });
                //数据商状态、赔率差 升序排序
                ListUtils.sort(thirdMarketDtos, true, "status", "oddsMetric");
                ThirdMarketDTO thirdMarketNewHeadFinal = thirdMarketDtos.get(0);
                String key = "THIRD_All_" + dataSourceCode + "_" + thirdMarketNewHeadFinal.getMarketCategoryId() + "_" + thirdMarketNewHeadFinal.getMarketType();
                Object obj = redisService.hGet(fistKey, key);
                if (Objects.isNull(obj)) {
                    StandardMarketDataMessage standardMarketDataMessage = thirdConvertStandardMarket(thirdMarketNewHeadFinal);
                    if (null != standardMarketDataMessage) {
                        standardMarketDataMessage.setDataSourceCode(dataSourceCode);
                        standardMarketDataMessage.setMarketCategoryId(thirdMarketNewHeadFinal.getMarketCategoryId());
                        redisService.hSet(fistKey, key, standardMarketDataMessage);
                    }
                    redisService.hSet(fistMatchKey, standardMatchInfo.getId().toString(), standardMatchInfo.getBeginTime());
                }

            }
        }
    }

    /**
     * 赋值盘口
     *
     * @param thirdMarketDTO
     */
    private ThirdSportMarketMessage copyThirdMarketDTO(ThirdMarketDTO thirdMarketDTO) {
        ThirdSportMarketMessage thirdSportMarketMessage = new ThirdSportMarketMessage();
        thirdSportMarketMessage.setAddition1(thirdMarketDTO.getAddition1());
        thirdSportMarketMessage.setAddition2(thirdMarketDTO.getAddition2());
        thirdSportMarketMessage.setAddition3(thirdMarketDTO.getAddition3());
        thirdSportMarketMessage.setAddition4(thirdMarketDTO.getAddition4());
        thirdSportMarketMessage.setAddition5(thirdMarketDTO.getAddition5());
        thirdSportMarketMessage.setMarketType(thirdMarketDTO.getMarketType());
        thirdSportMarketMessage.setThirdMarketSourceId(thirdMarketDTO.getThirdMarketSourceId());
        thirdSportMarketMessage.setPlaceNum(thirdMarketDTO.getOfferLineId());
        thirdSportMarketMessage.setOfferLineId(thirdMarketDTO.getOfferLineId());
        thirdSportMarketMessage.setModifyTime(thirdMarketDTO.getModifyTime());
        thirdSportMarketMessage.setOddsName(thirdMarketDTO.getOddsName());
        MergeFunctionUtils.setNumberOfWinners( thirdSportMarketMessage, thirdMarketDTO.getNumberOfWinners());
        return thirdSportMarketMessage;
    }

    /**
     * 赋值盘口赔率
     *
     * @param thirdMarketOddsDTO
     */
    private ThirdSportMarketOdds copyThirdMarketOddsDTO(ThirdMarketOddsDTO thirdMarketOddsDTO) {
        ThirdSportMarketOdds thirdSportMarketOdds = new ThirdSportMarketOdds();
        thirdSportMarketOdds.setOddsType(thirdMarketOddsDTO.getOddsType());
        thirdSportMarketOdds.setThirdOddsFieldSourceId(thirdMarketOddsDTO.getThirdOddsFieldSourceId());
        thirdSportMarketOdds.setAddition1(thirdMarketOddsDTO.getAddition1());
        thirdSportMarketOdds.setOddsValue(thirdMarketOddsDTO.getOddsValue());
        thirdSportMarketOdds.setOriginalOddsValue(thirdMarketOddsDTO.getOriginalOddsValue());
        thirdSportMarketOdds.setActive(thirdMarketOddsDTO.getActive());
        thirdSportMarketOdds.setModifyTime(thirdMarketOddsDTO.getModifyTime());
        thirdSportMarketOdds.setOrderOdds(thirdMarketOddsDTO.getOrderOdds());
        return thirdSportMarketOdds;
    }
}
