/**
 *
 */
package com.panda.merge.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Description  :  Rollback Bet Settlement DTO
 * @author       :  Vito
 * @Date: 2019年11月6日 下午2:22:06
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ThirdBetSettlementRollbackDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息所属产品类型
     * 1=LiveOdds, 2=MTS, 3=BetradarCtrl, 4=Betpal, 5=premium cricket
     */
    @Range(min = 1, max = 5, message = "消息所属产品类型为1到5的数字，请确认数据是否正确")
    @NotNull(message = "消息所属产品类型为1到5的数字，不能为空")
    private Integer product;

    /**
     * 数据源事件产生时间
     */
    @Min(value = 1, message = "数据接入模块数据源事件产生时间错误")
    @NotNull(message = "数据接入模块数据源事件产生时间不能为空")
    private Long sourceTimestamp;

    /**
     * 数据接入模块发送消息时间
     */
    @Min(value = 1, message = "数据接入模块发送消息时间错误")
    @NotNull(message = "数据接入模块发送消息时间不能为空")
    private Long sendTimestamp;

    /**
     * 数据源
     */
    @NotNull(message = "数据源不能为空")
    private String dataSourceCode;

    /**
     * 三方赛事ID
     */
    @NotNull(message = "三方赛事ID不能为空")
    private String thirdSourceMatchId;

    /**
     * 盘口列表
     */
    @NotEmpty(message = "推送的三方盘口数据不能为空")
    @Valid
    private List<ThirdBetSettlementRollbackItemDTO> markets;
}
