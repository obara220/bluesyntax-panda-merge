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
public class FootBallGoalScore5Vo implements Serializable {
    /**
     * 0-5 分钟
     * */
    private CommonItem goal5;

    /**
     * 5-10 分钟
     * */
    private CommonItem goal10;

    /**
     * 10-15 分钟
     * */
    private CommonItem goal15;

    /**
     * 15-20 分钟
     * */
    private CommonItem goal20;

    /**
     * 20-25 分钟
     * */
    private CommonItem goal25;

    /**
     * 25-30 分钟
     * */
    private CommonItem goal30;
    /**
     * 30-35 分钟
     * */
    private CommonItem goal35;

    /**
     * 35-40 分钟
     * */
    private CommonItem goal40;
    /**
     * 40-45 分钟
     * */
    private CommonItem goal45;

    /**
     * 上半场绝杀
     * */
    private CommonItem goal49;
    /**
     * 45-50 分钟
     * */
    private CommonItem goal50;

    /**
     * 50-55 分钟
     * */
    private CommonItem goal55;

    /**
     * 55-60 分钟
     * */
    private CommonItem goal60;

    /**
     * 60-65 分钟
     * */
    private CommonItem goal65;

    /**
     * 65-70 分钟
     * */
    private CommonItem goal70;

    /**
     * 70-75 分钟
     * */
    private CommonItem goal75;
    /**
     * 75-80 分钟
     * */
    private CommonItem goal80;

    /**
     * 80-85 分钟
     * */
    private CommonItem goal85;
    /**
     * 85-90 分钟
     * */
    private CommonItem goal90;

    /**
     * 下半场绝杀
     * */
    private CommonItem goal99;

    public FootBallGoalScore5Vo() {
    }


    public FootBallGoalScore5Vo(MatchScoresInfo matchScoresInfo) {
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            return;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores score6005=allPeriodScores.get(6005L);
        if(score6005!=null){
            this.goal5=score6005.getGoal();
        }
        FootballScores score6010=allPeriodScores.get(6010L);
        if(score6010!=null){
            this.goal10=score6010.getGoal();
        }
        FootballScores score6015=allPeriodScores.get(6015L);
        if(score6015!=null){
            this.goal15=score6015.getGoal();
        }
        FootballScores score6020=allPeriodScores.get(6020L);
        if(score6020!=null){
            this.goal20=score6020.getGoal();
        }
        FootballScores score6025=allPeriodScores.get(6025L);
        if(score6025!=null){
            this.goal25=score6025.getGoal();
        }
        FootballScores score6030=allPeriodScores.get(6030L);
        if(score6030!=null){
            this.goal30=score6030.getGoal();
        }
        FootballScores score6035=allPeriodScores.get(6035L);
        if(score6035!=null){
            this.goal35=score6035.getGoal();
        }
        FootballScores score6040=allPeriodScores.get(6040L);
        if(score6040!=null){
            this.goal40=score6040.getGoal();
        }
        FootballScores score6045=allPeriodScores.get(6045L);
        if(score6045!=null){
            this.goal45=score6045.getGoal();
        }
        FootballScores score6049=allPeriodScores.get(6050L);
        if(score6049!=null){
            this.goal49=score6049.getGoal();
        }

        FootballScores score7050=allPeriodScores.get(7050L);
        if(score7050!=null){
            this.goal50=score7050.getGoal();
        }
        FootballScores score7055=allPeriodScores.get(7055L);
        if(score7055!=null){
            this.goal55=score7055.getGoal();
        }
        FootballScores score7060=allPeriodScores.get(7060L);
        if(score7060!=null){
            this.goal60=score7060.getGoal();
        }
        FootballScores score7065=allPeriodScores.get(7065L);
        if(score7065!=null){
            this.goal65=score7065.getGoal();
        }
        FootballScores score7070=allPeriodScores.get(7070L);
        if(score7070!=null){
            this.goal70=score7070.getGoal();
        }
        FootballScores score7075=allPeriodScores.get(7075L);
        if(score7075!=null){
            this.goal75=score7075.getGoal();
        }
        FootballScores score7080=allPeriodScores.get(7080L);
        if(score7080!=null){
            this.goal80=score7080.getGoal();
        }
        FootballScores score7085=allPeriodScores.get(7085L);
        if(score7085!=null){
            this.goal85=score7085.getGoal();
        }
        FootballScores score7090=allPeriodScores.get(7090L);
        if(score7090!=null){
            this.goal90=score7090.getGoal();
        }
        FootballScores score7095=allPeriodScores.get(7095L);
        if(score7095!=null){
            this.goal99=score7095.getGoal();
        }
    }
}
