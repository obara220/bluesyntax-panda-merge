package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("match_settle_third_basket_score")
public class MatchSettleThirdBasketScoreEntity implements Serializable {
    private static final long serialVersionUID = -7883126342917094259L;
    private Long id;

    @ApiModelProperty(value = "运动ID")
    private Long sportId;

    @ApiModelProperty(value = "标准赛事id")
    private Long standardMatchId;

    @ApiModelProperty(value = "三方赛事id")
    private Long thirdMatchId;

    @ApiModelProperty(value = "数据商编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "主队总分")
    private Integer t1;

    @ApiModelProperty(value = "客队总分")
    private Integer t2;

    @ApiModelProperty(value = "当前半场主队比分")
    private Integer firstT1;

    @ApiModelProperty(value = "当前半场客队比分")
    private Integer firstT2;

    @ApiModelProperty(value = "当前节主队比分")
    private Integer secondT1;

    @ApiModelProperty(value = "当前节客队比分")
    private Integer secondT2;

    @ApiModelProperty(value = "追踪link")
    private String linkId;

    @ApiModelProperty(value = "总分和")
    private Integer sumScore;

    @ApiModelProperty(value = "结算总分")
    private Integer settleSumScore;

    @ApiModelProperty(value = "阶段")
    private Long periodId;

    @ApiModelProperty(value = "当前进行时长(篮球倒计时)")
    private Integer secondFromStart;

    @ApiModelProperty(value = "比分的发生时间")
    private Long eventTime;

    @ApiModelProperty(value = "三方赛事事件id")
    private String thirdEventId;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

}