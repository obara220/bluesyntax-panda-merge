package com.panda.merge.common.enums;

import lombok.Getter;
import lombok.Setter;

public enum BasketBallSettleNumEnum {

    /**
     * 篮球2.0玩法阶段中英文
     */
    BK_1HT("bk_1ht", "上半场", "1st Half"),
    BK_2HT("bk_2ht", "下半场阶段", "2nd Half "),
    BK_2HT_OT("bk_2htet", "下半场(含加时)", "2nd Half (included Overtime)"),
    BK_ET("bk_et", "加时赛", "Extra Time"),
    BK_FT_ET("bk_ft_et", "全场比分", "Full Time (included Overtime)"),
    BK_FT_RG("bk_ft_rg", "全场(常规赛)", "Full Time (Regular)"),
    BK_Q101("bk_q101", "第1节首先获得10分", "1th Quarter - Race to 10 Points"),
    BK_Q102("bk_q102", "第1节首先获得20分", "1th Quarter - Race to 20 Points"),
    BK_Q103("bk_q103", "第1节首先获得30分", "1th Quarter - Race to 30 Points"),
    BK_Q104("bk_q104", "第1节", "1st Quarter"),
    BK_Q201("bk_q201", "第2节首先获得10分", "2th Quarter - Race to 10 Points"),
    BK_Q202("bk_q202", "第2节首先获得20分", "2th Quarter - Race to 20 Points"),
    BK_Q203("bk_q203", "第2节首先获得30分", "2th Quarter - Race to 30 Points"),
    BK_Q204("bk_q204", "第2节", "2nd Quarter"),
    BK_Q301("bk_q301", "第3节首先获得10分", "3th Quarter - Race to 10 Points"),
    BK_Q302("bk_q302", "第3节首先获得20分", "3th Quarter - Race to 20 Points"),
    BK_Q303("bk_q303", "第3节首先获得30分", "3th Quarter - Race to 30 Points"),
    BK_Q304("bk_q304", "第3节", "3rd Quarter"),
    BK_Q401("bk_q401", "第4节首先获得10分", "4th Quarter - Race to 10 Points"),
    BK_Q402("bk_q402", "第4节首先获得20分", "4th Quarter - Race to 20 Points"),
    BK_Q403("bk_q403", "第4节首先获得30分", "4th Quarter - Race to 30 Points"),
    BK_Q404("bk_q404", "第4节", "4th Quarter"),
    BK_1ST_10("bk_1st_10","首先获得 10 分","Race To 10 Points"),
    BK_1ST_20("bk_1st_20","首先获得 20 分","Race To 20 Points"),
    BK_1ST_30("bk_1st_30","首先获得 30 分","Race To 30 Points"),
    BK_1ST_40("bk_1st_40","首先获得 40 分","Race To 40 Points"),
    BK_1ST_50("bk_1st_50","首先获得 50 分","Race To 50 Points"),
    BK_1ST_60("bk_1st_60","首先获得 60 分","Race To 60 Points"),
    BK_1ST_70("bk_1st_70","首先获得 70 分","Race To 70 Points"),
    BK_1ST_80("bk_1st_80","首先获得 80 分","Race To 80 Points"),
    BK_1ST_90("bk_1st_90","首先获得 90 分","Race To 90 Points"),
    BK_1ST_100("bk_1st_100","首先获得 100 分","Race To 100 Points"),
    BK_1ST_110("bk_1st_110","首先获得 110 分","Race To 110 Points"),
    BK_1ST_120("bk_1st_120","首先获得 120 分","Race To 120 Points"),
    BK_1ST_130("bk_1st_130","首先获得 130 分","Race To 130 Points"),
    BK_1ST_140("bk_1st_140","首先获得 140 分","Race To 140 Points"),
    BK_1ST_150("bk_1st_150","首先获得 150 分","Race To 150 Points"),
    BK_SN("BK_SN","首先获得 N 分","Race To N Points"),
    BK_POINT("bk_plyr_point","球员得分","Player Points"),
    BK_3PT("bk_plyr_3pt","球员三分球","Player 3-Point Field Goals"),
    BK_AST("bk_plyr_ast","球员助攻","Total Assists"),
    BK_RBD("bk_plyr_rbd","球员篮板","Total Rebound"),
    BK_END("bk_end", "比赛结束", "Match Ended"),
    //即使结算
    BK_IN_Q01("bk_in_q01","第一节即时","1st Instant"),
    BK_IN_Q02("bk_in_q02","第二节即时","2th Instant"),
    BK_IN_Q03("bk_in_q03","第三节即时","3th Instant"),
    BK_IN_Q04("bk_in_q04", "第4节即时", "4th Instant"),

    BK_IN_1HT("bk_in_1ht","上半场即时","1ht Instant"),
    BK_IN_2HT("bk_in_2ht","下半场即时","2ht Instant"),
    BK_IN_2HT_OT("bk_in_2htet","下半场即时(含加时)","2ht(included Overtime) Instant"),
    BK_IN_RG("bk_in_rg","全场(常规赛)","ft Instant"),
    BK_IN_ET("bk_in_et", "全场即时", "ft(included Overtime) Instant"),

    BK_IN_ALL("bk_in_all", "即时&阶段", "In-play & Period"),
    BK_WHO_XX0("bk_all_xx0", "首先获得N分", "Race To N Points"),

    BK_Q1041("bk_q1041","第一节12:00-06:01","Q1 12:00-06:01"),
    BK_Q1042("bk_q1042","第一节06:00-00:00","Q1 06:00 -00:00"),

    BK_Q2041("bk_q2041","第二节12:00-06:01","Q2 12:00-06:01"),
    BK_Q2042("bk_q2042","第二节06:00-00:00","Q2 06:00 -00:00"),

    BK_Q3041("bk_q3041","第三节12:00-06:01","Q3 12:00-06:01"),
    BK_Q3042("bk_q3042","第三节06:00-00:00","Q3 06:00 -00:00"),

    BK_Q4041("bk_q4041","第四节12:00-06:01","Q4 12:00-06:01"),
    BK_Q4042("bk_q4042","第四节06:00-00:00","Q4 06:00 -00:00"),

    BK_401("bk_401","首个进球队伍","First Team To Score"),
    BK_403("bk_403","最后进球队伍","Last Team To Score"),
    ;

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String value;

    @Getter
    @Setter
    private String name;

    BasketBallSettleNumEnum(String code, String value, String name) {
        this.code = code;
        this.value = value;
        this.name = name;
    }

    public static BasketBallSettleNumEnum getEnum(String code) {
        for (BasketBallSettleNumEnum basketBallSettleNumEnum : BasketBallSettleNumEnum.values()) {
            if (basketBallSettleNumEnum.getCode().equals(code)) {
                return basketBallSettleNumEnum;
            }
        }
        return null;
    }

}
