package com.panda.merge.dto;

import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @Description  :  标准盘口投注项消息
 * @author       :  Vito
 * @Date:  2019年10月7日 下午5:41:31
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardMarketOddsDTO implements Serializable{
	private static final long serialVersionUID = 1L;

    private String id;
    
    private Long matchId;

    /**
     *  三方数据源投注项状态： 0未激活(锁盘)、1激活、2投注项封盘
     */
    private Integer thirdSourceActive;

    /**
     * 当前投注项是否被激活.1激活; 0未激活(锁盘)
     */
    private Integer active;

    /**
     * 投注项结算结果文本
     */
    private String settlementResultText;

    /**
     * 投注项结算结果文本
     */
    private String settlementResult;

    /**
     * 赛果已确认: Confirmed, 盘中事件确认: LiveScouted, 未知: Unknown
     */
    private String betSettlementCertainty;

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
     * 附加字段5
     */
    private String addition5;

    /**
     * 投注项名称.
     */
    private String name;

    /**
     * 投注项名称中包含的表达式的值
     */
    private String nameExpressionValue;
    /**
     * margin值
     */
    private Double margin;

    /**
     * 概率差
     */
    private Double probability;

    /**
     * 描点 ：0(否),1(是)
     */
    private Integer anchor;

    /**
     * margin概率赔率
     */
    private Integer marginProbabilityOdds;

    /**
     * 概率赔率
     */
    private Integer probabilityOdds;
    /**
     * 水差值
     */
    private Double marketDiffValue;

    /**
     * 投注项赔率. 单位: 0.00001
     * @since v1.2丢弃, 将在后续版本删除
     */
    private Integer oddsValue;
    /**
     * 马来赔
     */
    private Double malayOddsValue;
    /**
     * 标准投注项模板id   standard_sport_odds_fields_templet.id
     */
    private Long oddsFieldsTemplateId;

    /**
     * 投注项原始赔率. 单位: 0.00001
     * @since v1.2丢弃, 将在后续版本删除
     */
    private Integer originalOddsValue;

    /**
     * 投注给哪一方: T1主队, T2客队,
     */
    private String targetSide;

    /**
     * 用于排序, 大于1, 越小越靠前
     */
    private Integer orderOdds;

    /**
     * 取值:  PA 代表熊猫操盘
     */
    private String dataSourceCode;

    /**
     * 该字段用于做风控时, 需要替换成风控服务商提供的投注项id
     */
    private String thirdOddsFieldSourceId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 修改时间
     */
    private Long modifyTime;

    private Long nameCode;

    private String paStatusReason;

    /**
     * 名称编码. 用于多语言。交易项可能有也可能没有该字段。需要的时候填入
     */
    @Valid
    private List<I18nItemDTO> i18nNames;
}
