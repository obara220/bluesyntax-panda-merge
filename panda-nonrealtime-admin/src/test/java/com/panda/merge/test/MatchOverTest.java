package com.panda.merge.test;

import com.google.common.collect.Lists;
import com.panda.merge.common.enums.MatchStatusEnum;
import com.panda.merge.common.enums.StandardSportTypeEnum;
import com.panda.merge.common.enums.YesNoEnum;
import com.panda.merge.job.MatchOverBaseMethod;
import com.panda.merge.job.MatchOverByHourJob;
import com.panda.merge.mapper.StandardMatchInfoMapper;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardMatchInfoExample;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.List;

import static com.panda.merge.constant.ConstantSystem.HOUR_1;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MatchOverTest {

    @Autowired
    private MatchOverByHourJob matchOverByHourJob;

    @Autowired
    public StandardMatchInfoMapper standardMatchInfoMapper;

    @Autowired
    public MatchOverBaseMethod matchOverBaseMethod;

    @Test
    public void testStandardMatchMatchOver() {
        //1.查询符合条件的集合
        Long nowTime = Calendar.getInstance().getTimeInMillis();
        Long beginTime = nowTime - HOUR_1 * 4;
        //排除的运动类型
        List<Long> sportList = Lists.newArrayList(StandardSportTypeEnum.BaseBall.code,StandardSportTypeEnum.Snooker.code,StandardSportTypeEnum.Tennis.code);
        //组装条件
        StandardMatchInfoExample example = new StandardMatchInfoExample();
        example.createCriteria().andMatchOverNotEqualTo(YesNoEnum.Y.value)
                .andMatchStatusNotEqualTo(MatchStatusEnum.Delayed.value)
                .andMatchStatusNotEqualTo(MatchStatusEnum.Postponed.value)
                .andMatchStatusNotEqualTo(MatchStatusEnum.Suspended.value)
                .andMatchStatusNotEqualTo(MatchStatusEnum.Interrupted.value)
                //小于等于N小时前的赛事
                .andBeginTimeLessThanOrEqualTo(beginTime)
                .andSportIdNotIn(sportList);
        List<StandardMatchInfo> standardMatchInfoList = standardMatchInfoMapper.selectByExample(example);
        matchOverBaseMethod.standardMatchInfoListProcessOver(standardMatchInfoList.subList(0,10));
    }

    @Test
    public void testMatchOverByHourJob(){
        String param = "{\"4\":\"1,2,5,8,11,12\",\"12\":\"10\",\"24\":\"4,6,9,13\",\"48\":\"3\",\"168\":\"7\"}";
        matchOverByHourJob.execute(param);
    }

}
