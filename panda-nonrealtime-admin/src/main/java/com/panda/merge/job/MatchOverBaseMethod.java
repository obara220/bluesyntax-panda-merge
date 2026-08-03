package com.panda.merge.job;

import cn.hutool.core.lang.UUID;
import com.panda.merge.bo.StandardMatchOverBO;
import com.panda.merge.bo.ThirdMatchOverBO;
import com.panda.merge.common.enums.DataSourceCommerceEnum;
import com.panda.merge.common.enums.MatchTypeEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.common.utils.TimeUtils;
import com.panda.merge.constant.ConstantSystem;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.mapper.ThirdMatchInfoMapper;
import com.panda.merge.model.DataSource;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.ThirdMatchInfo;
import com.panda.merge.rocketmq.producer.MatchOverProducer;
import com.panda.merge.rocketmq.producer.MatchSaleOverJobProducer;
import com.panda.merge.service.DataSourceService;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 完赛处理公共方法
 * @author   tell
 * @since    2020年9月12日17:06:53
 * */
@Slf4j
@Component
public class MatchOverBaseMethod {

    @Autowired
    private StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    private ThirdMatchInfoMapper thirdMatchInfoMapper;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private MatchSaleOverJobProducer matchSaleOverJobProducer;
    @Resource
    private MatchOverProducer matchOverProducer;


    /**
     * 三方赛事列表进行完赛处理(全部数据源都可以)
     * @author tell
     * @since  2020年9月13日19:38:01
     **/
    public void thirdMatchInfoListProcessOver(List<ThirdMatchInfo> thirdMatchInfoList) {
        if(!CollectionUtils.isEmpty(thirdMatchInfoList)){
            thirdMatchInfoList.forEach(thirdMatchInfo -> {
                thirdMatchInfoProcessOver(thirdMatchInfo);
            });
        }
    }

    /**
     * 单条三方赛事进行完赛处理(全部数据源都可以)
     * @author tell
     * @since  2020年9月13日19:38:01
     * @param oldThirdMatchInfo   库中三方赛事信息
     **/
    public void thirdMatchInfoProcessOver(ThirdMatchInfo oldThirdMatchInfo) {
        if(null != oldThirdMatchInfo){
            Long nowTime = TimeUtils.millsSecondsEast8ZoneGmt();
            //开赛时间晚于当前时间的跳出
            if (oldThirdMatchInfo.getBeginTime() > nowTime) {
                return;
            }
            //已经是完赛状态的跳出
            if (YesNoEnum.Y.value.equals(oldThirdMatchInfo.getMatchOver())) {
                return;
            }
            ThirdMatchInfo thirdMatchInfo = new ThirdMatchInfo();
            thirdMatchInfo.setId(oldThirdMatchInfo.getId());
            thirdMatchInfo.setMatchOver(YesNoEnum.Y.value);
            thirdMatchInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            thirdMatchInfoMapper.updateByPrimaryKeySelective(thirdMatchInfo);
            //log.info("【thirdMatchInfoProcessOver 三方赛事完赛处理】 三方赛事ID={}",oldThirdMatchInfo.getId());
            XxlJobLogger.log("【thirdMatchInfoProcessOver 三方赛事完赛处理】 三方赛事ID={}",oldThirdMatchInfo.getId());
            DataSource dataSource = dataSourceService.getItemByCode(oldThirdMatchInfo.getDataSourceCode());
            if(null != dataSource){
                //是商业数据源
                if(DataSourceCommerceEnum.COMMERCE.getCode().equals(dataSource.getCode())){
                    //对标准赛事进行完赛处理
                    if(null != oldThirdMatchInfo.getReferenceId() && !Long.valueOf(ConstantSystem.ZERO).equals(oldThirdMatchInfo.getReferenceId())){
                        standardMatchInfoProcessOver(standardMatchInfoMapper.selectByPrimaryKey(oldThirdMatchInfo.getReferenceId()));
                    }
                }
            }
            //3803【比分网】比分网后台
            if (StandardSportTypeEnum.FootBall.getCode().equals(oldThirdMatchInfo.getSportId()) && MatchTypeEnum.NORMAL.getCode().equals(oldThirdMatchInfo.getMatchType())) {
                List<ThirdMatchOverBO> thirdMatchOverBOS = new ArrayList<>();
                ThirdMatchOverBO thirdMatchOverBO = new ThirdMatchOverBO();
                thirdMatchOverBO.setThirdMatchId(oldThirdMatchInfo.getId());
                thirdMatchOverBO.setMatchOver(YesNoEnum.Y.value);
                thirdMatchOverBOS.add(thirdMatchOverBO);
                matchOverProducer.sendThirdMatchOverPls(UUID.fastUUID().toString().replace("-", ""),thirdMatchOverBOS, System.currentTimeMillis());
            }
        }
    }

