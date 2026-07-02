package com.panda.merge.advertise.dto;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.FootballPenaltyScores;
import com.panda.merge.dto.FootballScores;
import com.panda.merge.model.MatchScoresInfo;
import com.panda.merge.utils.JsonMapUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 足球报球版详情
 * */
@Data
public class FootBallPenaltyScoreVo implements Serializable {

    /**
     * 当前点球大战局数
     * */
    private Integer firstNum;
    /**
     * 主客队射门次数
     * */
    private Integer pointNum;
    /**
     *每局比分
     **/
    private Map<String, CommonItem> roundScores;
    /**
     * 前五轮比分
     * */
    private CommonItem round5Scores;
    /**
     * 谁先射门
     * */
    private String shootFirst;

    /**
     * 当前点球开始
     */
    private List<Map<String, Object>> roundPenaltyKick;

    /**
     * 点球大战总比分
     * */
    private CommonItem allRoundScores;

    public FootBallPenaltyScoreVo( ) {
    }
    public FootBallPenaltyScoreVo(MatchScoresInfo matchScoresInfo) {
        if(StringUtils.isEmpty(matchScoresInfo.getScoresJsonExtra())){
            return;
        }
        JSONObject periodFootballScores = JSONObject.parseObject(matchScoresInfo.getScoresJson());
        Map<Long, FootballScores> allPeriodScores= JsonMapUtils.parseFootballMap(periodFootballScores);
        FootballScores footballScores50 =allPeriodScores.get(50L);
        if(footballScores50==null){
            return;
        }
        String extraScores =matchScoresInfo.getScoresJsonExtra();
        FootballPenaltyScores footballPenaltyScores=null;
        if(StringUtils.isEmpty(extraScores)){
            footballPenaltyScores =new FootballPenaltyScores();
        }else {
            footballPenaltyScores= JSONObject.toJavaObject((JSONObject.parseObject(extraScores)) , FootballPenaltyScores.class);
        }
        BeanUtils.copyProperties(footballPenaltyScores,this);
        allRoundScores=footballScores50.getGoal();
    }
}
