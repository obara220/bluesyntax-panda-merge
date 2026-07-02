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
public class FootBallRedCard15Vo implements Serializable {
    /**
     * 0-15 分钟
     * */
    private CommonItem redCard15;

    /**
     * 15-30 分钟
     * */
    private CommonItem redCard30;

    /**
     * 30-45 分钟
     * */
    private CommonItem redCard45;

    /**
     * 45-60 分钟
     * */
    private CommonItem redCard60;

    /**
     * 60-75 分钟
     * */
    private CommonItem redCard75;

    /**
     * 75-90 分钟
     * */
    private CommonItem redCard90;

    public FootBallRedCard15Vo() {
    }


    public FootBallRedCard15Vo(MatchScoresInfo matchScoresInfo) {
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            return;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores = JsonMapUtils.parseFootballMap(periodFootballScores);

        FootballScores score60899 = allPeriodScores.get(60899L);
        if(score60899!=null){
            this.redCard15=score60899.getRedCard();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.redCard15 = commonItem;
//        }

        FootballScores score61799 = allPeriodScores.get(61799L);
        if(score61799!=null){
            this.redCard30=score61799.getRedCard();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.redCard30 = commonItem;
//        }

        FootballScores score62699 = allPeriodScores.get(62699L);
        if(score62699!=null){
            this.redCard45=score62699.getRedCard();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.redCard45 = commonItem;
//        }

        FootballScores score73599 = allPeriodScores.get(73599L);
        if(score73599!=null){
            this.redCard60 = score73599.getRedCard();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.redCard60 = commonItem;
//        }

        FootballScores score74499 = allPeriodScores.get(74499L);
        if(score74499!=null){
            this.redCard75 = score74499.getRedCard();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.redCard75 = commonItem;
//        }

        FootballScores score75399 = allPeriodScores.get(75399L);
        if(score75399!=null){
            this.redCard90 = score75399.getRedCard();
        }
//        else
//        {
//            CommonItem commonItem = new CommonItem();
//            commonItem.setAway(0);
//            commonItem.setHome(0);
//            this.redCard90 = commonItem;
//        }

    }
}
