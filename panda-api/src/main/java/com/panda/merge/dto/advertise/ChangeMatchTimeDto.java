package com.panda.merge.dto.advertise;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChangeMatchTimeDto extends AbstructAdvertiseDto {
    //Long thirdMatchId,Long matchStartTime
    private Long thirdMatchId;
    private Long matchTime;
    private Integer sportId;
    /**
     * 篮球修改赛事时间，0是左右的+、- 按钮，1 是中间的修改时间
     */
    private Integer type;

    @ApiModelProperty(value = "修改前的时间")
    private String beforeTime;

    @ApiModelProperty(value = "修改后的时间")
    private String afterTime;

}
