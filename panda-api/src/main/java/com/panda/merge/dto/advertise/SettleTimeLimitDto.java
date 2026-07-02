package com.panda.merge.dto.advertise;

import com.panda.merge.dto.settle.AbstructMatchSettleDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 即时结算时间，当前只有篮球
 */
@Data
@ApiModel(description = "即时结算时间")
public class SettleTimeLimitDto extends AbstructMatchSettleDto implements Serializable {

	@ApiModelProperty(value = "赛种",notes = "sportId")
	private Long sportId;

	@ApiModelProperty(value = "即时结算设置",notes = "即时结算设置")
	@NotNull(message = "即时结算设置参数不能为空")
	private String limitSwitchJson;
}

