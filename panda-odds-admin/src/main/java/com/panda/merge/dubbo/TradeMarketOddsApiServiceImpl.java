package com.panda.merge.dubbo;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.merge.api.ITradeMarketOddsApi;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.IdWorker;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.StandardMatchMarketOddsLinkageProcessor;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.dto.message.StandardMatchMarketMessage;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.rocketmq.producer.StandardMarketOddsProducer;
import com.panda.merge.service.*;
import com.panda.merge.util.CategoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/21 <br>
 * @see com.panda.merge.dubbo <br>
 */
@Slf4j
@Component
@DubboService
public class TradeMarketOddsApiServiceImpl extends BaseProcessor implements ITradeMarketOddsApi {
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    @Lazy
    @Autowired
    private StandardMarketOddsProducer standardMarketOddsProducer;

    @Autowired
    private MarketCategorySellService marketCategorySellService;

    @Autowired
    private StandardSportMarketNewService standardSportMarketService;

    @Autowired
    private StandardSportMarketOddsNewService standardSportMarketOddsService;

    @Autowired
    private ConfigMarketTradeItemService configMarketTradeItemService;

    @Autowired
    private StandardSportMarketMService standardSportMarketMService;

    @Autowired
    private StandardSportMarketCategoryService standardSportMarketCategoryService;

    @Autowired
    private StandardOutrightMarketService standardOutrightMarketService;

    @Autowired
    private StandardMatchMarketOddsLinkageProcessor standardMatchMarketOddsLinkageProcessor;

