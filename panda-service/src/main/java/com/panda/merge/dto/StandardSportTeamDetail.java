package com.panda.merge.dto;

import com.panda.merge.model.LanguageInternation;
import com.panda.merge.model.StandardMatchTeamRelation;
import com.panda.merge.model.StandardSportTeam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author   tell
 * @since    2020年9月21日12:40:41
 * */
public class StandardSportTeamDetail extends StandardSportTeam {

    @ApiModelProperty(value = "标准赛事球队关系信息")
    @Getter
    @Setter
    private StandardMatchTeamRelation matchTeamRelation;

    @ApiModelProperty(value = "标准球队名称国际化信息")
    @Getter
    @Setter
    private List<LanguageInternation> il8nNameList;


}