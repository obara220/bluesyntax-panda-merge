package com.panda.merge.dto;

import com.panda.merge.model.LanguageInternation;
import com.panda.merge.model.ThirdSportTeam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author   tell
 * @since    2020年9月24日20:47:27
 * */
public class ThirdSportTeamDetail extends ThirdSportTeam {

    @ApiModelProperty(value = "三方球队名称国际化信息")
    @Getter
    @Setter
    private List<LanguageInternation> il8nNameList;

}