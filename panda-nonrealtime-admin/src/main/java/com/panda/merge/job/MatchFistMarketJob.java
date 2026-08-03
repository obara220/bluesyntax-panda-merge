package com.panda.merge.job;

import com.alibaba.fastjson.JSONObject;
import com.panda.aocollect.model.MatchOddsHistory;
import com.panda.aocollect.model.MatchOddsHistoryBasketball;
import com.panda.aocollect.model.MatchStoppagetimeHistory;
import com.panda.aoodds.sports.api.entity.Response;
import com.panda.aoodds.sports.api.service.ApplyService;
import com.panda.merge.common.enums.Constant;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.component.UUIdUtils;
import com.panda.merge.config.RedisService;
import com.panda.merge.dto.message.StandardMarketDataMessage;
import com.panda.merge.dto.message.StandardMarketOddsDataMessage;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.rocketmq.producer.MatchFistMarketProducer;
import com.panda.merge.service.StandardMatchInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@JobHandler(value = "MatchFistMarketJob")
public class MatchFistMarketJob extends IJobHandler {
    @Autowired
    public RedisService redisService;
    @Autowired
    public MatchFistMarketProducer matchFistMarketProducer;
    @DubboReference
    private ApplyService applyService;
    @Autowired
    public StandardMatchInfoService standardMatchInfoService;

