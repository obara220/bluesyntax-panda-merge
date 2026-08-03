package com.panda.merge.rocketmq.consumer;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.component.MarketOddsPlaceProcessor;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.constant.MarginCategoryConfig;
import com.panda.merge.dto.CategoryDataSourceCodeDTO;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMarketCategory;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.odds.service.PlayRiskManagerService;
import com.panda.merge.rocketmq.processor.ThirdMatchMarketProcessor;
import com.panda.merge.rocketmq.producer.StandardMarketOddsProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.panda.merge.service.ThirdMarketCategoryService;
import com.panda.merge.service.ThirdMatchInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RocketMQMessageListener(topic = "CATEGORY_DATASOURCECODE_L_ClOSE_API",
        consumerGroup = "odds-group-CATEGORY_DATASOURCECODE_L_ClOSE_API",
        consumeThreadMax = 20)
@DependsOn("oddsAdminApplication")
public class CloseStandardMarketCategoryConsumer extends BaseProcessor implements RocketMQListener<Request<List<CategoryDataSourceCodeDTO>>> {

    @Autowired
    private RedisService redisService;
    @Autowired
    private StandardMatchInfoService standardMatchInfoService;
    @Autowired
    private ThirdMatchInfoService thirdMatchInfoService;
    @Autowired
    private ThirdMarketCategoryService thirdMarketCategoryService;
    @Autowired
    private MarketOddsPlaceProcessor marketOddsPlaceProcessor;
    @Lazy
    @Autowired
    private ThirdMatchMarketProcessor thirdMatchMarketProcessor;
    @Lazy
    @Autowired
    private StandardMarketOddsProducer standardMarketOddsProducer;
    @Autowired
    private PlayRiskManagerService playRiskManagerService;

