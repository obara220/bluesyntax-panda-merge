package com.panda.merge.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.pagehelper.PageHelper;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.dto.MatchEventInfoDetail;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.common.RcsHisDataService;
import com.panda.merge.service.MatchEventInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.ONE;

/**
 * @author : crazy
 * @project Name : panda-merge
 * @package Name : com.panda.merge.job
 * @description : 清理完赛1周以上的数据，事件，赔率，赛果（每天凌晨6点执行一次 ）
 * @date: 2020-10-17 17:25
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Component
@JobHandler(value = "CleanCacheByMatch")
public class CleanCacheByMatch extends IJobHandler {

    @Autowired
    private RcsHisDataService rcsHisDataService;


    /**
     * 循环删除事件可控上限次数
     */
    @NacosValue(value = "${clean-match.event.totalDeleted:10}", autoRefreshed = true)
    private Integer totalDeletedEventCount;

    @Override
    public ReturnT<String> execute(String param) {
        //默认10天
        Integer dayNum = 10;
        //默认一次清理赛事条数200
        Integer matchNum = 200;
        String methodName = "CleanCacheByMatch";
        Integer deleteEvent = 0;
        try {
            //log.info("【" + methodName + " 清理赛事相关数据】 开始,入参：{}", param);
            XxlJobLogger.log("【" + methodName + "清理赛事相关数据】 开始,入参：{}", param);
            if (StringUtils.isNotBlank(param)) {
                JSONObject jsonObj = JSON.parseObject(param);
                methodName = jsonObj.getString("methodName");
                //根据赛季ID清理TS联赛榜单,球员榜单数据
                String seasonId = jsonObj.getString("seasonId");
                if (StringUtils.isNotBlank(seasonId)) {
                    //log.info("【" + methodName + " 赛事分析相关数据】 开始,入参：{}", jsonObj);
                    XxlJobLogger.log("【" + methodName + " 清理赛事分析相关数据】 开始,入参：{}", jsonObj);
                    if ("cleanThirdSportRanking".equals(methodName)) {
                        String thirdTournamentSourceId = jsonObj.getString("thirdTournamentSourceId");
                        //log.info("cleanThirdSportRanking,【赛事分析相关数据】根据赛季清理联赛榜单数据开始,数据源赛季ID:{},数据源联赛ID:{}", seasonId,thirdTournamentSourceId);
                        cleanThirdSportRanking(seasonId,thirdTournamentSourceId);
                    }
                } else {
                    //默认一次清理赛事条数
                    if (null != jsonObj.getInteger("matchNum")) {
                        matchNum = jsonObj.getInteger("matchNum");
                    }
                    //需要清理多少天之前的数据
                    if (null != jsonObj.getInteger("dayNum")) {
                        dayNum = jsonObj.getInteger("dayNum");
                    }
                    //是否执行删除事件（0:否，1:是）
                    if (null != jsonObj.getInteger("deleteEvent")) {
                        deleteEvent = jsonObj.getInteger("deleteEvent");
                    }
                    //dayNum天前的时间戳
                    Long dayDateTime = System.currentTimeMillis() - dayNum * 24 * 60 * 60 * 1000L;
                    //log.info("【" + methodName + " 清理赛事相关数据】 业务处理开始,入参：{},dayDateTime:{}", jsonObj, dayDateTime);
                    XxlJobLogger.log("【" + methodName + " 清理赛事相关数据】 业务处理开始,入参：{},dayDateTime:{}", jsonObj, dayDateTime);
                    switch (methodName) {
                        //【标准赛事】清理标准赛事历史表数据
                        case "cleanStandardMatchInfoHis":
                            cleanStandardMatchInfoHis(dayDateTime);
                            break;
                        //【三方赛事】清理三方赛事关联数据
                        case "cleanEndedDayThirdMatch":
                            cleanEndedDayThirdMatch(dayDateTime, matchNum,deleteEvent);
                            break;
                        //【标准赛事】清理标准赛事关联数据
                        case "cleanEndedDayStandardMatch":
                            cleanEndedDayStandardMatch(dayDateTime, matchNum,deleteEvent);
                            break;
                        //【三方赛事】清理三方赛事半年以上数据
                        case "cleanHalfYearThirdMatch":
                            cleanHalfYearThirdMatch(dayDateTime, matchNum,deleteEvent);
                            break;
                        //【标准赛事】清理标准赛事半年以上数据
                        case "cleanHalfYearStandardMatch":
                            cleanHalfYearStandardMatch(dayDateTime, matchNum,deleteEvent);
                            break;
                        //【赛事分析相关】赛事关联信息清理
                        case "cleanTsThirdMatchHistory":
                            cleanTsThirdMatchHistory(dayDateTime, matchNum);
                            break;
                        //【赛事分析相关】赛事分析数据清理
                        case "cleanTsCacheDayByMatch":
                            cleanTsCacheDayByMatch(dayDateTime,matchNum);
                            break;
                        //【赛事分析相关】过期视频数据清理
                        case "cleanThirdVideoInfo":
                            cleanThirdVideoInfo(dayDateTime);
                            break;
                        //【赛事分析相关】历史百家赔清理
                        case "cleanTsCacheDayByHistoryMatch":
                            cleanTsCacheDayByHistoryMatch(dayDateTime,matchNum);
                            break;
                        //【事件数据】清理历史事件数据
                        case "cleanMatchEvenIdsByDayDateTime":
                            cleanMatchEvenIdsByDayDateTime(dayDateTime,matchNum,deleteEvent);
                            break;
                        default:
                            //log.info("【" + methodName + " 清理赛事相关数据】 default");
                            XxlJobLogger.log("【" + methodName + " 清理赛事相关数据】 default");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("【" + methodName + " 清理赛事相关数据】 异常,Exception:", e);
            XxlJobLogger.log("【" + methodName + " 清理赛事相关数据】 异常,Exception:" + e.getMessage());
        }
        //log.info("【" + methodName + " 清理赛事相关数据】 结束");
        XxlJobLogger.log("【" + methodName + " 清理赛事相关数据】 结束");
        return ReturnT.SUCCESS;
    }


    /**
     * 【标准赛事】清理标准赛事历史表数据
     * {"methodName":"cleanStandardMatchInfoHis","dayNum":30}
     * 0 0 5 1/1 * ? *
     */
    public void cleanStandardMatchInfoHis(Long dayDate) {
        try {
            //log.info("cleanStandardMatchInfoHis,每天清理N天前完赛标准赛事历史信息开始:{}", dayDate);
            StandardMatchInfoHisExample example = new StandardMatchInfoHisExample();
            example.createCriteria().andBeginTimeLessThanOrEqualTo(dayDate);
            int num = standardMatchInfoHisMapper.deleteByExample(example);
            //log.info("cleanStandardMatchInfoHis,每天清理N天前完赛标准赛事历史信息条数：{}", num);
        } catch (Exception e) {
            log.error("cleanStandardMatchInfoHis,每天清理N天前完赛标准赛事历史信息执行异常，Exception:", e);
        }
    }


    @Autowired
    private StandardMatchInfoHisMapper standardMatchInfoHisMapper;

    /**
     * 【标准赛事】清理标准赛事关联数据
     * {"methodName":"cleanEndedDayStandardMatch","dayNum":5,"matchNum":500}
     * 48 5/10 * * * ?
     */
    public void cleanEndedDayStandardMatch(Long dayDateTime, Integer matchNum,Integer deleteEvent) {
        rcsHisDataService.cleanEndedDayStandardMatch(dayDateTime, matchNum, YesNoEnum.Y.value,deleteEvent);
    }


    /**
     * 【三方赛事】清理三方赛事关联数据
     * {"methodName":"cleanEndedDayThirdMatch","dayNum":10,"matchNum":500}
     * 30 1/10 * * * ?
     */
    public void cleanEndedDayThirdMatch(Long dayDateTime, Integer matchNum,Integer deleteEvent) {
        rcsHisDataService.cleanEndedDayThirdMatch(dayDateTime, matchNum, YesNoEnum.Y.value,deleteEvent);
    }


    /**
     * 【标准赛事】清理标准赛事半年以上数据
     * {"methodName":"cleanHalfYearStandardMatch","dayNum":180,"matchNum":500}
     *  0 10 1/1 * * ? *
     */
    public void cleanHalfYearStandardMatch(Long dayDateTime, Integer matchNum,Integer deleteEvent) {
        //当前时间最少要大于传入时间90天
        if (System.currentTimeMillis() - dayDateTime > (90 * 24 * 60 * 60 * 1000L)) {
            rcsHisDataService.cleanEndedDayStandardMatch(dayDateTime, matchNum, YesNoEnum.N.value,deleteEvent);
        } else {
            //log.info("【cleanHalfYearStandardMatch 清理半年前标准赛事相关信息】 传入时间{}不小于90天,不清理历史赛事", dayDateTime);
        }
    }


    /**
     * 【三方赛事】清理三方赛事半年以上数据
     * {"methodName":"cleanHalfYearThirdMatch","dayNum":180,"matchNum":500}
     * 0 20 1/1 * * ? *
     */
    public void cleanHalfYearThirdMatch(Long dayDateTime, Integer matchNum,Integer deleteEvent) {
        if (System.currentTimeMillis() - dayDateTime > (90 * 24 * 60 * 60 * 1000L)) {
            rcsHisDataService.cleanEndedDayThirdMatch(dayDateTime, matchNum, YesNoEnum.N.value,deleteEvent);
        } else {
            //log.info("【cleanHalfYearStandardMatch 清理半年前三方赛事相关信息】 传入时间{}不小于90天,不清理历史赛事", dayDateTime);
        }
    }


    @Autowired
    private ThirdMatchHistoryStatisticsMapper thirdMatchHistoryStatisticsMapper;
    @Autowired
    private ThirdVideoBoardCastRecordMapper thirdVideoBoardCastRecordMapper;
    @Autowired
    private ThirdMatchLineupMapper thirdMatchLineupMapper;
    @Autowired
    private ThirdMatchExInfomationMapper thirdMatchExInfomationMapper;
    @Autowired
    private ThirdMatchSidelinedMapper thirdMatchSidelinedMapper;
    @Autowired
    private ThirdMatchHistoryOddsMapper thirdMatchHistoryOddsMapper;
    @Autowired
    private ThirdMatchPhraseMapper thirdMatchPhraseMapper;
    @Autowired
    private ThirdMatchFrontStatisticsMapper thirdMatchFrontStatisticsMapper;
    @Autowired
    private ThirdMatchTeamSkillStatisticsMapper thirdMatchTeamSkillStatisticsMapper;
    @Autowired
    private ThirdMatchPromotionChartMapper thirdMatchPromotionChartMapper;

    /**
     * 【赛事分析相关】赛事关联信息清理
     * {"methodName":"cleanTsThirdMatchHistory","dayNum":10,"matchNum":500}
     * 	0 40 1/1 * * ? *
     */
    public void cleanTsThirdMatchHistory(Long dayDateTime, Integer matchNum) {
        try {
            //log.info("cleanTsThirdMatchHistory,【赛事分析相关】每天清理N天前完赛历史赛事信息开始，dayDateTime：{}，matchNum：{}", dayDateTime, matchNum);
            ThirdMatchHistoryStatisticsExample matchExample = new ThirdMatchHistoryStatisticsExample();
            matchExample.createCriteria().andBeginTimeLessThanOrEqualTo(System.currentTimeMillis()).andModifyTimeLessThanOrEqualTo(dayDateTime);
            PageHelper.startPage(ONE, matchNum);
            List<ThirdMatchHistoryStatistics> resMatchList = thirdMatchHistoryStatisticsMapper.selectByExample(matchExample);
            if (!CollectionUtils.isEmpty(resMatchList)) {
                int size = resMatchList.size();
                //赛事信息按照数据源编码分组
                Map<String, List<ThirdMatchHistoryStatistics>> dataSourceCode2Map = resMatchList.stream().collect(Collectors.groupingBy(obj -> obj.getDataSourceCode()));
                //log.info("::"+dayDateTime+"::,cleanTsThirdMatchHistory,每天清理N天前完赛历史赛事数据,本次需要清理的总条数={},包含数据源编码={}", size,JSON.toJSONString(dataSourceCode2Map.keySet()));
                for (String dataSourceCode: dataSourceCode2Map.keySet()) {
                    List<ThirdMatchHistoryStatistics> matchInfosByCode = dataSourceCode2Map.get(dataSourceCode);
                    //log.info("::"+dayDateTime+"::,cleanEndedDayThirdMatch,每天清理N天前完赛历史赛事数据,数据源{}本次需要清理的总条数：{}",dataSourceCode,matchInfosByCode.size());
                    //需要清理的数据源赛事ID
                    List<String> thirdMatchSourceIds = resMatchList.stream().map(obj -> obj.getThirdMatchSourceId()).collect(Collectors.toList());

                    //视频数据
                    ThirdVideoBoardCastRecordExample videoExample = new ThirdVideoBoardCastRecordExample();
                    videoExample.createCriteria().andMatchIdIn(thirdMatchSourceIds).andDataSourceCodeEqualTo(dataSourceCode);
                    int videoNum = thirdVideoBoardCastRecordMapper.deleteByExample(videoExample);
                    //log.info("::"+dayDateTime+"::,cleanTsThirdMatchHistory,清除"+dataSourceCode+"视频数据{}条", videoNum);

                    //阵容数据
                    ThirdMatchLineupExample lineupExample = new ThirdMatchLineupExample();
                    lineupExample.createCriteria().andThirdMatchSourceIdIn(thirdMatchSourceIds).andDataSourceCodeEqualTo(dataSourceCode);
                    int lineupNum = thirdMatchLineupMapper.deleteByExample(lineupExample);
                    //log.info("::"+dayDateTime+"::,cleanTsThirdMatchHistory,清除"+dataSourceCode+"阵容数据{}条", lineupNum);

                    //比赛情报综合资讯数据
                    ThirdMatchExInfomationExample exInfoExample = new ThirdMatchExInfomationExample();
                    exInfoExample.createCriteria().andThirdMatchSourceIdIn(thirdMatchSourceIds).andDataSourceCodeEqualTo(dataSourceCode);
                    int exInfoNum = thirdMatchExInfomationMapper.deleteByExample(exInfoExample);
                    //log.info("::"+dayDateTime+"::,cleanTsThirdMatchHistory,清除"+dataSourceCode+"比赛情报综合资讯数据{}条", exInfoNum);

                    //球员伤停信息数据
                    ThirdMatchSidelinedExample sidelinedExample = new ThirdMatchSidelinedExample();
                    sidelinedExample.createCriteria().andThirdMatchSourceIdIn(thirdMatchSourceIds).andDataSourceCodeEqualTo(dataSourceCode);
                    int sidelinedNum = thirdMatchSidelinedMapper.deleteByExample(sidelinedExample);
                    //log.info("::"+dayDateTime+"::,cleanTsCacheDayByMatch,清除"+dataSourceCode+"球员伤停数据{}条", sidelinedNum);

                    //赛事文字直播数据
                    ThirdMatchPhraseExample phraseExample = new ThirdMatchPhraseExample();
                    phraseExample.createCriteria().andThirdMatchSourceIdIn(thirdMatchSourceIds).andDataSourceCodeEqualTo(dataSourceCode);
                    int phrasesNum = thirdMatchPhraseMapper.deleteByExample(phraseExample);
                    //log.info("::"+dayDateTime+"::,cleanTsCacheDayByMatch,清除"+dataSourceCode+"赛事文字直播数据{}条", phrasesNum);

                    //历史赛事赔率数据
                    ThirdMatchHistoryOddsExample matchOddsExample = new ThirdMatchHistoryOddsExample();
                    matchOddsExample.createCriteria().andThirdMatchSourceIdIn(thirdMatchSourceIds).andDataSourceCodeEqualTo(dataSourceCode);
                    int oddsNum = thirdMatchHistoryOddsMapper.deleteByExample(matchOddsExample);
                    //log.info("::"+dayDateTime+"::,cleanTsThirdMatchHistory,清除"+dataSourceCode+"历史赛事赔率数据{}条", oddsNum);

                    //正面交手信息
                    ThirdMatchFrontStatisticsExample matchFrontStatisticsExample = new ThirdMatchFrontStatisticsExample();
                    matchFrontStatisticsExample.createCriteria().andThirdMatchSourceIdIn(thirdMatchSourceIds).andDataSourceCodeEqualTo(dataSourceCode);
                    int frontNum = thirdMatchFrontStatisticsMapper.deleteByExample(matchFrontStatisticsExample);
                    //log.info("::"+dayDateTime+"::,cleanTsThirdMatchHistory,清除"+dataSourceCode+"正面交手信息赛事数据{}条", frontNum);

                    ThirdMatchTeamSkillStatisticsExample skillExample = new ThirdMatchTeamSkillStatisticsExample();
                    skillExample.createCriteria().andMatchIdIn(thirdMatchSourceIds).andDataSourceCodeEqualTo(dataSourceCode);
                    int skillNum = thirdMatchTeamSkillStatisticsMapper.deleteByExample(skillExample);
                    //log.info("::"+dayDateTime+"::,cleanTsThirdMatchHistory,清除"+dataSourceCode+"赛事球队技术统计数据{}条", skillNum);

//                    ThirdMatchPromotionChartExample promotionChartExample = new ThirdMatchPromotionChartExample();
//                    promotionChartExample.createCriteria().andMatchIdIn(thirdMatchSourceIds).andDataSourceCodeEqualTo(dataSourceCode);
//                    int promotionChartNum = thirdMatchPromotionChartMapper.deleteByExample(promotionChartExample);
//                    //log.info("::"+dayDateTime+"::,cleanTsThirdMatchHistory,清除"+dataSourceCode+"杯赛淘汰赛事数据{}条", promotionChartNum);

                    //历史赛事数据
                    ThirdMatchHistoryStatisticsExample matchHistoryExample = new ThirdMatchHistoryStatisticsExample();
                    matchHistoryExample.createCriteria().andThirdMatchSourceIdIn(thirdMatchSourceIds).andDataSourceCodeEqualTo(dataSourceCode);
                    int tsMatchNum = thirdMatchHistoryStatisticsMapper.deleteByExample(matchHistoryExample);
                    //log.info("::"+dayDateTime+"::,cleanTsThirdMatchHistory,清除"+dataSourceCode+"历史赛事数据{}条", tsMatchNum);
                }
            }
            //log.info("::"+dayDateTime+"::,cleanTsThirdMatchHistory,【赛事分析相关】每天清理N天前完赛历史赛事信息结束");
        } catch (Exception e) {
            log.error("::"+dayDateTime+"::,cleanTsThirdMatchHistory,【赛事分析相关】每天清理N天前完赛TS历史赛事信息执行异常，Exception:", e);
        }
    }

    /**
     * 【赛事分析相关】赛事分析数据清理
     * {"methodName":"cleanTsCacheDayByMatch","dayNum":7}
     * 0 0 11 1/1 * ? *
     */
    public void cleanTsCacheDayByMatch(Long dayDate, Integer matchNum) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            //log.info("cleanTsCacheDayByMatch,每天清理N天前无修改的赛事分析相关数据开始:{}", dayDate);
            //视频数据
            ThirdVideoBoardCastRecordExample videoExample = new ThirdVideoBoardCastRecordExample();
            videoExample.createCriteria().andModifyTimeLessThanOrEqualTo(dayDate);
            List<ThirdVideoBoardCastRecord> videoList = thirdVideoBoardCastRecordMapper.selectByExample(videoExample);
            if (!CollectionUtils.isEmpty(videoList)) {
                List<String> ids = videoList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                ThirdVideoBoardCastRecordExample delExample = new ThirdVideoBoardCastRecordExample();
                if(ids.size() > matchNum){
                    delExample.createCriteria().andIdIn(ids.subList(0,matchNum));
                }else{
                    delExample.createCriteria().andIdIn(ids);
                }
                int num = thirdVideoBoardCastRecordMapper.deleteByExample(delExample);
                //log.info("cleanTsCacheDayByMatch,清除视频数据{}条，成功：{}", ids.size(), num);
            }

            //阵容数据
            ThirdMatchLineupExample lineupExample = new ThirdMatchLineupExample();
            lineupExample.createCriteria().andModifyTimeLessThanOrEqualTo(dayDate);
            List<ThirdMatchLineup> lineupList = thirdMatchLineupMapper.selectByExample(lineupExample);
            if (!CollectionUtils.isEmpty(lineupList)) {
                List<String> ids = lineupList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                ThirdMatchLineupExample delExample = new ThirdMatchLineupExample();
                if(ids.size() > matchNum){
                    delExample.createCriteria().andIdIn(ids.subList(0,matchNum));
                }else{
                    delExample.createCriteria().andIdIn(ids);
                }
                int num = thirdMatchLineupMapper.deleteByExample(delExample);
                //log.info("cleanTsCacheDayByMatch,清除阵容数据{}条，成功：{}", ids.size(), num);
            }

            //球员伤停数据
            ThirdMatchSidelinedExample sidelinedExample = new ThirdMatchSidelinedExample();
            sidelinedExample.createCriteria().andModifyTimeLessThanOrEqualTo(dayDate);
            List<ThirdMatchSidelined> sidelinedList = thirdMatchSidelinedMapper.selectByExample(sidelinedExample);
            if (!CollectionUtils.isEmpty(sidelinedList)) {
                List<String> ids = sidelinedList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                ThirdMatchSidelinedExample delExample = new ThirdMatchSidelinedExample();
                if(ids.size() > matchNum){
                    delExample.createCriteria().andIdIn(ids.subList(0,matchNum));
                }else{
                    delExample.createCriteria().andIdIn(ids);
                }
                int num = thirdMatchSidelinedMapper.deleteByExample(delExample);
                //log.info("cleanTsCacheDayByMatch,清除球员伤停数据{}条，成功：{}", ids.size(), num);
            }

            //比赛情报综合资讯数据
            ThirdMatchExInfomationExample exInfoExample = new ThirdMatchExInfomationExample();
            exInfoExample.createCriteria().andModifyTimeLessThanOrEqualTo(dayDate);
            List<ThirdMatchExInfomation> exInfoList = thirdMatchExInfomationMapper.selectByExample(exInfoExample);
            if (!CollectionUtils.isEmpty(exInfoList)) {
                List<String> ids = exInfoList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                ThirdMatchExInfomationExample delExample = new ThirdMatchExInfomationExample();
                if(ids.size() > matchNum){
                    delExample.createCriteria().andIdIn(ids.subList(0,matchNum));
                }else{
                    delExample.createCriteria().andIdIn(ids);
                }
                int num = thirdMatchExInfomationMapper.deleteByExample(delExample);
                //log.info("cleanTsCacheDayByMatch,清除比赛情报综合资讯数据{}条，成功：{}", ids.size(), num);
            }

            //赛事文字直播数据
            ThirdMatchPhraseExample phraseExample = new ThirdMatchPhraseExample();
            phraseExample.createCriteria().andModifyTimeLessThanOrEqualTo(dayDate);
            List<ThirdMatchPhrase> phrasesList = thirdMatchPhraseMapper.selectByExample(phraseExample);
            if (!CollectionUtils.isEmpty(phrasesList)) {
                List<String> ids = phrasesList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                ThirdMatchPhraseExample delExample = new ThirdMatchPhraseExample();
                if(ids.size() > matchNum){
                    delExample.createCriteria().andIdIn(ids.subList(0,matchNum));
                }else{
                    delExample.createCriteria().andIdIn(ids);
                }
                int num = thirdMatchPhraseMapper.deleteByExample(delExample);
                //log.info("cleanTsCacheDayByMatch,清除赛事文字直播数据{}条，成功：{}", ids.size(), num);
            }


            ThirdMatchTeamSkillStatisticsExample skillExample = new ThirdMatchTeamSkillStatisticsExample();
            skillExample.createCriteria().andModifyTimeLessThanOrEqualTo(dayDate);
            List<ThirdMatchTeamSkillStatistics> skillList = thirdMatchTeamSkillStatisticsMapper.selectByExample(skillExample);
            if (!CollectionUtils.isEmpty(skillList)) {
                List<String> ids = skillList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                ThirdMatchTeamSkillStatisticsExample delExample = new ThirdMatchTeamSkillStatisticsExample();
                if(ids.size() > matchNum){
                    delExample.createCriteria().andIdIn(ids.subList(0,matchNum));
                }else{
                    delExample.createCriteria().andIdIn(ids);
                }
                int num = thirdMatchTeamSkillStatisticsMapper.deleteByExample(delExample);
                //log.info("cleanTsCacheDayByMatch,清除赛事球队技术统计数据{}条，成功：{}", ids.size(), num);
            }

            ThirdMatchPromotionChartExample promotionChartExample = new ThirdMatchPromotionChartExample();
            promotionChartExample.createCriteria().andModifyTimeLessThanOrEqualTo(dayDate);
            List<ThirdMatchPromotionChart> promotionChartList = thirdMatchPromotionChartMapper.selectByExample(promotionChartExample);
            if (!CollectionUtils.isEmpty(promotionChartList)) {
                List<String> ids = promotionChartList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                ThirdMatchPromotionChartExample delExample = new ThirdMatchPromotionChartExample();
                if(ids.size() > matchNum){
                    delExample.createCriteria().andIdIn(ids.subList(0,matchNum));
                }else{
                    delExample.createCriteria().andIdIn(ids);
                }
                int num = thirdMatchPromotionChartMapper.deleteByExample(delExample);
                //log.info("cleanTsCacheDayByMatch,清除杯赛淘汰赛事数据{}条，成功：{}", ids.size(), num);
            }

            //清除TS历史赛事数据
            ThirdMatchHistoryStatisticsExample matchHistoryExample = new ThirdMatchHistoryStatisticsExample();
            matchHistoryExample.createCriteria().andBeginTimeLessThanOrEqualTo(System.currentTimeMillis()).andModifyTimeLessThanOrEqualTo(dayDate);
            List<ThirdMatchHistoryStatistics> matchHistoryList = thirdMatchHistoryStatisticsMapper.selectByExample(matchHistoryExample);
            if (!CollectionUtils.isEmpty(matchHistoryList)) {
                List<String> ids = matchHistoryList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                ThirdMatchHistoryStatisticsExample delExample = new ThirdMatchHistoryStatisticsExample();
                if(ids.size() > matchNum){
                    delExample.createCriteria().andIdIn(ids.subList(0,matchNum));
                }else{
                    delExample.createCriteria().andIdIn(ids);
                }
                int tsMatchNum = thirdMatchHistoryStatisticsMapper.deleteByExample(delExample);
                //log.info("cleanTsCacheDayByHistoryMatch,清除TS历史赛事数据{}条", tsMatchNum);
                XxlJobLogger.log("cleanTsCacheDayByHistoryMatch,清除TS历史赛事数据{}条", tsMatchNum);
            }
        } catch (Exception e) {
            //log.info("cleanTsCacheDayByMatch,每天清理N天前无修改的赛事分析相关数据执行异常，Exception:", e);
        }
        stopWatch.stop();
        //log.info("cleanTsCacheDayByMatch,每天清理N天前无修改的赛事分析相关数据执行用时{}毫秒", stopWatch.getTotalTimeMillis());
    }

    /**
     * 【赛事分析相关】过期视频数据清理
     * {"methodName":"cleanThirdVideoInfo","dayNum":10}
     * 	0 0 2 1/1 * ? *
     */
    public void cleanThirdVideoInfo(Long dayDate) {
        try {
            //log.info("cleanThirdVideoInfo,每天清理N天前完赛视频信息开始:{}", dayDate);
            ThirdVideoBoardCastRecordExample example = new ThirdVideoBoardCastRecordExample();
            example.createCriteria().andModifyTimeLessThanOrEqualTo(dayDate);
            int num = thirdVideoBoardCastRecordMapper.deleteByExample(example);
            //log.info("cleanThirdVideoInfo,每天清理N天前完赛视频信息条数：{}", num);
        } catch (Exception e) {
            log.error("cleanThirdVideoInfo,每天清理N天前完赛视频信息执行异常，Exception:", e);
        }
    }

    @Autowired
    private MatchEventInfoMapper matchEventInfoMapper;

    /**
     * 【赛事分析相关】历史百家赔清理
     * {"methodName":"cleanTsCacheDayByHistoryMatch","dayNum":10,"matchNum":5000}
     * 	35 4/10 * * * ? *
     */
    public void cleanTsCacheDayByHistoryMatch(Long dayDateTime,Integer matchEventNum) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            //log.info("cleanTsCacheDayByHistoryMatch,每天清理N天前TS历史赛事信息开始:{},matchEventNum={}", dayDateTime,matchEventNum);
            XxlJobLogger.log("cleanTsCacheDayByHistoryMatch,每天清理N天前TS历史赛事信息开始:{},matchEventNum={}", dayDateTime,matchEventNum);
            //清除TS历史赛事赔率数据
            ThirdMatchHistoryOddsExample matchOddsExample = new ThirdMatchHistoryOddsExample();
            matchOddsExample.createCriteria().andModifyTimeLessThanOrEqualTo(dayDateTime);
            PageHelper.startPage(ONE, matchEventNum);
            List<ThirdMatchHistoryOdds> resOddsList = thirdMatchHistoryOddsMapper.selectByExample(matchOddsExample);
            int oddsNum = 0;
            if(!CollectionUtils.isEmpty(resOddsList)){
                List<String> ids = resOddsList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                ThirdMatchHistoryOddsExample matchOddsDelExample = new ThirdMatchHistoryOddsExample();
                matchOddsDelExample.createCriteria().andIdIn(ids);
                oddsNum = thirdMatchHistoryOddsMapper.deleteByExample(matchOddsDelExample);
//                thirdMatchHistoryOddsMapper.deleteByExample(matchOddsExample);
            }
            //log.info("cleanTsCacheDayByHistoryMatch,清除TS历史赛事赔率数据{}条", oddsNum);
            XxlJobLogger.log("cleanTsCacheDayByHistoryMatch,清除TS历史赛事赔率数据{}条", oddsNum);

            //清除TS历史赛事视频数据
            ThirdVideoBoardCastRecordExample videoExample = new ThirdVideoBoardCastRecordExample();
            videoExample.createCriteria().andModifyTimeLessThanOrEqualTo(dayDateTime);
            int videoNum = thirdVideoBoardCastRecordMapper.deleteByExample(videoExample);
            //log.info("cleanTsCacheDayByHistoryMatch,清除TS历史赛事视频数据{}条", videoNum);
            XxlJobLogger.log("cleanTsCacheDayByHistoryMatch,清除TS历史赛事视频数据{}条", videoNum);

        } catch (Exception e) {
            log.error("cleanTsCacheDayByHistoryMatch,每天清理N天前TS历史赛事信息执行异常，Exception:", e);
            XxlJobLogger.log("cleanTsCacheDayByHistoryMatch,每天清理N天前TS历史赛事信息执行异常，Exception:", e);
        }
        stopWatch.stop();
        //log.info("cleanTsCacheDayByHistoryMatch,每天清理N天前TS历史赛事信息执行用时{}毫秒", stopWatch.getTotalTimeMillis());
        XxlJobLogger.log("cleanTsCacheDayByHistoryMatch,每天清理N天前TS历史赛事信息执行用时{}毫秒", stopWatch.getTotalTimeMillis());
    }


    @Autowired
    private ThirdSportTeamRankingMapper thirdSportTeamRankingMapper;

    @Autowired
    private ThirdSportPlayerRankingMapper thirdSportPlayerRankingMapper;

    @Autowired
    private ThirdMatchSeasonStatisticsMapper thirdMatchSeasonStatisticsMapper;

    @Autowired
    private ThirdMatchHistoryExpressionMapper thirdMatchHistoryExpressionMapper;

    /**
     * 根据赛季ID清理联赛榜单,球员榜单数据
     * 【赛事分析相关】手动清理榜单数据
     * {"methodName":"cleanThirdSportRanking","seasonId":""}
     */
    public void cleanThirdSportRanking(String seasonId,String thirdTournamentSourceId) {
        ThirdSportTeamRankingExample example1 = new ThirdSportTeamRankingExample();
        example1.createCriteria().andThirdSourceSeasonIdEqualTo(seasonId);
        if(StringUtils.isNotBlank(thirdTournamentSourceId)){
            example1.createCriteria().andThirdTournamentSourceIdEqualTo(thirdTournamentSourceId);
        }
        int num1 = thirdSportTeamRankingMapper.deleteByExample(example1);
        //log.info("cleanThirdSportRanking,根据赛季ID:{}清理TS联赛榜单数据，成功{}条", seasonId, num1);
        XxlJobLogger.log("cleanThirdSportRanking,根据赛季ID:{}清理TS联赛榜单数据，成功{}条", seasonId, num1);

        ThirdSportPlayerRankingExample example2 = new ThirdSportPlayerRankingExample();
        example2.createCriteria().andThirdSourceSeasonIdEqualTo(seasonId);
        if(StringUtils.isNotBlank(thirdTournamentSourceId)){
            example2.createCriteria().andThirdTournamentSourceIdEqualTo(thirdTournamentSourceId);
        }
        int num2 = thirdSportPlayerRankingMapper.deleteByExample(example2);
        //log.info("cleanThirdSportRanking,根据赛季ID:{}清理TS联赛球员榜单数据，成功{}条", seasonId, num2);
        XxlJobLogger.log("cleanThirdSportRanking,根据赛季ID:{}清理TS联赛球员榜单数据，成功{}条", seasonId, num2);

        ThirdMatchSeasonStatisticsExample example3 = new ThirdMatchSeasonStatisticsExample();
        example3.createCriteria().andThirdSourceSeasonIdEqualTo(seasonId);
//        if(StringUtils.isNotBlank(thirdTournamentSourceId)){
//            example3.createCriteria().andThird(thirdTournamentSourceId);
//        }
        int num3 = thirdMatchSeasonStatisticsMapper.deleteByExample(example3);
        //log.info("cleanThirdSportRanking,根据赛季ID:{}清理赛季统计数据，成功{}条", seasonId, num3);
        XxlJobLogger.log("cleanThirdSportRanking,根据赛季ID:{}清理赛季统计榜单数据，成功{}条", seasonId, num3);

        if(StringUtils.isNotBlank(thirdTournamentSourceId)){
            ThirdMatchHistoryExpressionExample example4 = new ThirdMatchHistoryExpressionExample();
            example4.createCriteria().andThirdTournamentSourceIdEqualTo(thirdTournamentSourceId);
            int num4 = thirdMatchHistoryExpressionMapper.deleteByExample(example4);
            //log.info("cleanThirdSportRanking,赛季ID：{},根据源联赛ID:{},清理联赛球队历史表现数据，成功{}条",seasonId, thirdTournamentSourceId, num4);
            XxlJobLogger.log("cleanThirdSportRanking,赛季ID：{},根据源联赛ID:{},根据源联赛ID:{}清理联赛球队历史表现数据，成功{}条",seasonId,thirdTournamentSourceId, num4);
        }

    }


    @Autowired
    private MatchEventInfoService matchEventInfoService;

    /**
     * 【事件数据】清理历史事件数据
     * {"methodName":"cleanMatchEvenIdsByDayDateTime","dayNum":5,"matchNum":5000,"deleteEvent":1}
     * 12 2/10 * * * ?
     * */
    public void cleanMatchEvenIdsByDayDateTime(Long dayDateTime,Integer matchEventNum,Integer deleteEvent) {
        for (String dataSourceCode : DataSourceCodeEnum.getEventCodeList()) {
            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                Integer num;
                if(deleteEvent == 1){
                    int count = 0;
                    int totalDeleted = 0;

                    while (true) {
                        int rows = cleanMatchEventInfoData(dataSourceCode,dayDateTime,matchEventNum);

                        totalDeleted += rows;
                        count++;

                        log.info("linkId=【" + dayDateTime + "】," + matchEventNum + ",deleteMatchEvenIdsByDayDateTime,数据源编码={},第{}批,删除{},累计{}", dataSourceCode, count, rows, totalDeleted);

                        if (rows == 0 || rows < matchEventNum) {
                            log.info("linkId=【" + dayDateTime + "】," + matchEventNum + ",deleteMatchEvenIdsByDayDateTime,数据源编码={},删除完成", dataSourceCode);
                            break;
                        }

                        if (count >= totalDeletedEventCount) {
                            log.info("linkId=【" + dayDateTime + "】," + matchEventNum + ",deleteMatchEvenIdsByDayDateTime,数据源编码={},达到最大批次数，停止（防止风险）", dataSourceCode);
                            break;
                        }

                        Thread.sleep(500);
                    }
                    num = totalDeleted;
                }else{
                    MatchEventInfoDetail matchEventInfoDetail = new MatchEventInfoDetail();
                    matchEventInfoDetail.setTableName("match_event_info_"+dataSourceCode.toLowerCase(Locale.ROOT));
                    matchEventInfoDetail.setDataSourceCode(dataSourceCode);
                    matchEventInfoDetail.setDayDateTime(dayDateTime);
                    matchEventInfoDetail.setSize(matchEventNum);
                    num = matchEventInfoService.deleteMatchEvenIdsByDayDateTime(matchEventInfoDetail);
                }
                stopWatch.stop();
                //log.info("linkId=【" + dayDateTime + "】," + matchEventNum + ",deleteMatchEvenIdsByDayDateTime,数据源编码={},清理条数={},deleteEvent={},耗时={}", dataSourceCode, num,deleteEvent, stopWatch.getTotalTimeMillis());
            } catch (Exception e) {
                log.error("linkId=【" + dayDateTime + "】," + matchEventNum + ",deleteMatchEvenIdsByDayDateTime,数据源编码=" + dataSourceCode + "本次清理三方赛事事件脏数据异常，Exception:", e);
            }
        }

    }

    /**
     * 因为RcsHisDataService.cleanMatchEventInfoData 方法使用了线程池,返回null,所以把方法复制了一份
     * @param dataSourceCode
     * @param dayDateTime
     * @param matchEventNum
     * @return
     */
    private int cleanMatchEventInfoData(String dataSourceCode,Long dayDateTime,Integer matchEventNum){
        MatchEventInfoDetail matchEventInfoDetail = new MatchEventInfoDetail();
        matchEventInfoDetail.setTableName("match_event_info_"+dataSourceCode.toLowerCase(Locale.ROOT));
        matchEventInfoDetail.setDataSourceCode(dataSourceCode);
        matchEventInfoDetail.setDayDateTime(dayDateTime);
        matchEventInfoDetail.setSize(matchEventNum);
        PageHelper.startPage(ONE, matchEventNum);
        List<MatchEventInfo> resMatchEventInfoList = matchEventInfoService.getMatchEvenIdsByDayDateTime(matchEventInfoDetail);
        if(!CollectionUtils.isEmpty(resMatchEventInfoList)){
            List<Long> eventIds = resMatchEventInfoList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
            MatchEventInfoExample matchEventInfoDelExample = new MatchEventInfoExample();
            matchEventInfoDelExample.createCriteria().andDataSourceCodeEqualTo(dataSourceCode).andIdIn(eventIds);
            return matchEventInfoMapper.deleteByExample(matchEventInfoDelExample);
        }
        return 0;
    }
}