    @Override
    public Response putTradeMarketOdds(Request<StandardMatchMarketDTO> request) {
//            validateLinkId("putTradeMarketOdds", request);
            log.info("::{}:: putTradeMarketOdds入参:{}", request.getLinkId(), JSON.toJSONString(request));
            List<StandardMarketDTO> standardMarketDTOList = request.getData().getMarketList();
            Long standardMatchInfoId = request.getData().getStandardMatchInfoId();
            //查询标准赛事是否存在
            boolean isOutright = null != request.getData().getMatchType() && request.getData().getMatchType() == 1;
            //获取标准赛事
            StandardMatchInfoDetail standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(isOutright, standardMatchInfoId);
            if (standardMatchInfo == null) {
                log.info("::{}::putTradeMarketOdds error:{}", request.getLinkId(), "标准赛事不存在，标准赛事id：" + standardMatchInfoId);
                return Response.failed("标准赛事不存在");
            }
            log.info("::{}:: putTradeMarketOdds,标准赛事:{}", request.getLinkId(), JSON.toJSONString(standardMatchInfo));
            //需要发送的盘口
            List<StandardMarketDTO> validStandardMarketDTOList = new ArrayList<>();
            //本次改变的玩法
            Set<Long> marketCategoryIdSet = new HashSet<>();
            //转为可以发送的dto
            List<StandardMarketMessage> sendStandardMarketMessageList = new ArrayList<>();
            List<StandardMarketMessage> scoreSettleSpMarketList = new ArrayList<>();
            if(isOutright){
                log.info("::{}::champion的putTradeMarketOdds入参:{}", request.getLinkId(), JSON.toJSONString(request));
                // 为统一赛程后台与融合冠军赔率,需要对调赔数据做入库操作
                syncChampionMarket(request);
                processOutrightTradeMarketOdds(request, standardMarketDTOList, standardMatchInfo, sendStandardMarketMessageList);
                StandardMatchMarketMessage standardMatchMarketMessage = standardMarketOddsProducer.standardMarketOddsAsyncSend(request.getLinkId(), standardMatchInfo, sendStandardMarketMessageList, request.getDataSourceTime(),false);
                //刷新缓存
                //if (DataSourceCodeEnum.PA.name().equals(standardMatchInfo.getDataSourceCode())) {}
                //toUpdateCacheOfMarket(request.getLinkId(), sendStandardMarketMessageList, standardMatchInfo);
                return Response.success(standardMatchMarketMessage);
            }else{
                //修改缓存开赛时间
                String matchBeginStr = Constant.REDIS_KEY.RONGHE_THIRD_PER_MARKET;
                String updatedKey = redisService.genNewHashKey(matchBeginStr, standardMatchInfo.getId().toString(), ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
                redisService.hSet(updatedKey, standardMatchInfo.getId().toString(), standardMatchInfo.getBeginTime(),Integer.MAX_VALUE);
            }
             //查询开售数据源
            Integer marketType = standardMarketDTOList.get(0).getMarketType();
            String categoryRedisKey = Constant.REDIS_KEY.RONGHE_MARKET_CATEGORY_SELL + standardMatchInfo.getId() + "_" + marketType;
            Map<String, String> oldStringHashMap = redisService.hGetAll(categoryRedisKey);

           //子玩法支持切换的玩法
            List<Long> switchModeChildCategory = MarginCategoryConfig.SWITCH_MODE_CHILD_CATEGORY.get(standardMatchInfo.getSportId());
            List<Long> modeChildCategory = CollectionUtils.isEmpty(switchModeChildCategory)? new ArrayList<>():switchModeChildCategory;
            //对所有盘口按照玩法分类，风控M模式可能存在一批数据有多个玩法
            //不支持切换的子玩法的总玩法
            Map<Long, List<StandardMarketDTO>>  standardMarketMap = standardMarketDTOList.stream().filter(s->!modeChildCategory.contains(s.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDTO::getMarketCategoryId));
            standardMarketOddsProcessor(request, standardMatchInfoId, validStandardMarketDTOList, marketCategoryIdSet, standardMarketMap);
            //支持切换子玩法的总玩法
            Map<Long, List<StandardMarketDTO>>  standardMarketChildCategoryMap = standardMarketDTOList.stream().filter(s->modeChildCategory.contains(s.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketDTO::getChildStandardCategoryId));
            log.info("::{}::standardMarketChildCategoryMap:{}",request.getLinkId(),JSONObject.toJSONString(standardMarketChildCategoryMap));
            standardMarketOddsProcessor(request, standardMatchInfoId, validStandardMarketDTOList, marketCategoryIdSet, standardMarketChildCategoryMap);
            if (CollectionUtils.isEmpty(validStandardMarketDTOList)) {
                log.info("::{}::putTradeMarketOdds error:{}", request.getLinkId(), "没有需求下发的盘口列表");
                return Response.failed("没有需要下发的盘口列表");
            }
            //获取该赛事的所有盘口位置最大最小值,一次获取比循环获取快
            Map<String, ConfigMarketTradeItem> configMarketTradeItemMap = configMarketTradeItemService.getItemByMatchAndCategorys(standardMatchInfo.getId(),marketCategoryIdSet);
            log.info("::{}:: putTradeMarketOdds, 开始批量获取标准球种玩法信息", request.getLinkId());
            // 批量获取赛事开售信息
            List<Pair<Long, Long>> standardCategories = validStandardMarketDTOList.stream()
                    .map(t -> Pair.of(t.getMarketCategoryId(), standardMatchInfo.getSportId()))
                    .collect(Collectors.toList());
            List<StandardSportMarketCategory> standardSportMarketCategories = standardSportMarketCategoryService.getItemsByStandardCategories(standardCategories);
            Map<String, StandardSportMarketCategory> standardSportMarketCategoryMap = standardSportMarketCategories.stream()
                    .collect(Collectors.toMap(t -> t.getMarketCategoryId() + "-" + t.getSportId(),
                            Function.identity(), (v1, v2) -> v1));
            log.info("::{}:: putTradeMarketOdds, 批量获取标准球种玩法信息结束，最终获取记录数:{}", request.getLinkId(), standardSportMarketCategoryMap.size());
            validStandardMarketDTOList.forEach(standardMarketDTO -> {
                StandardSportMarket standardSportMarket = new StandardSportMarket();
                List<StandardSportMarketOddsDetail> standardSportMarketOddsList = new ArrayList<>();
                BeanUtils.copyProperties(standardMarketDTO, standardSportMarket);
                standardSportMarket.setStandardMatchInfoId(standardMatchInfoId);
                standardSportMarket.setRelationMarketId(standardSportMarketService.getRelationMarketId(request.getLinkId(), standardSportMarket));
                if (!CollectionUtils.isEmpty(standardMarketDTO.getMarketOddsList()))
                {
                    for (StandardMarketOddsDTO standardMarketOddsDTO : standardMarketDTO.getMarketOddsList()) {
                        StandardSportMarketOddsDetail standardSportMarketOdds = new StandardSportMarketOddsDetail();
                        BeanUtils.copyProperties(standardMarketOddsDTO, standardSportMarketOdds);
                        standardSportMarketOdds.setId(IdWorker.getId());
                        standardSportMarketOdds.setRelationMarketId(standardSportMarket.getRelationMarketId());
                        standardSportMarketOdds.setPaOddsValue(standardMarketOddsDTO.getOddsValue());
                        standardSportMarketOdds.setThirdOddsFieldSourceId(standardSportMarketOdds.getId().toString());
                        standardSportMarketOdds.setRelationMarketOddsId(standardSportMarketOddsService.getRelationMarketOddsId(standardSportMarketOdds,standardMarketDTO.getMarketCategoryId()));
                        standardSportMarketOddsList.add(standardSportMarketOdds);
                    }
                }
                log.info("::{}::盘口最大最小值配置:{},赔率：{}", request.getLinkId(), JSON.toJSONString(configMarketTradeItemMap),JSONObject.toJSONString(standardSportMarket));
                //数据组装及转换(按relationMarketId、relationMarketOddsId下发盘口、赔率)
                StandardMarketMessage standardMarketMessage = convertMarketDataToMessage(standardSportMarket, standardSportMarketOddsList,request.getDataSourceTime());
                standardMarketMessage.setChildMarketCategoryId(CategoryUtils.getChildCategoryId(request.getLinkId(),standardMarketMessage.getMarketCategoryId(),
                        standardMarketMessage.getAddition1(),standardMarketMessage.getAddition2(),standardMarketMessage.getAddition3(),
                        standardMarketMessage.getAddition4(),standardMarketMessage.getAddition5(),String.valueOf(request.getData().getStandardMatchInfoId())));
                standardMarketMessage.setMarketHeadGap(standardMarketDTO.getMarketHeadGap());
                standardMarketMessage.setMarketSource(standardMarketDTO.getMarketSource());
                standardMarketMessage.setPlaceNumStatusDisplay(standardMarketDTO.getPlaceNumStatusDisplay());
                standardMarketMessage.setRiskStatus(standardMarketDTO.getStatus());
                String dataSourceCodeSold = oldStringHashMap.get(standardMarketMessage.getMarketCategoryId().toString());
                /*if (!StringUtils.isEmpty(dataSourceCodeSold)) {
                    String key = Constant.REDIS_KEY.THIRD_MATCH_WITCH_DATA_SOURCE_KEY + standardMatchInfo.getId() + "_" + dataSourceCodeSold;
                    Object o = redisService.get(key);
                    standardMarketMessage.setInternalDataSourceCode(null == o ? null : o.toString());
                }*/
                log.info("::{}::转换后赔率：{}", request.getLinkId(),JSONObject.toJSONString(standardMarketMessage));
                if (null != standardMarketMessage.getPlaceNum()) {
                    //设置盘口位置id
                    standardMarketMessage.setPlaceNumId(standardSportMarket.getStandardMatchInfoId() + "_" + standardSportMarket.getMarketCategoryId() + "_" + standardMarketMessage.getChildMarketCategoryId() + "_"  + standardSportMarket.getPlaceNum());
                }
                //手动模式不改变风控传的盘口位置操盘状态
                if (null != standardMarketDTO.getPlaceNumStatus()) {
                    standardMarketMessage.setPlaceNumStatus(standardMarketDTO.getPlaceNumStatus());
                } else {
                    standardMarketMessage.setPlaceNumStatus(0);
                }
                
              //单球种玩法关闭
                StandardSportMarketCategory standardSportMarketCategory = standardSportMarketCategoryMap.get(standardMarketMessage.getMarketCategoryId()+"-"+standardMatchInfo.getSportId());
                log.info("::{}::判断玩法是否开启, sportId:{},categoryId:{},standardSportMarketCategory{}",
                		request.getLinkId(), standardMatchInfo.getSportId(),standardMarketMessage.getMarketCategoryId(),standardSportMarketCategory);
                if (standardSportMarketCategory == null || Constant.SPORT_MARKET_CATEGORY.STATUS.INVALID.equals(standardSportMarketCategory.getStatus()))
                {
                	log.info("::{}::putTradeMarketOdds 玩法状态为关闭，关闭赛事盘口，标准赛事id:{},赛种id:{},玩法id:{}", request.getLinkId(), standardMatchInfo.getId(), standardMatchInfo.getSportId(), standardMarketMessage.getMarketCategoryId());
                    standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                }
                standardMarketMessage.setEndEdStatus(0);
                //获取操盘的表现状态
                Integer status = standardMarketMessage.getStatus();
                //盘口赔率合法性判断
                if (Constant.SPORT_MARKET.STATUS.ACTIVE.equals(status) || Constant.SPORT_MARKET.STATUS.SUSPENDED.equals(status)
                        || Constant.SPORT_MARKET.STATUS.ENDED.equals(status))
                {
                    standardMarketMessage.setPaStatus(0);
                    thirdMatchMarketProcessor.checkMarketOddsValid(request.getLinkId(), standardMatchInfoId, standardMarketMessage,
                            configMarketTradeItemMap,standardMatchInfo.getSportId(), standardMatchInfo);
                    //球头校验
                    thirdMatchMarketProcessor.ballVerify(request.getLinkId(), marketType, standardMatchInfo, standardMarketMessage);
                    int lastStatus = Math.max(standardMarketMessage.getPaStatus(),standardMarketDTO.getStatus());
                    lastStatus = Math.max(lastStatus,standardMarketMessage.getPlaceNumStatus());
                    standardMarketMessage.setStatus(lastStatus);
                    if (Constant.SPORT_MARKET.STATUS.ENDED.equals(standardMarketMessage.getStatus())){
                        standardMarketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.SUSPENDED);
                        standardMarketMessage.setEndEdStatus(1);
                        if (standardMarketDTO.getMarketOddsList()==null
                                ||standardMarketDTO.getMarketOddsList().isEmpty()){
                            standardMarketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                            standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        }
                        log.info("::{}::putTradeMarketOdds 盘口最终最终为收盘下发封盘，标准赛事id:{},赛种id:{},玩法id:{}", request.getLinkId(), standardMatchInfo.getId(), standardMatchInfo.getSportId(), standardMarketMessage.getMarketCategoryId());
                    }
                }
                //standardMarketMessage.setPlaceNumStatus(standardMarketDTO.getPlaceNumStatus());
                thirdMatchMarketProcessor.processOddsValueDecimals(request.getLinkId(), standardMarketMessage, standardMatchInfo);
                sendStandardMarketMessageList.add(standardMarketMessage);
                if ((standardMarketMessage.getMarketCategoryId() >= 1109000L && standardMarketMessage.getMarketCategoryId() <= 1109999L)
                        || standardMarketMessage.getMarketCategoryId() >= 3109000L && standardMarketMessage.getMarketCategoryId() <= 3109999L ) {
                    scoreSettleSpMarketList.add(standardMarketMessage);
                }
            });
            //更新最后下发赔率缓存，防止addStandardMarketA用旧缓存覆盖修改后的赔率
        log.info("::{}::thirdMatchMarketProcessor.saveTheLastMarketOddsToReids()===开始执行=== 更新最后下发赔率缓存，防止addStandardMarketA用旧缓存覆盖修改后的赔率，标准赛事id:{},赛种id:{},玩法id:{}", request.getLinkId(), standardMatchInfo.getId(), standardMatchInfo.getSportId(), marketCategoryIdSet);
        thirdMatchMarketProcessor.saveTheLastMarketOddsToReids(request.getLinkId(), standardMatchInfo, marketCategoryIdSet, sendStandardMarketMessageList, request.getDataSourceTime(), false);
        log.info("::{}::thirdMatchMarketProcessor.saveTheLastMarketOddsToReids===执行结束=== 更新最后下发赔率缓存，防止addStandardMarketA用旧缓存覆盖修改后的赔率，标准赛事id:{},赛种id:{},玩法id:{}", request.getLinkId(), standardMatchInfo.getId(), standardMatchInfo.getSportId(), marketCategoryIdSet);

        //融合A模式子玩法下发
            addStandardMarketA(request.getLinkId(), standardMatchInfo, sendStandardMarketMessageList, modeChildCategory);
            //-------------赔率下发-----------------
            //下发赔率
           standardMarketOddsProducer.standardMarketOddsAsyncSendByRisk(request.getLinkId(), standardMatchInfo, sendStandardMarketMessageList, request.getDataSourceTime(),false);
           //下发特殊玩法给结算
           standardMarketOddsProducer.scoreSettleSpMarketAsyncSend(request.getLinkId(), standardMatchInfo, scoreSettleSpMarketList, request.getDataSourceTime());
           //standardMarketMessageMDB(request.getLinkId(), standardMatchInfo, sendStandardMarketMessageList);
           return Response.success();
    }

