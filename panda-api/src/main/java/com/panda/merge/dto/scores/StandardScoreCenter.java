package com.panda.merge.dto.scores;


import com.panda.merge.model.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 标准比分中心页面
 */
@Slf4j
@Data
public class StandardScoreCenter implements Serializable{
    private Long sportId;
    private Long standardMatchId;
    /**
     * 数据源编码
     */
    private String dataSourceCode;

    /**
     * 常规比分
     */
    private List<StandardScoreDTO> scores;
    /**
     * 区间比分
     */
    private List<StandardScoresSixDetailDTO> minute6Scores;
    /**
     * 区间比分
     */
    private List<StandardScoresDetailDTO> minute15Scores;
    /**
     * 与数据源联动开关0关 1开
     */
    private String switchStatus;
    /**
     * 是否主事件源
     */
    private Boolean isMain = false;
    /**
     * 结算比分
     */
    private List<StandardScoreDTO> settleScores;
    /**
     * 排序序号
     */
    private int index;
    private String userId;
    private String userName;
    private String ipAddress;

    //阶段
    private Integer period;
    //中断时间
    private Long interruptTime;

}
