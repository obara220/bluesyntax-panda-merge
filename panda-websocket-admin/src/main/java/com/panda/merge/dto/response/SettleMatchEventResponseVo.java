package com.panda.merge.dto.response;

import com.panda.merge.constant.SubscriptionTypeEnum;
import com.panda.merge.dto.response.StandardSettleScoresPushDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SettleMatchEventResponseVo {

    @ApiModelProperty(name = "错误码", notes = "200:成功;其他:包含错误码;详情见: exception")
    private Integer code = 200;

    @ApiModelProperty(name = "具体异常信息")
    private String msg ="标准赛事结算事件推送";

    private StandardSettleEventPushDto data;

    private Integer command = SubscriptionTypeEnum.SETTLE_MATCH_EVENT.getCode();


}
