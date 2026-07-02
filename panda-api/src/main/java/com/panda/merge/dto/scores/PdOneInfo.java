package com.panda.merge.dto.scores;

import com.panda.merge.cache.CommonItem;
import lombok.Data;

import java.util.Map;

/**
 * @author warren
 * @since 2024/06/26 04:00:02
 */
@Data
public class PdOneInfo {
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

    private Map<String,CommonItem> allScore;

    private CommonItem periodScores;

    private CommonItem matchScores;

    private Integer status;
}
