package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class StandardCategoryDataSourceCodeDTO implements Serializable {

    /**
     * 内部数据源集合
     */
    @NotNull(message = "内部数据源不能为空")
    private List<ThirdMatchInternalCode> ThirdMatchInternalCodeList;

    /**
     * 赛事id
     */
    @NotNull(message="标准赛id不能为空")
    private Long standardMatchSourceId;
}
