package com.panda.merge.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.panda.merge.common.enums.DataSourceCodeEnum;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.dto.scores.B02ScoresSourceDTO;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
import com.panda.merge.rocketmq.common.RcsHisDataService;
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
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.ONE;

/**
 * @author : fymen
 * @project Name : panda-merge
 * @package Name : com.panda.merge.job
 * @description : 清理完赛1周以上的数据，事件，赔率，赛果（每天凌晨6点执行一次 ）
 * @date: 2020-10-17 17:25
 * @modificationHistory Who When What
 * -------- --------- --------------------------
 */
@Slf4j
@Component
@JobHandler(value = "CleanMatchScoresData")
public class CleanMatchScoresDataJob extends IJobHandler {

    @Autowired
    private MatchScoresInfoMapper matchScoresInfoMapper;

    @Autowired
    private MatchTimeInfoMapper matchTimeInfoMapper;

    @Autowired
    private MatchScoresSourceTypeMapper matchScoresSourceTypeMapper;

    @Autowired
    private MatchScoresEventInfoMapper matchScoresEventInfoMapper;

    @Autowired
    private MatchSettleResultMapper matchSettleResultMapper;

    @Autowired
    private MatchScoresPdLogMapper matchScoresPdLogMapper;

    @Autowired
    private StandardMatchScoresMapper standardMatchScoresMapper;

