package com.panda.merge.dto.settle;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 三方赛事结算比分
 * */
@Data
public class ThirdMatchSettleScoresDto implements Serializable {
    /**
     * 标准赛事ID
     * */
    private Long standardMatchId;
    /**
     * key: 三方数据商编码
     * value: 三方比分list
     * */
    private Map<String, List<MatchSettleScoreDto>> thirdMatchScoresMap ;

    private String eventCode;

}
