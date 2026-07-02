package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Description  :  数据接入格式：盘口投注项
 * @author       :  Vito
 * @Date:  2019年10月7日 下午8:12:44
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class ThirdMarketOddsDTO implements Serializable{
	private static final long serialVersionUID = 1L;

    /**
     * 当前投注项是否被激活.1激活; 0未激活(锁盘)
     */
    @NotNull(message="投注项是否被激活标识不能为空")
    private Integer active;

    /**
     * 投注项类型
     */
    private String oddsType;

    /**
     * 附加字段1
     */
    private String addition1;

    /**
     * 附加字段2
     */
    private String addition2;

    /**
     * 附加字段3
     */
    private String addition3;

    /**
     * 附加字段4
     */
    private String addition4;

    /**
     * 附件字段5
     */
    private String addition5;

    /**
     * 第三方投注项原始ID.
     */
    private String thirdOddsFieldSourceId;

    /**
     * 用于排序, 大于1, 越小越靠前
     */
    private Integer orderOdds;

    /**
     * 投注项名称中包含的表达式的值
     */
    private String nameExpressionValue;

    /**
     * 投注项赔率. 单位: 0.0001
     */
    private Integer oddsValue;
    /**
     * AO百家赔赔率
     */
    private Double aoOddsValue;
    /**
     * 投注项原始赔率. 单位: 0.0001
     */
    private Integer originalOddsValue;

    /**
     * 三方投注项模板原始id
     */
    @NotNull(message="三方投注项模板不能为null")
    private String thirdTempletSourceId;

    /**
     * 投注给哪一方: T1主队, T2客队
     */
    private String targetSide;

    /**
     * 	结算赛果（BC用）
     * 	赛果已确认：Confirmed，盘中事件确认：LiveScouted，未知：Unknown
     */
    private String betSettlementCertainty;

    /**
     * 	投注项结算结果（BC用）
	 *	0 - Not Resulted
	 *	1 - Place*
	 *	2 - Return
	 *	3 - Lost
	 *	4 - Won
	 *	5 - Win Return
	 *	6 - Loose Return
     */
    private String settlementResult;

    /**
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String dataSourceCode;

    private String remark;

    private Long modifyTime;

    /**
     * 名称编码. 用于多语言。交易项可能有也可能没有该字段。需要的时候填入
     */
    @Valid
    private List<I18nItemDTO> i18nNames;
    /**
     * 扩展参数：用于上游需通过融合向下游透传的参数，融合不做任何处理及存储
     */
    private String extraInfo;
}
