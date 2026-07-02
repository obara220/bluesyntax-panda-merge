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
public class FootBallCornerScore15Vo implements Serializable {
    /**
     * 0-15 分钟
     * */
    private CommonItem corner15;

    /**
     * 15-30 分钟
     * */
    private CommonItem corner30;

    /**
     * 30-45 分钟
     * */
    private CommonItem corner45;

    /**
     * 45-60 分钟
     * */
    private CommonItem corner60;

    /**
     * 60-75 分钟
     * */
    private CommonItem corner75;

    /**
     * 75-90 分钟
     * */
    private CommonItem corner90;

    public FootBallCornerScore15Vo() {
    }


    public FootBallCornerScore15Vo(MatchScoresInfo matchScoresInfo) {
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            return;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);

        FootballScores score60899=allPeriodScores.get(60899L);
        if( null != score60899 && null != score60899.getCorner() ){
            this.corner15 = score60899.getCorner();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.corner15 = commonItem;
//        }

        FootballScores score61799 = allPeriodScores.get(61799L);
        if( null != score61799 && null != score61799.getCorner() ){
            this.corner30 = score61799.getCorner();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.corner30 = commonItem;
//        }

        FootballScores score62699=allPeriodScores.get(62699L);
        if( null != score62699 && null != score62699.getCorner() ){
            this.corner45 = score62699.getCorner();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.corner45 = commonItem;
//        }

        FootballScores score73599=allPeriodScores.get(73599L);
        if( null != score73599 && null != score73599.getCorner()  ){
            this.corner60 = score73599.getCorner();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.corner60 = commonItem;
//        }

        FootballScores score74499=allPeriodScores.get(74499L);
        if( null != score74499 && null != score74499.getCorner() ){
            this.corner75 = score74499.getCorner();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.corner75 = commonItem;
//        }

        FootballScores score75399=allPeriodScores.get(75399L);
        if( null != score75399 && null != score75399.getCorner() ){
            this.corner90 = score75399.getCorner();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.corner90 = commonItem;
//        }
    }
}