    /**
     * 玩法配置校验
     * 盘口数最大最小
     *
     * @param request
     * @param standardMatchInfoId
     * @param validStandardMarketDTOList
     * @param marketCategoryIdSet
     * @param standardMarketMap
     */
    private void standardMarketOddsProcessor(Request<StandardMatchMarketDTO> request, Long standardMatchInfoId, List<StandardMarketDTO> validStandardMarketDTOList, Set<Long> marketCategoryIdSet, Map<Long, List<StandardMarketDTO>> standardMarketMap) {
        if (MapUtils.isEmpty(standardMarketMap)) {
            return;
        }
        for (Map.Entry<Long, List<StandardMarketDTO>> entry : standardMarketMap.entrySet()) {
            List<StandardMarketDTO> standardMarketDTOs = entry.getValue();
            Long marketCategoryId = entry.getKey();
            //处理最大盘口数量设置，多余的盘口设置为DEACTIVATED
            processConfigMarketDisplayTrade(request.getLinkId(), standardMatchInfoId, standardMarketDTOs);
            //查询操盘配置
            ConfigTradeType configTradeType = thirdMatchMarketProcessor.isSendMarketOddsByTradeType(request.getLinkId(), standardMatchInfoId, marketCategoryId);
            log.info("::{}::玩法:{},查询操盘配置:{}", request.getLinkId(), marketCategoryId, JSONObject.toJSONString(configTradeType));
            //操盘方式
            Integer tradeType = 0;
            if (null != configTradeType) {
                tradeType = configTradeType.getTradeType();
            }
            //if (!Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeType)) {
                for (StandardMarketDTO standardMarketDTO : standardMarketDTOs) {
                    if (null == standardMarketDTO.getChildStandardCategoryId())
                    {
                        standardMarketDTO.setChildStandardCategoryId(CategoryUtils.getChildCategoryId(request.getLinkId(),standardMarketDTO.getMarketCategoryId(),standardMarketDTO.getAddition1(),
                                standardMarketDTO.getAddition2(),standardMarketDTO.getAddition3(),standardMarketDTO.getAddition4(),standardMarketDTO.getAddition5(),String.valueOf(request.getData().getStandardMatchInfoId())));
                    }
                    if (!Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(standardMarketDTO.getTradeType())) {
                        standardMarketDTO.setMarketSource(0);
                    }
                    if (standardMarketDTO.getTradeType() == null){
                        standardMarketDTO.setTradeType(tradeType);
                    }
                    //standardMarketDTO.setTradeType(tradeType);
                }
                validStandardMarketDTOList.addAll(standardMarketDTOs);
                marketCategoryIdSet.add(standardMarketDTOs.get(0).getMarketCategoryId());
                marketCategoryIdSet.add(marketCategoryId);
            //}
        }
    }

