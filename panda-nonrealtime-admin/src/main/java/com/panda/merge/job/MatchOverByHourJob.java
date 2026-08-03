package com.panda.merge.job;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.panda.merge.bo.StandardMatchOverBO;
import com.panda.merge.bo.ThirdMatchOverBO;
import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.common.enums.MatchTypeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.common.utils.CommUtils;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.config.JobExecuteTimeConfig;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardMatchInfoExample;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.model.ThirdMatchInfoExample;
import com.panda.merge.rocketmq.producer.MatchOverProducer;
import com.panda.merge.rocketmq.producer.MatchSaleOverJobProducer;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.HOUR_1;

/**
 * 完赛操作调度，按小时完赛 （每5分钟执行一次 0 0/5 * * * ?）
 *  需求地址： http://lan-confluence.dbsports.online/pages/viewpage.action?pageId=24122341
 *  足、篮、乒乓球，手球，拳击保持4小时完赛兜底
 *  羽毛球 兜底时间为12小时，
 *  网球、沙滩排球、排球、美足、冰球兜底时间为1天，
 *  棒球兜底为2天、
 *  斯诺克，板球兜底为7天
 * @author     tell
 * @since     2020年10月4日13:54:33
 */
@Slf4j
@Component
@JobHandler(value = "MatchOverByHourJob")
public class MatchOverByHourJob extends IJobHandler {

    @Autowired
    public JobExecuteTimeConfig jobExecuteTimeConfig;
    @Autowired
    public ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    public StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    public MatchSaleOverJobProducer matchSaleOverJobProducer;
    @Resource
    private MatchOverProducer matchOverProducer;

    //赛种为足球，未完赛，且赛事出现过取消或中断状态的赛事兜底时间(天),默认为30
    @NacosValue(value = "${football.match.over.days}", autoRefreshed = true)
    private Integer footBallMatchOverDays;

    //92233 【产品】【生产】足球完赛兜底机制优化
    @NacosValue(value = "${match.over.third.tournament:10011003316}", autoRefreshed = true)
    private String matchOverThirdTournamentIds;

    @NacosValue(value = "${match.over.standard.tournament:10011003316}", autoRefreshed = true)
    private String matchOverStandardTournaments;


    /**
     * 调度作业 进行完赛操作
     * 1.根据类型查询赛事的滚球赛事
     * 2.三方赛事完赛处理
     */
    @Override
    public ReturnT<String> execute(String param){
        try {
            //key：小时数,vla:运动类型列表 例：{"4":"1,2,8","12":"10","24":"4,5,6,9","48":"3","168":"7"}
            Map<String, String> parMap = JSON.parseObject(param, Map.class);
            //log.info("【MatchOverByHourJob 完赛操作调度，兜底完赛】 完赛处理开始,入参：{}",parMap);
            XxlJobLogger.log("【MatchOverByHourJob 完赛操作调度，兜底完赛】 完赛处理开始,入参：{}",parMap);
            for (String hourNum: parMap.keySet()) {
                if(StringUtils.isNotBlank(hourNum)){
                    //hourNum小时兜底完赛的运动种类
                    List<Long> sportIds = new HashSet<>(Splitter.on(",").splitToList(parMap.get(hourNum))).stream().map(sportId -> Long.valueOf(sportId)).collect(Collectors.toList());
                    //log.info("【MatchOverByHourJob 完赛操作调度，{}小时兜底完赛】 需要完赛的运动类型：{}",hourNum,JSON.toJSONString(sportIds));
                    XxlJobLogger.log("【MatchOverByHourJob 完赛操作调度，{}小时兜底完赛】 需要完赛的运动类型：{}",hourNum,JSON.toJSONString(sportIds));
                    //三方赛事完赛处理
                    this.thirdMatchInfoOver(hourNum,sportIds);
                    //标准赛事完赛处理
                    this.standardMatchMatchOver(hourNum,sportIds);
                }
            }
            //【49152赛事开赛时间超过4小时限制优化】赛种为足球，且赛事出现过中断或是取消状态，自动完赛兜底时间为365天
            footBallOtherMatchOver();
        } catch (Exception e) {
            log.error("【MatchOverByHourJob 完赛操作调度，兜底完赛异常】 Exception:", e);
            XxlJobLogger.log("【MatchOverByHourJob 完赛操作调度，兜底完赛异常】 Exception:"+e.getMessage());
        }
        //log.info("【MatchOverByHourJob 完赛操作调度，兜底完赛】 完赛处理结束");
        XxlJobLogger.log("【MatchOverByHourJob 完赛操作调度，兜底完赛】 完赛处理结束");
        return ReturnT.SUCCESS;
    }

