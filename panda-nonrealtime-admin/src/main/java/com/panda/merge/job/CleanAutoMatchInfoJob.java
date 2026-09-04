package com.panda.merge.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.panda.merge.common.utils.CommUtils;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardMatchTeamRelationMapper;
import com.panda.merge.model.StandardMatchInfoExample;
import com.panda.merge.model.StandardMatchTeamRelation;
import com.panda.merge.model.StandardMatchTeamRelationExample;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Component
@JobHandler(value = "cleanAutoMatchInfo")
public class CleanAutoMatchInfoJob extends IJobHandler {

    @Autowired
    private StandardMatchTeamRelationMapper standardMatchTeamRelationMapper;
    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Override
    public ReturnT<String> execute(String param) {
        Integer dayNum = 2;
        //默认一次清理赛事条数1000
        Integer matchNum = 1000;
        String methodName = "deleteAutoMatchInfo";
        String standardTeamId = null;
        try {
            log.info("【" + methodName + " 清理自动化赛事相关数据】 开始,入参：{}", param);
            XxlJobLogger.log("【" + methodName + "清理自动化赛事相关数据】 开始,入参：{}", param);
            if (StringUtils.isNotBlank(param)) {
                JSONObject jsonObj = JSON.parseObject(param);
                if(null != jsonObj.getString("standardTeamId")){
                    standardTeamId = jsonObj.getString("standardTeamId");
                }
                if(StringUtils.isBlank(standardTeamId)){
                    log.info("【" + methodName + " 清理自动化赛事相关数据】, 标准球队ID不能为空");
                    XxlJobLogger.log("【" + methodName + " 清理自动化赛事相关数据, 标准球队ID不能为空");
                    return ReturnT.SUCCESS;
                }
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
                log.info("【" + methodName + " 清理自动化赛事相关数据】 数据处理开始,入参：{}, dayDateTime:{}, standardTeamId:{}", jsonObj, dayDateTime, standardTeamId);
                XxlJobLogger.log("【" + methodName + " 清理自动化赛事相关数据】,入参：{}, dayDateTime:{}, standardTeamId:{}", jsonObj, dayDateTime, standardTeamId);
                deleteAutoStandardMatch(dayDateTime, matchNum, standardTeamId);
            }
        } catch (Exception e) {
            log.error("【" + methodName + " 清理自动化赛事相关数据】 异常,Exception:", e);
            XxlJobLogger.log("【" + methodName + " 清理自动化赛事相关数据】 异常,Exception:" + e.getMessage());
        }
        log.info("【" + methodName + " 清理自动化赛事相关数据】 结束");
        XxlJobLogger.log("【" + methodName + " 清理自动化赛事相关数据】 结束");
        return ReturnT.SUCCESS;

    }

    /**
     *  定时清除自动化赛事相关信息(标准赛事、赛事与球队关联信息)+
     *
     * @param dayDateTime       数据保留天数
     * @param matchNum          删除数量
     * @param standardTeamId    标准球队ID
     */
    private void deleteAutoStandardMatch(Long dayDateTime, Integer matchNum, String standardTeamId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String linkId = StringUtils.join(dayDateTime, "_delete_auto_match");
        List<Long> standardTeamIds = Arrays.stream(standardTeamId.split(",")).map(Long::parseLong).collect(Collectors.toList());
        StandardMatchTeamRelationExample standardMatchTeamRelationExample = new StandardMatchTeamRelationExample();
        standardMatchTeamRelationExample.createCriteria().andStandardTeamIdIn(standardTeamIds).andCreateTimeLessThan(dayDateTime);
        List<StandardMatchTeamRelation> list = standardMatchTeamRelationMapper.selectByExample(standardMatchTeamRelationExample);
        Set<Long> relationIds = new HashSet<>();
        Set<Long> standardMatchIds = new HashSet<>();
        if(CollectionUtils.isNotEmpty(list)){
            for(StandardMatchTeamRelation standardMatchTeamRelation : list){
                relationIds.add(standardMatchTeamRelation.getId());
                standardMatchIds.add(standardMatchTeamRelation.getStandardMatchId());
            }
            List<Long> relationIdList = Arrays.asList(relationIds.toArray(new Long[0]));
            List<Long> standardMatchIdList = Arrays.asList(standardMatchIds.toArray(new Long[0]));
            if(relationIds.size() < matchNum){
                StandardMatchTeamRelationExample standardMatchTeamRelation = new StandardMatchTeamRelationExample();
                standardMatchTeamRelation.createCriteria().andIdIn(relationIdList);
                standardMatchTeamRelationMapper.deleteByExample(standardMatchTeamRelation);
            } else {
                List<List<Long>> lists = CommUtils.groupList(relationIdList, matchNum);
                for(List<Long> relationId : lists){
                    StandardMatchTeamRelationExample standardMatchTeamRelation = new StandardMatchTeamRelationExample();
                    standardMatchTeamRelation.createCriteria().andIdIn(relationId);
                    standardMatchTeamRelationMapper.deleteByExample(standardMatchTeamRelation);
                }
            }
            if(standardMatchIds.size() < matchNum){
                StandardMatchInfoExample standardMatchInfoExample = new StandardMatchInfoExample();
                standardMatchInfoExample.createCriteria().andIdIn(standardMatchIdList);
                standardMatchInfoMapper.deleteByExample(standardMatchInfoExample);
            } else {
                List<List<Long>> lists = CommUtils.groupList(standardMatchIdList, matchNum);
                for(List<Long> standardMatchId : lists){
                    StandardMatchInfoExample standardMatchInfoExample = new StandardMatchInfoExample();
                    standardMatchInfoExample.createCriteria().andIdIn(standardMatchId);
                    standardMatchInfoMapper.deleteByExample(standardMatchInfoExample);
                }
            }
        }
        log.info("::" + linkId + "::,deleteAutoStandardMatch,定时清除自动化赛事,共删除关联表数据{}条", relationIds.size());
        log.info("::" + linkId + "::,deleteAutoStandardMatch,定时清除自动化赛事,共删除标准赛事数据{}条", standardMatchIds.size());
        stopWatch.stop();
        log.info("::" + linkId + "::,deleteAutoStandardMatch,定时清除自动化赛事相关信息{}毫秒", stopWatch.getTotalTimeMillis());
        XxlJobLogger.log("::" + linkId + "::,deleteAutoStandardMatch,定时清除自动化赛事相关信息{}毫秒", stopWatch.getTotalTimeMillis());
    }
}