    public void processOutrightTradeMarketOdds(Request<StandardMatchMarketDTO> request, List<StandardMarketDTO> standardMarketDTOList,
                                                StandardMatchInfoDetail standardMatchInfo, List<StandardMarketMessage> sendStandardMarketMessageList) {
        String linkId = request.getLinkId();
        //兼容冠军盘口的排序值(数据较少)
        List<StandardOutrightMarket> outrightMarketList = standardOutrightMarketService.selectOutrightMarketSellList(standardMatchInfo.getId());
        Map<Long, Integer> marketOrderMap = Maps.newConcurrentMap();
        if (!CollectionUtils.isEmpty(outrightMarketList)) {
            marketOrderMap = outrightMarketList.stream().collect(Collectors.toMap(StandardOutrightMarket::getId, StandardOutrightMarket::getMarketOrderNumber));
        }
        log.info("::{}::processOutrightTradeMarketOdds查询的冠军盘口排序值:{}", linkId, JSON.toJSONString(marketOrderMap));
        for (StandardMarketDTO standardMarketDTO: standardMarketDTOList) {
            StandardSportMarket standardSportMarket = new StandardSportMarket();
            List<StandardSportMarketOddsDetail> standardSportMarketOddsList = new ArrayList<>();
            BeanUtils.copyProperties(standardMarketDTO, standardSportMarket);
            standardSportMarket.setStandardMatchInfoId(standardMatchInfo.getId());
            standardSportMarket.setRelationMarketId(Long.valueOf(standardMarketDTO.getId()));
            if (!CollectionUtils.isEmpty(standardMarketDTO.getMarketOddsList()))
            {
                for (StandardMarketOddsDTO standardMarketOddsDTO : standardMarketDTO.getMarketOddsList()) {
                    StandardSportMarketOddsDetail standardSportMarketOdds = new StandardSportMarketOddsDetail();
                    BeanUtils.copyProperties(standardMarketOddsDTO, standardSportMarketOdds);
                    standardSportMarketOdds.setId(IdWorker.getId());
                    standardSportMarketOdds.setRelationMarketId(standardSportMarket.getRelationMarketId());
                    standardSportMarketOdds.setPaOddsValue(processOddsValueDecimals( linkId, standardMarketOddsDTO.getOddsValue()));
                    standardSportMarketOdds.setRelationMarketOddsId(Long.valueOf(standardMarketOddsDTO.getId()));
                    standardSportMarketOddsList.add(standardSportMarketOdds);
                }
            }
            //数据组装及转换(按relationMarketId、relationMarketOddsId下发盘口、赔率)
            StandardMarketMessage standardMarketMessage = convertMarketDataToMessage(standardSportMarket, standardSportMarketOddsList,request.getDataSourceTime());
            standardMarketMessage.setPaStatus(standardMarketMessage.getStatus());
            if (marketOrderMap.containsKey(standardMarketMessage.getId())) {
                standardMarketMessage.setOrderNo(marketOrderMap.get( standardMarketMessage.getId()) );
            }
            //赔率合法性校验
            if (null != standardMarketMessage.getMarketOddsList()) {
                Integer activeOddsNum = 0;
                for (StandardMarketOddsMessage message : standardMarketMessage.getMarketOddsList()) {
                    if(message.getPaOddsValue() == null || message.getPaOddsValue() <= 100000){
                        //投注项赔率不合法时，只封当前投注项
                        message.setActive(Constant.SPORT_MARKET.ODDS_STATUS.SUSPENDED);
                        message.setPaActiveReason("投注项赔率不合法，赔率小于1，投注项封盘");
                        log.info("::{}::processOutrightTradeMarketOdds赔率合法性校验，标准赛事id:{},统一盘口id:{},三方盘口源id:{},pa赔率:{}",
                                linkId, standardMatchInfo.getId(), standardMarketMessage.getId(), standardMarketMessage.getThirdMarketSourceId(), message.getPaOddsValue());
                    }
                    Integer activeStatus = message.getActive();
                    if ( Constant.SPORT_MARKET.ODDS_STATUS.ACTIVE.equals(activeStatus) ) {
                        activeOddsNum  += 1;
                    }
                }
                if ( 0 == activeOddsNum ) {
                    standardMarketMessage.setPaStatusReason("无开售投注项，盘口封盘");
                    standardMarketMessage.setPaStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    log.info("::{}::无开售投注项，盘口封盘, 盘口id:{}", linkId, standardMarketMessage.getId() );
                }
            }
            thirdMatchMarketProcessor.processOddsValueDecimals( linkId, standardMarketMessage, standardMatchInfo);
            if ( null == standardMarketMessage.getNumberOfWinners() || standardMarketMessage.getNumberOfWinners() < 1 ) {
                standardMarketMessage.setNumberOfWinners(1);
            }
            sendStandardMarketMessageList.add(standardMarketMessage);
            log.info("::{}::processOutrightTradeMarketOdds组装参数:{}", linkId, JSON.toJSONString(sendStandardMarketMessageList));
        }
        // 刷新赔率
        toUpdateCacheOfMarket(linkId, sendStandardMarketMessageList, standardMatchInfo);
        standardMatchMarketOddsLinkageProcessor.championMarketOddsMainLinkage(linkId, standardMatchInfo, sendStandardMarketMessageList);
    }

