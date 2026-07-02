package com.panda.merge.mq.build;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.constant.SportPeriodConstant;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.utils.JsonMapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static com.panda.merge.constant.SportPeriodConstant.SportPeriod.WHOLE_MATCH;

public class BusinessFootballMessageBuilder implements BusinessMessageBuilder{
//    6L, 7L, 41L, 42L, 50L
    private long period6 =6l;
    private long period7 =7l;
    private long period41 =41L;
    private long period42 =42L;

    /**
     * 构建足球阶段比分下发数据
     * @param json
     * @return
     */
    public  Map<String,Object> buildBusinessMessage(String json){
        if(StringUtils.isEmpty(json)){
            return null;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(json);
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores wholeSores= allPeriodScores.get(WHOLE_MATCH);
        FootballScores penaltySores= allPeriodScores.get(SportPeriodConstant.FootballPeriod.PENALTY_SHOOTOUT);

        Map<String,Object> scores =new HashMap<>();
        //全场比分
        scores.put("match_score",wholeSores.getGoal());
        scores.put("corner_score",wholeSores.getCorner());
        scores.put("yellow_card_score",wholeSores.getYellowCard());
        scores.put("red_card_score",wholeSores.getRedCard());
        scores.put("shot_on_target_score",wholeSores.getShotOn());
        scores.put("shot_off_target_score",wholeSores.getShotOff());
        scores.put("dangerous_attack_score",wholeSores.getDangerousAttack());
        scores.put("attacks_score",wholeSores.getAttack());
        scores.put("penalty_score",wholeSores.getPenaltyAwarded());
        scores.put("free_kick_score",wholeSores.getFreeKickScore());
        scores.put("offsides_score",wholeSores.getOffside());
        scores.put("possession",wholeSores.getPossession());

        //1.计算半场得分
        if(allPeriodScores.get(period6)!=null){
            scores.put("period_score",allPeriodScores.get(period6).getGoal());
        }
        if(allPeriodScores.get(period7)!=null){
            scores.put("period_score",allPeriodScores.get(period6).getGoal());
        }
        //2.计算加时赛得分
        if(allPeriodScores.get(period41)!=null||allPeriodScores.get(period42)!=null){
            CommonItem commonItem=new CommonItem();

            if(allPeriodScores.get(period41)!=null){
                commonItem.setHome(commonItem.getHome()+allPeriodScores.get(period41).getGoal().getHome());
                commonItem.setAway(commonItem.getAway()+allPeriodScores.get(period41).getGoal().getAway());
            }
            if(allPeriodScores.get(period42)!=null){
                commonItem.setHome(commonItem.getHome()+allPeriodScores.get(period42).getGoal().getHome());
                commonItem.setAway(commonItem.getAway()+allPeriodScores.get(period42).getGoal().getAway());
            }
            scores.put("extra_time_score",commonItem);
        }

        if(penaltySores!=null){
            scores.put("penalty_shootout",penaltySores.getGoal());
        }
        return  scores;
    }
}
