package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ThirdMatchInternalCode  implements Serializable {
    /**
     * 第三方数据源编码
     */
    private String dataSourceCode;
    /**
     * 内部编码列表
     */
    private List<String> internalCodeList;
    /**
     * 第三方比赛原始ID,可为空
     */
    private String thirdMatchSourceId;
    /**
     * 当前比赛内部编码
     */
    private String currentInternalCode;
    /**
     * 市场类型 0-滚球 1-早盘
     */
    private int marketType;
}