    /**
     * 刷新盘口的缓存数据
     */
    public void toUpdateCacheOfMarket(String linkId, List<StandardMarketMessage> sendStandardMarketMessageList, StandardMatchInfoDetail standardMatchInfo)
    {
        for (StandardMarketMessage standardMarketMessage : sendStandardMarketMessageList) {
            log.info("::{}:: putTradeMarketOdds toUpdateCacheOfMarket,StandardMarketMessage:{}", linkId, JSON.toJSONString(standardMarketMessage));
            StandardSportMarket standardSportMarket = new StandardSportMarket();
            BeanUtils.copyProperties(standardMarketMessage, standardSportMarket);
            //StandardSportMarket standardSportMarket = standardSportMarketMap.get(standardMarketMessage.getId());
            log.info("::{}:: putTradeMarketOdds toUpdateCacheOfMarket,标准盘口:{}", linkId, JSON.toJSONString(standardSportMarket));
            List<StandardSportMarketOdds> standardSportMarketOddsList = Lists.newLinkedList();
            for (StandardMarketOddsMessage standardMarketOddsMessage : standardMarketMessage.getMarketOddsList()) {
                StandardSportMarketOdds standardSportMarketOdds = new StandardSportMarketOdds();
                BeanUtils.copyProperties(standardMarketOddsMessage, standardSportMarketOdds);
                standardSportMarketOddsList.add(standardSportMarketOdds);
            }
            StandardMarketDataMessage standardMarketDataMessage = thirdMatchMarketProcessor.convertToStandardMarketDataMessage(
                    standardSportMarketOddsList, standardSportMarket,TimeUtils.millsSecondsEast8ZoneGmt()-10*1000);
            standardMarketDataMessage.setRelationMarketId(standardMarketMessage.getId());
            standardMarketDataMessage.setStandardMatchInfoId(standardMatchInfo.getId());
            log.info("::{}:: putTradeMarketOdds toUpdateCacheOfMarket,standardMarketDataMessage:{}", linkId, JSON.toJSONString(standardMarketDataMessage) );
            String marketKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + standardMatchInfo.getDataSourceCode();
            log.info("::{}:: putTradeMarketOdds toUpdateCacheOfMarket,marketKey:{}", linkId, marketKey);
            redisService.hSet(marketKey, standardMarketDataMessage.getRelationMarketId().toString(), standardMarketDataMessage, RedisConfig.REDIS_YEAR_TIME);
        }
    }



