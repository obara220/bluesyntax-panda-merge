package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("match_settle_operate_log")
public class MatchSettleOperateLogEntity implements Serializable {
    private static final long serialVersionUID = 2345637010064362261L;

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "操作页面")
    private String operateModule;

    @ApiModelProperty(value = "操作对象Id")
    private String operateId;

    @ApiModelProperty(value = "操作玩法名称")
    private String operateName;

    @ApiModelProperty(value = "操作对象赛事管理id")
    private String operateMatchId;

    @ApiModelProperty(value = "赛事信息")
    private String operateMatchName;

    @ApiModelProperty(value = "操作类型")
    private String operateType;

    @ApiModelProperty(value = "参数名称")
    private String operateParaName;

    @ApiModelProperty(value = "操作前")
    private String operateForwText;

    @ApiModelProperty(value = "操作后")
    private String operateRearText;

    @ApiModelProperty(value = "事件序号")
    private String eventOrder;

    @ApiModelProperty(value = "赛事阶段")
    private Long periodId;

    @ApiModelProperty(value = "操作员名称")
    private String operateUserName;

    @ApiModelProperty(value = "操作用户ip地址")
    private String ipAddress;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "操作时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

}