    @Autowired
    private MatchScoresCenterLogMapper matchScoresCenterLogMapper;
    @Autowired
    private B02ScoresSourceMapper b02ScoresSourceMapper;
    @Override
    public ReturnT<String> execute(String param) {
        Integer dayNum = 90;
        //默认一次清理赛事条数5
        Integer matchNum = 50;
        String methodName = "cleanMatchScoresData";
        try {
            //log.info("【" + methodName + " 清理比分相关数据】 开始,入参：{}", param);
            XxlJobLogger.log("【" + methodName + "清理比分相关数据】 开始,入参：{}", param);
            if (StringUtils.isNotBlank(param)) {
                JSONObject jsonObj = JSON.parseObject(param);
                methodName = jsonObj.getString("methodName");
                //默认一次清理赛事条数
                if (null != jsonObj.getInteger("matchNum")) {
                    matchNum = jsonObj.getInteger("matchNum");
                }
                //需要清理多少天之前的数据
                if (null != jsonObj.getInteger("dayNum")) {
                    dayNum = jsonObj.getInteger("dayNum");
                }
                //dayNum天前的时间戳
                Long dayDateTime = System.currentTimeMillis() - dayNum * 24 * 60 * 60 * 1000L;
                //log.info("【" + methodName + " 清理赛事相关数据】 业务处理开始,入参：{},dayDateTime:{}", jsonObj, dayDateTime);
                XxlJobLogger.log("【" + methodName + " 清理赛事相关数据】 业务处理开始,入参：{},dayDateTime:{}", jsonObj, dayDateTime);

                switch (methodName) {
                    case "deleteStandMatchScore":
                        //删除网乒羽排标准比分和日志standard_match_scores、match_scores_center_log
                        deleteStandMatchScore(dayDateTime);
                        break;
                    case "deleteMatchScoresPdLogs":
                        //删除报球板日志表match_scores_pd_log
                        deleteMatchScoresPdLogs(dayDateTime,matchNum);
                        break;
                    case "deleteMatchScoresInfo":
                        //删除三方比分数据 match_scores_info
                        deleteMatchScoresInfo(dayDateTime,matchNum);
                        break;
                    case "deleteMatchScoresEventInfo":
                        //报球板事件表 match_scores_event_info
                        deleteMatchScoresEventInfo(dayDateTime,matchNum);
                        break;
                    case "deleteMatchSettleResult":
                        //删除结算赛果 match_settle_result
                        deleteMatchSettleResult(dayDateTime);
                        break;
                    case "cleanMatchScoresData":
                        //通过三方赛事信息删除 、match_time_info、match_scores_source_type
                        deleteScoreInfoDataByThirdMatchInfo(dayDateTime,matchNum);
                        break;
                    case "deleteB02ScoreSourceLogs":
                        //B02_scores_source
                        deleteB02ScoreSourceLogs(dayDateTime);
                        break;
                    default:
                        //log.info("【" + methodName + " 清理赛事相关数据】 default");
                        XxlJobLogger.log("【" + methodName + " 清理赛事相关数据】 default");
                        break;
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

    private void deleteB02ScoreSourceLogs(Long dayDateTime) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String linkId = StringUtils.join(dayDateTime, "_clean_B02_source_type_");
        //log.info("::" + linkId + "::,deleteB02ScoreSourceLogs,定时B02比分通道切换记录，dayDateTime：{}", dayDateTime);
        try {
            B02ScoresSourceDTO dto = new B02ScoresSourceDTO();
            dto.setCreateTime(dayDateTime);
            int matchScoresNums = b02ScoresSourceMapper.delete(dto);
            //log.info("::" + linkId + "::,deleteB02ScoreSourceLogs,定时B02比分通道切换记录 时间: {},条数：{}", dayDateTime,matchScoresNums);
        } catch (Exception e) {
            //log.info("::" + linkId + "::,deleteB02ScoreSourceLogs,定时B02比分通道切换记录执行异常，Exception:", e);
        }
        stopWatch.stop();
        //log.info("::" + linkId + "::,deleteB02ScoreSourceLogs,定时B02比分通道切换记录，用时{}毫秒", stopWatch.getTotalTimeMillis());
    }

    private void deleteMatchScoresInfo(Long dayDateTime, Integer matchNum) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String linkId = StringUtils.join(dayDateTime, "_clean_match_score_info_");
        //log.info("::" + linkId + "::,deleteMatchScoresInfo,定时清理三方比分信息开始，dayDateTime：{}，matchNum：{}", dayDateTime, matchNum);
        try {
            MatchScoresInfoExample matchScoresInfoExample=new MatchScoresInfoExample();
            matchScoresInfoExample.createCriteria().andCreateTimeLessThanOrEqualTo(dayDateTime);
            matchScoresInfoExample.setOrderByClause("create_time asc");
            PageHelper.startPage(ONE, matchNum);
            List<MatchScoresInfo> dataList = matchScoresInfoMapper.selectByExample(matchScoresInfoExample);
            if (!CollectionUtils.isEmpty(dataList)) {
                int size = dataList.size();
                //log.info("::" + linkId + "::,deleteMatchScoresInfo,定时清理三方比分信息开始历史数据条数：{}", size);
                List<MatchScoresInfo> matchScoresList = size > matchNum ? dataList.subList(0, matchNum) : dataList;
                List<Long> ids = matchScoresList.stream().map(MatchScoresInfo::getId).collect(Collectors.toList());
                //log.info("::" + linkId + "::,deleteMatchScoresInfo,定时清理比分三方赛事ID：{}", ids);
                MatchScoresInfoExample deleteMatchScoresInfoExample=new MatchScoresInfoExample();
                deleteMatchScoresInfoExample.createCriteria().andIdIn(ids);
                int matchScoresNums = matchScoresInfoMapper.deleteByExample(deleteMatchScoresInfoExample);
                //log.info("::" + linkId + "::,deleteMatchScoresInfo,定时清理matchScoresInfo id: {},条数：{}", ids,matchScoresNums);

            }
        } catch (Exception e) {
            //log.info("::" + linkId + "::,deleteMatchScoresInfo,定时清理三方比分信息执行异常，Exception:", e);
        }
        stopWatch.stop();
        //log.info("::" + linkId + "::,deleteMatchScoresInfo,定时清理三方比分表，用时{}毫秒", stopWatch.getTotalTimeMillis());
    }

    private void deleteMatchScoresEventInfo(Long dayDateTime, Integer matchNum) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String linkId = StringUtils.join(dayDateTime, "_clean_match_score_event_info_");
        //log.info("::" + linkId + "::,deleteMatchScoresEventInfo,定时清理结算历史信息开始，dayDateTime：{}，matchNum：{}", dayDateTime, matchNum);
        int matchScoreEventNums = 0;
        try {
            MatchScoresEventInfoExample matchScoresEventInfoExample= new MatchScoresEventInfoExample();
            matchScoresEventInfoExample.createCriteria().andCreateTimeLessThanOrEqualTo(dayDateTime);
            matchScoresEventInfoExample.setOrderByClause("create_time asc");
            PageHelper.startPage(ONE, matchNum);
            List<MatchScoresEventInfo> dataList = matchScoresEventInfoMapper.selectByExample(matchScoresEventInfoExample);
            if (!CollectionUtils.isEmpty(dataList)) {
                int size = dataList.size();
                //log.info("::" + linkId + "::,deleteMatchScoresEventInfo,定时清理赛事数据源类型历史数据条数：{}", size);
                List<MatchScoresEventInfo> matchSourceList = size > matchNum ? dataList.subList(0, matchNum) : dataList;
                List<Long> ids = matchSourceList.stream().map(MatchScoresEventInfo::getId).collect(Collectors.toList());
                if(!ids.isEmpty()){
                    MatchScoresEventInfoExample deleteMatchScoresEventInfoExample= new MatchScoresEventInfoExample();
                    deleteMatchScoresEventInfoExample.createCriteria().andIdIn(ids);
                    matchScoreEventNums = matchScoresEventInfoMapper.deleteByExample(deleteMatchScoresEventInfoExample);
                    //log.info("::" + linkId + "::,deleteMatchScoresEventInfo,定时清理MatchScoresEventInfo三方赛事源id: ,条数：{}",matchScoreEventNums);
                }
            }
        } catch (Exception e) {
            //log.info("::" + linkId + "::,deleteMatchScoresEventInfo,删除比分事件表数据执行异常，Exception:", e);
        }
        stopWatch.stop();
        //log.info("::" + linkId + "::,deleteMatchScoresEventInfo,定时清理三方赛事数据源类型表，用时{}毫秒,删除{}条数据", stopWatch.getTotalTimeMillis(), matchScoreEventNums);
    }

    private void deleteStandMatchScore(Long dayDateTime) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String linkId = StringUtils.join(dayDateTime, "_clean_match_score_by_stand_");
        //log.info("::" + linkId + "::,deleteStandMatchScore,定时清理综合球种标准比分历史数据开始，dayDateTime：{}", dayDateTime);
        try {
            //标准比分
            StandardMatchScoresExample standardMatchScoresExample = new StandardMatchScoresExample();
            standardMatchScoresExample.createCriteria().andCreateTimeLessThanOrEqualTo(dayDateTime);
            int scoreNums = standardMatchScoresMapper.deleteByExample(standardMatchScoresExample);
            //log.info("::" + linkId + "::,deleteStandMatchScore,定时清理standardMatchScores标准赛事id: {},条数：{}", scoreNums,scoreNums);

            //标准比分日志
            MatchScoresCenterLogExample matchScoresCenterLogExample = new MatchScoresCenterLogExample();
            matchScoresCenterLogExample.createCriteria().andCreateTimeLessThanOrEqualTo(dayDateTime);
            int scoreLogNums = matchScoresCenterLogMapper.deleteByExample(matchScoresCenterLogExample);
            //log.info("::" + linkId + "::,deleteStandMatchScore,定时清理MatchScoresCenterLog 标准赛事id: {},条数：{}", scoreLogNums,scoreLogNums);

        } catch (Exception e) {
            //log.info("::" + linkId + "::,deleteStandMatchScore,定时根据标准赛事ID清理结算历史数据执行异常，Exception:", e);
        }
        stopWatch.stop();
        //log.info("::" + linkId + "::,deleteStandMatchScore,定时清理standardMatchScores综合球种标准比分{}毫秒", stopWatch.getTotalTimeMillis());
    }

