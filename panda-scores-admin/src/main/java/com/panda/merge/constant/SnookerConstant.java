package com.panda.merge.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: common constant
 * @author: Henry Wang
 * @create: 2024-07-22 15:45
 **/
public class SnookerConstant {

    public static final Long MINUTE = 60l;
    public static final String SNOOKER_CUR_HIGHEST_SCORE = "snooker:cur:highest:score:";
    public static final String RONGHE_PD_MATCH_STATUS = "ronghe:pd:match:status:";
    public static final String SNOOKER_RERACK_PERIOD_SCORE = "snooker:rerack:period:score:";
    public static final String SNOOKER_PERIOD_BEGIN_EVENT_ID = "snooker:period:begin:event:id:";
    public static final String SNOOKER_RERACK = "rerack";
    public static final String KICKOFF_FIRST_CLICK = "kickoffFirstClick";
    public static final String CURRENT_STRIKER = "currentStriker";
    public static final String MATCH_CURRENT_PERIOD = "currentPeriodId";
    public static final String MATCH_INTERRUPTED = "matchInterrupted";
    public static final String MATCH_EVENT_INTERRUPTED = "matchEventInterrupted";
    public static final String CONTROL_TYPE = "controlType";
    /** 赛事中断前的阶段ID（存于 ronghe:pd:match:status:{thirdMatchId}） */
    public static final String PREVIOUS_PERIOD_BEFORE_INTERRUPT = "previousPeriodBeforeInterrupt";
    public static final String HOME = "home";
    public static final String AWAY = "away";

    public static final Map<String, Integer> WINNING_SCORE_EVENT_TYPE = new HashMap<>();
    static {
        WINNING_SCORE_EVENT_TYPE.put("snooker_score_red", 1);
        WINNING_SCORE_EVENT_TYPE.put("snooker_score_yellow", 2);
        WINNING_SCORE_EVENT_TYPE.put("snooker_score_green", 3);
        WINNING_SCORE_EVENT_TYPE.put("snooker_score_brown", 4);
        WINNING_SCORE_EVENT_TYPE.put("snooker_score_blue", 5);
        WINNING_SCORE_EVENT_TYPE.put("snooker_score_pink", 6);
        WINNING_SCORE_EVENT_TYPE.put("snooker_score_black", 7);
    }

    public static final Map<Long, Integer> SNOOKER_SET_BEGIN = new HashMap<>();
    static {
        SNOOKER_SET_BEGIN.put(8l, 1);
        SNOOKER_SET_BEGIN.put(9l, 2);
        SNOOKER_SET_BEGIN.put(10l, 3);
        SNOOKER_SET_BEGIN.put(11l, 4);
        SNOOKER_SET_BEGIN.put(12l, 5);
        SNOOKER_SET_BEGIN.put(441l, 6);
        SNOOKER_SET_BEGIN.put(442l, 7);
        SNOOKER_SET_BEGIN.put(500l, 8);
        SNOOKER_SET_BEGIN.put(501l, 9);
        SNOOKER_SET_BEGIN.put(502l, 10);
        SNOOKER_SET_BEGIN.put(503l, 11);
        SNOOKER_SET_BEGIN.put(504l, 12);
        SNOOKER_SET_BEGIN.put(505l, 13);
        SNOOKER_SET_BEGIN.put(506l, 14);
        SNOOKER_SET_BEGIN.put(507l, 15);
        SNOOKER_SET_BEGIN.put(508l, 16);
        SNOOKER_SET_BEGIN.put(509l, 17);
        SNOOKER_SET_BEGIN.put(510l, 18);
        SNOOKER_SET_BEGIN.put(511l, 19);
        SNOOKER_SET_BEGIN.put(512l, 20);
        SNOOKER_SET_BEGIN.put(513l, 21);
        SNOOKER_SET_BEGIN.put(514l, 22);
        SNOOKER_SET_BEGIN.put(515l, 23);
        SNOOKER_SET_BEGIN.put(516l, 24);
        SNOOKER_SET_BEGIN.put(517l, 25);
        SNOOKER_SET_BEGIN.put(518l, 26);
        SNOOKER_SET_BEGIN.put(519l, 27);
        SNOOKER_SET_BEGIN.put(520l, 28);
        SNOOKER_SET_BEGIN.put(521l, 29);
        SNOOKER_SET_BEGIN.put(522l, 30);
        SNOOKER_SET_BEGIN.put(523l, 31);
        SNOOKER_SET_BEGIN.put(524l, 32);
        SNOOKER_SET_BEGIN.put(525l, 33);
        SNOOKER_SET_BEGIN.put(526l, 34);
        SNOOKER_SET_BEGIN.put(527l, 35);
    }

    public static final Map<Long, Integer> SNOOKER_SET_END = new HashMap<>();
    static {
        // 结束阶段ID（小局休息）：301=第一局结束，302=第二局结束 ... 335=第三十五局结束
        for (int i = 0; i < 35; i++) {
            SNOOKER_SET_END.put(301L + i, i + 1);
        }
    }

