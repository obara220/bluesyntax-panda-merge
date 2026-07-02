package com.panda.merge.dto.settle;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleDataSourceDto extends AbstructMatchSettleDto implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;

    /**
     * 球种的类型
     */
    private Long sportId;

    /**
     * 开关状态,0:关闭、1:开启
     */
    private Integer status;

    /**
     * 联赛的等级0-20，-1为全部联赛开关
     */
    private Integer tournamentLevel;

    /**
     * 数据源名称BC、BG
     */
    private String dataSourceCode;


}