    /**
     * 手动完赛三方赛事
     * @param hourNum  小时数
     * @param sportIds 需要完赛的运动类型
     */
    public void thirdMatchInfoOver(String hourNum,List<Long> sportIds) {
        //需要查询N小时前的赛事开赛时间
        Long time = TimeUtils.millsSecondsEast8ZoneGmt() - HOUR_1 * Integer.valueOf(hourNum);
        //需要过滤掉的赛事状态
        List<Integer> matchStatusList = Lists.newArrayList(MatchStatusEnum.Delayed.value, MatchStatusEnum.Postponed.value, MatchStatusEnum.Suspended.value, MatchStatusEnum.Interrupted.value);
        //92233 【产品】【生产】足球完赛兜底机制优化，需要过滤的三方联赛
        List<Long> tournamentIds = Pattern.compile(",").splitAsStream(matchOverThirdTournamentIds).map(Long::valueOf).collect(Collectors.toList());
        //查询条件
        ThirdMatchInfoExample example = new ThirdMatchInfoExample();
        ThirdMatchInfoExample.Criteria criteria = example.createCriteria();
        criteria.andMatchOverNotEqualTo(YesNoEnum.Y.value)
                .andMatchStatusNotIn(matchStatusList)
                //小于等于N小时前的赛事
                .andBeginTimeLessThanOrEqualTo(time)
                //状态没有出现过比赛中断、取消状态的三方赛事
                .andInterruptionCancellationStatusEqualTo(YesNoEnum.N.value)
                //需要过滤的三方联赛
                .andTournamentIdNotIn(tournamentIds)
                .andSportIdIn(sportIds);
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoMapper.selectByExample(example);
        //log.info("【MatchOverByHourJob 完赛操作调度，{}小时兜底完赛】三方赛事表需要完赛的集合条数{}，时间{}",hourNum, thirdMatchInfoList.size(),time);
        XxlJobLogger.log("【MatchOverByHourJob 完赛操作调度，{}小时兜底完赛】三方赛事表需要完赛的集合条数{}，时间{}",hourNum, thirdMatchInfoList.size(),time);
        if (!CollectionUtils.isEmpty(thirdMatchInfoList)) {
//            List<Long> thirdMatchIds = thirdMatchInfoList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
//            //log.info("【MatchOverByHourJob 完赛操作调度，{}小时兜底完赛】完赛的三方赛事ID列表：{}",hourNum, thirdMatchIds);
//            XxlJobLogger.log("【MatchOverByHourJob 完赛操作调度，{}小时兜底完赛】完赛的三方赛事ID列表：{}",hourNum, thirdMatchIds);
            ThirdMatchInfo enity = new ThirdMatchInfo();
            enity.setMatchOver(YesNoEnum.Y.value);
            enity.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            thirdMatchInfoMapper.updateByExampleSelective(enity,example);
        }
        //3803【比分网】比分网后台
        if (CollectionUtils.isEmpty(thirdMatchInfoList)) {
            return;
        }
        List<ThirdMatchOverBO> thirdMatchOverBOS = new ArrayList<>();
        for (ThirdMatchInfo thirdMatchInfo : thirdMatchInfoList) {
            if (StandardSportTypeEnum.FootBall.getCode().equals(thirdMatchInfo.getSportId()) && MatchTypeEnum.NORMAL.getCode().equals(thirdMatchInfo.getMatchType())) {
                ThirdMatchOverBO thirdMatchOverBO = new ThirdMatchOverBO();
                thirdMatchOverBO.setThirdMatchId(thirdMatchInfo.getId());
                thirdMatchOverBO.setMatchOver(YesNoEnum.Y.value);
                thirdMatchOverBOS.add(thirdMatchOverBO);
            }
        }
        if(!CollectionUtils.isEmpty(thirdMatchOverBOS)){
            List<List<ThirdMatchOverBO>> thirdMatchOverBOGroups = CommUtils.groupList(thirdMatchOverBOS, 200);
            for (List<ThirdMatchOverBO> thirdMatchOverBOList : thirdMatchOverBOGroups) {
                matchOverProducer.sendThirdMatchOverPls(UUID.fastUUID().toString().replace("-", ""),thirdMatchOverBOList, System.currentTimeMillis());
            }
        }
    }


