package com.panda.merge.dto;

import com.panda.merge.model.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author   tell
 * @since    2020年9月21日12:40:41
 * */
public class ThirdMatchInfoDetail extends ThirdMatchInfo {

    @ApiModelProperty(value = "联赛名称")
    @Getter
    @Setter
    private String tournamentName;

    @ApiModelProperty(value = "联赛英文名称")
    @Getter
    @Setter
    private String tournamentNameEn;

    @ApiModelProperty(value = "赛事球队关系")
    @Getter
    @Setter
    private List<ThirdMatchTeamRelation> mtRelationList;

    @ApiModelProperty(value = "赛事主队关系")
    @Getter
    @Setter
    private ThirdMatchTeamRelation homeRelation;

    @ApiModelProperty(value = "赛事客队关系")
    @Getter
    @Setter
    private ThirdMatchTeamRelation awayRelation;

    @ApiModelProperty(value = "赛事下球队列表")
    @Getter
    @Setter
    private List<ThirdSportTeamDetail> teamList;

    @ApiModelProperty(value = "场地名称国际化信息")
    @Getter
    @Setter
    private List<LanguageInternation> psitionNameList;

    @ApiModelProperty(value = "三方赛事区域信息")
    @Getter
    @Setter
    private ThirdSportRegion thirdSportRegion;

    @ApiModelProperty(value = "标准赛事区域信息")
    @Getter
    @Setter
    private StandardSportRegionDetail standardSportRegion;

}