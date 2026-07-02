package com.panda.merge.constant;

import lombok.Getter;

@Getter
public enum SettleSyncEnum {

    FOOTBALL_SYNC_SCORE(0, "football_sync_score", "足球比分同步"),
    ;

    private Integer score;

    private String value;

    private String desc;

    SettleSyncEnum(Integer score, String value, String desc) {
        this.score = score;
        this.value = value;
        this.desc = desc;
    }


}
