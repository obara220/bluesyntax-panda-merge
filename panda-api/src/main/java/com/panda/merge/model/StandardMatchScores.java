package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Fymen
 * @description 标准比分表
 * @date 2024-01-29
 */
@Data
public class StandardMatchScores implements Serializable {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 三方赛事ID
     */
    private Long thirdMatchId;

    /**
     * 体种
     */
    private Long sportId;

    /**
     * 标准赛事ID
     */
    private Long matchId;

    /**
     * 数据源编码
     */
    private String dataSourceCode;

    /**
     * 赛事管理ID
     */
    private String matchManageId;

    /**
     * 比分串
     */
    private String scoreJson;
    /**
     * 与数据源联动开关 0关 1开 默认1开
     */
    private String dataSourceAccoSwitch;

    /**
     * 比分展示状态 1展示，0不展示
     */
    private Integer showStatus;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 下发结算次数
     */
    private Integer sendSettleCount;

    /**
     * 赛制(目前主要针对篮球)
     */
    private Integer matchLength;
}
