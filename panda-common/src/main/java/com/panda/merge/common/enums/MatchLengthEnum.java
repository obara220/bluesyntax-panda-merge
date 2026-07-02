package com.panda.merge.common.enums;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 赛制枚举
 * @author   tell
 * @since    2021年10月10日17:04:53
 */
public enum MatchLengthEnum {
    matchLengt1_0(0, 5400L, 1L,"{\"6\":\"44\",\"7\":\"89\",\"41\":\"104\",\"42\":\"119\"}","{\"6\":\"40\",\"7\":\"85\",\"31\":\"45\",\"41\":\"102\",\"42\":\"117\"}"),
    matchLengt1_1(1, 4800L, 1L,"{\"6\":\"39\",\"7\":\"79\",\"41\":\"104\",\"42\":\"119\"}","{}"),
    matchLengt1_9(9, 4200L, 1L,"{\"6\":\"34\",\"7\":\"69\",\"41\":\"104\",\"42\":\"119\"}","{}"),
    matchLengt1_10(10, 3600L, 1L,"{\"6\":\"29\",\"7\":\"59\",\"41\":\"104\",\"42\":\"119\"}","{}"),
    matchLengt1_11(11, 3000L, 1L,"{\"6\":\"24\",\"7\":\"49\",\"41\":\"104\",\"42\":\"119\"}","{}"),
    matchLengt1_46(46, 5400L, 1L,"{\"6\":\"44\",\"7\":\"89\",\"41\":\"104\",\"42\":\"119\"}","{\"6\":\"44\",\"7\":\"89\",\"31\":\"45\",\"41\":\"102\",\"42\":\"117\"}"),
    ;
    public Integer code;
    /** 全局赛事时长（秒）*/
    public Long time;
    /** 标准运动类型*/
    public Long   sportId;

    /**
     * 优化单：71841
     * 指定阶段补时事件（injury_time）需要再超过多少分钟下发才有效
     */
    public JSONObject period2MinMinute;


    /**
     * 优化单：88998 【生产】【产品】【操盘风控】足球主玩法-客户端不展示关盘赛事尾声球头兜底优化
     * 指定阶段事件进行时间大于N需要缓存
     */
    public JSONObject period2MinMinute2;

    MatchLengthEnum(Integer code, Long time, Long sportId, String period2MinMinuteStr, String period2MinMinute2Str) {
        this.code = code;
        this.time = time;
        this.sportId = sportId;
        this.period2MinMinute = JSON.parseObject(period2MinMinuteStr);
        this.period2MinMinute2 = JSON.parseObject(period2MinMinute2Str);
    }

    /**
     * 根据运动类型，和赛制类型获取补时事件最小分钟数
     */
    public static Long getMinMinute(Long sportId,Integer code,String matchPeriodId) {
        for (MatchLengthEnum obj : values()) {
            if (obj.sportId.equals(sportId) && obj.code.equals(code)) {
                JSONObject period2MinMinute = obj.period2MinMinute;
                if(null != period2MinMinute){
                    return period2MinMinute.getLong(matchPeriodId);
                }
            }
        }
        return null;
    }

    /**
     * 根据运动类型，和赛制类型获取需要特殊处理事件最小分钟数
     */
    public static Long getMinMinute2(Long sportId,Integer code,String matchPeriodId) {
        for (MatchLengthEnum obj : values()) {
            if (obj.sportId.equals(sportId) && obj.code.equals(code)) {
                JSONObject period2MinMinute = obj.period2MinMinute2;
                if(null != period2MinMinute){
                    return period2MinMinute.getLong(matchPeriodId);
                }
            }
        }
        return null;
    }

    /**
     * 根据运动类型，和赛制类型获取全局赛事时长
     */
    public static Long getTime(Long sportId,Integer code) {
        for (MatchLengthEnum obj : values()) {
            if (obj.sportId.equals(sportId) && obj.code.equals(code)) {
                return obj.time;
            }
        }
        return matchLengt1_0.time;
    }
}
