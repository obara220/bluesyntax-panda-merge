package com.panda.merge.dubbo;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.panda.merge.api.IOutrightTradeConfigApi;
import com.panda.merge.bo.OutrightMarketOddsBO;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.LanguageTypeEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.RedisConfig;
import com.panda.merge.dao.StandardSportMarketOddsDao;
import com.panda.merge.dto.*;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <Description> <br>
 *
 * @author raulvii<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2021/06/15 <br>
 * @see com.panda.merge.dubbo <br>
 */
@Slf4j
@Component
@DubboService
public class OutrightTradeConfigApiServiceImpl extends BaseProcessor implements IOutrightTradeConfigApi {

    private final static boolean isOutright = true;

    @Autowired
    private OutrightTradeTypeConfigService outrightTradeTypeConfigService;

    @Autowired
    private OutrightTradeMarketConfigService outrightTradeMarketConfigService;

    @Autowired
    private OutrightTradeOddsConfigService outrightTradeOddsConfigService;

    @Autowired
    private OutrightTradeProbabilityConfigService outrightTradeProbabilityConfigService;

    @Autowired
    private StandardOutrightMarketService outrightMarketService;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;

    @Autowired
    private StandardOutrightMatchInfoService standardOutrightMatchInfoService;

    @Autowired
    private ThirdOutrightMatchInfoService thirdOutrightMatchInfoService;

    @Autowired
    private ThirdSportMarketNewService thirdSportMarketNewService;

    @Autowired
    private ThirdSportMarketOddsNewService thirdSportMarketOddsNewService;

    @Autowired
    private StandardSportMarketNewService standardSportMarketNewService;

    @Autowired
    private StandardSportMarketOddsNewService standardSportMarketOddsService;

    @Autowired
    StandardSportMarketOddsDao standardSportMarketOddsDao;

    @Autowired
    private StandardSportMarketNewService standardSportMarketService;

    @Autowired
    private I18nOutrightMarketOddsService i18nOutrightMarketOddsService;

    @Autowired
    private StandardOutrightMarketService standardOutrightMarketService;

    final Map<String, String> marketResultMap = new HashMap<String, String>() {
        {
            put("0", "没结果");
            put("1", "不知道");
            put("2", "走水");
            put("3", "全输");
            put("4", "全赢");
            put("5", "赢半");
            put("6", "输半");
        }
    };

    public static ArrayList<String> initFivelanguage;

    static{
        initFivelanguage = Lists.newArrayList();
        Arrays.asList(LanguageTypeEnum.values()).stream().forEach(leagueTeamMatchLogEnum -> initFivelanguage.add(leagueTeamMatchLogEnum.name()) );
    }


