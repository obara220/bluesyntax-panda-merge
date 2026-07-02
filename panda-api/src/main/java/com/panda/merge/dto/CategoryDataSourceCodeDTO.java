package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CategoryDataSourceCodeDTO implements Serializable {
    /**
     * 三方玩法id，（LS：xxx，TX：xxx）
     */
    @NotNull(message = "三方玩法id不能为空")
    private String thirdCategoryId;
    /**
     * 内部数据源
     */
    private String internalDataSourceCode;
    /**
     * 数据源
     */
    @NotNull(message = "数据源不能为空")
    private String dataSourceCode;

    /**
     * 第三方比赛原始ID
     */
    @NotNull(message="三方赛事源id不能为空")
    private String thirdMatchSourceId;

    /**
     * 市场类型 0-滚球 1-早盘
     */
    private int marketType;

    /**
     * 比分变动的需要校验开售玩法数据源
     * 切换导致的关闭，不需要校验开售玩法数据源
     * 是否需要校验玩法 0-否，1-是
     */
    private int checkDataSourceCode;
}
