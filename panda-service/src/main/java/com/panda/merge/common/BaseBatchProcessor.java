package com.panda.merge.common;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.panda.merge.common.enums.*;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.dto.message.StandardMatchMarketPreResultMessage;
import com.panda.merge.exception.ApiException;
import com.panda.merge.model.*;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.panda.merge.config.RedisConfig.REDIS_FIVE_MINS_TIME;
import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 数据处理公共方法
 * @author  tell
 * @since   2020年9月3日14:17:58
 * */

@Slf4j
@Component
public class BaseBatchProcessor {

    @Autowired
    public RedisService redisService;
    @Autowired
    public LanguageInternationService languageInternationService;
    @Autowired
    public ThirdSportRegionService thirdSportRegionService;
    @Autowired
    public SystemItemDictService systemItemDictService;
    @Autowired
    public ThirdSportTypeService thirdSportTypeService;
    @Autowired
    public DataSourceService dataSourceService;
    @Autowired
    public LanguageTypeService languageTypeService;
    @Autowired
    private MarketCategorySellService marketCategorySellService;
    @Autowired
    private ConfigMarketAutoDiffTradeService configMarketAutoDiffTradeService;
    @Autowired
    private ConfigPlaceNumAutoDiffTradeService configPlaceNumAutoDiffTradeService;
    @Autowired
    private ConfigCategoryAutoDiffTradeService configCategoryAutoDiffTradeService;
    @Autowired
    private ConfigMarketHeadGapService headGapService;
    @Autowired
    public ConfigMarketMarginGapService configMarketMarginGapService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private StandardSportMarketSellService sportMarketSellService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private RocketMQTemplate rocketMqTemplate;
    @Autowired
    private ConfigMarketOddsStatusService configMarketOddsStatusService;
    @Autowired
    private ConfigCashOutTradeItemService configCashOutTradeItemService;

    public boolean supportA99(String linkId,Long matchId,Integer marketType,Long categoryId){
/*        String key = marketType==1?Constant.REDIS_KEY.RONGHE_A99_PRE_MATCH_IDS:Constant.REDIS_KEY.RONGHE_A99_LIVE_MATCH_IDS;
        Map<String, Object> map = redisService.hGetAll(key);
        Set<String> matchSet = map.keySet();
        Set<Long> set = matchSet.stream()
                .map(Long::valueOf)
                .collect(Collectors.toSet());
        log.info("::{}::supportA99 matchId:{},marketType:{},categoryId:{},map:{},contion1:{}", linkId,matchId, marketType, categoryId,map,(!set.contains(matchId)));

        if (!set.contains(matchId)){
            return false;
        }
        Object categoryStrs = map.get(matchId.toString());
        log.info("::{}::supportA99 matchId:{},marketType:{},categoryId:{},map:{},contion2:{}", linkId,matchId, marketType, categoryId,map,categoryStrs==null);

        if (categoryStrs==null){
            return false;
        }
        String[] categoryArrs = categoryStrs.toString().split(",");
        log.info("::{}::supportA99 matchId:{},marketType:{},categoryId:{},map:{},contion3:{}", linkId,matchId, marketType, categoryId,map,(categoryArrs==null||categoryArrs.length==0));

        if (categoryArrs==null||categoryArrs.length==0){
            return false;
        }
        for (String cat : categoryArrs){
            log.info("::{}::supportA99 matchId:{},marketType:{},categoryId:{},cat:{},contion4:{}", linkId,matchId, marketType, categoryId,cat,(MarginCategoryConfig.A99_category.containsKey(cat) && MarginCategoryConfig.A99_category.get(cat).contains(categoryId)));
            if (MarginCategoryConfig.A99_category.containsKey(cat) && MarginCategoryConfig.A99_category.get(cat).contains(categoryId)){
                return true;
            }
        }*/
        return false;
    }
    /**
     * 校验三方运动类型是否合法 并返回合法数据
     * @param  distinctRequests       数据来源
     * @return Map<String, Long>   三方运动类型和标准运动类型关系
     * */
    public List<OddsWrapper<ThirdMatchMarketDTO>> validateSportId(Map<String, Request<ThirdMatchMarketDTO>> distinctRequests){
        List<Request<ThirdMatchMarketDTO>> validatedRequests = new ArrayList<>();

        Map<String, List<Request<ThirdMatchMarketDTO>>> validRequests = distinctRequests.values().stream().filter(t->{
            ThirdMatchMarketDTO marketDTO = t.getData();
            if(marketDTO.getSportId() == null){
                return false;
            }
            if(DataSourceCodeEnum.getCodeList().contains(marketDTO.getDataSourceCode())){
                validatedRequests.add(t);
                return false;
            }
            return true;
        }).collect(Collectors.groupingBy(t->t.getData().getDataSourceCode()));

        Map<String, ThirdSportType> thirdSportId2Item = thirdSportTypeService.batchGetThirdSportId2Item(validRequests.keySet());

        validRequests = validRequests.values().stream().flatMap(t->t.stream()).filter(t->{
            ThirdMatchMarketDTO marketDTO = t.getData();
            String key = marketDTO.getDataSourceCode()+"-"+marketDTO.getSportId();
            if(thirdSportId2Item.containsKey(key)){
                validatedRequests.add(t);
                return false;
            }
            return true;
        }).collect(Collectors.groupingBy(t->t.getData().getDataSourceCode()));

        Map<String, DataSource> dataSourceMap = dataSourceService.batchGetItemByCode(validRequests.keySet());
        validRequests.entrySet().stream().forEach(t->{
            if (dataSourceMap.containsKey(t.getKey())){
                DataSource dataSource = dataSourceMap.get(t.getKey());
                if(!ONE.equals(dataSource.getCommerce())){
                    validatedRequests.addAll(t.getValue());
                }
            }
        });
        return validatedRequests.stream().map(t->{
            OddsWrapper<ThirdMatchMarketDTO> oddsWrapper = new OddsWrapper<>();
            ThirdMatchMarketDTO dto = t.getData();
            oddsWrapper.setData(dto);
            oddsWrapper.setLinkId(t.getLinkId());
            oddsWrapper.setDataSourceTime(t.getDataSourceTime());
            oddsWrapper.setDataSourceCode(dto.getDataSourceCode());
            oddsWrapper.setThirdMatchSourceId(dto.getThirdMatchSourceId());
            oddsWrapper.setMarketType(dto.getMarketList().get(0).getMarketType());
            oddsWrapper.setSportId(dto.getSportId());
            return oddsWrapper;
        }).collect(Collectors.toList());
    }

