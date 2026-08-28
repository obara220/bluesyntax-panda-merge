package com.panda.merge.constant;

import lombok.Getter;

/**
 * 乒乓球报球板（PA）事件类型枚举。
 * <p>
 * doCalculation 行为：
 *   score=1 → 事件持有人计数字段 +1；setScore 按 opposite 决定加在哪侧（false=本方, true=对手）
 *   score=null → 事件持有人计数字段仍 +1（pure 兜底为 1），但 setScore 不变
 * <p>
 * perSetCounter=true：sendEvent 时 MatchEventInfoDTO 的 t1/t2 携带「当前局该统计字段累计次数」，
 * 便于 PD 直接读取；其它事件 t1/t2 保留盘比分（matchScore）。
 */
@Getter
public enum TableTennisEventTypeEnum {

    // —— 发球流程（不影响计分；仅累加 kickoff / serve 计数） ——
    KICK_OFF("which_team_serves_first", "先发球", null, Boolean.FALSE, "Which team serves first", false),
    CURRENT_SERVE_TABLE_TENNIS("current_serve_tabletennis", "发球", null, Boolean.FALSE, "Serve", false),
    RE_SERVE("re_serve", "重新发球", null, Boolean.FALSE, "Re-serve", false),

    // —— 持有人得分（setScore.actor +1） ——
    TABLE_TENNIS_SCORE_CHANGE("table_tennis_score_change", "赢分", 1, Boolean.FALSE, "Score change", true),

    // —— 对手得分（持有人犯错，setScore.opponent +1） ——
    RED_CARD("red_card", "红牌", 1, Boolean.TRUE, "Red card", false),

    // —— 仅累加计数字段，不直接动 setScore（score=null）——
    YELLOW_CARD("yellow_card", "黄牌", null, Boolean.FALSE, "Yellow card", true),
    EXPEDITE_MODE("expedite_mode", "加速模式", null, Boolean.FALSE, "Expedite mode", true),
    YELLOW_RED_CARD_SAME_HAND("yellowred_card_same_hand", "红黄牌同手", null, Boolean.FALSE, "Yellow-red card same hand", true),

    // —— 状态/行政事件（不影响 setScore） ——
    MATCH_STATUS("match_status", "小局休息", null, Boolean.FALSE, "Small break", false),
    MATCH_OVER_999("match_status", "正常结束", null, Boolean.FALSE, "Match end", false),
    SUSPENSION("suspension", "比赛中断", null, Boolean.FALSE, "Suspension", false),
    SUSPENSION_OVER("suspension_over", "比赛重开", null, Boolean.FALSE, "Suspension over", false),
    TIMEOUT("timeout", "比赛暂停", null, Boolean.FALSE, "Timeout", false),
    TIMEOUT_OVER("timeout_over", "比赛继续", null, Boolean.FALSE, "Timeout over", false),
    DELETE_EVENT("delete_event", "删除事件", null, Boolean.FALSE, "Delete event", false),
    ;

    private final String eventCode;
    private final String name;
    private final Integer score;
    private final Boolean opposite;
    private final String engName;
    private final boolean perSetCounter;

    TableTennisEventTypeEnum(String eventCode, String name, Integer score, Boolean opposite,
                              String engName, boolean perSetCounter) {
        this.eventCode = eventCode;
        this.name = name;
        this.score = score;
        this.opposite = opposite;
        this.engName = engName;
        this.perSetCounter = perSetCounter;
    }

    public static TableTennisEventTypeEnum getByCode(String eventCode) {
        for (TableTennisEventTypeEnum value : TableTennisEventTypeEnum.values()) {
            if (value.getEventCode().equals(eventCode)) {
                return value;
            }
        }
        return null;
    }
}
