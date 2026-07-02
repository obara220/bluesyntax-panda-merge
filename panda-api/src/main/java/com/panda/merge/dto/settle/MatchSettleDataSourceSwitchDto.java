package com.panda.merge.dto.settle;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleDataSourceSwitchDto extends AbstructMatchSettleDto implements Serializable {
    //数据商编码
    private String dataSourceCode;
    //进球开关
    private  Integer goal;
    //角球开关
    private  Integer corner;
    //罚牌开关
    private  Integer booking;
    //灰色区间开关
    private  Integer gray;
    //最高权重开关
    private  Integer topWeight;
    //数据商心跳开关
    private Integer dataSourceHeartbeat;
    //单数据源结算开关
    private Integer singleDataSourceSettle;

    private Long createTime;

    private Long modifyTime;



}
