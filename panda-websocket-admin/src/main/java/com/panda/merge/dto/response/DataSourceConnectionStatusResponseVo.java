package com.panda.merge.dto.response;

import com.panda.merge.constant.SubscriptionTypeEnum;
import com.panda.merge.dto.settle.DataSourceConnectionStatusDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class DataSourceConnectionStatusResponseVo implements Serializable {
    @ApiModelProperty(name = "错误码", notes = "200:成功;其他:包含错误码;详情见: exception")
    private Integer code = 200;

    @ApiModelProperty(name = "具体异常信息")
    private String msg = "数据商连接状态推送";

    private DataSourceConnectionStatusDto data;

    private Integer command = SubscriptionTypeEnum.DATASOURCE_CONNECTION_STATUS_PUSH.getCode();
}

