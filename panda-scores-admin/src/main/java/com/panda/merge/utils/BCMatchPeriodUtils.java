package com.panda.merge.utils;

import com.panda.merge.constant.SportTypeEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BCMatchPeriodUtils {
    static List<Long> TENNIS_ALL_PERIODS = Arrays.asList(new Long[]{8L,301L,9L,302L,10L,303L, 11L,304L, 12L,305L});
    static List<Long> TABLE_TENNIS_ALL_PERIODS = Arrays.asList(new Long[]{8L,301L,9L,302L,10L,303L, 11L,304L, 12L,305L});
    /**
     * 获得BC需要补偿的阶段
     * 目前只支持 乒乓球和网球
     * */
    public static List<Long> getBCAllEndPeriodSByCurrentPeriod(Long sportId, Long period) {
        List<Long> allPeriods =new ArrayList<>();
        //如果是乒乓球
        if(sportId.equals(SportTypeEnum.TABLE_TENNIS.getValue())){
            allPeriods=TABLE_TENNIS_ALL_PERIODS;
        }else if(sportId.equals(SportTypeEnum.TENNIS.getValue())){
            allPeriods=TENNIS_ALL_PERIODS;
        }else {
            //都不是则先过滤
            return allPeriods;
        }
        //如果是网球
        Integer idx =  allPeriods.indexOf(period);
        if(idx>=0&&idx<allPeriods.size()){
            List<Long> periods = allPeriods.subList(0,idx);
            return periods;
        }
        //获得全部之前的阶段
        return new ArrayList<>();
    }

    public static void main(String[] xx){
        System.out.println(getBCAllEndPeriodSByCurrentPeriod(5L,303L));
    }

}
