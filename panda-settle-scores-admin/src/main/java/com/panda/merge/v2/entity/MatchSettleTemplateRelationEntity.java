package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("match_settle_template_relation")
public class MatchSettleTemplateRelationEntity implements Serializable {
    private static final long serialVersionUID = -6679823755499979696L;

    private Long id;

    @ApiModelProperty(value = "运动ID")
    private Long sportId;

    @ApiModelProperty(value = "结算权重模版id")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long templateSettleWeightId;

    @ApiModelProperty(value = "结算延迟模版id")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long templateCountDowenId;

    @ApiModelProperty(value = "灰色区间设置模版id")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long templateGrayAreaId;

    @ApiModelProperty(value = "标准联赛id")
    private Long standardTournamentId;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

}