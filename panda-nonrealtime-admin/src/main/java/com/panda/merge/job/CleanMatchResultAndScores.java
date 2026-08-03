package com.panda.merge.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.github.pagehelper.PageHelper;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.ONE;


@Slf4j
@Component
@JobHandler(value = "CleanMatchResultAndScores")
public class CleanMatchResultAndScores extends IJobHandler {

    @Autowired
    StandardMatchResultMapper standardMatchResultMapper;
    @Autowired
    ThirdMatchResultMapper thirdMatchResultMapper;
    @Autowired
    StandardMatchResultAmendMapper standardMatchResultAmendMapper;
    @Autowired
    StandardMatchResultAlterMapper standardMatchResultAlterMapper;

    @Autowired
    MatchScoresStandardRelationMapper matchScoresStandardRelationMapper;

    @Autowired
    MatchResultReportEventMapper matchResultReportEventMapper;
    @Autowired
    MatchResultLogMapper matchResultLogMapper;
    @Autowired
    MatchEventCommonMapper matchEventCommonMapper;
    /**
     * 循环删除时间审核赛果日志可控上限次数
     */
    @NacosValue(value = "${clean-match.match-result-log.totalDeleted:10}", autoRefreshed = true)
    private Integer totalDeletedMatchResultLogCount;


    static Integer DELETE_DATE_7 = -7;

    @Override
    public ReturnT<String> execute(String param) {
        //log.info("CleanMatchResultAndScores,清理事件审核相关数据开始,入参：{}", param);
        XxlJobLogger.log("CleanMatchResultAndScores,清理事件审核相关数据开始,入参：{}", param);
        //计算删除的时间节点
        Long dateTime = null;
        try {
            Integer dayNum = DELETE_DATE_7;
            Integer pageSize = 5000;
            if (StringUtils.isNotBlank(param)) {
                JSONObject jsonObj = JSON.parseObject(param);
                if(null != jsonObj.getInteger("dayNum")){
                    dayNum = jsonObj.getInteger("dayNum");
                }
                if(null != jsonObj.getInteger("pageSize")){
                    pageSize = jsonObj.getInteger("pageSize");
                }
            }
            dateTime = getStatetime(dayNum);
            //删除事件审核-三方，标准赛果数据
            deleteResultInfo(dateTime, pageSize);
        } catch (Exception e) {
            //log.info("CleanMatchResultAndScores,清理事件审核相关数据异常,Exception：{}", e);
            XxlJobLogger.log("CleanMatchResultAndScores,清理事件审核相关数据异常,Exception：{}", e.getMessage());
        } finally {
            //log.info("CleanMatchResultAndScores,清理事件审核相关数据结束,dateTime={}", dateTime);
            XxlJobLogger.log("CleanMatchResultAndScores,清理事件审核相关数据结束,dateTime={}", dateTime);
        }
        return ReturnT.SUCCESS;
    }