    @Override
    public void onMessage(Request<List<CategoryDataSourceCodeDTO>> message) {
        String linkId = UUIdUtils.getId() + "";
        log.info("::{}::切换数据源不支持的玩法进行关盘,接收数据为:{}", linkId, JSON.toJSONString(message));
        String dataSourceCode = message.getDataSourceCode();
        List<CategoryDataSourceCodeDTO> categoryDataSourceCodeDTOList = message.getData();
        String thirdMatchSourceId = categoryDataSourceCodeDTOList.get(0).getThirdMatchSourceId();
        boolean checkDataSourceCode = categoryDataSourceCodeDTOList.get(0).getCheckDataSourceCode()==1;
        ThirdMatchInfo thirdMatchInfo = thirdMatchInfoService.getItem(dataSourceCode, thirdMatchSourceId);
        if (null == thirdMatchInfo || thirdMatchInfo.getReferenceId() == null | thirdMatchInfo.getReferenceId() == 0) {
            log.info("切换数据源不支持的玩法进行关盘,三方赛事不存在:{}", linkId);
            return;
        }
        try {
            Set<Long> categoryIds = new HashSet<>();
            StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(thirdMatchInfo.getReferenceId());
            for (CategoryDataSourceCodeDTO categoryDataSourceCodeDTO : categoryDataSourceCodeDTOList) {
                String internalDataSourceCode = categoryDataSourceCodeDTO.getInternalDataSourceCode();
                int marketType = categoryDataSourceCodeDTO.getMarketType();
                String thirdCategoryId = categoryDataSourceCodeDTO.getThirdCategoryId();
                ThirdMarketCategory thirdMarketCategory = thirdMarketCategoryService.getItem(dataSourceCode, thirdCategoryId);
                if (null == thirdMarketCategory || null == thirdMarketCategory.getReferenceId() || thirdMarketCategory.getReferenceId() == 0) {
                    log.info("::{}::切换数据源不支持的玩法进行关盘,玩法不支持:{}", linkId, thirdCategoryId);
                    continue;
                }
                /*if (StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId()) && MarginCategoryConfig.FootBall_MAIN3484_CATEGORY.contains(thirdMarketCategory.getReferenceId())) {
                } else if (StandardSportTypeEnum.Basketball.code.equals(standardMatchInfo.getSportId()) && MarginCategoryConfig.BASKETBALL_MAIN_CATEGORY.contains(thirdMarketCategory.getReferenceId())) {
                } else {
                    continue;
                }*/
                String linkIdNew = linkId + "_" + thirdMarketCategory.getReferenceId();
                Long categoryId = thirdMarketCategory.getReferenceId();
                categoryIds.add(categoryId);
                String marketKey = Constant.REDIS_KEY.RONGHE_STANDARD_CATEGORY_MARKET + thirdMatchInfo.getReferenceId() + "_" + dataSourceCode + "_" + thirdMarketCategory.getReferenceId();
                String redisKey = DigestUtil.md5Hex(marketKey);
                String marketLaskkey = Constant.REDIS_KEY.RONGHE_STANDARD_THE_LAST_MARKETODDS + standardMatchInfo.getId();
                String lastMarketOddsKey = DigestUtil.md5Hex(marketLaskkey);
                log.info("::{}::切换数据源不支持的玩法进行关盘,盘口缓存key:{},玩法级缓存key:{}", linkIdNew,marketKey, marketLaskkey);
                // 4405：按玩法级操盘模式判断本次关盘的下发链路（MTS/Panda）
                String playRiskKey = playRiskManagerService.buildKey(standardMatchInfo.getId(), marketType);
                Object rmObj = redisService.hGet(playRiskKey, String.valueOf(categoryId));
                String riskManagerCode = rmObj == null ? null : String.valueOf(rmObj);
                boolean isMtsFamily = playRiskManagerService.isMtsFamily(riskManagerCode);
                Set<Long> singleCategory = new HashSet<>(Collections.singletonList(categoryId));
                //获取本次玩法下面所有盘口
                Map<String, StandardMarketDataMessage> standardMarketMessageNewMap = redisService.hGetAll(redisKey);
                if (!MapUtils.isEmpty(standardMarketMessageNewMap)) {
                    log.info("::{}::切换数据源不支持的玩法进行关盘,标准缓存存在", linkIdNew);
                    List<Long> relationMarketIds = new ArrayList<>();
                    standardMarketMessageNewMap.values().stream().forEach(standardMarketMessage -> {
                        standardMarketMessage.setColseMarket(2);
                        standardMarketMessage.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        standardMarketMessage.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        standardMarketMessage.setRemark(standardMarketMessage.getRemark()+",L系关闭玩法");
                        relationMarketIds.add(standardMarketMessage.getRelationMarketId());
                    });
                    //redisService.hDel(redisKey, relationMarketIds.toArray());
                    redisService.hSetAll(redisKey, standardMarketMessageNewMap,marketCacheTime(standardMatchInfo.getBeginTime()));
                    redisService.hDel(lastMarketOddsKey, String.valueOf(thirdMarketCategory.getReferenceId()));
                    marketOddsPlaceProcessor.setOddsOrderByOddsValue(linkIdNew, standardMarketMessageNewMap, standardMatchInfo, singleCategory, null, null, null, false);
                    if (isMtsFamily) {
                        thirdMatchMarketProcessor.processOddsByMts(linkIdNew, standardMatchInfo, singleCategory, standardMarketMessageNewMap, System.currentTimeMillis(), Boolean.TRUE);
                    } else {
                        thirdMatchMarketProcessor.processOddsByPanda(linkIdNew, message.getOddsSource(), message.getOperaterId(), standardMatchInfo, singleCategory, standardMarketMessageNewMap, System.currentTimeMillis(), new HashMap<>(), Boolean.TRUE);
                    }
                } else {
                    log.info("::{}::切换数据源不支持的玩法进行关盘,标准缓存不存在", linkIdNew);
                    //获取上一次下发的最新盘口 ，上一次不存在不处理
                    List<StandardMarketMessage> TempLastStandardMarketMessages = (List<StandardMarketMessage>) redisService.hGet(lastMarketOddsKey, String.valueOf(thirdMarketCategory.getReferenceId()));
                    if (TempLastStandardMarketMessages == null || TempLastStandardMarketMessages.isEmpty()) {
                        continue;
                    }
                    List<StandardMarketMessage> lastStandardMarketMessages = TempLastStandardMarketMessages.stream().filter(e->(checkDataSourceCode&&dataSourceCode.equals(e.getDataSourceCode()))||!checkDataSourceCode).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(lastStandardMarketMessages) ) {
                        continue;
                    }
                    lastStandardMarketMessages.stream().forEach(l -> {
                        l.setThirdMarketSourceStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                        l.setStatus(Constant.SPORT_MARKET.STATUS.DEACTIVATED);
                    });
                    //redisService.hDel(lastMarketOddsKey, String.valueOf(thirdMarketCategory.getReferenceId()));
                    redisService.hSet(lastMarketOddsKey, String.valueOf(thirdMarketCategory.getReferenceId()), lastStandardMarketMessages, marketCacheTime(standardMatchInfo.getBeginTime()));
                    standardMarketOddsProducer.standardMarketOddsAsyncSend(linkIdNew, standardMatchInfo, lastStandardMarketMessages, System.currentTimeMillis(), isMtsFamily);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(linkId + ",数据：" + JSON.toJSONString(message) + "切换数据源不支持的玩法进行关盘,出现异常:{}", e);
        }
    }
}