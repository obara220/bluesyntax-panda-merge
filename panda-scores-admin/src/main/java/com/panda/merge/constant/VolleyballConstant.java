package com.panda.merge.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 排球报球板相关常量。
 * <p>
 * 字段值与 {@code SnookerConstant} 中的同名 Redis hash 字段保持一致（共用一份
 * ronghe:pd:match:status:{thirdMatchId} 哈希）。这里重新声明的目的是让排球代码
 * 不必直接引用 SnookerConstant，未来若需要分化（例如真正拆分到独立 Redis key），
 * 只需改这里一处。
 */
public class VolleyballConstant {

    public static final Long SPORT_ID = 9L;

    public static final String VOLLEYBALL_PERIOD_BEGIN_EVENT_ID = "volleyball:period:begin:event:id:";
    public static final String VOLLEYBALL_CURRENT_SERVER = "currentServer";

    /** Redis hash 字段名（与 SnookerConstant 同步，共用同一份 ronghe:pd:match:status 哈希） */
    public static final String KICKOFF_FIRST_CLICK = "kickoffFirstClick";
    public static final String MATCH_CURRENT_PERIOD = "currentPeriodId";
    public static final String CONTROL_TYPE = "controlType";
    public static final String MATCH_INTERRUPTED = "matchInterrupted";
    public static final String MATCH_EVENT_INTERRUPTED = "matchEventInterrupted";
    public static final String PREVIOUS_PERIOD_BEFORE_INTERRUPT = "previousPeriodBeforeInterrupt";

    /** 比赛中断 (suspension) 时使用的 periodId（与父类 AbsMatchCommonProcessor 写入 DB 的 80L 对齐） */
    public static final Long PERIOD_SUSPENDED = 80L;
    /** 比赛结束使用的 periodId（排球语义；与斯诺克的 100L 不同） */
    public static final Long PERIOD_MATCH_END = 999L;

    /** 比赛阶段：每局开始 → 第几局 */
    public static final Map<Long, Integer> VOLLEYBALL_SET_BEGIN = new HashMap<>();
    static {
        //PS:排球跟斯诺克不一样，不做阶段->局次的转换，以阶段ID作为比分KEY1
        VOLLEYBALL_SET_BEGIN.put(8L, 1);
        VOLLEYBALL_SET_BEGIN.put(9L, 2);
        VOLLEYBALL_SET_BEGIN.put(10L, 3);
        VOLLEYBALL_SET_BEGIN.put(11L, 4);
        VOLLEYBALL_SET_BEGIN.put(12L, 5);
        VOLLEYBALL_SET_BEGIN.put(441L, 6);
        VOLLEYBALL_SET_BEGIN.put(442L, 7);

//        VOLLEYBALL_SET_BEGIN.put(301L, 8);
//        VOLLEYBALL_SET_BEGIN.put(308L, 9);
//        VOLLEYBALL_SET_BEGIN.put(303L, 10);
//        VOLLEYBALL_SET_BEGIN.put(304L, 11);
//        VOLLEYBALL_SET_BEGIN.put(305L, 12);
//        VOLLEYBALL_SET_BEGIN.put(306L, 441);
//        VOLLEYBALL_SET_BEGIN.put(307L, 442);
    }

    /** 比赛阶段：每局结束（小局休息）→ 第几局。301=第一局结束 ... 306=第六局结束，307=黄金局结束 */
    public static final Map<Long, Integer> VOLLEYBALL_SET_END = new HashMap<>();
    static {
        // 常规局结束 ID 连续：301 ... 307 对应第一到第六局
        for (int i = 0; i < 7; i++) {
            VOLLEYBALL_SET_END.put(301L + i, i + 1);
        }
    }

    /** 每局开始 → 该局的休息（结束）阶段 */
    public static final Map<Long, Long> VOLLEYBALL_SET_BEGIN_TO_END = new HashMap<>();
    static {
        VOLLEYBALL_SET_BEGIN_TO_END.put(8L, 301L);    // 第一局开始 -> 第一局结束
        VOLLEYBALL_SET_BEGIN_TO_END.put(9L, 302L);    // 第二局开始 -> 第二局结束
        VOLLEYBALL_SET_BEGIN_TO_END.put(10L, 303L);   // 第三局开始 -> 第三局结束
        VOLLEYBALL_SET_BEGIN_TO_END.put(11L, 304L);   // 第四局开始 -> 第四局结束
        VOLLEYBALL_SET_BEGIN_TO_END.put(12L, 305L);   // 第五局开始 -> 第五局结束
        VOLLEYBALL_SET_BEGIN_TO_END.put(441L, 306L);  // 第六局开始 -> 第六局结束
        VOLLEYBALL_SET_BEGIN_TO_END.put(442L, 307L);  // 黄金局开始 -> 黄金局结束
    }

    /** 黄金局（决胜局）开始 periodId */
    public static final Long GOLDEN_SET_BEGIN = 442L;

    /** 普通局结束所需最低分数 */
    public static final int NORMAL_SET_MIN_SCORE = 25;
    /** 黄金局结束所需最低分数 */
    public static final int GOLDEN_SET_MIN_SCORE = 15;
    /** 局结束所需最小分差 */
    public static final int MIN_SCORE_DIFF = 2;
}
