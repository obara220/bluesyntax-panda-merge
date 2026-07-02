package com.panda.merge.common.enums;


import com.google.common.collect.Lists;

import java.util.List;


/**
 * 足蓝赛事正在进行中的阶段
 * @author   Riben
 * @since    2020-10-25 13:27
 */
public enum SportPeriodWholeEnum {
    FOOTBALL(Lists.newArrayList(6L, 7L, 41L, 42L, 50L ), 1L, "足球阶段标识,这些半场构成全场"),
    BASKETBALL(Lists.newArrayList(1L, 2L, 13L, 14L, 15L, 16L), 2L, "蓝球运动支持开滚球的赛事阶段列表");

    private List<Long> periods;

    private Long sportId;

    private String description;

    SportPeriodWholeEnum(List<Long> periods, Long sportId, String description) {
        this.periods = periods;
        this.sportId = sportId;
        this.description = description;
    }

    public List<Long> getPeriods() {
        return periods;
    }

    public Long getSportId() {
        return sportId;
    }

    public String getDescription() {
        return description;
    }

    public static SportPeriodWholeEnum getSprotPeriodBySportId(Long sportId){
        for(SportPeriodWholeEnum thisEnum : SportPeriodWholeEnum.values()){
            if(thisEnum.getSportId().equals(sportId)){
                return thisEnum;
            }
        }
        throw new RuntimeException("未找到对应运动种类赛事阶段定义！sportId : " + sportId);
    }
}
