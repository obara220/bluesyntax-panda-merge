package com.panda.merge.service;

import com.panda.merge.model.*;

import java.util.List;

/**
 * 结算模版方法
 * */
public interface ISettleTemplateService {
    /**
     *根据标准赛事id获得联赛模版
     * */
    MatchSettleTemplate getTemplateByStandardMatchId(Long standardMatchId, Integer templateType);
//    /**
//     * 事件或者比分灰色区间区域判定
//     * */
//    CheckIsGreyDto oneIsGray(MatchSettleTemplate template, String eventCode, Integer secondFromStart, String dataSourceCode);
    /**
     * 灰色区间权重判断和更新
     * */
    Boolean judgeGrayStatus(MatchEventInfo matchEventInfo, MatchSettleTemplate template, String dataSourceCode, String grayType, Integer min, Long sportId);

    Long getGrayId(Long standardMatchId,String dataSourceCode, String grayType, Integer min);
    /**
     * 结算权重判断
     * */
    Boolean judgeSettleWeight(MatchSettleTemplate template, List<MatchSettleCheckInfo> checkInfoList);

    Integer getTemplateGraySeconds(String type, String dataSourceCode, MatchSettleTemplate template);
    /**
     * 根据条件判断是否灰色
     * */
    void  cancelGrayStatus(MatchSettleThirdScore matchSettleThirdScore, MatchSettleScore matchSettleScore);

    void  batchCancelGrayStatus(String dataSourceCode, List<MatchSettleScore> matchSettleScores);
}