    /**
     * 数据组装及转换(按relationMarketId、relationMarketOddsId下发盘口、赔率)
     * @param standardSportMarket
     * @param standardSportMarketOddsList
     * @return
     */
    private StandardMarketMessage convertMarketDataToMessage(StandardSportMarket standardSportMarket, List<StandardSportMarketOddsDetail> standardSportMarketOddsList,Long dataSourceTime) {
        StandardMarketMessage standardMarketMessage = new StandardMarketMessage();
        BeanUtils.copyProperties(standardSportMarket, standardMarketMessage);
        standardMarketMessage.setId(standardSportMarket.getRelationMarketId());
        standardMarketMessage.setModifyTime(dataSourceTime);
        List<StandardMarketOddsMessage> standardMarketOddsMessageList = new ArrayList<>();
        for (StandardSportMarketOdds standardSportMarketOdds : standardSportMarketOddsList) {
            StandardMarketOddsMessage standardMarketOddsMessage = new StandardMarketOddsMessage();
            BeanUtils.copyProperties(standardSportMarketOdds, standardMarketOddsMessage);
            standardMarketOddsMessage.setMarketId(standardSportMarketOdds.getRelationMarketId());
            standardMarketOddsMessage.setId(standardSportMarketOdds.getRelationMarketOddsId());
            standardMarketOddsMessage.setModifyTime(dataSourceTime);
            standardMarketOddsMessageList.add(standardMarketOddsMessage);
        }
        standardMarketMessage.setMarketOddsList(standardMarketOddsMessageList);
        return standardMarketMessage;
    }

