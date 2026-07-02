package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.merge.dto.settle.AbstructMatchSettleDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("match_settle_template")
//public class MatchSettleTemplateEntity extends AbstructMatchSettleDto implements Serializable {
public class MatchSettleTemplateEntity implements Serializable {
    private static final long serialVersionUID = 3139535114254067179L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "运动ID")
    private Long sportId;

    @ApiModelProperty(value = "联赛等级")
    private Integer tournamentLevel;

    @ApiModelProperty(value = "模版")
    private String templateJson;

    @ApiModelProperty(value = "模版类型:1.数据商结算权重2.结算倒计时模版3.灰色区间模版")
    private Integer templateType;

    @ApiModelProperty(value = "模版名称")
    private String templateName;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

}