    /**
     * 根据配置的几阶段，获取该玩法的准确关盘时间
     * @param marketCategorySell
     * @param sportId
     * @return
     */
    private Integer getCloseTimeByMarketCategorySellRec(MarketCategorySell marketCategorySell,Long sportId){
        //篮球直接返回配置中该节的剩余时间，在掉用此方法前已经判断过阶段，因此不需要再计算已经过去的阶段时间
        if(StandardSportTypeEnum.Basketball.code.equals(sportId)){
            return marketCategorySell.getMatchProgressTime();
        }
        //开始处理足球
        Integer closeTime = 0;
        if(marketCategorySell.getInjuryTime() != null && marketCategorySell.getInjuryTime() != 0){
            List<Long> matchPeriod =  SportPeriodWholeEnum.getSprotPeriodBySportId(sportId).getPeriods();
            int index = matchPeriod.indexOf(Long.valueOf(marketCategorySell.getAutoCloseMarket().toString()));
            index = index > 3 ? 3 : index;
            closeTime += periodTimeIntegerValue.get(index);
            closeTime += marketCategorySell.getInjuryTime();
        }else{
            closeTime += marketCategorySell.getMatchProgressTime();
        }
        return closeTime;
    }

    /**
     * 需求1852兜底 阶段切换时，关闭上个阶段玩法的盘口
     * @param linkId
     * @param standardMatchInfo 标准赛事
     */
    public Set<Long> getAutoCloseBeforePeriodCategory(String linkId, StandardMatchInfo standardMatchInfo, Long nowPeriod){
        Long sportId = standardMatchInfo.getSportId();
        if (!StandardSportTypeEnum.FootBall.code.equals(sportId)) {
            log.info("::{}::getAutoCloseBeforePeriodCategory 赛种种类不匹配,兜底阶段:{},赛种种类:{}", linkId, nowPeriod, sportId);
            return null;
        }
        Long matchPeriodId = (Long) redisService.get(Constant.REDIS_KEY.RONGHE_MATCH_CURRENT_PERIODID+standardMatchInfo.getId());
        if(matchPeriodId == null || matchPeriodId != nowPeriod) {
            redisService.set(Constant.REDIS_KEY.RONGHE_MATCH_CURRENT_PERIODID+standardMatchInfo.getId(),nowPeriod,marketCacheTime(standardMatchInfo.getBeginTime()));
        }else {
            log.info("::{}::getAutoCloseBeforePeriodCategory 阶段没有变化,当前阶段:{},历史阶段:{}", linkId, nowPeriod, matchPeriodId);
            return null;
        }
        //查询当前阶段需要兜底关闭的玩法
        List<Long> categoryIds = MarginCategoryConfig.MATCH_PERIOD_CLOS_CATEGORY.get(nowPeriod);
        if (CollectionUtils.isEmpty(categoryIds)) {
            log.info("::{}::getAutoCloseBeforePeriodCategory 当前阶段没有兜底关盘的玩法,当前阶段:{}", linkId, nowPeriod);
            return null;
        }
        return new HashSet(categoryIds);
    }

