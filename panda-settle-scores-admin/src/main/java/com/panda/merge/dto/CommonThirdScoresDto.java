package com.panda.merge.dto;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.model.MatchEventInfo;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.utils.JsonMapUtils;
import lombok.Data;

import java.util.Map;

@Data
public class CommonThirdScoresDto {
    private String linkedId;
    //1.三方赛事ID
    private Long thirdMatchId;
    //标准赛事ID
    private Long standardMatchId;

    //2.赛事阶段
    private Long periodId;
    //3.赛种
    private Long sportId;
    //4.数据源
    private String dataSourceCode;
    //5.比分
    private Map scores;
    //6.事件源类型
    private Integer eventSourceType;
    //比分计算的时间
    private Long scoreTime;

    private Map<String, Object> allScores;
    //1. 15分钟阶段比分
    private Map  minuteScores;

    private  Long secondFromStart;

    private Long eventId;

    private MatchEventInfo matchEventInfo;
    /**
     * 暂时放点球大战比分
     * */
    private JSONObject extraScores;

    private StandardMatchInfo standardMatchInfo;

    private String userName;

    public CommonItem getHomeAwayScore(){
        Map<String, BasketballScores> basketballScoresMap = JsonMapUtils.transferBasketballMap(scores);
        BasketballScores basketballScores = basketballScoresMap.get(new Long(-1).toString());
        if(basketballScores==null){
            return null;
        }
        return basketballScores.getMatchScore();
    }
}