    @Override
    public Response putOutrightTradeTypeConfig(Request<OutrightTradeTypeConfigDTO> request) {
        String linkId = request.getLinkId();
        if ( StringUtils.isEmpty(linkId) ) {
            linkId = UUID.randomUUID().toString();
        }
        log.info("::{}::putOutrightTradeTypeConfig: {}", linkId, JSON.toJSONString(request));
        validateLinkId("putOutrightTradeTypeConfig", request);
        OutrightTradeTypeConfigDTO configDTO = request.getData();
        ConfigOutrightTradeType configOutrightTradeType = outrightTradeTypeConfigService.selectItem(configDTO);
        Integer oldTradeType = null;
        Integer newTradeType = configDTO.getTradeType();
        if(null == configOutrightTradeType){
            outrightTradeTypeConfigService.insertItem(request.getLinkId(),configDTO);
        }else {
            oldTradeType = configOutrightTradeType.getTradeType();
            configOutrightTradeType.setTradeType(configDTO.getTradeType());
            configOutrightTradeType.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            outrightTradeTypeConfigService.updateItem(configOutrightTradeType);
        }
        //切操盘方式，清理概率差
        clearProbabilityValue(request.getLinkId(),configDTO.getStandardMatchId(),configDTO.getStandardMarketId());
        //获取标准赛事
        StandardMatchInfoDetail standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(isOutright, configDTO.getStandardMatchId());
        //获取标准赛事开售信息
        StandardSportMarketSell standardSportMarketSell = thirdMatchMarketProcessor.getStandardSportMarketSell(isOutright, standardMatchInfo.getId());
        //更新标准盘口数据
        StandardSportMarket standardSportMarket = standardSportMarketService.getMarketByRelationId(configDTO.getStandardMarketId());
        if ( null == standardSportMarket) {
            return Response.failed("调赔的标准盘口不存在");
        }
        standardSportMarket.setTradeType(configDTO.getTradeType());
        standardSportMarketService.updateByPrimaryKeySelective(standardSportMarket);
        //更新缓存中的盘口操盘方式
        String redisKey = Constant.REDIS_KEY.RONGHE_STANDARD_MARKET + standardMatchInfo.getId() + "_" + standardMatchInfo.getDataSourceCode();
        StandardMarketDataMessage standardMarketMessage = (StandardMarketDataMessage) redisService.hGet( redisKey, configDTO.getStandardMarketId().toString());
        if ( null != standardMarketMessage) {
            standardMarketMessage.setTradeType(configDTO.getTradeType());
            {
                standardMarketMessage.setNumberOfWinners(1);
            }
            redisService.hSet(redisKey, configDTO.getStandardMarketId().toString(), standardMarketMessage, RedisConfig.REDIS_YEAR_TIME);
        }

        if ( null != oldTradeType && Constant.OUTRIGHT_ONE.equals(oldTradeType) && Constant.OUTRIGHT_ZERO.equals(newTradeType) ) {
            revertOddsByTradeType( linkId, redisKey, configDTO.getStandardMatchId(), configDTO, standardMarketMessage);
        }
        //获取缓存中的所有盘口（赛前数据商和滚球数据商）
        Map<String, StandardMarketDataMessage> standardMarketDataMessageMap =
                thirdMatchMarketProcessor.getChampionStandardMarketDataMessageMap( request.getLinkId(), standardMatchInfo,  standardSportMarketSell);
        Set<Long> marketIdSet = new HashSet<>();
        marketIdSet.add(configDTO.getStandardMarketId());
        //异步处理
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo, marketIdSet,
                standardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
        return Response.success();
    }


