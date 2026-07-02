/**
 *
 */
package com.panda.merge.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @Description  :  bet cancel DTO
 * @author       :  Vito
 * @Date: 2019年11月6日 下午2:22:06
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ThirdBetCancelDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息所属产品类型
     * 1=LiveOdds, 2=MTS, 3=BetradarCtrl, 4=Betpal, 5=premium cricket
     */

    @Range(min = 1, max = 5, message = "消息所属产品类型为1到5的数字，请确认数据是否正确")
    @NotNull(message = "消息所属产品类型为1到5的数字，不能为空")
    private Integer product;

    /**
     * 开始时间：若为0表示无开始时间
     */
    private Long startTime;

    /**
     * 结束时间：若为0表示无结束时间
     */
    private Long endTime;

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
    @Size(min = 1, message = "推送的三方盘口数据不能为空")
    @Valid
    private List<ThirdBetCancelItemDTO> markets;
}
