package com.panda.merge.rocketmq.processor;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.panda.merge.api.ITradeMarketConfigApi;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.constant.CategoryOppositeConfig;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.constant.SaleMatchSellStausEnum;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMatchMarketOddsPreResultMessage;
import com.panda.merge.dto.message.StandardMatchMarketPreResultMessage;
import com.panda.merge.exception.ExceptionHelper;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.producer.StandardMatchPreResultProducer;
import com.panda.merge.service.*;
import com.panda.merge.util.CategoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.ONE;

/**
 * 消费数据源盘口提前结算信息
 *
 * @author Aison<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2020/8/29 <br>
 * @see com.panda.merge.rocketmq.processor <br>
 */
@Component
@Slf4j
@Validated
@Async("ThirdMarketPreResultThreadPool")
public class ThirdMarketPreResultProcessor extends BaseProcessor {
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdMarketCategoryService thirdMarketCategoryService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportMarketSellService standardSportMarketSellService;
    @Autowired
    private ThirdSportMarketNewService thirdSportMarketService;
    @Autowired
    private ThirdSportMarketOddsNewService thirdSportMarketOddsService;
    @Autowired
    private StandardMatchPreResultProducer standardMatchPreResultProducer;
    @Autowired
    private ConfigCashOutTradeItemService configCashOutTradeItemService;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Lazy
    @DubboReference
    private ITradeMarketConfigApi iTradeMarketConfigApi;
    @Autowired
    private ThirdMarketCategoryFieldService thirdMarketCategoryFieldService;
    @Autowired
    private ConfigMarketCategoryMarginService configMarketCategoryMarginService;
    /**
     * 提前结算开关，false关，true开
     */
    @NacosValue(value = "${market.pre.switch}", autoRefreshed = true)
    private boolean marketPreSwitch;

