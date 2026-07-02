package com.panda.merge.dto;

import com.panda.merge.model.LanguageInternation;
import com.panda.merge.model.ThirdSportSeason;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ThirdSportSeasonDetail extends ThirdSportSeason {
    @ApiModelProperty(value = "国际化信息")
    @Getter
    @Setter
    private List<LanguageInternation> seasonNameList;
}
