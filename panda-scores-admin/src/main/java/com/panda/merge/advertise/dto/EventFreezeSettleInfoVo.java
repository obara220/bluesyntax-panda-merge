package com.panda.merge.advertise.dto;


import lombok.Data;


import java.io.Serializable;


/**
 * @author dorf
 * @version 1.0
 * @description 冻结参数
 * @date 2021/1/5
 */
@Data
public class EventFreezeSettleInfoVo implements Serializable {

    private static final long serialVersionUID = 332020592812727347L;

    /**
     * sportId不能为空
     */
    private Integer sportId;
    /**
     * matchId不能为空
     */
    private Long matchId;
    /**
     * 操作人id
     */
    private Integer operatorId;
    /**
     * 操作人姓名
     */
    private String operatorName;
}
