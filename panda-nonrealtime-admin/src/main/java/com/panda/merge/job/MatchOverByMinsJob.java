package com.panda.merge.job;

import com.google.common.collect.Lists;
import com.panda.merge.common.BaseProcessor;
import com.panda.merge.common.enums.DataSourceCommerceEnum;
import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.common.enums.PreSaleMatchStatusEnum;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.common.utils.CommUtils;
import com.panda.merge.config.JobExecuteTimeConfig;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.StandardSportMarketSellMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.mapper.ThirdSportMarketMapper;
import com.panda.merge.model.*;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.panda.merge.constant.ConstantSystem.*;

/**
 * 完赛操作调度，按分钟完赛 （每分钟执行一次 30 0/1 * * * ?）
 * 1.操盘手操作关盘后，之后10分钟，移入历史赛程
 * 2.全部关盘且整条赛程移除后，正常全场结束，且15分钟未收到新盘口数据，移入“历史赛程”
 * @author     tell
 * @since     2020年10月4日13:54:33
 */
@Slf4j
@Component
@JobHandler(value = "MatchOverByMinsJob")
public class MatchOverByMinsJob extends IJobHandler {

    @Autowired
    public JobExecuteTimeConfig jobExecuteTimeConfig;
    @Autowired
    public ThirdMatchInfoMapper thirdMatchInfoMapper;
    @Autowired
    public StandardMatchInfoMapper standardMatchInfoMapper;
    @Autowired
    private ThirdSportMarketMapper thirdSportMarketMapper;
    @Autowired
    private StandardSportMarketSellMapper standardSportMarketSellMapper;
    @Autowired
    public MatchOverBaseMethod matchOverBaseMethod;
    @Autowired
    private BaseProcessor baseProcessor;