    /**
     * 验证参数的合法性，缓存是否重复
     * @param serviceType    类型标识
     * @param request        请求参数
     */
    public void validateLinkId(String serviceType, Request request) {
        String key = RedisConfig.REDIS_KEY_LINKID + serviceType + FIX +request.getLinkId();
        if(!redisService.tryLockOnce(key,key,REDIS_FIVE_MINS_TIME)){
            throw new ApiException("参数linkID重复:" + request.getLinkId());
        }else{
            //类型标识存入请求中，方便在异常中释放锁
            request.setDataType(serviceType);
        }
    }

    /**
     * 盘口缓存时间
     * （比赛时间 - 系统时间） + 一周时间
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
     * 清除盘口水差、玩法水差、坑位水差，篮球的话还有盘口差
     * 清理独赢配置 清概率差，水差
     * @param linkId
     * @param standardMatchId
     * @param categoryList
     * @param sportId
     */
    public void delDiffByMatchIdAndCategoryList(String linkId, Long standardMatchId, List<Long> categoryList, Integer sportId) {
        if (CollectionUtils.isEmpty(categoryList)) {
            return;
        }
        if (StandardSportTypeEnum.FootBall.code.equals(sportId.longValue())) {
            ThirdMatchInfo aoMatchInfo = thirdMatchInfoService.getItem(standardMatchId, DataSourceCodeEnum.AO.code);
            if (null != aoMatchInfo) {
                sendClearAoDiffConfig(linkId, standardMatchId, aoMatchInfo.getThirdMatchSourceId(), categoryList);
            }
        }
        log.info("::{}::处理清除水差delDiffByMatchIdAndCategoryList,开始处理", linkId);
        CompletableFuture c1 = CompletableFuture.runAsync(() -> {
            configMarketAutoDiffTradeService.delDiffByMatchIdAndCategoryList(linkId, standardMatchId, categoryList);
        });
        CompletableFuture c2 = CompletableFuture.runAsync(() -> {
            headGapService.delCacheByCategoryIdList(linkId, standardMatchId, categoryList);
        });
        CompletableFuture c3 = CompletableFuture.runAsync(() -> {
            configCategoryAutoDiffTradeService.delDiffByMatchIdAndCategoryList(linkId, standardMatchId, categoryList);
        });
        CompletableFuture c4 = CompletableFuture.runAsync(() -> {
            configPlaceNumAutoDiffTradeService.delDiffByMatchIdAndCategoryList(linkId, standardMatchId, categoryList);
        });
        CompletableFuture c6 = CompletableFuture.runAsync(() -> {
            List<Long> thanThreeCategoryIds = getMoreCategoryId(categoryList, true);
            if (!CollectionUtils.isEmpty(thanThreeCategoryIds)) {
                configMarketMarginGapService.upProbabilityByMatchIdAndCategoryIdList(linkId, standardMatchId, thanThreeCategoryIds);
            }
        });
        CompletableFuture c7 = CompletableFuture.runAsync(() -> {
            List<Long> otherCategoryIds = getMoreCategoryId(categoryList, false);
            if (!CollectionUtils.isEmpty(otherCategoryIds)) {
                configMarketMarginGapService.updateByMatchIdAndCategoryList(linkId, standardMatchId, otherCategoryIds);
            }
        });
        CompletableFuture.allOf(c1, c2, c3, c4, c6, c7);
        log.info("::{}::处理清除水差delDiffByMatchIdAndCategoryList,处理完成", linkId);
    }

    public List<Long> getMoreCategoryId(List<Long> categoryList, boolean isTrue) {
        //大于三项盘玩法 清除概率差 ,其他玩法 清除清概率差，水差
        List<Long> thanThreeCategoryIds = Collections.synchronizedList(new ArrayList());
        List<Long> otherCategoryIds = Collections.synchronizedList(new ArrayList());
        categoryList.forEach(categoryId -> {
            if (MarginCategoryConfig.THREE_CATEGORY.contains(categoryId)) {
                thanThreeCategoryIds.add(categoryId);
            } else {
                otherCategoryIds.add(categoryId);
            }
        });
        return isTrue ? thanThreeCategoryIds : otherCategoryIds;
    }