    /**
     * 从开始阶段ID映射到结束阶段ID（小局休息）
     * 第一局开始(8) -> 第一局结束(301)
     * 第二局开始(9) -> 第二局结束(302)
     * ...
     */
    public static final Map<Long, Long> SNOOKER_SET_BEGIN_TO_END = new HashMap<>();
    static {
        SNOOKER_SET_BEGIN_TO_END.put(8L, 301L);   // 第一局开始 -> 第一局结束
        SNOOKER_SET_BEGIN_TO_END.put(9L, 302L);   // 第二局开始 -> 第二局结束
        SNOOKER_SET_BEGIN_TO_END.put(10L, 303L);  // 第三局开始 -> 第三局结束
        SNOOKER_SET_BEGIN_TO_END.put(11L, 304L); // 第四局开始 -> 第四局结束
        SNOOKER_SET_BEGIN_TO_END.put(12L, 305L); // 第五局开始 -> 第五局结束
        SNOOKER_SET_BEGIN_TO_END.put(441L, 306L); // 第六局开始 -> 第六局结束
        SNOOKER_SET_BEGIN_TO_END.put(442L, 307L); // 第七局开始 -> 第七局结束
        SNOOKER_SET_BEGIN_TO_END.put(500L, 308L); // 第八局开始 -> 第八局结束
        SNOOKER_SET_BEGIN_TO_END.put(501L, 309L); // 第九局开始 -> 第九局结束
        SNOOKER_SET_BEGIN_TO_END.put(502L, 310L); // 第十局开始 -> 第十局结束
        SNOOKER_SET_BEGIN_TO_END.put(503L, 311L); // 第十一局开始 -> 第十一局结束
        SNOOKER_SET_BEGIN_TO_END.put(504L, 312L); // 第十二局开始 -> 第十二局结束
        SNOOKER_SET_BEGIN_TO_END.put(505L, 313L); // 第十三局开始 -> 第十三局结束
        SNOOKER_SET_BEGIN_TO_END.put(506L, 314L); // 第十四局开始 -> 第十四局结束
        SNOOKER_SET_BEGIN_TO_END.put(507L, 315L); // 第十五局开始 -> 第十五局结束
        SNOOKER_SET_BEGIN_TO_END.put(508L, 316L); // 第十六局开始 -> 第十六局结束
        SNOOKER_SET_BEGIN_TO_END.put(509L, 317L); // 第十七局开始 -> 第十七局结束
        SNOOKER_SET_BEGIN_TO_END.put(510L, 318L); // 第十八局开始 -> 第十八局结束
        SNOOKER_SET_BEGIN_TO_END.put(511L, 319L); // 第十九局开始 -> 第十九局结束
        SNOOKER_SET_BEGIN_TO_END.put(512L, 320L); // 第二十局开始 -> 第二十局结束
        SNOOKER_SET_BEGIN_TO_END.put(513L, 321L); // 第二十一局开始 -> 第二十一局结束
        SNOOKER_SET_BEGIN_TO_END.put(514L, 322L); // 第二十二局开始 -> 第二十二局结束
        SNOOKER_SET_BEGIN_TO_END.put(515L, 323L); // 第二十三局开始 -> 第二十三局结束
        SNOOKER_SET_BEGIN_TO_END.put(516L, 324L); // 第二十四局开始 -> 第二十四局结束
        SNOOKER_SET_BEGIN_TO_END.put(517L, 325L); // 第二十五局开始 -> 第二十五局结束
        SNOOKER_SET_BEGIN_TO_END.put(518L, 326L); // 第二十六局开始 -> 第二十六局结束
        SNOOKER_SET_BEGIN_TO_END.put(519L, 327L); // 第二十七局开始 -> 第二十七局结束
        SNOOKER_SET_BEGIN_TO_END.put(520L, 328L); // 第二十八局开始 -> 第二十八局结束
        SNOOKER_SET_BEGIN_TO_END.put(521L, 329L); // 第二十九局开始 -> 第二十九局结束
        SNOOKER_SET_BEGIN_TO_END.put(522L, 330L); // 第三十局开始 -> 第三十局结束
        SNOOKER_SET_BEGIN_TO_END.put(523L, 331L); // 第三十一局开始 -> 第三十一局结束
        SNOOKER_SET_BEGIN_TO_END.put(524L, 332L); // 第三十二局开始 -> 第三十二局结束
        SNOOKER_SET_BEGIN_TO_END.put(525L, 333L); // 第三十三局开始 -> 第三十三局结束
        SNOOKER_SET_BEGIN_TO_END.put(526L, 334L); // 第三十四局开始 -> 第三十四局结束
        SNOOKER_SET_BEGIN_TO_END.put(527L, 335L); // 第三十五局开始 -> 第三十五局结束
    }
}
