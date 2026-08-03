package com.panda.merge.constant;

import lombok.Getter;

@Getter
public enum SnookerEventTypeEnum {
    KICK_OFF("which_team_serves_first", "开球", null, Boolean.FALSE, ""),
    WINNING_SCORE_RED("snooker_score_red", "红球赢分",1, Boolean.FALSE, ""),
    WINNING_SCORE_YELLOW("snooker_score_yellow", "黄球赢分",2, Boolean.FALSE, ""),
    WINNING_SCORE_GREEN("snooker_score_green", "绿球赢分",3, Boolean.FALSE, ""),
    WINNING_SCORE_BROWN("snooker_score_brown", "棕球赢分",4, Boolean.FALSE, ""),
    WINNING_SCORE_BLUE("snooker_score_blue", "蓝球赢分",5, Boolean.FALSE, ""),
    WINNING_SCORE_PINK("snooker_score_pink", "粉球赢分",6, Boolean.FALSE, ""),
    WINNING_SCORE_BLACK("snooker_score_black", "黑球赢分",7, Boolean.FALSE, ""),
    FOUL_SCORE_RED("snooker_foul_red", "红球罚分",4, Boolean.TRUE, ""),
    FOUL_SCORE_YELLOW("snooker_foul_yellow", "黄球罚分",4, Boolean.TRUE, ""),
    FOUL_SCORE_GREEN("snooker_foul_green", "绿球罚分",4, Boolean.TRUE, ""),
    FOUL_SCORE_BROWN("snooker_foul_brown", "棕球罚分",4, Boolean.TRUE, ""),
    FOUL_SCORE_BLUE("snooker_foul_blue", "蓝球罚分",5, Boolean.TRUE, ""),
    FOUL_SCORE_PINK("snooker_foul_pink", "粉球罚分",6, Boolean.TRUE, ""),
    FOUL_SCORE_BLACK("snooker_foul_black", "黑球罚分",7, Boolean.TRUE, ""),
    PENALTY_FOUR("snooker_foul_4", "犯规罚4",4, Boolean.TRUE, ""),
    PENALTY_SEVEN("snooker_foul_7", "犯规罚7",7, Boolean.TRUE, ""),
    FOUL_LOSE("snooker_foul_lose", "直接判输",null, Boolean.FALSE, ""),
    CONCEDE("snooker_concede", "认输",null, Boolean.FALSE, ""),
    STRIKER("Striker", "正在持杆",null, Boolean.FALSE, ""),
    NO_POT("nopot", "未进球",null, Boolean.FALSE, ""),
    FREE_BALL_POT("free_ball_pot", "自由球进球",1, Boolean.FALSE, ""),
    FREE_BALL_NO_POT("free_ball_nopot", "自由球未进球",null, Boolean.FALSE, ""),
    RERACK("rerack", "重新排球",null, Boolean.FALSE, ""),
    DELETE_EVENT("delete_event", "删除事件", null, Boolean.FALSE, ""),
    ;

    private final String eventCode;
    private final String name;
    private final Integer score;
    private final Boolean opposite;
    private final String engName;


    SnookerEventTypeEnum(String eventCode, String name, Integer score,Boolean opposite, String engName) {
        this.eventCode = eventCode;
        this.name = name;
        this.score = score;
        this.opposite = opposite;
        this.engName = engName;
    }

    public static SnookerEventTypeEnum getByCode(String eventCode) {
        for (SnookerEventTypeEnum value : SnookerEventTypeEnum.values()) {
            if (value.getEventCode().equals(eventCode)) {
                return value;
            }
        }
        return null;
    }
}
