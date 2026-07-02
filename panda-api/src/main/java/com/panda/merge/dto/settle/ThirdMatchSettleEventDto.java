package com.panda.merge.dto.settle;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 三方赛事结算事件
 * */
@Data
public class ThirdMatchSettleEventDto implements Serializable {
    /**
     * 标准赛事ID
     * */
    private Long standardMatchId;
    /**
     * key: 三方数据商编码
     * value: 三方事件list
     * */
    private Map<String, List<MatchSettleEventExtryInfoDto>> thirdMatchEventMap ;

    private String eventCode;

}
