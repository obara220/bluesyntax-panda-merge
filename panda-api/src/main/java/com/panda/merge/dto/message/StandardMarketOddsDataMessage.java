package com.panda.merge.dto.message;

import com.panda.merge.dto.I18nItemDTO;
import lombok.Data;

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
public class StandardMarketOddsDataMessage implements Serializable{
	private static final long serialVersionUID = 1L;

    private Long id;

    private Long relationMarketOddsId;

    /**
     * 盘口ID  standard_sport_market.id
     */
    private Long marketId;

    private Long relationMarketId;

    /**
     * 标准投注项模板id 对应standard_sport_odds_fields_templet.id
     */
    private Long oddsFieldsTemplateId;

    /**
     * 三方投注项模板源ID
     */
    private String thirdTemplateSourceId;

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
     * 名称编码. 用于多语言. 投注项可能有也可能没有该字段. 需要的时候填入
     */
    private Long nameCode;

    /**
     * 投注项名称.
     */
    private String name;

    /**
     * 投注项名称中包含的表达式的值
     */
    private String nameExpressionValue;

    /**
     * 马来赔
     */
    private Double malayOddsValue;

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
     * margin概率赔率
     */
    private Integer marginProbabilityOdds;

    /**
     * 水差值
     */
    private Double marketDiffValue;

    /**
     * 投注项赔率. 单位: 0.0001
     */
    private Integer oddsValue;

    /**
     * 投注项PA赔率. 单位: 0.0001
     */
    private Integer paOddsValue;

    /**
     * 投注项原始赔率. 单位: 0.0001
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

    private String extraInfo;

    private String remark;

    private Long createTime;

    private Long modifyTime;

    /**
     * 冗余字段：投注项名称，多语言信息
     */
    private List<I18nItemDTO> i18nNames;

    /**
     * 下盘标识，用于特殊抽水需求 true:下  false:上
     */
    private Boolean oddsTypeTag = Boolean.FALSE;

    /**
     * 上一条数据投注项原始赔率. 单位: 0.0001
     */
    private Integer oldOriginalOddsValue;
    /**
     * 投注项操盘状态，0-关闭，1-开启
     */
    private Integer status = 0;

}