    /**
     * 滚球切换 清除盘口水差、玩法水差、坑位水差 ，清除盘口差
     * 清理独赢配置 清概率差，水差
     * @param linkId
     * @param standardMatchId
     * @param sportId
     */
    public void delDiffByMatchInfoId(String linkId, Long standardMatchId, Long sportId) {
        if (StandardSportTypeEnum.FootBall.code.equals(sportId)) {
            ThirdMatchInfo aoMatchInfo = thirdMatchInfoService.getItem(standardMatchId, DataSourceCodeEnum.AO.code);
            if (null != aoMatchInfo) {
                sendClearAoDiffConfig(linkId, standardMatchId, aoMatchInfo.getThirdMatchSourceId(), null);
            }
        }
        configMarketAutoDiffTradeService.delDiffByMatchInfoId(standardMatchId, linkId);
        headGapService.delCacheByStandardMatchInfoId(standardMatchId, linkId);
        configCategoryAutoDiffTradeService.delDiffByMatchInfoId(standardMatchId, linkId);
        configPlaceNumAutoDiffTradeService.delDiffByMatchInfoId(standardMatchId, linkId);
        configMarketMarginGapService.updateByMatchId(linkId, standardMatchId);
    }

    /**
     * TX统一盘口ID 为 sendData 其他数据源为  relationMarketId
     * 统一转为 relationMarketId
     *
     * @param linkId
     * @param standardMarketDataMessage
     * @return
     */
    public Long convertRelationMarketId(String linkId, StandardMarketDataMessage standardMarketDataMessage) {
        Long relationMarketId = 0L;
        try {
            if (standardMarketDataMessage.getDataSourceCode().equals(DataSourceCodeEnum.TX.code) && StringUtils.isNotBlank(standardMarketDataMessage.getSendData())) {
                relationMarketId = Long.valueOf(standardMarketDataMessage.getSendData());
            } else {
                relationMarketId = standardMarketDataMessage.getRelationMarketId();
            }

        } catch (Exception e) {
            relationMarketId = standardMarketDataMessage.getRelationMarketId();
            log.info("::{}::三方盘口数据源ID:{},TX统一盘口ID转换失败,", linkId, standardMarketDataMessage.getThirdMarketSourceId(), e);
        }
        return relationMarketId;
    }

    /**
     * 获取PA数据服务日志对象
     * @param linkId       线路ID
     * @param serviceType  服务类型
     * @param apiCode      接口编码
     * @param apiName      接口名称
     * @param consumeTime  消耗时间（毫秒）
     * @param errorCode    错误编码
     * @param message      描述
     * */
    public PaDataServiceLogDTO getPaDataServiceLogDTO(String linkId,String serviceType,String apiCode,String apiName,Long consumeTime,Integer errorCode,String message){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        PaDataServiceLogDTO item = new PaDataServiceLogDTO();
        item.setLinkId(linkId);
        item.setDate(Long.valueOf(sdf.format(new Date())));
        item.setServiceType(serviceType);
        item.setApiCode(apiCode);
        item.setApiName(apiName);
        item.setConsumeTime(consumeTime);
        item.setErrorCode(errorCode);
        item.setMessage(message);
        return item;
    }

//    public static void main(String[] args) {
//        processOddsValueDecimals(">>", 1900000);
//    }
    /**
     * margin原始赔率和概率赔率小数点处理
     *
     * @param linkId
     * @param oddsValue
     * @return
     */
    public static Integer processOddsValueDecimals(String linkId, Integer oddsValue) {
        Integer paOddsValue = 0;
        if (null == oddsValue || 0 == oddsValue) {
            return paOddsValue;
        }
        BigDecimal bigDecimal = new BigDecimal(oddsValue).divide(new BigDecimal(100000), 2, BigDecimal.ROUND_DOWN);
        int left = bigDecimal.intValue();
        int right = bigDecimal.subtract(new BigDecimal(left)).multiply(new BigDecimal(100)).intValue();
        if (left < 3){
            paOddsValue = oddsValue;
        }
        else if(left >=3 && left < 5)
        {
            if(right < 5){
                paOddsValue = bigDecimal.intValue() * 100000;
            }else{
                BigDecimal divide = new BigDecimal(right).divide(new BigDecimal(5), 0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(0.05));
                paOddsValue = new BigDecimal(left).add(divide).multiply(new BigDecimal(100000)).intValue();
            }
        }
        else if(left >= 5 && left < 10)
        {
            if(right < 10){
                paOddsValue = bigDecimal.intValue() * 100000;
            }else{
                BigDecimal divide = new BigDecimal(right).divide(new BigDecimal(100), 1, BigDecimal.ROUND_DOWN);
                paOddsValue = new BigDecimal(left).add(divide).multiply(new BigDecimal(100000)).intValue();
            }
        }
        else if(left >=10 && left < 20)
        {
            if(right < 50){
                paOddsValue = bigDecimal.intValue() * 100000;
            }else{
                paOddsValue = new BigDecimal(left).add(new BigDecimal(0.5)).multiply(new BigDecimal(100000)).intValue();
            }
        }
        else if(left >= 20)
        {
            paOddsValue = left * 100000;
        }
        log.info("::{}::赔率小数点处理,oddsValue:{},paOddsValue:{}",linkId,oddsValue,paOddsValue);
        return paOddsValue;
    }