    @Override
    public ReturnT<String> execute(String param) {
        try {
            //赛事
            String thirdFistMatchKey = Constant.REDIS_KEY.THIRD_FIST_MATCH;
            //log.info("定时任务开始处理初盘盘口,key:{}", thirdFistMatchKey);
            Map<String, Long> matchMap = redisService.hGetAll(thirdFistMatchKey);
            if (!MapUtils.isEmpty(matchMap)) {
                for (Map.Entry<String, Long> entry : matchMap.entrySet()) {
                    Long standardMatchId = Long.valueOf(entry.getKey());
                    String linkId = UUIdUtils.getId() + "_" + entry.getKey();
                    try {
                        if (StringUtils.isNotEmpty(param)) {
                            if (!entry.getKey().equals(param)) {
                                continue;
                            }
                        }
                        //开始赛事时间 + 3小时
                        Long beginTime = entry.getValue() + 10800000;
                        if (StringUtils.isEmpty(param) && TimeUtils.millsSecondsEast8ZoneGmt() < beginTime) {
                            continue;
                        }
                        //盘口赔率
                        String fistKey = Constant.REDIS_KEY.THIRD_FIST_MARKET_HEAD + standardMatchId;
                        Map<String, StandardMarketDataMessage> standardMarketMessageDataMap = redisService.hGetAll(fistKey);
                        if (MapUtils.isEmpty(standardMarketMessageDataMap)) {
                            delKey(thirdFistMatchKey, entry, fistKey);
                            continue;
                        }
                        //查询标准赛事
                        StandardMatchInfo standardMatchInfo = standardMatchInfoService.getItem(standardMatchId);
                        if (null == standardMatchInfo) {
                            //log.info("::{}::定时任务开始处理初盘盘口,标准赛事不存在:{}", linkId, standardMatchId);
                            delKey(thirdFistMatchKey, entry, fistKey);
                            continue;
                        }
                        //不是完赛状态不处理
                        if (StringUtils.isEmpty(param) && !YesNoEnum.Y.value.equals(standardMatchInfo.getMatchOver())) {
                            //log.info("::{}::定时任务开始处理初盘盘口,不是完赛状态不处理:{}", linkId, standardMatchId);
                            continue;
                        }
                        List<StandardMarketDataMessage> standardMarketDataMessageList = new ArrayList<StandardMarketDataMessage>(standardMarketMessageDataMap.values());
                        //log.info("::{}::定时任务开始处理初盘盘口,赛事ID:{},比赛时间:{},加时间后:{},盘口数据:{}", linkId, standardMatchId, entry.getValue(), beginTime, JSONObject.toJSONString(standardMarketDataMessageList));
                        standardMarketDataMessageProcessor(linkId, standardMatchId, standardMatchInfo.getSportId(), standardMarketDataMessageList);
                        //下发成功删除赛事，赔率
                        if (StringUtils.isEmpty(param)) {
                            delKey(thirdFistMatchKey, entry, fistKey);
                            //log.info("::{}::定时任务开始处理初盘盘口删除缓存成功,赛事ID:{}", linkId, standardMatchId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //log.info("::{}::定时任务开始处理初盘盘口出现异常,赛事ID:{}", linkId, standardMatchId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("【定时任务开始扫描缓存中赛事初盘数据,异常】 Exception:", e);
            XxlJobLogger.log("【定时任务开始扫描缓存中赛事初盘数据,异常】 Exception:" + e.getMessage());
        }
        XxlJobLogger.log("【定时任务开始扫描缓存中赛事初盘数据】 处理结束");
        return ReturnT.SUCCESS;
    }

    /**
     * 删除redis缓存
     *
     * @param thirdFistMatchKey
     * @param entry
     * @param fistKey
     */
    private void delKey(String thirdFistMatchKey, Map.Entry<String, Long> entry, String fistKey) {
        //不存在盘口，删除赛事 ，删除盘口
        redisService.hDel(thirdFistMatchKey, entry.getKey());
        redisService.del(fistKey);
    }

    /**
     * 盘口数据配料处理
     *
     * @param linkId
     * @param standardMatchId
     * @param sportId
     * @param standardMarketDataMessageList
     */
    private void standardMarketDataMessageProcessor(String linkId, Long standardMatchId, Long sportId, List<StandardMarketDataMessage> standardMarketDataMessageList) {
        if (StandardSportTypeEnum.FootBall.code.equals(sportId)) {
            List<MatchOddsHistory> oddsHistories = convertFootBall(linkId, standardMatchId, sportId, standardMarketDataMessageList);
            if (CollectionUtils.isNotEmpty(oddsHistories)) {
                //下发赛程足球赔率
                matchFistMarketProducer.sendFistMarketFootBall(linkId, standardMatchId, sportId, oddsHistories);
                //log.info("::{}::定时任务开始处理足球初盘盘口最后发送数据,赛事ID:{},数据:{}", linkId, standardMatchId, JSONObject.toJSONString(oddsHistories));
                sendMatchStoppageTime(linkId, standardMatchId, oddsHistories);
            }
        } else {
            List<MatchOddsHistoryBasketball> oddsHistories = convertBasketball(linkId, standardMatchId, sportId, standardMarketDataMessageList);
            if (CollectionUtils.isNotEmpty(oddsHistories)) {
                //下发赛程足球赔率
                matchFistMarketProducer.sendFistMarketBasketball(linkId, standardMatchId, sportId, oddsHistories);
                //log.info("::{}::定时任务开始处理篮球初盘盘口最后发送数据,赛事ID:{},数据:{}", linkId, standardMatchId, JSONObject.toJSONString(oddsHistories));
            }
        }
    }

    /**
     * 足球
     * 转换实体 ，构建赔率，发送AOrev
     */
    private List<MatchOddsHistory> convertFootBall(String linkId, Long standardMatchId, Long sportId, List<StandardMarketDataMessage> standardMarketDataMessageList) {
        setStandardMarketDataSourceCode(standardMarketDataMessageList);
        List<MatchOddsHistory> matchOddsHistorys = new ArrayList<>();
        //数据源分组
        Map<String, List<StandardMarketDataMessage>> StandardMarketMessageDataSourceMap = standardMarketDataMessageList.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getDataSourceCode));
        for (Map.Entry<String, List<StandardMarketDataMessage>> entryDataSource : StandardMarketMessageDataSourceMap.entrySet()) {
            List<StandardMarketDataMessage> entryDataSourceValue = entryDataSource.getValue();
            MatchOddsHistory matchOddsHistory = marketCategoryOddsFootBall(linkId, standardMatchId, entryDataSourceValue);
            if (null != matchOddsHistory) {
                matchOddsHistorys.add(matchOddsHistory);
            }
        }
        //发送参数到AO rev
        return aoRevFootBall(linkId, standardMatchId, sportId, matchOddsHistorys);
    }

    /**
     * 篮球
     * 转换实体 ，构建赔率，发送AOrev
     */
    private List<MatchOddsHistoryBasketball> convertBasketball(String linkId, Long standardMatchId, Long sportId, List<StandardMarketDataMessage> standardMarketDataMessageList) {
        setStandardMarketDataSourceCode(standardMarketDataMessageList);
        List<MatchOddsHistoryBasketball> matchOddsHistorys = new ArrayList<>();
        //数据源分组
        Map<String, List<StandardMarketDataMessage>> StandardMarketMessageDataSourceMap = standardMarketDataMessageList.stream().collect(Collectors.groupingBy(StandardMarketDataMessage::getDataSourceCode));
        for (Map.Entry<String, List<StandardMarketDataMessage>> entryDataSource : StandardMarketMessageDataSourceMap.entrySet()) {
            List<StandardMarketDataMessage> entryDataSourceValue = entryDataSource.getValue();
            MatchOddsHistoryBasketball matchOddsHistory = marketCategoryOddsBasketball(linkId, standardMatchId, entryDataSourceValue);
            if (null != matchOddsHistory) {
                //判断赛前是否完整
                if (null == matchOddsHistory.getEarlyMorningHandicapOdds() || null == matchOddsHistory.getEarlyMorningBigSmallOdds() || null == matchOddsHistory.getEarlyMorningWinAloneOdds()) {
                    continue;
                }
                matchOddsHistorys.add(matchOddsHistory);
            }
        }
        //发送参数到AO rev
        return aoRevBasketball(linkId, standardMatchId, sportId, matchOddsHistorys);
    }

    /**
     * 设置数据源
     *
     * @param standardMarketDataMessageList
     */
    private void setStandardMarketDataSourceCode(List<StandardMarketDataMessage> standardMarketDataMessageList) {
        standardMarketDataMessageList.forEach(standardMarketMessage -> {
            String dataSourceCodePa = standardMarketMessage.getDataSourceCodePA();
            if (!StringUtils.isEmpty(dataSourceCodePa)) {
                standardMarketMessage.setDataSourceCode(standardMarketMessage.getDataSourceCodePA());
            }
        });
    }

    /**
     * 足球
     * 玩法赔率
     *
     * @param
     */
    private MatchOddsHistory marketCategoryOddsFootBall(String linkId, Long standardMatchId, List<StandardMarketDataMessage> standardMarketDataMessages) {
        MatchOddsHistory matchOddsHistory = new MatchOddsHistory();
        matchOddsHistory.setMatchId(standardMatchId);
        standardMarketDataMessages.forEach(standardMarketMessage -> {
            Long marketCategoryId = standardMarketMessage.getMarketCategoryId();
            String dataSourceCode = standardMarketMessage.getDataSourceCode();
            Integer marketType = standardMarketMessage.getMarketType();
            String addition1 = standardMarketMessage.getAddition1();
            Long createTime = null == standardMarketMessage.getModifyTime() ? standardMarketMessage.getCreateTime() : standardMarketMessage.getModifyTime();
            matchOddsHistory.setDataSourceCode(dataSourceCode);
            //投注项类型分组
            Map<String, StandardMarketOddsDataMessage> marketOddsMap = standardMarketMessage.getMarketOddsList().stream().collect(Collectors.toMap(StandardMarketOddsDataMessage::getOddsType, i -> i, (oldValue, newValue) -> newValue));
            StandardMarketOddsDataMessage oddsOver = marketOddsMap.get("Over");
            StandardMarketOddsDataMessage oddsUnder = marketOddsMap.get("Under");
            StandardMarketOddsDataMessage odds1 = marketOddsMap.get("1");
            StandardMarketOddsDataMessage odds2 = marketOddsMap.get("2");

            Integer oddsStr1 = 0;
            Integer oddsStr2 = 0;
            switch (marketCategoryId.intValue()) {
                case 2://全场大小
                    oddsStr1 = dataSourceCode.equals("PA") ? oddsOver.getPaOddsValue() : oddsOver.getOriginalOddsValue();
                    oddsStr2 = dataSourceCode.equals("PA") ? oddsUnder.getPaOddsValue() : oddsUnder.getOriginalOddsValue();
                    //赛前
                    if (marketType == 1) {
                        matchOddsHistory.setOddsD1(oddsStr1 + "/" + addition1 + "/" + oddsStr2);
                        matchOddsHistory.setTime1(createTime);
                    } else {
                        matchOddsHistory.setOddsD2(oddsStr1 + "/" + addition1 + "/" + oddsStr2);
                        matchOddsHistory.setTime2(createTime);
                    }
                    break;
                case 4://全场让球
                    oddsStr1 = dataSourceCode.equals("PA") ? odds1.getPaOddsValue() : odds1.getOriginalOddsValue();
                    oddsStr2 = dataSourceCode.equals("PA") ? odds2.getPaOddsValue() : odds2.getOriginalOddsValue();
                    //赛前
                    if (marketType == 1) {
                        matchOddsHistory.setOddsR1(oddsStr1 + "/" + addition1 + "/" + oddsStr2);
                        matchOddsHistory.setTime1(createTime);
                    } else {
                        matchOddsHistory.setOddsR2(oddsStr1 + "/" + addition1 + "/" + oddsStr2);
                        matchOddsHistory.setTime2(createTime);
                    }
                    break;
                case 127://加时赛-大小
                    oddsStr1 = dataSourceCode.equals("PA") ? oddsOver.getPaOddsValue() : oddsOver.getOriginalOddsValue();
                    oddsStr2 = dataSourceCode.equals("PA") ? oddsUnder.getPaOddsValue() : oddsUnder.getOriginalOddsValue();
                    matchOddsHistory.setOddsD3(oddsStr1 + "/" + addition1 + "/" + oddsStr2);
                    matchOddsHistory.setTime3(createTime);
                    break;
                case 128://加时赛-让球
                    oddsStr1 = dataSourceCode.equals("PA") ? odds1.getPaOddsValue() : odds1.getOriginalOddsValue();
                    oddsStr2 = dataSourceCode.equals("PA") ? odds2.getPaOddsValue() : odds2.getOriginalOddsValue();
                    matchOddsHistory.setOddsR3(oddsStr1 + "/" + addition1 + "/" + oddsStr2);
                    matchOddsHistory.setTime3(createTime);
                    break;
                default:
                    break;
            }
        });
        //让球 初盘，收盘不存在 不下发rev
        if (StringUtils.isEmpty(matchOddsHistory.getOddsR1()) && StringUtils.isEmpty(matchOddsHistory.getOddsR2())) {
            //log.info("::{}::定时任务开始处理初盘盘口,大小初盘收盘不存在,赛事ID:{},盘口数据:{}", linkId, standardMatchId, JSONObject.toJSONString(matchOddsHistory));
            return null;
        }
        //大小 初盘，收盘不存在 不下发rev
        if (StringUtils.isEmpty(matchOddsHistory.getOddsD1()) && StringUtils.isEmpty(matchOddsHistory.getOddsD2())) {
            //log.info("::{}::定时任务开始处理初盘盘口,大小初盘收盘不存在,赛事ID:{},盘口数据:{}", linkId, standardMatchId, JSONObject.toJSONString(matchOddsHistory));
            return null;
        }
        //让球初盘不存在，收盘存在 收盘=初盘
        if (StringUtils.isEmpty(matchOddsHistory.getOddsR1()) && StringUtils.isNotEmpty(matchOddsHistory.getOddsR2())) {
            matchOddsHistory.setOddsR1(matchOddsHistory.getOddsR2());
        }
        //让球初盘存在，收盘不存在 初盘=收盘
        if (StringUtils.isNotEmpty(matchOddsHistory.getOddsR1()) && StringUtils.isEmpty(matchOddsHistory.getOddsR2())) {
            matchOddsHistory.setOddsR2(matchOddsHistory.getOddsR1());
        }
        //大小初盘不存在，收盘存在 收盘=初盘
        if (StringUtils.isEmpty(matchOddsHistory.getOddsD1()) && StringUtils.isNotEmpty(matchOddsHistory.getOddsD2())) {
            matchOddsHistory.setOddsD1(matchOddsHistory.getOddsD2());
        }
        //大小初盘存在，收盘不存在 初盘=收盘
        if (StringUtils.isNotEmpty(matchOddsHistory.getOddsD1()) && StringUtils.isEmpty(matchOddsHistory.getOddsD2())) {
            matchOddsHistory.setOddsD2(matchOddsHistory.getOddsD1());
        }
        return matchOddsHistory;
    }

    /**
     * 篮球
     * 玩法赔率
     *
     * @param
     */
    private MatchOddsHistoryBasketball marketCategoryOddsBasketball(String linkId, Long standardMatchId, List<StandardMarketDataMessage> standardMarketDataMessages) {
        MatchOddsHistoryBasketball matchOddsHistory = new MatchOddsHistoryBasketball();
        matchOddsHistory.setMatchId(standardMatchId);
        standardMarketDataMessages.forEach(standardMarketMessage -> {
            Long marketCategoryId = standardMarketMessage.getMarketCategoryId();
            String dataSourceCode = standardMarketMessage.getDataSourceCode();
            Integer marketType = standardMarketMessage.getMarketType();
            String addition1 = standardMarketMessage.getAddition1();
            Long createTime = null == standardMarketMessage.getModifyTime() ? standardMarketMessage.getCreateTime() : standardMarketMessage.getModifyTime();
            matchOddsHistory.setDataSourceCode(dataSourceCode);
            //投注项类型分组
            Map<String, StandardMarketOddsDataMessage> marketOddsMap = standardMarketMessage.getMarketOddsList().stream().collect(Collectors.toMap(StandardMarketOddsDataMessage::getOddsType, i -> i, (oldValue, newValue) -> newValue));
            StandardMarketOddsDataMessage oddsOver = marketOddsMap.get("Over");
            StandardMarketOddsDataMessage oddsUnder = marketOddsMap.get("Under");
            StandardMarketOddsDataMessage odds1 = marketOddsMap.get("1");
            StandardMarketOddsDataMessage odds2 = marketOddsMap.get("2");

            Integer oddsStr1 = 0;
            Integer oddsStr2 = 0;
            switch (marketCategoryId.intValue()) {
                case 39://让分
                    oddsStr1 = dataSourceCode.equals("PA") ? odds1.getPaOddsValue() : odds1.getOriginalOddsValue();
                    oddsStr2 = dataSourceCode.equals("PA") ? odds2.getPaOddsValue() : odds2.getOriginalOddsValue();
                    //赛前
                    if (marketType == 1) {
                        matchOddsHistory.setEarlyMorningHandicapOdds(oddsStr1 + "/" + addition1 + "/" + oddsStr2);
                        matchOddsHistory.setEarlyMorningOpenTime(createTime);
                    } else {
                        matchOddsHistory.setEarlyClosHandicapOdds(oddsStr1 + "/" + addition1 + "/" + oddsStr2);
                        matchOddsHistory.setEarlyClosOpenTime(createTime);
                    }
                    break;
                case 38://总分
                    oddsStr1 = dataSourceCode.equals("PA") ? oddsOver.getPaOddsValue() : oddsOver.getOriginalOddsValue();
                    oddsStr2 = dataSourceCode.equals("PA") ? oddsUnder.getPaOddsValue() : oddsUnder.getOriginalOddsValue();
                    //赛前
                    if (marketType == 1) {
                        matchOddsHistory.setEarlyMorningBigSmallOdds(oddsStr1 + "/" + addition1 + "/" + oddsStr2);
                        matchOddsHistory.setEarlyMorningOpenTime(createTime);
                    } else {
                        matchOddsHistory.setEarlyClosBigSmallOdds(oddsStr1 + "/" + addition1 + "/" + oddsStr2);
                        matchOddsHistory.setEarlyClosOpenTime(createTime);
                    }
                    break;
                case 37://全场独赢
                    oddsStr1 = dataSourceCode.equals("PA") ? odds1.getPaOddsValue() : odds1.getOriginalOddsValue();
                    oddsStr2 = dataSourceCode.equals("PA") ? odds2.getPaOddsValue() : odds2.getOriginalOddsValue();
                    //赛前
                    if (marketType == 1) {
                        matchOddsHistory.setEarlyMorningWinAloneOdds(oddsStr1 + "/" + oddsStr2);
                        matchOddsHistory.setEarlyMorningOpenTime(createTime);
                    } else {
                        matchOddsHistory.setEarlyClosWinAloneOdds(oddsStr1 + "/" + oddsStr2);
                        matchOddsHistory.setEarlyClosOpenTime(createTime);
                    }
                    break;
                default:
                    break;
            }
        });
        return matchOddsHistory;
    }

    /**
     * 通知AO rev 足球
     *
     * @param matchOddsHistories
     * @return
     */
    private List<MatchOddsHistory> aoRevFootBall(String linkId, Long StandardMatchId, Long sportId, List<MatchOddsHistory> matchOddsHistories) {
        List<MatchOddsHistory> oddsHistories = new ArrayList<>();
        for (MatchOddsHistory oddsHistory : matchOddsHistories) {
            try {
                Response reverseParam = applyService.getReverseParam(linkId, oddsHistory);
                log.error("::{}::aoRevFootBall,reverseParam：{}", linkId, JSONObject.toJSONString(reverseParam));
                MatchOddsHistory matchOddsHistory = JSONObject.parseObject(JSONObject.toJSONString(reverseParam.getData()),MatchOddsHistory.class);
                if (null == matchOddsHistory) {
                    oddsHistories.add(oddsHistory);
                } else {
                    oddsHistories.add(matchOddsHistory);
                }

            } catch (Exception e) {
                log.error("::{}::定时任务REV足球初盘盘口异常：{}", linkId, e);
                oddsHistories.add(oddsHistory);
            }
        }
        return oddsHistories;
    }

    /**
     * 通知AO rev 篮球
     *
     * @param matchOddsHistories
     * @return
     */
    private List<MatchOddsHistoryBasketball> aoRevBasketball(String linkId, Long StandardMatchId, Long sportId, List<MatchOddsHistoryBasketball> matchOddsHistories) {
        List<MatchOddsHistoryBasketball> oddsHistories = new ArrayList<>();
        for (MatchOddsHistoryBasketball oddsHistory : matchOddsHistories) {
            try {
                //log.info("::{}::aoRevBasketball:{}", linkId, JSONObject.toJSONString(oddsHistory));
                MatchOddsHistoryBasketball matchOddsHistoryBasketball = (MatchOddsHistoryBasketball) applyService.getBKReverseParam(linkId, oddsHistory).getData();
                oddsHistories.add(matchOddsHistoryBasketball);
            } catch (Exception e) {
                log.error("::{}::定时任务REV篮球初盘盘口异常：{}", linkId, e);
                oddsHistories.add(oddsHistory);
            }
        }
        return oddsHistories;
    }


    /**
     * 下发赛程补时
     *
     * @param linkId
     * @param standardMatchId
     * @param oddsHistories
     */
    private void sendMatchStoppageTime(String linkId, Long standardMatchId, List<MatchOddsHistory> oddsHistories) {
        MatchStoppagetimeHistory matchStoppagetimeHistory = new MatchStoppagetimeHistory();
        matchStoppagetimeHistory.setMatchId(standardMatchId);
        matchStoppagetimeHistory.setStoppageTimeHt(oddsHistories.get(0).getInjTime1st());
        matchStoppagetimeHistory.setStoppageTimeFull(oddsHistories.get(0).getInjTime2nd());
        matchFistMarketProducer.sendApplyInjTime(linkId, matchStoppagetimeHistory);
    }

}
