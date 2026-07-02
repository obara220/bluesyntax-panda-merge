package com.panda.merge.dto.message;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class SoldMessage implements Serializable {

    /**
     * 标准赛事Id
     */
    @NotNull(message="标准赛事id不能为空")
    private Long matchId;

    /**
     * 操盘平台.
     */
    @NotNull(message="操盘平台不能为空")
    private String riskManagerCode;

    /**
     * 盘口类型. 1: 赛前盘; 0: 滚球盘.
     */
    @NotNull(message="盘口类型不能为空")
    private Integer marketType;

    /**
     * 0:普通赛事、1：冠军赛事
     **/
    @NotNull(message="是否是冠军玩法")
    String isOutRight;

    /**
     * 标准盘口玩法
     */
    @NotNull(message="开售玩法列表不能为空")
    private List<CategoryMessage> marketCategoryIds;

    /**
     * 开售的数据源:SR,BC,BG
     */
    @NotNull(message="开售的数据源不能为null")
    private String dataSource;


}
