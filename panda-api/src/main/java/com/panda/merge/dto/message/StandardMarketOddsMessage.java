package com.panda.merge.dto.message;

import com.panda.merge.dto.I18nItemDTO;
import com.panda.merge.dto.odds.StandardMarketOddsModification;
import com.panda.merge.dto.odds.StandardMarketScoreModification;
import com.panda.merge.util.CalculateOdds;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @Description  :  标准盘口投注项消息
 * @author       :  Vito
 * @Date:  2019年10月7日 下午5:41:31
 * @ModificationHistory   Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardMarketOddsMessage implements Serializable, StandardMarketOddsModification {
	private static final long serialVersionUID = 1L;

    /**
     * 标准投注项ID
     */
    private Long id;

    /**
     * 盘口ID  standard_sport_market.id
     */
    private Long marketId;

    /**
     *  三方数据源投注项状态： 0未激活(锁盘)、1激活、2投注项封盘
     */
    private Integer thirdSourceActive;

    /**
     *  投注项状态： 0未激活(锁盘)、1激活、2投注项封盘
     */
    private Integer active;

    private String paReason;

    private String paActiveReason;

    private String paStatusReason;

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
     * 投注项名称编码.
     */
    private Long nameCode;

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
     * 概率赔率
     */
    private Integer probabilityOdds;
    /**
     * 水差值
     */
    private Double marketDiffValue;

    /**
     * 投注项赔率. 单位: 0.00001
     */
    private Integer oddsValue;

    /**
     * 投注项赔率. 单位: 0.00001
     */
    private Integer paOddsValue;

    /**
     * 马来赔
     */
    private Double malayOddsValue;

    /**
     * 标准投注项模板id   standard_sport_odds_fields_templet.id
     */
    private Long oddsFieldsTemplateId;
    /**
     * 三方源投注项模板id
     */
    private String thirdTemplateSourceId;
    /**
     * 投注项原始赔率. 单位: 0.00001
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
     * 取值:  SR BC分别代表: SportRadar、FeedConstruc. 详情见data_source
     */
    private String dataSourceCode;

    /**
     * 该字段用于做风控时, 需要替换成风控服务商提供的投注项id.  如果数据源发生切换, 当前字段需要更新.
     */
    private String thirdOddsFieldSourceId;

    private String remark;

    private Long modifyTime;
    /**
     * 名称编码. 用于多语言。交易项可能有也可能没有该字段。需要的时候填入
     */
    private List<I18nItemDTO> i18nNames;
    /**
     * 扩展参数：用于上游需通过融合向下游透传的参数，融合不做任何处理及存储
     */
    private String extraInfo;

    /**
     * margin概率赔率
     */
    private Integer marginProbabilityOdds;

    /**
     * 清除概率标识 0(否),1(是)
     */
    private Integer clearProbability = 0;

    /**
     * 分组赔率
     */
    private Map<String, CalculateOdds> oddsMap;
    
    /**
     * 投注项操盘状态，0-关闭，1-开启
     */
    private Integer status = 0;

    /**
     * oddsIsMain  0：副投注项   1：主投注项   2：无联动
     */
    private Integer oddsIsMain = 2;

    /**
     * 优惠赔率状态 0或者null-未配置，默认，删除，1-开启，2-关闭
     */
    private Integer discountStatus = 0;
    /**
     * 优惠赔率状态 融合只需透传
     */
    private Integer card;

    /**
     * 优惠赔率创建时间
     */
    private Long discountCreateTime;

    /**
     * 优惠赔率
     */
    private Integer discountOdds;

    @Override
    public Long getRelationMarketId() {
        return marketId;
    }

    @Override
    public void setRelationMarketId(Long relationMarketId) {
        marketId = relationMarketId;
    }

}
