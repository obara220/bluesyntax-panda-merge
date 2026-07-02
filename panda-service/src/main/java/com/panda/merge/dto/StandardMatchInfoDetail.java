package com.panda.merge.dto;

import com.panda.merge.dto.message.RcsProfitRectangle;
import com.panda.merge.model.StandardMatchInfo;
import com.panda.merge.model.StandardSportMarketSell;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author   tell
 * @since    2020年9月21日12:40:41
 * */
public class StandardMatchInfoDetail extends StandardMatchInfo {

    @ApiModelProperty(value = "标准联赛级别")
    @Getter
    @Setter
    private Integer tournamentLevel;

    @ApiModelProperty(value = "预开售信息")
    @Getter
    @Setter
    private StandardSportMarketSell marketSell;

    @ApiModelProperty(value = "早盘盘口数")
    @Getter
    @Setter
    private Integer  displayMarketCount;

    @ApiModelProperty(value = "滚球盘口数")
    @Getter
    @Setter
    private Integer  liveMarketCount;

    @ApiModelProperty(value = "赛事类型,0:普通赛事、1冠军赛事")
    @Getter
    @Setter
    private Integer matchType = 0;

    @ApiModelProperty(value = "是否自动开售新玩法")
    @Getter
    @Setter
    private String autoSellStatus;
}
