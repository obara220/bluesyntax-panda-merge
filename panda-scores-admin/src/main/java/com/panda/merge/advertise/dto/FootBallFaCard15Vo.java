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
public class FootBallFaCard15Vo implements Serializable {
    /**
     * 0-15 分钟
     * */
    private CommonItem faCard15;

    /**
     * 15-30 分钟
     * */
    private CommonItem faCard30;

    /**
     * 30-45 分钟
     * */
    private CommonItem faCard45;

    /**
     * 45-60 分钟
     * */
    private CommonItem faCard60;

    /**
     * 60-75 分钟
     * */
    private CommonItem faCard75;

    /**
     * 75-90 分钟
     * */
    private CommonItem faCard90;

    public FootBallFaCard15Vo() {
    }
    
    public FootBallFaCard15Vo(MatchScoresInfo matchScoresInfo) {
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJson())){
            return;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        
        FootballScores score60899 = allPeriodScores.get(60899L);
        if( null != score60899 && null != score60899.getFaCard() ){
            this.faCard15 = score60899.getFaCard();
        }
        
        FootballScores score61799 = allPeriodScores.get(61799L);
        if( null != score61799 && null != score61799.getFaCard() )
        {
            this.faCard30 = score61799.getFaCard();
        }


        FootballScores score62699 = allPeriodScores.get(62699L);
        if( null != score62699 && null != score62699.getFaCard() ){
            this.faCard45 = score62699.getFaCard();
        }


        FootballScores score73599 = allPeriodScores.get(73599L);
        if( null != score73599 && null != score73599.getFaCard() ){
            this.faCard60 = score73599.getFaCard();
        }


        FootballScores score74499 = allPeriodScores.get(74499L);
        if( null != score74499 && null != score74499.getFaCard() ){
            this.faCard75 = score74499.getFaCard();
        }


        FootballScores score75399 = allPeriodScores.get(75399L);
        if( null != score75399 && null != score75399.getFaCard() ){
            this.faCard90 = score75399.getFaCard();
        }

    }
}
