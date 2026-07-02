package com.panda.merge.constant;

import org.apache.curator.shaded.com.google.common.collect.Lists;

import java.util.List;

/**
 * @author :  kb
 */
public enum SportTypeEnum {

    /**
     * 足球
     */
    FOOTBALL(1L, "足球"),

    /**
     * 篮球
     */

    BASKETBALL(2L, "篮球"),
    /**
     * 棒球
     */
    BASEBALL(3L, "棒球"),
    /**
     * 冰球
     */
    ICE_HOCKEY(4L, "冰球"),
    /**
     * 网球
     */
    TENNIS(5L, "网球"),
    /**
     * 美式足球
     */
    AMERICAN_FOOTBALL(6L, "美式足球"),
    /**
     * 斯诺克
     */
    SNOOKER(7L, "斯诺克"),
    /**
     * 乒乓球
     */
    TABLE_TENNIS(8L, "乒乓球"),
    /**
     * 排球
     */
    VOLLEYBALL(9L, "排球"),
    /**
     * 羽毛球
     */
    BADMINTON(10L, "羽毛球"),
    /**
     * 英雄联盟
     */
    LOL(100L, "英雄联盟"),
    /**
     * Dota2
     */
    Dota2(101L, "Dota2"),
    /**
     * CS
     */
    CS(102L, "CS"),
    /**
     * 王者荣耀
     */
    Honor_of_Kings(103L, "王者荣耀"),
    /**
     * 虚拟足球
     */
    VIRTUAL_FOOTBALL(1001L, "虚拟足球"),
    /**
     * 公共
     */
    COMMON(0L, "公共"),
    ;

    private String sportName;
    private Long value;

    SportTypeEnum(Long value, String name) {
        this.value = value;
        this.sportName = name;
    }

    public Long getValue() {
        return this.value;
    }

    public static List<Long> getSports() {
        List<Long> sportIds = Lists.newArrayList();
        SportTypeEnum[] values = SportTypeEnum.values();
        for (SportTypeEnum sportTypeEnum : values) {
            if (sportTypeEnum.getValue() != 0L) {
                sportIds.add(sportTypeEnum.value);
            }
        }
        return sportIds;
    }

    public String getSportName() {
        return this.sportName;
    }
}