    @ExceptionHelper
    public void thirdMarketPreResultApi(@Valid Request<ThirdMatchPreResultDTO> request) {
        String linkId = request.getLinkId();
        if (!marketPreSwitch) {
            log.info("::{}::提前结算NACOS关,接收数据商提前结算不处理", linkId);
            return;
        }
        StopWatch sw = new StopWatch(UUID.randomUUID().toString());
        sw.start("提前结算处理开始");
        log.info("::{}::数据源提前结算处理开始", linkId);
        ThirdMatchPreResultDTO thirdMatchPreResultDTO = request.getData();
        Long dataSourceTime = request.getDataSourceTime();
        String dataSourceCode = thirdMatchPreResultDTO.getDataSourceCode();
        String thirdMatchId = thirdMatchPreResultDTO.getThirdMatchId();
        //查找三方赛事
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSourceCode, thirdMatchId);
        if (null == thirdMatchInfo) {
            sw.stop();
            log.info("::{}::提前结算,查询三方赛事为空,三方赛事id={}", linkId, thirdMatchId);
            return;
        }
        Long standardMatchId = thirdMatchInfo.getReferenceId();
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (null == standardMatchInfo) {
            log.info("::{}::提前结算,未找到标准赛事信息,标准赛事id={}", linkId, standardMatchId);
            sw.stop();
            return;
        }
        //查找开售信息
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchId);
        if (null == standardSportMarketSell) {
            log.info("::{}::提前结算,未找到预开售信息,标准赛事id={}", linkId, standardMatchId);
            sw.stop();
            return;
        }
        int marketType = isOddsLive(standardMatchInfo.getId());
        if (marketType == 0) {
            if (!StringUtils.equals(standardSportMarketSell.getLiveMatchSellStatus(), SaleMatchSellStausEnum.Sold.name())) {
                log.info("::{}::提前结算,滚球未开售,标准赛事id={}", linkId, standardMatchId);
                sw.stop();
                return;
            }
        } else {
            if (!StringUtils.equals(standardSportMarketSell.getPreMatchSellStatus(), SaleMatchSellStausEnum.Sold.name())) {
                log.info("::{}::提前结算,赛前未开售,标准赛事id={}", linkId, standardMatchId);
                sw.stop();
                return;
            }
        }
        //处理三方盘口提前结算信息
        List<StandardMatchMarketPreResultMessage> marketPreResultMessageList = thirdConvertStandardMarket(linkId, standardMatchInfo, thirdMatchPreResultDTO, dataSourceTime, standardSportMarketSell, thirdMatchInfo);
        sw.stop();
        log.info("::{}::提前结算处理耗时{}ms,处理三方盘口条数:{},标准盘口条数:{}," + sw.prettyPrint(), linkId, sw.getTotalTimeMillis(), thirdMatchPreResultDTO.getMarketResultList().size(), marketPreResultMessageList.size());
    }

    /**
     * 三方盘口转标准 ，下发最新模板开关配置
     *
     * @param linkId
     * @param standardMatchInfo
     * @param thirdMatchPreResultDTO
     * @param dataSourceTime
     * @param standardSportMarketSell
     * @param thirdMatchInfo
     * @return
     */
    private List<StandardMatchMarketPreResultMessage> thirdConvertStandardMarket(String linkId, StandardMatchInfo standardMatchInfo, ThirdMatchPreResultDTO thirdMatchPreResultDTO,
                                                                                 Long dataSourceTime, StandardSportMarketSell standardSportMarketSell, ThirdMatchInfo thirdMatchInfo) {
        List<StandardMatchMarketPreResultMessage> marketPreResultMessageList = new ArrayList<>();
        if (!StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId())) {
            log.info("::{}::提前结算,非足球不处理,标准赛事id={}", linkId, standardMatchInfo.getId());
            return marketPreResultMessageList;
        }
        //获取系统级提前结算开关参数信息
        String SystemThirdMarketPreParams = Constant.REDIS_KEY.SYSTEM_THIRD_MARKET_PRE_PARAMS;
        Map<String, Integer> paramsMap = redisService.hGetAll(SystemThirdMarketPreParams);
        if (!CollectionUtils.isEmpty(paramsMap)) {
            int AoOnOff = paramsMap.get("AO");
            int SrOnOff = paramsMap.get("SR");

            if (AoOnOff == 0 && "AO".equals(thirdMatchPreResultDTO.getDataSourceCode())) {
                log.info("::{}::AO 系统级提前结算为关", thirdMatchPreResultDTO.getDataSourceCode());
                return marketPreResultMessageList;
            }
            if (SrOnOff == 0 && "SR".equals(thirdMatchPreResultDTO.getDataSourceCode())) {
                log.info("::{}::SR 系统级提前结算为关", thirdMatchPreResultDTO.getDataSourceCode());
                return marketPreResultMessageList;
            }
        }
        //最终赛事状态 （提前结算开关 - 赛事操盘状态） 业务用
        AtomicInteger resultStatus = new AtomicInteger(0);
        //赛事提前结算开关状态  风控用
        AtomicInteger matchPreStatusRisk = new AtomicInteger(0);
        //添加赛事界别开关
        //转换状态为 0:关  1:开
        int marketType = isOddsLive(standardMatchInfo.getId());
        //查询出赛事级别开关,对下发数据进行比较,如果 赛事级别提前结算开关 没开的话 不进行下发
        ConfigCashOutTradeItem configCashOutTradeItemRace = configCashOutTradeItemService.getItem(standardMatchInfo.getId(), marketType, 1, 1);
        log.info("::{}::查询赛事级别开关拦截相关操作,标准赛事configCashOutTradeItem={}", linkId, configCashOutTradeItemRace);
        if (configCashOutTradeItemRace != null) {
            String configDataScource = null == configCashOutTradeItemRace.getDataSourceCode() ? DataSourceCodeEnum.SR.code : configCashOutTradeItemRace.getDataSourceCode();
            if (!thirdMatchPreResultDTO.getDataSourceCode().equals(configDataScource)) {
                return marketPreResultMessageList;
            }
        }
        //玩法级别配置
        Map<Long, ConfigCashOutTradeItem> cashOutTradeItemCategoryMap = getCashOutTradeItemConfig(linkId, standardMatchInfo, resultStatus, matchPreStatusRisk);
        //缓存最新提前结算盘口
        String thirdPreMarketKey = Constant.REDIS_KEY.THIRD_MARKET_PRE_RESULT + standardMatchInfo.getId();
        //提前结算概率标准盘口缓存 Map<标准盘口ID，标准提前结算盘口>
        String standardPreMarketKey = Constant.REDIS_KEY.STANDARD_MARKET_PRE_RESULT + standardMatchInfo.getId();
        //提前结算-辅助信息
        String checkstandardMatchInfo = Constant.REDIS_KEY.CHECK_STANDARD_MATCH_INFO;
        //最新下发(业务/风控) 盘口赔率缓存
        Map<Long, StandardMarketMessage> newMarketCacheMessageMap = getNewStandardMarketCacheMessage(linkId, standardMatchInfo);
        //获取标准缓存中的所有盘口（赛前数据商和滚球数据商）
        List<StandardMarketDataMessage> collect = getStandardMarketDataMessages(linkId, standardMatchInfo, standardSportMarketSell);
        Map<Long, StandardMarketDataMessage> standardMarketMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(collect)) {
            standardMarketMap = collect.stream().collect(Collectors.toMap(e -> e.getRelationMarketId(), e -> e, (oldValue, newValue) -> newValue));
        }

        String hashValue = UUIdUtils.getId() + "_lock_StandardMatchMarketPreResult";
        String redisKey = RedisConfig.REDIS_KEY_DATABASE + "lock::StandardMatchMarketPreResult:" + standardMatchInfo.getId();
        log.info("::{}::提前结算,thirdConvertStandardMarket,redisLocKey:{},准备获取分布式锁,lockValue:{}", linkId, redisKey, hashValue);
        redisService.tryLock(redisKey, hashValue, 5, 3);
        log.info("::{}::提前结算,thirdConvertStandardMarket,redisLocKey:{},获取到分布式锁,lockValue:{}", linkId, redisKey, hashValue);

        try {
            //判断是否主客相反
            boolean isHomeAwayOpposite = Boolean.FALSE;
            if (null != thirdMatchInfo && thirdMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.code) && ONE.equals(thirdMatchInfo.getHomeAwayOpposite())) {
                //isHomeAwayOpposite = Boolean.TRUE;
            }
            //处理三方盘口提前结算信息
            List<ThirdMarketPreResultDTO> marketResultList = thirdMatchPreResultDTO.getMarketResultList();
            for (ThirdMarketPreResultDTO thirdMarket : marketResultList) {
                thirdMarket.setThirdMatchId(thirdMatchPreResultDTO.getThirdMatchId());
                String thirdCategorySourceId = thirdMarket.getThirdMarketCategorySourceId();
                String thirdMarketId = thirdMarket.getThirdMarketId();
                thirdMarket.setThirdMarketId(thirdMarketId);
                ThirdMarketCategory thirdMarketCategory = thirdMarketCategoryService.getItem(thirdMatchPreResultDTO.getDataSourceCode(), thirdCategorySourceId);

                if (thirdMarketCategory == null) {
                    log.info("::{}::提前结算,未找到三方玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                    continue;
                }
                Long marketCategoryId = thirdMarketCategory.getReferenceId();
                if (null == marketCategoryId || 0L == marketCategoryId) {
                    log.info("::{}::提前结算,三方玩法未绑定标准玩法,三方玩法id:{}", linkId, thirdCategorySourceId);
                    continue;
                }
                //提前计算支持玩法
                if (!MarginCategoryConfig.PRE_STANDARD_CATEGORY.contains(marketCategoryId)) {
                    log.info("::{}::提前结算,不在本期玩法集合内，不处理,三方玩法id:{},标准玩法id：{}", linkId, thirdCategorySourceId, marketCategoryId);
                    continue;
                }
                if (isHomeAwayOpposite) {
                    log.info("::{}::提前结算,主客相反，需要处理提前结算盘口数据，玩法id：{}", linkId, marketCategoryId);
                    changeThirdMarketPreResultContent(linkId, thirdMarket, marketCategoryId);
                    marketCategoryId = thirdMarket.getMarketCategoryId();
                }
                //标准盘口ID生成
                Long relationMarketId = thirdSportMarketService.getRelationMarketId(linkId, standardMatchInfo.getId(), marketCategoryId,
                        thirdMarket.getAddition1(), thirdMarket.getAddition2(), thirdMarket.getAddition3(), thirdMarket.getAddition4(), thirdMarket.getAddition5(),
                        0, thirdMarket.getThirdMarketId());
                //时间戳校验
                String dataSourceTimeKey = Constant.REDIS_KEY.THIRD_MARKET_PRE_RESULT_DATASOURCE_TIME + standardMatchInfo.getSportId() + "_" + relationMarketId;
                Long oldTime = (Long) redisService.get(dataSourceTimeKey);
                if (oldTime != null && oldTime > dataSourceTime) {
                    log.info("::{}::提前结算盘口时间戳小于当前盘口时间戳,三方源盘口id:{},RedisKEY:{},旧时间戳:{}", linkId, thirdMarketId, dataSourceTimeKey, oldTime);
                    continue;
                }
                //设置盘口时间搓
                redisService.set(dataSourceTimeKey, dataSourceTime, RedisConfig.REDIS_MY_TIME);

                //缓存提前结算三方盘口信息
                Map<String, ThirdMarketPreResultDTO> categoryMap = new HashMap<>();
                Object o = redisService.hGet(thirdPreMarketKey, String.valueOf(marketCategoryId));
                if (!Objects.isNull(o)) {
                    categoryMap = (Map<String, ThirdMarketPreResultDTO>) o;
                    // 只下发数据商CashOutStatus = 1 , 缓存只下发非1状态
                    ThirdMarketPreResultDTO oldThirdMarketPreResultDTO = categoryMap.get(thirdMarketId);
                    if (thirdMarket.getCashOutStatus() != 1) {
                        if (oldThirdMarketPreResultDTO != null && oldThirdMarketPreResultDTO.getCashOutStatus().equals(thirdMarket.getCashOutStatus())) {
                            log.info("::{}::提前结算盘口非1状态只下发一次,三方源盘口id:{},赛事ID:{},当前状态:{},缓存状态:{}",
                                    linkId, thirdMarketId, standardMatchInfo.getId(), thirdMarket.getCashOutStatus(), oldThirdMarketPreResultDTO.getCashOutStatus());
                            continue;
                        } else {
                            log.info("::{}::提前结算盘口下发状态与缓存状态不一致正常下发,三方源盘口id:{},赛事ID:{},当前状态:{}",
                                    linkId, thirdMarketId, standardMatchInfo.getId(), thirdMarket.getCashOutStatus());
                        }
                    } else {
                        log.info("::{}::提前结算盘口数据商CashOutStatus为1正常下发,三方源盘口id:{},赛事ID:{},当前状态:{}",
                                linkId, thirdMarketId, standardMatchInfo.getId(), thirdMarket.getCashOutStatus());
                    }
                }
                categoryMap.put(thirdMarketId, thirdMarket);
                //缓存最新的盘口信息
                redisService.hSet(thirdPreMarketKey, String.valueOf(marketCategoryId), categoryMap, marketCacheTime(standardMatchInfo.getBeginTime()));

                //转换标准盘口信息
                StandardMatchMarketPreResultMessage marketMessage = new StandardMatchMarketPreResultMessage();
                BeanUtils.copyProperties(thirdMarket, marketMessage);
                marketMessage.setId(relationMarketId);
                marketMessage.setMatchPreStatus(resultStatus.get());
                marketMessage.setMatchPreStatusRisk(matchPreStatusRisk.get());
                marketMessage.setMarketCategoryId(marketCategoryId);
                //转换标准盘口信息记录获取三方数据时间
                marketMessage.setThirdSportSendTime(TimeUtils.millsSecondsEast8ZoneGmt());

                ConfigCashOutTradeItem configCashOutTradeItem = cashOutTradeItemCategoryMap.get(marketCategoryId);
                if (configCashOutTradeItem != null) {
                    marketMessage.setCategoryPreStatus(configCashOutTradeItem.getCategoryPreStatus());
                    marketMessage.setCashOutMargin(new BigDecimal(configCashOutTradeItem.getCashOutMargin()));
                }
                Boolean isUpCashStatus = Boolean.FALSE;
                //获取最新盘口赔率最终状态
                StandardMarketMessage standardMarketMessage = newMarketCacheMessageMap.get(relationMarketId);
                if (standardMarketMessage != null) {
                    log.info("::{}::提前结算盘口状态:{},三方源盘口id:{},标准盘口id:{},赛事ID:{}",
                            linkId, standardMarketMessage.getStatus(), thirdMarketId, relationMarketId, standardMatchInfo.getId());
                    marketMessage.setStatus(standardMarketMessage.getStatus());
                    if (!CollectionUtils.isEmpty(standardMarketMessage.getMarketOddsList()) && standardMarketMessage.getMarketOddsList().size() == 2) {
                        marketMessage.setSpread(getMargin(linkId, standardMatchInfo, standardMarketMessage.getMarketCategoryId(), standardMarketMessage.getChildMarketCategoryId(), standardMarketMessage.getPlaceNum()));
                    }
                } else {

                    //这里一定要找标准盘口赔率缓存的坑位，用于盘口开关封锁逻辑
                    StandardMarketDataMessage standardMarketDataMessage = standardMarketMap.get(relationMarketId);
                    if (standardMarketDataMessage != null) {
                        //判断是SR,还是AO,如果是SR处理下面流程,AO不处理
                        if ("SR".equals(standardMarketDataMessage.getDataSourceCode())) {
                            //最新盘口赔率不存在，有可能被最大盘口数过滤了，下发关盘不提前结算
                            isUpCashStatus = Boolean.TRUE;
                            StandardMarketMessage newMarketStatusMessage = verifyMarketStatus(linkId, relationMarketId, standardMarketDataMessage, standardMatchInfo, Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                            log.info("::{}::提前结算盘口状态最新盘口缓存不存在,重新计算状态:{},标准盘口id:{},三方盘口id:{},赛事ID:{},源CashOutStatus:{}",
                                    linkId, newMarketStatusMessage.getStatus(), relationMarketId, thirdMarketId, standardMatchInfo.getId(), thirdMarket.getCashOutStatus());
                            marketMessage.setStatus(newMarketStatusMessage.getStatus());
                        } else if ("AO".equals(standardMarketDataMessage.getDataSourceCode())) {
                            marketMessage.setStatus(standardMarketDataMessage.getStatus());
                        }
                        if (!CollectionUtils.isEmpty(standardMarketDataMessage.getMarketOddsList()) && standardMarketDataMessage.getMarketOddsList().size() == 2) {
                            marketMessage.setSpread(getMargin(linkId, standardMatchInfo, standardMarketDataMessage.getMarketCategoryId(), standardMarketDataMessage.getChildMarketCategoryId(), standardMarketDataMessage.getPlaceNum()));
                        }
                    } else {
                        log.info("::{}::提前结算盘口状态最新盘口缓存不存在默认盘口关盘,标准盘口id:{},三方盘口id:{},赛事ID:{}",
                                linkId, relationMarketId, thirdMarketId, standardMatchInfo.getId());
                    }
                }

                //转换标准投注项
                List<StandardMatchMarketOddsPreResultMessage> marketOddsMessageList = new ArrayList<>();
                List<ThirdMarketOddsPreResultDTO> marketOddsResultList = thirdMarket.getMarketOddsResultList();
                if (!CollectionUtils.isEmpty(marketOddsResultList)) {
                    //主客相反提前结算投注项处理
                    if (isHomeAwayOpposite) {
                        log.info("::{}::提前结算,主客相反，需要处理提前结算投注项数据，玩法id：{}", linkId, marketCategoryId);
                        changePreResultThirdMarketOddsContent(linkId, marketOddsResultList, thirdMarket, thirdMatchPreResultDTO.getDataSourceCode());
                    }
                    for (ThirdMarketOddsPreResultDTO thirdMarketOdds : marketOddsResultList) {
                        StandardMatchMarketOddsPreResultMessage marketOddsPreResultMessage = new StandardMatchMarketOddsPreResultMessage();
                        BeanUtils.copyProperties(thirdMarketOdds, marketOddsPreResultMessage);
                        //标准投注项ID生成
                        Long relationMarketOddsId = thirdSportMarketOddsService.getRelationMarketOddsId(relationMarketId, thirdMarketOdds.getOddsType(),
                                thirdMarketOdds.getThirdOddsFieldSourceId(), thirdMarketOdds.getAddition1(), marketCategoryId);
                        marketOddsPreResultMessage.setId(relationMarketOddsId);
                        marketOddsMessageList.add(marketOddsPreResultMessage);
                    }
                    marketMessage.setMarketOddsPreResultMessages(marketOddsMessageList);
                }
                //缓存标准提前计算概率信息
                redisService.hSet(standardPreMarketKey, relationMarketId.toString(), marketMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
                if (isUpCashStatus) {
                    marketMessage.setCashOutStatus(-1);
                }
                //处理旧数据
                if (marketMessage.getMarketType() == null) {
                    marketMessage.setMarketType(isOddsLive(standardMatchInfo.getId()));
                }
                log.info("::{}::赛事ID:{},thirdConvertStandardMarket提前结算最终下发,三方盘口ID:{},标准盘口ID:{},赛事提前结算开关:{},盘口CashOutStatus:{},玩法提前结算开关:{},盘口状态:{},赛前滚球类型:{},赛事阶段:{}",
                        linkId, standardMatchInfo.getId(), thirdMarketId, relationMarketId, resultStatus.get(), marketMessage.getCashOutStatus(),
                        marketMessage.getCategoryPreStatus(), marketMessage.getStatus(), marketMessage.getMarketType(), marketMessage.getMatchPeriod());
                marketPreResultMessageList.add(marketMessage);
                //保存赛事ID(取滚球状态下的赛事)
                if (0 == thirdMarket.getMarketType()) {
                    redisService.hSet(checkstandardMatchInfo, String.valueOf(standardMatchInfo.getId()), standardMatchInfo.getId(), marketCacheTime(standardMatchInfo.getBeginTime()));
                }
            }
        } finally {
            redisService.unLock(redisKey, hashValue);
            log.info("::{}::提前结算,thirdConvertStandardMarket,redisKey:{},释放分布式锁,lockValue:{}", linkId, redisKey, hashValue);
        }
        //下发数据
        if (!CollectionUtils.isEmpty(marketPreResultMessageList)) {
            standardMatchPreResultProducer.sendStandardMatchPreResult(linkId, standardMatchInfo, standardMatchInfo.getSportId(),
                    marketPreResultMessageList, resultStatus.get(), dataSourceTime);
        } else {
            log.info("::{}::提前结算,marketPreResultMessageList为空,标准玩法id:{},三方结算信息:{},",
                    linkId, standardMatchInfo.getId(), JSONObject.toJSONString(thirdMatchPreResultDTO));
        }
        return marketPreResultMessageList;
    }


    /**
     * 修改模板参数触发，给下游最新的开关配置
     * 赛事级别提前结算开关，玩法级别提前结算开关 ，操盘下发赔率
     *
     * @param standardMatchInfo 赛事信息
     * @param dataSoureTime     时间
     */
    public void sendThirdPreMarket(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageAllList, long dataSoureTime) {
        if (!marketPreSwitch) {
            log.info("::{}::提前结算NACOS关,修改模板参数触发不处理", linkId);
            return;
        }
        //AO提前结算逻辑已经处理过，主流程不处理
        if (linkId.contains("AO_")) {
            return;
        }
        if (!standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.getCode())) {
            return;
        }
        StandardSportMarketSell standardSportMarketSell = standardSportMarketSellService.getItem(standardMatchInfo.getId());
        if (null == standardSportMarketSell) {
            log.info("::{}::sendThirdPreMarket提前结算,未找到预开售信息,标准赛事id={}", linkId, standardMatchInfo.getId());
            return;
        }
        List<StandardMarketDataMessage> collect = getStandardMarketDataMessages(linkId, standardMatchInfo, standardSportMarketSell);
        if (!CollectionUtils.isEmpty(collect)) {
            Map<Long, List<StandardMarketDataMessage>> standardMarketList = collect.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getMarketCategoryId));
            log.info("::{}::sendThirdPreMarket下发提前结算配置级最新结算盘口信息,赛事ID:{},提前结算盘口条数:{}",
                    linkId, standardMatchInfo.getId(), collect.size());
            StopWatch sw = new StopWatch(UUID.randomUUID().toString());
            sw.start("提前结算处理开始");
            List<StandardMatchMarketPreResultMessage> marketPreResultMessageList = sendStandardMarket(linkId, standardMatchInfo, standardMarketList, dataSoureTime);
            sw.stop();
            log.info("::{}::sendThirdPreMarket提前结算处理耗时{}ms,处理三方盘口条数:{},标准盘口条数:{}," + sw.prettyPrint(), linkId, sw.getTotalTimeMillis(), marketPreResultMessageList.size(), marketPreResultMessageList.size());
        } else {
            log.info("::{}::提前结算赛事ID:{},sendThirdPreMarket标准赔率盘口不存在提前结算玩法", linkId, standardMatchInfo.getId());
        }
    }

    /**
     * 获取缓存中的所有盘口（赛前数据商和滚球数据商）
     *
     * @param linkId
     * @param standardMatchInfo
     * @param standardSportMarketSell
     * @return
     */
    private List<StandardMarketDataMessage> getStandardMarketDataMessages(String linkId, StandardMatchInfo standardMatchInfo, StandardSportMarketSell standardSportMarketSell) {
        Set<Long> marketCategoryIdSet = new HashSet<>();
        Map<String, StandardMarketDataMessage> stringStandardMarketDataMessageMap = iTradeMarketConfigApi.getStringStandardMarketDataMessageMap(
                null, linkId, standardMatchInfo, standardSportMarketSell);
        List<StandardMarketDataMessage> collect = stringStandardMarketDataMessageMap.values().stream()
                .filter(e -> MarginCategoryConfig.PRE_STANDARD_CATEGORY.contains(e.getMarketCategoryId())).collect(Collectors.toList());
        collect.forEach(v -> {
            if (v.getChildMarketCategoryId() == null) {
                v.setChildMarketCategoryId(CategoryUtils.getChildCategoryId(linkId, v.getMarketCategoryId(),
                        v.getAddition1(), v.getAddition2(), v.getAddition3(),
                        v.getAddition4(), v.getAddition5(), String.valueOf(v.getStandardMatchInfoId())));
            }
            marketCategoryIdSet.add(v.getMarketCategoryId());
        });
        //TX坑位处理
//        thirdMatchMarketProcessor.txMarketPlaceMerge(linkId, standardMatchInfo, stringStandardMarketDataMessageMap, marketCategoryIdSet);
        //AO坑位处理
        thirdMatchMarketProcessor.aoMarketPlaceMerge(linkId, standardMatchInfo, collect, false);
        return collect;
    }

    /**
     * @param linkId
     * @param standardMatchInfo
     * @param standardMarketDataMessageList
     * @param dataSourceTime
     * @return
     */
    private List<StandardMatchMarketPreResultMessage> sendStandardMarket(String linkId, StandardMatchInfo standardMatchInfo, Map<Long, List<StandardMarketDataMessage>> standardMarketDataMessageList, Long dataSourceTime) {
        //最终赛事状态 （提前结算开关 - 赛事操盘状态） 业务用
        AtomicInteger resultStatus = new AtomicInteger(0);
        //赛事提前结算开关状态  风控用
        AtomicInteger matchPreStatusRisk = new AtomicInteger(0);
        //玩法级别配置
        Map<Long, ConfigCashOutTradeItem> cashOutTradeItemCategoryMap = getCashOutTradeItemConfig(linkId, standardMatchInfo, resultStatus, matchPreStatusRisk);
        //状态有改变的标准提前结算概率
        List<StandardMatchMarketPreResultMessage> sendStandardPreResultMessageList = new ArrayList<>();
        //最新下发(业务/风控) 盘口赔率缓存
        Map<Long, StandardMarketMessage> newMarketCacheMessageMap = getNewStandardMarketCacheMessage(linkId, standardMatchInfo);

        String hashValue = UUIdUtils.getId() + "_lock_StandardMatchMarketPreResult";
        String redisKey = RedisConfig.REDIS_KEY_DATABASE + "lock::StandardMatchMarketPreResult:" + standardMatchInfo.getId();
        log.info("::{}::提前结算,sendStandardMarket,redisLocKey:{},准备获取分布式锁,lockValue:{}", linkId, redisKey, hashValue);
        redisService.tryLock(redisKey, hashValue, 5, 3);
        log.info("::{}::提前结算,sendStandardMarket,redisLocKey:{},获取到分布式锁,lockValue:{}", linkId, redisKey, hashValue);

        try {
            //提前结算概率标准盘口缓存 Map<标准盘口ID，标准提前结算盘口>
            String standardPreMarketKey = Constant.REDIS_KEY.STANDARD_MARKET_PRE_RESULT + standardMatchInfo.getId();
            Map<String, StandardMatchMarketPreResultMessage> standardMatchMarketPreResultMessageMap = redisService.hGetAll(standardPreMarketKey);
            for (Map.Entry<Long, List<StandardMarketDataMessage>> entry : standardMarketDataMessageList.entrySet()) {
                Long categoryId = entry.getKey();
                List<StandardMarketDataMessage> marketDataMessages = entry.getValue();
                for (StandardMarketDataMessage marketMessage : marketDataMessages) {
                    Long relationMarketId = marketMessage.getRelationMarketId();
                    //获取上一次缓存提前计算概率信息
                    StandardMatchMarketPreResultMessage cacheStandardMarketPreResult = standardMatchMarketPreResultMessageMap.get(relationMarketId.toString());
                    if (cacheStandardMarketPreResult == null) {
                        log.info("::{}::提前结算盘口缓存不存在,赛事ID:{},玩法ID:{},标准盘口ID:{},三方盘口id:{}",
                                linkId, categoryId, standardMatchInfo.getId(), relationMarketId, marketMessage.getThirdMarketSourceId());
                        continue;
                    }
                    //时间戳校验
                    String dataSourceTimeKey = Constant.REDIS_KEY.THIRD_MARKET_PRE_RESULT_DATASOURCE_TIME + standardMatchInfo.getSportId() + "_" + cacheStandardMarketPreResult.getId();
                    Long oldTime = (Long) redisService.get(dataSourceTimeKey);
                    if (oldTime != null && oldTime > dataSourceTime) {
                        log.info("::{}::提前结算盘口时间戳小于当前盘口时间戳,玩法ID:{},标准盘口ID:{},三方盘口id:{},RedisKEY:{},旧时间戳:{}",
                                linkId, categoryId, cacheStandardMarketPreResult.getId(), marketMessage.getThirdMarketSourceId(), dataSourceTimeKey, oldTime);
                        continue;
                    }
                    ConfigCashOutTradeItem configCashOutTradeItem = cashOutTradeItemCategoryMap.get(marketMessage.getMarketCategoryId());
                    if (configCashOutTradeItem == null) {
                        log.info("::{}::提前结算玩法配置不存在11,赛事ID:{},标准盘口ID:{},玩法ID:{},三方盘口id:{}",
                                linkId, standardMatchInfo.getId(), cacheStandardMarketPreResult.getId(), categoryId, marketMessage.getThirdMarketSourceId());
                        configCashOutTradeItem = new ConfigCashOutTradeItem();
                        //初始化赋值：玩法级别提前结算开关 默认关
                        configCashOutTradeItem.setCategoryPreStatus(0);
                        configCashOutTradeItem.setCashOutMargin(0L);
                    }
                    StandardMarketMessage standardMarketMessage = newMarketCacheMessageMap.get(relationMarketId);
                    //
                    Boolean isUpCashStatus = Boolean.FALSE;
                    //如果上一次不存在盘口赔率，可能被最大盘口数给过滤了 ，数据商CashOutStatus改为-1 ，最终盘口状态：关盘
                    Integer finalMarketStatus = 0;
                    if (standardMarketMessage != null) {
                        finalMarketStatus = standardMarketMessage.getStatus();
                        log.info("::{}::提前结算盘口状态最新盘口存在,状态:{},标准盘口id:{},三方盘口id:{},赛事ID:{}",
                                linkId, standardMarketMessage.getStatus(), relationMarketId, marketMessage.getThirdMarketSourceId(), standardMatchInfo.getId());
                    } else {
                        //需要重新计算开关封锁逻辑 ，数据源盘口状态 和 paStatus(盘口赔率不通过) 都默认为 关盘
                        //判断是SR,还是AO,如果是SR处理下面流程,AO不处理
                        if ("SR".equals(marketMessage.getDataSourceCode())) {
                            isUpCashStatus = Boolean.TRUE;
                            StandardMarketMessage newMarketStatusMessage = verifyMarketStatus(linkId, relationMarketId, marketMessage, standardMatchInfo, Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                            finalMarketStatus = newMarketStatusMessage.getStatus();
                            log.info("::{}::提前结算盘口状态最新盘口不存在,重新计算状态:{},标准盘口id:{},三方盘口id:{},赛事ID:{},源CashOutStatus:{},改为-1",
                                    linkId, finalMarketStatus, relationMarketId, marketMessage.getThirdMarketSourceId(), standardMatchInfo.getId(), cacheStandardMarketPreResult.getCashOutStatus());
                        } else if ("AO".equals(marketMessage.getDataSourceCode())) {
                            finalMarketStatus = marketMessage.getStatus();
                        }
                    }
                    //最终盘口状态与概率盘口状态一致不下发
                    if (finalMarketStatus.equals(cacheStandardMarketPreResult.getStatus())) {
                        //玩法提前结算开关，玩法提前结算margin ，赛事提前结算开关 ，
                        Integer categoryPreStatus = cacheStandardMarketPreResult.getCategoryPreStatus();
                        Long cashOutMargin = cacheStandardMarketPreResult.getCashOutMargin().longValue();
                        Integer matchPreStatus = cacheStandardMarketPreResult.getMatchPreStatus();
                        Integer categoryPreStatusDB = configCashOutTradeItem.getCategoryPreStatus();
                        Long cashOutMarginDB = configCashOutTradeItem.getCashOutMargin();
                        log.info("::{}::提前结算最终盘口状态对比,赛事ID:{},标准盘口ID:{},三方盘口id:{},玩法ID:{},最终盘口状态【{}:{}】,玩法提前结算开关【{}:{}】,玩法提前结算margin【{}:{}】,最终赛事状态【{}:{}】",
                                linkId, standardMatchInfo.getId(), cacheStandardMarketPreResult.getId(), marketMessage.getThirdMarketSourceId(), categoryId, finalMarketStatus, cacheStandardMarketPreResult.getStatus(),
                                categoryPreStatus, categoryPreStatusDB, cashOutMargin, cashOutMarginDB, resultStatus.get(), matchPreStatus);
                        if (categoryPreStatus.equals(categoryPreStatusDB) && cashOutMargin.equals(cashOutMarginDB) && resultStatus.get() == matchPreStatus) {
                            log.info("::{}::提前结算最终盘口状态与概率盘口状态一致不下发,赛事ID:{},标准盘口ID:{},三方盘口id:{},玩法ID:{}",
                                    linkId, standardMatchInfo.getId(), cacheStandardMarketPreResult.getId(), marketMessage.getThirdMarketSourceId(), categoryId);
                            continue;
                        }
                    } else {
                        log.info("::{}::提前结算最终盘口状态与概率盘口状态不一致下发,赛事ID:{},标准盘口ID:{},玩法ID:{},缓存盘口状态:{},最新盘口状态:{}",
                                linkId, standardMatchInfo.getId(), cacheStandardMarketPreResult.getId(), categoryId, cacheStandardMarketPreResult.getStatus(), cacheStandardMarketPreResult.getStatus());
                    }
                    //玩法级别提前结算开关
                    cacheStandardMarketPreResult.setCategoryPreStatus(configCashOutTradeItem.getCategoryPreStatus());
                    //cashOut Margin
                    cacheStandardMarketPreResult.setCashOutMargin(new BigDecimal(configCashOutTradeItem.getCashOutMargin()));
                    //赛事级别提前结算开关 1开 ，0关 融合 赛事操盘级别开关
                    cacheStandardMarketPreResult.setMatchPreStatus(resultStatus.get());
                    cacheStandardMarketPreResult.setMatchPreStatusRisk(matchPreStatusRisk.get());
                    //最终盘口状态
                    cacheStandardMarketPreResult.setStatus(finalMarketStatus);
                    //缓存标准提前计算概率信息
                    redisService.hSet(standardPreMarketKey, relationMarketId.toString(), cacheStandardMarketPreResult, marketCacheTime(standardMatchInfo.getBeginTime()));
                    if (isUpCashStatus) {
                        cacheStandardMarketPreResult.setCashOutStatus(-1);
                    }
                    //处理旧数据
                    if (cacheStandardMarketPreResult.getMarketType() == null) {
                        cacheStandardMarketPreResult.setMarketType(isOddsLive(standardMatchInfo.getId()));
                    }
                    log.info("::{}::赛事ID:{},sendStandardMarket提前结算最终下发,标准盘口ID:{},赛事提前结算开关:{},盘口CashOutStatus:{},玩法提前结算开关:{},盘口状态:{},赛前滚球类型:{},赛事阶段:{}",
                            linkId, standardMatchInfo.getId(), relationMarketId, resultStatus.get(), cacheStandardMarketPreResult.getCashOutStatus(),
                            cacheStandardMarketPreResult.getCategoryPreStatus(), marketMessage.getStatus(), cacheStandardMarketPreResult.getMarketType(), cacheStandardMarketPreResult.getMatchPeriod());
                    sendStandardPreResultMessageList.add(cacheStandardMarketPreResult);
                }
            }
        } finally {
            redisService.unLock(redisKey, hashValue);
            log.info("::{}::提前结算,sendStandardMarket,redisKey:{},释放分布式锁,lockValue:{}", linkId, redisKey, hashValue);
        }

        //下发数据
        if (!CollectionUtils.isEmpty(sendStandardPreResultMessageList)) {
            StandardMatchMarketPreResultMessage standardMatchMarketPreResultMessage = sendStandardPreResultMessageList.get(0);
            standardMatchPreResultProducer.sendStandardMatchPreResult(linkId, standardMatchInfo, standardMatchInfo.getSportId(),
                    sendStandardPreResultMessageList, standardMatchMarketPreResultMessage.getMatchPreStatus(), dataSourceTime);
        } else {
            log.info("::{}::提前结算,下发数据为空,标准玩法赛事ID:{},", linkId, standardMatchInfo.getId());
        }
        return sendStandardPreResultMessageList;
    }

    /**
     * 获取操盘配置
     *
     * @param linkId
     * @param standardMatchInfo
     * @param resultStatus       最终赛事状态 （提前结算开关 - 赛事操盘状态） 业务用
     * @param matchPreStatusRisk 提前结算开关 风控用
     * @return
     */
    private Map<Long, ConfigCashOutTradeItem> getCashOutTradeItemConfig(String linkId, StandardMatchInfo standardMatchInfo, AtomicInteger resultStatus, AtomicInteger matchPreStatusRisk) {
        Integer operateMatchStatus = standardMatchInfo.getOperateMatchStatus() == -1 ? 0 : standardMatchInfo.getOperateMatchStatus();
        //转换状态为 0:关  1:开
        operateMatchStatus = operateMatchStatus == 0 ? 1 : 0;
        //玩法级别配置
        Map<Long, ConfigCashOutTradeItem> cashOutTradeItemCategoryMap = new HashMap<>();
        int marketType = isOddsLive(standardMatchInfo.getId());
        List<ConfigCashOutTradeItem> configCashOutTradeItemList = configCashOutTradeItemService.getItemList(standardMatchInfo.getId(), marketType);
        if (!CollectionUtils.isEmpty(configCashOutTradeItemList)) {
            //赛事级别配置
            List<ConfigCashOutTradeItem> cashOutTradeItemCategoryMatch = configCashOutTradeItemList.stream().filter(e -> e.getLeve() == 1).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(cashOutTradeItemCategoryMatch)) {
                //提前结算配置表状态
                Integer matchPreStatus = cashOutTradeItemCategoryMatch.get(0).getMatchPreStatus();
                matchPreStatusRisk.set(matchPreStatus);
                resultStatus.set(Math.min(matchPreStatus, operateMatchStatus));
                log.info("::{}::提前赛事配置,赛事ID:{},赛事操盘原始状态:{},赛事操盘转换后状态:{},赛事提前结算状态:{},最终下发状态:{}",
                        linkId, standardMatchInfo.getId(), standardMatchInfo.getOperateMatchStatus(), operateMatchStatus, matchPreStatus, resultStatus);
            }
            cashOutTradeItemCategoryMap = configCashOutTradeItemList.stream().filter(e -> e.getLeve() == 2).collect(Collectors.toMap(e -> e.getMarketCategoryId(), e -> e, (oldValue, newValue) -> newValue));
            log.info("::{}::提前结算配置,赛事ID:{},赛事信息:{},玩法信息:{}",
                    linkId, standardMatchInfo.getId(), JSONObject.toJSONString(cashOutTradeItemCategoryMatch), JSONObject.toJSONString(cashOutTradeItemCategoryMap));
        }
        return cashOutTradeItemCategoryMap;
    }

    /**
     * 最新下发 标准盘口赔率缓存
     * <p>
     * 特殊1：  以数据商概率状态为准，数据商盘口状态不作为开关封锁逻辑，默认为数据商开盘
     * 特殊2： PAstatus 赔率校验后的状态
     *
     * @param linkId
     * @param standardMatchInfo
     * @return
     */
    private Map<Long, StandardMarketMessage> getNewStandardMarketCacheMessage(String linkId, StandardMatchInfo standardMatchInfo) {
        Map<Long, StandardMarketMessage> marketMessageMap = new HashMap<>();
        String redisKey = DigestUtil.md5Hex(Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS + standardMatchInfo.getId());
        Object obj = redisService.hGetAll(redisKey);
        List<StandardMarketMessage> standardMarketMessageList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(obj)) {
            Map<Long, List<StandardMarketMessage>> standardMarketDataMessageMapNew = (Map<Long, List<StandardMarketMessage>>) obj;
            standardMarketDataMessageMapNew.entrySet().stream().forEach(k -> {
                if (MarginCategoryConfig.PRE_STANDARD_CATEGORY.contains(Long.valueOf(String.valueOf(k.getKey())))) {
                    standardMarketMessageList.addAll(k.getValue());
                }
            });
            if (!CollectionUtils.isEmpty(standardMarketMessageList)) {
                standardMarketMessageList.forEach(s -> {
                    //数据源状态 默认开
                    s.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.ACTIVE);
                    //开关封锁逻辑判断处理
                    //thirdMatchMarketProcessor.dealMarketStatus(linkId, s, standardMatchInfo);
                });
                marketMessageMap = standardMarketMessageList.stream().collect(Collectors.toMap(e -> e.getId(), e -> e, (oldValue, newValue) -> newValue));
            }
        }
        return marketMessageMap;
    }

    /**
     * 需要重新计算开关封锁逻辑 ，数据源盘口状态 和 paStatus(盘口赔率不通过等等状态) 都默认为 开盘
     *
     * @param linkId
     * @param standardMatchInfo
     * @return
     */
    private StandardMarketMessage verifyMarketStatus(String linkId, Long relationMarketId, StandardMarketDataMessage marketMessage, StandardMatchInfo standardMatchInfo, Integer status) {
        //转换盘口开关封锁实体
        StandardMarketMessage convertStandardMarketMessage = new StandardMarketMessage();
        BeanUtils.copyProperties(marketMessage, convertStandardMarketMessage, "marketOddsList");
        convertStandardMarketMessage.setId(relationMarketId);
        //数据源盘口默认状态
        convertStandardMarketMessage.setThirdMarketSourceStatus(status);
        //赔率校验盘口默认
        convertStandardMarketMessage.setPaStatus(status);
        //盘口开关封锁逻辑
        //thirdMatchMarketProcessor.dealMarketStatus(linkId, convertStandardMarketMessage, standardMatchInfo);
        return convertStandardMarketMessage;
    }


    /**
     * 收到滚球标识 / 收到SR赛事级别状态
     * 需要把赛前的概率全部改为cashOutStatus =-1下发
     *
     * @param linkId
     * @param standardMatchId
     * @param dataSourceTime
     */
    public void liveCloseCashOutStatus(String linkId, Long standardMatchId, Long dataSourceTime, String marketType, Boolean isTrue) {
        if (!marketPreSwitch) {
            log.info("::{}::提前结算NACOS关,收到滚球标识不处理", linkId);
            return;
        }
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
        if (standardMatchInfo == null) {
            return;
        }
        if (!standardMatchInfo.getSportId().equals(StandardSportTypeEnum.FootBall.getCode())) {
            return;
        }
        //提前结算赛事对应的数据源
        ConfigCashOutTradeItem outTradeItemServiceItem = configCashOutTradeItemService.getItem(standardMatchId, Integer.valueOf(marketType), 1);
        if (null == outTradeItemServiceItem) {
            log.info("::{}::收到滚球标识/提前结算赛事对应的数据源不存在,赛事ID:{},类型:{}", linkId, standardMatchId, marketType);
            return;
        }
        //兼容旧数据
        if (isTrue) {
            String dataSourceCode = null == outTradeItemServiceItem.getDataSourceCode() ? DataSourceCodeEnum.SR.code : outTradeItemServiceItem.getDataSourceCode();
            if (!StringUtils.equals(dataSourceCode, DataSourceCodeEnum.SR.code)) {
                log.info("::{}::收到滚球标识/只对SR赛事级别关盘关闭提前结算,赛事ID:{},类型:{}", linkId, standardMatchId, marketType);
                return;
            }
        }
        String hashValue = UUIdUtils.getId() + "_lock_StandardMatchMarketPreResult";
        String redisKey = RedisConfig.REDIS_KEY_DATABASE + "lock::StandardMatchMarketPreResult:" + standardMatchId;
        log.info("::{}::提前结算,收到滚球标识/SR赛事级别状态,redisLocKey:{},准备获取分布式锁,lockValue:{}", linkId, redisKey, hashValue);
        redisService.tryLock(redisKey, hashValue, 5, 3);
        log.info("::{}::提前结算,收到滚球标识/SR赛事级别状态,redisLocKey:{},获取到分布式锁,lockValue:{}", linkId, redisKey, hashValue);

        //状态有改变的标准提前结算概率
        List<StandardMatchMarketPreResultMessage> sendStandardPreResultMessageList = new ArrayList<>();
        try {
            //提前结算概率标准盘口缓存 Map<标准盘口ID，标准提前结算盘口>
            String standardPreMarketKey = Constant.REDIS_KEY.STANDARD_MARKET_PRE_RESULT + standardMatchInfo.getId();
            Map<String, StandardMatchMarketPreResultMessage> standardMatchMarketPreResultMessageMap = redisService.hGetAll(standardPreMarketKey);
            if (!CollectionUtils.isEmpty(standardMatchMarketPreResultMessageMap)) {
                standardMatchMarketPreResultMessageMap.forEach((key, marketPreResultMessage) -> {
                    //时间戳校验
                    String dataSourceTimeKey = Constant.REDIS_KEY.THIRD_MARKET_PRE_RESULT_DATASOURCE_TIME + standardMatchInfo.getSportId() + "_" + key;
                    Long oldTime = (Long) redisService.get(dataSourceTimeKey);
                    if (oldTime != null && oldTime > dataSourceTime) {
                        log.info("::{}::收到滚球标识/SR赛事级别状态cashOutStatus,提前结算盘口时间戳小于当前盘口时间戳,玩法ID:{},标准盘口ID:{},RedisKEY:{},旧时间戳:{}",
                                linkId, marketPreResultMessage.getMarketCategoryId(), key, dataSourceTimeKey, oldTime);
                        return;
                    }
                    marketPreResultMessage.setCashOutStatus(-1);
                    marketPreResultMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    marketPreResultMessage.setMatchPeriod(MatchStatusEnum.Not_Started.value);
                    log.info("::{}::赛事ID:{},收到滚球标识/SR赛事级别状态,提前结算最终下发,标准盘口ID:{},赛前滚球类型:{}", linkId, standardMatchInfo.getId(), key, marketPreResultMessage.getMarketType());
                    sendStandardPreResultMessageList.add(marketPreResultMessage);
                    redisService.hSet(standardPreMarketKey, marketPreResultMessage.getId().toString(), marketPreResultMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
                    //处理旧数据
                    if (marketPreResultMessage.getMarketType() == null) {
                        marketPreResultMessage.setMarketType(1);
                    }
                });

                if (!CollectionUtils.isEmpty(sendStandardPreResultMessageList)) {
                    StandardMatchMarketPreResultMessage standardMatchMarketPreResultMessage = sendStandardPreResultMessageList.get(0);
                    standardMatchPreResultProducer.sendStandardMatchPreResult(linkId, standardMatchInfo, standardMatchInfo.getSportId(),
                            sendStandardPreResultMessageList, standardMatchMarketPreResultMessage.getMatchPreStatus(), dataSourceTime);
                }
            }
        } finally {
            redisService.unLock(redisKey, hashValue);
            log.info("::{}::提前结算,收到滚球标识/SR赛事级别状态,redisLocKey:{},释放分布式锁,lockValue:{}", linkId, redisKey, hashValue);
        }
    }

    public void changeAndSendConfigCashOutTradeItem(String linkId, StandardMatchInfo standardMatchInfo, ConfigCashOutTradeItemDTO cashOutTradeItemDTO, ConfigCashOutTradeItem item) {
        if (!marketPreSwitch) {
            log.info("::{}::提前结算NACOS关,切换赛事级别提前提前结算数据源不处理", linkId);
            return;
        }
        log.info("::{}::收到提前结算开关数据源切换cashOutStatus:-2,赛事ID:{}:模版:" + JSON.toJSONString(cashOutTradeItemDTO) + "::item::" + JSON.toJSONString(item), linkId, standardMatchInfo.getDataSourceCode());

        String hashValue = UUIdUtils.getId() + "_lock_StandardMatchMarketPre";
        String redisKey = RedisConfig.REDIS_KEY_DATABASE + "lock::StandardMatchMarketPre:" + standardMatchInfo.getId();
        log.info("::{}::提前结算,提前结算赛事开关数据源切换,redisLocKey:{},准备获取分布式锁,lockValue:{}", linkId, redisKey, hashValue);
        redisService.tryLock(redisKey, hashValue, 5, 3);
        log.info("::{}::提前结算,赛事级别提前结算开关更改切换,redisLocKey:{},获取到分布式锁,lockValue:{}", linkId, redisKey, hashValue);

        try {
            if (item != null) {
                String dataSourceCodeDB = null == item.getDataSourceCode() ? "SR" : item.getDataSourceCode();
                if (!dataSourceCodeDB.equals(cashOutTradeItemDTO.getDataSourceCode())) {
                    log.info("需要变更数据源的提前结算开关,原始数据源为::{}::,新的数据源为::{}", item.getDataSourceCode(), cashOutTradeItemDTO.getDataSourceCode());
                    //提前结算开关 数据源变了 原来的数据源下发cashoutstatus=-2,
                    //循环并设置 提前结算标准盘口
                    List<StandardMatchMarketPreResultMessage> marketPreResultMessageList = new ArrayList<>();
                    String linkId_swift = linkId + "PRERESULT_SWIFT";
                    //提前结算标准盘口缓存 Map<标准盘口ID，标准提前结算盘口>
                    String standardPreMarketKey = Constant.REDIS_KEY.STANDARD_MARKET_PRE_RESULT + cashOutTradeItemDTO.getMatchId();
                    Map<String, StandardMatchMarketPreResultMessage>
                            standardMatchMarketPreResultMessageMap = redisService.hGetAll(standardPreMarketKey);
                    Set<String> standardMatchMarketPreResultMessage = standardMatchMarketPreResultMessageMap.keySet();
                    //循环并更改 盘口提前结算
                    for (String key : standardMatchMarketPreResultMessage) {
                        StandardMatchMarketPreResultMessage marketMessage = standardMatchMarketPreResultMessageMap.get(key);
                        //更改cashOut为-2,并下发
                        marketMessage.setCashOutStatus(-2);
                        marketPreResultMessageList.add(marketMessage);
                        //刷新缓存[缓存时间:（比赛时间 - 系统时间） + 一周时间]
                        log.info("{}::刷新提前结算缓存:" + marketMessage, linkId);
                        redisService.hSet(standardPreMarketKey, key, marketMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
                    }
                    //下发盘口数据
                    log.info("需关闭提前结算的数据源为" + item.getDataSourceCode() + ",cashout=-2,下发提前结算数据到业务系统:" + marketPreResultMessageList);
                    standardMatchPreResultProducer.sendStandardMatchPreResult(linkId_swift, standardMatchInfo, 1L, marketPreResultMessageList, marketPreResultMessageList.get(0).getMatchPreStatus(), System.currentTimeMillis());
                }
            }
        } catch (Exception e) {
            log.info("::{}::提前结算切换赛事级数据源AO/SR 报错信息::{}" + linkId, e.getMessage());
        } finally {
            redisService.unLock(redisKey, hashValue);
            log.info("::{}::提前结算赛事开关数据源切换,redisLocKey:{},释放分布式锁,lockValue:{}", linkId, redisKey, hashValue);
        }

    }

    /**
     * 提前结算主客相反三方盘口内容替换
     *
     * @param linkId
     * @param thirdSportMarket
     * @param marketCategoryId
     */
    public void changeThirdMarketPreResultContent(String linkId, ThirdMarketPreResultDTO thirdSportMarket, Long marketCategoryId) {
        log.info("::{}::提前结算主客相反, 标准玩法id:{}，addition1:{},addition2:{},addition3:{},addition4:{},", linkId, marketCategoryId,
                thirdSportMarket.getAddition1(), thirdSportMarket.getAddition2(), thirdSportMarket.getAddition3(), thirdSportMarket.getAddition4());
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.containsKey(marketCategoryId)) {
            thirdSportMarket.setMarketCategoryId(CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.get(marketCategoryId));
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_2.contains(marketCategoryId)) {
            String add1 = thirdSportMarket.getAddition1().contains("-") ? thirdSportMarket.getAddition1().replace("-", "") : "-" + thirdSportMarket.getAddition1();
            thirdSportMarket.setAddition1(add1);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_3.contains(marketCategoryId)) {
            String add2 = thirdSportMarket.getAddition2().contains("-") ? thirdSportMarket.getAddition2().replace("-", "") : "-" + thirdSportMarket.getAddition2();
            thirdSportMarket.setAddition2(add2);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_4.contains(marketCategoryId)) {
            String add3 = thirdSportMarket.getAddition3();
            String add4 = thirdSportMarket.getAddition4();
            thirdSportMarket.setAddition3(add4);
            thirdSportMarket.setAddition4(add3);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_6.contains(marketCategoryId)) {
            String add1 = thirdSportMarket.getAddition1();
            String add2 = thirdSportMarket.getAddition2();
            thirdSportMarket.setAddition1(add2);
            thirdSportMarket.setAddition2(add1);
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_10.contains(marketCategoryId)) {
            String add3 = thirdSportMarket.getAddition3();
            String add4 = thirdSportMarket.getAddition4();
            thirdSportMarket.setAddition3(add4);
            thirdSportMarket.setAddition4(add3);
        }
    }

    /**
     * 提前结算主客相反改变投注项内容
     *
     * @param linkId
     * @param thirdMarketOddsPreResultDTOS
     * @param thirdMarket
     */

    public void changePreResultThirdMarketOddsContent(String linkId, List<ThirdMarketOddsPreResultDTO> thirdMarketOddsPreResultDTOS, ThirdMarketPreResultDTO thirdMarket, String dataSourceCode) {
        Map<String, String> thirdTemplateSourceIdMap = new HashMap<>();
        for (ThirdMarketOddsPreResultDTO thirdSportMarketOdds : thirdMarketOddsPreResultDTOS) {
            thirdTemplateSourceIdMap.put(thirdSportMarketOdds.getOddsType(), thirdSportMarketOdds.getThirdOddsFieldSourceId());
        }
        if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_1.containsKey(thirdMarket.getMarketCategoryId())) {
            List<ThirdMarketCategoryFieldDetail> thirdMarketCategoryFieldDetails = thirdMarketCategoryFieldService.queryThirdMarketCategoryFieldDetail(dataSourceCode, thirdMarket.getMarketCategoryId());
            if (!CollectionUtils.isEmpty(thirdMarketCategoryFieldDetails)) {
                Map<String, Long> longMap = thirdMarketCategoryFieldDetails.stream().collect(
                        Collectors.toMap(ThirdMarketCategoryFieldDetail::getOddsName, ThirdMarketCategoryFieldDetail::getReferenceId));
                Map<String, String> stringMap = thirdMarketCategoryFieldDetails.stream().collect(
                        Collectors.toMap(ThirdMarketCategoryFieldDetail::getOddsName, ThirdMarketCategoryFieldDetail::getThirdSourceId));
                for (ThirdMarketOddsPreResultDTO thirdSportMarketOdds : thirdMarketOddsPreResultDTOS) {
                    thirdSportMarketOdds.setThirdOddsFieldSourceId(stringMap.get(thirdSportMarketOdds.getOddsType().toLowerCase()));
                }
            }
        }
        for (ThirdMarketOddsPreResultDTO thirdSportMarketOdds : thirdMarketOddsPreResultDTOS) {
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_7.contains(thirdMarket.getMarketCategoryId())) {
                String add1 = thirdSportMarketOdds.getAddition1();
                String add2 = thirdSportMarketOdds.getAddition2();
                thirdSportMarketOdds.setAddition1(add2);
                thirdSportMarketOdds.setAddition2(add1);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_8.contains(thirdMarket.getMarketCategoryId())) {
                String add3 = thirdSportMarketOdds.getAddition3();
                String add4 = thirdSportMarketOdds.getAddition4();
                thirdSportMarketOdds.setAddition3(add4);
                thirdSportMarketOdds.setAddition4(add3);
            }
            if (CategoryOppositeConfig.FootBall.CATEGORY_TYPE_5.contains(thirdMarket.getMarketCategoryId())) {
                if (thirdMarket.getMarketCategoryId() == 104L) {
                    if (CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE_104.containsKey(thirdSportMarketOdds.getOddsType())) {
                        String oddsType = thirdSportMarketOdds.getOddsType();
                        thirdSportMarketOdds.setOddsType(CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE_104.get(oddsType));
                        thirdSportMarketOdds.setThirdOddsFieldSourceId(thirdTemplateSourceIdMap.get(thirdSportMarketOdds.getOddsType()));
                    }
                } else if (thirdMarket.getMarketCategoryId() == 103L) {
                    String str1 = (thirdSportMarketOdds.getAddition1() == null || thirdSportMarketOdds.getAddition1().contains("+")) ? thirdSportMarketOdds.getAddition1() : thirdSportMarketOdds.getAddition1() + ":" + thirdSportMarketOdds.getAddition2();
                    String str2 = (thirdSportMarketOdds.getAddition3() == null || thirdSportMarketOdds.getAddition3().contains("+")) ? thirdSportMarketOdds.getAddition3() : thirdSportMarketOdds.getAddition3() + ":" + thirdSportMarketOdds.getAddition4();
                    thirdSportMarketOdds.setOddsType(str1 + " " + str2);
                    thirdSportMarketOdds.setThirdOddsFieldSourceId(thirdTemplateSourceIdMap.get(thirdSportMarketOdds.getOddsType()));
                } else {
                    if (CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE.containsKey(thirdSportMarketOdds.getOddsType())) {
                        String oddsType = thirdSportMarketOdds.getOddsType();
                        thirdSportMarketOdds.setOddsType(CategoryOppositeConfig.FootBall.CATEGORY_ODDS_TYPE_CHANGE.get(oddsType));
                        thirdSportMarketOdds.setThirdOddsFieldSourceId(thirdTemplateSourceIdMap.get(thirdSportMarketOdds.getOddsType()));
                    } else {
                        if (thirdSportMarketOdds.getOddsType().contains(":")) {
                            String[] strArr = thirdSportMarketOdds.getOddsType().split(":");
                            if (strArr.length == 2) {
                                thirdSportMarketOdds.setOddsType(strArr[1] + ":" + strArr[0]);
                                thirdSportMarketOdds.setThirdOddsFieldSourceId(thirdTemplateSourceIdMap.get(thirdSportMarketOdds.getOddsType()));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 查询spread
     *
     * @return
     */
    private Double getMargin(String linkId, StandardMatchInfo standardMatchInfo, Long marketCategoryId, Long childMarketCategoryId, Integer placeNum) {
        Double spread = 0D;
        if (!MarginCategoryConfig.FootBall_MY_CATEGORY.contains(marketCategoryId)) {
            return spread;
        }
        ConfigMarketCategoryMargin configMarketCategoryMarginOne = configMarketCategoryMarginService.getItemTwo(linkId, standardMatchInfo.getId(), marketCategoryId, childMarketCategoryId, placeNum == null ? 1 : placeNum);
        if (null == configMarketCategoryMarginOne) {
            configMarketCategoryMarginOne = configMarketCategoryMarginService.getItemTwo(linkId, standardMatchInfo.getId(), marketCategoryId, marketCategoryId, 1);
        }
        if (null != configMarketCategoryMarginOne) {
            spread = configMarketCategoryMarginOne.getMargin();
        }
        return spread;
    }
}
