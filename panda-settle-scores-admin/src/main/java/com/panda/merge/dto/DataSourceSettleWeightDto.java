package com.panda.merge.dto;

import com.panda.merge.utils.Compare;
import lombok.Data;

/**
 * 结算权重json模版
 * */
@Data
public class DataSourceSettleWeightDto {
    /**
     * 进球权重
     * */
    @Compare("goal")
    private Integer goalWeight;
    /**
     *角球权重
     * */
    @Compare("corner")
    private Integer cornerWeight;
    /**
     *罚牌权重
     * */
    @Compare("booking")
    private Integer bookingWeight;
    /**
     * 灰色权重
     * */
    @Compare("gray")
    private Integer grayWeight;

    //数据商心跳开关
    @Compare("heartbeatSecond")
    private Integer heartbeatSecond;
    //单数据源结算开关
    @Compare("singleDatasourceSettleSwitch")
    private Integer singleDatasourceSettleSwitch;
    /**
     *数据商编码
     * */
    private String dataSourceCode;


    /**
     * 初始化参数
     * @return
     */
    public static DataSourceSettleWeightDto initDataSourceSettleWeight() {
        DataSourceSettleWeightDto dataSourceSettleWeight =new DataSourceSettleWeightDto();
        dataSourceSettleWeight.setGrayWeight(0);
        dataSourceSettleWeight.setGoalWeight(0);
        dataSourceSettleWeight.setCornerWeight(0);
        dataSourceSettleWeight.setBookingWeight(0);
        return dataSourceSettleWeight;
    }


}
