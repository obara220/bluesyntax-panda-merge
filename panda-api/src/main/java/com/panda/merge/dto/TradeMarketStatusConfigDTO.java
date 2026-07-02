package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName TradeMarketStatusConfigDTO
 * @Description TODO
 * @Author Administrator
 * @Date 2020/11/4 13:19
 **/
@Data
public class TradeMarketStatusConfigDTO implements Serializable {
    private static final long serialVersionUID = -5002332682163275298L;
    /**
     * 标准赛事ID
     */
    @NotNull(message = "标准盘口ID不能为空")
    private Long relationMarketId;
    /**
     * 标准赛事ID
     */
    @NotNull(message = "标准赛事不能为空")
    private Long standardMatchInfoId;

    /**
     * 标准玩法ID
     */
    @NotNull(message = "标准玩法不能为空")
    private Long standardCategoryId;

    /**
     * 盘口类型. 属于赛前盘或者滚球盘. 1: 赛前盘; 0: 滚球盘.
     */
    @NotNull(message = "盘口类型不能为空")
    private Integer marketType;
    /**
     * 盘口值
     */
    @NotNull(message = "盘口值不能为空")
    private String addtion;
    /**
     * 操盘后台操作盘口状态：
     *   12:弃用
     *   null或者不等于12 表示启用
     */
    private Integer marketStatus;
}
