package com.panda.merge.dto;

import com.panda.merge.model.ThirdSportPlayer;
import com.panda.merge.model.ThirdSportRegion;
import com.panda.merge.model.ThirdTeamPlayerRelation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author  tell
 * @since   2020年9月21日12:19:20
 * */
public class ThirdSportPlayerDetail extends ThirdSportPlayer {

    @ApiModelProperty(value = "三方库球队球员关系信息")
    @Getter
    @Setter
    private ThirdTeamPlayerRelation teamPlayerRelation;

    @ApiModelProperty(value = "线路ID")
    @Getter
    @Setter
    String linkId;

    @ApiModelProperty(value = "球员区域")
    @Getter
    @Setter
    private ThirdSportRegion playerRegion;

    @ApiModelProperty(value = "国籍区域")
    @Getter
    @Setter
    private ThirdSportRegion countryRegion;
}