    /**
     * 主客队对调（如果三方赛事主客队和标准赛事主客队相反，则事件中主客队相关数据需要对调位置，比如比分）
     * @param matchEventInfo  需要转换的事件
     * @param thirdMatchInfo  当前事件列表对应的三方赛事信息
     * */
    public MatchEventInfo matchHomeAwayExchange(MatchEventInfo matchEventInfo,ThirdMatchInfo thirdMatchInfo){
        return matchHomeAwayExchange(Lists.newArrayList(matchEventInfo),thirdMatchInfo).get(0);
    }

    /**
     * 主客队对调（如果三方赛事主客队和标准赛事主客队相反，则事件中主客队相关数据需要对调位置，比如比分）
     * @param matchEventInfos 需要转换的事件列表
     * @param thirdMatchInfo  当前事件列表对应的三方赛事信息
     * */
    public List<MatchEventInfo> matchHomeAwayExchange(List<MatchEventInfo> matchEventInfos,ThirdMatchInfo thirdMatchInfo){
        //目前只处理足球
        if(StandardSportTypeEnum.FootBall.getCode().equals(thirdMatchInfo.getSportId())){
            //如果主客队是相反
            if(ONE.equals(thirdMatchInfo.getHomeAwayOpposite())){
                LinkedList<MatchEventInfo> list = new LinkedList<>();
                for (MatchEventInfo matchEventInfo: matchEventInfos) {
                    MatchEventInfo item = new MatchEventInfo();
                    BeanUtil.copyProperties(matchEventInfo, item);
                    //主客队标识互换
                    item.setHomeAway(TeamTypeEnum.homeAwayExchange(item.getHomeAway()));
                    //主客队比分互换
                    Integer t1 = item.getT1() == null ? ZERO:item.getT1();
                    Integer t2 = item.getT2() == null ? ZERO:item.getT2();
                    if(!t1.equals(t2)){
                        item.setT1(t2);
                        item.setT2(t1);
                    }
                    list.add(item);
                }
                return list;
            }
        }
        return matchEventInfos;
    }