    /**
     * 调度作业 进行完赛操作
     * 1.完成对操盘手关盘的赛事进行完赛处理 根据产品要求暂时删除操盘手关盘进行完赛的操作
     * 2.针对爬虫无法爬取到滚球赛事的完赛处理
     * 3.针对爬虫三方赛事滚球完赛处理
     * 4.查询调度状态没有变化的间隔5分钟的数据，进行自动调度进行完赛处理，弥补调度被中断，完赛状态为中间临时态2的问题
     * 5.查询所有完赛的赛事数据，判断是否存在状态的更新以及不是完赛状态的数据并移除完赛状态
     * @param   param
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String param){
        log.info("【MatchOverByMinsJob 完赛操作调度，每分钟执行】 完赛处理结束");
        //2.针对爬虫无法爬取到滚球赛事的完赛处理（非商业数据源不能生成标准赛事）
//        try {
//            this.matchOverScrapyMatch();
//        } catch (Exception e) {
//            log.error("调度作业 进行完赛操作-标准赛事完赛", e.getMessage());
//        }
        //3.非商业数据源三方赛事滚球完赛处理
        try {
            this.matchOverScrapyThirdMatch();
        } catch (Exception e) {
            log.error("【matchOverScrapyThirdMatch 完赛操作调度，每分钟执行异常】 非商业数据源三方赛事完赛处理异常！Exception:", e);
            XxlJobLogger.log("【matchOverScrapyThirdMatch 完赛操作调度，每分钟执行异常】 非商业数据源三方赛事完赛处理异常！Exception:"+e.getMessage());
        }
        //4.查询调度状态没有变化的间隔5分钟的数据，进行自动调度进行完赛处理，"弥补调度被中断"，完赛状态为中间临时态2的问题
        try {
            this.fixMatchOverOtherStatus();
        } catch (Exception e) {
            log.error("【fixMatchOverOtherStatus 完赛操作调度，每分钟执行异常】 调度状态没有变化的间隔5分钟的数据完赛处理异常！Exception:", e);
            XxlJobLogger.log("【fixMatchOverOtherStatus 完赛操作调度，每分钟执行异常】 调度状态没有变化的间隔5分钟的数据完赛处理异常！Exception:"+e.getMessage());
        }
        //5.查询所有完赛的标准赛事数据，判断是否存在状态的更新以及不是完赛状态的数据并移除完赛状态
        try {
            this.fixStandardInfoMatchOverRecords();
        } catch (Exception e) {
            log.error("【fixStandardInfoMatchOverRecords 完赛操作调度，每分钟执行异常】 标准完赛处理异常！Exception:", e);
            XxlJobLogger.log("【fixStandardInfoMatchOverRecords 完赛操作调度，每分钟执行异常】 标准完赛处理异常！Exception:"+e.getMessage());
        }
        //6.查询所有完赛的三方赛事数据，判断是否存在状态的更新以及不是完赛状态的数据并移除完赛状态
        try {
            this.fixThirdInfoMatchOverRecords();
        } catch (Exception e) {
            log.error("【fixStandardInfoMatchOverRecords 完赛操作调度，每分钟执行异常】 三方赛事完赛处理异常！Exception:", e);
            XxlJobLogger.log("【fixStandardInfoMatchOverRecords 完赛操作调度，每分钟执行异常】 三方赛事完赛处理异常！Exception:"+e.getMessage());
        }
        try {
            //检查 为999 的状态进行完赛处理
            this.checkToMatchOver();
        } catch (Exception e) {
            log.error("【fixStandardInfoMatchOverRecords 完赛操作调度，每分钟执行异常】 赛事状态未999的赛事完赛处理异常！Exception:", e);
            XxlJobLogger.log("【fixStandardInfoMatchOverRecords 完赛操作调度，每分钟执行异常】 赛事状态未999的赛事完赛处理异常！Exception:"+e.getMessage());
        }
        log.info("【MatchOverByMinsJob 完赛操作调度，每分钟执行】 完赛处理结束");
        XxlJobLogger.log("【MatchOverByMinsJob 完赛操作调度，每分钟执行】 完赛处理结束");
        return ReturnT.SUCCESS;
    }

    /**
     * 检查 为999 的状态进行完赛处理
     */
    private void checkToMatchOver() {
        //1.查询标准赛事表
        StandardMatchInfoExample standardMatchInfoExample = new StandardMatchInfoExample();
        standardMatchInfoExample.createCriteria()
                .andMatchPeriodIdEqualTo(Long.valueOf(MatchStatusEnum.Ended999.value))
                .andMatchOverEqualTo(YesNoEnum.N.value);
        List<StandardMatchInfo> standardMatchInfoList = standardMatchInfoMapper.selectByExample(standardMatchInfoExample);
        log.info("checkToMatchOver :标准赛事需要完赛的集合条数{}", standardMatchInfoList.size());
        matchOverBaseMethod.standardMatchInfoListProcessOver(standardMatchInfoList);

        //3.查询三方赛事表
        ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
        thirdMatchInfoExample.createCriteria()
                .andMatchPeriodEqualTo(String.valueOf(MatchStatusEnum.Ended999.value))
                .andMatchOverEqualTo(YesNoEnum.N.value);
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
        log.info("checkToMatchOver :三方赛事需要完赛的集合条数{}", thirdMatchInfoList.size());
        matchOverBaseMethod.thirdMatchInfoListProcessOver(thirdMatchInfoList);
    }


