package com.panda.merge.constant;

import com.panda.merge.model.ConfigTemplateEvent;
import lombok.Getter;

@Getter
public enum VolleyballEventTypeEnum {
    // doCalculation 行为：
    //   score=1 → 事件持有人计数字段 +1；setScore 按 opposite 决定加在哪侧（false=本方, true=对手）
    //   score=null → 事件持有人计数字段仍 +1（pure 兜底为 1），但 setScore 不变
    // 排球每个 rally 只产生 1 分，计分类事件都应是 1。
    //
    // perSetCounter=true：sendEvent 时 MatchEventInfoDTO 的 t1/t2 携带「当前局该统计字段累计次数」
    // （periodScores 对应 CommonItem），便于 PD 直接读取；其它事件 t1/t2 保留盘比分（matchScore）。

    // —— 发球流程（不影响计分；仅累加 kickoff / serve 计数） ——
    KICK_OFF("which_team_serves_first", "先发球", null, Boolean.FALSE, "Which team serves first", false),
    CURRENT_SERVE_VOLLEYBALL("current_serve_volleyball", "发球", null, Boolean.FALSE, "Serve", false),

    // —— 持有人得分（setScore.actor +1） ——
    ACE("ace", "发球得分", 1, Boolean.FALSE, "Ace", true),
    // 赢分没有独立计数字段，仍按 matchScore 输出
    VOLLEYBALL_SCORE_CHANGE("volleyball_score_change", "赢分", 1, Boolean.FALSE, "Score change", false),

    // —— 对手得分（持有人犯错/失误，setScore.opponent +1） ——
    SERVICE_ERROR("service_error", "发球失误", 1, Boolean.TRUE, "Service error", true),
    OUT("out", "出界", 1, Boolean.TRUE, "Out", true),
    ERROR("error", "失误", 1, Boolean.TRUE, "Error", true),

    // —— 仅累加计数字段，不直接动 setScore（score=null）：
    //    实际比分通过 VOLLEYBALL_SCORE_CHANGE 或 batchEditScores 单独处理 ——
    KILL("kill", "扣杀", null, Boolean.FALSE, "Kill", true),
    BLOCK("block", "拦网", null, Boolean.FALSE, "Block", true),
    EXPULSION("expulsion", "驱逐", null, Boolean.FALSE, "Expulsion", true),
    DISQUALIFICATION("disqualification", "取消资格", null, Boolean.FALSE, "Disqualification", true),
    PENALTY("penalty", "处罚", null, Boolean.FALSE, "Penalty", true),

    // —— 状态/行政事件（不影响 setScore；这些事件 code 不会落到任何 @ScoresProperty 字段） ——
    // 注意：MATCH_STATUS 必须排在所有 MATCH_OVER_* 之前 —— getByCode("match_status")
    // 按声明顺序返回首个命中项，需要它路由到 MATCH_STATUS（小局休息）而非具体的结束分支。
    MATCH_STATUS("match_status", "小局休息", null, Boolean.FALSE, "Small break", false),
    MATCH_OVER_999("match_status", "正常结束", null, Boolean.FALSE, "Match end", false),
    MATCH_OVER_93("match_status", "主队赢，客队未到场", null, Boolean.FALSE, "Home wins, away not present", false),
    MATCH_OVER_94("match_status", "客队赢，主队未到场", null, Boolean.FALSE, "Away wins, home not present", false),
    MATCH_OVER_95("match_status", "由于主队退赛，客队赢", null, Boolean.FALSE, "Home withdrawn, away wins", false),
    MATCH_OVER_96("match_status", "由于客队退赛，主队赢", null, Boolean.FALSE, "Away withdrawn, home wins", false),
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


    VolleyballEventTypeEnum(String eventCode, String name, Integer score, Boolean opposite, String engName, boolean perSetCounter) {
        this.eventCode = eventCode;
        this.name = name;
        this.score = score;
        this.opposite = opposite;
        this.engName = engName;
        this.perSetCounter = perSetCounter;
    }

    public static VolleyballEventTypeEnum getByCode(String eventCode) {
        for (VolleyballEventTypeEnum value : VolleyballEventTypeEnum.values()) {
            if (value.getEventCode().equals(eventCode)) {
                return value;
            }
        }
        return null;
    }
}