    private void revertOddsByTradeType(String linkId, String redisKey, Long matchId, OutrightTradeTypeConfigDTO configDTO,
                                       StandardMarketDataMessage standardMarketMessage) {
        log.info("::{}::revertOddsByTradeType的入参, matchId:{}", linkId, matchId);
        StandardOutrightMatchInfo matchInfo =  standardOutrightMatchInfoService.getItem(matchId);
        if ( Objects.isNull(matchInfo) ) {
            log.info("::{}::revertOddsByTradeType的matchInfo为空", linkId);
            return;
        }
        if ( null == matchInfo.getThirdOutrightMatchId() ) {
            log.info("::{}::revertOddsByTradeType的三方赛事id为空", linkId);
            return;
        }
        ThirdOutrightMatchInfo thirdOutrightMatchInfo = thirdOutrightMatchInfoService.getItemByMatchId( matchId, matchInfo.getDataSourceCode());
        if ( Objects.isNull(thirdOutrightMatchInfo) ) {
            log.info("::{}::revertOddsByTradeType的三方赛事为空", linkId);
            return;
        }

        StandardSportMarket standardSportMarket =  standardSportMarketNewService.getMarketByRelationId(configDTO.getStandardMarketId());
        if ( Objects.isNull(standardSportMarket) ) {
            log.info("::{}::revertOddsByTradeType的标准盘口为空", linkId);
            return;
        }

        List<StandardSportMarketOdds> standardSportMarketOddsList = standardSportMarketOddsService.getItemList( standardSportMarket.getId());
        if ( CollectionUtils.isEmpty(standardSportMarketOddsList) ) {
            log.info("::{}::revertOddsByTradeType的标准赔率为空, marketId:{}", linkId, standardSportMarket.getId());
            return;
        }

        Map<String, StandardSportMarketOdds> standardOddsMap = standardSportMarketOddsList.stream()
                .collect(Collectors.toMap(StandardSportMarketOdds::getThirdOddsFieldSourceId, Function.identity()));

        // 三方盘口的获取
        ThirdSportMarket thirdSportMarket = thirdSportMarketNewService.getItem(thirdOutrightMatchInfo.getDataSourceCode(),
                standardSportMarket.getThirdMarketSourceId(), thirdOutrightMatchInfo.getId());
        if ( Objects.isNull(thirdSportMarket) ) {
            log.info("::{}::revertOddsByTradeType的三方盘口为空", linkId);
            return;
        }

        standardSportMarket.setAddition1(thirdSportMarket.getAddition1());
        standardSportMarket.setAddition2(thirdSportMarket.getAddition2());
        standardSportMarket.setAddition3(thirdSportMarket.getAddition3());
        standardSportMarket.setModifyTime(Calendar.getInstance().getTimeInMillis());
        standardMarketMessage.setAddition1(thirdSportMarket.getAddition1());
        standardMarketMessage.setAddition2(thirdSportMarket.getAddition2());
        standardMarketMessage.setAddition3(thirdSportMarket.getAddition3());

        List<ThirdSportMarketOdds> thirdSportMarketOdds = thirdSportMarketOddsNewService.getItemList( thirdOutrightMatchInfo.getDataSourceCode(), thirdSportMarket.getId());
        if ( CollectionUtils.isEmpty(thirdSportMarketOdds) ) {
            log.info("::{}::revertOddsByTradeType的三方赔率为空, marketId:{}", linkId, thirdSportMarket.getId());
            return;
        }

        List<StandardMarketOddsDataMessage> marketOddsList = standardMarketMessage.getMarketOddsList();
        if ( !CollectionUtils.isEmpty(marketOddsList) ) {
            for ( StandardMarketOddsDataMessage oddsDataMessage : marketOddsList ) {
                for (ThirdSportMarketOdds thirdOdds : thirdSportMarketOdds) {
                    if ( oddsDataMessage.getThirdOddsFieldSourceId().equals(thirdOdds.getThirdOddsFieldSourceId())) {
                        oddsDataMessage.setOddsValue( thirdOdds.getOddsValue());
                        oddsDataMessage.setPaOddsValue( thirdOdds.getOddsValue());
                        oddsDataMessage.setAddition1( thirdOdds.getAddition1());
                        oddsDataMessage.setAddition2( thirdOdds.getAddition2());
                        oddsDataMessage.setAddition3( thirdOdds.getAddition3());
                    }
                }
            }
        }

        if (  null == standardSportMarket.getNumberOfWinners() || standardSportMarket.getNumberOfWinners() < 1 )
        {
            standardSportMarket.setNumberOfWinners(1);
        }
        standardSportMarketNewService.updateByPrimaryKeySelective(standardSportMarket);

        if ( null == standardMarketMessage.getNumberOfWinners() || standardMarketMessage.getNumberOfWinners() < 1 )
        {
            standardMarketMessage.setNumberOfWinners(1);
        }
        standardMarketMessage.setMarketOddsList(marketOddsList);
        redisService.hSet(redisKey, configDTO.getStandardMarketId().toString(), standardMarketMessage, RedisConfig.REDIS_YEAR_TIME);

        List<StandardSportMarketOdds> addMarketOdds = Lists.newArrayList();
        for (ThirdSportMarketOdds thirdOdds : thirdSportMarketOdds) {
            if ( MapUtils.isNotEmpty(standardOddsMap) && standardOddsMap.containsKey(thirdOdds.getThirdOddsFieldSourceId()) ) {
                StandardSportMarketOdds standardMarketOdds = standardOddsMap.get(thirdOdds.getThirdOddsFieldSourceId());
                standardMarketOdds.setOddsValue( thirdOdds.getOddsValue());
                standardMarketOdds.setPaOddsValue( thirdOdds.getOddsValue());
                addMarketOdds.add(standardMarketOdds);
            }
        }

        if ( !CollectionUtils.isEmpty(addMarketOdds) ) {
            standardSportMarketOddsService.upStandardOddsList(linkId, matchId, addMarketOdds);
        }

    }

