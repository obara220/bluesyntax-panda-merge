package com.panda.merge.constant;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum MatchInfoConvertEnum {
    MATCH_START(1, 10049, "比赛开始", "match_status", null, 1),
    MATCH_INTERRUPTED(2, 10053,"比赛中断", "suspension", null, null),
    MATCH_RESTART(3, 10056, "比赛恢复", "suspension_over", null,null),
    MATCH_END(4, 10052, "比赛结束", "match_status", null, 3),
    MATCH_EVENT_INTERRUPTED(5, 100113, "赛事中断", "match_status", null,80),
    MATCH_EVENT_RESTART(6, 100114,"赛事重开", "match_status",null,1),

    /**
     * 球种
     */
    FOOTBBALL(null, 100100, "报球板-足球", "Match Interrupted", 1l, null),
    SNOOKER(null, 100105, "报球板-斯诺克", "Match Ended", 7l, null),
    VOLLEYBALL(null, 100129, "报球板-排球", "PA Live Feed - Volleyball", 9L, null),
    ;

    private final Integer curCode;
    private final Integer oriCode;
    private final String name;
    private final String value;
    private final Long sportId;
    private final Integer matchStatus;   //1:滚球 3：结束 4：赛程中断


    MatchInfoConvertEnum(Integer curCode, Integer oriCode, String name, String value, Long sportId, Integer matchStatus) {
        this.curCode = curCode;
        this.oriCode = oriCode;
        this.name = name;
        this.value = value;
        this.sportId = sportId;
        this.matchStatus = matchStatus;
    }

    public static MatchInfoConvertEnum getByCurCode(Integer curCode) {
        for (MatchInfoConvertEnum value : MatchInfoConvertEnum.values()) {
            if (value.getCurCode().equals(curCode)) {
                return value;
            }
        }
        return null;
    }

    public static MatchInfoConvertEnum getBySport(Long sprotId) {
        if (sprotId == null) {
            return null;
        }
        for (MatchInfoConvertEnum value : MatchInfoConvertEnum.values()) {
            if (value.getSportId() != null && value.getSportId().equals(sprotId)) {
                return value;
            }
        }
        return null;
    }
}
