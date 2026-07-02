package com.panda.merge.dto;

import com.panda.merge.model.ThirdMatchHistoryStatistics;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author   tell
 * @since    2021年2月11日14:01:15
 * */
public class ThirdMatchHistoryStatisticsDetail extends ThirdMatchHistoryStatistics {

    @ApiModelProperty(value = "三方赛事id")
    @Getter
    @Setter
    private Long thirdMatchId;

    @ApiModelProperty(value = "标准赛事id")
    @Getter
    @Setter
    private Long standardMatchId;

}