package com.panda.merge.dto;

import com.panda.merge.model.LanguageInternation;
import com.panda.merge.model.StandardSportRegion;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class StandardSportRegionDetail extends StandardSportRegion {
    @ApiModelProperty(value = "标准球队名称国际化信息")
    @Getter
    @Setter
    private List<LanguageInternation> il8nNameList;
}
