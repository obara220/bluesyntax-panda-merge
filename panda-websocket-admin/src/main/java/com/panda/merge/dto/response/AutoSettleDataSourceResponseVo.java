package com.panda.merge.dto.response;

import com.panda.merge.constant.SubscriptionTypeEnum;
import com.panda.merge.dto.settle.AutoSettleDataSourceDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AutoSettleDataSourceResponseVo implements Serializable {
    @ApiModelProperty(name = "错误码", notes = "200:成功;其他:包含错误码;详情见: exception")
    private Integer code = 200;

    @ApiModelProperty(name = "具体异常信息")
    private String msg ="数据商自动结算开关状态推送";

    private AutoSettleDataSourceDto data;

    private Integer command = SubscriptionTypeEnum.AUTO_SETTLE_DATA_SOURCE_PUSH.getCode();
}
