package com.panda.merge.mq.message;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 人工事件信息
 * @author   idol
 * @since    2021年12月28日15:00:44
 */
@Data
public class MatchEventInfoMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 三方数据源赛事id*/
    @NotEmpty(message = "thirdMatchSourceId不能为空")
    private String thirdMatchSourceId;

    /** 事件编码*/
    private String eventCode;

    /**三方数据源事件id.*/
    private String thirdEventId;

    /** 运动种类id。 对应sport.id（如果玩法不区分体育类型，传0，否则传对应体育类型标识）*/
    private Long sportId;

    /** 数据来源编码*/
    @NotEmpty(message="dataSourceCode字段不能为空")
    private String dataSourceCode;

    /** 三方数据源球队原始id*/
    private String thirdTeamId;

    /** 是否被取消.1 被取消; 0:没有被取消*/
    @NotNull(message = "canceled字段不能为null")
    private Integer canceled;

    /** 数据来源类型（0 : UOF，1:  Scoring Feed）*/
    @NotEmpty(message="sourceType不能为空")
    @Pattern(regexp = "^[0-1]*$",message = "sourceType状态不正确")
    private String sourceType;

    /** 事件发生时间*/
    @NotNull(message = "eventTime字段不能为null")
    @Min(value = 0, message = "必须为正数")
    private Long eventTime;

    /** 主客场. 主队: home 客队: away*/
    private String homeAway;

    /** 比赛阶段id.  system_item_dict.value*/
    private Long matchPeriodId;

    /** 球员1的id*/
    private Long player1Id;
    /** 球员2的id*/
    private Long player2Id;

    /** 球员1的名称*/
    private String player1Name;
    /** 球员2的名称*/
    private String player2Name;

    /** 比赛时长*/
    private Integer matchLength;

    /** 比赛已进行时长*/
    @NotNull(message = "secondsFromStart字段不能为null")
    @Min(value = 0, message = "必须为正数")
    private Long secondsFromStart;

    /** 当前节\阶段剩余时间*/
    private Long periodRemainingSeconds;

    /** 主队数量*/
    private Integer t1;

    /** 客队数量*/
    private Integer t2;

    /** 当运动种类如网球斯诺克有局比分概念时表示当前局数*/
    private Integer secondNum;

    /** 当运动种类如网球斯诺克有局比分概念时表示主队局比分*/
    private Integer secondT1;

    /** 当运动种类如网球斯诺克有局比分概念时表示客队局比分*/
    private Integer secondT2;

    /** 当运动种类如网球斯诺克有盘比分概念时表示当前盘数*/
    private Integer firstNum;

    /** 当运动种类如网球斯诺克有盘比分概念时表示主队盘比分*/
    private Integer firstT1;

    /** 当运动种类如网球斯诺克有盘比分概念时表示客队盘比分*/
    private Integer firstT2;

    /** 扩展信息*/
    private String extrainfo;

    /** 扩展字段*/
    private String addition1;
    /** 扩展字段*/
    private String addition2;
    /** 扩展字段*/
    private String addition3;
    /** 扩展字段*/
    private String addition4;
    /** 扩展字段*/
    private String addition5;
    /** 扩展字段*/
    private String addition6;
    /** 扩展字段*/
    private String addition7;
    /** 扩展字段*/
    private String addition8;
    /** 扩展字段*/
    private String addition9;
    /** 扩展字段*/
    private String addition10;
    /** 链路ID(冗余字段)*/
    private String copyLinkId;
    /**下发事件操作人*/
    private String remark;
    /**
     * 是否错误完赛事件（普通足球阶段为999才会使用该字段，0:否，1:是 这里表示操盘确认完赛后的回调）
     */
    private Integer isErrorEndEvent = 0;
}
