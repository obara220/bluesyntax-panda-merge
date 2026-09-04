package com.panda.merge.dto.settle;


import com.panda.merge.model.MatchSettleCheckInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MatchSettleScoreDto  extends AbstructMatchSettleDto {
    private String id;

    @ApiModelProperty(value = "比分事件编码")
    private String eventCode;

    @ApiModelProperty(value = "主队比分")
    private Integer t1;

    @ApiModelProperty(value = "客队比分")
    private Integer t2;

    @ApiModelProperty(value = "结算比分编号")
    private String settleNum;

    @ApiModelProperty(value = "比分状态:0未确认1已确认2已结算")
    private Integer status;

    @ApiModelProperty(value = "赛种")
    private Long sportId;

    @ApiModelProperty(value = "数据源编码")
    private String dataSourceCode;

    @ApiModelProperty(value = "标准赛事ID")
    private Long standardMatchId;

    @ApiModelProperty(value = "比分阶段")
    private Long periodId;

    private String extryInfo;

    @ApiModelProperty(value = "结算比分冻结0未冻结1冻结")
    private Integer scoresPeriodFreeze;

    @ApiModelProperty(value = "结算次数")
    private Integer settleTimes;

    @ApiModelProperty(value = "总结算次数(不能回滚)")
    private Integer settleCount;
    /**
     * 根据key去匹配三方比分
     * */
    private String key;
    /**
     * 是否走水: 0 不走水 1走水
     * */
    private Integer goWaterStatus;
    /**
     * 玩法级冻结状态
     */
    private CategoryDto categoryDto;
    /**
     * 是否需要审核  1 需要 0 不需要
     * */
    private Integer needCheck;

    /**
     * 回滚状态0未回滚，1回滚中
     * */
    private Integer rollBackStatus;
    /**
     * 回滚订单数
     * */
    private Long rollBackOrderCount;

    /**
     * 五分钟阶段比分
     * */
    private List<MatchSettleScoreDto> fiveMinList;

//    @ApiModelProperty(value = "有删除事件:1是0否")
    private Integer hasDeleteEvent;

    @ApiModelProperty(value = "有赛果不匹配事件:1是0否")
    private Integer hasDataMismatchEvent;


//  有数据源与结算比分不一致提示:1是0否
    private Integer currentEventTag;

    //同步数据按钮用户信息"
    private String popupUsers;

    //已结算比分校验标记:1是0否"
    private Integer scoreCheckTag;

//    @ApiModelProperty(value = "当前事件状态：0无1灰色区间2删除事件")
    private Integer currentEventStatus;

//    @ApiModelProperty(value = "是否灰色区间：1是0不是")
    private Integer isGrey;

    /**
     * 延时结算秒数
     */
    private Long delayTimeSecond;

    /**
     * 即时开关是否开启
     */
    private Boolean realTimeOnOff;
}
