package com.panda.merge.dto.settle;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleDataSourceWeightConfigDto extends AbstructMatchSettleDto implements Serializable {

    private Long sportId;
    private Integer tournamentLevel;
    //数据商编码
    private String dataSourceCode;
    //权重上限
    private  Integer weightNum;

}
