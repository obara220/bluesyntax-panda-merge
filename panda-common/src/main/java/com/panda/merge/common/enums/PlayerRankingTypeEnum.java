package com.panda.merge.common.enums;

import lombok.Getter;

/**
 * 联赛球员排行榜单类型
 * @author tell
 * @since 2020年10月14日19:28:49
 */
@Getter
public enum PlayerRankingTypeEnum {

    /**
     * 足球类： 1 射手榜,24 助攻榜,
     */
    FOOTBALL_TYPE_1(1, "射手榜", true,1L),
    FOOTBALL_TYPE_24(24, "助攻榜", true,1L),
    /**
     * 篮球类：24 助攻榜,59 盖帽榜,60 得分榜,61 篮板榜,62 抢断榜,63 技术犯规榜,64 失误榜,65 投篮次数榜,66 进球次数榜,67 效率榜,69 二分命中数榜,70 二分投球次数榜,71 三分命中数榜,72 三分投中次数榜,73 罚中次数榜,74 罚球次数榜,
     */
    BASKETBALL_TYPE_24(24, "助攻榜", true,2L),
    BASKETBALL_TYPE_59(59, "盖帽榜", false,2L),
    BASKETBALL_TYPE_60(60, "得分榜", true,2L),
    BASKETBALL_TYPE_61(61, "篮板榜", true,2L),
    BASKETBALL_TYPE_62(62, "抢断榜", false,2L),
    BASKETBALL_TYPE_63(63, "技术犯规榜", false,2L),
    BASKETBALL_TYPE_64(64, "失误榜", false,2L),
    BASKETBALL_TYPE_65(65, "投篮次数榜", false,2L),
    BASKETBALL_TYPE_66(66, "进球次数榜", false,2L),
    BASKETBALL_TYPE_67(67, "效率榜", false,2L),
    BASKETBALL_TYPE_69(69, "二分命中数榜", false,2L),
    BASKETBALL_TYPE_70(70, "二分投球次数榜", false,2L),
    BASKETBALL_TYPE_71(71, "三分命中数榜", false,2L),
    BASKETBALL_TYPE_72(72, "三分投中次数榜", false,2L),
    BASKETBALL_TYPE_73(73, "罚中次数榜", false,2L),
    BASKETBALL_TYPE_74(74, "罚球次数榜", false,2L),
    ;

    private Integer code;
    private String msg;
    /** 是否下发*/
    private boolean isValue;
    /** 运动类型*/
    private Long sportId;

    PlayerRankingTypeEnum(Integer code, String msg, Boolean isValue,Long sportId) {
        this.code = code;
        this.msg = msg;
        this.isValue = isValue;
        this.sportId = sportId;
    }
}
