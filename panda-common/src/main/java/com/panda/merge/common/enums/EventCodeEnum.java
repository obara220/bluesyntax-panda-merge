package com.panda.merge.common.enums;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * 设置事件类型用于接入时比对设置特定的事件类型
 *
 * @author :  tell
 * @since 2021年1月12日18:38:01
 */
public enum EventCodeEnum implements Serializable {

    EARLY_BET_STATUS("early_betstatus", "早期投注状态事件"),
    INJURY_TIME("injury_time", "伤停补时"),
    /**
     * 事件接入DTO extrainfo字段(-1:点球未开始,1:点球得分)
     */
    PENALTY_SHOOTOUT_EVENT("penalty_shootout_event", "点球大战事件"),
    PENALTY_MISSED("penalty_missed", "点球大战未进事件"),

    KICK_OFF("kick_off", "进球后第一个开球事件"),
    KICK_OFF_TEAM("kick_off_team", "赛事开赛开球球队"),

    POSSIBLE_VAR("possible_var", "可能需要视频辅助裁判"),
    VIDEO_ASSISTANT_REFEREE("video_assistant_referee", "可能需要视频辅助裁判"),
    POSSIBLE_VIDEO_ASSISTANT_REFEREE("possible_video_assistant_referee", "可能需要视频辅助裁判"),

    LOST_CONNECTION("lost_connection", "连线中断"),
    RECOVERY_CONNECTION("recovery_connection", "连线恢复"),

    MATCH_STATUS("match_status", "赛事状态事件"),
    CORNER("corner", "角球事件"),
    RED_CARD("red_card", "红牌事件"),
    YELLOW_CARD("yellow_card", "黄牌事件"),
    //足球，冰球，手球
    GOAL("goal", "进球事件"),
    //篮球，沙滩排球

    SCORE_CHANGE("score_change", "比分改变事件"),
    SCORE_CORRECTION("score_correction", "修正比分事件"),
    RUN_SCORED("run_scored", "棒球得分事件"),
    TENNIS_SCORE_CHANGE("tennis_score_change", "网球得分事件"),
    EXTRA_POINT("extra_point", "美式足球得分事件"),
    BALL_POT("ball_pot", "斯诺克得分事件"),
    TABLE_TENNIS_SCORE_CHANGE("table_tennis_score_change", "乒乓球得分事件"),
    VOLLEYBALL_SCORE_CHANGE("volleyball_score_change", "排球得分事件"),
    BADMINTON_SCORE_CHANGE("badminton_score_change", "羽毛球得分事件"),
    POINT("point", "板球得分事件"),
    OVER("over", "板球轮事件"),

    MISS_2P("2p_miss", "二分未进"),
    MISS_3P("3p_miss", "三分未进"),
    SCORE_MISS("score_miss", "未命中"),


    DELETE_EVENT("delete_event", "删除事件"),
    GOAL_TIME_MODIFIED("goal_time_modified", "进球时间修改事件"),
    REDCARD_TIME_MODIFIED("redcard_time_modified", "红牌时间修改事件"),
    YELLOWCARD_TIME_MODIFIED("yellowcard_time_modified", "黄牌时间修改事件"),
    CONNER_TIME_MODIFIED("corner_time_modified", "角球时间修改事件"),

    POSSIBLE_PENALTY("possible_penalty", "当很有可能出现点球时产生该事件"),

    TAKE_PENALTY("take_penalty", "开始点球"),
    PENALTY_AWARDED("penalty_awarded", "获得点球"),
    GOAL_CONFIRM("goal_confirm", "进球确认"),
    VIDEO_ASSISTANT_REFEREE_OVER("video_assistant_referee_over", "视频辅助裁判结束"),
    CANCELED_VIDEO_ASSISTANT_REFEREE("canceled_video_assistant_referee", "视频辅助裁判取消"),
    ATTACK("attack", "进攻"),
    FREE_KICK("free_kick", "任意球"),
    BALL_SAFE("ball_safe", "安全球"),

    CANCELED_GOAL("canceled_goal", "取消进球"),
    POSSIBLE_GOAL("possible_goal", "可能进球"),

    CANCELED_PENALTY("canceled_penalty", "点球取消"),

    GOAL_UNDER_INVESTIGATION("goal_under_investigation", "进球调查中"),
    POSSIBLE_RED_CARD("possible_red_card", "可能红牌"),

    PENALTY_GOAL("penalty_goal", "确认点球进球"),
    VAR_GOAL("var_goal", "VAR确认进球"),


    VAR_YELLOW_CARD("var_yellow_card", "VAR确认黄牌"),
    YELLOW_RED_CARD("yellow_red_card", "第二次黄牌"),

    VAR_REASON("var_reason", "视频助理裁判原因发送"),
    VAR_REVIEWING("var_reviewing", "视频助理裁判审查原因"),

    ;

    /**
     * 描述说明
     */
    public String code;
    public String desc;

    EventCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 需要缓存比分的事件编码
     */
    public static List<String> getScoresEventCodes() {
        return Lists.newArrayList(EventCodeEnum.GOAL.code, EventCodeEnum.CORNER.code,
                EventCodeEnum.RED_CARD.code, EventCodeEnum.YELLOW_CARD.code);
    }

    /**
     * 删除事件-需要缓存的事件编码
     */
    public static List<String> getDeleteEventCodes() {
        return Lists.newArrayList(EventCodeEnum.MATCH_STATUS.code
                , EventCodeEnum.GOAL.code, EventCodeEnum.CORNER.code, EventCodeEnum.RED_CARD.code, EventCodeEnum.YELLOW_CARD.code
                , EventCodeEnum.SCORE_CHANGE.code, EventCodeEnum.RUN_SCORED.code
                , EventCodeEnum.TENNIS_SCORE_CHANGE.code, EventCodeEnum.EXTRA_POINT.code
                , EventCodeEnum.BALL_POT.code, EventCodeEnum.TABLE_TENNIS_SCORE_CHANGE.code, EventCodeEnum.VOLLEYBALL_SCORE_CHANGE.code
                , EventCodeEnum.BADMINTON_SCORE_CHANGE.code, EventCodeEnum.POINT.code
        );
    }