    private void deleteMatchScoresPdLogs(Long dayDateTime,Integer matchNum) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String linkId = StringUtils.join(dayDateTime, "_clean_match_score_pd_log_");
        //log.info("::" + linkId + "::,deleteMatchScoresPdLogs,定时清理报球板日志开始，dayDateTime：{}，matchNum:{}", dayDateTime,matchNum);
        try {
            MatchScoresPdLogExample matchScoresPdLogExample= new MatchScoresPdLogExample();
            matchScoresPdLogExample.createCriteria().andCreateTimeLessThanOrEqualTo(dayDateTime);
            matchScoresPdLogExample.setOrderByClause("create_time asc");
            PageHelper.startPage(ONE, matchNum);
            List<MatchScoresPdLog> dataList = matchScoresPdLogMapper.selectByExample(matchScoresPdLogExample);
            if (!CollectionUtils.isEmpty(dataList)) {
                int size = dataList.size();
                //log.info("::" + linkId + "::,deleteMatchScoresPdLogs,定时清理报球板日志数据条数：{}", size);
                List<MatchScoresPdLog> logList = size > matchNum ? dataList.subList(0, matchNum) : dataList;
                List<String> matchManageIds = logList.stream().map(MatchScoresPdLog::getMatchManageId).collect(Collectors.toList());
                if(!matchManageIds.isEmpty()){
                    try {
                        MatchScoresPdLogExample deleteMatchScoresPdLogExample= new MatchScoresPdLogExample();
                        deleteMatchScoresPdLogExample.createCriteria().andMatchManageIdIn(matchManageIds);
                        int matchScorePdLogsNums = matchScoresPdLogMapper.deleteByExample(deleteMatchScoresPdLogExample);
                        //log.info("::" + linkId + "::,deleteMatchScoresPdLogs,定时清理报球板日志 标准赛事ID: {},条数：{}", matchManageIds,matchScorePdLogsNums);
                    } catch (Exception e) {
                        log.error("::" + linkId + "::deleteMatchScoresPdLogs,定时清理报球板日志执行异常 ,Exception:", e);
                    }
                }
            }
        } catch (Exception e) {
            //log.info("::" + linkId + "::,deleteMatchScoresPdLogs,定时清理报球板日志执行异常，Exception:", e);
        }
        stopWatch.stop();
        //log.info("::" + linkId + "::,deleteMatchScoresPdLogs,定时清理报球板日志执行用时{}毫秒", stopWatch.getTotalTimeMillis());
    }
    private void deleteMatchSettleResult(Long dayDateTime) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String linkId = StringUtils.join(dayDateTime, "_clean_match_settle_result_");
        //log.info("::" + linkId + "::,deleteMatchSettleResult,定时清理结算赛果历史数据开始，dayDateTime：{}", dayDateTime);
        try {
            //结算赛果
            MatchSettleResultExample matchSettleResultExample = new MatchSettleResultExample();
            matchSettleResultExample.createCriteria().andCreateTimeLessThanOrEqualTo(dayDateTime);
            int settleResultNum = matchSettleResultMapper.deleteByExample(matchSettleResultExample);
            //log.info("::" + linkId + "::,deleteMatchSettleResult,定时清理standardMatchScores 条数：{}", settleResultNum);

        } catch (Exception e) {
            //log.info("::" + linkId + "::,deleteMatchSettleResult,定时清理结算赛果历史数据执行异常，Exception:", e);
        }
        stopWatch.stop();
        //log.info("::" + linkId + "::,deleteMatchSettleResult,定时清理结算赛果历史数据 执行用时{}毫秒", stopWatch.getTotalTimeMillis());
    }

    /**
     * 通过三方赛事信息删除 、match_time_info、match_scores_source_type
     * @param dayDateTime
     * @param matchNum
     */
    private void deleteScoreInfoDataByThirdMatchInfo(Long dayDateTime,Integer matchNum) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String linkId = StringUtils.join(dayDateTime, "_clean_match_score_by_third");
        //log.info("::" + linkId + "::,deleteScoreInfoDataByThirdMatchInfo,定时清理结算历史信息开始，dayDateTime：{}，matchNum：{}", dayDateTime, matchNum);
        try {
            MatchTimeInfoExample matchTimeInfoExample=new MatchTimeInfoExample();
            matchTimeInfoExample.createCriteria().andCreateTimeLessThanOrEqualTo(dayDateTime);
            matchTimeInfoExample.setOrderByClause("create_time asc");
            PageHelper.startPage(ONE, matchNum);
            List<MatchTimeInfo> timeList = matchTimeInfoMapper.selectByExample(matchTimeInfoExample);
            if (!CollectionUtils.isEmpty(timeList)) {
                int size = timeList.size();
                //log.info("::" + linkId + "::,deleteScoreInfoDataByThirdMatchInfo,定时清理赛事时间表信息表历史数据条数：{}", size);
                List<MatchTimeInfo> matchTimeList = size > matchNum ? timeList.subList(0, matchNum) : timeList;
                if(!matchTimeList.isEmpty()){
                    List<Long> thirdMatchIds = matchTimeList.stream().map(MatchTimeInfo::getThirdMatchId).collect(Collectors.toList());
                    if(!thirdMatchIds.isEmpty()){
                        MatchTimeInfoExample deleteMatchTimeInfoExample=new MatchTimeInfoExample();
                        deleteMatchTimeInfoExample.createCriteria().andThirdMatchIdIn(thirdMatchIds);
                        int matchTimeNums = matchTimeInfoMapper.deleteByExample(deleteMatchTimeInfoExample);
                        //log.info("::" + linkId + "::,deleteScoreInfoDataByThirdMatchInfo,定时清理matchTimeInfo三方赛事id: {},条数：{}", thirdMatchIds,matchTimeNums);

                        MatchScoresSourceTypeExample matchSourceTypeExample= new MatchScoresSourceTypeExample();
                        matchSourceTypeExample.createCriteria().andThirdMatchIdIn(thirdMatchIds);
                        int sourceTypeNum = matchScoresSourceTypeMapper.deleteByExample(matchSourceTypeExample);
                        //log.info("::" + linkId + "::,deleteScoreInfoDataByThirdMatchInfo,定时清理MatchScoresSourceType三方赛事id: {},条数：{}", thirdMatchIds,sourceTypeNum);
                    }
                }
            }
        } catch (Exception e) {
            //log.info("::" + linkId + "::,deleteScoreInfoDataByThirdMatchInfo,定时根据根据三方赛事ID清理比分历史数据执行异常，Exception:", e);
        }
        stopWatch.stop();
        //log.info("::" + linkId + "::,deleteScoreInfoDataByThirdMatchInfo,定时根据三方赛事ID清理历史数据执行用时{}毫秒", stopWatch.getTotalTimeMillis());
    }

}
