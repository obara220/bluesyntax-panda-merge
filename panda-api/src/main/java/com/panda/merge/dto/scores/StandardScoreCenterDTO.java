package com.panda.merge.dto.scores;


import com.panda.merge.dto.advertise.AbstructAdvertiseDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 标准比分中心页面
 */
@Slf4j
@Data
public class StandardScoreCenterDTO extends AbstructAdvertiseDto {
    private Long sportId;
    private Long standardMatchId;
    private String matchManageId;
    /**
     * 比分对象
     */
    private List<StandardScoreCenter> scores;

    /**
     * 主事件源
     */
    private String businessEvent;
    /*8
    关联事件源
     */
    private String relatedDataSourceCoderList;
    //预开售ID
    private Long preId;
    /**
     * 赛制
     */
    private Integer matchLength;
    /**
     * 结算次数
     */
    private Integer sendSettleCount;

    //区间比分校验开关  false不校验
    private Boolean minScoresCheck;

    /**
     * 比分展示开关
     */
    private Integer showStatus;
}