    @Override
    public Response putOutrightTradeMarketConfig(Request<OutrightTradeMarketConfigDTO> request) {
        log.info("::{}::putOutrightTradeMarketConfig: {}", request.getLinkId(), JSON.toJSONString(request));

        validateLinkId("putOutrightTradeMarketConfig", request);
        OutrightTradeMarketConfigDTO configDTO = request.getData();
        ConfigOutrightTradeMarket configOutrightTradeMarket = outrightTradeMarketConfigService.selectItem(configDTO.getStandardMatchId(),configDTO.getStandardMarketId());
        if(null == configOutrightTradeMarket){
            outrightTradeMarketConfigService.insertItem(request.getLinkId(), configDTO);
        }else{
            configOutrightTradeMarket.setMarketStatus(configDTO.getMarketStatus());
            configOutrightTradeMarket.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            configOutrightTradeMarket.setLinkId(request.getLinkId());
            Integer operateType = null == configDTO.getOperateType() ? Constant.OUTRIGHT_ONE : configDTO.getOperateType();
            configOutrightTradeMarket.setOperateType(operateType);
            outrightTradeMarketConfigService.updateItem(configOutrightTradeMarket);
        }
        StandardOutrightMarket standardOutrightMarket = standardOutrightMarketService.selectByExample(configDTO.getStandardMarketId());
        if (standardOutrightMarket == null ) {
            log.info("::{}::putOutrightTradeMarketConfig,冠军盘口不存在，冠军盘口id:{}", request.getLinkId(), configDTO.getStandardMarketId());
            return Response.failed("冠军盘口不存在!");
        }
        standardOutrightMarket.setMarketStatus(configDTO.getMarketStatus());
        standardOutrightMarket.setModfiyTime(TimeUtils.millsSecondsEast8ZoneGmt());
        log.info("::{}::putOutrightTradeMarketConfig,冠军盘口修改，冠军盘口参数:{}", request.getLinkId(), JSON.toJSONString(standardOutrightMarket));
        standardOutrightMarketService.updateByPrimaryKeySelective(standardOutrightMarket);
        //获取标准赛事
        StandardMatchInfoDetail standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(isOutright, configDTO.getStandardMatchId());
        //获取标准赛事开售信息
        StandardSportMarketSell standardSportMarketSell = thirdMatchMarketProcessor.getStandardSportMarketSell(isOutright, standardMatchInfo.getId());
        //获取缓存中的所有盘口（赛前数据商和滚球数据商）
        Map<String, StandardMarketDataMessage> standardMarketDataMessageMap =
                thirdMatchMarketProcessor.getChampionStandardMarketDataMessageMap( request.getLinkId(), standardMatchInfo,  standardSportMarketSell);
        Set<Long> marketIdSet = new HashSet<>();
        marketIdSet.add(configDTO.getStandardMarketId());
        //异步处理
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo, marketIdSet,
                standardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
        return Response.success();
    }

    @Override
    public Response putOutrightTradeOddsConfig(Request<OutrightTradeOddsConfigDTO> request) {
        log.info("::{}::putOutrightTradeOddsConfig: {}", request.getLinkId(), JSON.toJSONString(request));
        validateLinkId("putOutrightTradeOddsConfig", request);
        OutrightTradeOddsConfigDTO configDTO = request.getData();
        ConfigOutrightTradeOdds configOutrightTradeOdds = outrightTradeOddsConfigService.selectItem(configDTO.getStandardMatchId(),configDTO.getStandardMarketOddsId());
        if(null == configOutrightTradeOdds){
            outrightTradeOddsConfigService.insertItem(request.getLinkId(), configDTO);
        }else{
            configOutrightTradeOdds.setOddsStatus(configDTO.getOddsStatus());
            configOutrightTradeOdds.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            configOutrightTradeOdds.setLinkId(request.getLinkId());
            outrightTradeOddsConfigService.updateItem(configOutrightTradeOdds);
        }
        //获取标准赛事
        StandardMatchInfoDetail standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(isOutright, configDTO.getStandardMatchId());
        //获取标准赛事开售信息
        StandardSportMarketSell standardSportMarketSell = thirdMatchMarketProcessor.getStandardSportMarketSell(isOutright, standardMatchInfo.getId());
        //判断盘口是否存在与开售
        List<StandardOutrightMarket> outrightMarkets = outrightMarketService.selectOutrightMarketSellList(configDTO.getStandardMarketId());
        Map<String, StandardMarketDataMessage> standardMarketDataMessageMap =
                thirdMatchMarketProcessor.getChampionStandardMarketDataMessageMap( request.getLinkId(), standardMatchInfo,  standardSportMarketSell);
        Set<Long> marketIdSet = new HashSet<>();
        marketIdSet.add(configDTO.getStandardMarketId());
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(),request.getOperaterId(), standardMatchInfo, marketIdSet,standardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
        return Response.success();
    }

