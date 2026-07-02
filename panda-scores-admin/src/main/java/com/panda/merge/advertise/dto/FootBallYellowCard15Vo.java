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
public class FootBallYellowCard15Vo implements Serializable {
    /**
     * 0-15 分钟
     * */
    private CommonItem yellowCard15;

    /**
     * 15-30 分钟
     * */
    private CommonItem yellowCard30;

    /**
     * 30-45 分钟
     * */
    private CommonItem yellowCard45;

    /**
     * 45-60 分钟
     * */
    private CommonItem yellowCard60;

    /**
     * 60-75 分钟
     * */
    private CommonItem yellowCard75;

    /**
     * 75-90 分钟
     * */
    private CommonItem yellowCard90;

    public FootBallYellowCard15Vo() {
    }


    public FootBallYellowCard15Vo(MatchScoresInfo matchScoresInfo) {
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            return;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores score60899=allPeriodScores.get(60899L);

        if(score60899!=null && null != score60899.getYellowCard() ){
            this.yellowCard15 = score60899.getYellowCard();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.yellowCard15 = commonItem;
//        }

        FootballScores score61799 = allPeriodScores.get(61799L);
        if(score61799!=null && null != score61799.getYellowCard() )
        {
            this.yellowCard30 = score61799.getYellowCard();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.yellowCard30 = commonItem;
//        }

        FootballScores score62699 = allPeriodScores.get(62699L);
        if(score62699!=null){
            this.yellowCard45 = score62699.getYellowCard();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.yellowCard45 = commonItem;
//        }

        FootballScores score73599 = allPeriodScores.get(73599L);
        if(score73599!=null){
            this.yellowCard60 = score73599.getYellowCard();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.yellowCard60 = commonItem;
//        }

        FootballScores score74499 = allPeriodScores.get(74499L);
        if(score74499!=null){
            this.yellowCard75 = score74499.getYellowCard();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.yellowCard75 = commonItem;
//        }

        FootballScores score75399 = allPeriodScores.get(75399L);
        if(score75399!=null){
            this.yellowCard90 = score75399.getYellowCard();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.yellowCard90 = commonItem;
//        }

    }
}
