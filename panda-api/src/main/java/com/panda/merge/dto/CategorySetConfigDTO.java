package com.panda.merge.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author bevan
 * @Description TODO 玩法集 ： 标准玩法ID 、子玩法ID 、开关封锁 透传给下游
 * @createTime 2021年10月20日
 */
@Data
public class CategorySetConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标准玩法
     */
    private Long standardcategoryId;

    /**
     * 子玩法
     */
    private List<Long> childStandardCategoryIds;

    /**
     * 盘口位置开关，开关封锁状态
     */
    private Integer status;
}
