package com.panda.merge.dto.advertise;

import lombok.Data;

//比分设置dto

@Data
public class TennisEditSecondScoreDto extends AbstructAdvertiseDto {
    private Long standardMatchId;
    //三方赛事id
    private Long thirdMatchId;
    //赛事(第几盘)
    private Long  firstNum;

    //赛事(第几局)
    private Long  secondNum;

    //主客队 比分变更
    private String homeAway;
    // 抢七  0~7 抢十 0~10 ; 0 ,15,30,40,50(AD),60(AD 获胜)  用于校验
    private Integer scoreNumber;
    //当前主客队比分 获胜确认需要前端传值才能进行更新
    private Integer T1;
    //当前主客队比分
    private Integer T2;
    //事件信息
    private Long eventTime;
    //当前盘
    private Integer currentSet;
    //当前局
    private Integer currentRound;
}
