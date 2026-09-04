package com.panda.merge.dto;

import com.panda.merge.utils.Compare;
import lombok.Data;

/**
 * 结算权重json模版
 * */
@Data
public class DataSourceSettleWeightAndConfigDto {
    /**
     * 进球权重
     * */
    private Integer goalWeight;
    /**
     *角球权重
     * */
    private Integer cornerWeight;
    /**
     *罚牌权重
     * */
    private Integer bookingWeight;
    /**
     * 灰色权重
     * */
    private Integer grayWeight;
    /**
     *数据商编码
     * */
    private String dataSourceCode;

    /**
     * 权重上限
     */
    private Integer WeightNum;
    /**
     * 初始化参数
     * @return
     */
    public static DataSourceSettleWeightAndConfigDto initDataSourceSettleWeight() {
        DataSourceSettleWeightAndConfigDto dataSourceSettleWeight =new DataSourceSettleWeightAndConfigDto();
        dataSourceSettleWeight.setGrayWeight(0);
        dataSourceSettleWeight.setGoalWeight(0);
        dataSourceSettleWeight.setCornerWeight(0);
        dataSourceSettleWeight.setBookingWeight(0);
        dataSourceSettleWeight.setWeightNum(0);
        return dataSourceSettleWeight;
    }


}
