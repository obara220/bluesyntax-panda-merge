package com.panda.merge.dto.message;

import com.panda.merge.common.utils.TimeUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 标准 提前结算赛事信息
 */
@Getter
@Setter
public class StandardMatchMarketPreMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 标准比赛ID
     */
    private Long standardMatchInfoId;

    /**
     * 赛事类型：0：普通赛事，1:冠军赛事
     */
    private Integer matchType = 0;
    /**
     * 数据源
     */
    private String dataSourceCode;

    private Long modifyTime = TimeUtils.millsSecondsEast8ZoneGmt();

    /**
     * 运动种类
     */
    private Long sportId;

    /**
     * 1开 ，0关
     * 最终状态  = 赛事提前结算状态 、操盘赛事级别状态
     */
    private Integer matchPreStatus = 0 ;

    /**
     * 1开 ，0关
     * 赛事提前结算状态
     */
    private Integer matchPreStatusRisk = 0 ;

    /**
     * 盘口投注项
     */
    private List<StandardMatchMarketPreResultMessage> marketPreResultMessages;

    private Integer a01Verify;

    private String requestType;
}
