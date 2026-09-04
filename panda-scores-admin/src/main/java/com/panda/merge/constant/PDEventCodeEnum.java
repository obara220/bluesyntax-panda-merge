package com.panda.merge.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum PDEventCodeEnum {
    /**
     *------------------------------------报球板regular event code------------------------------------
     */

    /**-----confirm event code-----*/
    GOAL("goal", "进球确认"),
    YELLOW_CARD("yellow_card", "黄牌确认"),
    RED_CARD("red_card", "红牌确认"),
    CORNER("corner", "角球确认"),
    YELLOW_RED_CARD("yellow_red_card", "红黄牌确认"),
    THROW_IN("throw_in", "掷界外球"),
    POSSESSION("possession", "控球权"),
    BALL_POSSESSION_PERCENTAGE("ball_possession_percentage", "控球率"),
    POSSESSION_COUNT("possession_count", "持球数(持球事件下发次数)"),
    ATTACK("attack", "进攻"),
    GOAL_KICK("goal_kick", "球门球"),
    FREE_KICK("free_kick", "确认任意球"),
    OFFSIDE("offside", "越位"),
    SHOT_ON_TARGET("shot_on_target", "射正"),
    SHOT_OFF_TARGET("shot_off_target", "射偏"),
    DANGEROUS_ATTACK("dangerous_attack", "危险"),
    PENALTY("penalty", "点球确认"),
    PENALTY_GOAL("penalty_goal", "点球状态: 确认点球进球"),

    /**
     * possible event code
     */
    POSSIBLE_RED_CARD("possible_red_card", "可能红牌"),
    POSSIBLE_YELLOW_CARD("possible_yellow_card", "可能黄牌"),
    POSSIBLE_CORNER("possible_corner", "可能角球"),
    POSSIBLE_PENALTY("possible_penalty", "可能点球"),
    POSSIBLE_GOAL("possible_goal", "可能进球"),
    POSSIBLE_FREE_KICK("possible_free_kick", "可能任意球"),
    POSSIBLE_VIDEO_ASSISTANT_REFEREE("possible_video_assistant_referee", "可能需要视频辅助裁判"),

    /**-----cancel event code-----*/
    CANCELED_PENALTY("canceled_penalty", "取消点球"),
    PENALTY_MISSED("penalty_missed", "点球状态: 点球未进"),
    PENALTY_CANCELED("penalty_canceled", "点球状态: 取消点球"),
    CANCELED_YELLOW_CARD("canceled_yellow_card", "取消黄牌"),
    CANCELED_RED_CARD("canceled_red_card", "取消红牌"),
    CANCELED_CORNER("canceled_corner", "取消角球"),
    CANCELED_GOAL("canceled_goal", "取消进球"),
    CANCELED_FREE_KICK("canceled_free_kick", "取消可能的任意球"),

    /**------------------------------------VAR Event Code------------------------------------*/
    POSSIBLE_VAR_RED_CARD("possible_var_red_card", "可能VAR: 罚牌"),
    POSSIBLE_VAR_GOAL("possible_var_goal", "可能VAR: 进球"),
    POSSIBLE_VAR_PENALTY("possible_var_penalty", "可能VAR: 点球"),
    VAR_RED_CARD("var_red_card", "VAR确认罚牌"),
    VAR_GOAL("var_goal", "VAR确认进球"),
    VAR_PENALTY("var_penalty", "VAR确认点球"),
    VAR_YELLOW_CARD("var_yellow_card", " VAR确认黄牌"),
    CANCELED_VAR_RED_CARD("canceled_var_red_card", "VAR取消罚牌"),
    CANCELED_VAR_GOAL("canceled_var_goal", "VAR取消进球"),
    CANCELED_VAR_PENALTY("canceled_var_penalty", "VAR取消点球"),
    VIDEO_ASSISTANT_REFEREE_OVER("video_assistant_referee_over", "确认进入VAR"),   // extra_info: 0: 进球 1: 点球 2: 红牌
    CANCELED_VIDEO_ASSISTANT_REFEREE("canceled_video_assistant_referee", "取消进入VAR"), // extra_info: 0: 进球 1: 点球 2: 红牌

    /**
     * ------------------------------------other Event Code------------------------------------
     */
    KICK_OFF("kick_off", "开球"),
    KICK_OFF_TEAM("kick_off_team", "开球球队"),
    SUBSTITUTION("substitution", "换人"),
    RETAKE_PEN("retake_pen", "点球重踢"),
    INJURY("injury", "受伤"),
    BALL_SAFE("ball_safe", "安全球"),
    VAR_REASON("var_reason", "VAR原因"),
    WATER_BREAK("water_break", "喝水"),
    INJURY_TIME("injury_time", "伤停补时"),

    /**------------------------------------点球大战Event Code------------------------------------*/
    PENALTY_FIRST("penalty_first", "先罚"),

    TAKE_PENALTY("take_penalty", "开始点球"),

    PENALTY_ABOUT_TO_BE_TAKEN("penalty_about_to_be_taken", "点球即将开始");

    private final String eventCode;

    private final String eventName;

    /**
     * ------------------------------- 点击进球后，启用VAR按钮------------------------------------
     */
    public static final List<PDEventCodeEnum> ENABLE_VAR_BUTTON = Arrays.asList(VIDEO_ASSISTANT_REFEREE_OVER, CANCELED_VIDEO_ASSISTANT_REFEREE);

    /**
     * ------------------------------- 点击进球后，禁用相关可能事件按钮------------------------------------
     */
    public static final List<PDEventCodeEnum> DISABLE_MIDDLE_POSSIBLE_BUTTON = Arrays.asList(POSSIBLE_VAR_PENALTY, POSSIBLE_VAR_GOAL, POSSIBLE_VAR_RED_CARD);

    /**
     * ------------------------------- 点击进球后，禁用相关确认事件按钮------------------------------------
     */
    public static final List<PDEventCodeEnum> DISABLE_MIDDLE_CONFIRM_BUTTON = Arrays.asList(SHOT_ON_TARGET, SHOT_OFF_TARGET, OFFSIDE, GOAL_KICK,
            THROW_IN, ATTACK, DANGEROUS_ATTACK, INJURY, SUBSTITUTION, POSSESSION_COUNT);

    /**
     * ------------------------------- 计时事件: 主客队事件------------------------------------
     */
    public static final List<PDEventCodeEnum> HOME_AWAY_EVENT = Arrays.asList(POSSIBLE_RED_CARD, POSSIBLE_YELLOW_CARD, POSSIBLE_CORNER,
            POSSIBLE_PENALTY, POSSIBLE_GOAL, DANGEROUS_ATTACK, YELLOW_CARD, RED_CARD, CORNER, CANCELED_YELLOW_CARD, CANCELED_RED_CARD,
            CANCELED_CORNER, CANCELED_GOAL, CANCELED_PENALTY, PENALTY_MISSED, KICK_OFF, KICK_OFF_TEAM, ATTACK, YELLOW_RED_CARD, THROW_IN,
            POSSESSION_COUNT, GOAL_KICK, POSSIBLE_FREE_KICK, FREE_KICK, CANCELED_GOAL, OFFSIDE, SHOT_ON_TARGET, SHOT_OFF_TARGET, SUBSTITUTION,
            PENALTY, PENALTY_GOAL, PENALTY, PENALTY_CANCELED, RETAKE_PEN);

    /**
     * ------------------------------- 计时事件: 公共事件1------------------------------------
     */
    public static final List<PDEventCodeEnum> PUBLIC_EVENT_ONE = Arrays.asList(
            GOAL, INJURY, POSSIBLE_VIDEO_ASSISTANT_REFEREE, VIDEO_ASSISTANT_REFEREE_OVER, CANCELED_VIDEO_ASSISTANT_REFEREE,
            POSSIBLE_VAR_RED_CARD, POSSIBLE_VAR_GOAL, POSSIBLE_VAR_PENALTY, VAR_RED_CARD, VAR_GOAL, VAR_PENALTY, VAR_YELLOW_CARD,
            CANCELED_VAR_RED_CARD, CANCELED_VAR_GOAL, CANCELED_VAR_PENALTY);

    /**
     * ------------------------------- 计时事件: 公共事件2------------------------------------
     */
    public static final List<PDEventCodeEnum> PUBLIC_EVENT_TWO = Arrays.asList(BALL_SAFE, VAR_REASON, WATER_BREAK, INJURY_TIME, CANCELED_FREE_KICK);

    /**------------------------------- 无删除操作的事件------------------------------------*/
    public static final List<PDEventCodeEnum> NO_DEL_OPERATE_EVENT_CODES = Arrays.asList(DANGEROUS_ATTACK,ATTACK);

    /**------------------------------- total event codes based on type------------------------------------*/
    public static final List<PDEventCodeEnum> confirmEventCodes = Arrays.asList(GOAL, YELLOW_CARD, RED_CARD, CORNER, YELLOW_RED_CARD, THROW_IN, ATTACK, POSSESSION,
            GOAL_KICK, FREE_KICK, OFFSIDE, SHOT_ON_TARGET, SHOT_OFF_TARGET, DANGEROUS_ATTACK, PENALTY, PENALTY_GOAL, VAR_RED_CARD, VAR_GOAL, VAR_PENALTY, VAR_YELLOW_CARD);

    /**------------------------------- regular event codes ------------------------------------*/
    public static final List<PDEventCodeEnum> confirmRegularEventCodes = Arrays.asList(GOAL, YELLOW_CARD, RED_CARD, CORNER, YELLOW_RED_CARD, THROW_IN, ATTACK, POSSESSION,
            GOAL_KICK, FREE_KICK, OFFSIDE, SHOT_ON_TARGET, SHOT_OFF_TARGET, DANGEROUS_ATTACK, PENALTY, PENALTY_GOAL);

    /**------------------------------- var event codes ------------------------------------*/
    public static final List<PDEventCodeEnum> confirmVAREventCodes = Arrays.asList(VAR_RED_CARD, VAR_GOAL, VAR_PENALTY, VAR_YELLOW_CARD, PENALTY_GOAL);
    public static final List<PDEventCodeEnum> canceledVAREventCodes = Arrays.asList(CANCELED_VAR_RED_CARD, CANCELED_VAR_GOAL, CANCELED_VAR_PENALTY, PENALTY_MISSED, PENALTY_CANCELED);
    public static final List<PDEventCodeEnum> possibleVAREventCodes = Arrays.asList(POSSIBLE_VAR_RED_CARD, POSSIBLE_VAR_GOAL, POSSIBLE_VAR_PENALTY);
    PDEventCodeEnum(String eventCode, String eventName) {
        this.eventCode = eventCode;
        this.eventName = eventName;
    }

    public static PDEventCodeEnum getEventCodeEnum(String eventCode) {
        for (PDEventCodeEnum PDEventCodeEnum : PDEventCodeEnum.values()) {
            if (PDEventCodeEnum.getEventCode().equals(eventCode)) {
                return PDEventCodeEnum;
            }
        }
        return null;
    }

    /**
     * 点击进球后，启用VAR按钮
     *
     * @param eventCode 事件编码
     * @return 结果
     */
    public static boolean containVarEvent(String eventCode) {
        for (PDEventCodeEnum pdEventCodeEnum : ENABLE_VAR_BUTTON) {
            if (pdEventCodeEnum.getEventCode().equals(eventCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 点击进球后，禁用相关可能事件按钮
     *
     * @param eventCode 事件编码
     * @return 结果
     */
    public static boolean containDisablePossibleEvent(String eventCode) {
        for (PDEventCodeEnum pdEventCodeEnum : DISABLE_MIDDLE_POSSIBLE_BUTTON) {
            if (pdEventCodeEnum.getEventCode().equals(eventCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 点击进球后，禁用相关按钮
     *
     * @param eventCode 事件编码
     * @return 结果
     */
    public static boolean containDisableConfirmEvent(String eventCode) {
        for (PDEventCodeEnum pdEventCodeEnum : DISABLE_MIDDLE_CONFIRM_BUTTON) {
            if (pdEventCodeEnum.getEventCode().equals(eventCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计时事件: 包含主客队事件
     *
     * @param eventCode 事件编码
     * @return 判断结果
     */
    public static boolean containHomeAwayEvent(String eventCode) {
        for (PDEventCodeEnum pdEventCodeEnum : HOME_AWAY_EVENT) {
            if (pdEventCodeEnum.getEventCode().equals(eventCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计时事件: 包含公共事件1
     *
     * @param eventCode 事件编码
     * @return 判断结果
     */
    public static boolean containPublicEventOne(String eventCode) {
        for (PDEventCodeEnum pdEventCodeEnum : PUBLIC_EVENT_ONE) {
            if (pdEventCodeEnum.getEventCode().equals(eventCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计时事件: 包含公共事件2
     *
     * @param eventCode 事件编码
     * @return 判断结果
     */
    public static boolean containPublicEventTwo(String eventCode) {
        for (PDEventCodeEnum pdEventCodeEnum : PUBLIC_EVENT_TWO) {
            if (pdEventCodeEnum.getEventCode().equals(eventCode)) {
                return true;
            }
        }
        return false;
    }

    public static boolean getNoDelOperateEventCode(String eventCode) {
        for (PDEventCodeEnum PDEventCodeEnum : NO_DEL_OPERATE_EVENT_CODES) {
            if (PDEventCodeEnum.getEventCode().equals(eventCode)) {
                return false;
            }
        }
        return true;
    }

    /**------------------------------- regular event codes ------------------------------------*/
    public static boolean containConfirmRegularEvent(String eventCode) {
        for (PDEventCodeEnum PDEventCodeEnum : confirmRegularEventCodes) {
            if (PDEventCodeEnum.getEventCode().equals(eventCode)) {
                return true;
            }
        }
        return false;
    }

    /**------------------------------- var event codes ------------------------------------*/
    public static boolean containVAREvent(String eventCode, String type) {
        switch (type) {
            case CommonConstant.PD_EVENT_TYPE_CONFIRM:
                for (PDEventCodeEnum PDEventCodeEnum : confirmVAREventCodes) {
                    if (PDEventCodeEnum.getEventCode().equals(eventCode)) {
                        return true;
                    }
                }
                break;
            case CommonConstant.PD_EVENT_TYPE_CANCEL:
                for (PDEventCodeEnum PDEventCodeEnum : canceledVAREventCodes) {
                    if (PDEventCodeEnum.getEventCode().equals(eventCode)) {
                        return true;
                    }
                }
                break;
            case CommonConstant.PD_EVENT_TYPE_POSSIBLE:
                for (PDEventCodeEnum PDEventCodeEnum : possibleVAREventCodes) {
                    if (PDEventCodeEnum.getEventCode().equals(eventCode)) {
                        return true;
                    }
                }
                break;

        }
        return false;
    }
}
