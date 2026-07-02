package com.panda.merge.dto.response;

import com.panda.merge.constant.SubscriptionTypeEnum;
import com.panda.merge.dto.settle.ThirdMatchSettleEventDto;
import com.panda.merge.dto.settle.ThirdMatchSettleScoresDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SettleMatchThirdEventResponseVo {

    @ApiModelProperty(name = "错误码", notes = "200:成功;其他:包含错误码;详情见: exception")
    private Integer code = 200;

    @ApiModelProperty(name = "具体异常信息")
    private String msg ="三方赛事数据商事件";

    private ThirdMatchSettleEventDto data;

    private Integer command = SubscriptionTypeEnum.SETTLE_MATCH_THIRD_EVENT.getCode();


}
