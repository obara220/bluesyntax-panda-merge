package com.panda.merge.v2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
@Data
@TableName("match_settle_info")
public class MatchSettleInfoEntity implements Serializable {
    private static final long serialVersionUID = 2172072195603921216L;
    
    @ApiModelProperty(value = "主键id")
    private Long id;

    private Long sportId;

    @ApiModelProperty(value = "主队全场比分")
    private Integer ftT1;

    @ApiModelProperty(value = "客队全场比分")
    private Integer ftT2;

    @ApiModelProperty(value = "主队上半场")
    private Integer h1T1;

    @ApiModelProperty(value = "客队上半场")
    private Integer h1T2;

    @ApiModelProperty(value = "赛事id")
    private Long standardMatchId;

    private String scoresJson;

    private String scoresJsonExtra;

    @ApiModelProperty(value = "0未冻结1冻结")
    private Integer freezeStatus;

    @ApiModelProperty(value = "1.结算1.02结算2.0")
    private Integer settleType;

    @ApiModelProperty(value = "全部操盘手")
    private String allLiveTrader;

    @ApiModelProperty(value = "操盘手")
    private String liveTrader;

    @ApiModelProperty(value = "操盘手id")
    private String liveTraderId;

    @ApiModelProperty(value = "被限制操作用户名称array")
    private String limitUserArray;

    @ApiModelProperty(value = "全部审核员")
    private String auditorJson;

    @ApiModelProperty(value = "是否开启数据商自动结算:1是0否")
    private Integer isAutoSettleDataSource;

    @ApiModelProperty(value = "进球数据商自动结算:1是0否")
    private Integer goalAutoSettleDataSource;

    @ApiModelProperty(value = "角球数据商自动结算:1是0否")
    private Integer cornerAutoSettleDataSource;

    @ApiModelProperty(value = "当前事件状态：0无1灰色区间2删除事件")
    private Integer currentEventStatus;

    @ApiModelProperty(value = "0无1灰色区间")
    private Integer isGray;

    @ApiModelProperty(value = "有删除事件:1是0否")
    private Integer hasDeleteEvent;

    @ApiModelProperty(value = "有数据源与结算比分不一致提示:1是0否")
    private Integer currentEventTag;

    @ApiModelProperty(value = "罚牌数据商自动结算:1是0否")
    private Integer bookingAutoSettleDataSource;

    @ApiModelProperty(value = "是否有备忘录1:有0:无")
    private Integer ismemo;

    @ApiModelProperty(value = "结算顺序开关:0:开,1:关")
    private Integer settleOrderClosed;

    @ApiModelProperty(value = "是否开启五分钟玩法0:否1:是")
    private Integer fiveMinSwitch;

    @ApiModelProperty(value = "可操作的审核员")
    private String auditorActiveArray;

    @ApiModelProperty(value = "操作时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "玩法类型")
    private String categoryFreezeStatus;

}