    /**
     * 查询调度状态没有变化的间隔5分钟的数据，进行自动调度进行完赛处理，弥补调度被中断，完赛状态为中间临时态2的问题
     * 1.查询标准赛事表
     * 2.发送mq
     * 3.查询三方赛事表
     * 4.发送mq
     */
    private void fixMatchOverOtherStatus() {
        Long nowTime = Calendar.getInstance().getTimeInMillis();
        Long beginTime = nowTime - HOUR_1;
        //1.查询标准赛事表
        StandardMatchInfoExample standardMatchInfoExample = new StandardMatchInfoExample();
        standardMatchInfoExample.createCriteria().andMatchOverEqualTo(YesNoEnum.Other.value)
                .andBeginTimeLessThanOrEqualTo(nowTime+MINS_1)
                .andBeginTimeGreaterThanOrEqualTo(beginTime);
        List<StandardMatchInfo> standardMatchInfoList = standardMatchInfoMapper.selectByExample(standardMatchInfoExample);
        log.info("fixMatchOverOtherStatus :标准赛事需要完赛的集合条数{}", standardMatchInfoList.size());
        matchOverBaseMethod.standardMatchInfoListProcessOver(standardMatchInfoList);

        //3.查询三方赛事表
        ThirdMatchInfoExample thirdMatchInfoExample = new ThirdMatchInfoExample();
        thirdMatchInfoExample.createCriteria().andMatchOverEqualTo(YesNoEnum.Other.value)
                .andBeginTimeLessThanOrEqualTo(nowTime+MINS_1)
                .andBeginTimeGreaterThanOrEqualTo(beginTime);
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoMapper.selectByExample(thirdMatchInfoExample);
        log.info("fixMatchOverOtherStatus :三方赛事需要完赛的集合条数{}", thirdMatchInfoList.size());
        matchOverBaseMethod.thirdMatchInfoListProcessOver(thirdMatchInfoList);
    }

