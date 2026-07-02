package com.panda.merge.dto.odds;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CategoryDataSourceHighPriority
 *
 * @description:
 * @date: 6/10/2025
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDataSourceHighPriority {


    public static CategoryDataSourceHighPriority LOW_PRIORITY = lowPriority();

    private Long matchId;
    private Long categoryId;

    private Integer marketType;

    private Long sportId;

    // 当前数据源
    private String ods;

    // 新的数据源
    private String tds;

    // 新的优先级
    private Integer tp;

    // 旧的优先级
    private Integer op;

    private String linkId;


    private static CategoryDataSourceHighPriority lowPriority() {
        CategoryDataSourceHighPriority lowPriority = new CategoryDataSourceHighPriority();
        lowPriority.setTp(-1);
        return lowPriority;
    }

    public String getLinkId() {
        if (linkId == null) {
            linkId =  matchId + "_" + categoryId + "_" + marketType + "_" + tds + "_" + System.currentTimeMillis();
        }
        return linkId;
    }

}
