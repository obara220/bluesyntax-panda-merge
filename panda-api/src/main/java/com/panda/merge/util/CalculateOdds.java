package com.panda.merge.util;

import lombok.Data;

import java.io.Serializable;

@Data
public class CalculateOdds implements Serializable {
    /**
     * 投注项赔率. 单位: 0.00001
     */
    private Integer paOddsValue;
    /**
     * 马来赔
     */
    private Double malayOddsValue;
    /**
     *  投注项状态： 0未激活(锁盘)、1激活、2投注项封盘
     */
    private Integer active;

    /**
     * 优惠赔率
     */
    private Integer disOddsValue;
}
