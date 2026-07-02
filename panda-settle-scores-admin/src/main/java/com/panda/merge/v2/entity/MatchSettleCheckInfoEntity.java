package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
@Data
@TableName("match_settle_check_info")
public class MatchSettleCheckInfoEntity implements Serializable {
    private static final long serialVersionUID = 7701779323967843220L;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "赛事ID")
    private Long standardMatchId;

    @ApiModelProperty(value = "结算比分或者事件ID")
    private Long settleScoreEventId;

    @ApiModelProperty(value = "核对状态：0未编辑1已编辑2已确认待核对3已确认核对成功4已确认核对失败")
    private Integer checkStatus;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "核对数据类型1数据商2用户输入")
    private Integer checkDataType;

    @ApiModelProperty(value = "源三方结算比分事件ID")
    private Long thirdSettleScoreEventId;

    @ApiModelProperty(value = "1.比分表的比分2.事件表的事件")
    private Integer checkType;

    @ApiModelProperty(value = "数据商编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "是否灰色区间：1是0不是")
    private Integer isGrey;

    @ApiModelProperty(value = "主队比分")
    private Integer t1;

    @ApiModelProperty(value = "客队比分")
    private Integer t2;

    @ApiModelProperty(value = "盘主队比分")
    private Integer firstT1;

    @ApiModelProperty(value = "盘客队比分")
    private Integer firstT2;

    @ApiModelProperty(value = "局主队比分")
    private Integer secondT1;

    @ApiModelProperty(value = "局客队比分")
    private Integer secondT2;

    @ApiModelProperty(value = "主客队")
    private String homeAway;

    @ApiModelProperty(value = "事件序号")
    private Integer eventOrder;

    @ApiModelProperty(value = "附加字段")
    private String extryInfo;

    @ApiModelProperty(value = "核对次序")
    private Integer checkNumber;

    @ApiModelProperty(value = "事件编码")
    private String eventCode;

    @ApiModelProperty(value = "走水:0不走水1走水")
    private Integer goWaterStatus;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;

    @ApiModelProperty(value = "五分钟区间（5，10，15......90，每个区间+5；上半场绝杀49，下半场绝杀99，无进球0")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fiveMinSection;
    @TableField(exist = false)
    private String fifteenMinSection;

}