package com.panda.merge.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.panda.merge.common.utils.CommUtils;
import com.panda.merge.dto.Request;
import com.panda.merge.dto.ThirdMatchInfoDetail;
import com.panda.merge.mapper.*;
import com.panda.merge.model.*;
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

import java.util.*;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;


@Slf4j
@Component
@JobHandler(value = "CleanMatchSettle")
public class CleanMatchSettle extends IJobHandler {

    @Autowired
    MatchSettleInfoMapper matchSettleInfoMapper;
    @Autowired
    MatchSettleCheckInfoMapper  matchSettleCheckInfoMapper;
    @Autowired
    MatchSettleGoalStatusMapper   matchSettleGoalStatusMapper;

    @Autowired
    MatchSettleThirdScoreMapper   matchSettleThirdScoreMapper;
    @Autowired
    MatchSettleThirdEventMapper    matchSettleThirdEventMapper;

    @Autowired
    MatchSettleEventMapper    matchSettleEventMapper;
    @Autowired
    MatchSettleScoreMapper    matchSettleScoreMapper;
    @Autowired
    MatchSettleAbnormalMapper matchSettleAbnormalMapper;
    @Autowired
    MatchDelaySettleInfoMapper matchDelaySettleInfoMapper;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Override
    public ReturnT<String> execute(String param) {
        Integer dayNum = 120;
        //默认一次清理赛事条数5
        Integer matchNum = 5;
        String methodName = "deleteSettleCheckInfoAndMatch";
        try {
            log.info("【" + methodName + " 清理赛事相关数据】 开始,入参：{}", param);
            XxlJobLogger.log("【" + methodName + "清理赛事相关数据】 开始,入参：{}", param);
            if (StringUtils.isNotBlank(param)) {
                JSONObject jsonObj = JSON.parseObject(param);
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
                log.info("【" + methodName + " 清理赛事相关数据】 业务处理开始,入参：{},dayDateTime:{}", jsonObj, dayDateTime);
                XxlJobLogger.log("【" + methodName + " 清理赛事相关数据】 业务处理开始,入参：{},dayDateTime:{}", jsonObj, dayDateTime);
                deleteSettleCheckInfoAndMatch(dayDateTime,matchNum);
            }
        } catch (Exception e) {
            log.error("【" + methodName + " 清理赛事相关数据】 异常,Exception:", e);
            XxlJobLogger.log("【" + methodName + " 清理赛事相关数据】 异常,Exception:" + e.getMessage());
        }
        log.info("【" + methodName + " 清理赛事相关数据】 结束");
        XxlJobLogger.log("【" + methodName + " 清理赛事相关数据】 结束");
        return ReturnT.SUCCESS;

    }

