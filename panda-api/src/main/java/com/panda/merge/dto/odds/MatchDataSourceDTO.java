package com.panda.merge.dto.odds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CategoryDataSourceRequest
 *
 * @description: 特定比赛玩法下有效数据源列表
 * @date: 3/3/2025
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchDataSourceDTO implements Serializable {

    @NotNull(message = "matchId can not be null")
    private Long matchId;

    private List<CategoryDataSourceDTO> categoryDataSources;


    public List<Long> getCategoryList() {
        if (CollectionUtils.isEmpty(categoryDataSources)) {
            return Collections.emptyList();
        }
        return categoryDataSources.stream().map(CategoryDataSourceDTO::getCategoryId).collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDataSourceDTO implements Serializable {

        @NotEmpty(message = "category can not be empty")
        private Long categoryId;

        @NotEmpty(message = "dataSourceCandidates can not be empty")
        // 当前数据源
        private String ods;

        // 目标切换数据源
        private String tds;

        // 玩法切换数据源结果 0 成功  1 失败
        private Integer status;

        // 附加信息
        private String remark;

    }

}