    /**
     * 处理最大盘口数量设置，多余的盘口设置为DEACTIVATED
     * @param standardMarketDTOs
     */
    private void processConfigMarketDisplayTrade(String linkId, Long matchId, List<StandardMarketDTO> standardMarketDTOs) {
        //盘口类型
        //超出最大盘口数的盘口设置为关盘
        for(StandardMarketDTO standardMarket : standardMarketDTOs){
            //查询赛事玩法下的盘口设置
            int marketCount = 3;
            MarketCategorySell marketCategorySell = marketCategorySellService.getItem(linkId, matchId, standardMarketDTOs.get(0).getMarketType(), standardMarket.getMarketCategoryId());
            if(null != marketCategorySell){
                marketCount = marketCategorySell.getMarketCount() == null ? 3 : marketCategorySell.getMarketCount();
            }
            log.info("::{}::最大盘口数数据:{}", linkId, JSONObject.toJSONString(marketCategorySell));
            if(standardMarket.getPlaceNum() > marketCount){
                standardMarket.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                standardMarket.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
            }
        }
    }


    /**
     * M模式标准盘口入库
     *
     * @param linkId
     * @param standardMatchInfo
     * @param standardMarketMessageList
     */
    public void standardMarketMessageMDB(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageList) {
        if (CollectionUtils.isEmpty(standardMarketMessageList) && !standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code) &&
        		!standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Badminton.code) && !standardMatchInfo.getSportId().equals(StandardSportTypeEnum.Soccer.code)) {
            return;
        }
        List<StandardSportMarketM> insertList = new ArrayList<>();
        List<StandardSportMarketM> updateBatch = new ArrayList<>();
        for (StandardMarketMessage standardMarketMessage : standardMarketMessageList) {
//            if (!MarginCategoryConfig.STANDARD_MARKET_M_CATEGORY.contains(standardMarketMessage.getMarketCategoryId())) {
//                continue;
//            }
            StandardSportMarketM marketMessageM = new StandardSportMarketM();
            BeanUtils.copyProperties(standardMarketMessage, marketMessageM);
            marketMessageM.setStandardMatchInfoId(standardMatchInfo.getId());
            marketMessageM.setRelationMarketId(standardMarketMessage.getId());
            marketMessageM.setLinkId(linkId);
            StandardSportMarketM standardSportMarketM = standardSportMarketMService.getItem(standardMatchInfo.getId(), marketMessageM.getRelationMarketId());
            if (null == standardSportMarketM) {
                marketMessageM.setId(IdWorker.getId());
                marketMessageM.setCreateTime(System.currentTimeMillis());
                insertList.add(marketMessageM);
            } else {
                marketMessageM.setModifyTime(System.currentTimeMillis());
                marketMessageM.setId(standardSportMarketM.getId());
                updateBatch.add(marketMessageM);
            }
        }
        if (!CollectionUtils.isEmpty(insertList)) {
            log.info("::{}::标准赛事ID:{},M模式标准盘口入库-添加条数:{},数据:{}", linkId, standardMatchInfo.getId(), insertList.size(),JSONObject.toJSONString(insertList));
            standardSportMarketMService.insertList(linkId, insertList);
        }
        if (!CollectionUtils.isEmpty(updateBatch)) {
            log.info("::{}::标准赛事ID:{},M模式标准盘口入库-修改条数:{}", linkId, standardMatchInfo.getId(), updateBatch.size());
            standardSportMarketMService.updateBatch(linkId, updateBatch);
        }
    }


    public void syncChampionMarket(Request<StandardMatchMarketDTO> request)
    {
        String linkId = request.getLinkId();
        List<StandardMarketDTO> standardMarketDTOList = request.getData().getMarketList();
        if (CollectionUtils.isEmpty(standardMarketDTOList))
        {
            return;
        }
        for (StandardMarketDTO marketDTO : standardMarketDTOList)
        {
            String marketId = marketDTO.getId();
            List<StandardMarketOddsDTO> marketOddsList = marketDTO.getMarketOddsList();
            if (!StringUtils.isEmpty(marketId) && !CollectionUtils.isEmpty(marketOddsList) )
            {
                Long relationMarketId = Long.parseLong(marketId);
                StandardSportMarket standardSportMarket = standardSportMarketService.getMarketByRelationId(relationMarketId);
                if ( !Objects.isNull(standardSportMarket) )
                {
                    List<StandardSportMarketOdds> dbMarketOddsList = standardSportMarketOddsService.getItemList(standardSportMarket.getId());
                    log.info("::{}::syncChampionMarket的数据库oddsList:{}", linkId, JSON.toJSONString(dbMarketOddsList));
                    if ( !CollectionUtils.isEmpty(dbMarketOddsList) )
                    {
                        Map<String, StandardSportMarketOdds> oddsMap = dbMarketOddsList.stream().collect(
                                Collectors.toMap(StandardSportMarketOdds::getThirdOddsFieldSourceId, Function.identity()));
                        for ( StandardMarketOddsDTO marketOddsDTO : marketOddsList)
                        {
                            if ( oddsMap.size() >0 && !StringUtils.isEmpty(marketOddsDTO.getThirdOddsFieldSourceId())
                                    && oddsMap.containsKey(marketOddsDTO.getThirdOddsFieldSourceId()) )
                            {
                                StandardSportMarketOdds sportMarketOdds = oddsMap.get(marketOddsDTO.getThirdOddsFieldSourceId());
                                if (!sportMarketOdds.getOddsValue().equals(marketOddsDTO.getOddsValue()))
                                {
                                    sportMarketOdds.setOddsValue(marketOddsDTO.getOddsValue());
                                    sportMarketOdds.setPaOddsValue(marketOddsDTO.getOddsValue());
                                    sportMarketOdds.setOriginalOddsValue(marketOddsDTO.getOriginalOddsValue());
                                    sportMarketOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
                                    standardSportMarketOddsService.updateByPrimaryKeySelective(sportMarketOdds);
                                    log.info("::{}::syncChampionMarket更新投注项:{}", linkId, JSON.toJSONString(sportMarketOdds));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 融合A模式子玩法下发
     * 找出总玩法下 存在a模式的子玩法 ，添加到M模式下发集合
     * @param linkId
     * @param standardMatchInfo
     * @param sendStandardMarketMessageList 最终下发数据
     * @param modeChildCategory 支持总玩法支持子玩法
     */
    private void addStandardMarketA(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> sendStandardMarketMessageList,
                                    List<Long> modeChildCategory) {
        try {
            if (CollectionUtils.isEmpty(modeChildCategory)) {
                return;
            }
            //需要处理的玩法
            Map<Long, List<StandardMarketMessage>> standardMarketMap = sendStandardMarketMessageList.stream().filter(s -> modeChildCategory.contains(s.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketMessage::getChildMarketCategoryId));
            if (MapUtils.isEmpty(standardMarketMap)) {
                return;
            }
            //总玩法获取A模式盘口，在根据子玩法获取A模式子玩法
            Set<Long> marketCategoryId = new HashSet<>();
            List<StandardMarketMessage> add = new ArrayList<>();
            List<StandardMarketMessage> delete = new ArrayList<>();
            //M模式盘口存入redis
            for (Map.Entry<Long, List<StandardMarketMessage>> entry : standardMarketMap.entrySet()) {
                List<StandardMarketMessage> standardMarketDTOs = entry.getValue();
                Long childMarketCategoryId = entry.getKey();
                marketCategoryId.add(standardMarketDTOs.get(0).getMarketCategoryId());
                //需要先把M模式的盘口全部存入到缓存中，A模式下发需要带出M模式盘口
                String standardMarketMKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_CHILD_MARKET_M_CATEGORY + standardMatchInfo.getId());
                redisService.hSet(standardMarketMKey, childMarketCategoryId.toString(), standardMarketDTOs, marketCacheTime(standardMatchInfo.getBeginTime()));
            }
            for (Long categoryId : marketCategoryId) {
                //获取A上一次下发的最新盘口
                String lastMarketOddsKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS + standardMatchInfo.getId());
                //得到A模式最新盘口找到子玩法ID盘口，加入到M模式下发集合中
                List<StandardMarketMessage> lastStandardMarketMessages = (List<StandardMarketMessage>) redisService.hGet(lastMarketOddsKey, String.valueOf(categoryId));
                if (CollectionUtils.isEmpty(lastStandardMarketMessages)) {
                    log.info("::{}::addStandardMarketA，A模式:{},盘口不存在不处理", linkId, categoryId);
                    continue;
                }
                Map<Long, List<StandardMarketMessage>> lastStandardMarketMessageMap = lastStandardMarketMessages.stream().collect(Collectors.groupingBy(StandardMarketMessage::getChildMarketCategoryId));
                for (Map.Entry<Long, List<StandardMarketMessage>> listEntry : lastStandardMarketMessageMap.entrySet()) {
                    Long childMarketCategoryIdLast = listEntry.getKey();
                    List<StandardMarketMessage> standardMarketDTOlast = listEntry.getValue();
                    ConfigTradeType configTradeType = thirdMatchMarketProcessor.isSendMarketOddsByTradeType(linkId, standardMatchInfo.getId(), childMarketCategoryIdLast);
                    //查询子玩法是不是A模式如果是放入到M模式集合下发
                    Integer tradeTypeDB = 0;
                    if (null != configTradeType) {
                        tradeTypeDB = configTradeType.getTradeType();
                    }
                    if (Constant.TRADE_MARKET_CONFIG.TRADE_TYPE.AUTO.equals(tradeTypeDB)) {
                        add.addAll(standardMarketDTOlast);
                        List<StandardMarketMessage> standardMarketMessagesDel = standardMarketMap.get(childMarketCategoryIdLast);
                        if (!CollectionUtils.isEmpty(standardMarketMessagesDel)) {
                            delete.addAll(standardMarketMessagesDel);
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(add)) {
                sendStandardMarketMessageList.removeAll(delete);
                sendStandardMarketMessageList.addAll(add);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
