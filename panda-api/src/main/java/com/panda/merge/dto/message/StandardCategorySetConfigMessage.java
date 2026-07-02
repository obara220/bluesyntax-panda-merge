package com.panda.merge.dto.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 玩法集开关封锁
 *
 * @author bevan
 * @since 2021年10月19日12:05:38
 */
@Data
public class StandardCategorySetConfigMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标准赛事ID
     */
    private Long standardMatchInfoId;

    /**
     * 玩法集 开关封锁配置
     */
    private List<CategorySetConfigMessage> categorySets;

}

@Data
class CategorySetConfigMessage {
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