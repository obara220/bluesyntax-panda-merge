package com.panda.merge.dto.settle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UpdateBasketBallSettleScoreDto extends AbstructMatchSettleDto{
    /**
     *  赛事比分ID
     * */
    private Long matchScoreId;
    /**
     * 赛事ID
     * */
    private Long standardMatchId;
    /**
     * 主队比分
     * */
    private Integer t1;
    /**
     * 客队比分
     * */
    private Integer t2;

    private String eventCode;
    /**
     * 结算比分序号
     * */
    private String settleNum;
    /**
     * 获胜方式
     * 1.Please select	请选择
     * 2.Home regular time	主队常规时间
     * 3.Away regular time	客队常规时间
     * 4.Home overtime	主队加时赛
     * 5.Away overtime	客队加时赛
     * 6.Home penalties	主队点球大战
     * 7.Away penalties 客队点球大战
     * */
    private String extryInfo;

    @ApiModelProperty(value = "总结算次数(不能回滚)")
    private Integer settleCount;

    private Integer goWaterStatus;

    private Integer matchLength;
    /**
     * 权限编码 1为管理员[二次编辑权限] 2次序审核员
     */
    private Integer roleCode;
}
