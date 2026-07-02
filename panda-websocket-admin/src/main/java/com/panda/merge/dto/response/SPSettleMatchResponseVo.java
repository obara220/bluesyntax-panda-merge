package com.panda.merge.dto.response;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.constant.SubscriptionTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SPSettleMatchResponseVo implements Serializable {

    @ApiModelProperty(name = "错误码", notes = "200:成功;其他:包含错误码;详情见: exception")
    private Integer code = 200;

    @ApiModelProperty(name = "具体异常信息")
    private String msg ="结算2.0特殊玩法赛事级推送刷新";

    private String standardMatchId;

    private Integer command = SubscriptionTypeEnum.SP_SETTLE_MATCH_PUSH.getCode();


}