    /**
     * 标准赛事列表进行完赛处理(商业数据源才有标准赛事)
     * @author tell
     * @since  2020年9月13日19:38:01
     **/
    public void standardMatchInfoListProcessOver(List<StandardMatchInfo> standardMatchInfoList) {
        if(!CollectionUtils.isEmpty(standardMatchInfoList)){
            standardMatchInfoList.forEach(standardMatchInfo -> {
                standardMatchInfoProcessOver(standardMatchInfo);
            });
        }
    }

    /**
     * 单条标准赛事进行完赛处理(商业数据源才有标准赛事)
     * @author tell
     * @since  2020年9月13日19:38:01
     * @param oldStandardMatchInfo   库中标准赛事信息
     **/
    public void standardMatchInfoProcessOver(StandardMatchInfo oldStandardMatchInfo) {
        if(null != oldStandardMatchInfo){
            Long nowTime = TimeUtils.millsSecondsEast8ZoneGmt();
            //完赛状态已经是完赛就跳出
            if (YesNoEnum.Y.value.equals(oldStandardMatchInfo.getMatchOver())) {
                return;
            }
            //开赛时间晚于当前时间就跳出
            if (oldStandardMatchInfo.getBeginTime() > nowTime) {
                return;
            }
            StandardMatchInfo standardMatchInfo = new StandardMatchInfo();
            standardMatchInfo.setId(oldStandardMatchInfo.getId());
            standardMatchInfo.setMatchOver(YesNoEnum.Y.value);
            standardMatchInfo.setModifyTime(TimeUtils.millsSecondsEast8ZoneGmt());
            standardMatchInfoMapper.updateByPrimaryKeySelective(standardMatchInfo);
            //log.info("【standardMatchInfoProcessOver 标准赛事完赛处理】 标准赛事ID={}",oldStandardMatchInfo.getId());
            XxlJobLogger.log("【standardMatchInfoProcessOver 标准赛事完赛处理】 标准赛事ID={}",oldStandardMatchInfo.getId());
            //完赛通知预开售
            matchSaleOverJobProducer.sendMatchSaleOverMessage(standardMatchInfo.getId()+"_MinsJob",oldStandardMatchInfo);
            //3803【比分网】比分网后台
            if (oldStandardMatchInfo.getPlsStandardMatchId()!=null && oldStandardMatchInfo.getPlsStandardMatchId()!=0) {
                List<StandardMatchOverBO> standardMatchOverBOS = new ArrayList<>();
                StandardMatchOverBO standardMatchOverBO = new StandardMatchOverBO();
                standardMatchOverBO.setStandardMatchId(oldStandardMatchInfo.getId());
                standardMatchOverBO.setMatchOver(YesNoEnum.Y.value);
                standardMatchOverBO.setPlsStandardMatchId(oldStandardMatchInfo.getPlsStandardMatchId());
                standardMatchOverBOS.add(standardMatchOverBO);
                matchOverProducer.sendStandardMatchOverPls(UUID.fastUUID().toString().replace("-", ""),standardMatchOverBOS, System.currentTimeMillis());
            }
        }
    }

}
