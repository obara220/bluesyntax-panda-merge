package com.panda.merge.utils;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.BasketballScores;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.dto.HandballScores;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.panda.merge.constant.DataSourceConstant.WHOLE_MATCH;

public  class ScoreBuildUtils {
    public static Map<String, CommonItem> buildMatchScore2ByMap(String sjon){
        if(StringUtils.isEmpty(sjon)){
            return new HashMap<>();
        }
        JSONObject periodFootballScores = JSONObject.parseObject(sjon);
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        //.定义要求结果
        Map<String,CommonItem> matchScore =new HashMap<>();
        //1.求全场数据
        CommonItem whole =new CommonItem();
        //2.求当前半场数据
        CommonItem period =new CommonItem();
        //3.求加时赛数据
        CommonItem overtime =new CommonItem();
        //4.求点球大战数据
        CommonItem penalty =new CommonItem();

        for (Map.Entry<Long, FootballScores> entry : allPeriodScores.entrySet()) {
            if(entry.getKey().equals(6l)||entry.getKey().equals(7l)){
                whole.setHome(whole.getHome()+entry.getValue().getGoal().getHome());
                whole.setAway(whole.getAway()+entry.getValue().getGoal().getAway());
                if(entry.getKey().equals(6L)){
                    period.setHome(entry.getValue().getGoal().getHome());
                    period.setAway(entry.getValue().getGoal().getAway());
                }
            }
            if(entry.getKey().equals(41L)||entry.getKey().equals(42L)){
                overtime.setHome(overtime.getHome()+entry.getValue().getGoal().getHome());
                overtime.setAway(overtime.getAway()+entry.getValue().getGoal().getAway());
                matchScore.put("overtimeScore",overtime);
            }
        }
        matchScore.put("wholeScore",whole);
        matchScore.put("periodScore",period);
        FootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH.longValue());
        if(wholeSores!=null&&allPeriodScores.get(50L)!=null){
            matchScore.put("penaltyShootout",allPeriodScores.get(50L).getGoal());
        }
        return matchScore;
    }

    /**
     * 比分json转map 方便数据组装
     * @param sjon
     * @return
     */
    public  static    Map<String,CommonItem> buildFootballMatchScoreByMap(String sjon){
        if(StringUtils.isEmpty(sjon)){
            return new HashMap<>();
        }
        JSONObject periodFootballScores = JSONObject.parseObject(sjon);
        Map<Long, HandballScores> allPeriodScores= JsonMapUtils.parseHandballMap(periodFootballScores);
        //.定义要求结果
        Map<String,CommonItem> matchScore =new HashMap<>();
        //1.求全场数据
        CommonItem whole =new CommonItem();
        //2.求当前半场数据
        CommonItem period =new CommonItem();
        //3.求加时赛数据
        CommonItem overtime =new CommonItem();
        //4.求点球大战数据
        CommonItem penalty =new CommonItem();
        //5.组装返回string
        Long periodKey= 0l;
        for (Map.Entry<Long, HandballScores> entry : allPeriodScores.entrySet()) {
            if(entry.getKey().equals(6l)||entry.getKey().equals(7l)){
                whole.setHome(whole.getHome()+entry.getValue().getGoal().getHome());
                whole.setAway(whole.getAway()+entry.getValue().getGoal().getAway());
                if(periodKey<entry.getKey()){
                    periodKey=entry.getKey();
                    period.setHome(entry.getValue().getGoal().getHome());
                    period.setAway(entry.getValue().getGoal().getAway());
                }
            }
            if(entry.getKey().equals(41L)||entry.getKey().equals(42L)){
                overtime.setHome(overtime.getHome()+entry.getValue().getGoal().getHome());
                overtime.setAway(overtime.getAway()+entry.getValue().getGoal().getAway());
                matchScore.put("overtimeScore",overtime);
            }
        }
        matchScore.put("wholeScore",whole);
        matchScore.put("periodScore",period);
        HandballScores wholeSores= allPeriodScores.get(WHOLE_MATCH.longValue());
        if(wholeSores!=null&&allPeriodScores.get(50L)!=null){
            matchScore.put("penaltyShootout",allPeriodScores.get(50L).getGoal());
        }
        return matchScore;
    }

    public static   Map<String, CommonItem> buildBasketballMatchScoreByMap(String sjon){
        if(StringUtils.isEmpty(sjon)){
            return new HashMap<>();
        }
        JSONObject periodFootballScores = JSONObject.parseObject(sjon);
        Map<Long, BasketballScores> allPeriodScores= JsonMapUtils.parseBasketballMap(periodFootballScores);
        //.定义要求结果
        Map<String,CommonItem> matchScore =new HashMap<>();
        //1.半场比分计算
        CommonItem period =new CommonItem();
        //1.赛制判断
        for (Map.Entry<Long, BasketballScores> entry : allPeriodScores.entrySet()) {
            if(entry.getKey().equals(1l)||entry.getKey().equals(13L)||entry.getKey().equals(14L)){
                period.setHome(period.getHome()+entry.getValue().getMatchScore().getHome());
                period.setAway(period.getAway()+entry.getValue().getMatchScore().getAway());
            }
        }
        BasketballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        matchScore.put("periodScore",period);
        matchScore.put("wholeScore",wholeSores.getMatchScore());
        return matchScore;
    }
}
