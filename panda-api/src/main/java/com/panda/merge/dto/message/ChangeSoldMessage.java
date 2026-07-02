package com.panda.merge.dto.message;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class ChangeSoldMessage implements Serializable {
    /**
     * 原三方赛事id
     */
    @NotNull(message="原三方赛事id不能为空")
    private String newThirdMatchId;
    /**
     * 新三方赛事id
     */
    @NotNull(message="新三方赛事id不能为空")
    private String oldThirdMatchId;
    /**
     * 原始开售的数据源:SR,BC,BG
     */
    @NotNull(message="原始的数据源不能为null")
    private String oldDataSource;
    /**
     * 标准赛事Id
     */
    @NotNull(message="标准赛事id不能为空")
    private Long matchId;

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
    private List<Long> marketCategoryIds;

    /**
     * 开售的数据源:SR,BC,BG
     */
    @NotNull(message="开售的数据源不能为null")
    private String dataSource;
}
