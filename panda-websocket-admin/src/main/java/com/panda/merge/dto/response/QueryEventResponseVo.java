package com.panda.merge.dto.response;

import com.alibaba.fastjson.JSONObject;
import com.panda.merge.constant.SubscriptionTypeEnum;
import com.panda.merge.dto.request.RequestVo;
import com.panda.merge.model.MatchEventCommon;
import com.panda.merge.model.ThirdMatchInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class QueryEventResponseVo {

    @ApiModelProperty(name = "错误码", notes = "200:成功;其他:包含错误码;详情见: exception")
    private Integer code = 200;

    @ApiModelProperty(name = "具体异常信息")
    private String msg ="事件请求返回";

    private  List<MatchEventCommon> data;

    private Integer command = SubscriptionTypeEnum.SCORE_EVENT.getCode();


}
