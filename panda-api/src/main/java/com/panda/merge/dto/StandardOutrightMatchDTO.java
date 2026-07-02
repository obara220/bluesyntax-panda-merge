package com.panda.merge.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description  :  数据接入格式：赛事盘口
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardOutrightMatchDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 三方冠军赛事表主键id **/
    @NotNull(message="三方冠军赛事id不能为空")
    private Long thirdOutrightMatchId;

    /** 标准冠军赛事表主键id **/
    @NotNull(message="标准冠军赛事id不能为空")
    private Long standardOutrightMatchId;

    /** 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source **/
    @NotNull(message="数据源不能为空")
    private String dataSourceCode;

    @NotNull(message="是否自动开售新玩法吧标识不能为空")
    private String autoSellStatus;

    /** 操作的类型方式 **/
    private Integer operateType;

    /** 三方盘口源id **/
    private String thirdMarketSourceId;

    /**  统一盘口id，相当于 relationMarketId  **/
    private Long standardMarketId;

    /**  三方盘口表id  **/
    private Long thirdMarketId;

    /** 操作人id **/
    private Long operatorId;

    /** 操作人名称 **/
    private String operatorName;

}