    /**
     * 手动完赛标准赛事，通知预开售
     * @param hourNum  小时数
     * @param sportIds 需要完赛的运动类型
     */
    public void standardMatchMatchOver(String hourNum,List<Long> sportIds) {
        //需要查询N小时前的赛事开赛时间
        Long time = TimeUtils.millsSecondsEast8ZoneGmt() - HOUR_1 * Integer.valueOf(hourNum);
        //需要过滤掉的赛事状态
        List<Integer> matchStatusList = Lists.newArrayList(MatchStatusEnum.Delayed.value, MatchStatusEnum.Postponed.value, MatchStatusEnum.Suspended.value, MatchStatusEnum.Interrupted.value);
        //92233 【产品】【生产】足球完赛兜底机制优化，需要过滤的标准联赛
        List<Long> tournamentIds = Pattern.compile(",").splitAsStream(matchOverStandardTournaments).map(Long::valueOf).collect(Collectors.toList());
        //组装条件
        StandardMatchInfoExample example = new StandardMatchInfoExample();
        StandardMatchInfoExample.Criteria criteria = example.createCriteria();
        criteria.andMatchOverNotEqualTo(YesNoEnum.Y.value)
                .andMatchStatusNotIn(matchStatusList)
                //小于等于N小时前的赛事
                .andBeginTimeLessThanOrEqualTo(time)
                //状态没有出现过比赛中断、取消状态的三方赛事
                .andInterruptionCancellationStatusEqualTo(YesNoEnum.N.value)
                //需要过滤的标准联赛
                .andStandardTournamentIdNotIn(tournamentIds)
                .andSportIdIn(sportIds);
        List<StandardMatchInfo> standardMatchInfoList = standardMatchInfoMapper.selectByExample(example);
        //log.info("【MatchOverByHourJob 完赛操作调度，{}小时兜底完赛】标准赛事表需要完赛的集合条数{}，时间{}",hourNum, standardMatchInfoList.size(),time);
        XxlJobLogger.log("【MatchOverByHourJob 完赛操作调度，{}小时兜底完赛】标准赛事表需要完赛的集合条数{}，时间{}",hourNum, standardMatchInfoList.size(),time);
        if (!CollectionUtils.isEmpty(standardMatchInfoList)) {
//            List<Long> standardIds = standardMatchInfoList.stream().map(obj -> obj.getId()).collect(Collectors.toList());
//            //log.info("【MatchOverByHourJob 完赛操作调度，{}小时兜底完赛】完赛的标准赛事ID列表：{}",hourNum, standardIds);
//            XxlJobLogger.log("【MatchOverByHourJob 完赛操作调度，{}小时兜底完赛】完赛的标准赛事ID列表：{}",hourNum, standardIds);
            StandardMatchInfo enity = new StandardMatchInfo();
            enity.setMatchOver(YesNoEnum.Y.value);
            enity.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            standardMatchInfoMapper.updateByExampleSelective(enity,example);
            //完赛通知预开售
            for (StandardMatchInfo matchInfo: standardMatchInfoList) {
                matchSaleOverJobProducer.sendMatchSaleOverMessage(matchInfo.getId()+"_HourJob"+matchInfo.getSportId(),matchInfo);
            }
        }
        //3803【比分网】比分网后台
        if (CollectionUtils.isEmpty(standardMatchInfoList)) {
            return;
        }
        List<StandardMatchOverBO> standardMatchOverBOS = new ArrayList<>();
        for (StandardMatchInfo standardMatchInfo : standardMatchInfoList) {
            if (standardMatchInfo.getPlsStandardMatchId()==null || standardMatchInfo.getPlsStandardMatchId()==0) {
                continue;
            }
            StandardMatchOverBO standardMatchOverBO = new StandardMatchOverBO();
            standardMatchOverBO.setStandardMatchId(standardMatchInfo.getId());
            standardMatchOverBO.setMatchOver(YesNoEnum.Y.value);
            standardMatchOverBO.setPlsStandardMatchId(standardMatchInfo.getPlsStandardMatchId());
            standardMatchOverBOS.add(standardMatchOverBO);
        }
        if (!CollectionUtils.isEmpty(standardMatchOverBOS)) {
            List<List<StandardMatchOverBO>> standardMatchOverBOGroups = CommUtils.groupList(standardMatchOverBOS, 200);
            for (List<StandardMatchOverBO> standardMatchOverBOList : standardMatchOverBOGroups) {
                matchOverProducer.sendStandardMatchOverPls(UUID.fastUUID().toString().replace("-", ""),standardMatchOverBOList, System.currentTimeMillis());
            }
        }
    }

