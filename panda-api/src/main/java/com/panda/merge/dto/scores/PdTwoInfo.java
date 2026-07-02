package com.panda.merge.dto.scores;

import com.panda.merge.cache.CommonItem;
import lombok.Data;

import java.util.Map;

/**
 * 按前端要求将PD2数据(数据源、报球员、当前阶段)设置到该类
 *
 * @author warren
 * @since 2024/05/07 16:51:21
 */
@Data
public class PdTwoInfo {
    /**
     * 数据源
     */
    private String dataSourceCode;

    /**
     * 当前阶段
     */
    private Long periodNow;

    /**
     * 操盘手
     */
    private String operator;

    private Map<String, CommonItem> allScore;

    private CommonItem periodScores;

    private CommonItem matchScores;

    private Integer status;
}
