package com.panda.merge.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class StandardSportMarketCategory implements Serializable {
    @ApiModelProperty(value = "表ID,自增")
    private Long id;

    @ApiModelProperty(value = "运动种类id.对应表sport.id")
    private Long sportId;

    @ApiModelProperty(value = "标准玩法ID.")
    private Long marketCategoryId;

    @ApiModelProperty(value = "玩法名称编码.用于多语言.")
    private Long nameCode;

    @ApiModelProperty(value = "玩法名称描述")
    private Long descNameCode;

    @ApiModelProperty(value = "是否展开，1：“是”代表默认展开，0：“否”代表默认收起")
    private Integer isCollapse;

    @ApiModelProperty(value = "所属时段")
    private String scopeId;

    @ApiModelProperty(value = "玩法状态.0无效;1有效")
    private Integer status;

    @ApiModelProperty(value = "对外商户状态0:关闭;1:开启")
    private Integer merchantStatus;

    @ApiModelProperty(value = "AO玩法状态.0无效;1有效")
    private Integer aoStatus;

    @ApiModelProperty(value = "排序值.")
    private Integer orderNo;

    @ApiModelProperty(value = "H5模板展示")
    private Integer templateH5;

    @ApiModelProperty(value = "PC模板展示")
    private Integer templatePc;

    private Long createTime;

    private Long modifyTime;

    @ApiModelProperty(value = "客户端PC模板展示")
    private Integer templatePcClient;

    @ApiModelProperty(value = "客户端h5模板展示")
    private Integer templateH5Client;

    @ApiModelProperty(value = "玩法名称详情")
    private Long detailNameCode;

    @ApiModelProperty(value = "对外商户编码集合")
    private String merchantApiCodeList;

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "合买开关 0:关闭;1:开启")
    private Integer mrStatus;

    @ApiModelProperty(value = "主玩法多语言")
    private Long mainNameCode;
}