    /**
     * 删除事件需要告警比分的事件编码
     */
    public static List<String> alertsDeleteEventCodes() {
        return Lists.newArrayList(EventCodeEnum.GOAL.code, EventCodeEnum.CORNER.code, EventCodeEnum.YELLOW_CARD.code);
    }

    /**
     * V02动画需要校验的比分类型
     */
    public static List<String> getVideoScoresEventCodes() {
        return Lists.newArrayList(EventCodeEnum.GOAL.code, EventCodeEnum.CORNER.code, "redCard", "yellowCard");
    }

    /**
     * 范特西赛事需要处理比分的事件类型集合
     */
    public static List<String> getFtsScoresEventCodes() {
        return Lists.newArrayList(EventCodeEnum.GOAL.code, EventCodeEnum.CORNER.code, EventCodeEnum.RED_CARD.code, EventCodeEnum.YELLOW_CARD.code,
                EventCodeEnum.MATCH_STATUS.code, EventCodeEnum.KICK_OFF.code);
    }


    /**
     * Z01动画非主事件源无需处理的事件编码
     * 单号：87646 主事件源事件特殊处理
     */
    public static List<String> getZ01NotProcessEventCodes() {
        return Lists.newArrayList(EventCodeEnum.LOST_CONNECTION.code, EventCodeEnum.RECOVERY_CONNECTION.code);
    }

    /**
     * dubbo处理的重要事件
     */
    public static List<String> getDubboEventCodes() {
        return Lists.newArrayList(EventCodeEnum.GOAL.code, EventCodeEnum.CORNER.code,
                EventCodeEnum.RED_CARD.code, EventCodeEnum.YELLOW_CARD.code, EventCodeEnum.DELETE_EVENT.code);
    }

    /**
     * 104504 【生产】【产品】【pc＆h5】自研动画var＆可能点球事件常驻展示
     * var后续结束常驻事件（自研动画和列表/比分版需一致）
     * @return
     */
    public static List<String> getZ01VarSubsequentEvents() {
        return Lists.newArrayList(
                EventCodeEnum.TAKE_PENALTY.code,
                EventCodeEnum.PENALTY_AWARDED.code,
                EventCodeEnum.GOAL_CONFIRM.code,
                EventCodeEnum.VIDEO_ASSISTANT_REFEREE_OVER.code,
                EventCodeEnum.CANCELED_VIDEO_ASSISTANT_REFEREE.code,
                EventCodeEnum.ATTACK.code,
                EventCodeEnum.FREE_KICK.code,
                EventCodeEnum.BALL_SAFE.code,
                EventCodeEnum.INJURY_TIME.code,
                EventCodeEnum.CANCELED_GOAL.code,
                EventCodeEnum.POSSIBLE_GOAL.code,
                EventCodeEnum.POSSIBLE_PENALTY.code,
                EventCodeEnum.CANCELED_PENALTY.code,
                EventCodeEnum.PENALTY_MISSED.code,
                EventCodeEnum.GOAL_UNDER_INVESTIGATION.code,
                EventCodeEnum.POSSIBLE_RED_CARD.code,
                EventCodeEnum.GOAL.code,
                EventCodeEnum.PENALTY_GOAL.code,
                EventCodeEnum.VAR_GOAL.code,
                EventCodeEnum.CORNER.code,
                EventCodeEnum.YELLOW_CARD.code,
                EventCodeEnum.VAR_YELLOW_CARD.code,
                EventCodeEnum.YELLOW_RED_CARD.code,
                EventCodeEnum.RED_CARD.code
                );
    }

    /**
     * 104504 【生产】【产品】【pc＆h5】自研动画var＆可能点球事件常驻展示
     * 可能点球后续结束常驻事件（自研动画和列表/比分版需一致）
     * @return
     */
    public static List<String> getZ01PossiblePenaltySubsequentEvents() {
        return Lists.newArrayList(
                EventCodeEnum.TAKE_PENALTY.code,
                EventCodeEnum.PENALTY_AWARDED.code,
                EventCodeEnum.CANCELED_PENALTY.code,
                EventCodeEnum.ATTACK.code,
                EventCodeEnum.FREE_KICK.code,
                EventCodeEnum.BALL_SAFE.code,
                EventCodeEnum.POSSIBLE_VIDEO_ASSISTANT_REFEREE.code,
                EventCodeEnum.VIDEO_ASSISTANT_REFEREE.code,
                EventCodeEnum.POSSIBLE_VAR.code,
                EventCodeEnum.VAR_REASON.code,
                EventCodeEnum.VAR_REVIEWING.code,
                EventCodeEnum.GOAL_UNDER_INVESTIGATION.code,
                EventCodeEnum.INJURY_TIME.code,
                EventCodeEnum.CANCELED_GOAL.code,
                EventCodeEnum.POSSIBLE_RED_CARD.code,
                EventCodeEnum.POSSIBLE_GOAL.code,
                EventCodeEnum.GOAL.code,
                EventCodeEnum.PENALTY_GOAL.code,
                EventCodeEnum.VAR_GOAL.code,
                EventCodeEnum.CORNER.code,
                EventCodeEnum.YELLOW_CARD.code,
                EventCodeEnum.VAR_YELLOW_CARD.code,
                EventCodeEnum.YELLOW_RED_CARD.code,
                EventCodeEnum.RED_CARD.code
        );
    }
}
