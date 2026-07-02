package com.panda.merge.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MatchOperateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "操作类型.", notes = "1:关联操作;2:取消关联操作;合并操作拆分为多个其他操作")
    private Integer operateType;

    @ApiModelProperty(value = "第三方赛事id", notes = "关联操作时至少包含一个;取消关联操作时仅包含一个;")
    private List<Long> thirdMatchIds;

    @ApiModelProperty(value = "标准赛事id", notes = "关联操作时至少包含一个;取消关联操作时仅包含一个;")
    private Long standardMatchId;
}

