package com.panda.merge.advertise.dto;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.dto.CommonItem;
import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import lombok.Data;

import java.util.Map;

/**
 * 网球报球版详情
 * */
@Data
public class TennisAdvertiseVo extends AbstructAdvertiseDto {
    // 赛事信息相关
    /***
     * 赛事Id
     */
    private String thirdMatchId;
    /**
     * 标准赛事ID
     * */
    private Long standardMatchId;
    /**
     * 开赛时间
     * */
//    private Long matchBeginTime;
    //赛事时间阶段相关
    /**
     * 阶段
     * */
    private Long period;

    //赛事长度
    private Integer  matchLength;
    //局制
    private Integer roundType;
    //当前第几局
    private Integer currentRound;

    //当前盘数
    private Integer currentSet;

    private Integer firstNum;

    //总局数 由局结束+1计算
    private Integer totalRound;

    //总盘数 由盘结束+1计算
    private Integer totalSet;

    //盘比分
    private CommonItem matchScore;
    //总局比分
    private CommonItem totalRoundScore;
    //所有盘局比分
    private Map<Integer, CommonItem> allSetRoundScore;
    //所有盘局内比分
    private Map<Integer, Map<Integer,CommonItem>> allSetSecondScore;

    private JSONObject periodLengthJson;

    private JSONObject matchLengthJson;
}