    /**
     * 删除事件审核-原始赛果-三方赛果-标准赛果
     */
    private void deleteResultInfo(Long dateTime, Integer pageSize) {
        try {
            //清除标准赛果
            StandardMatchResultExample example1 = new StandardMatchResultExample();
            example1.createCriteria().andCreateTimeLessThan(dateTime);
            PageHelper.startPage(ONE, pageSize);
            List<StandardMatchResult> resList1 = standardMatchResultMapper.selectByExample(example1);
            int num1 = 0;
            if (!CollectionUtils.isEmpty(resList1)) {
                List<Long> ids = resList1.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                StandardMatchResultExample delExample1 = new StandardMatchResultExample();
                delExample1.createCriteria().andIdIn(ids);
                num1 = standardMatchResultMapper.deleteByExample(delExample1);
            }
            //log.info("CleanMatchResultAndScores,标准赛果清理完成,dateTime={},num={}", dateTime, num1);
            XxlJobLogger.log("CleanMatchResultAndScores,标准赛果清理完成,dateTime={},num={}", dateTime, num1);
        } catch (Exception e) {
            //log.info("CleanMatchResultAndScores,标准赛果清理异常,Exception：{}", e);
        }


        try {
            StandardMatchResultAlterExample example2 = new StandardMatchResultAlterExample();
            example2.createCriteria().andCreateTimeLessThan(dateTime);
            PageHelper.startPage(ONE, pageSize);
            List<StandardMatchResultAlter> resList2 = standardMatchResultAlterMapper.selectByExample(example2);
            int num2 = 0;
            if (!CollectionUtils.isEmpty(resList2)) {
                List<Long> ids = resList2.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                StandardMatchResultAlterExample delExample2 = new StandardMatchResultAlterExample();
                delExample2.createCriteria().andIdIn(ids);
                num2 = standardMatchResultAlterMapper.deleteByExample(delExample2);
            }
            //log.info("CleanMatchResultAndScores,标准赛果信息删除修正缓存清理完成,dateTime={},num={}", dateTime, num2);
            XxlJobLogger.log("CleanMatchResultAndScores,标准赛果信息删除修正缓存清理完成,dateTime={},num={}", dateTime, num2);
        } catch (Exception e) {
            //log.info("CleanMatchResultAndScores,标准赛果修正缓存清理异常,Exception：{}", e);
        }

        try {
            StandardMatchResultAmendExample example3 = new StandardMatchResultAmendExample();
            example3.createCriteria().andCreateTimeLessThan(dateTime);
            PageHelper.startPage(ONE, pageSize);
            List<StandardMatchResultAmend> resList3 = standardMatchResultAmendMapper.selectByExample(example3);
            int num3 = 0;
            if (!CollectionUtils.isEmpty(resList3)) {
                List<Long> ids = resList3.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                StandardMatchResultAmendExample delExample3 = new StandardMatchResultAmendExample();
                delExample3.createCriteria().andIdIn(ids);
                num3 = standardMatchResultAmendMapper.deleteByExample(delExample3);
            }
            //log.info("CleanMatchResultAndScores,标准赛果修正备份清理完成,dateTime={},num={}", dateTime, num3);
            XxlJobLogger.log("CleanMatchResultAndScores,标准赛果修正备份清理完成,dateTime={},num={}", dateTime, num3);
        } catch (Exception e) {
            //log.info("CleanMatchResultAndScores,标准赛果修正备份清理异常,Exception：{}", e);
        }

        try {
            MatchResultReportEventExample example4 = new MatchResultReportEventExample();
            example4.createCriteria().andCreateTimeLessThan(dateTime);
            PageHelper.startPage(ONE, pageSize);
            List<MatchResultReportEvent> resList4 = matchResultReportEventMapper.selectByExample(example4);
            int num4 = 0;
            if (!CollectionUtils.isEmpty(resList4)) {
                List<Long> ids = resList4.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                MatchResultReportEventExample delExample4 = new MatchResultReportEventExample();
                delExample4.createCriteria().andIdIn(ids);
                num4 = matchResultReportEventMapper.deleteByExample(delExample4);
            }
            //log.info("CleanMatchResultAndScores,事件审核统计基础事件清理完成,dateTime={},num={}", dateTime, num4);
            XxlJobLogger.log("CleanMatchResultAndScores,事件审核统计基础事件清理完成,dateTime={},num={}", dateTime, num4);
        } catch (Exception e) {
            //log.info("CleanMatchResultAndScores,事件审核统计基础事件清理异常,Exception：{}", e);
        }

        int count = 0;
        int totalDeleted = 0;

        while (true) {
            int rows =  clearMatchResultLog(dateTime, pageSize);

            totalDeleted += rows;
            count++;
            log.info("CleanMatchResultAndScores,事件审核赛果日志清理,dateTime={},第{}批,删除{},累计{}", dateTime, count, rows, totalDeleted);

            if (rows == 0 || rows < pageSize) {
                log.info("CleanMatchResultAndScores,事件审核赛果日志清理,dateTime={},删除完成", dateTime);
                break;
            }
            if (count >= totalDeletedMatchResultLogCount) {
                log.info("CleanMatchResultAndScores,事件审核赛果日志清理,dateTime={},达到最大批次数，停止（防止风险）", dateTime);
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        XxlJobLogger.log("CleanMatchResultAndScores,事件审核赛果日志清理完成,dateTime={},num={}", dateTime, totalDeleted);


        try {
            ThirdMatchResultExample example6 = new ThirdMatchResultExample();
            example6.createCriteria().andCreateTimeLessThan(dateTime);
            PageHelper.startPage(ONE, pageSize);
            List<ThirdMatchResult> resList6 = thirdMatchResultMapper.selectByExample(example6);
            int num6 = 0;
            if (!CollectionUtils.isEmpty(resList6)) {
                List<Long> ids = resList6.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                ThirdMatchResultExample delExample6 = new ThirdMatchResultExample();
                delExample6.createCriteria().andIdIn(ids);
                num6 = thirdMatchResultMapper.deleteByExample(delExample6);
            }
            //log.info("CleanMatchResultAndScores,事件审核三方赛果清理完成,dateTime={},num={}", dateTime, num6);
            XxlJobLogger.log("CleanMatchResultAndScores,事件审核三方赛果清理完成,dateTime={},num={}", dateTime, num6);
        } catch (Exception e) {
            //log.info("CleanMatchResultAndScores,事件审核三方赛果清理异常,Exception：{}", e);
        }

        try {
            MatchEventCommonExample example7 = new MatchEventCommonExample();
            example7.createCriteria().andCreateTimeLessThan(dateTime);
            PageHelper.startPage(ONE, pageSize);
            List<MatchEventCommon> resList7 = matchEventCommonMapper.selectByExample(example7);
            int num7 = 0;
            if (!CollectionUtils.isEmpty(resList7)) {
                List<Long> ids = resList7.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                MatchEventCommonExample delExample7 = new MatchEventCommonExample();
                delExample7.createCriteria().andIdIn(ids);
                num7 = matchEventCommonMapper.deleteByExample(delExample7);
            }
            //log.info("CleanMatchResultAndScores,事件审核原始赛果清理完成,dateTime={},num={}", dateTime, num7);
            XxlJobLogger.log("CleanMatchResultAndScores,事件审核原始赛果清理完成,dateTime={},num={}", dateTime, num7);
        } catch (Exception e) {
            //log.info("CleanMatchResultAndScores,事件审核原始赛果清理异常,Exception：{}", e);
        }
    }

    private int clearMatchResultLog(Long dateTime, Integer pageSize) {
        try {
            MatchResultLogExample example5 = new MatchResultLogExample();
            example5.createCriteria().andCreateTimeLessThan(dateTime);
            PageHelper.startPage(ONE, pageSize);
            List<MatchResultLog> resList5 = matchResultLogMapper.selectByExample(example5);
            int num5 = 0;
            if (!CollectionUtils.isEmpty(resList5)) {
                List<Long> ids = resList5.stream().map(obj -> obj.getId()).collect(Collectors.toList());
                MatchResultLogExample delExample5 = new MatchResultLogExample();
                delExample5.createCriteria().andIdIn(ids);
                return matchResultLogMapper.deleteByExample(delExample5);
            }
            //log.info("CleanMatchResultAndScores,事件审核赛果日志清理完成,dateTime={},num={}", dateTime, num5);
//            XxlJobLogger.log("CleanMatchResultAndScores,事件审核赛果日志清理完成,dateTime={},num={}", dateTime, num5);
        } catch (Exception e) {
            //log.info("CleanMatchResultAndScores,事件审核赛果日志清理异常,Exception：{}", e);
        }
        return 0;
    }

    /**
     * 获取多少天之前或者之后的时间戳
     *
     * @param dayNum 天数（负数表示之前日期，正数表示未来日期）
     */
    public static Long getStatetime(Integer dayNum) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayNum);
        Date date = calendar.getTime();
        return date.getTime();

    }

    public static void main(String[] xx) throws Exception {
        System.out.println(getStatetime(DELETE_DATE_7));
    }
}