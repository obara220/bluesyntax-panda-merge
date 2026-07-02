package com.panda.merge.dto.settle;

import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleDataSourceAllWeightDto extends AbstructMatchSettleDto implements Serializable {

    /**
     * 联赛的等级0-20，-1为全部联赛开关
     */
    private Integer tournamentLevel;

    /**
     * 数据源名称BC、BG
     */
    private String dataSourceCode;

    /**
     * 进球权重
     */
    private  Integer goalWeight;

    /**
     * 角球权重
     */
    private  Integer cornerWeight;

    /**
     * 罚牌权重
     */
    private  Integer bookingWeight;

    /**
     *  灰色区间权重
     */
    private  Integer grayWeight;
    /**
     * 进球15分钟灰色区间
     * */
    private  Integer goal15Min;
    /**
     * 角球15分钟灰色区间
     * */
    private  Integer corner15Min;
    /**
     * 罚牌15分钟灰色区间
     * */
    private  Integer booking15Min;
    /**
     * 进球5分钟灰色区间
     * */
    private  Integer goal5Min;

    /**
     * 篮球进球6分钟灰色区间
     * */
    private Integer goal6Min;

    /**
     * 权重上限
     */
    private Integer weightNum;

    /**
     * 心跳秒数
     */
    private Integer heartbeatSecond;

    /**
     * 单数据源结算开关：0关闭，1开启
     */
    private Integer singleDatasourceSettleSwitch;

}
