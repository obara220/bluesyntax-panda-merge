package com.panda.merge.dto.response;

import com.panda.merge.constant.SubscriptionTypeEnum;
import com.panda.merge.dto.advertise.PDFootBallMatchEventDto;
import com.panda.merge.model.MatchEventCommon;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PDMatchEventResponseVo {

    @ApiModelProperty(name = "错误码", notes = "200:成功;其他:包含错误码;详情见: exception")
    private Integer code = 200;

    @ApiModelProperty(name = "具体异常信息")
    private String msg ="事件请求返回";

    private PDFootBallMatchEventDto data;

    private Integer command = SubscriptionTypeEnum.PD_MATCH_EVENT.getCode();


}
