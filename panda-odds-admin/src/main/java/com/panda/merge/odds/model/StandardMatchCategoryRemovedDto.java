package com.panda.merge.odds.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;


@Data
public class StandardMatchCategoryRemovedDto implements Serializable {
    /**
     * 标准赛事ID
     */
    private Long standardMatchId;
    /**
     * 0上架，1下架
     */
    private Integer status;
    /**
     * 0滚球 1赛前
     */
    private int marketType;
    /**
     * 玩法集合
     */
    private Set<Long> marketCategoryIds;
}
