package com.panda.merge.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 乒乓球报球板（PA）相关常量。
 * <p>
 * 与 {@code VolleyballConstant} / {@code SnookerConstant} 分开声明，
 * 避免交叉依赖；Redis hash 字段共用同一份 {@code ronghe:pd:match:status}。
 */
public class TableTennisConstant {

    public static final Long SPORT_ID = 8L;

    /** Redis hash 字段名（与 SnookerConstant / VolleyballConstant 共用同一份 hash） */
    public static final String KICKOFF_FIRST_CLICK = "kickoffFirstClick";
    public static final String TABLE_TENNIS_CURRENT_SERVER = "currentServer";
    public static final String MATCH_CURRENT_PERIOD = "currentPeriodId";
    public static final String CONTROL_TYPE = "controlType";
    public static final String MATCH_INTERRUPTED = "matchInterrupted";
    public static final String MATCH_EVENT_INTERRUPTED = "matchEventInterrupted";

    /** 比赛中断 (suspension) 时使用的 periodId */
    public static final Long PERIOD_SUSPENDED = 80L;
    /** 比赛结束使用的 periodId */
    public static final Long PERIOD_MATCH_END = 999L;

    /** 比赛阶段：每局开始 → 第几局（8=SET1, 9=SET2, ..., 442=SET7） */
    public static final Map<Long, Integer> TABLE_TENNIS_SET_BEGIN = new HashMap<>();
    static {
        TABLE_TENNIS_SET_BEGIN.put(8L, 1);
        TABLE_TENNIS_SET_BEGIN.put(9L, 2);
        TABLE_TENNIS_SET_BEGIN.put(10L, 3);
        TABLE_TENNIS_SET_BEGIN.put(11L, 4);
        TABLE_TENNIS_SET_BEGIN.put(12L, 5);
        TABLE_TENNIS_SET_BEGIN.put(441L, 6);
        TABLE_TENNIS_SET_BEGIN.put(442L, 7);
    }

    /** 比赛阶段：每局结束（小局休息）→ 第几局（301=SET1结束, 302=SET2结束, ..., 100=SET7结束） */
    public static final Map<Long, Integer> TABLE_TENNIS_SET_END = new HashMap<>();
    static {
        // 301~306 对应第1~6局结束，307对应黄金局（本球种不用），100对应第7局结束
        TABLE_TENNIS_SET_END.put(301L, 1);
        TABLE_TENNIS_SET_END.put(302L, 2);
        TABLE_TENNIS_SET_END.put(303L, 3);
        TABLE_TENNIS_SET_END.put(304L, 4);
        TABLE_TENNIS_SET_END.put(305L, 5);
        TABLE_TENNIS_SET_END.put(306L, 6);
        TABLE_TENNIS_SET_END.put(100L, 7);
    }

    /** 每局结束所需最低分数（乒乓球每局 11 分制） */
    public static final int NORMAL_SET_MIN_SCORE = 11;
    /** 局结束所需最小分差 */
    public static final int MIN_SCORE_DIFF = 2;
}
