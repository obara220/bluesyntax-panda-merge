package com.panda.merge.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class BasketBallSearchScoreCompareDto implements Comparable {

    private Long period;

    private String extryInfo;

    private static Map<Long,Integer> PERIOD_COMPARE_MAP=new HashMap<>();

    static {
        PERIOD_COMPARE_MAP.put(13L,12);
        PERIOD_COMPARE_MAP.put(301L,11);
        PERIOD_COMPARE_MAP.put(14L,10);
        PERIOD_COMPARE_MAP.put(302L,9);
        PERIOD_COMPARE_MAP.put(1L,8);

        PERIOD_COMPARE_MAP.put(15L,7);
        PERIOD_COMPARE_MAP.put(303L,6);
        PERIOD_COMPARE_MAP.put(16L,5);
        PERIOD_COMPARE_MAP.put(304L,4);
        PERIOD_COMPARE_MAP.put(2L,3);
        PERIOD_COMPARE_MAP.put(100L,2);

        PERIOD_COMPARE_MAP.put(110L,1);
        PERIOD_COMPARE_MAP.put(0L,0);
    }

    @Override
    public int compareTo(Object o) {
        Integer compare1 =PERIOD_COMPARE_MAP.get(period);
        if(compare1==null){
            return -1;
        }
        if(StringUtils.isEmpty(extryInfo)){
            return compare1*1000;
        }else {
            return compare1*1000-Integer.parseInt(extryInfo);
        }
    }
}