    /**
     * 标准赛事开赛时间刷入缓存，到开赛时间下发滚球标识
     *
     * @param linkId
     * @param item
     */
    public void refreshStandardMatchBeginTimeByThirdMatchInfo(String linkId, ThirdMatchInfo item) {
        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(item.getReferenceId());
        if (standardMatchInfo != null) {
            //只处理赛前
            Object marketTypeObj = redisService.get(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchInfo.getId());
            //标准赛事是否已经下发过自动构建赔率key
            String key = Constant.REDIS_KEY.RONGHE_STANDARD_PER_MARKET + standardMatchInfo.getId();
            if (Objects.isNull(marketTypeObj) && Objects.isNull(redisService.get(key))) {
                Long standardMatchId = standardMatchInfo.getId();
                StandardSportMarketSell sportMarketSellServiceItem = sportMarketSellService.refreshCache(standardMatchId);
                if (sportMarketSellServiceItem != null) {
                    //标准赛事 主赛事状态源
                    String matchStatusSourceCode = sportMarketSellServiceItem.getMatchStatusSourceCode();
                    if (StringUtils.equals(item.getDataSourceCode(), matchStatusSourceCode)) {
                        String matchBeginStr = Constant.REDIS_KEY.RONGHE_THIRD_PER_MARKET;
                        if (TimeUtils.timeCalendar(item.getBeginTime())) {
                            log.info("::{}::推送三方赛事给下游缓存标准赛事开赛时间,标准赛事ID:{},开赛时间:{},开售赛事状态源:{}",
                                    linkId, standardMatchInfo.getId(), item.getBeginTime(), matchStatusSourceCode);
                            String updatedKey = redisService.genNewHashKey(matchBeginStr, standardMatchInfo.getId().toString(), ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
                            redisService.hSet(updatedKey, standardMatchInfo.getId().toString(), item.getBeginTime(),marketCacheTime(item.getBeginTime()));
                        }else{
                            log.info("::{}::推送三方赛事给下游缓存标准赛事开赛时间,大于7天后时间不入缓存,标准赛事ID:{},开赛时间:{},开售赛事状态源:{}",
                                    linkId, standardMatchInfo.getId(), item.getBeginTime(), matchStatusSourceCode);
                        }
                    } else {
                        log.info("::{}::推送三方赛事给下游缓存标准赛事开赛时间,与设置的主赛事状态源不匹配,标准赛事ID:{},设置赛事状态源:{},三方状态源:{}",
                                linkId, standardMatchInfo.getId(), matchStatusSourceCode, item.getDataSourceCode());
                    }
                } else {
                    log.info("::{}::推送三方赛事给下游缓存标准赛事开赛时间,标准赛事未开售,标准赛事ID:{}",
                            linkId, standardMatchInfo.getId());
                }
            } else {
                log.info("::{}::推送三方赛事给下游缓存标准赛事开赛时间,已下发过滚球标识或已下发过自动构建盘口,标准赛事ID:{}",
                        linkId, standardMatchInfo.getId());
            }
        }
    }

    /**
     * 标准赛事开赛时间刷入缓存，到开赛时间下发滚球标识
     *
     * @param linkId
     * @param standardMatchInfo
     */
    public void refreshStandardMatchBeginTimeByMatchId(String linkId, StandardMatchInfo standardMatchInfo) {
        //只处理赛前
        Object marketTypeObj = redisService.get(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchInfo.getId());
        //标准赛事是否已经下发过自动构建赔率key
        String key = Constant.REDIS_KEY.RONGHE_STANDARD_PER_MARKET + standardMatchInfo.getId();
        if (Objects.isNull(marketTypeObj) && Objects.isNull(redisService.get(key))) {
            StandardSportMarketSell sportMarketSellServiceItem = sportMarketSellService.getItem(standardMatchInfo.getId());
            if (sportMarketSellServiceItem != null) {
                //标准赛事查库
                StandardMatchInfo refreshStandardMatchInfo = standardMatchInfoService.getItemByPrimaryKey(standardMatchInfo.getId());
                //标识赛事开售 赛事状态源
                String matchBeginStr = Constant.REDIS_KEY.RONGHE_THIRD_PER_MARKET;
                if (TimeUtils.timeCalendar(refreshStandardMatchInfo.getBeginTime())) {
                    log.info("::{}::模板缓存标准赛事开赛时间,标准赛事ID:{},开赛时间:{},赛事状态源:{}",
                            linkId, standardMatchInfo.getId(), refreshStandardMatchInfo.getBeginTime(), sportMarketSellServiceItem.getMatchStatusSourceCode());
                    String updatedKey = redisService.genNewHashKey(matchBeginStr, standardMatchInfo.getId().toString(), ConstantSystem.BUCKET_QUANTITY_SIXTY_FOUR);
                    redisService.hSet(updatedKey, standardMatchInfo.getId().toString(), refreshStandardMatchInfo.getBeginTime(),marketCacheTime(refreshStandardMatchInfo.getBeginTime()));
                }else {
                    log.info("::{}::模板缓存标准赛事开赛时间,大于7天后时间不入缓存,标准赛事ID:{},开赛时间:{},赛事状态源:{}",
                            linkId, standardMatchInfo.getId(), standardMatchInfo.getBeginTime(), sportMarketSellServiceItem.getMatchStatusSourceCode());
                }
            } else {
                log.info("::{}::模板缓存标准赛事开赛时间,未开售,标准赛事ID:{}", linkId, standardMatchInfo.getId());
            }
        } else {
            log.info("::{}::模板缓存标准赛事开赛时间,已下发过滚球标识或已下发过自动构建盘口,标准赛事ID:{}",
                    linkId, standardMatchInfo.getId());
        }
    }

    /**
     * 查询缓存是否进入滚球
     *
     * @return
     */
    public int isOddsLive(Long standardMatchInfoId) {
        Object marketTypeObj = redisService.get(Constant.REDIS_KEY.RONGHE_STANDARD_MARKET_SWITCH_STATUS + standardMatchInfoId);
        return Objects.isNull(marketTypeObj) ? 1 : 0;
    }

    /**
     * AO 盘口水差/MARGIN 配置下发
     *
     * @param linkId
     * @param standardMatchInfo      标准赛事
     * @param marketDataMessageMap   标准盘口缓存
     * @param uiConfigDTO            ui接口 水差、margin参数
     * @param diffConfigList         水差集合接口
     * @param marketMarginConfigDTOS Margin接口、Margin集合接口
     */
    public List<TradeMarketDiffAndMarginConfigDTO> aoMarketDiffAndMarginConfig(String linkId, StandardMatchInfo standardMatchInfo, Map<String, StandardMarketDataMessage> marketDataMessageMap,
                                                                               TradeMarketUiConfigDTO uiConfigDTO, List<TradeMarketAutoDiffConfigItemDTO> diffConfigList, List<TradeMarketMarginConfigDTO> marketMarginConfigDTOS) {
        if (!StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId()) && !StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId())) {
            return null;
        }
        Long standardMatchInfoId = standardMatchInfo.getId();
        ThirdMatchInfo aoMatchInfo = thirdMatchInfoService.getItem(standardMatchInfoId, DataSourceCodeEnum.AO.code);
        if (null == aoMatchInfo) {
            return null;
        }
        try {
            List<TradeMarketDiffAndMarginConfigDTO> configList = new ArrayList<>();
            TradeMarketDiffAndMarginConfigDTO diffAndMarginConfigDTO = new TradeMarketDiffAndMarginConfigDTO();
            diffAndMarginConfigDTO.setStandardMatchInfoId(standardMatchInfo.getId());
            diffAndMarginConfigDTO.setAoMatchId(aoMatchInfo.getThirdMatchSourceId());
            diffAndMarginConfigDTO.setLinkId(linkId);

            //ui接口 水差、margin参数处理
            if (null != uiConfigDTO) {
                List<TradeMarketAutoDiffConfigItemDTO> diffConfigs = uiConfigDTO.getDiffConfigs();
                Long standardCategoryId = uiConfigDTO.getStandardCategoryId();
                diffAndMarginConfigDTO.setPlaceNum(uiConfigDTO.getPlaceNum());
                diffAndMarginConfigDTO.setMarketType(uiConfigDTO.getMarketType());
                diffAndMarginConfigDTO.setStandardCategoryId(uiConfigDTO.getStandardCategoryId());
                diffAndMarginConfigDTO.setChildStandardCategoryId(uiConfigDTO.getChildStandardCategoryId());
                //水差处理
                //marketDiffDealWith(diffConfigs, marketDataMessageMap, diffAndMarginConfigDTO);
                //margin
                if(!CollectionUtils.isEmpty(uiConfigDTO.getMarketMarginDtlDTOList())){
                    diffAndMarginConfigDTO.setMarketMarginDtlDTOList(uiConfigDTO.getMarketMarginDtlDTOList());
                }
                if (!CollectionUtils.isEmpty(uiConfigDTO.getMarginGapDtlDTOList())) {
                    List<MarketMarginDtlDTO> marketMarginDtlDTOList = new ArrayList<>();
                    List<MarketMarginGapDtlDTO> marginGapDtlDTOList = uiConfigDTO.getMarginGapDtlDTOList();
                    marginGapDtlDTOList.forEach(margin -> {
                        MarketMarginDtlDTO marginDtlDTO = new MarketMarginDtlDTO();
                        BeanUtils.copyProperties(margin, marginDtlDTO);
                        marketMarginDtlDTOList.add(marginDtlDTO);
                    });
                    diffAndMarginConfigDTO.setMarketMarginDtlDTOList(marketMarginDtlDTOList);
                }
                configList.add(diffAndMarginConfigDTO);
            }
            //水差接口、集合接口 处理
            if (!CollectionUtils.isEmpty(diffConfigList)) {
                //marketDiffDealWith(diffConfigList, marketDataMessageMap, diffAndMarginConfigDTO);
                configList.add(diffAndMarginConfigDTO);
            }
            //margin接口、集合接口 处理
            if (!CollectionUtils.isEmpty(marketMarginConfigDTOS)) {
                marketMarginConfigDTOS.forEach(marginConfigDTO -> {
                    Integer placeNum = marginConfigDTO.getPlaceNum();
                    Integer marketType = marginConfigDTO.getMarketType();
                    diffAndMarginConfigDTO.setPlaceNum(placeNum);
                    diffAndMarginConfigDTO.setMarketType(marketType);
                    diffAndMarginConfigDTO.setMarketMarginDtlDTOList(marginConfigDTO.getMarketMarginDtlDTOList());
                    configList.add(diffAndMarginConfigDTO);
                });
            }
            return configList;
        } catch (Exception e) {
            log.info("::{}::发送AO水差,margin配置异常:" + e, linkId);
        }
        return null;
    }

    /**
     * 处理水差
     */
    public TradeMarketDiffAndMarginConfigDTO marketDiffDealWith(List<TradeMarketAutoDiffConfigItemDTO> diffConfigs, Map<String, StandardMarketDataMessage> marketDataMessageMap,
                                                                TradeMarketDiffAndMarginConfigDTO diffAndMarginConfigDTO) {
        if (!CollectionUtils.isEmpty(diffConfigs)) {
            TradeMarketAutoDiffConfigItemDTO diffConfig = diffConfigs.stream().filter(m -> m.getDiffValue() != 0).findFirst().orElse(null);
            if (null == diffConfig) {
                diffConfig = diffConfigs.get(0);
            }
            StandardMarketDataMessage marketDataMessage = marketDataMessageMap.get(String.valueOf(diffConfig.getMarketId()));
            if (null != marketDataMessage) {
                diffAndMarginConfigDTO.setAddition1(marketDataMessage.getAddition1());
            }
            diffAndMarginConfigDTO.setDiffConfigs(diffConfig);
        }
        return diffAndMarginConfigDTO;
    }

    /**
     * 赛事切换 玩法切换 清除AO配置
     *
     * @param linkId
     * @param standardMatchId
     * @param thirdMatchSourceId
     * @param categoryList
     */
    public void sendClearAoDiffConfig(String linkId, Long standardMatchId, String thirdMatchSourceId, List<Long> categoryList) {
        List<Long> categorys = new ArrayList<>();
        if (!CollectionUtils.isEmpty(categoryList)) {
            categorys = categoryList.stream()
                    .collect(Collectors.toMap(e -> e, e -> 1, Integer::sum))
                    .entrySet()
                    .stream()
                    .filter(e -> e.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(categorys)) {
                return;
            }
        }
        JSONObject obj = new JSONObject();
        obj.put("linkId", linkId);
        obj.put("standardMatchId", standardMatchId);
        obj.put("aoMatchId", thirdMatchSourceId);
        obj.put("categorys", categorys);
        MessageBuilder<JSONObject> builder = MessageBuilder.withPayload(obj).setHeader(MessageConst.PROPERTY_KEYS, linkId);
        rocketMqTemplate.asyncSend("AO_DIFF_CONFIG_CLEAR:" + standardMatchId, builder.build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("::{}::,AO_DIFF_CONFIG_CLEAR，send successful", linkId);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("::{}::TOPIC={}，send fail; ", linkId, "AO_MATCH_DIFF_CONFIG_CLEAR", throwable);
            }
        });
    }

    /**
     * AO icon状态
     *
     * @param linkId
     * @param standardMatchId
     * @param marketPreResultMessageList
     */
    public void aoMatchPreIconStatus(String linkId, Long standardMatchId, List<StandardMatchMarketPreResultMessage> marketPreResultMessageList) {
        int marketType = isOddsLive(standardMatchId);
        //赛事是否支持提前结算
        Integer matchPreStatusRisk = marketPreResultMessageList.get(0).getMatchPreStatusRisk();
        //icon 状态,默认关
        Integer iconStatus = 0;
        //获取系统级提前结算开关参数信息
        String SystemThirdMarketPreParams = Constant.REDIS_KEY.SYSTEM_THIRD_MARKET_PRE_PARAMS;
        Map<String, Integer> paramsMap = redisService.hGetAll(SystemThirdMarketPreParams);
        //赛事级别
        ConfigCashOutTradeItem configCashOutTradeItemRace = configCashOutTradeItemService.getItem(standardMatchId, marketType, 1);
        log.info("::{}::提前结算状态,赛事ID:{},类型:{},系统状态:{},赛事是否支持:{},赛事级别数据源:{}",
                linkId, standardMatchId, marketType, paramsMap, matchPreStatusRisk, JSONObject.toJSONString(configCashOutTradeItemRace));
        if (MapUtils.isNotEmpty(paramsMap) && null != configCashOutTradeItemRace) {
            if (matchPreStatusRisk == 1) {
                for (Object k : paramsMap.keySet()) {
                    Integer v = Integer.parseInt(paramsMap.get(k.toString()).toString());
                    if (k.equals(configCashOutTradeItemRace.getDataSourceCode())) {
                        if (v.equals(configCashOutTradeItemRace.getMatchPreStatus())) {
                            iconStatus = 1;
                        }
                    }
                }
            }
        }
        Integer finalIconStatus = iconStatus;
        marketPreResultMessageList.forEach(m -> {
            m.setMatchPreStatusRisk(finalIconStatus);
        });
        //最后兜底
        if (!CollectionUtils.isEmpty(paramsMap)) {
            int AoOnOff = paramsMap.get("AO");
            if (AoOnOff == 0) {
                marketPreResultMessageList.forEach(m -> {
                    m.setMatchPreStatusRisk(0);
                    m.setMatchPreStatus(0);
                });
            }
        }
    }

    /**
     * 初盘 三方盘口转换标准盘口
     *
     * @param thirdMarketDTO
     * @return
     */
    public StandardMarketDataMessage thirdConvertStandardMarket(ThirdMarketDTO thirdMarketDTO) {
        StandardMarketDataMessage standardMarketDataMessage = new StandardMarketDataMessage();
        BeanUtil.copyProperties(thirdMarketDTO, standardMarketDataMessage);
        List<ThirdMarketOddsDTO> marketOddsList = thirdMarketDTO.getMarketOddsList();
        if (CollectionUtils.isEmpty(marketOddsList)) {
            return null;
        }
        List<StandardMarketOddsDataMessage> standardMarketOddsDataMessages = new ArrayList<>();
        marketOddsList.forEach(thirdMarketOdds -> {
            StandardMarketOddsDataMessage standardMarketOddsDataMessage = new StandardMarketOddsDataMessage();
            BeanUtil.copyProperties(thirdMarketOdds, standardMarketOddsDataMessage);
            standardMarketOddsDataMessages.add(standardMarketOddsDataMessage);
        });
        standardMarketDataMessage.setMarketOddsList(standardMarketOddsDataMessages);
        return standardMarketDataMessage;
    }

}
