package com.panda.merge.common.enums;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 赛事阶段中常用到的停止，终止，结束状态枚举 用于判断 赛事的完赛
 *
 * @author :  Franz
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.common.enums
 * @Description :  赛事阶段中常用到的停止，终止，结束状态枚举
 * @Date: 2019-11-11 20:07
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public enum MatchPeriodForMatchOverEnum {

    MATCH_1H(6L, "上半场"),
    MATCH_2H(7L, "下半场"),
    Interrupted(80L, "比赛中断"),
    Abandoned(90L, "放弃播报"),
    WALKOVER1(93L, "客队球员未参赛"),
    WALKOVER2(94L, "主队球员未参赛"),
    RETIRED1(95L, "主队球员退赛"),
    RETIRED2(96L, "客队球员退赛"),
    DEFAULTED1(97L, "主队球员未到场"),
    DEFAULTED2(98L, "客队球员未到场"),
    //比赛没有加时赛的全场结束 100 状态
    Ended(100L, "全场结束"),
    Ended999(999L, "比赛结束"),

    Wait_OverTime_STA(32L, "等待加时开始"),
    OverTime(40L, "加时赛"),
    OverTime_1H(41L, "加时赛上半场"),
    OverTime_HT(33L, "加时赛中场"),
    OverTime_2H(42L, "加时赛下半场"),

    //有加时赛的全场结束不发送100 而发送110
    After_OverTime(110L, "加时赛打完结束，全场结束"),
    Wait_PENALTY_SHOOTOUT_STA(34L, "等待点球大战"),
    /** 足球点球大战*/
    PENALTY_SHOOTOUT(50L, "点球大战"),
    //足球中点球结束，不发送100，发120表示全场结束
    AFTER_PENALTIES(120L, "点球结束，全场结束"),
    NOT_STARTED(0L, "未开赛"),
    ;
    public Long value;
    public String desc;

    MatchPeriodForMatchOverEnum(Long value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private static final Map<Long, MatchPeriodForMatchOverEnum> ENUM_MAP = new HashMap<>();
    static {
        for (MatchPeriodForMatchOverEnum matchStatusEnum : MatchPeriodForMatchOverEnum.values()) {
            ENUM_MAP.put(matchStatusEnum.value, matchStatusEnum);
        }
    }

    /**
     * 根据状态值获取枚举对象
     *
     * @param statusValue 状态值
     * @return MatchStatusEnum 具体状态枚举对象
     */
    public static MatchPeriodForMatchOverEnum getEnum(Long statusValue) {
        return ENUM_MAP.get(statusValue);
    }

    /**
     * 获取需要延迟下发的 赛事阶段和时长的关系
     * */
    public static Map<Long,Long> getSleepMatchPeriodId2Time(){
        Map<Long,Long> periodId2Time = new LinkedHashMap<>();
        //100常规赛事完赛
        periodId2Time.put(MatchPeriodForMatchOverEnum.Ended.value,1000L);
        //32等待加时
        periodId2Time.put(MatchPeriodForMatchOverEnum.Wait_OverTime_STA.value,1100L);
        //40加时赛（篮球）
        periodId2Time.put(MatchPeriodForMatchOverEnum.OverTime.value,1100L);
        //110加时赛打完结束
        periodId2Time.put(MatchPeriodForMatchOverEnum.After_OverTime.value,1200L);
        //34等待点球大战
        periodId2Time.put(MatchPeriodForMatchOverEnum.Wait_PENALTY_SHOOTOUT_STA.value,1250L);
        //50点球大战
        periodId2Time.put(MatchPeriodForMatchOverEnum.PENALTY_SHOOTOUT.value,1300L);
        //120点球结束
        periodId2Time.put(MatchPeriodForMatchOverEnum.AFTER_PENALTIES.value,1400L);
        //999全场结束
        periodId2Time.put(MatchPeriodForMatchOverEnum.Ended999.value,3000L);
        return periodId2Time;
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
