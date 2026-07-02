package com.panda.merge.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panda.merge.constant.SubscriptionTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class OnlineResponseVo {
    @ApiModelProperty(name = "错误码", notes = "200:成功;其他:包含错误码;详情见: exception")
    private Integer code = 200;

    @ApiModelProperty(name = "具体异常信息")
    private String msg ="操盘手在线返回";

    private Object data;

    private Integer command = SubscriptionTypeEnum.CAOPAN_ONLINE_PUSH.getCode();

}