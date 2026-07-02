package com.panda.merge.dto.response;

import com.panda.merge.constant.SubscriptionTypeEnum;
import com.panda.merge.dto.settle.AutoSettleDataSourceDto;
import com.panda.merge.dto.settle.MatchSettleRollBackDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MatchSettleRollBackResponseVo implements Serializable {
    @ApiModelProperty(name = "错误码", notes = "200:成功;其他:包含错误码;详情见: exception")
    private Integer code = 200;

    @ApiModelProperty(name = "具体异常信息")
    private String msg ="赛事回滚状态推送";

    private MatchSettleRollBackDto data;

    private Integer command = SubscriptionTypeEnum.MATCH_SETTLE_ROLL_BACK_STATUS_PUSH.getCode();
}
