package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataMerchantMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 批量玩法级别
     */
    private Integer tradeLevel;
    /**
     * 运动类型id
     */
    private Long sportId;
    /**
     * 标准赛事id
     */
    private Long matchId;
    /**
     * 玩法集合
     */
    private List<Long> playIdList;
    /**
     * 子玩法ID
     */
    private Long subPlayId;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 风控定义
     * 3：数据源挡板
     * 11: A模式出现让球0或±0.5
     * 16: TX内部切换数据源
     */
    private Integer linkedType;


    private Integer isErrorProofing;
}
