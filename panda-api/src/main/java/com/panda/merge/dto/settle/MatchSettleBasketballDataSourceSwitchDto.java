package com.panda.merge.dto.settle;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleBasketballDataSourceSwitchDto extends AbstructMatchSettleDto implements Serializable {
    //数据商编码
    private String dataSourceCode;
    //进球开关
    private  Integer goal;
    //灰色区间开关
    private  Integer gray;
    //最高权重开关
    private  Integer topWeight;

    private Long createTime;

    private Long modifyTime;



}
