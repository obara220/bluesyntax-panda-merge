package com.panda.merge.constant;

import com.panda.merge.dto.CommonThirdScoresDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchLengthConstant {
    public static List<Integer> FOOT_BALL_FULL_SPORT_MATCH_LENGTH=new ArrayList<>();

    static {
        FOOT_BALL_FULL_SPORT_MATCH_LENGTH.add(0);
        FOOT_BALL_FULL_SPORT_MATCH_LENGTH.add(1);
        FOOT_BALL_FULL_SPORT_MATCH_LENGTH.add(9);
        FOOT_BALL_FULL_SPORT_MATCH_LENGTH.add(10);
        FOOT_BALL_FULL_SPORT_MATCH_LENGTH.add(11);
        FOOT_BALL_FULL_SPORT_MATCH_LENGTH.add(46);
    }

    public static Map<Integer,Integer> MATCH_LENGTH_MITNUTES_FOOTBALL_MAP = new HashMap<>();
    static {
        MATCH_LENGTH_MITNUTES_FOOTBALL_MAP.put(0,45);
        MATCH_LENGTH_MITNUTES_FOOTBALL_MAP.put(1,40);
        MATCH_LENGTH_MITNUTES_FOOTBALL_MAP.put(9,35);
        MATCH_LENGTH_MITNUTES_FOOTBALL_MAP.put(10,30);
        MATCH_LENGTH_MITNUTES_FOOTBALL_MAP.put(11,25);
        MATCH_LENGTH_MITNUTES_FOOTBALL_MAP.put(46,45);
    }

    public static boolean isPeriodTimeRight(CommonThirdScoresDto standardScoresDto,Long period){
        Integer matchLength =standardScoresDto.getStandardMatchInfo().getMatchLength();
        Integer matchLenthMinutes = MatchLengthConstant.MATCH_LENGTH_MITNUTES_FOOTBALL_MAP.get(matchLength);
        if (matchLenthMinutes == null) {
            return true;
        }
        //设置150秒误差
        if (period.equals(31l)) {
            if (standardScoresDto.getSecondFromStart() < matchLenthMinutes * 60-150) {
                return false;
            }
        } else if (period.equals(100l)) {
            if (standardScoresDto.getSecondFromStart() < matchLenthMinutes * 120-150) {
                return false;
            }
        } else if (period.equals(33l)) {
            if (standardScoresDto.getSecondFromStart() < (matchLenthMinutes * 120 + 15 * 60-150)) {
                return false;
            }
        } else if (period.equals(110l)) {
            if (standardScoresDto.getSecondFromStart() < (matchLenthMinutes * 120 + 30 * 60-150)) {
                return false;
            }
        }
        return true;
    }
}

