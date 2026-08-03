package com.panda.merge.common.enums;

import com.alibaba.fastjson.JSONObject;

/**
 * 盘口封盘提示语
 */
public enum MarketTipsLanguageEnum {
    CHAMPION_HANDICAP_STATUS("CHAMPION_HANDICAP_STATUS", "冠军操盘，盘口状态为：[X]", "Outright, market status : [X]"),
    ODDS_ARE_NOT_LEGAL("ODDS_ARE_NOT_LEGAL", "计算出的赔率不合法，封盘处理", "Paused, due to incorrect odds after calculating"),
    INSERT_PRE_TRADER("INSERT_PRE_TRADER", "综合球种未知玩法直接封盘", "Other sports unknown market paused"),
    COMPREHENSIVE_BALL_SPECIES_UNKNOWN("COMPREHENSIVE_BALL_SPECIES_UNKNOWN", "综合球种未配置最大最小赔率，直接封盘", "Other sports market paused, due to no configured max min odds"),
    ODDS_CANNOT_BE_NEGATIVE("ODDS_CANNOT_BE_NEGATIVE", "赔率值不能都为负数", "Odds cannot be negative for all"),
    MULTIPLE_DISCS_DO_NOT_MEET_THE_RULES("MULTIPLE_DISCS_DO_NOT_MEET_THE_RULES", "多项盘 1/(1/o1 + 1/o2 + ... + 1/on) 大于0.99，盘口封盘", "Multi-ways 1/(1/o1 + 1/o2 + … + 1/on) higher than 0.99, market paused"),
    HANDICAP_LOWER("HANDICAP_LOWER", "球头小于[X]，大于[Y]，盘口封盘", "Exceed min number of market 0.5，max number of market 21.5 , Market Suspend"),
    HANDICAP_MAX("HANDICAP_MAX", "超过最大球头数额[X]，盘口关盘", "Exceed max number of market [X], Market Suspend"),
    HANDICAP_MIN("HANDICAP_MIN", "超过最小球头数额[X]，盘口关盘", "Exceed min number of market [X], Market Suspend"),
    BALL_HEAD_DOES_NOT_MEET_THE_RULES("BALL_HEAD_DOES_NOT_MEET_THE_RULES", "此类玩法球头不满足要求,Odd2.5(1.75) 规则，盘口封盘", "Market paused, due to handicap not fullfilled requirement, Odd2.5 (1.75) rule"),
    ONLY_ONE_BET("ONLY_ONE_BET", "只有一个投注项，盘口封盘", "Market paused, due to 1 bet selection exists only"),
    A_CLOSURE("A_CLOSURE", "对两项盘有一项封盘的都做成了盘口级别关盘", "2-ways market DEACTIVATED, due to one of bet selection DEACTIVATED"),
    ILLEGAL_BET_ODDS("ILLEGAL_BET_ODDS", "投注项赔率不合法，超过最大赔率[X]，盘口封盘", "Market paused, due to bet selection odds incorrect, over max odds [X]"),
    ILLEGAL_BET_ODDS_MIN("ILLEGAL_BET_ODDS", "投注项赔率不合法，超过最小赔率[X]，盘口封盘", "Market paused, due to bet selection odds incorrect, over min odds [X]"),
    ODDS_NOT_UPDATED("ODDS_NOT_UPDATED", "玩法数据源超过一分钟未更新赔率", "Odds feed not updated for more than 1 minute"),
    BALL_HEAD_NOT_SATISFIED("BALL_HEAD_NOT_SATISFIED", "此类玩法球头不满足要求,< 0.5，盘口封盘", "Market paused, due to handicap not fullfilled requirement, < 0.5"),
    PLAYER_NOT_FOUND("PLAYER_NOT_FOUND", "篮球三方球员源id：[X],未找到标准球员，盘口关盘", "Market closed, due to 3rd party provider basketball player ID not meets with player ID：[X]."),
    THE_ODDS_ARE_NOT_SATISFIED("THE_ODDS_ARE_NOT_SATISFIED", "经过计算之后的赔率不满足 Odd0.5（2.05）>Odd1.5（1.90）>Odd2.5(1.75) 规则，盘口封盘", "Market paused, due to odds not fulfilled Odd 0.5 (2.05) > Odd 1.5 (1.90) > Odd 2.5(1.75) rule after calculating"),
    EXCEEDS_MAX_AND_MIN_ODDS("EXCEEDS_MAX_AND_MIN_ODDS", "投注项赔率不合法，超过最大最小赔率[X],[Y]，盘口关盘", "Market paused, due to bet selection odds incorrect, over max odds [X],[Y]"),
    MARCH_STATUS("MARCH_STATUS", "赛事级操盘状态为：[X]，盘口状态发生变化", "Match trading status : [X], market status changed"),
    PLACE_STATUS("PLACE_STATUS", "操盘盘口位置状态为：[X]，盘口状态发生变化", "Market position status : [X], market status changed"),
    CATEGORY_SET_STATUS("CATEGORY_SET_STATUS", "操盘玩法集状态：[X]，盘口状态发生变化", "Market group status : [X], market status changed"),
    PLAY_STATUS_CLOSE("PLAY_STATUS_CLOSE", "操盘单球种玩法状态为：[X]，盘口状态发生变化", "Market status : [x], market status changed"),
    ;

    private String code;
    private String zs;
    private String en;

    MarketTipsLanguageEnum(String code, String zs, String en) {
        this.code = code;
        this.zs = zs;
        this.en = en;
    }

    public String getCode() {
        return en;
    }

    public String getZs() {
        return zs;
    }

    public String getEn() {
        return en;
    }

    public static void main(String[] args) {
        System.out.println(MarketTipsLanguageEnum.getEnum(MarketTipsLanguageEnum.HANDICAP_LOWER.getCode(), "12.5"));
    }

    public static String getEnum(String code, String... param) {
        JSONObject obj = new JSONObject();
        String zsStr = "";
        String enStr = "";
        for (MarketTipsLanguageEnum marketTipsLanguageEnum : MarketTipsLanguageEnum.values()) {
            if (marketTipsLanguageEnum.getCode().equals(code)) {
                String zs = marketTipsLanguageEnum.getZs();
                String en = marketTipsLanguageEnum.getEn();
                obj.put("zs", zs);
                obj.put("en", en);
                if (null == param) {
                    return obj.toJSONString();
                } else if (param.length == 1) {
                    zsStr = zs.replace("[X]", param[0]);
                    enStr = en.replace("[X]", param[0]);
                } else if (param.length == 2) {
                    zsStr = zs.replace("[X]", param[0]).replace("[Y]", param[1]);
                    enStr = en.replace("[X]", param[0]).replace("[Y]", param[1]);
                }
                obj.put("zs", zsStr);
                obj.put("en", enStr);
                return obj.toJSONString();
            }
        }
        obj.put("zs", zsStr);
        obj.put("en", enStr);
        return obj.toJSONString();
    }

}