    private void deleteSettleCheckInfoAndMatch(Long dayDateTime,Integer matchNum) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String linkId = StringUtils.join(dayDateTime, "_clean_match_settle_");
        log.info("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时清理结算历史信息开始，dayDateTime：{}，matchNum：{}", dayDateTime, matchNum);
        try {
            Long standardMatchId = 0l;
            //先查标准赛事中 开赛时间>8天 并且已完赛
//            StandardMatchInfoExample standardMatchInfoExample = new StandardMatchInfoExample();
//            standardMatchInfoExample.createCriteria().andMatchOverEqualTo(1).andBeginTimeLessThanOrEqualTo(dayDateTime);
//            PageHelper.startPage(ONE, matchNum);
//            List<StandardMatchInfo> standardMatchInfos = standardMatchInfoMapper.selectByExample(standardMatchInfoExample);
            List<MatchSettleInfo> matchSettleInfoList = null;
            matchSettleInfoList = matchSettleInfoMapper.queryDeleteMatchSettleInfo(dayDateTime);
            if (!CollectionUtils.isEmpty(matchSettleInfoList)) {
                standardMatchId = matchSettleInfoList.get(0).getId();
            }else {
                //再查标准赛事表中没有,但是结算信息表中有的数据
                matchSettleInfoList =matchSettleInfoMapper.querySettleInfoNotInStandardMatchInfo(dayDateTime);
                if (!CollectionUtils.isEmpty(matchSettleInfoList)){
                    standardMatchId = matchSettleInfoList.get(0).getStandardMatchId();
                }

            }
            if (standardMatchId.equals(0l)){
                log.info("::" + linkId + "::,deleteSettleCheckInfoAndMatch,没有需要删除的结算赛事");
                XxlJobLogger.log("::" + linkId + "::,deleteSettleCheckInfoAndMatch,没有需要删除的结算赛事");
                return;
            }
            log.info("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时清理结算信息表历史信息条数,标准赛事id：{}", standardMatchId);
            XxlJobLogger.log("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时清理结算信息表历史信息条数,标准赛事id:"+ standardMatchId);
            try {
                MatchSettleCheckInfoExample checkInfoExample=new MatchSettleCheckInfoExample();
                checkInfoExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
                int checkNum = matchSettleCheckInfoMapper.deleteByExample(checkInfoExample);
                log.info("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时清理MatchSettleCheckInfo标准赛事id: {},条数：{}", standardMatchId,checkNum);

                MatchSettleGoalStatusExample goalStatusExample=new MatchSettleGoalStatusExample();
                goalStatusExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
                int goalNum = matchSettleGoalStatusMapper.deleteByExample(goalStatusExample);
                log.info("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时清理MatchSettleGoalStatus标准赛事id: {},条数：{}", standardMatchId,goalNum);

                MatchSettleThirdScoreExample settleThirdScoreExample=new MatchSettleThirdScoreExample();
                settleThirdScoreExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
                int thirdScoreNum = matchSettleThirdScoreMapper.deleteByExample(settleThirdScoreExample);
                log.info("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时清理MatchSettleThirdScore标准赛事id: {},条数：{}", standardMatchId,thirdScoreNum);

                MatchSettleThirdEventExample thirdEventExample=new MatchSettleThirdEventExample();
                thirdEventExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
                int thirdEventNum = matchSettleThirdEventMapper.deleteByExample(thirdEventExample);
                log.info("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时清理MatchSettleThirdEvent标准赛事id: {},条数：{}", standardMatchId,thirdEventNum);

                MatchSettleEventExample settleEventExample=new MatchSettleEventExample();
                settleEventExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
                int eventNum = matchSettleEventMapper.deleteByExample(settleEventExample);
                log.info("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时清理MatchSettleEvent标准赛事id: {},条数：{}", standardMatchId,eventNum);

                MatchSettleScoreExample settleScoreExample=new MatchSettleScoreExample();
                settleScoreExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
                int scoreNum = matchSettleScoreMapper.deleteByExample(settleScoreExample);
                log.info("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时清理MatchSettleScore标准赛事id: {},条数：{}", standardMatchId,scoreNum);

                MatchSettleAbnormalExample abnormalExample=new MatchSettleAbnormalExample();
                abnormalExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
                int abnormal =  matchSettleAbnormalMapper.deleteByExample(abnormalExample);
                log.info("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时清理MatchSettleAbnormal标准赛事id: {},条数：{}", standardMatchId,abnormal);

                MatchDelaySettleInfoExample delaySettleInfoExample = new MatchDelaySettleInfoExample();
                delaySettleInfoExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
                int delay = matchDelaySettleInfoMapper.deleteByExample(delaySettleInfoExample);
                log.info("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时清理MatchDelaySettleInfo标准赛事id: {},条数：{}", standardMatchId,delay);

                MatchSettleInfoExample InfoExample = new MatchSettleInfoExample();
                InfoExample.createCriteria().andStandardMatchIdEqualTo(standardMatchId);
                matchSettleInfoMapper.deleteByExample(InfoExample);

                log.info("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时清理MatchSettleInfo标准赛事id: {}", standardMatchId);

            } catch (Exception e) {
                log.error("::" + linkId + "::deleteSettleCheckInfoAndMatch,根据标准赛事ID删除结算相关历史数据异常 ,Exception:", e);
            }

        } catch (Exception e) {
            log.info("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时根据标准赛事ID清理结算历史数据执行异常，Exception:", e);
        }
        stopWatch.stop();
        log.info("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时根据标准赛事ID清理历史数据执行用时{}毫秒", stopWatch.getTotalTimeMillis());
        XxlJobLogger.log("::" + linkId + "::,deleteSettleCheckInfoAndMatch,定时根据标准赛事ID清理历史数据执行用时{}毫秒", stopWatch.getTotalTimeMillis());
    }
}