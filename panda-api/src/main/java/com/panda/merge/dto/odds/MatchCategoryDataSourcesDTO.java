package com.panda.merge.dto.odds;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * CategoryDataSourceRequest
 *
 * @description: 特定比赛玩法下有效数据源列表
 * @date: 3/3/2025
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchCategoryDataSourcesDTO implements Serializable {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDataSources implements Serializable {
        @NotEmpty(message = "category can not be empty")
        private Long category;

        @NotEmpty(message = "dataSourceCandidates can not be empty")
        private List<String> dataSourceCandidates;
    }

    @NotNull(message = "matchId can not be null")
    private Long matchId;

    /** 数据源数据有效期  **/
    private Integer validityPeriod;

    private List<CategoryDataSources> categoryDataSources;

}