    @Override
    public Response putOutrightTradeProbabilityConfig(Request<OutrightTradeProbabilityConfigDTO> request) {
        log.info("::{}::putOutrightTradeProbabilityConfig: {}", request.getLinkId(), JSON.toJSONString(request));
        validateLinkId("putOutrightTradeProbabilityConfig", request);
        String linkId = request.getLinkId();
        OutrightTradeProbabilityConfigDTO configDTO = request.getData();
        //判断赛事是否存在与开售
        //获取标准赛事
        StandardMatchInfoDetail standardMatchInfo = thirdMatchMarketProcessor.getStandardMatchInfo(isOutright, configDTO.getStandardMatchId());
        //获取标准赛事开售信息
        StandardSportMarketSell standardSportMarketSell = thirdMatchMarketProcessor.getStandardSportMarketSell(isOutright, standardMatchInfo.getId());
        //判断盘口是否存在与开售
        List<StandardOutrightMarket> outrightMarkets = outrightMarketService.selectOutrightMarketSellList(configDTO.getStandardMarketId());
        if (!CollectionUtils.isEmpty(outrightMarkets)) {
            log.info("::{}::putOutrightTradeProbabilityConfig,冠军冠军盘口不存在，冠军盘口id:{}", linkId, configDTO.getStandardMarketId());
            return Response.failed("冠军盘口不存在!");
        }
        //保存数据
        ConfigOutrightTradeProbability configOutrightTradeProbability = outrightTradeProbabilityConfigService.selectItem(configDTO.getStandardMatchId(),configDTO.getStandardMarketOddsId());
        if(null == configOutrightTradeProbability){
            outrightTradeProbabilityConfigService.insertItem(request.getLinkId(),configDTO);
        }else{
            configOutrightTradeProbability.setProbability(configDTO.getProbability());
            configOutrightTradeProbability.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            configOutrightTradeProbability.setLinkId(request.getLinkId());
            outrightTradeProbabilityConfigService.updateItem(configOutrightTradeProbability);
        }
        Map<String, StandardMarketDataMessage> standardMarketDataMessageMap =
                thirdMatchMarketProcessor.getChampionStandardMarketDataMessageMap( request.getLinkId(), standardMatchInfo,  standardSportMarketSell);
        Set<Long> marketIdSet = new HashSet<>();
        marketIdSet.add(configDTO.getStandardMarketId());
        thirdMatchMarketProcessor.processOddsByAll(request.getLinkId(),request.getOddsSource(), request.getOperaterId(),standardMatchInfo, marketIdSet,standardMarketDataMessageMap, request.getDataSourceTime(), standardSportMarketSell, new HashMap<>());
        return Response.success();
    }