    /**
     * 针对爬虫第三方赛事的滚球进行完赛处理
     * 1.查询商业数据源
     * 2.查询滚球的三方赛事
     * 3.分页处理三方赛事集合
     */
    private void matchOverScrapyThirdMatch() {
        //1.查询非商业数据源
        List<String> commerceList = baseProcessor.getDataSourceCodes(DataSourceCommerceEnum.NON_COMMERCE.getCode());
        if (commerceList.isEmpty()) {
            return;
        }
        //2.查询滚球的三方赛事
        ThirdMatchInfoExample example = new ThirdMatchInfoExample();
        example.createCriteria().andMatchStatusEqualTo(MatchStatusEnum.Live.value)
                .andMatchOverEqualTo(YesNoEnum.N.value)
                .andBeginTimeLessThanOrEqualTo( Calendar.getInstance().getTimeInMillis())
                .andBeginTimeGreaterThanOrEqualTo(Calendar.getInstance().getTimeInMillis() - MINS_1 * FIVES)
                .andDataSourceCodeIn(commerceList);
        List<ThirdMatchInfo> thirdMatchInfoList = thirdMatchInfoMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(thirdMatchInfoList)){
            return;
        }
        Long intervalTime = MINS_1 * jobExecuteTimeConfig.getFinishScrapyMatchMins();
        Long nowTime = Calendar.getInstance().getTimeInMillis();
        //3.分割处理三方赛事集合
        List<List<ThirdMatchInfo>> lists = CommUtils.groupList(thirdMatchInfoList,HUNDRED);
        for (List<ThirdMatchInfo> list: lists) {
            list.forEach(thirdMatchInfo -> {
                //开赛时间晚于当前时间的跳出
                if (thirdMatchInfo.getBeginTime() > nowTime) {
                    return;
                }
                //已经是完赛状态的跳出
                if (YesNoEnum.Y.value.equals(thirdMatchInfo.getMatchOver())) {
                    return;
                }
                //是临时调度态的进行补发mq跳出
                if (YesNoEnum.Other.value.equals(thirdMatchInfo.getMatchOver())) {
                    matchOverBaseMethod.thirdMatchInfoProcessOver(thirdMatchInfo);
                    return;
                }
                ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
                thirdSportMarketExample.setOrderByClause("modify_time desc");
                thirdSportMarketExample.createCriteria().andMatchIdEqualTo(thirdMatchInfo.getId()).andDataSourceCodeNotIn(commerceList);
                List<ThirdSportMarket> thirdSportMarkets = thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
                if (CollectionUtils.isEmpty(thirdSportMarkets)) {
                    return;
                }
                ThirdSportMarket thirdSportMarketLoad = thirdSportMarkets.get(0);
                //2.根据滚球赛事查询赔率表是否有变化如果间隔15分钟没有变化进行完赛处理
                if (nowTime - thirdSportMarketLoad.getModifyTime() > intervalTime) {
                    matchOverBaseMethod.thirdMatchInfoProcessOver(thirdMatchInfo);
                }
            });
        }
    }

    /**
    /**
     * 针对爬虫无法爬取到滚球赛事的完赛处理 （有问题，非商业数据源不能生成标准赛事）
     * 1.根据滚球赛事情况进行查询，计算出结束时间并将大于15分钟的进行取出
     */
    private void matchOverScrapyMatch() {
        /** 根据滚球赛事情况进行查询，计算出结束时间并将大于15分钟的进行取出*/
        //1.查询商业数据源后面用于排除
        List<String> commerceList = baseProcessor.getDataSourceCodes(DataSourceCommerceEnum.COMMERCE.getCode());
        if (commerceList.isEmpty()) {
            return;
        }
        //2.查询滚球的三方赛事
        StandardMatchInfoExample example = new StandardMatchInfoExample();
        example.createCriteria()
                .andMatchStatusEqualTo(MatchStatusEnum.Live.value)
                .andMatchOverEqualTo(YesNoEnum.N.value)
                .andBeginTimeLessThanOrEqualTo( Calendar.getInstance().getTimeInMillis())
                .andBeginTimeGreaterThanOrEqualTo(Calendar.getInstance().getTimeInMillis() - MINS_1 * FIVES)
                .andDataSourceCodeNotIn(commerceList);
        List<StandardMatchInfo> standardMatchInfoList = standardMatchInfoMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(standardMatchInfoList)){
            return;
        }
        Long intervalTime = MINS_1 * jobExecuteTimeConfig.getFinishScrapyMatchMins();
        Long nowTime = Calendar.getInstance().getTimeInMillis();
        //3.分割处理三方赛事集合
        List<List<StandardMatchInfo>> lists = CommUtils.groupList(standardMatchInfoList,HUNDRED);
        for (List<StandardMatchInfo> list: lists) {
            standardMatchInfoList.forEach(standardMatchInfo -> {
                //开赛时间晚于当前时间的跳出
                if (standardMatchInfo.getBeginTime() > nowTime) {
                    return;
                }
                //已经是完赛状态的跳出
                if (YesNoEnum.Y.value.equals(standardMatchInfo.getMatchOver())) {
                    return;
                }
                //已经是调度状态的补发mq跳出
                if (YesNoEnum.Other.value.equals(standardMatchInfo.getMatchOver())) {
                    matchOverBaseMethod.standardMatchInfoProcessOver(standardMatchInfo);
                    return;
                }
                ThirdSportMarketExample thirdSportMarketExample = new ThirdSportMarketExample();
                thirdSportMarketExample.setOrderByClause("modify_time desc");
                thirdSportMarketExample.createCriteria()
                        .andMatchIdEqualTo(standardMatchInfo.getThirdMatchId())
                        .andDataSourceCodeNotIn(commerceList);
                List<ThirdSportMarket> thirdSportMarkets = thirdSportMarketMapper.selectByExample(thirdSportMarketExample);
                if (CollectionUtils.isEmpty(thirdSportMarkets)) {
                    return;
                }
                ThirdSportMarket thirdSportMarketLoad = thirdSportMarkets.get(0);
                //2.根据滚球赛事查询赔率表是否有变化如果间隔15分钟没有变化进行完赛处理
                if (nowTime - thirdSportMarketLoad.getModifyTime() > intervalTime) {
                    matchOverBaseMethod.standardMatchInfoProcessOver(standardMatchInfo);
                }
            });
        }
    }



    /**
     * 查询所有完赛的赛事数据，判断是否存在状态的更新以及不是完赛状态的数据并移除完赛状态
     * 1.查询所有赛事状态为完赛状态， 完赛的数据
     * 2.集合处理
     */
    private void fixStandardInfoMatchOverRecords() {
        Long nowTime = Calendar.getInstance().getTimeInMillis();
        List<Integer> matchStatusList = Lists.newArrayList(MatchStatusEnum.Not_Started.value, MatchStatusEnum.Suspended.value,
                MatchStatusEnum.Delayed.value, MatchStatusEnum.Live.value, MatchStatusEnum.Postponed.value,
                MatchStatusEnum.Unknown.value, MatchStatusEnum.UnUsable.value);
        StandardMatchInfoExample standardMatchInfoExample = new StandardMatchInfoExample();
        standardMatchInfoExample.createCriteria().andMatchOverEqualTo(YesNoEnum.Y.value)
                .andBeginTimeLessThanOrEqualTo(nowTime+MINS_1)
                .andBeginTimeGreaterThanOrEqualTo(nowTime)
                .andMatchStatusIn(matchStatusList);
        List<StandardMatchInfo> standardMatchInfos = standardMatchInfoMapper.selectByExample(standardMatchInfoExample);
        if (CollectionUtils.isEmpty(standardMatchInfos)) {
            return;
        }
        Long fourHours = HOUR_1 * jobExecuteTimeConfig.getMaxMatchTimeOverHour();
        //3.分割处理标准赛事
        List<List<StandardMatchInfo>> lists = CommUtils.groupList(standardMatchInfos,HUNDRED);
        for (List<StandardMatchInfo> list: lists) {
            list.forEach(standardMatchInfo -> {
                if (nowTime - standardMatchInfo.getBeginTime() > fourHours) {
                    return;
                }
               matchOverBaseMethod.standardMatchInfoProcessOver(standardMatchInfo);
            });
            Set<Long> standardMatchIds = list.stream().map(StandardMatchInfo::getId).collect(Collectors.toSet());
            log.info("standardMatchIds:{}", standardMatchIds);
            StandardSportMarketSellExample tandardSportMarketSellExample = new StandardSportMarketSellExample();
            tandardSportMarketSellExample.createCriteria().andMatchInfoIdIn(Lists.newArrayList(standardMatchIds));
            List<StandardSportMarketSell> standardSportMarketSells = standardSportMarketSellMapper.selectByExample(tandardSportMarketSellExample);
            if (!CollectionUtils.isEmpty(standardSportMarketSells)) {
                for (StandardSportMarketSell standardSportMarketSell : standardSportMarketSells) {
                    standardSportMarketSell.setStatus(PreSaleMatchStatusEnum.Enable.name());
                    int updateNum = standardSportMarketSellMapper.updateByPrimaryKeySelective(standardSportMarketSell);
                    log.info("end=enable:{},更新条数:{}", standardSportMarketSell.getMatchInfoId(), updateNum);
                }
            }
        }
    }

    /**
     * 查询所有完赛的赛事数据，判断是否存在状态的更新以及不是完赛状态的数据并移除完赛状态
     * 1.查询所有赛事状态为未完赛状态， 完赛的数据
     * 2.集合处理
     */
    private void fixThirdInfoMatchOverRecords() {
        Long nowTime = Calendar.getInstance().getTimeInMillis();
        List<Integer> matchStatusList = Lists.newArrayList(MatchStatusEnum.Not_Started.value, MatchStatusEnum.Suspended.value,
                MatchStatusEnum.Delayed.value, MatchStatusEnum.Live.value, MatchStatusEnum.Postponed.value, MatchStatusEnum.Unknown.value);
        ThirdMatchInfoExample example = new ThirdMatchInfoExample();
        example.createCriteria().andMatchOverEqualTo(YesNoEnum.Y.value)
                .andBeginTimeLessThanOrEqualTo(nowTime + MINS_1)
                .andBeginTimeGreaterThanOrEqualTo(nowTime)
                .andMatchStatusIn(matchStatusList);
        List<ThirdMatchInfo> thirdMatchInfos = thirdMatchInfoMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(thirdMatchInfos)) {
            return;
        }
        Long fourHours = HOUR_1 * jobExecuteTimeConfig.getMaxMatchTimeOverHour();
        thirdMatchInfos.forEach(thirdMatchInfo -> {
            if (nowTime - thirdMatchInfo.getBeginTime() > fourHours) {
                return;
            }
            matchOverBaseMethod.thirdMatchInfoProcessOver(thirdMatchInfo);
        });
    }


}
