package com.panda.merge.component;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.config.NacosParameterConfig;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.MarketOddsLinkageConfigMessage;
import com.panda.merge.dto.message.StandardMarketMessage;
import com.panda.merge.dto.message.StandardMarketOddsMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.processor.StandardMarketOddsLinkageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.MarginCategoryConfig.STANDARD_OUTRIGHT_CATEGORY;

/**
 * 普通赛事
 * 2868	【冠军操盘】投注项之间赔率联动
 */
@Slf4j
@Component
public class StandardMatchMarketOddsLinkageProcessor extends BaseProcessor {

    @Autowired
    private RedisService redisService;

    /**
     * 联动开关 0关 1开
     */
    @Autowired
    private NacosParameterConfig nacosParameterConfig;
    public static final List<Long> CATEGORY = Arrays.asList(1L, 2L, 4L, 7L, 17L, 18L, 19L, 141L, 135L, 136L);

    @Lazy
    @Autowired
    private StandardMarketOddsLinkageProcessor standardMarketOddsLinkageProcessor;


    /**
     * 普通赛事足球部分玩法，赔率联动
     * 1.判断开关是否开启
     * 2.过滤出需要处理的玩法
     * 3.投注项ID查询配置 ，
     *      存在
     *          判断是否主
     *              是 判断盘口状态、赔率和上一次缓存是否一致 ，不一致通知副赔率下发
     *          判断是否副
     *              是 获取主盘口状态、赔率直接下发
     * @param linkId
     * @param standardMatchInfo
     * @param standardMarketMessageList
     */
    public void matchMarketOddsMainLinkage(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageList) {
        if (!StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId()) || 0 == nacosParameterConfig.getLinkageStatus()) {
            return;
        }
        //过滤玩法
        Map<Long, List<StandardMarketMessage>> standardMarketMessagesMap = standardMarketMessageList.stream().filter(standardMarketMessage -> CATEGORY.contains(standardMarketMessage.getMarketCategoryId())).collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));
        for (Map.Entry<Long, List<StandardMarketMessage>> entry : standardMarketMessagesMap.entrySet()) {
            Long marketCategoryId = entry.getKey();
            List<StandardMarketMessage> standardMarketMessages = entry.getValue();
            for (StandardMarketMessage standardMarketMessage : standardMarketMessages) {
                Long standardMarketId = standardMarketMessage.getId();
                List<StandardMarketOddsMessage> marketOddsList = standardMarketMessage.getMarketOddsList();
                if(CollectionUtils.isEmpty(marketOddsList)){
                    continue;
                }
                //盘口级别 自动水差
                Object autoDiff = redisService.get(Constant.REDIS_KEY.RONGE_MARKET_ODDS_AUTO_DIFF + standardMarketId);
                for (StandardMarketOddsMessage standardMarketOddsMessage : marketOddsList) {
                    Long standardMarketOddsId = standardMarketOddsMessage.getId();
                    //查询联动缓存
                    String oddsKey = Constant.REDIS_KEY.RONGE_ODDS_LINKAGE_CONFIG + standardMarketOddsId;
                    MarketOddsLinkageConfigMessage marketOddsLinkageConfigMessage = (MarketOddsLinkageConfigMessage) redisService.get(oddsKey);
                    if (null == marketOddsLinkageConfigMessage) {
                        continue;
                    }
                    //如果是主投注项，判断缓存 和 当前的赔率、盘口状态是否一致，不一致通知其他副投注项触发赔率下发
                    if (1 == marketOddsLinkageConfigMessage.getType()) {
                        if (!standardMarketMessage.getStatus().equals(marketOddsLinkageConfigMessage.getStatus()) || !standardMarketOddsMessage.getPaOddsValue().equals(marketOddsLinkageConfigMessage.getPaOddsValue())) {
                            log.info("::{}::赔率联动,赛事ID：{}，玩法ID：{}，盘口ID：{}，投注项ID：{}，主投注项赔率：{}-改-{}，盘口状态{}-改-{}，发生改变", linkId, standardMatchInfo.getId(), marketCategoryId, standardMarketId, standardMarketOddsId, marketOddsLinkageConfigMessage.getPaOddsValue(), standardMarketOddsMessage.getPaOddsValue(), marketOddsLinkageConfigMessage.getStatus(), standardMarketMessage.getStatus());
                            //刷新主投注项赔率 和 盘口状态
                            marketOddsLinkageConfigMessage.setStatus(standardMarketMessage.getStatus());
                            marketOddsLinkageConfigMessage.setPaOddsValue(standardMarketOddsMessage.getPaOddsValue());
                            redisService.set(oddsKey, marketOddsLinkageConfigMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
                            //通知副投注项下发
                            standardMarketOddsLinkageProcessor.mainSendMarketOddsProcessor(linkId, marketOddsLinkageConfigMessage.getStandardMarketOddsId());
                        }
                        //设置主赔率标识
                        standardMarketOddsMessage.setOddsIsMain(1);
                    } else if (0 == marketOddsLinkageConfigMessage.getType()) {
                        //自动水差存在，不联动
                        if(ObjectUtils.isNotEmpty(autoDiff)){
                            log.info("::{}::赔率联动,赛事ID：{}，玩法ID：{}，盘口ID：{}，投注项ID：{}，自动水差存在，不联动：{}", linkId, standardMatchInfo.getId(), marketCategoryId, standardMarketId, standardMarketOddsId, autoDiff);
                            return;
                        }
                        //副投注项，找出主投注项的投注项ID  ,赔率，和盘口状态下发
                        String oddsMainKey = Constant.REDIS_KEY.RONGE_ODDS_LINKAGE_CONFIG + marketOddsLinkageConfigMessage.getParentStandardMarketOddsId();
                        MarketOddsLinkageConfigMessage marketOddsLinkageConfigMainMessage = (MarketOddsLinkageConfigMessage) redisService.get(oddsMainKey);
                        if (null == marketOddsLinkageConfigMainMessage) {
                            log.info("::{}::赔率联动,赛事ID：{}，玩法ID：{}，盘口ID：{}，投注项ID：{}，主投注项赔率不存在KEY：{}", linkId, standardMatchInfo.getId(), marketCategoryId, standardMarketId, standardMarketOddsId, oddsMainKey);
                            continue;
                        }
                        //主赔率异常不处理
                        if (null == marketOddsLinkageConfigMainMessage.getPaOddsValue() || 0 == marketOddsLinkageConfigMainMessage.getPaOddsValue()) {
                            log.info("::{}::赔率联动,赛事ID：{}，玩法ID：{}，盘口ID：{}，投注项ID：{}，主赔率异常不处理：{}", linkId, standardMatchInfo.getId(), marketCategoryId, standardMarketId, standardMarketOddsId, JSONObject.toJSONString(marketOddsLinkageConfigMainMessage));
                            continue;
                        }
                        log.info("::{}::赔率联动,赛事ID：{}，玩法ID：{}，盘口ID：{}，主投注项ID：{},副投注项ID：{}，副投注项赔率：{}-改-{}，副盘口状态{}-改-{}，发生改变",
                                linkId, standardMatchInfo.getId(), marketCategoryId, standardMarketId, marketOddsLinkageConfigMessage.getParentStandardMarketOddsId(), standardMarketOddsId, standardMarketOddsMessage.getPaOddsValue(), marketOddsLinkageConfigMainMessage.getPaOddsValue(), standardMarketMessage.getStatus(), marketOddsLinkageConfigMainMessage.getStatus());
                        standardMarketOddsMessage.setPaOddsValue(marketOddsLinkageConfigMainMessage.getPaOddsValue());
                        standardMarketMessage.setStatus(marketOddsLinkageConfigMainMessage.getStatus());
                        //standardMarketMessage.setThirdMarketSourceStatus(marketOddsLinkageConfigMainMessage.getStatus());
                        //设置副赔率标识
                        standardMarketOddsMessage.setOddsIsMain(0);
                    }
                }
            }

        }
    }



    /**
     * 冠军
     * @param linkId
     * @param standardMatchInfo
     * @param standardMarketMessageList
     */
    public void championMarketOddsMainLinkage(String linkId, StandardMatchInfo standardMatchInfo, List<StandardMarketMessage> standardMarketMessageList) {
        if (!StandardSportTypeEnum.FootBall.code.equals(standardMatchInfo.getSportId()) || 0 == nacosParameterConfig.getLinkageStatus()) {
            return;
        }
        //过滤玩法
        Map<Long, List<StandardMarketMessage>> standardMarketMessagesMap = standardMarketMessageList.stream()
                .filter(standardMarketMessage -> STANDARD_OUTRIGHT_CATEGORY.contains(standardMarketMessage.getMarketCategoryId()))
                .collect(Collectors.groupingBy(StandardMarketMessage::getMarketCategoryId));

        for (Map.Entry<Long, List<StandardMarketMessage>> entry : standardMarketMessagesMap.entrySet()) {
            Long marketCategoryId = entry.getKey();
            List<StandardMarketMessage> standardMarketMessages = entry.getValue();
            for (StandardMarketMessage standardMarketMessage : standardMarketMessages) {
                Long standardMarketId = standardMarketMessage.getId();
                List<StandardMarketOddsMessage> marketOddsList = standardMarketMessage.getMarketOddsList();
                if (CollectionUtils.isEmpty(marketOddsList)) {
                    continue;
                }
                //盘口级别 自动水差
                Object autoDiff = redisService.get(Constant.REDIS_KEY.RONGE_MARKET_ODDS_AUTO_DIFF + standardMarketId);
                for (StandardMarketOddsMessage standardMarketOddsMessage : marketOddsList) {
                    Long standardMarketOddsId = standardMarketOddsMessage.getId();
                    //查询联动缓存
                    String oddsKey = Constant.REDIS_KEY.RONGE_ODDS_LINKAGE_CONFIG + standardMarketOddsId;
                    MarketOddsLinkageConfigMessage marketOddsLinkageConfigMessage = (MarketOddsLinkageConfigMessage) redisService.get(oddsKey);
                    if (null == marketOddsLinkageConfigMessage) {
                        continue;
                    }
                    //如果是主投注项，判断缓存 和 当前的赔率、盘口状态是否一致，不一致通知其他副投注项触发赔率下发
                    if (1 == marketOddsLinkageConfigMessage.getType()) {
                        Integer active = standardMarketOddsMessage.getActive();
                        if ( null == active ) {
                            continue;
                        }

                        boolean oddsEquals = !standardMarketOddsMessage.getPaOddsValue().equals(marketOddsLinkageConfigMessage.getPaOddsValue());

                        Integer dbOddsStatus = active == 1 ? 0 : 2;
                        if ( oddsEquals || !dbOddsStatus.equals( marketOddsLinkageConfigMessage.getStatus()) ) {
                            log.info("::{}::赔率联动,赛事ID：{}，玩法ID：{}，盘口ID：{}，投注项ID：{}，主投注项赔率：{}-改-{}，盘口状态{}-改-{}，active：{}，发生改变",
                                    linkId, standardMatchInfo.getId(), marketCategoryId, standardMarketId, standardMarketOddsId, marketOddsLinkageConfigMessage.getPaOddsValue(), standardMarketOddsMessage.getPaOddsValue(), marketOddsLinkageConfigMessage.getStatus(), standardMarketOddsMessage.getActive(),active);

                            //刷新主投注项赔率 和 盘口状态
                            // marketOddsLinkageConfigMessage.setStatus(standardMarketOddsMessage.getStatus());
                            marketOddsLinkageConfigMessage.setStatus( dbOddsStatus );

                            marketOddsLinkageConfigMessage.setPaOddsValue(standardMarketOddsMessage.getPaOddsValue());
                            redisService.set(oddsKey, marketOddsLinkageConfigMessage, marketCacheTime(standardMatchInfo.getBeginTime()));
                            //通知副投注项下发
                            standardMarketOddsLinkageProcessor.mainSendMarketOddsProcessor(linkId, marketOddsLinkageConfigMessage.getStandardMarketOddsId());
                        }
                        //设置主赔率标识
                        standardMarketOddsMessage.setOddsIsMain(1);
                    } else if (0 == marketOddsLinkageConfigMessage.getType()) {
                        //自动水差存在，不联动
                        if(ObjectUtils.isNotEmpty(autoDiff)){
                            log.info("::{}::赔率联动,赛事ID：{}，玩法ID：{}，盘口ID：{}，投注项ID：{}，自动水差存在，不联动：{}", linkId, standardMatchInfo.getId(), marketCategoryId, standardMarketId, standardMarketOddsId, autoDiff);
                            return;
                        }
                        //副投注项，找出主投注项的投注项ID  ,赔率，和盘口状态下发
                        String oddsMainKey = Constant.REDIS_KEY.RONGE_ODDS_LINKAGE_CONFIG + marketOddsLinkageConfigMessage.getParentStandardMarketOddsId();
                        MarketOddsLinkageConfigMessage marketOddsLinkageConfigMainMessage = (MarketOddsLinkageConfigMessage) redisService.get(oddsMainKey);
                        if (null == marketOddsLinkageConfigMainMessage) {
                            log.info("::{}::赔率联动,赛事ID：{}，玩法ID：{}，盘口ID：{}，投注项ID：{}，主投注项赔率不存在KEY：{}", linkId, standardMatchInfo.getId(), marketCategoryId, standardMarketId, standardMarketOddsId, oddsMainKey);
                            continue;
                        }
                        //主赔率异常不处理
                        if (null == marketOddsLinkageConfigMainMessage.getPaOddsValue() || 0 == marketOddsLinkageConfigMainMessage.getPaOddsValue()) {
                            log.info("::{}::赔率联动,赛事ID：{}，玩法ID：{}，盘口ID：{}，投注项ID：{}，主赔率异常不处理：{}", linkId, standardMatchInfo.getId(), marketCategoryId, standardMarketId, standardMarketOddsId, JSONObject.toJSONString(marketOddsLinkageConfigMainMessage));
                            continue;
                        }
                        log.info("::{}::赔率联动,赛事ID：{}，玩法ID：{}，盘口ID：{}，主投注项ID：{},副投注项ID：{}，副投注项赔率：{}-改-{}，副盘口状态{}-改-{}，发生改变",
                                linkId, standardMatchInfo.getId(), marketCategoryId, standardMarketId, marketOddsLinkageConfigMessage.getParentStandardMarketOddsId(), standardMarketOddsId, standardMarketOddsMessage.getPaOddsValue(), marketOddsLinkageConfigMainMessage.getPaOddsValue(), standardMarketMessage.getStatus(), marketOddsLinkageConfigMainMessage.getStatus());
                        standardMarketOddsMessage.setPaOddsValue(marketOddsLinkageConfigMainMessage.getPaOddsValue());
                        if ( marketOddsLinkageConfigMainMessage.getStatus() == 0 ) {
                            standardMarketOddsMessage.setStatus(marketOddsLinkageConfigMainMessage.getStatus());
                            standardMarketOddsMessage.setActive(1);
                        } else {
                            standardMarketOddsMessage.setStatus(2);
                            standardMarketOddsMessage.setActive(0);
                        }
                        // standardMarketMessage.setStatus(marketOddsLinkageConfigMainMessage.getStatus());
                        //设置副赔率标识
                        standardMarketOddsMessage.setOddsIsMain(0);
                    }
                }
            }

        }
    }
}