    @Override
    public Response getOutrightMarketDetail(Request<OutrightTradeMarketConfigDTO> request)
    {
        log.info("::{}::getOutrightMarketDetail: {}", request.getLinkId(), JSON.toJSONString(request));
        String linkId = request.getLinkId();
        OutrightTradeMarketConfigDTO outrightTradeMarketConfigDTO = request.getData();

        //判断赛事是否存在与开售
        StandardOutrightMatchInfo standardMatchInfo = standardOutrightMatchInfoService.getItem(outrightTradeMarketConfigDTO.getStandardMatchId());
        if (standardMatchInfo == null) {
            log.info("::{}::putOutrightTradeProbabilityConfig,冠军赛事不存在，冠军赛事id:{}", linkId, outrightTradeMarketConfigDTO.getStandardMatchId());
            return Response.failed("冠军赛事不存在!");
        }
        //判断盘口是否存在与开售
        List<StandardOutrightMarket> outrightMarkets = outrightMarketService.selectOutrightMarketSellList(outrightTradeMarketConfigDTO.getStandardMarketId());
        if (!CollectionUtils.isEmpty(outrightMarkets)) {
            log.info("::{}::putOutrightTradeProbabilityConfig,冠军盘口不存在，冠军盘口id:{}", linkId, outrightTradeMarketConfigDTO.getStandardMarketId());
            return Response.failed("冠军盘口不存在!");
        }
        StandardOutrightMarket standardOutrightMarket = outrightMarkets.get(0);

        List<StandardSportMarket> standardSportMarkets = standardSportMarketService.getItemList(standardMatchInfo.getId());
        if (!CollectionUtils.isEmpty(standardSportMarkets)) {
            log.info("::{}::putOutrightTradeProbabilityConfig,赛事标准盘口不存在，冠军盘口id:{}", linkId, outrightTradeMarketConfigDTO.getStandardMarketId());
            return Response.failed("标准盘口不存在!");
        }
        StandardSportMarket standardSportMarket = standardSportMarkets.get(0);
        List<StandardSportMarketOdds> standardSportMarketOdds = standardSportMarketOddsService.getItemList(standardSportMarket.getId());
        List<Long> nameCodes = standardSportMarketOdds.stream().map(odds -> odds.getNameCode()).collect(Collectors.toList());

        List<I18nOutrightMarketOdds> i18nOutrightMarketOdds = i18nOutrightMarketOddsService.getListOutrightMarketOdds(nameCodes, standardSportMarket.getDataSourceCode());

        Map<Long, StandardSportMarketOdds> marketOddsMap = standardSportMarketOdds.stream().collect(Collectors.toMap(StandardSportMarketOdds::getNameCode, Function.identity()));

        Map<Long, List<I18nOutrightMarketOdds>> i18nodds =  i18nOutrightMarketOdds.stream().collect(Collectors.groupingBy(obj->obj.getNameCode()));

        List<OutrightMarketOddsBO> outrightMarketOddsBOS = Lists.newArrayList();

        standardSportMarketOdds.forEach( ssmo -> {
            OutrightMarketOddsBO outrightMarketOddsBO = new OutrightMarketOddsBO();
            BeanUtils.copyProperties(ssmo,outrightMarketOddsBO);
            outrightMarketOddsBO.setOddsNameMap(getI18nMap(i18nodds.get(ssmo.getNameCode())));
            outrightMarketOddsBOS.add(outrightMarketOddsBO);
        });

        return Response.success(outrightMarketOddsBOS);
    }

    public void clearProbabilityValue(String linkId, Long standardMatchId, Long standardMarketId) {
        log.info("::{}::clearProbabilityValue standardMatchId: {}，standardMarketId:{} ", linkId, standardMatchId, standardMarketId);
        List<ConfigOutrightTradeProbability> itemList = outrightTradeProbabilityConfigService.getItemList(standardMatchId, standardMarketId);
        if(CollectionUtils.isEmpty(itemList)){
            log.info("::{}::clearProbabilityValue 盘口id: {}，没有找到相应的概率差配置", linkId, standardMarketId);
            return;
        }
        for (ConfigOutrightTradeProbability item :itemList) {
            outrightTradeProbabilityConfigService.del(item);
        }
        log.info("::{}::clearProbabilityValue 盘口id: {} 清理概率差成功", linkId, standardMarketId);
    }


    private Map<String, String> getI18nMap(List<I18nOutrightMarketOdds> i18nOutrightMarketOdds) {
        Map<String, String> map = Maps.newHashMap();
        i18nOutrightMarketOdds.stream().forEach(i18n -> {
            map.put(i18n.getLanguageType(), i18n.getText());
        });
        initFivelanguage.stream().forEach(initlan -> {
            if (!map.containsKey(initlan)) {
                map.put(initlan, "");
            }
        });
        return map;
    }

}