    /**
     * 赛种为足球，赛事未完赛，且赛事出现过取消或中断状态处理
     */
    public void footBallOtherMatchOver(){
        //需要查询N天前的赛事开赛时间
        Long time = TimeUtils.millsSecondsEast8ZoneGmt() - HOUR_1 * 24 * footBallMatchOverDays;
        //三方赛事查询条件
        ThirdMatchInfoExample exampleNew = new ThirdMatchInfoExample();
        exampleNew.createCriteria().andMatchOverNotEqualTo(YesNoEnum.Y.value)
                //赛事是否存在中断或取消状态
                .andInterruptionCancellationStatusEqualTo(YesNoEnum.Y.value)
                //小于等于N天前的赛事
                .andBeginTimeLessThanOrEqualTo(time)
                .andSportIdEqualTo(StandardSportTypeEnum.FootBall.code);
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoMapper.selectByExample(exampleNew);
        //log.info("【MatchOverByHourJob 足球完赛操作调度，{}天兜底完赛】三方赛事表需要完赛的集合条数{}，时间{}",footBallMatchOverDays, thirdMatchInfoList.size(),time);
        XxlJobLogger.log("【MatchOverByHourJob 足球完赛操作调度，{}天兜底完赛】三方赛事表需要完赛的集合条数{}，时间{}",footBallMatchOverDays, thirdMatchInfoList.size(),time);
        if (!CollectionUtils.isEmpty(thirdMatchInfoList)) {
//            List<Long> thirdMatchIds = thirdMatchInfoList.stream().map(ThirdMatchInfo::getId).collect(Collectors.toList());
//            //log.info("【MatchOverByHourJob  足球完赛操作调度，{}天兜底完赛】完赛的三方赛事ID列表：{}",footBallMatchOverDays, thirdMatchIds);
//            XxlJobLogger.log("【MatchOverByHourJob 足球完赛操作调度，{}天兜底完赛】完赛的三方赛事ID列表：{}",footBallMatchOverDays, thirdMatchIds);
            ThirdMatchInfo enity = new ThirdMatchInfo();
            enity.setMatchOver(YesNoEnum.Y.value);
            enity.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            thirdMatchInfoMapper.updateByExampleSelective(enity,exampleNew);
        }
        //标准赛事
        StandardMatchInfoExample standardExample = new StandardMatchInfoExample();
        standardExample.createCriteria().andMatchOverNotEqualTo(YesNoEnum.Y.value)
                //赛事是否存在中断或取消状态
                .andInterruptionCancellationStatusEqualTo(YesNoEnum.Y.value)
                //小于等于N小时前的赛事
                .andBeginTimeLessThanOrEqualTo(time)
                .andSportIdEqualTo(StandardSportTypeEnum.FootBall.code);
        List<StandardMatchInfo> standardMatchInfoList = standardMatchInfoMapper.selectByExample(standardExample);
        //log.info("【MatchOverByHourJob 足球完赛操作调度，{}天兜底完赛】标准赛事表需要完赛的集合条数{}，时间{}",footBallMatchOverDays, standardMatchInfoList.size(),time);
        XxlJobLogger.log("【MatchOverByHourJob 足球完赛操作调度，{}天兜底完赛】标准赛事表需要完赛的集合条数{}，时间{}",footBallMatchOverDays, standardMatchInfoList.size(),time);
        if (!CollectionUtils.isEmpty(standardMatchInfoList)) {
//            List<Long> standardIds = standardMatchInfoList.stream().map(StandardMatchInfo::getId).collect(Collectors.toList());
//            //log.info("【MatchOverByHourJob 足球完赛操作调度，{}天兜底完赛】完赛的标准赛事ID列表：{}",footBallMatchOverDays, standardIds);
//            XxlJobLogger.log("【MatchOverByHourJob 足球完赛操作调度，{}天兜底完赛】完赛的标准赛事ID列表：{}",footBallMatchOverDays, standardIds);
            StandardMatchInfo standardEntity = new StandardMatchInfo();
            standardEntity.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            standardEntity.setMatchOver(YesNoEnum.Y.value);
            standardMatchInfoMapper.updateByExampleSelective(standardEntity,standardExample);
            //完赛通知预开售
            for (StandardMatchInfo matchInfo: standardMatchInfoList) {
                matchSaleOverJobProducer.sendMatchSaleOverMessage(matchInfo.getId()+"_HourJob"+matchInfo.getSportId(),matchInfo);
            }
        }
    }


}
