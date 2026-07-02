package com.panda.merge.dto;

import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import com.panda.merge.model.ThirdMatchTeamRelation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author   tell
 * @since    2020年9月21日12:40:41
 * */
public class ThirdMatchTeamRelationDetail extends ThirdMatchTeamRelation {

    @ApiModelProperty(value = "三方球员源id")
    @Getter
    @Setter
    private String  thirdSourcePlayerId;

}