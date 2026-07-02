package com.panda.merge.advertise.dto;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.utils.JsonMapUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * 足球报球版详情
 * */
@Data
public class FootBallGoalScore15Vo implements Serializable {
    /**
     * 0-15 分钟
     * */
    private CommonItem goal15;

    /**
     * 15-30 分钟
     * */
    private CommonItem goal30;

    /**
     * 30-45 分钟
     * */
    private CommonItem goal45;

    /**
     * 45-60 分钟
     * */
    private CommonItem goal60;

    /**
     * 60-75 分钟
     * */
    private CommonItem goal75;

    /**
     * 75-90 分钟
     * */
    private CommonItem goal90;

    public FootBallGoalScore15Vo() {
    }

    public FootBallGoalScore15Vo(MatchScoresInfo matchScoresInfo) {
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            return;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores score60899=allPeriodScores.get(60899L);
        if(score60899!=null){
            this.goal15=score60899.getGoal();
        }
        FootballScores score61799=allPeriodScores.get(61799L);
        if(score61799!=null){
            this.goal30=score61799.getGoal();
        }
        FootballScores score62699=allPeriodScores.get(62699L);
        if(score62699!=null){
            this.goal45=score62699.getGoal();
        }
        FootballScores score73599=allPeriodScores.get(73599L);
        if(score73599!=null){
            this.goal60=score73599.getGoal();
        }
        FootballScores score74499=allPeriodScores.get(74499L);
        if(score74499!=null){
            this.goal75=score74499.getGoal();
        }
        FootballScores score75399=allPeriodScores.get(75399L);
        if(score75399!=null){
            this.goal90=score75399.getGoal();
        }
    }
}
