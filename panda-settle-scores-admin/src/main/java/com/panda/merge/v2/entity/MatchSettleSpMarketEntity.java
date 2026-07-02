package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("match_settle_sp_market")
public class MatchSettleSpMarketEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "赛事ID")
    private Long standardMatchId;

    @ApiModelProperty(value = "数据商编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "赛种")
    private Long sportId;

    @ApiModelProperty(value = "玩法Id")
    private Long marketCategoryId;

    @ApiModelProperty(value = "子玩法Id")
    private Long childMarketCategoryId;

    @ApiModelProperty(value = "玩法英文名")
    private String categoryNameEn;

    @ApiModelProperty(value = "玩法中文名")
    private String categoryNameCn;

    @ApiModelProperty(value = "1.比分表的比分2.事件表的事件")
    private Integer checkType;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;



}